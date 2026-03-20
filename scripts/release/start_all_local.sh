#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-12345678}"
DB_NAME="${DB_NAME:-gov_qa}"
FRONTEND_PORT="${FRONTEND_PORT:-5173}"
BACKEND_PORT="${BACKEND_PORT:-8080}"
NLP_PORT="${NLP_PORT:-8000}"
MAVEN_REPO_LOCAL="${MAVEN_REPO_LOCAL:-/tmp/m2}"

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "[error] missing command: $1" >&2
    exit 1
  }
}

wait_http() {
  local url="$1"
  local name="$2"
  local i
  for i in $(seq 1 60); do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "[ok] $name ready: $url"
      return 0
    fi
    sleep 1
  done
  echo "[error] $name not ready: $url" >&2
  return 1
}

kill_port_if_needed() {
  local port="$1"
  local pid
  pid="$(lsof -tiTCP:"$port" -sTCP:LISTEN -n -P 2>/dev/null || true)"
  if [[ -n "$pid" ]]; then
    echo "[info] stop process on port $port: $pid"
    kill "$pid" || true
    sleep 1
  fi
}

db_initialized() {
  mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" -Nse \
    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${DB_NAME}' AND table_name='admin';" \
    2>/dev/null | grep -q '^1$'
}

init_db_if_needed() {
  if db_initialized; then
    echo "[info] database already initialized: $DB_NAME"
    return 0
  fi
  echo "[info] initialize database: $DB_NAME"
  mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" < "$ROOT_DIR/sql/init.sql"
}

require_cmd curl
require_cmd lsof
require_cmd mysql
require_cmd python3
require_cmd pip3
require_cmd npm
require_cmd mvn

cd "$ROOT_DIR"

init_db_if_needed

echo "[info] install frontend dependencies if needed"
if [[ ! -d "$ROOT_DIR/frontend/node_modules" ]]; then
  (cd "$ROOT_DIR/frontend" && npm install)
fi

echo "[info] install nlp dependencies"
(cd "$ROOT_DIR/nlp-service" && pip3 install -r requirements.txt >/tmp/govqa_pip_install.log 2>&1)

echo "[info] compile backend"
(cd "$ROOT_DIR/backend" && mvn -q compile -Dmaven.repo.local="$MAVEN_REPO_LOCAL")

kill_port_if_needed "$NLP_PORT"
kill_port_if_needed "$BACKEND_PORT"
kill_port_if_needed "$FRONTEND_PORT"

echo "[info] start nlp service"
(cd "$ROOT_DIR/nlp-service" && nohup uvicorn app.main:app --host 127.0.0.1 --port "$NLP_PORT" > /tmp/govqa_nlp.log 2>&1 &)

echo "[info] start backend service"
(cd "$ROOT_DIR/backend" && nohup mvn -q org.springframework.boot:spring-boot-maven-plugin:3.3.5:run -Dmaven.repo.local="$MAVEN_REPO_LOCAL" > /tmp/govqa_backend.log 2>&1 &)

echo "[info] start frontend service"
(cd "$ROOT_DIR/frontend" && nohup npm run dev -- --host 127.0.0.1 --port "$FRONTEND_PORT" > /tmp/govqa_frontend.log 2>&1 &)

wait_http "http://127.0.0.1:${NLP_PORT}/health" "nlp"
wait_http "http://127.0.0.1:${BACKEND_PORT}/health" "backend"
wait_http "http://127.0.0.1:${FRONTEND_PORT}" "frontend"

echo
echo "Frontend: http://127.0.0.1:${FRONTEND_PORT}/"
echo "Backend : http://127.0.0.1:${BACKEND_PORT}/health"
echo "NLP     : http://127.0.0.1:${NLP_PORT}/health"
echo "Logs    : /tmp/govqa_frontend.log /tmp/govqa_backend.log /tmp/govqa_nlp.log"
