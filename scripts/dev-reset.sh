#!/usr/bin/env sh
set -eu
docker compose --env-file .env.local down -v
docker compose --env-file .env.local up -d
