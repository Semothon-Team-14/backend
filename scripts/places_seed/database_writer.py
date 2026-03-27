from __future__ import annotations

from dataclasses import dataclass
from typing import Iterable

from seed_writer import SeedPlace

try:
    import psycopg
except ModuleNotFoundError:  # pragma: no cover - handled at runtime
    psycopg = None


@dataclass(frozen=True)
class DatabaseConfig:
    host: str
    port: int
    database: str
    username: str
    password: str


def sync_places_to_database(
    *,
    database_config: DatabaseConfig,
    cafes: list[SeedPlace],
    restaurants: list[SeedPlace],
) -> None:
    if psycopg is None:
        raise RuntimeError(
            "psycopg is required for database writes. Install it with "
            "`pip install -r scripts/places_seed/requirements.txt`."
        )

    connection = psycopg.connect(
        host=database_config.host,
        port=database_config.port,
        dbname=database_config.database,
        user=database_config.username,
        password=database_config.password,
    )

    with connection:
        with connection.cursor() as cursor:
            _truncate_existing_seed_tables(cursor)
            _insert_places(
                cursor,
                table_name="cafes",
                places=cafes,
            )
            _insert_images(
                cursor,
                table_name="cafe_images",
                parent_column_name="cafe_id",
                places=cafes,
            )
            _insert_places(
                cursor,
                table_name="restaurants",
                places=restaurants,
            )
            _insert_images(
                cursor,
                table_name="restaurant_images",
                parent_column_name="restaurant_id",
                places=restaurants,
            )
            _reset_sequences(cursor)


def _truncate_existing_seed_tables(cursor: "psycopg.Cursor") -> None:
    cursor.execute("TRUNCATE TABLE cafe_images, restaurant_images, cafes, restaurants RESTART IDENTITY CASCADE")


def _insert_places(
    cursor: "psycopg.Cursor",
    *,
    table_name: str,
    places: list[SeedPlace],
) -> None:
    rows = [
        (
            place.id,
            place.city_id,
            place.name,
            place.phone_number,
            place.address,
            place.food_category,
            place.latitude,
            place.longitude,
        )
        for place in places
    ]
    cursor.executemany(
        (
            f"INSERT INTO {table_name} "
            "(id, city_id, name, phone_number, address, food_category, latitude, longitude) "
            "VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"
        ),
        rows,
    )


def _insert_images(
    cursor: "psycopg.Cursor",
    *,
    table_name: str,
    parent_column_name: str,
    places: list[SeedPlace],
) -> None:
    rows = list(_iter_image_rows(places))
    cursor.executemany(
        (
            f"INSERT INTO {table_name} "
            f"(id, {parent_column_name}, image_url, main_image) "
            "VALUES (%s, %s, %s, %s)"
        ),
        rows,
    )


def _iter_image_rows(places: list[SeedPlace]) -> Iterable[tuple[int, int, str, bool]]:
    next_id = 1
    for place in places:
        for photo_index, photo_url in enumerate(place.photos):
            yield next_id, place.id, photo_url, photo_index == 0
            next_id += 1


def _reset_sequences(cursor: "psycopg.Cursor") -> None:
    cursor.execute("SELECT setval('cafes_id_seq', COALESCE((SELECT MAX(id) FROM cafes), 1), true)")
    cursor.execute(
        "SELECT setval('restaurants_id_seq', COALESCE((SELECT MAX(id) FROM restaurants), 1), true)"
    )
    cursor.execute(
        "SELECT setval('cafe_images_id_seq', COALESCE((SELECT MAX(id) FROM cafe_images), 1), true)"
    )
    cursor.execute(
        "SELECT setval('restaurant_images_id_seq', COALESCE((SELECT MAX(id) FROM restaurant_images), 1), true)"
    )
