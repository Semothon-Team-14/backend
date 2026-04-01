# Place Image Upload Script

This script uploads local cafe and restaurant images to S3 while preserving the relative path as the object key.

## Requirements

- Python 3.10+
- `pip install -r scripts/place_image_upload/requirements.txt`
- `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`, and `AWS_S3_BUCKET` configured locally

## Expected local file layout

Use the same key layout as the seed data:

```text
<source-dir>/
  cafes/
    10001/
      1.jpg
      2.jpg
      3.jpg
  restaurants/
    30001/
      1.jpg
      2.jpg
      3.jpg
```

## Usage

```bash
python3 scripts/place_image_upload/upload_place_images_to_s3.py \
  --source-dir /path/to/place-images
```

Each uploaded file prints its final public URL.
