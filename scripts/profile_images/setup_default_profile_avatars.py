from __future__ import annotations

import argparse
import io
import os
import random
import sys
from dataclasses import dataclass
from pathlib import Path

try:
    import boto3
except ModuleNotFoundError:  # pragma: no cover
    boto3 = None

try:
    import psycopg
except ModuleNotFoundError:  # pragma: no cover
    psycopg = None

try:
    from PIL import Image
except ModuleNotFoundError:  # pragma: no cover
    Image = None


DEFAULT_DB_HOST = "localhost"
DEFAULT_DB_PORT = 5432
DEFAULT_DB_NAME = "semobackend"
DEFAULT_DB_USERNAME = "semo"
DEFAULT_DB_PASSWORD = "secret"
DEFAULT_S3_REGION = "ap-northeast-2"
DEFAULT_PROFILE_BUCKET = "semothon-14-profile-pictures"
DEFAULT_S3_KEY_PREFIX = "defaults/profile-avatars"
DEFAULT_SOURCE_IMAGE = Path(__file__).resolve().parents[3] / "frontend" / "img_1.png"


@dataclass(frozen=True)
class AxisSegment:
    start: int
    end: int

    @property
    def length(self) -> int:
        return self.end - self.start


def load_local_env_defaults() -> dict[str, str]:
    env_path = Path(__file__).resolve().parents[2] / ".env.properties"
    if not env_path.exists():
        return {}

    values: dict[str, str] = {}
    for line in env_path.read_text().splitlines():
        line = line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        values[key.strip()] = value.strip()
    return values


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Slice a 3x3 avatar sheet, upload defaults to S3, and backfill users.profile_image_url when null.",
    )
    parser.add_argument("--source-image", default=str(DEFAULT_SOURCE_IMAGE))
    parser.add_argument("--db-host", default=os.getenv("PLACES_DB_HOST", DEFAULT_DB_HOST))
    parser.add_argument("--db-port", type=int, default=int(os.getenv("PLACES_DB_PORT", str(DEFAULT_DB_PORT))))
    parser.add_argument("--db-name", default=os.getenv("PLACES_DB_NAME", DEFAULT_DB_NAME))
    parser.add_argument("--db-user", default=os.getenv("PLACES_DB_USER", DEFAULT_DB_USERNAME))
    parser.add_argument("--db-password", default=os.getenv("PLACES_DB_PASSWORD", DEFAULT_DB_PASSWORD))
    parser.add_argument("--s3-bucket", default="")
    parser.add_argument("--s3-region", default="")
    parser.add_argument("--s3-key-prefix", default=DEFAULT_S3_KEY_PREFIX)
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--skip-db-backfill", action="store_true")
    parser.add_argument("--seed", type=int, default=42)
    return parser.parse_args()


def is_separator_column(image: Image.Image, x: int) -> bool:
    width, height = image.size
    dark_count = 0
    for y in range(height):
        r, g, b, _ = image.getpixel((x, y))
        if r < 24 and g < 24 and b < 24:
            dark_count += 1
    return (dark_count / height) >= 0.95


def is_separator_row(image: Image.Image, y: int) -> bool:
    width, height = image.size
    dark_count = 0
    for x in range(width):
        r, g, b, _ = image.getpixel((x, y))
        if r < 24 and g < 24 and b < 24:
            dark_count += 1
    return (dark_count / width) >= 0.95


def extract_segments(length: int, is_separator) -> list[AxisSegment]:
    segments: list[AxisSegment] = []
    start: int | None = None
    for i in range(length):
        if is_separator(i):
            if start is not None:
                segments.append(AxisSegment(start=start, end=i))
                start = None
            continue
        if start is None:
            start = i
    if start is not None:
        segments.append(AxisSegment(start=start, end=length))
    return [segment for segment in segments if segment.length > max(20, int(length * 0.1))]


def fallback_equal_segments(length: int) -> list[AxisSegment]:
    step = length // 3
    return [
        AxisSegment(0, step),
        AxisSegment(step, step * 2),
        AxisSegment(step * 2, length),
    ]


def detect_grid_segments(image: Image.Image) -> tuple[list[AxisSegment], list[AxisSegment]]:
    width, height = image.size
    col_segments = extract_segments(width, lambda idx: is_separator_column(image, idx))
    row_segments = extract_segments(height, lambda idx: is_separator_row(image, idx))

    if len(col_segments) != 3:
        col_segments = fallback_equal_segments(width)
    if len(row_segments) != 3:
        row_segments = fallback_equal_segments(height)

    col_segments = sorted(col_segments, key=lambda segment: segment.start)[:3]
    row_segments = sorted(row_segments, key=lambda segment: segment.start)[:3]
    return col_segments, row_segments


