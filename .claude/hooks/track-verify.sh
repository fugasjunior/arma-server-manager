#!/usr/bin/env bash
# PostToolUse(Bash): clear the needs-verify marker when a build/test command ran.
input=$(cat)
cmd=$(jq -r '.tool_input.command // empty' <<<"$input")
root="${CLAUDE_PROJECT_DIR:-$(pwd)}"
case "$cmd" in
  *gradlew*test*|*gradlew*build*|*"gradlew install"*|*"npm test"*|*"npm run test"*|*"npm run e2e"*|*"playwright test"*)
    rm -f "$root/.claude/.needs-verify"
    ;;
esac
exit 0
