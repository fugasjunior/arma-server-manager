/# Advanced Server Configuration

Plan for an "advanced editing mode" that lets power users edit a server's raw config
file (text/JSON) instead of the predefined frontend form.

> This plan is self-contained. Implementers do not have the context of the planning
> session — read the **Background** and **Investigation findings** sections before
> touching code.

---

## 1. Motivation

Predefined forms are good for beginners but cannot express config parameters the UI
doesn't know about. Advanced users want to write the config file by hand, exactly as
they would when managing the server manually.

Solution: a per-config-file **advanced mode** toggle. When enabled, the form for that
file is replaced by a text editor holding the raw config. On generation, the stored
raw text is written verbatim instead of rendering the FreeMarker template.

**Core constraint (drove the redesign):** advanced mode must be usable while
**creating a new server**, which has no ID yet. Therefore overrides cannot live behind
separate per-file endpoints keyed by server id. Instead, **config overrides travel
inside the normal `ServerDto`** and are persisted by the same single "Save" action that
creates/updates the server. No per-file save buttons.

---

## 2. Locked decisions (from requirements + planning interview)

| Topic | Decision |
|-------|----------|
| Granularity | Per config file, not per server. Each file toggles independently. |
| Transport | Overrides are a field on `ServerDto` (`configOverrides: [{configKey, content}]`), sent and saved with the normal server create/update. **One Save button** for the whole server, including all advanced configs. No per-file save/enable/revert endpoints. |
| New servers | Advanced mode works at create time (no id) because the override content rides in the create payload. Seeding the initial editor content uses a stateless render endpoint (§5.2) that needs no persisted server. |
| Permission | New `ADVANCED_CONFIG_EDIT`. **Granted to Admin role only** in seed migration. Admins can grant to custom roles via existing RBAC UI. |
| Secrets collision | Raw-config view/edit requires **both** `ADVANCED_CONFIG_EDIT` **and** `SERVER_SECRETS_VIEW`. Raw config contains inline passwords, so the existing secrets gate applies on top of the advanced-edit gate. Override content is only included in server-GET responses, and only accepted on save, when the caller holds both authorities. |
| Reversibility | **Revert allowed.** Toggling advanced off in the UI drops that override from the form state; on Save the backend sees the override absent and deletes its row, so generation falls back to the template using the entity fields (which were never deleted). No parsing of raw text needed. Requires a clear warning before discarding raw edits. |
| Form fields when advanced | **Hide config-mapped fields in the UI only.** Fields that exclusively feed the advanced file are hidden while that file is advanced. **No backend freeze / no DTO rejection** — the backend accepts whatever the DTO carries; the override simply wins at generation time. Fields with an app-level role beyond that file stay visible/editable (see per-type tables in §6). |
| Seed default on enable | Stateless render endpoint (§5.2): if an id is supplied and the on-disk file exists, return its current contents; otherwise render the FreeMarker template from the submitted draft DTO (covers the new-server case). |
| Validation | Reforger: reject save if not parseable JSON (Jackson `readTree`). Arma3/DayZ: no validation (custom `.cfg` syntax, no cheap validator). Validation runs in the service during the normal server save. |
| Reforger mods | `game.mods` lives inside the Reforger JSON. When Reforger JSON is advanced, the separate mod-management UI for that server must be disabled (mods are edited in the JSON). |
| Phase order | DayZ → Reforger → Arma3. Backend override mechanism built in phase 1. |
| JSON/text editor | `@uiw/react-codemirror` for **all** config types, added in a **later phase**. JSON lang (`@codemirror/lang-json`) for Reforger; C-like (`@codemirror/lang-cpp`) approximation for `.cfg`/profile. Early phases use a plain `<textarea>`. Implementer must verify package availability + React 19 compat at build time. |

---

## 3. Background — current config generation (investigation findings)

### 3.1 Generation flow

1. `ServerController.createServer(ServerDto)` → `ServerMapper.mapServerDtoToEntity()` →
   `ServerInstanceService.createServer(Server)`
   (`backend/.../serverinstance/ServerController.java:77`, `ServerInstanceService.java:52`).
2. `ServerInstanceService.createServer()` saves the entity, then
   `getConfigFiles(ctx).forEach(ServerConfig::generate)` (`ServerInstanceService.java:52-57`).
