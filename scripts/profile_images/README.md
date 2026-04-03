# Default Profile Avatar Setup

This script slices a 3x3 avatar sheet into 9 separate PNG files, uploads them to the profile-picture S3 bucket, and backfills `users.profile_image_url` for users that currently have no profile image.

## Prerequisites

- Python 3.11+
- Install dependencies:
  - `pip install -r scripts/profile_images/requirements.txt`
- AWS credentials in `backend/.env.properties` or shell env:
  - `AWS_ACCESS_KEY_ID`
  - `AWS_SECRET_ACCESS_KEY`
  - `AWS_REGION` (defaults to `ap-northeast-2`)
  - `AWS_PROFILE_PICTURES_BUCKET` (defaults to `semothon-14-profile-pictures`)
- PostgreSQL access (defaults match local backend dev DB)

## Run

From `backend/`:

```bash
python3 scripts/profile_images/setup_default_profile_avatars.py \
  --source-image ../frontend/img_1.png
```

## Dry Run

```bash
python3 scripts/profile_images/setup_default_profile_avatars.py \
  --source-image ../frontend/img_1.png \
  --dry-run
```

## Behavior

- Upload target keys:
  - `defaults/profile-avatars/avatar_1.png` ... `avatar_9.png`
- Public URL format:
  - `https://{bucket}.s3.{region}.amazonaws.com/defaults/profile-avatars/avatar_{n}.png`
- Backfill query updates users with `NULL`/blank `profile_image_url` using random one of the 9 URLs.
