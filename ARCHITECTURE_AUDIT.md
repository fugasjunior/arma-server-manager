# Architecture Audit & Kotlin Migration Plan

Audit date: 2026-05-14
Scope: `backend/` (Spring Boot 4.0.6, Java 25, MySQL 8, Gradle 9)

---

## 1. Pain Points & Proposed Solutions

Findings are grouped by category and ranked by severity (🔴 high / 
Each entry: **what's wrong → why it matters → proposed fix**.

### 1.1 Concurrency & shared state

| # | Severity | Finding                                                                                                                                                     | Fix                                                                                                                  |
|---|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| 1 | 🔴       | `AttributeEncryptor.java:29-43` reuses a single `Cipher` across threads. `Cipher.init()` on a shared instance is a race condition.                          | Use `ThreadLocal<Cipher>` or create a per-call instance. Better: switch to Google Tink / BouncyCastle and add an IV. |
| 2 | 🔴       | `AdditionalServersService.java:122` blocks the request thread with `Thread.sleep(30s)` during destroy.                                                      | Replace with `ScheduledExecutorService` or (in Kotlin) `delay()` inside a coroutine.                                 |
| 3 | 🟡       | Shutdown hook in `AdditionalServersService.java:40-41` iterates over a live `ConcurrentHashMap` snapshot with no timeout on `destroyWithTimeout()`.         | Bound total shutdown wait; use a `CompletableFuture.allOf(...)` with a hard deadline.                                |
| 4 | 🟡       | `CheckAdditionalServerInstancesStatusCronJob.java:24-39` filters on `process.alive()` then calls `.process()` again — process may be `null` between checks. | Snapshot the `Process` reference once per iteration; `Optional` chain.                                               |
| 5 | 🟢       | `PathsFactory.java:118-122` uses non-thread-safe `SimpleDateFormat`.                                                                                        | Switch to `DateTimeFormatter` (immutable).                                                                           |

### 1.2 Process lifecycle & state durability

| # | Severity | Finding                                                                                                                                                                             | Fix                                                                                             |
|---|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| 6 | 🔴       | `ServerProcessRepository` is an in-memory `ConcurrentHashMap`. After a backend crash/restart, the UI shows running game servers as stopped and orphan OS processes are unreachable. | Persist PIDs (or a process registry file) and re-attach via `ProcessHandle.of(pid)` on startup. |
| 7 | 🟡       | `ProcessFactory.java:24-27` silently falls back from `unbuffer` to plain `ProcessBuilder`. Log streaming becomes line-buffered in Docker.                                           | Fail fast at startup if `unbuffer` is configured but missing; surface it in a health endpoint.  |
| 8 | 🟡       | `TestRunService.java:40` uses a `synchronized` method with a port-probe retry loop; no `try-with-resources` around `SourceQueryClient`.                                             | Use `try-with-resources`; replace the lock with a per-server `ReentrantLock`.                   |
| 9 | 🟡       | `ServerConfig.java:48-72` deletes the old config before writing the new one — non-atomic; a failed write leaves the server unbootable.                                              | Write to `*.tmp`, then `Files.move(..., ATOMIC_MOVE)`.                                          |

### 1.3 Tight coupling & abstraction leaks

| #  | Severity | Finding                                                                                                                                                                                                                                                                             | Fix                                                                                                                                        |
|----|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| 10 | 🔴       | `@Configurable` + `@Autowired` on JPA entities (`Server.java:75-78`) and on `ServerProcess.java:109-120`. Requires AspectJ load-time weaving; makes entities un-unit-testable and creates a circular dependency (`ServerProcessRepository` ↔ `ServerProcess` ↔ `ServerRepository`). | Move all Spring deps out of entities. Pass `PathsFactory`/repositories at the service layer. Construct `ServerProcess` via a factory bean. |
| 11 | 🟡       | `ServerRepository.java:19-27` uses a hand-written native UNION over `arma3_server` / `dayz_server`, missing `reforger_server`.                                                                                                                                                      | Use a JPA polymorphic query on the abstract `Server`, or a Spring Data projection.                                                         |
| 12 | 🟡       | `WorkshopModsFacade` orchestrates 4 services and embeds validation. Classic god facade.                                                                                                                                                                                             | Split: a `ModValidator` (pure), a `ModInstallOrchestrator` (workflow), keep the facade thin.                                               |
| 13 | 🟡       | `ServerMapper.java:82-95` uses `instanceof` over the Server subtype tree; new subtypes silently miss mappings.                                                                                                                                                                      | In Kotlin: sealed class + exhaustive `when`. Today: add a default branch that throws.                                                      |
| 14 | 🟡       | `ServerType` enum is `switch`-ed in 5+ places (mappers, validators, factories). Adding a game touches all of them.                                                                                                                                                                  | Strategy pattern keyed on `ServerType`, or sealed-class polymorphism after the Kotlin migration.                                           |

### 1.4 Data integrity & persistence

| #  | Severity | Finding                                                                                                                                                                | Fix                                                                                                              |
|----|----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| 15 | 🟡       | No `@Version` on `Server` — concurrent edits silently last-write-win (e.g. mod list + restart flag).                                                                   | Add `@Version Long version` and handle `OptimisticLockException` in the controller advice.                       |
| 16 | 🟡       | Process state (running/PID) lives in memory; entity `running` flag lives in DB. The two drift on crashes.                                                              | Single source of truth: derive runtime state from the process registry, write through to DB on every transition. |
| 17 | 🟡       | `WorkshopApiMetadataProvider.java:58-59` chains `findValue("response").findValue("publishedfiledetails").get(0)` with no null checks. NPE on malformed Steam response. | Use `Optional` chain or move to `tools.jackson` `JsonNode.path()` (already imported elsewhere).                  |

### 1.5 Security & configuration

| #  | Severity | Finding                                                                                                                            | Fix                                                                                                                  |
|----|----------|------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| 18 | 🔴       | `WebSecurityConfig.java:106` sets CORS `allowedOrigins=*` with `allowCredentials` semantics; CSRF is also disabled.                | Require an explicit allowlist from `application.properties`; reject `*` at startup if credentials are enabled.       |
| 19 | 🔴       | `AttributeEncryptor` is a no-op if `database.encryption.secret` is unset — Steam passwords stored in plaintext, only a `WARN` log. | Fail startup when the secret is missing in non-dev profiles.                                                         |
| 20 | 🟡       | `InMemoryUserDetailsManager` with a single hardcoded user. Fine for single-admin, blocks multi-user features.                      | Move users to DB-backed `UserDetailsService` behind a feature flag; keep the in-memory path for the default profile. |
| 21 | 🟡       | `Constants.java:38` hard-codes Steam URLs, game IDs, executable names.                                                             | Externalize to `@ConfigurationProperties` so new games / private Steam mirrors don't require a recompile.            |
| 22 | 🟡       | `AdditionalServersService.java:113` sanitizes log dir names with a regex that preserves `.` — `serverName="../x"` survives.        | Use the server's numeric ID (UUID) for filesystem paths; treat the user-provided name as display-only.               |

### 1.6 Error handling & observability

| #  | Severity | Finding                                                                                                                           | Fix                                                                                 |
|----|----------|-----------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| 23 | 🟡       | `ServerConfig.java:48-52` swallows `IOException` on delete and continues to write.                                                | Bubble up; let the controller advice turn it into a 5xx with a clear message.       |
| 24 | 🟢       | `CheckServerInstancesStatusCronJob.java:74-76` catches `Exception` and logs the same way for timeout, parse error, network error. | Catch each specifically; expose counters via Micrometer.                            |
| 25 | 🟢       | `CheckServerInstancesStatusCronJob.java:49` log-spams the PID of a dead process every cron tick.                                  | Detect the *transition* (alive → dead) and log once.                                |
| 26 | 🟢       | No structured (JSON) logging; grep-based log review is fragile.                                                                   | Add `logstash-logback-encoder`; emit `serverId`, `gameType`, `event` as MDC fields. |

### 1.7 Testing & build

| #  | Severity | Finding                                                                                                  | Fix                                                                         |
|----|----------|----------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| 27 | 🟢       | Tests mostly use plain `Mockito.mock(...)` in constructors; no shared fixtures/builders.                 | Introduce object mothers (`Arma3ServerFixture.minimal()`).                  |
| 28 | 🟢       | No `testcontainers` for MySQL; tests use H2 or in-memory. Native query (#11) won't catch dialect issues. | Add a Testcontainers-based integration test slice.                          |
| 29 | 🟢       | API has no version prefix; `/api/...` directly. Hard to evolve during/after the rewrite.                 | Introduce `/api/v1/` now, before the rewrite, so v2 can land incrementally. |

### 1.8 What's already good (preserve in the rewrite)

- Feature-based packages (`workshop/`, `serverinstance/`, `additionalserver/` …) — keep.
- Clean exception pyramid (`CustomUserErrorException` / `NotFoundException` + `RestExceptionHandler`).
- MapStruct + DTO/entity separation.
- FreeMarker for `server.cfg` / `Arma3Profile` generation.
- Constructor injection in services (no field injection except the `@Configurable` cases above).
- JPA `JOINED` inheritance for the `Server` hierarchy.
- Flyway migrations.

---

## 2. Kotlin Rewrite Plan

### 2.1 Goals & non-goals

**Goals**

- Eliminate the high-severity findings above (1, 2, 6, 10, 18, 19) as part of the migration rather than as a separate refactor.
- Replace Lombok with Kotlin data classes / `val` properties.
- Replace `@Scheduled` blocking jobs with structured concurrency (coroutines).
- Convert the `Server` hierarchy to a `sealed class` so adding a game is a compile-time error wherever it's not handled.
- Keep the public REST contract byte-for-byte identical during the migration. Frontend must not change.

**Non-goals**

- No framework swap (stay on Spring Boot; do **not** move to Ktor).
- No database schema redesign beyond adding `@Version` and the process-registry table.
- No move to reactive / WebFlux.

### 2.2 Foundation work (do *before* writing Kotlin)

Land these PRs on `master` first — they shrink the migration surface and freeze the public API contract before the rewrite touches any layer.

1. **Remove `@Configurable` from entities and `ServerProcess`.** Refactor so all Spring deps flow through services/factories. (Finding #10.)
2. **Add `@Version` to `Server`.** Plus a Flyway migration. (Finding #15.)
3. **Replace the native UNION query** with a JPA polymorphic query on `Server`. (Finding #11.)
4. **Introduce `/api/v1/` prefix.** Frontend updated in lockstep.
5. **Introduce OpenAPI-driven codegen** (see §2.2.1 below). Load-bearing step: everything after assumes the API contract is frozen in a spec file, not in hand-written Java.
6. **Add Testcontainers MySQL integration test slice.** Gives us a safety net for the rewrite.
7. **Fix the encryption fail-open** (#19) and the CORS wildcard (#18). Security regressions during migration would be invisible otherwise.

#### 2.2.1 OpenAPI codegen integration

Goal: a single `openapi.yaml` is the source of truth; backend controller interfaces + DTOs and frontend client + types are both generated from it during build.

**Spec authoring.** The repo already has an `openapi/` directory — use it. Either hand-author `openapi.yaml` against the current `/api/v1/` surface, or bootstrap once from `springdoc-openapi`'s runtime output, then commit and treat the spec as authoritative going forward. Hand-authoring is preferable: it forces a contract review and surfaces inconsistencies (snake_case vs. camelCase, optional vs. required, response envelopes) that have accumulated.

**Backend (`openapi-generator-gradle-plugin`, `spring` generator):**
- `interfaceOnly=true`, `useTags=true`, `useSpringBoot3=true`, `useJakartaEe=true`.
- `delegatePattern=false` — controllers `implements Api` directly; existing controller classes shrink to method bodies.
- `modelPackage=cz.forgottenempire.servermanager.api.model`, `apiPackage=cz.forgottenempire.servermanager.api`.
- Generated sources go to `build/generated/openapi/` and are added to the main source set — **never** committed.
- Bean Validation annotations flow through from the spec automatically.

**Frontend:** `openapi-generator` `typescript-fetch` (or `typescript-axios`, matching whatever the frontend currently uses) writing into `frontend/src/generated/api/`, wired into the frontend's build/dev scripts. Same spec file, same build.

**Migration sequence inside this step:**
1. Author/extract the spec covering all current endpoints. CI job: `openapi-generator validate`.
2. Generate API interfaces; have existing `@RestController` classes implement them. Compiler now enforces that controllers match the spec.
3. Replace hand-written request/response DTOs with generated models, one controller at a time. Adjust MapStruct mappers (or just replace them with extension-friendly hand-written mappers — see §2.4 step 9) to target generated types. Hand-written DTO classes get deleted as they're displaced.
4. Switch the frontend to the generated client. Delete hand-written `fetch`/`axios` API wrappers.
5. Add a CI check: generated sources must compile and the spec must be backward-compatible vs. `master` (via `openapi-diff` or similar) — breaking changes require an explicit label.

**Why this belongs in the foundation, not after Kotlin.** Hand-written DTOs are roughly a third of what the Kotlin migration would otherwise have to convert. Generating them now means those files never need a Kotlin rewrite — when step 9 of the migration runs, we just flip the generator's `language` from `spring` to `kotlin-spring` and rebuild. The REST contract stays byte-identical across the rewrite, which is the single biggest de-risk available.

### 2.3 Build setup

`backend/build.gradle` → `backend/build.gradle.kts`:

- Apply `org.jetbrains.kotlin.jvm`, `kotlin.plugin.spring`, `kotlin.plugin.jpa`, `kotlin.plugin.allopen`.
- `allopen` for `@Entity`, `@MappedSuperclass`, `@Embeddable` (JPA needs non-final classes).
- `kotlin.jvmToolchain(25)`.
- Keep Lombok during the transition (mixed sources compile in two passes; configure `compileJava` to run before `compileKotlin`).
- Add `kotlinx-coroutines-core`, `kotlinx-coroutines-reactor` (for `@Async` interop).
- Replace `MapStruct` annotation processor with the Kotlin-compatible one (`mapstruct-processor` via `kapt`), or migrate mappers to hand-written extension functions (recommended — mappers are a thin layer, especially once DTOs are generated from OpenAPI).
- **OpenAPI generator:** flip `language` from `spring` (Java) to `kotlin-spring`; set `useSpringBoot3=true`, `interfaceOnly=true`, `serializationLibrary=jackson`, `enumPropertyNaming=UPPERCASE`. Generated output moves from `build/generated/openapi/*.java` to `*.kt` — no spec changes, no contract changes.

### 2.4 Migration order (module by module)

Convert leaves first, root last. Each step is one PR, mergeable independently. Java and Kotlin coexist throughout.

| Step | Package                                                   | Why this order                                                                                                                                                                                            | Notes                                                 |
|------|-----------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| 1    | `common/`                                                 | Pure utilities, no Spring entanglement.                                                                                                                                                                   | Drop Lombok here first. Validate the build toolchain. |
| 2    | `system/`                                                 | Small, self-contained, exercises Guava cache → `by lazy` pattern.                                                                                                                                         |                                                       |
| 3    | `steamauth/`, `security/`                                 | Replace `AttributeEncryptor` with a Kotlin object using `ThreadLocal<Cipher>`; fix #1, #19 here.                                                                                                          |                                                       |
| 4    | `workshop/metadata/`                                      | Lots of nullable JSON parsing → biggest null-safety win.                                                                                                                                                  | Fixes #17.                                            |
| 5    | `workshop/` (rest)                                        | Split `WorkshopModsFacade` while rewriting (#12).                                                                                                                                                         |                                                       |
| 6    | `steamcmd/`, `installation/`                              | Convert `SteamCmdExecutor`; replace single-thread `ThreadPoolExecutor` with a coroutine-backed dispatcher.                                                                                                |                                                       |
| 7    | `scenario/`, `modpreset/`                                 | Mostly CRUD; quick wins.                                                                                                                                                                                  |                                                       |
| 8    | `additionalserver/`                                       | Coroutine port for the 30s sleep (#2); fixes the cron race (#4).                                                                                                                                          |                                                       |
| 9    | `serverinstance/` (mappers + remaining hand-written DTOs) | Flip OpenAPI generator to `kotlin-spring` — generated DTOs become Kotlin in one toolchain change. Rewrite remaining hand-written mappers as Kotlin extension functions; drop MapStruct here if practical. |                                                       |
| 10   | `serverinstance/entities/`                                | **Convert `Server` hierarchy to a `sealed class`.** Coordinated PR — touches every `instanceof` site.                                                                                                     | Fixes #13, #14.                                       |
| 11   | `serverinstance/process/`                                 | Replace in-memory `ServerProcessRepository` with a hybrid (DB-backed registry + `ProcessHandle.of(pid)` on startup).                                                                                      | Fixes #6, #16.                                        |
| 12   | Controllers + `Application.java`                          | Last to convert; smallest payoff, highest blast radius.                                                                                                                                                   |                                                       |

### 2.5 Patterns to apply during conversion

- **Entities:** `open class` (via `allopen`) with `var` for mutable JPA fields, `val` for immutable ones. Don't use `data class` for `@Entity` (equals/hashCode on IDs only).
- **DTOs:** `data class` with non-null defaults; nullable only where the API contract permits.
- **Mappers:** extension functions (`fun Arma3Server.toDto(): Arma3ServerDto`) instead of MapStruct interfaces. Compile-time checked, no codegen.
- **Cron jobs:** keep `@Scheduled` annotation (Spring handles it) but make the body `runBlocking { ... }` calling `suspend` functions. Drop the explicit `Thread.sleep`.
- **Validation:** Bean Validation annotations still work on Kotlin properties. For sealed-class state machines, prefer constructor-enforced invariants.
- **Exceptions:** keep the existing hierarchy; just convert the classes.

### 2.6 Risks & mitigations

| Risk                                                                                                                                                                | Mitigation                                                                                                                                                                                                                         |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Lombok + Kotlin in the same module: Kotlin can't see Lombok-generated methods.                                                                                      | Compile order: `delombok` Java sources first, or convert Lombok-heavy classes early so Kotlin code only consumes Kotlin classes.                                                                                                   |
| MapStruct + Kotlin requires `kapt`, which is slower and being phased out for `ksp`.                                                                                 | Migrate mappers to hand-written extension functions during step 9; remove MapStruct entirely.                                                                                                                                      |
| JPA + Kotlin nullability mismatches on legacy nullable columns.                                                                                                     | Audit each entity field against the schema before converting; use `?` for nullable, non-null + Flyway `NOT NULL` migration where appropriate.                                                                                      |
| Sealed-class conversion (#13/#14) touches many files at once.                                                                                                       | Do it last (step 10), gated by the full test suite + Testcontainers integration tests added in §2.2.                                                                                                                               |
| `@Configurable` removal changes runtime behavior.                                                                                                                   | Land §2.2 PR #1 *before* any Kotlin work; verify with integration tests on master.                                                                                                                                                 |
| Hand-authoring the OpenAPI spec exposes contract inconsistencies (camelCase vs. snake_case, optional fields, error envelopes) that the frontend silently relied on. | Bootstrap from `springdoc-openapi` runtime output first, diff against intended contract, then normalize in a dedicated PR *before* generation is wired into the build. Contract bug fixes ship visibly, not buried in the rewrite. |
| `kotlin-spring` generator emits slightly different code than `spring` (e.g. nullable handling, `data class` vs. POJO).                                              | Land the language flip on `master` *before* step 9, in isolation — frontend stays on its existing TS client (unchanged spec), only backend code shape differs. Caught by integration tests.                                        |

### 2.7 Done criteria

- 0 `.java` files under `backend/src/main/`.
- All §1 🔴 findings closed; ≥80% of 🟡 findings closed.
- REST contract unchanged: existing frontend works without modification.
- Test coverage ≥ pre-migration baseline (measured by JaCoCo before step 1).
- Startup time and steady-state memory within ±10% of baseline.

### 2.8 Rough effort estimate

Assuming one engineer, familiar with the codebase:

- Foundation work (§2.2), excl. OpenAPI: **1–2 weeks**.
- OpenAPI codegen integration (§2.2.1): **1.5–2.5 weeks** — spec authoring + per-controller DTO replacement + frontend client cutover is the long pole.
- Migration steps 1–9: **3–4 weeks** (each step 1–3 days; step 9 lighter now since DTOs regenerate via toolchain flip).
- Step 10 (sealed-class refactor): **1 week** — riskiest single step.
- Steps 11–12: **1 week**.
- Buffer + cleanup: **1 week**.

**Total: ~9–11 weeks of focused work**, parallelizable with feature work since each step is an independent PR. OpenAPI adds ~2 weeks up front but removes work from step 9 and pays for itself in contract stability across the rewrite.