3. `updateServer()` guards against a running server, then calls `createServer`
   (`ServerInstanceService.java:60-64`).
4. `ServerProcess.start(Server)` calls `getConfigFiles(ctx).forEach(ServerConfig::generateIfNecessary)`
   before launch (`process/ServerProcess.java:39-47`). `generateIfNecessary()` only writes
   if the file is missing.

**Implication for this feature:** because overrides ride in the `ServerDto` and are saved
through the existing `createServer`/`updateServer` path, the normal `generate()` call writes
the raw text to disk automatically. No separate force-write-to-disk logic is needed.

### 3.2 `ServerConfig` (`serverinstance/ServerConfig.java`)

Wraps `(File configFile, String templateName, Object templateModel, FreeMarkerConfigurer)`.
`generate()` deletes the old file and renders the template. This is the seam the override
hooks into (§4.2).

### 3.3 Config files per server type

`getConfigFiles(ctx)` is defined per entity. Each `ServerConfig` = template + output path + model entity.

| Server type | Files | Template → output | Model entity |
|-------------|-------|-------------------|--------------|
| **Arma3** | 3 | `serverConfigArma3.ftl` → `ARMA3_{id}.cfg` | `Arma3Server` |
| | | `arma3ServerProfile.ftl` → `…/ARMA3_{id}.Arma3Profile` | `Arma3DifficultySettings` |
| | | `arma3NetworkSettings.ftl` → `ARMA3_{id}_network.cfg` (only if `networkSettings != null`) | `Arma3NetworkSettings` |
| **DayZ** | 1 | `serverConfigDayZ.ftl` → `DAYZ_{id}.cfg` | `DayZServer` |
| **DayZ Exp** | 1 | same template as DayZ (`ServerType` enum, shares `DayZServer` class) | `DayZServer` |
| **Reforger** | 1 | `serverConfigReforger.ftl` → `REFORGER_{id}.json` | `ReforgerServer` |

Template constants: `common/Constants.java:28-36` (`SERVER_CONFIG_TEMPLATES` map,
`ARMA3_PROFILE_TEMPLATE`, `ARMA3_NETWORK_SETTINGS`).

> Note: there is no `ReforgerExpServer` entity. `DAYZ_EXP` is a `ServerType` value sharing
> `DayZServer`. The plan covers DayZ, Reforger, Arma3; DayZ Exp comes free with DayZ.

### 3.4 Launch parameters vs config content

Launch params built in each entity's `getLaunchParameters(ctx)`:
`Arma3Server.java:78-94`, `DayZServer.java:62-72`, `ReforgerServer.java:36-45`.
Command line assembled in `process/ServerProcess.java:39-59` →
`process/ServerProcessCreator.java:12-24`.

**Key:** for Arma3/DayZ, `port`, `mods`, `customLaunchParameters` are launch params, **not**
in the config file — so they remain editable in advanced mode. For Reforger, `port`/`queryPort`
**are** in the JSON, and so are mods.

### 3.5 Existing free-form escape hatches (do not confuse with this feature)

`Arma3Server.additionalOptions` and `DayZServer.additionalOptions`
(`@Column(columnDefinition="LONGTEXT")`) are appended to the rendered config
(`${additionalOptions!}` at the end of the `.ftl`). This is an *append* mechanism only;
this feature provides a *full* override. When a file is in advanced mode, its
`additionalOptions` field is part of the raw text and its form field is hidden (see §6).

### 3.6 RBAC model (investigation findings)

- `security/permission/PermissionCode.java` — permission code constants (e.g. `SERVER_SECRETS_VIEW` at line 9). 19 codes today.
- `security/permission/Permission.java` — JPA entity, `String code` PK.
- `security/role/Role.java` — `@ManyToMany Permission`, `built_in` flag.
- `security/user/User.java:46-52` — `getPermissionCodes()` aggregates role permissions.
- `security/SecurityBootstrap.java:42-70` — `seedAdminUser()`; initial user always gets Admin role.
- Enforcement: `@EnableMethodSecurity` (`security/WebSecurityConfig.java:35`) + `@PreAuthorize("hasAuthority('CODE')")` on controllers (examples: `ServerController.java:56-72`, `security/user/UserController.java`).
- `security/user/UserController.java:79-84` — `getCurrentUser()` returns `CurrentUserDto` with `permissions` list.
- `serverinstance/ServerSecretsMasker.java:14-31` — `maskIfUnauthorized()` nullifies `password`/`adminPassword` in DTOs when caller lacks `SERVER_SECRETS_VIEW`. (Pattern to mirror for stripping override content from responses, §4.4.)
- Latest migration: `db/migration/V1_3_6__USER_ROLE_PERMISSIONS.sql`. Seeds 19 permissions + 3 built-in roles (Admin = all, Operator = 9, Viewer = 6). Admin gets all permissions via `INSERT … SELECT r.id, p.code FROM roles r, permissions p WHERE r.name='Admin'`.

