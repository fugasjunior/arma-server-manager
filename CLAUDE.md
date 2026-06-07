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

## Agents

For codebase exploration use `Explore` subagent or `cavecrew-investigator` (returns ~60% compressed output). For 1-2 file edits use `cavecrew-builder`.