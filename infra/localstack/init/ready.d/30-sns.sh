#!/usr/bin/env sh
set -eu

topic="${MARKETPLACE_EVENTS_TOPIC:-marketplace-local-events}"

topic_arn="$(
  awslocal sns create-topic \
    --name "$topic" \
    --query TopicArn \
    --output text
)"

echo "SNS topic ready: $topic_arn"