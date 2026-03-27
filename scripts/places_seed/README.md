# Places Seed Scripts

This directory contains Python scripts for generating cafe and restaurant seed data from the Google Places API and inserting it into the local PostgreSQL database.

This is a local-only seed job. It is not intended to run as part of the main production deploy flow.

## What it generates

Running the generator inserts rows into these tables:

- `cafes`
- `cafe_images`
- `restaurants`
- `restaurant_images`

Optionally, it can also write these files to `scripts/places_seed/output/`:

- `cafes.csv`
- `cafe_images.csv`
- `restaurants.csv`
- `restaurant_images.csv`

Each city from `src/main/resources/db/changelog/data/cities.csv` is processed, and the script requires:

- 5 cafes per city
- 5 restaurants per city
- 3 photos per cafe or restaurant

If a city does not have enough usable Google Places results, the run fails instead of writing partial data.

## Requirements

- Python 3.10+
- Install dependencies with `pip install -r scripts/places_seed/requirements.txt`
- A Google Places API key with Places API access enabled
- A local PostgreSQL database matching the backend local profile

## Usage

Set the API key:

```bash
export GOOGLE_PLACES_API_KEY="your-api-key"
```

Preview the fetch without touching the database:

```bash
python3 scripts/places_seed/generate_places_seed.py --dry-run
```

Run the local seed job and insert directly into PostgreSQL:

```bash
python3 scripts/places_seed/generate_places_seed.py
```

Also write CSV snapshots:

```bash
python3 scripts/places_seed/generate_places_seed.py --write-csv
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
- The default database connection values match `src/main/resources/application-local.yaml` and `compose.yaml`.
- Use `--skip-database --write-csv` if you only want CSV output.
- The generated place CSVs match the current `cafes` and `restaurants` table columns.
- The generated image CSVs match the current `cafe_images` and `restaurant_images` table columns.
- Image URLs are Google Places photo media URLs built from the returned photo references.
