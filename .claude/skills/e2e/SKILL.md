---
name: e2e
description: Run the Playwright E2E suite with a clean backend lifecycle — kill stale port 8080 backend, run suite, iterate failures to green. Use for "run e2e", "fix e2e tests", E2E verification.
---

# E2E test loop

1. Check port 8080: `ss -ltn | grep ':8080 '`. If occupied, kill it (`fuser -k 8080/tcp`), wait 2s, confirm free. Playwright's `reuseExistingServer` would otherwise reuse a stale backend and produce false failures.
2. Run the suite: `cd frontend && npm run e2e`. Playwright's `webServer` starts a fresh backend itself (`./gradlew :backend:e2eApp`, up to 180s) plus the Vite dev server — do not start a backend manually.
3. On failures: root-cause from test output and traces in `frontend/e2e/test-results/`. Fix, then re-run only the failed spec first (`npm run e2e -- <spec-file>`), then the full suite.
4. Iterate until the whole suite is green. Green full suite is the completion bar — never claim done on partial results.
5. If a run was interrupted and left a backend on 8080, kill it so the next run starts clean.
