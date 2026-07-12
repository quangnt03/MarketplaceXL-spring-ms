#!/usr/bin/env sh
set -eu
if [ ! -f .env ]; then
  cp .env.example .env
fi
if [ ! -f frontend/web/.env.local ]; then
  cp frontend/web/.env.local.example frontend/web/.env.local
fi
