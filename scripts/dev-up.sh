#!/usr/bin/env sh
set -eu
sh ./scripts/sync-local-env.sh
docker compose --env-file .env.local up -d --build