### 3.7 Frontend permission plumbing

- `frontend/src/store/auth-context.tsx` — `CurrentUser.permissions: string[]`, `hasPermission()`.
- `frontend/src/hooks/usePermission.ts` — `usePermission(code)` hook.
- `frontend/src/components/auth/PermissionGuard.tsx` — gates children by permission.
- Example existing gate: `components/servers/EditArma3ServerSettingsForm.tsx:59-61` wraps password fields with `SERVER_SECRETS_VIEW`.

### 3.8 API contract

OpenAPI-first. **Never hand-write DTOs/controller interfaces.** Edit
`openapi/openapi.yaml`, regenerate (`cd frontend && npm run generate`; backend regenerates
on build). Backend controllers implement generated `api/` interfaces; frontend uses
`typescript-axios` client via instances in `frontend/src/api/client.ts`.
Server-type-specific frontend types live in `frontend/src/api/serverModels.ts` (generator
collapses `allOf`+discriminator subtypes — see project `CLAUDE.md`).

---

## 4. Architecture

### 4.1 Storage — `ServerConfigOverride` entity

One row per overridden config file, owned by a server.

```
ServerConfigOverride (new table: server_config_override)
  id        Long      @Id @GeneratedValue
  server    Server    @ManyToOne   (FK server_id)   -- or store serverId Long directly
  configKey ConfigFileKey  (enum, stored as String)
  content   String    @Column(columnDefinition = "LONGTEXT")
  -- unique constraint (server_id, config_key)
```

Written in **Kotlin** (new file → `src/main/kotlin`, per language policy). Normal class with
manual `equals`/`hashCode` (JPA entity — never a Kotlin data class).

```
ConfigFileKey (enum)
  ARMA3_SERVER_CFG
  ARMA3_PROFILE
  ARMA3_NETWORK_CFG
  DAYZ_SERVER_CFG
  REFORGER_JSON
```

Repository: `ServerConfigOverrideRepository` —
`findByServerIdAndConfigKey(...)`, `findByServerId(...)`, `deleteByServerIdAndConfigKey(...)`.

Existence of a row = that file is in advanced mode. No separate boolean needed.

If the JPA cascade from `Server` is convenient, mapping the overrides as a child collection on
`Server` (cascade + orphan removal) lets the create/update sync (§4.3) fall out of normal entity
persistence. Implementer's choice; a standalone repository is equally fine.

**Key→type validation (A5).** Each `ConfigFileKey` is valid only for one `ServerType` (e.g.
`REFORGER_JSON` only for Reforger; the three `ARMA3_*` keys only for Arma3). Give this mapping
an explicit home — a `Set<ConfigFileKey> validKeys(ServerType)` (or `ServerType` field on the
enum) on `ConfigFileKey` — and have the save path reject mismatched `(serverType, configKey)`
requests with 400. Don't leave the mapping implicit in controller code.

### 4.2 Generation hook — `ServerConfig` learns about overrides

The cleanest seam is `ServerConfig` itself. Add an optional raw-override:

- Add a nullable `String rawOverride` (or factory variant) to `ServerConfig`.
- In `writeNewConfig()`: if `rawOverride != null` → write it verbatim; else render the template
  as today.

Where `getConfigFiles(ctx)` builds each `ServerConfig`, look up the override by
`(serverId, configKey)` and pass `content` as `rawOverride` when present.

