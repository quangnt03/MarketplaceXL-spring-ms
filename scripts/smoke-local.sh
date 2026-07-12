#!/usr/bin/env sh
set -eu
curl -fsS http://localhost:8080/actuator/health >/dev/null
