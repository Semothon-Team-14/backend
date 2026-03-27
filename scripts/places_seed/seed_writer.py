from __future__ import annotations

import csv
from dataclasses import dataclass
from decimal import Decimal, ROUND_HALF_UP
from pathlib import Path

from google_places_client import PlaceResult


@dataclass(frozen=True)
class City:
    id: int
    name_english: str


@dataclass(frozen=True)
class SeedPlace:
    id: int
    city_id: int
    name: str
    phone_number: str | None
    address: str | None
    food_category: str | None
    latitude: str | None
    longitude: str | None
    photos: tuple[str, ...]


def read_cities(cities_csv_path: Path) -> list[City]:
    with cities_csv_path.open("r", encoding="utf-8", newline="") as csv_file:
        reader = csv.DictReader(csv_file)
        return [
            City(
                id=int(row["id"]),
                name_english=row["city_name_english"].strip(),
            )
            for row in reader
        ]


def build_seed_places(
    *,
    city_id: int,
    places: list[PlaceResult],
    target_count: int,
    photo_count: int,
    starting_id: int,
) -> list[SeedPlace]:
    seed_places: list[SeedPlace] = []
    seen_names: set[str] = set()
    next_id = starting_id

    for place in places:
        normalized_name = place.name.casefold()
        if not place.name or normalized_name in seen_names:
            continue
        if len(place.photos) < photo_count:
            continue

        seen_names.add(normalized_name)
        seed_places.append(
            SeedPlace(
                id=next_id,
                city_id=city_id,
                name=place.name,
                phone_number=place.phone_number,
                address=place.address,
                food_category=place.category,
                latitude=_format_coordinate(place.latitude),
                longitude=_format_coordinate(place.longitude),
                photos=tuple(photo.url for photo in place.photos[:photo_count]),
            )
        )
        next_id += 1

        if len(seed_places) == target_count:
            break

    return seed_places


def write_place_csv(output_path: Path, places: list[SeedPlace]) -> None:
    _write_csv(
        output_path,
        fieldnames=[
            "id",
            "city_id",
            "name",
            "phone_number",
            "address",
            "food_category",
            "latitude",
            "longitude",
        ],
        rows=[
            {
                "id": place.id,
                "city_id": place.city_id,
                "name": place.name,
                "phone_number": place.phone_number or "",
                "address": place.address or "",
                "food_category": place.food_category or "",
                "latitude": place.latitude or "",
                "longitude": place.longitude or "",
            }
            for place in places
        ],
    )


def write_image_csv(
    output_path: Path,
    *,
    places: list[SeedPlace],
    parent_column_name: str,
) -> None:
    rows = []
    next_id = 1
    for place in places:
        for photo_index, photo_url in enumerate(place.photos):
            rows.append(
                {
                    "id": next_id,
                    parent_column_name: place.id,
                    "image_url": photo_url,
                    "main_image": str(photo_index == 0).lower(),
                }
            )
            next_id += 1

    _write_csv(
        output_path,
        fieldnames=["id", parent_column_name, "image_url", "main_image"],
        rows=rows,
    )


def _write_csv(output_path: Path, *, fieldnames: list[str], rows: list[dict]) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8", newline="") as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)


def _format_coordinate(value: float | None) -> str | None:
    if value is None:
        return None

    return str(
        Decimal(str(value)).quantize(
            Decimal("0.0000001"),
            rounding=ROUND_HALF_UP,
        )
    )
