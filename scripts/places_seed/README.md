# Places Seed Scripts

This directory contains Python scripts for generating cafe and restaurant seed data from the Google Places API, uploading place photos to S3, and inserting the resulting rows into the local PostgreSQL database. It also fetches one representative image per city and updates `cities.representative_image_url`.

This is a local-only seed job. It is not intended to run as part of the main production deploy flow.

## What it generates

Running the generator inserts rows into these tables:

- `cafes`
- `cafe_images`
- `restaurants`
- `restaurant_images`
- updates `cities.representative_image_url`

The current default run targets 10 major cities already present in `src/main/resources/db/changelog/data/cities.csv`:

- Seoul
- Tokyo
- Hong Kong
- Singapore
- Bangkok
- New York
- Toronto
- London
- Paris
- Dubai

For each city, the script stores:

- 5 cafes
- 5 restaurants
- 3 photos per cafe or restaurant
- 1 city representative image (`places/cities/{city_id}/representative.jpg`)

If a city does not have enough usable Google Places results, the run fails instead of writing partial data.

## Requirements

- Python 3.10+
- Install dependencies with `pip install -r scripts/places_seed/requirements.txt`
- A Google Places API key with Places API access enabled
- AWS credentials and bucket settings configured locally
- A local PostgreSQL database matching the backend local profile

## Usage

Set the API key:

```bash
export GOOGLE_PLACES_API_KEY="your-api-key"
```

Preview the fetch without touching the database or S3:

```bash
python3 scripts/places_seed/generate_places_seed.py --dry-run --skip-s3-upload
```

Run the local seed job, upload photos to S3, and insert directly into PostgreSQL:

```bash
python3 scripts/places_seed/generate_places_seed.py
```

Use custom local database settings:

```bash
python3 scripts/places_seed/generate_places_seed.py \
  --db-host localhost \
  --db-port 5432 \
  --db-name semobackend \
  --db-user semo \
  --db-password secret
```

## Notes

- By default the script truncates and reloads `cafes`, `cafe_images`, `restaurants`, and `restaurant_images`.
- The script updates `cities.representative_image_url` for the seeded city IDs.
- The default database connection values match `src/main/resources/application-local.yaml` and `compose.yaml`.
- Progress is tracked in `scripts/places_seed/output/google_places_progress.json`, so reruns can continue without repeating finished city/category work.
- Image URLs stored in the database are public S3 object URLs, not Google media URLs.
- Use `--reset-progress` if you need to discard prior partial progress and start over.
