#!/usr/bin/env bash
# Script para testar todos os endpoints da API (Transações, Estatísticas, Config).
# Uso: ./test-endpoints.sh [BASE_URL]
# Exemplo: BASE_URL=http://localhost:8080 ./test-endpoints.sh

set -e
BASE_URL="${1:-http://localhost:8080}"
PASS=0
FAIL=0

red() { printf '\033[31m%s\033[0m\n' "$1"; }
green() { printf '\033[32m%s\033[0m\n' "$1"; }
bold() { printf '\033[1m%s\033[0m\n' "$1"; }

check() {
  local name="$1"
  local expected_status="$2"
  local actual_status="$3"
  if [ "$expected_status" = "$actual_status" ]; then
    green "  OK $name (HTTP $actual_status)"
    ((PASS+=1))
    return 0
  else
    red "  FALHA $name (esperado $expected_status, obtido $actual_status)"
    ((FAIL+=1))
    return 1
  fi
}

# Data ISO-8601 para transação (agora - 5 segundos, para garantir past-or-present)
DATA_HORA=$(date -u -Iseconds 2>/dev/null || date -u +"%Y-%m-%dT%H:%M:%SZ")

bold "=== Testando API em $BASE_URL ==="

bold "--- GET /api/v1/estatisticas ---"
status=$(curl -s -o /tmp/estatisticas.json -w "%{http_code}" "$BASE_URL/api/v1/estatisticas")
check "GET estatísticas" "200" "$status"

bold "--- GET /api/v1/config/estatisticas/janela ---"
status=$(curl -s -o /tmp/janela.json -w "%{http_code}" "$BASE_URL/api/v1/config/estatisticas/janela")
check "GET janela config" "200" "$status"

bold "--- PATCH /api/v1/config/estatisticas/janela (120 segundos) ---"
status=$(curl -s -o /tmp/janela_patch.json -w "%{http_code}" -X PATCH \
  -H "Content-Type: application/json" \
  -d '{"janelaSegundos":120}' \
  "$BASE_URL/api/v1/config/estatisticas/janela")
check "PATCH janela 120" "200" "$status"

bold "--- PATCH /api/v1/config/estatisticas/janela (restaurar 60) ---"
status=$(curl -s -o /dev/null -w "%{http_code}" -X PATCH \
  -H "Content-Type: application/json" \
  -d '{"janelaSegundos":60}' \
  "$BASE_URL/api/v1/config/estatisticas/janela")
check "PATCH janela 60" "200" "$status"

bold "--- POST /api/v1/transacoes (transação válida) ---"
status=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  -H "Content-Type: application/json" \
  -d "{\"valor\":10.50,\"dataHora\":\"$DATA_HORA\"}" \
  "$BASE_URL/api/v1/transacoes")
check "POST transação válida" "201" "$status"

bold "--- POST /api/v1/transacoes (validação: data futura -> 422) ---"
status=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  -H "Content-Type: application/json" \
  -d '{"valor":5.00,"dataHora":"2030-01-01T12:00:00Z"}' \
  "$BASE_URL/api/v1/transacoes")
check "POST transação data futura (422)" "422" "$status"

bold "--- POST /api/v1/transacoes (validação: valor negativo -> 422) ---"
status=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  -H "Content-Type: application/json" \
  -d "{\"valor\":-1,\"dataHora\":\"$DATA_HORA\"}" \
  "$BASE_URL/api/v1/transacoes")
check "POST transação valor negativo (422)" "422" "$status"

bold "--- GET /api/v1/estatisticas (após uma transação) ---"
status=$(curl -s -o /tmp/estatisticas2.json -w "%{http_code}" "$BASE_URL/api/v1/estatisticas")
check "GET estatísticas após POST" "200" "$status"

bold "--- DELETE /api/v1/transacoes ---"
status=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/api/v1/transacoes")
check "DELETE transações" "200" "$status"

bold "--- PATCH /api/v1/config/estatisticas/janela (validação: fora do range -> 422) ---"
status=$(curl -s -o /dev/null -w "%{http_code}" -X PATCH \
  -H "Content-Type: application/json" \
  -d '{"janelaSegundos":99999}' \
  "$BASE_URL/api/v1/config/estatisticas/janela")
check "PATCH janela inválida 99999 (422)" "422" "$status"

bold "=== Resultado: $PASS passou, $FAIL falhou ==="
[ "$FAIL" -eq 0 ]
