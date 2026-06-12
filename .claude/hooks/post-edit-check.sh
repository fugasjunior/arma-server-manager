#!/usr/bin/env bash
# PostToolUse(Edit|Write): typecheck the edited side, mark session as needing verification.
input=$(cat)
file=$(jq -r '.tool_input.file_path // empty' <<<"$input")
[ -z "$file" ] && exit 0
root="${CLAUDE_PROJECT_DIR:-$(pwd)}"

case "$file" in
  *"/frontend/src/api/generated/"*) exit 0 ;;
  *.ts|*.tsx)
    [[ "$file" != "$root/frontend/"* ]] && exit 0
    touch "$root/.claude/.needs-verify"
    cd "$root/frontend" || exit 0
    npx eslint --fix "$file" >/dev/null 2>&1
    if ! errors=$(npx tsc --noEmit --pretty false 2>&1); then
      { echo "tsc errors after edit:"; echo "$errors" | head -30; } >&2
      exit 2
    fi
    ;;
  *.java|*.kt)
    [[ "$file" != "$root/backend/"* ]] && exit 0
    touch "$root/.claude/.needs-verify"
    cd "$root" || exit 0
    if ! errors=$(./gradlew :backend:compileJava :backend:compileKotlin :backend:compileTestJava :backend:compileTestKotlin -q -DskipFrontendBuild=true 2>&1); then
      { echo "backend compile errors after edit:"; echo "$errors" | head -40; } >&2
      exit 2
    fi
    ;;
esac
exit 0
