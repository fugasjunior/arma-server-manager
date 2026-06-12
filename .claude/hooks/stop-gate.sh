#!/usr/bin/env bash
# Stop: block finishing when code was edited but no build/tests ran since.
input=$(cat)
active=$(jq -r '.stop_hook_active // false' <<<"$input")
[ "$active" = "true" ] && exit 0
root="${CLAUDE_PROJECT_DIR:-$(pwd)}"
if [ -f "$root/.claude/.needs-verify" ]; then
  rm -f "$root/.claude/.needs-verify"
  echo "Code was edited this session but no build/tests have run since the last edit. Verify before finishing: './gradlew :backend:test -DskipFrontendBuild=true' for backend changes, 'cd frontend && npm test' for frontend changes. If verification is genuinely not applicable (docs-only change, user said skip), state that explicitly and finish." >&2
  exit 2
fi
exit 0
