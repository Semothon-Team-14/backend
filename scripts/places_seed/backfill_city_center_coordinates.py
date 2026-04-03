from __future__ import annotations

import argparse
import os
import sys
from pathlib import Path

from google_places_client import GooglePlacesClient, GooglePlacesError

try:
    import psycopg
except ModuleNotFoundError:  # pragma: no cover
    psycopg = None


DEFAULT_DB_HOST = "localhost"
DEFAULT_DB_PORT = 5432
DEFAULT_DB_NAME = "semobackend"
DEFAULT_DB_USERNAME = "semo"
DEFAULT_DB_PASSWORD = "secret"


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
        description="Backfill cities.center_latitude/center_longitude for all cities via Google Places.",
    )
    parser.add_argument("--api-key", default="", help="Google Places API key.")
    parser.add_argument("--db-host", default=os.getenv("PLACES_DB_HOST", DEFAULT_DB_HOST))
    parser.add_argument("--db-port", type=int, default=int(os.getenv("PLACES_DB_PORT", str(DEFAULT_DB_PORT))))
    parser.add_argument("--db-name", default=os.getenv("PLACES_DB_NAME", DEFAULT_DB_NAME))
    parser.add_argument("--db-user", default=os.getenv("PLACES_DB_USER", DEFAULT_DB_USERNAME))
    parser.add_argument("--db-password", default=os.getenv("PLACES_DB_PASSWORD", DEFAULT_DB_PASSWORD))
    parser.add_argument("--max-search-results", type=int, default=10)
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument(
        "--skip-existing",
        action="store_true",
        help="When set, updates only cities missing one of center coordinates.",
    )
    return parser.parse_args()


def resolve_city_center(
    client: GooglePlacesClient,
    *,
    city_name_english: str,
    country_name_english: str,
    max_search_results: int,
) -> tuple[float, float] | None:
    query_labels = ("city center", "city hall", "downtown")
    place_types = ("tourist_attraction", None)
    city_query = f"{city_name_english}, {country_name_english}".strip(", ")
    for query_label in query_labels:
        for place_type in place_types:
            results = client.search_places(
                city_name=city_query,
                place_type=place_type,
                query_label=query_label,
                page_size=max_search_results,
                photo_limit=0,
            )
            for result in results:
                latitude = result.latitude
                longitude = result.longitude
                if latitude is None or longitude is None:
                    continue
                if abs(latitude) < 0.0001 and abs(longitude) < 0.0001:
                    continue
                return (latitude, longitude)
    return None


def main() -> int:
    if psycopg is None:
        print("psycopg is required. Install with scripts/places_seed/requirements.txt", file=sys.stderr)
        return 1

    args = parse_args()
    env_defaults = load_local_env_defaults()
    api_key = args.api_key or env_defaults.get("GOOGLE_PLACES_API_KEY") or os.getenv("GOOGLE_PLACES_API_KEY")
    if not api_key:
        print("Missing GOOGLE_PLACES_API_KEY", file=sys.stderr)
        return 1

    client = GooglePlacesClient(api_key=api_key, language_code="en", max_retries=3, timeout_seconds=30)
    connection = psycopg.connect(
        host=args.db_host,
        port=args.db_port,
        dbname=args.db_name,
        user=args.db_user,
        password=args.db_password,
    )

    updated_count = 0
    failed_count = 0

    filter_sql = """
        SELECT c.id, c.city_name_english, n.country_name_english
        FROM cities c
        JOIN nationalities n ON n.id = c.nationality_id
        ORDER BY id ASC
    """
    if args.skip_existing:
        filter_sql = """
            SELECT c.id, c.city_name_english, n.country_name_english
            FROM cities c
            JOIN nationalities n ON n.id = c.nationality_id
            WHERE c.center_latitude IS NULL
               OR c.center_longitude IS NULL
            ORDER BY id ASC
        """

    with connection:
        with connection.cursor() as cursor:
            cursor.execute(filter_sql)
            rows = cursor.fetchall()

        print(f"Cities to process: {len(rows)}")
        for city_id, city_name_english, country_name_english in rows:
            try:
                center = resolve_city_center(
                    client,
                    city_name_english=city_name_english,
                    country_name_english=country_name_english,
                    max_search_results=args.max_search_results,
                )
                if center is None:
                    failed_count += 1
                    print(f"[FAIL] cityId={city_id} city={city_name_english} reason=no usable coordinate found")
                    continue

                latitude, longitude = center
                if not args.dry_run:
                    with connection.cursor() as update_cursor:
                        update_cursor.execute(
                            """
                            UPDATE cities
                            SET center_latitude = %s,
                                center_longitude = %s
                            WHERE id = %s
                            """,
                            (latitude, longitude, city_id),
                        )
                updated_count += 1
                print(f"[OK] cityId={city_id} city={city_name_english} center=({latitude:.6f},{longitude:.6f})")
            except GooglePlacesError as error:
                failed_count += 1
                print(f"[FAIL] cityId={city_id} city={city_name_english} reason={error}")
            except Exception as error:  # pragma: no cover - operational fallback
                failed_count += 1
                print(f"[FAIL] cityId={city_id} city={city_name_english} reason={error}")

    print(f"Done. updated={updated_count} failed={failed_count} dryRun={args.dry_run}")
    return 0 if failed_count == 0 else 2


if __name__ == "__main__":
    raise SystemExit(main())
