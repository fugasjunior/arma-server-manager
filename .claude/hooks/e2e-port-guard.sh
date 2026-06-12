#!/usr/bin/env bash
# PreToolUse(Bash): block E2E runs while a possibly-stale backend occupies port 8080.
input=$(cat)
cmd=$(jq -r '.tool_input.command // empty' <<<"$input")
case "$cmd" in
  *"npm run e2e"*|*"playwright test"*) ;;
  *) exit 0 ;;
esac
case "$cmd" in *PORT_OK*) exit 0 ;; esac
if ss -ltn 2>/dev/null | grep -q ':8080 '; then
  echo "Port 8080 is occupied. Playwright's reuseExistingServer will reuse this possibly-stale backend and produce false test failures. Kill it first ('fuser -k 8080/tcp', wait 2s), then retry. If reuse is intentional (backend freshly started via :backend:e2eApp this run), prefix the command with PORT_OK=1." >&2
  exit 2
fi
exit 0
