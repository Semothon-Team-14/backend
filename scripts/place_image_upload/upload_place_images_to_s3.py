from __future__ import annotations

import argparse
import mimetypes
import os
from pathlib import Path

import boto3


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Upload place images to S3 with stable public object keys.",
    )
    parser.add_argument(
        "--source-dir",
        type=Path,
        required=True,
        help="Local directory whose relative paths will become S3 object keys.",
    )
    parser.add_argument(
        "--bucket",
        default=os.getenv("AWS_S3_BUCKET", ""),
        help="S3 bucket name. Defaults to AWS_S3_BUCKET.",
    )
    parser.add_argument(
        "--region",
        default=os.getenv("AWS_REGION", ""),
        help="AWS region. Defaults to AWS_REGION.",
    )
    return parser.parse_args()


def iter_files(root: Path) -> list[Path]:
    return sorted(path for path in root.rglob("*") if path.is_file())


def main() -> int:
    args = parse_args()
    if not args.bucket:
        raise SystemExit("Missing bucket. Pass --bucket or set AWS_S3_BUCKET.")
    if not args.region:
        raise SystemExit("Missing region. Pass --region or set AWS_REGION.")

    source_dir = args.source_dir.resolve()
    if not source_dir.is_dir():
        raise SystemExit(f"Source directory does not exist: {source_dir}")

    files = iter_files(source_dir)
    if not files:
        raise SystemExit(f"No files found under {source_dir}")

    s3 = boto3.client("s3", region_name=args.region)
    for file_path in files:
        key = file_path.relative_to(source_dir).as_posix()
        content_type, _ = mimetypes.guess_type(file_path.name)
        extra_args = {"ContentType": content_type or "application/octet-stream"}
        s3.upload_file(str(file_path), args.bucket, key, ExtraArgs=extra_args)
        print(f"{key} -> https://{args.bucket}.s3.{args.region}.amazonaws.com/{key}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
