# Places Seed Scripts

This directory contains Python scripts for generating cafe and restaurant seed CSVs from the Google Places API.

## What it generates

Running the generator writes these files to `scripts/places_seed/output/`:

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
- A Google Places API key with Places API access enabled

## Usage

Set the API key:

```bash
export GOOGLE_PLACES_API_KEY="your-api-key"
```

Preview the fetch without writing files:

```bash
python3 scripts/places_seed/generate_places_seed.py --dry-run
```

Generate the CSV files:

```bash
python3 scripts/places_seed/generate_places_seed.py
```

Use a custom output directory:

```bash
python3 scripts/places_seed/generate_places_seed.py --output-dir /tmp/places-seed
```

## Notes

- The generated place CSVs match the current `cafes` and `restaurants` table columns.
- The generated image CSVs match the current `cafe_images` and `restaurant_images` table columns.
- Image URLs are Google Places photo media URLs built from the returned photo references.
