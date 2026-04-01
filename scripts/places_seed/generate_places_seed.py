from __future__ import annotations

import argparse
import json
import mimetypes
import os
import sys
import urllib.request
from dataclasses import asdict, dataclass
from pathlib import Path

from database_writer import DatabaseConfig, sync_places_to_database
from google_places_client import GooglePlacesClient, GooglePlacesError
from seed_writer import SeedPlace, build_seed_places, read_cities_by_ids

try:
    import boto3
except ModuleNotFoundError:  # pragma: no cover - handled at runtime
    boto3 = None


DEFAULT_CITIES_CSV = (
    Path(__file__).resolve().parents[2]
    / "src/main/resources/db/changelog/data/cities.csv"
)
DEFAULT_OUTPUT_DIR = Path(__file__).resolve().parent / "output"
DEFAULT_PROGRESS_PATH = DEFAULT_OUTPUT_DIR / "google_places_progress.json"
DEFAULT_DB_HOST = "localhost"
DEFAULT_DB_PORT = 5432
DEFAULT_DB_NAME = "semobackend"
DEFAULT_DB_USERNAME = "semo"
DEFAULT_DB_PASSWORD = "secret"
DEFAULT_MAJOR_CITY_IDS = [1, 4, 13, 16, 19, 43, 46, 58, 61, 82]
DEFAULT_S3_KEY_PREFIX = "places"


@dataclass(frozen=True)
class PlaceCategoryConfig:
    label: str
    api_type: str
    query_labels: tuple[str, ...]


PLACE_CONFIGS = (
    PlaceCategoryConfig(
        label="cafes",
        api_type="cafe",
        query_labels=("best cafes", "popular cafes", "cafes"),
    ),
    PlaceCategoryConfig(
        label="restaurants",
        api_type="restaurant",
        query_labels=("best restaurants", "top rated restaurants", "restaurants"),
    ),
)


@dataclass(frozen=True)
class S3Config:
    bucket: str
    region: str
    key_prefix: str
    access_key_id: str
    secret_access_key: str

    def public_url(self, key: str) -> str:
        return f"https://{self.bucket}.s3.{self.region}.amazonaws.com/{key}"


def main() -> int:
    args = parse_args()
    env_defaults = load_local_env_defaults()
    api_key = args.api_key or env_defaults.get("GOOGLE_PLACES_API_KEY") or os.getenv("GOOGLE_PLACES_API_KEY")
    if not api_key:
        print(
            "Missing Google Places API key. Pass --api-key or set GOOGLE_PLACES_API_KEY.",
            file=sys.stderr,
        )
        return 1

    client = GooglePlacesClient(
        api_key=api_key,
        language_code=args.language_code,
        max_retries=args.max_retries,
        timeout_seconds=args.timeout_seconds,
    )

    s3_config = build_s3_config(args, env_defaults)
    progress = load_progress(args.progress_path, reset=args.reset_progress)

    cities = read_cities_by_ids(args.cities_csv, args.city_ids)
    generated = fetch_seed_data(
        client=client,
        cities=cities,
        args=args,
        s3_config=s3_config,
        progress=progress,
    )

    if args.dry_run:
        for label, places in generated.items():
            print(f"{label}: {len(places)} rows ready")
        return 0

    if not args.skip_database:
        sync_places_to_database(
            database_config=DatabaseConfig(
                host=args.db_host,
                port=args.db_port,
                database=args.db_name,
                username=args.db_user,
                password=args.db_password,
            ),
            cafes=generated["cafes"],
            restaurants=generated["restaurants"],
        )
        print("Inserted cafes, cafe_images, restaurants, and restaurant_images into the database.")

    return 0