def slice_avatar_images(source_path: Path) -> list[Image.Image]:
    if Image is None:
        raise RuntimeError("Pillow is required. Install with scripts/profile_images/requirements.txt")

    if not source_path.exists():
        raise FileNotFoundError(f"Source image not found: {source_path}")

    with Image.open(source_path) as source:
        rgba = source.convert("RGBA")
        col_segments, row_segments = detect_grid_segments(rgba)

        avatars: list[Image.Image] = []
        for row in row_segments:
            for col in col_segments:
                cropped = rgba.crop((col.start, row.start, col.end, row.end))
                avatars.append(cropped)
        return avatars


def image_to_png_bytes(image: Image.Image) -> bytes:
    output = io.BytesIO()
    image.save(output, format="PNG")
    return output.getvalue()


def center_crop_square(image: Image.Image) -> Image.Image:
    width, height = image.size
    side = min(width, height)
    left = (width - side) // 2
    top = (height - side) // 2
    right = left + side
    bottom = top + side
    return image.crop((left, top, right, bottom))


def main() -> int:
    args = parse_args()
    env_defaults = load_local_env_defaults()

    needs_db = not args.skip_db_backfill
    if needs_db and psycopg is None:
        print("psycopg is required. Install with scripts/profile_images/requirements.txt", file=sys.stderr)
        return 1

    s3_bucket = (
        args.s3_bucket
        or env_defaults.get("AWS_PROFILE_PICTURES_BUCKET")
        or os.getenv("AWS_PROFILE_PICTURES_BUCKET")
        or DEFAULT_PROFILE_BUCKET
    )
    s3_region = (
        args.s3_region
        or env_defaults.get("AWS_REGION")
        or os.getenv("AWS_REGION")
        or DEFAULT_S3_REGION
    )
    aws_access_key_id = env_defaults.get("AWS_ACCESS_KEY_ID") or os.getenv("AWS_ACCESS_KEY_ID", "")
    aws_secret_access_key = env_defaults.get("AWS_SECRET_ACCESS_KEY") or os.getenv("AWS_SECRET_ACCESS_KEY", "")

    if not args.dry_run and boto3 is None:
        print("boto3 is required. Install with scripts/profile_images/requirements.txt", file=sys.stderr)
        return 1
    if not args.dry_run and (not aws_access_key_id or not aws_secret_access_key):
        print("Missing AWS credentials", file=sys.stderr)
        return 1

    source_path = Path(args.source_image).expanduser().resolve()
    avatars = slice_avatar_images(source_path)
    if len(avatars) != 9:
        print(f"Expected 9 avatars from source, got {len(avatars)}", file=sys.stderr)
        return 1

    s3_client = None
    if not args.dry_run:
        s3_client = boto3.client(
            "s3",
            region_name=s3_region,
            aws_access_key_id=aws_access_key_id,
            aws_secret_access_key=aws_secret_access_key,
        )

    uploaded_urls: list[str] = []
    for index, avatar in enumerate(avatars, start=1):
        square_avatar = center_crop_square(avatar)
        key = f"{args.s3_key_prefix.strip('/')}/avatar_{index}.png"
        public_url = f"https://{s3_bucket}.s3.{s3_region}.amazonaws.com/{key}"
        uploaded_urls.append(public_url)
        if not args.dry_run:
            s3_client.put_object(
                Bucket=s3_bucket,
                Key=key,
                Body=image_to_png_bytes(square_avatar),
                ContentType="image/png",
            )
        print(f"[AVATAR] {index} -> {public_url}")

    if args.skip_db_backfill:
        print("Skip DB backfill (--skip-db-backfill).")
        return 0

    random.seed(args.seed)
    shuffled_urls = uploaded_urls[:]
    random.shuffle(shuffled_urls)

    with psycopg.connect(
        host=args.db_host,
        port=args.db_port,
        dbname=args.db_name,
        user=args.db_user,
        password=args.db_password,
    ) as connection:
        with connection.cursor() as cursor:
            cursor.execute(
                """
                UPDATE users
                SET profile_image_url = (%s::text[])[1 + floor(random() * %s)::int]
                WHERE profile_image_url IS NULL
                   OR btrim(profile_image_url) = ''
                """,
                (shuffled_urls, len(shuffled_urls)),
            )
            updated_count = cursor.rowcount
        connection.commit()
    print(f"[DB] updated users with null profile_image_url: {updated_count}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
