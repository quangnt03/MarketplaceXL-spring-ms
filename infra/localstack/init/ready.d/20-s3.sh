#!/usr/bin/env sh
set -eu

bucket="${MARKETPLACE_DOCUMENT_BUCKET:-marketplace-local-documents}"

if ! awslocal s3api head-bucket --bucket "$bucket" >/dev/null 2>&1; then
  awslocal s3api create-bucket --bucket "$bucket"
fi

awslocal s3api put-public-access-block \
  --bucket "$bucket" \
  --public-access-block-configuration \
  BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true

echo "S3 bucket ready: $bucket"