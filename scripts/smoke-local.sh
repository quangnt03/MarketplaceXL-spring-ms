#!/usr/bin/env sh
set -eu

wait_for_url() {
  name="$1"
  url="$2"
  attempts=30

  while [ "$attempts" -gt 0 ]; do
    if curl -fsS "$url" >/dev/null; then
      echo "$name is ready"
      return 0
    fi
    attempts=$((attempts - 1))
    sleep 2
  done

  echo "$name did not become ready: $url" >&2
  return 1
}

wait_for_url "backend" "http://localhost:${BACKEND_PORT:-8080}/actuator/health/readiness"
wait_for_url "frontend" "http://localhost:${FRONTEND_PORT:-3000}"