> Implementation note: `getConfigFiles(ctx)` lives on the entities, which cannot inject the
> repository. The `ServerLaunchContext ctx` is already threaded into `getConfigFiles`/
> `getLaunchParameters`. **Preferred approach:** carry an override lookup
> (`Map<ConfigFileKey,String>` or a functional `(ConfigFileKey)->String?`) on `ctx`, populated
> by the caller (`ServerInstanceService` / `ServerProcess`) from the repository, so entities
> stay persistence-free. Confirm the exact shape of `ServerLaunchContext` and the
> `getConfigFiles` signature when implementing phase 1; pick the lookup-on-ctx route unless
> the existing structure makes a different seam obviously cleaner.

This keeps the change tiny and centralized: every existing generation path
(`createServer`, `updateServer`, `ServerProcess.start`) automatically honors overrides with no
change to its own logic, and the raw text reaches disk through the normal `generate()` call.

### 4.3 Save-time override sync

On `createServer`/`updateServer`, the service receives `ServerDto.configOverrides` (a list of
`{configKey, content}`). It **syncs** the override rows for that server to match the list:

1. Validate each `(serverType, configKey)` (§4.1/A5) → 400 on mismatch.
2. Validate content where required (Reforger JSON, §5.3) → 400 on parse error.
3. Upsert a row for each entry; **delete** any existing override row whose key is **not** in the
   list. (Deletion is how "revert" happens — the UI drops the override from the payload.)
4. Persist the entity, then `generate()` (existing flow) writes each file, honoring the freshly
   synced overrides via §4.2.

**Permission check at the gate:** the normal server create/update endpoints are not gated on
`ADVANCED_CONFIG_EDIT`. If the incoming `configOverrides` would **add, change, or remove** an
override, the service must require the caller to hold **both** `ADVANCED_CONFIG_EDIT` and
`SERVER_SECRETS_VIEW`, else 403. A request that leaves overrides untouched needs no extra
authority. (Simplest robust check: compare the submitted override set against the persisted one;
if they differ, enforce the authorities.)

### 4.4 Reading overrides back — existence vs content

The server-GET response (`GET /servers/{id}`) includes `configOverrides`. Split two concerns:

- **Existence** of an override for a `configKey` is **not secret** and is **always returned to
  everyone** (the `configKey` + an `advanced: true` flag). Every user must be able to tell a file
  is in advanced mode.
- **Content** (raw text) holds inline secrets and is **content-masked** exactly like
  `ServerSecretsMasker`: included only for callers holding **both** `ADVANCED_CONFIG_EDIT` and
  `SERVER_SECRETS_VIEW`; nulled/omitted otherwise.

So `ConfigOverrideDto` carries `{ configKey, advanced: true, content?: string }` where `content`
is present only for authorized callers.

**Why this matters (the no-permission case).** If existence were hidden, a user without the
authorities would see a normal editable form for an advanced file, edit it, Save, and have their
values silently ignored at generation (the override wins). To prevent that:

- The frontend hides/disables the config-mapped fields for that file whenever the **existence
  flag** is set — regardless of whether content is present (§4.6).
- A read-only notice is shown in that section: *"This config is in advanced mode and is managed
  by an authorized user. These settings do not apply."* No toggle, no editor, no content for
  unauthorized users.
- App-level / launch-param fields (`name`, port, etc.) stay editable as normal — those are not
  governed by the override.

### 4.5 Reversibility

Revert needs no parsing. The entity's config-mapped fields are never deleted when advanced mode
is enabled — they are merely hidden in the UI. To revert, the user toggles the file's advanced
mode off; the UI removes that override from the form state, and on Save the backend deletes the
row (§4.3 step 3). The next generation renders the template from the still-present entity fields.

Because there is **no freeze**, the entity fields may differ from what they were when advanced
mode was enabled (e.g. the form was edited elsewhere) — that's acceptable and simpler. Revert
restores the form to the **current** entity field values and **discards the raw edits**. A clear
warning is mandatory ("Reverting discards your advanced config and restores the form fields"),
plus a pre-revert escape hatch (copy raw to clipboard — §5.4).

### 4.6 Field-hiding model (frontend only)

Each entity field is classified per the per-type tables in §6 as either **config-mapped**
(hidden when its file is advanced) or **app-level / launch-param** (always editable). The
**frontend** decides what to render based on which override **existence flags** are set for the
server (from the server-GET response, §4.4) — *not* on whether content is present, so the hide
applies to unauthorized viewers too. **The backend does not reject or freeze writes to
hidden fields** — it accepts the DTO as-is; the override wins at generation regardless of the
entity field values.

