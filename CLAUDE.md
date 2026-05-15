# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

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
├── backend/                      # Spring Boot 4 / Java 25
└── frontend/                     # React 18 / TypeScript / Vite
```

The OpenAPI spec drives both sides: the backend generates Spring controller interfaces + DTOs; the frontend generates a TypeScript-Axios client. **Never hand-write API interfaces or DTOs on either side** — edit `openapi/openapi.yaml` and regenerate.

Frontend build output is copied into `backend/build/resources/main/static/` so the backend serves the SPA.

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
| `security/` | JWT auth, in-memory user config |
| `common/` | Cross-cutting enums (`ServerType`, `InstallationStatus`), exceptions, `RestExceptionHandler` |

Server entity hierarchy: `Server` (base JPA entity) → `Arma3Server`, `DayZServer`, `ReforgerServer`, `ReforgerExpServer`. These use `@Configurable` + AspectJ post-compile weaving for Spring injection into JPA entities — a known coupling issue.

Schema migrations live in `backend/src/main/resources/db/migration/`. Server config files are rendered via FreeMarker templates in `backend/src/main/resources/templates/`.

### Frontend architecture

```
frontend/src/
├── api/
│   ├── generated/        # Generated — never edit
│   ├── client.ts         # Shared Axios instance (JWT interceptor, error toasts), all API instances
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

All API calls go through the instances exported from `src/api/client.ts`. The generator creates `typescript-axios` classes with request-object parameter style (e.g., `serversApi.getServer({id})`).

**Known generator limitation:** The `allOf: [$ref, inline_schema]` pattern with a discriminator collapses subtypes to their base type. `src/api/serverModels.ts` provides the correct `Arma3ServerDto`, `DayZServerDto`, `ReforgerServerDto` interfaces that include type-specific fields. Import server-specific types from `../api/serverModels`, not `../api/generated`.

### Configuration

`config/application.properties` (not committed) is required to run the backend. Copy from `config/application.properties.EXAMPLE`. Key fields: `steam.api.key`, `steamcmd.path`, directory paths, DB credentials, `auth.username`/`auth.password`, `auth.jwt.secret`.

The `.env` file at repo root is used by Docker Compose and is also not committed.

### Testing

Backend integration tests extend `AbstractIntegrationTest` which spins up MySQL via Testcontainers. Test helpers live in `backend/src/test/java/.../support/`: `Api.java` (REST Assured builder), `AuthTestHelper.java`, `Builders.java` (object mothers), fake process implementations.

Frontend tests use Jest + jsdom + ts-jest. Tests live in `frontend/__tests__/`. Mock `src/api/client` for tests that call API methods — use `jest.mocked()` for type-safe access to mocks (not `as any`).
