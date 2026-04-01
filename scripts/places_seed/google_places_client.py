from __future__ import annotations

import json
import time
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass


TEXT_SEARCH_URL = "https://places.googleapis.com/v1/places:searchText"


class GooglePlacesError(RuntimeError):
    pass


@dataclass(frozen=True)
class PlacePhoto:
    name: str
    url: str


@dataclass(frozen=True)
class PlaceResult:
    name: str
    phone_number: str | None
    address: str | None
    category: str | None
    latitude: float | None
    longitude: float | None
    photos: tuple[PlacePhoto, ...]


class GooglePlacesClient:
    def __init__(
        self,
        api_key: str,
        *,
        language_code: str = "en",
        max_retries: int = 3,
        timeout_seconds: int = 30,
    ) -> None:
        self._api_key = api_key
        self._language_code = language_code
        self._max_retries = max_retries
        self._timeout_seconds = timeout_seconds

    def search_places(
        self,
        *,
        city_name: str,
        place_type: str,
        query_label: str,
        page_size: int,
        photo_limit: int,
    ) -> list[PlaceResult]:
        payload = {
            "textQuery": f"{query_label} in {city_name}",
            "languageCode": self._language_code,
            "pageSize": page_size,
            "includedType": place_type,
            "strictTypeFiltering": True,
        }
        data = self._post_json(TEXT_SEARCH_URL, payload)
        places = data.get("places", [])
        return [self._to_place_result(place, photo_limit) for place in places]

    def _to_place_result(self, place: dict, photo_limit: int) -> PlaceResult:
        location = place.get("location") or {}
        photos = tuple(
            PlacePhoto(
                name=photo["name"],
                url=self._build_photo_media_url(photo["name"]),
            )
            for photo in place.get("photos", [])[:photo_limit]
            if photo.get("name")
        )

        return PlaceResult(
            name=place.get("displayName", {}).get("text", "").strip(),
            phone_number=place.get("nationalPhoneNumber"),
            address=place.get("formattedAddress"),
            category=place.get("primaryTypeDisplayName", {}).get("text"),
            latitude=location.get("latitude"),
            longitude=location.get("longitude"),
            photos=photos,
        )

    def _build_photo_media_url(self, photo_name: str) -> str:
        encoded_name = urllib.parse.quote(photo_name, safe="/")
        query = urllib.parse.urlencode(
            {
                "key": self._api_key,
                "maxWidthPx": 1600,
                "maxHeightPx": 1200,
            }
        )
        return f"https://places.googleapis.com/v1/{encoded_name}/media?{query}"

    def _post_json(self, url: str, payload: dict) -> dict:
        request = urllib.request.Request(
            url,
            data=json.dumps(payload).encode("utf-8"),
            method="POST",
            headers={
                "Content-Type": "application/json",
                "X-Goog-Api-Key": self._api_key,
                "X-Goog-FieldMask": ",".join(
                    [
                        "places.displayName",
                        "places.formattedAddress",
                        "places.location",
                        "places.nationalPhoneNumber",
                        "places.photos",
                        "places.primaryTypeDisplayName",
                    ]
                ),
            },
        )

        for attempt in range(1, self._max_retries + 1):
            try:
                with urllib.request.urlopen(
                    request,
                    timeout=self._timeout_seconds,
                ) as response:
                    return json.loads(response.read().decode("utf-8"))
            except urllib.error.HTTPError as error:
                if error.code not in {429, 500, 502, 503, 504}:
                    details = error.read().decode("utf-8", errors="replace")
                    raise GooglePlacesError(
                        f"Google Places request failed with HTTP {error.code}: {details}"
                    ) from error
                if attempt == self._max_retries:
                    raise GooglePlacesError(
                        f"Google Places request failed after {attempt} attempts with HTTP {error.code}"
                    ) from error
            except urllib.error.URLError as error:
                if attempt == self._max_retries:
                    raise GooglePlacesError(
                        f"Google Places request failed after {attempt} attempts: {error.reason}"
                    ) from error
            time.sleep(attempt)

        raise GooglePlacesError("Google Places request failed unexpectedly")