def fetch_seed_data(
    *,
    client: GooglePlacesClient,
    cities,
    args: argparse.Namespace,
    s3_config: S3Config,
    progress: dict,
) -> dict[str, list[SeedPlace]]:
    generated_outputs: dict[str, list[SeedPlace]] = {
        "cafes": [],
        "restaurants": [],
    }
    next_starting_ids = {
        "cafes": 1,
        "restaurants": 1,
    }

    s3_client = None
    if not args.skip_s3_upload:
        if boto3 is None:
            raise RuntimeError(
                "boto3 is required for S3 uploads. Install it with "
                "`pip install -r scripts/places_seed/requirements.txt`.",
            )
        s3_client = boto3.client(
            "s3",
            region_name=s3_config.region,
            aws_access_key_id=s3_config.access_key_id,
            aws_secret_access_key=s3_config.secret_access_key,
        )

    for config in PLACE_CONFIGS:
        for city in cities:
            stored_places = get_progress_places(progress, config.label, city.id)
            if stored_places:
                generated_outputs[config.label].extend(stored_places)
                next_starting_ids[config.label] = max(
                    next_starting_ids[config.label],
                    max(place.id for place in stored_places) + 1,
                )
                continue

            print(f"Fetching {config.label} for {city.name_english}...")
            search_results = []
            for query_label in config.query_labels:
                try:
                    fetched_results = client.search_places(
                        city_name=city.name_english,
                        place_type=config.api_type,
                        query_label=query_label,
                        page_size=args.max_search_results,
                        photo_limit=args.photos_per_place,
                    )
                except GooglePlacesError as error:
                    print(
                        f"Failed to fetch {config.label} for {city.name_english} using '{query_label}': {error}",
                        file=sys.stderr,
                    )
                    raise SystemExit(1) from error
                search_results.extend(fetched_results)

                city_places = build_seed_places(
                    city_id=city.id,
                    places=search_results,
                    target_count=args.places_per_city,
                    photo_count=args.photos_per_place,
                    starting_id=next_starting_ids[config.label],
                )
                if len(city_places) >= args.places_per_city:
                    break

            city_places = build_seed_places(
                city_id=city.id,
                places=search_results,
                target_count=args.places_per_city,
                photo_count=args.photos_per_place,
                starting_id=next_starting_ids[config.label],
            )

            if len(city_places) < args.places_per_city:
                print(
                    (
                        f"{city.name_english}: only found {len(city_places)} usable {config.label} "
                        f"with at least {args.photos_per_place} photos"
                    ),
                    file=sys.stderr,
                )
                raise SystemExit(1)

            uploaded_places: list[SeedPlace] = []
            for place in city_places:
                uploaded_places.append(
                    upload_place_photos(
                        place=place,
                        category_label=config.label,
                        s3_config=s3_config,
                        s3_client=s3_client,
                        dry_run=args.skip_s3_upload,
                    ),
                )

            generated_outputs[config.label].extend(uploaded_places)
            next_starting_ids[config.label] += len(uploaded_places)

            save_progress_places(
                progress=progress,
                label=config.label,
                city_id=city.id,
                places=uploaded_places,
                progress_path=args.progress_path,
            )

    return generated_outputs


def upload_place_photos(
    *,
    place: SeedPlace,
    category_label: str,
    s3_config: S3Config,
    s3_client,
    dry_run: bool,
) -> SeedPlace:
    if dry_run:
        photo_urls = tuple(
            s3_config.public_url(f"{s3_config.key_prefix}/{category_label}/{place.id}/{index + 1}.jpg")
            for index, _ in enumerate(place.photos)
        )
        return SeedPlace(
            id=place.id,
            city_id=place.city_id,
            name=place.name,
            phone_number=place.phone_number,
            address=place.address,
            food_category=place.food_category,
            latitude=place.latitude,
            longitude=place.longitude,
            photos=photo_urls,
        )

    uploaded_urls = []
    for index, photo_url in enumerate(place.photos, start=1):
        key = f"{s3_config.key_prefix}/{category_label}/{place.id}/{index}.jpg"
        with urllib.request.urlopen(photo_url, timeout=60) as response:
            content = response.read()
            content_type = response.headers.get_content_type()
        s3_client.put_object(
            Bucket=s3_config.bucket,
            Key=key,
            Body=content,
            ContentType=content_type or (mimetypes.guess_type(key)[0] or "image/jpeg"),
        )
        uploaded_urls.append(s3_config.public_url(key))

    return SeedPlace(
        id=place.id,
        city_id=place.city_id,
        name=place.name,
        phone_number=place.phone_number,
        address=place.address,
        food_category=place.food_category,
        latitude=place.latitude,
        longitude=place.longitude,
        photos=tuple(uploaded_urls),
    )


def build_s3_config(args: argparse.Namespace, env_defaults: dict[str, str]) -> S3Config:
    bucket = args.s3_bucket or env_defaults.get("AWS_S3_BUCKET") or os.getenv("AWS_S3_BUCKET")
    region = args.s3_region or env_defaults.get("AWS_REGION") or os.getenv("AWS_REGION")
    access_key_id = env_defaults.get("AWS_ACCESS_KEY_ID") or os.getenv("AWS_ACCESS_KEY_ID", "")
    secret_access_key = env_defaults.get("AWS_SECRET_ACCESS_KEY") or os.getenv("AWS_SECRET_ACCESS_KEY", "")
    if not bucket or not region:
        raise SystemExit("Missing AWS S3 bucket or region. Set env values or pass --s3-bucket and --s3-region.")
    if not access_key_id or not secret_access_key:
        raise SystemExit("Missing AWS access key or secret key. Set them in env or .env.properties.")
    return S3Config(
        bucket=bucket,
        region=region,
        key_prefix=args.s3_key_prefix.strip("/"),
        access_key_id=access_key_id,
        secret_access_key=secret_access_key,
    )


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


def load_progress(progress_path: Path, *, reset: bool) -> dict:
    if reset or not progress_path.exists():
        return {"cafes": {}, "restaurants": {}}
    return json.loads(progress_path.read_text())


