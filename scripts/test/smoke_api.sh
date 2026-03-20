#!/usr/bin/env bash
set -euo pipefail

BASE=${1:-http://127.0.0.1:8080}
TOKEN=$(curl -s -X POST "$BASE/api/admin/login" -H 'Content-Type: application/json' -d '{"username":"admin","password":"Admin@123"}' | jq -r '.data.token')

echo "[smoke] auth_guard"
code=$(curl -s -o /tmp/auth_guard.json -w '%{http_code}' "$BASE/api/admin/categories")
[[ "$code" == "401" ]]

echo "[smoke] admin categories"
curl -s "$BASE/api/admin/categories" -H "Authorization: $TOKEN" | jq '.code==0' | grep true >/dev/null

echo "[smoke] public ask"
curl -s -X POST "$BASE/api/public/ask" -H 'Content-Type: application/json' -d '{"question":"社保怎么转到本地","topN":3}' | jq '.code==0' | grep true >/dev/null

echo "[smoke] dashboard"
curl -s "$BASE/api/admin/dashboard/overview" -H "Authorization: $TOKEN" | jq '.code==0' | grep true >/dev/null

echo "smoke_ok"
