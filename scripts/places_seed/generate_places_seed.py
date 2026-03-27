from __future__ import annotations

import argparse
import os
import sys
from dataclasses import dataclass
from pathlib import Path

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
    cities = read_cities(args.cities_csv)

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
                return 1

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
                return 1

            all_places.extend(city_places)
            next_starting_ids[config.label] += len(city_places)

        generated_outputs.append((config, all_places))

    if args.dry_run:
        for config, places in generated_outputs:
            print(f"{config.label}: {len(places)} rows ready")
        return 0

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


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description=(
            "Generate cafe and restaurant seed CSVs for every city using the Google Places API."
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
        help=f"Directory for generated CSV files. Defaults to {DEFAULT_OUTPUT_DIR}.",
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
        help="Fetch and validate data without writing CSV files.",
    )
    return parser.parse_args()


if __name__ == "__main__":
    raise SystemExit(main())