def save_progress_places(
    *,
    progress: dict,
    label: str,
    city_id: int,
    places: list[SeedPlace],
    progress_path: Path,
) -> None:
    progress.setdefault(label, {})[str(city_id)] = [asdict(place) for place in places]
    progress_path.parent.mkdir(parents=True, exist_ok=True)
    progress_path.write_text(json.dumps(progress, ensure_ascii=False, indent=2))


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Generate Google Places seed data, upload photos to S3, and fill the local database.",
    )
    parser.add_argument(
        "--api-key",
        help="Google Places API key. Defaults to GOOGLE_PLACES_API_KEY or .env.properties.",
    )
    parser.add_argument(
        "--cities-csv",
        type=Path,
        default=DEFAULT_CITIES_CSV,
        help=f"Path to the input city CSV. Defaults to {DEFAULT_CITIES_CSV}.",
    )
    parser.add_argument(
        "--city-ids",
        type=int,
        nargs="+",
        default=DEFAULT_MAJOR_CITY_IDS,
        help="City IDs to seed. Defaults to 10 major world cities already present in cities.csv.",
    )
    parser.add_argument(
        "--places-per-city",
        type=int,
        default=5,
        help="Number of cafes and restaurants to keep for each city.",
    )
    parser.add_argument(
        "--photos-per-place",
        type=int,
        default=3,
        help="Number of photos to keep for each cafe or restaurant.",
    )
    parser.add_argument(
        "--max-search-results",
        type=int,
        default=20,
        help="Number of Google Places search results to inspect per city and type.",
    )
    parser.add_argument(
        "--language-code",
        default="en",
        help="Language code passed to Google Places.",
    )
    parser.add_argument(
        "--timeout-seconds",
        type=int,
        default=30,
        help="HTTP timeout for each Google Places request.",
    )
    parser.add_argument(
        "--max-retries",
        type=int,
        default=3,
        help="Retry count for transient Google Places request failures.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Fetch and upload data without writing to the database.",
    )
    parser.add_argument(
        "--skip-database",
        action="store_true",
        help="Skip database writes.",
    )
    parser.add_argument(
        "--skip-s3-upload",
        action="store_true",
        help="Skip S3 uploads and only calculate target public URLs.",
    )
    parser.add_argument(
        "--reset-progress",
        action="store_true",
        help="Reset any saved progress and start over.",
    )
    parser.add_argument(
        "--progress-path",
        type=Path,
        default=DEFAULT_PROGRESS_PATH,
        help=f"Progress file path. Defaults to {DEFAULT_PROGRESS_PATH}.",
    )
    parser.add_argument(
        "--s3-bucket",
        default="",
        help="S3 bucket name. Defaults to AWS_S3_BUCKET or .env.properties.",
    )
    parser.add_argument(
        "--s3-region",
        default="",
        help="S3 region. Defaults to AWS_REGION or .env.properties.",
    )
    parser.add_argument(
        "--s3-key-prefix",
        default=DEFAULT_S3_KEY_PREFIX,
        help=f"S3 key prefix for uploaded place images. Defaults to {DEFAULT_S3_KEY_PREFIX}.",
    )
    parser.add_argument(
        "--db-host",
        default=os.getenv("PLACES_DB_HOST", DEFAULT_DB_HOST),
        help=f"PostgreSQL host. Defaults to {DEFAULT_DB_HOST}.",
    )
    parser.add_argument(
        "--db-port",
        type=int,
        default=int(os.getenv("PLACES_DB_PORT", str(DEFAULT_DB_PORT))),
        help=f"PostgreSQL port. Defaults to {DEFAULT_DB_PORT}.",
    )
    parser.add_argument(
        "--db-name",
        default=os.getenv("PLACES_DB_NAME", DEFAULT_DB_NAME),
        help=f"PostgreSQL database. Defaults to {DEFAULT_DB_NAME}.",
    )
    parser.add_argument(
        "--db-user",
        default=os.getenv("PLACES_DB_USER", DEFAULT_DB_USERNAME),
        help=f"PostgreSQL username. Defaults to {DEFAULT_DB_USERNAME}.",
    )
    parser.add_argument(
        "--db-password",
        default=os.getenv("PLACES_DB_PASSWORD", DEFAULT_DB_PASSWORD),
        help=f"PostgreSQL password. Defaults to {DEFAULT_DB_PASSWORD}.",
    )
    args = parser.parse_args()
    if args.dry_run:
        args.skip_database = True
    return args


def progress_place_to_seed_places(stored_places: list[dict]) -> list[SeedPlace]:
    return [
        SeedPlace(
            id=place["id"],
            city_id=place["city_id"],
            name=place["name"],
            phone_number=place["phone_number"],
            address=place["address"],
            food_category=place["food_category"],
            latitude=place["latitude"],
            longitude=place["longitude"],
            photos=tuple(place["photos"]),
        )
        for place in stored_places
    ]


def get_progress_places(progress: dict, label: str, city_id: int) -> list[SeedPlace] | None:
    stored = progress.get(label, {}).get(str(city_id))
    if not stored:
        return None
    return progress_place_to_seed_places(stored)


if __name__ == "__main__":
    raise SystemExit(main())