Two render states per advanced file:
- **Authorized** (both authorities): config-mapped fields hidden, raw editor shown with content,
  toggle present.
- **Unauthorized**: config-mapped fields hidden, no editor/toggle, read-only "in advanced mode"
  notice instead (§4.4).

**Launch-param overlap is the user's responsibility (A1).** Fields that double as launch
parameters (e.g. Reforger `port`/`queryPort`, DayZ `steamQueryPort`/`instanceId`) stay as
separate, editable UI fields because the launch command reads them from the entity, not the raw
config. When such a field also appears inside an advanced config file, the user must keep the
raw value consistent with the UI field — the app does not reconcile them. Document this in the
advanced-mode notice.

---

## 5. API design (`openapi/openapi.yaml`)

### 5.1 `ServerDto` change

Add an optional field to the server request/response schema:

```yaml
configOverrides:
  type: array
  items:
    $ref: '#/components/schemas/ConfigOverrideDto'   # { configKey: enum, advanced: bool, content?: string }
```

- On **create/update** (request): the list of active overrides with `content`; backend syncs
  rows (§4.3). (`advanced` is implied true for any entry present.)
- On **GET** (response): one entry per active override for every caller, with the
  `advanced: true` existence flag always set; `content` present only for callers holding both
  authorities (§4.4).

`configKey` is the `ConfigFileKey` enum; constrained to the keys valid for the server's type
(backend rejects mismatches, §4.1).

### 5.2 Seed/render endpoint

One stateless helper endpoint is still needed: when a user flips a toggle on, the editor must be
pre-filled. This works for **new servers** (no id) because it renders from the submitted draft.

```
POST /servers/config-overrides/seed
  body: { configKey, server: ServerDto }   # the current form draft, possibly without an id
  → { configKey, content }
  Gate: @PreAuthorize("hasAuthority('ADVANCED_CONFIG_EDIT') and hasAuthority('SERVER_SECRETS_VIEW')")
```

Behavior:
1. If `server.id` is present and the on-disk config file for `configKey` exists, return its
   contents (captures current generated config incl. on-disk edits).
2. Otherwise map the draft DTO to a transient entity (`ServerMapper.mapServerDtoToEntity`) and
   render the FreeMarker template for `configKey` to a string (reuse the render-to-string helper
   from §4.2). No persistence.

Seed includes real secrets (inline passwords) — consistent with the secrets gate.

### 5.3 Validation

Validation lives in the service during the normal server save (and may also run in the seed
endpoint for early feedback). Reforger `REFORGER_JSON` content is parsed with Jackson
`ObjectMapper.readTree`; on failure return **400** with the parse **line/column** from
`JsonProcessingException.getLocation()` (U6) so the user can find the fault — not a bare
"invalid JSON". Map via `common/RestExceptionHandler`. Arma3/DayZ: no validation.

### 5.4 Frontend consumption

- New types generated from the spec; server API called via `frontend/src/api/client.ts`.
- The edit/create form holds `configOverrides` in its form state alongside the rest of the
  server fields. On load (edit), seed from the server-GET response; for create, start empty.
- **Toggle placement (U1).** The control lives **inside each form section**, next to that
  section's header — one toggle per config file, NOT a single global "Advanced" switch. Arma3
  shows three independent toggles (server.cfg, difficulty profile, network) on their respective
  sections. Label each by what it edits, e.g. **"Edit raw server config (server.cfg)"**,
  **"Edit raw difficulty profile"**, **"Edit raw network config"**, **"Edit raw config (JSON)"**
  for Reforger. Avoid the bare word "Advanced" as the only label.
- The **advanced toggle + editor** are gated by `PermissionGuard` requiring **both**
  `ADVANCED_CONFIG_EDIT` and `SERVER_SECRETS_VIEW`; hidden entirely from users lacking either.
- The **"in advanced mode" notice + config-mapped field hiding** are driven by the existence
  flag and shown to **all** users (§4.4) — an unauthorized user sees the notice instead of the
  toggle, never an ignored editable form.
- **Enabling a toggle:** confirmation modal warning the form section will be replaced by raw
  editing; on confirm, call the **seed** endpoint with the current draft, store the returned
  content as an override in form state, hide the config-mapped fields, show the editor.
