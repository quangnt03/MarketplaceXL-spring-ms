#!/usr/bin/env sh
set -eu
if [ ! -f .env.local ]; then
  cp .env.example .env.local
fi
if [ ! -f frontend/web/.env.local ]; then
  cp frontend/web/.env.local.example frontend/web/.env.local
fi
