# CLAUDE.md

Guidance for Claude Code (claude.ai/code) working in this repo.

## Commands

### Development

```bash
# Start database only
docker-compose up -d db adminer

# Backend (skips frontend rebuild; IntelliJ also skips automatically)
./gradlew bootRun -DskipFrontendBuild=true

# Frontend dev server (http://localhost:5173, proxies API to backend)
cd frontend && npm run dev
```

### Build

```bash
# Full production build (frontend → backend → bootJar)
./gradlew install

# Backend only
./gradlew :backend:build

# Frontend only
cd frontend && npm run build
```

### Tests

```bash
# All tests (backend + frontend)
./gradlew install

# Backend tests only
./gradlew :backend:test

# Single backend test
./gradlew :backend:test --tests "cz.forgottenempire.servermanager.serverinstance.ServerControllerTest.methodName"

# Frontend tests
cd frontend && npm test

# Single frontend test (pattern match)
cd frontend && npm test -- SteamAuthWizard
```

### Code generation

```bash
# Regenerate TypeScript API client from openapi/openapi.yaml
cd frontend && npm run generate
# Output: frontend/src/api/generated/ (gitignored)

# Backend generates during build automatically (output: backend/build/generated/openapi/)
```

## Architecture

### Multi-module Gradle project

```
arma-server-manager/
├── openapi/openapi.yaml          # Single source of truth for API contract
├── backend/                      # Spring Boot 4 / Java 25 + Kotlin 2.3.21
└── frontend/                     # React 19 / TypeScript / Vite
```

OpenAPI spec drives both sides: backend generates Spring controller interfaces + DTOs; frontend generates TypeScript-Axios client. **Never hand-write API interfaces or DTOs on either side** — edit `openapi/openapi.yaml` and regenerate.

Frontend build output copied into `backend/build/resources/main/static/` so backend serves SPA.

### Language policy

New backend files must be written in **Kotlin** (`src/main/kotlin` / `src/test/kotlin`). Editing existing Java files is fine — do not mass-convert. OpenAPI classes remain Java-generated; Kotlin consumes them via standard Java interop.

### Backend architecture

Standard Spring layered architecture per domain feature. Key packages under `cz.forgottenempire.servermanager`:

| Package | Role |
|---------|------|
| `api/` | Generated Spring interfaces — controllers implement these |
| `serverinstance/` | Core server lifecycle (start/stop/status), JPA entities, headless clients |
| `serverinstance/process/` | In-memory process registry — **not persisted**, orphaned after restart |
| `installation/` | SteamCMD-driven server install/update |
| `workshop/` | Workshop mod download and metadata |
| `modpreset/` | Mod preset CRUD + Arma Launcher preset import/export |
| `steamcmd/` | SteamCMD process execution and output parsing |
| `steamauth/` | Steam Guard token handling |
| `additionalserver/` | Non-native servers (Minecraft, etc.) |
| `scenario/` | Scenario (.pbo) upload/management |
| `system/` | System resource monitoring |
| `security/` | Session-based auth (Spring Security form login + CSRF), user config |
| `security/user/` | User CRUD, password management |
| `security/role/` | Role and permission management (RBAC) |
| `localmod/` | Local filesystem mod sync and management |
| `common/` | Cross-cutting enums (`ServerType`, `InstallationStatus`), exceptions, `RestExceptionHandler` |

Server entity hierarchy: `Server` (base JPA entity) → `Arma3Server`, `DayZServer`, `ReforgerServer`, `ReforgerExpServer`.

Schema migrations in `backend/src/main/resources/db/migration/`. Server config files rendered via FreeMarker templates in `backend/src/main/resources/templates/`.

### Frontend architecture