- **Editing:** edits update the override content in form state. Nothing is saved until the user
  clicks the single server **Save** button.
- **Saving:** the normal server create/update call carries `configOverrides`. JSON parse errors
  (Reforger) come back on this save with line/column; surface them on the section.
- **Reverting (toggle off):** confirmation modal (warning per §4.5 — discards raw edits, restores
  the form fields). The modal includes a **"Copy raw config to clipboard"** button (U4) so the
  user can save their work first. On confirm, remove the override from form state and re-show the
  form fields; the deletion is persisted on the next Save.

---

## 6. Per-type field classification

**Config-mapped** = hidden when that file is advanced (frontend only — no backend freeze).
**App-level** = always editable. Source: templates in `backend/src/main/resources/templates/` +
entity fields (§3).

### 6.1 DayZ (`serverConfigDayZ.ftl`, key `DAYZ_SERVER_CFG`)

| Config-mapped (hide when advanced) | App-level (keep) |
|---|---|
| `password`, `adminPassword`, `maxPlayers`, `clientFilePatching`, `forceSameBuild`, `vonEnabled`, `persistent`, `timeAcceleration`, `nightTimeAcceleration`, `thirdPersonViewEnabled`, `crosshairEnabled`, `respawnTime`, `additionalOptions` | `name`¹ (server display name), `port`, `queryPort` (`steamQueryPort` in cfg but driven by launch/identity²), `id`/`instanceId`², mods (launch params), `customLaunchParameters`, restart settings, `description` |

¹ **`name` is a non-config field (P2 — decided).** It is the app-level server display name in
the UI and is **not bound to the config `hostname`**. Keep it always visible and editable (as a
"Server name" field) even while the cfg is advanced; the raw text owns `hostname` independently.
² `queryPort`/`instanceId` appear in the cfg **and** are derived from app-level data (port,
server id). Treat as app-level: keep editable; the user is responsible for keeping the raw
`steamQueryPort`/`instanceId` consistent. Confirm exact behavior during the DayZ phase by reading
`DayZServer.getLaunchParameters`.

### 6.2 Reforger (`serverConfigReforger.ftl`, key `REFORGER_JSON`)

Config-mapped (hide when advanced): `password`, `adminPassword`, `scenarioId`, `maxPlayers`,
`thirdPersonViewEnabled`, `battlEye`, **mods (`game.mods`)**. When advanced:

- Hide the config-mapped fields above.
- **Disable the mod-management UI** for this server (mods live in the JSON `game.mods` list —
  Reforger uses a JSON mod list, not the workshop pipeline). Surface a notice explaining why.
- Keep editable: `name`¹ (server display name, non-config), `description`, restart settings,
  `customLaunchParameters`, **`port` (bind/public) and `queryPort` (`a2s.port`)**. Per A1 (§4.6)
  these are launch params read from the entity, so they stay UI fields; the user must keep the
  JSON `bindPort`/`a2s.port` values consistent with them. Notice must state this.

### 6.3 Arma3 — three independent toggles

**`ARMA3_SERVER_CFG`** (`serverConfigArma3.ftl`):

| Config-mapped (hide) | App-level (keep) |
|---|---|
| `password`, `adminPassword`, `maxPlayers`, `verifySignatures`, `clientFilePatching`, `vonEnabled`, `persistent`, `battlEye`, `additionalOptions` | `name`¹ (server display name), `port`, `queryPort`, mods + DLCs (launch params), local mods, `serverFilePatching`, `targetHeadlessClientsCount`, `customLaunchParameters`, restart settings, `description` |

**`ARMA3_PROFILE`** (`arma3ServerProfile.ftl`, model `Arma3DifficultySettings`): when advanced,
hide the entire difficulty-settings form section. No app-level overlap.

**`ARMA3_NETWORK_CFG`** (`arma3NetworkSettings.ftl`, model `Arma3NetworkSettings`): when
advanced, hide the network-settings form section. Note this file is conditional today
(generated only if `networkSettings != null`); going advanced implies the file should always be
written. Ensure `getConfigFiles` includes the network `ServerConfig` whenever an
`ARMA3_NETWORK_CFG` override exists.

¹ Same `name` caveat as DayZ.

