#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
NLP_DIR="$ROOT_DIR/nlp-service"
VENV_DIR="$NLP_DIR/.venv"
NLP_HOST="${NLP_HOST:-127.0.0.1}"
NLP_PORT="${NLP_PORT:-8000}"
LOG_FILE="${LOG_FILE:-/tmp/govqa_nlp.log}"
PIP_LOG_FILE="${PIP_LOG_FILE:-/tmp/govqa_nlp_pip_install.log}"
REQ_FILE="$NLP_DIR/requirements.txt"
REQ_HASH_FILE="$VENV_DIR/.requirements.sha256"

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "[error] missing command: $1" >&2
    exit 1
  }
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

require_cmd curl
require_cmd lsof
require_cmd python3
require_cmd shasum

if [[ ! -d "$VENV_DIR" ]]; then
  echo "[info] create virtualenv: $VENV_DIR"
  python3 -m venv "$VENV_DIR"
fi

CURRENT_HASH="$(shasum -a 256 "$REQ_FILE" | awk '{print $1}')"
INSTALLED_HASH=""
if [[ -f "$REQ_HASH_FILE" ]]; then
  INSTALLED_HASH="$(cat "$REQ_HASH_FILE")"
fi

if [[ ! -x "$VENV_DIR/bin/uvicorn" || "$CURRENT_HASH" != "$INSTALLED_HASH" ]]; then
  echo "[info] install nlp dependencies"
  "$VENV_DIR/bin/pip" install -r "$REQ_FILE" >"$PIP_LOG_FILE" 2>&1
  printf '%s' "$CURRENT_HASH" >"$REQ_HASH_FILE"
fi

kill_port_if_needed "$NLP_PORT"

echo "[info] start nlp service"
(cd "$NLP_DIR" && nohup "$VENV_DIR/bin/uvicorn" app.main:app --host "$NLP_HOST" --port "$NLP_PORT" >"$LOG_FILE" 2>&1 &)

wait_http "http://${NLP_HOST}:${NLP_PORT}/health" "nlp"

echo
echo "NLP     : http://${NLP_HOST}:${NLP_PORT}/health"
echo "Log     : $LOG_FILE"
echo "Pip log : $PIP_LOG_FILE"