```
frontend/src/
├── api/
│   ├── generated/        # Generated — never edit
│   ├── client.ts         # Shared Axios instance (withCredentials, XSRF auto-attach, error toasts), all API instances
│   ├── downloads.ts      # Blob download helpers
│   └── serverModels.ts   # Extends generated types (Arma3/DayZ/Reforger ServerDto with type-specific fields)
├── pages/                # Route-level components
├── components/           # Feature components (servers/, mods/, dashboard/, steamauthwizard/, etc.)
├── services/             # authService.ts (login endpoint not in spec)
├── UI/                   # Shared primitives (ListBuilder, Form fields, EnhancedTable)
├── store/                # React Context (auth)
├── util/                 # Formatters, maps (serverNames, workshopErrorStatusMap)
└── pages/initialServerStateCreator.ts  # Default form values for new servers
```

All API calls go through instances exported from `src/api/client.ts`. Generator creates `typescript-axios` classes with request-object parameter style (e.g., `serversApi.getServer({id})`).

**Known generator limitation:** `allOf: [$ref, inline_schema]` pattern with discriminator collapses subtypes to base type. `src/api/serverModels.ts` provides correct `Arma3ServerDto`, `DayZServerDto`, `ReforgerServerDto` interfaces with type-specific fields. Import server-specific types from `../api/serverModels`, not `../api/generated`.

### Configuration

`config/application.properties` (not committed) required to run backend. Copy from `config/application.properties.EXAMPLE`. Key fields: `steam.api.key`, `steamcmd.path`, directory paths, DB credentials, `auth.username`/`auth.password`.

`.env` at repo root used by Docker Compose — also not committed.

### Testing

Backend integration tests extend `AbstractIntegrationTest` — spins up MySQL via Testcontainers. Test helpers in `backend/src/test/java/.../support/`: `Api.java` (REST Assured builder), `AuthTestHelper.java`, `Builders.java` (object mothers), fake process implementations.

Frontend tests use Jest + jsdom + ts-jest. Tests in `frontend/__tests__/`. Mock `src/api/client` for tests calling API methods — use `jest.mocked()` for type-safe mock access (not `as any`).

**E2E (Playwright):** use the `/e2e` skill. Before any E2E run, check for and kill a stale backend on port 8080 (`fuser -k 8080/tcp`) — Playwright's `reuseExistingServer` reuses whatever is listening and produces false failures. Playwright starts its own backend (`:backend:e2eApp`) and Vite dev server; never start them manually for E2E.

### Java/Kotlin conventions

- This is a JPA/Hibernate project: **never use Kotlin data classes for JPA entities** — use normal classes with manual `equals`/`hashCode`.
- Lombok interop from Kotlin uses the Kotlin Lombok compiler plugin, not kapt.

### Arma/DayZ/Reforger domain reference

Never assert launch-parameter or server-config behavior from memory. Consult `docs/wiki-db/` (offline BI wiki scrape: `params/<game>.json` for exact lookup, `guides/*.md` for context — 546 params, 13 guides). If `docs/wiki-db/` is absent on the current branch, it lives on `feature/per-instance-server-management`; read it via `git show` rather than guessing.

## Server config reference

`docs/wiki-db/` contains a local offline database scraped from the BI community wiki — use it instead of fetching the wiki live.

- `docs/wiki-db/params/<game>.json` — structured param records (name, type, default, description, since, example). Games: `arma3`, `dayz`, `reforger`, `reforger-exp`. 546 params total.
- `docs/wiki-db/guides/*.md` — cleaned Markdown guides (one per wiki page) with front-matter tags.

Quick lookup:
```bash
grep -r "paramName" docs/wiki-db/params/
grep -rl "topic" docs/wiki-db/guides/
```

Refresh: `cd tools/wiki-scraper && .venv/bin/python scrape.py`

## Agents

Delegate aggressively to keep the main thread's context lean — the main thread reasons on summaries, subagents do the gathering:

- Codebase exploration / "where is X": `cavecrew-investigator` (~60% compressed output) or `Explore`.
- 1-2 file mechanical edits: `cavecrew-builder`.
- Diff review: `cavecrew-reviewer`.
- Log parsing, bulk file reads, test-output triage, web research: `general-purpose` subagent with `model: sonnet` (or haiku for trivial scans); have it return a short summary, not raw output.

Do the work inline only when it needs the main thread's full conversation context or is a quick single-file read.