> The exact config-mapped vs app-level split per field must be re-verified against the entity's
> `getLaunchParameters` and template during each phase — the tables above are the intended
> rule, not a substitute for reading the code.

---

## 7. Phases

Each phase touches a minimal, reviewable set of files. Backend mechanism is phase 1.

### Phase 1 — Backend override mechanism + DayZ (pilot)

Establishes the full end-to-end path on the simplest server type (1 file, secrets inline, no
mod-in-config). Split into three reviewable PRs (P1):

#### Phase 1a — Core mechanism (no API, no UI)

1. New permission: add `ADVANCED_CONFIG_EDIT` to `PermissionCode`; migration
   `V1_3_7__ADVANCED_CONFIG_EDIT.sql` inserting the permission and granting it to Admin only
   (Admin already holds `SERVER_SECRETS_VIEW`).
2. `ConfigFileKey` enum (+ key→`ServerType` validation map, §4.1/A5) + `ServerConfigOverride`
   entity (Kotlin) + repository.
3. `ServerConfig` raw-override seam (§4.2) + override lookup threaded through
   `getConfigFiles`/`ctx` (§4.2 note). Render-to-string helper for seeding (§5.2).
4. Save-time override sync in `createServer`/`updateServer` (§4.3): upsert + delete-missing,
   key/type validation, permission-on-change check. Include overrides in the entity→DTO mapping
   for reads, masked when unauthorized (§4.4).
5. Tests: create server with override persists row + writes raw text to disk; update changes
   content; update without a key deletes that override (revert) → file regenerates from template;
   key/type mismatch rejected; override change without authority → 403; read returns the
   `advanced: true` existence flag to everyone but masks `content` when unauthorized.

#### Phase 1b — API surface

1. OpenAPI: `configOverrides` on `ServerDto` + `ConfigOverrideDto` schema + the `seed` endpoint
   (§5.1/§5.2); regenerate both sides.
2. Seed controller method implementing the generated interface, gated
   `@PreAuthorize("hasAuthority('ADVANCED_CONFIG_EDIT') and hasAuthority('SERVER_SECRETS_VIEW')")`.
3. Audit logging on override seed/save/delete (S2).
4. Tests: seed returns template render for a draft with no id; seed returns on-disk content for an
   existing server; permission enforcement (both authorities); JSON error path deferred to
   Reforger phase.

#### Phase 1c — DayZ frontend

1. Per-section Advanced toggle on the DayZ edit/create form (§5.4/U1 labels), `PermissionGuard`
   gated on both authorities. **Works on the new-server create form** (no id) via the seed
   endpoint.
2. Enable confirm modal; plain `<textarea>` editor bound to the override in form state; hide
   config-mapped fields (§6.1) when override present; keep `name`/port/queryPort editable
   (P2/A1). Revert confirm modal with **"Copy raw config to clipboard"** (U4). All persisted by
   the single server Save button.
3. Tests: frontend unit test for the toggle/guard + field hiding + override included in save
   payload.

**Verify (end of 1c):** create a **new** DayZ server with advanced enabled → seed renders from
the form draft → edit & Save → on-disk cfg equals saved text. Then edit the server → toggle off →
Save → cfg regenerates from form fields.

### Phase 2 — Reforger

1. Reuse the mechanism. Add `REFORGER_JSON` handling.
2. JSON-syntax validation on save (Jackson `readTree` → 400) with line/column from
   `JsonProcessingException.getLocation()` (U6); also surfaced from the seed endpoint.
3. Frontend: Advanced toggle on Reforger form; **disable mod-management UI** + notice when
   advanced; hide config-mapped fields (§6.2); keep port/queryPort editable (A1); plain
   `<textarea>` still; show JSON parse errors with line/col on Save.
4. Tests: invalid JSON rejected with location; mod UI disabled when advanced; generation writes
   raw JSON.

### Phase 3 — Arma3 (three sub-toggles)

Can be split into 3 sub-phases (server.cfg, profile/difficulty, network.cfg) if reviews get
large. Each adds one `ConfigFileKey` handling + one form section toggle.

1. `ARMA3_SERVER_CFG` toggle + field hiding (§6.3).
2. `ARMA3_PROFILE` toggle (difficulty section).
3. `ARMA3_NETWORK_CFG` toggle (network section; ensure file always written when override
   exists, §6.3).
