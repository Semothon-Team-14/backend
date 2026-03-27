from __future__ import annotations

import argparse
import os
import sys
from dataclasses import dataclass
from pathlib import Path

from database_writer import DatabaseConfig, sync_places_to_database
from google_places_client import GooglePlacesClient, GooglePlacesError
from seed_writer import (
    SeedPlace,
    build_seed_places,
    read_cities,
    write_image_csv,
    write_place_csv,
)


DEFAULT_CITIES_CSV = (
    Path(__file__).resolve().parents[2]
    / "src/main/resources/db/changelog/data/cities.csv"
)
DEFAULT_OUTPUT_DIR = Path(__file__).resolve().parent / "output"
DEFAULT_DB_HOST = "localhost"
DEFAULT_DB_PORT = 5432
DEFAULT_DB_NAME = "semobackend"
DEFAULT_DB_USERNAME = "semo"
DEFAULT_DB_PASSWORD = "secret"


@dataclass(frozen=True)
class PlaceCategoryConfig:
    label: str
    api_type: str
    query_label: str
    output_csv_name: str
    image_csv_name: str
    image_parent_column_name: str


PLACE_CONFIGS = (
    PlaceCategoryConfig(
        label="cafes",
        api_type="cafe",
        query_label="best cafes",
        output_csv_name="cafes.csv",
        image_csv_name="cafe_images.csv",
        image_parent_column_name="cafe_id",
    ),
    PlaceCategoryConfig(
        label="restaurants",
        api_type="restaurant",
        query_label="best restaurants",
        output_csv_name="restaurants.csv",
        image_csv_name="restaurant_images.csv",
        image_parent_column_name="restaurant_id",
    ),
)


def main() -> int:
    args = parse_args()
    api_key = args.api_key or os.getenv("GOOGLE_PLACES_API_KEY")
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
    generated_outputs = fetch_seed_data(client=client, cities_csv=args.cities_csv, args=args)

    if args.dry_run:
        for config, places in generated_outputs:
            print(f"{config.label}: {len(places)} rows ready")
        return 0

    places_by_label = {config.label: places for config, places in generated_outputs}
    if not args.skip_database:
        sync_places_to_database(
            database_config=DatabaseConfig(
                host=args.db_host,
                port=args.db_port,
                database=args.db_name,
                username=args.db_user,
                password=args.db_password,
            ),
            cafes=places_by_label["cafes"],
            restaurants=places_by_label["restaurants"],
        )
        print("Inserted cafes, cafe_images, restaurants, and restaurant_images into the database.")

    if args.write_csv:
        for config, places in generated_outputs:
            place_output_path = args.output_dir / config.output_csv_name
            image_output_path = args.output_dir / config.image_csv_name
            write_place_csv(place_output_path, places)
            write_image_csv(
                image_output_path,
                places=places,
                parent_column_name=config.image_parent_column_name,
            )
            print(f"Wrote {place_output_path}")
            print(f"Wrote {image_output_path}")

    return 0


def fetch_seed_data(
    *,
    client: GooglePlacesClient,
    cities_csv: Path,
    args: argparse.Namespace,
) -> list[tuple[PlaceCategoryConfig, list[SeedPlace]]]:
    cities = read_cities(cities_csv)
    generated_outputs: list[tuple[PlaceCategoryConfig, list[SeedPlace]]] = []
    next_starting_ids = {
        "cafes": 1,
        "restaurants": 1,
    }

    for config in PLACE_CONFIGS:
        all_places: list[SeedPlace] = []
        print(f"Fetching {config.label} for {len(cities)} cities...")

        for city in cities:
            print(f"  - {city.name_english}")
            try:
                search_results = client.search_places(
                    city_name=city.name_english,
                    place_type=config.api_type,
                    query_label=config.query_label,
                    page_size=args.max_search_results,
                    photo_limit=args.photos_per_place,
                )
            except GooglePlacesError as error:
                print(
                    f"Failed to fetch {config.label} for {city.name_english}: {error}",
                    file=sys.stderr,
                )
                raise SystemExit(1) from error

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

            all_places.extend(city_places)
            next_starting_ids[config.label] += len(city_places)

        generated_outputs.append((config, all_places))

    return generated_outputs


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Generate cafe and restaurant seed data for every city using the Google Places API."
        )
    )
    parser.add_argument(
        "--api-key",
        help="Google Places API key. Defaults to GOOGLE_PLACES_API_KEY.",
    )
    parser.add_argument(
        "--cities-csv",
        type=Path,
        default=DEFAULT_CITIES_CSV,
        help=f"Path to the input city CSV. Defaults to {DEFAULT_CITIES_CSV}.",
    )
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=DEFAULT_OUTPUT_DIR,
        help=f"Directory for generated CSV files when --write-csv is enabled. Defaults to {DEFAULT_OUTPUT_DIR}.",
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
        help="Fetch and validate data without writing to the database or CSV files.",
    )
    parser.add_argument(
        "--write-csv",
        action="store_true",
        help="Also write CSV snapshots alongside inserting into the database.",
    )
    parser.add_argument(
        "--skip-database",
        action="store_true",
        help="Skip database writes and only fetch data or optionally write CSV files.",
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
        help=f"PostgreSQL database name. Defaults to {DEFAULT_DB_NAME}.",
    )
    parser.add_argument(
        "--db-user",
        default=os.getenv("PLACES_DB_USER", DEFAULT_DB_USERNAME),
        help=f"PostgreSQL username. Defaults to {DEFAULT_DB_USERNAME}.",
    )
    parser.add_argument(
        "--db-password",
        default=os.getenv("PLACES_DB_PASSWORD", DEFAULT_DB_PASSWORD),
        help="PostgreSQL password. Defaults to the local profile password.",
    )
    return parser.parse_args()


if __name__ == "__main__":
    raise SystemExit(main())
