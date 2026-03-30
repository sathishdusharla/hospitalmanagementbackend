#!/usr/bin/env sh
set -eu

APP_PORT="${PORT:-8081}"
echo "Starting backend on port ${APP_PORT}"

exec java -Dserver.port="${APP_PORT}" -jar app.jar