4. Tests per file.

### Phase 4 — CodeMirror editor (all types)

Replace the plain `<textarea>` with `@uiw/react-codemirror` everywhere.

1. Add deps: `@uiw/react-codemirror`, `@codemirror/lang-json`, `@codemirror/lang-cpp`
   (verify availability + React 19 compatibility first; fall back to no-language `basicSetup` if
   `lang-cpp` is unsuitable for `.cfg`).
2. Shared editor component: JSON mode for `REFORGER_JSON`, C-like mode for `.cfg`/profile.
3. Swap in across all advanced sections.

---

## 8. Risks / notes for implementers

- **Secrets exposure:** raw config contains inline passwords. Override content is only included
  in responses, accepted on save, or rendered by the seed endpoint for callers holding **both**
  `ADVANCED_CONFIG_EDIT` and `SERVER_SECRETS_VIEW` (§2, §4.3, §4.4). Document the secrets
  implication in the `ADVANCED_CONFIG_EDIT` permission description.
- **Permission on a non-gated endpoint:** the main server create/update endpoint is not gated on
  `ADVANCED_CONFIG_EDIT`. The service must enforce both authorities **only when the override set
  changes** (§4.3), so ordinary edits by non-advanced users still work.
- **Audit logging (S2):** log seed / save / delete of config overrides (who, server, configKey,
  when). Secrets-exposure surface; do not leave unaudited.
- **No backend freeze:** hidden fields are a UI concern only; the backend accepts any DTO values
  and the override wins at generation (§4.6). Do not add DTO rejection for hidden fields.
- **No extra running-server check:** override save/revert go through the normal
  `createServer`/`updateServer` path; do **not** add a separate running-server guard for config
  saves. The existing `updateServer` behavior is unchanged.
- **`name`** (P2): non-config app display field, never bound to `hostname`, always editable.
- **`queryPort`/`instanceId`/Reforger `port` overlap** (A1, §4.6): launch params read from the
  entity, so they stay editable UI fields; user keeps the raw config consistent. Re-verify
  against `getLaunchParameters` per phase.
- **Network.cfg conditional generation** (§6.3): must become unconditional when an
  `ARMA3_NETWORK_CFG` override exists.
- **OpenAPI-first:** all DTOs/endpoints via `openapi/openapi.yaml` + regenerate. Frontend
  server-type types via `frontend/src/api/serverModels.ts`.
- **Kotlin for new backend files;** JPA entity = normal class, manual `equals`/`hashCode`.
- **Wiki reference:** `docs/wiki-db/` (offline BI wiki) for any config-field questions. Never
  assert config behavior from memory.

---

## 9. File touch-list (anticipated)

Backend (phase 1):
- `openapi/openapi.yaml` (`configOverrides` on `ServerDto` + `ConfigOverrideDto` + seed endpoint)
- `backend/.../security/permission/PermissionCode.java` (+ `ADVANCED_CONFIG_EDIT`)
- `backend/src/main/resources/db/migration/V1_3_7__ADVANCED_CONFIG_EDIT.sql`
- `backend/.../serverinstance/ConfigFileKey.kt` (new)
- `backend/.../serverinstance/ServerConfigOverride.kt` (new entity)
- `backend/.../serverinstance/ServerConfigOverrideRepository.kt` (new)
- `backend/.../serverinstance/ServerConfig.java` (raw-override seam)
- `backend/.../serverinstance/ServerInstanceService.java` + `ServerMapper` (override sync on
  create/update §4.3, override → DTO mapping + masking §4.4) + `process/ServerProcess.java` +
  `ServerLaunchContext` (thread override lookup)
- `backend/.../serverinstance/ServerController.java` or a small seed controller (seed endpoint)
- entity `getConfigFiles` (DayZ in phase 1; Arma3/Reforger as their phases land)
- render-to-string helper for seeding (§5.2)

Frontend (phase 1):
- `frontend/src/api/...` (regenerated client + `serverModels.ts` if needed)
- DayZ edit/create form component + a shared `AdvancedConfigSection` component (toggle + editor +
  confirm modals + clipboard), `PermissionGuard`-gated; `configOverrides` wired into the form
  state and the single Save payload
- permission code constant (frontend side, if mirrored)

Later phases extend the above for Reforger / Arma3 / CodeMirror.
