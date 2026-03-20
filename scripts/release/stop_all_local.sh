#!/usr/bin/env bash
set -euo pipefail

stop_port() {
  local port="$1"
  local pid
  pid="$(lsof -tiTCP:"$port" -sTCP:LISTEN -n -P 2>/dev/null || true)"
  if [[ -n "$pid" ]]; then
    echo "[info] stop port $port pid=$pid"
    kill "$pid" || true
  else
    echo "[info] port $port not running"
  fi
}

stop_port 5173
stop_port 8080
stop_port 8000
