# Plan: Per-instance Arma 3 servers (BI Method 5)

Status: **proposed** · Scope: **Arma 3 only** (DayZ/Reforger untouched) · Created: 2026-05-30

## Goal

Give each Arma 3 server instance its own isolated configs, keys, scenarios, and
profile, so users manage instances independently from the UI. Eliminates the
current shared-everything model where all Arma 3 instances share one
`mpmissions/`, one `keys/`, and one config directory.

## Approach — BI Method 5

Reference: <https://community.bistudio.com/wiki/Arma_3:_Dedicated_Server> →
*Multiple Server configuration* → *Method 5*.

Method 5 runs multiple servers from a **single shared install** using per-instance
startup flags — **no symlinked roots, no per-instance addon copies, no CWD
changes**:

| Flag | Purpose |
|------|---------|
| `-port=` | unique ports (already used) |
| `-name=` | profile name / instance label (already used) |
| `-profiles=<dir>` | profile dir (already used) |
| `-config=<file>` | server.cfg (already used) |
| `-cfg=<file>` | network/basic cfg (already used) |
| `-mpmissions=<dir>` | **NEW** — relocates missions dir per instance |
| `-keysFolder=<dir>` | **NEW** — relocates accepted-signatures dir per instance |
| `-mod= / -serverMod=` | mod selection (already used) |

Mods stay symlinked into the **shared** install dir exactly as today; `-mod=<name>`
per instance selects the subset. Only profiles/keys/mpmissions/config go
per-instance.

### Target on-disk layout

```
servers/ARMA3/
├── arma3server_x64                 # shared
├── addons/, keys/ (game bikeys)    # shared (game's own signatures)
├── @mod -> mod store               # shared mod symlinks (UNCHANGED)
└── profiles/
    └── ARMA3_<id>/                 # one per server instance, keyed by DB id
        ├── configs/
        │   ├── ARMA3_<id>.cfg
        │   └── ARMA3_<id>_network.cfg
        ├── keys/                   # active mods' bikeys + game bikeys, rebuilt at start
        ├── mpmissions/             # this instance's scenarios
        └── home/ARMA3_<id>/...     # profile (engine-created from -name)
```

### Locked decisions

- **Dir naming:** by DB id → `profiles/ARMA3_<id>` (rename-proof, collision-proof,
  matches existing `-name=ARMA3_<id>` convention).
- **Scenarios:** per-instance upload only (re-upload if shared across servers).
- **Migration:** auto-migrate on startup.
- **Keys phase 1:** auto-derive from active mods only; manual key management deferred to F5.

---

## Feature breakdown

**Build order:** **F0 → F1 → (F2 ∥ F3) → F4**, with **F6** alongside F1; **F5 later**.

**Release boundary — important.** The features are independently *developable and
mergeable* (separate PRs), but **not independently releasable to users** — except
F0. Once F1 makes the running server read from per-instance `mpmissions/`/`keys/`,
the legacy scenario-upload path (pre-F3) still writes to the old shared dir, so a
user uploading a scenario would see it silently ignored by the server. Therefore:

- **F0** — ships independently (pure path methods, no behavior change).
- **F1 + F2 + F3 + F4 + F6** — must ship **together as one user-facing release**.
  Develop/merge them incrementally (ideally behind a branch or a feature flag),
  but cut the release only when the full chain — server reads instance dirs (F1),
  keys derived (F2), scenario upload/list/delete is instance-scoped end-to-end
  (F3 backend + F4 frontend), and existing data migrated (F6) — is complete.
- **F5** — follows later as a separate, independently shippable increment on top.

### F0 — Instance path methods (foundation, no behavior change) ✅

Pure groundwork: introduce the per-instance path vocabulary in one place so F1–F3
have a stable API to build on. **No existing callers change**, nothing reads the
new paths yet — this ships green and is trivial to review.

**Touch point:** `common/PathsFactory.java` only.

**Why a dedicated step:** `PathsFactory` is the single chokepoint for all path
logic and every method is currently keyed by `ServerType`, never by instance id.
Adding the instance dimension is the smallest reviewable unit and de-risks the
larger F1/F2/F3 diffs (they become "swap path source", not "invent layout").

**Add (alongside existing type-scoped methods — keep both during transition):**

```java
// Base: servers/ARMA3/profiles/ARMA3_<id>
public Path getInstanceBasePath(long id) {
    return getServerPath(ServerType.ARMA3).resolve("profiles").resolve(instanceDirName(id));
}
public Path getInstanceProfilesPath(long id)   { return getInstanceBasePath(id); }              // -> -profiles
public Path getInstanceConfigsPath(long id)    { return getInstanceBasePath(id).resolve("configs"); }
public Path getInstanceKeysPath(long id)       { return getInstanceBasePath(id).resolve("keys"); }       // -> -keysFolder
public Path getInstanceMpmissionsPath(long id) { return getInstanceBasePath(id).resolve("mpmissions"); } // -> -mpmissions

private static String instanceDirName(long id) { return "ARMA3_" + id; } // matches -name convention
```

**Design notes / conventions to lock here:**
- **Container dirs are lowercase** — `profiles`, `mpmissions`, `keys`, `configs`.
  Only the per-instance dir is uppercase (`ARMA3_<id>`). The BI Method 5 wiki
  example uses CamelCase, but the flags accept any path, so we standardize on
  lowercase. Pick the casing once here; everything downstream inherits it. On
  Linux casing is load-bearing — be consistent.
- **Hardcode `ServerType.ARMA3`** in these methods (don't take a `ServerType`
  param). This is deliberately an Arma-3-only feature; keeping the type out of
  the signature prevents accidental reuse by DayZ/Reforger and documents intent.
- **`instanceDirName(id)`** is the single source of truth for the on-disk dir
  name. F1's `-name=ARMA3_<id>` and F6's migration both derive from the same
  `ARMA3_<id>` shape — centralize it so they can't drift.
- Return `Path` (not `File`/`String`); callers convert as needed. Don't call
  `.toAbsolutePath()` here — existing `getConfigFilePath` does it at the edge;
  keep that pattern at call sites.
- **Do not create directories in F0.** These are pure path builders. Dir
  creation is F1's `prepareLaunchEnvironment` hook. Keeps F0 side-effect-free
  and unit-testable without a filesystem.

**Tests:** unit test asserting each method yields the expected
`servers/ARMA3/profiles/ARMA3_<id>/...` suffix (construct `PathsFactory` with
known base paths, assert `.endsWith(...)`). No I/O.

**Out of scope for F0:** removing/repointing the old type-scoped methods
(`getScenariosBasePath`, `getServerKeysPath`, `getProfilesDirectoryPath`, etc.).
They stay live until their consumers migrate in F1/F2/F3, then get deleted when
the last caller is gone.

---

### F1 — Method 5 launch flags + per-instance config location ✅

**Touch points:** `entities/Arma3Server.java`, `process/ServerProcess.java` (dir prep).

1. `getLaunchParameters(ctx)` — add and repoint:
   - `-profiles="<instanceBase>"`               (= `.../profiles/ARMA3_<id>`)
   - `-config="<instanceConfigs>/ARMA3_<id>.cfg"`        (`configs/`)
   - `-cfg="<instanceConfigs>/ARMA3_<id>_network.cfg"` (`configs/`, only if networkSettings != null)
   - `-mpmissions="<instanceMpmissions>"`  ← new (`mpmissions/`)
   - `-keysFolder="<instanceKeys>"`        ← new (`keys/`)
   - Keep `-name=ARMA3_<id>` (drives the `home/ARMA3_<id>/` profile subdir).
   - Match existing quoting style (paths wrapped in escaped quotes like current `-config`).

2. Repoint the private path helpers to F0 methods:
   - `getConfigFile` → `getInstanceConfigsPath(id)/ARMA3_<id>.cfg` (`configs/`)
   - `getNetworkConfigFile` → `getInstanceConfigsPath(id)/ARMA3_<id>_network.cfg` (`configs/`)
   - `getProfilesDirectoryPath` → `getInstanceProfilesPath(id)`
   - `getProfileFile` — base on instance profiles path; keep the OS `home`/`Users`
     subdir + `ARMA3_<id>/ARMA3_<id>.Arma3Profile` tail.

3. **Directory creation.** Config generation already does `forceMkdirParent`
   (see `ServerConfig.writeNewConfig`), so `configs/` is auto-created. `keys/`
   and `mpmissions/` must be created explicitly. Add a polymorphic prep hook on
   the entity, e.g. `Server.prepareLaunchEnvironment(ctx)` (no-op in base),
   overridden in `Arma3Server` to `Files.createDirectories(keys, mpmissions)`.
   Call it from `ServerProcess.start()` right before/with config generation.
   This same hook becomes the home for F2 key rebuild.

**Gotchas:**
- `generateIfNecessary` only writes if the file is absent. With new instance
  paths the file won't exist there yet, so it regenerates cleanly — but a stale
  shared-dir copy from before the change is now orphaned (handled in F6 cleanup).
- Verify `-keysFolder` capitalization exactly (`-keysFolder`, capital F) — wiki
  example uses that spelling.
- Headless clients (`HeadlessClient`) launch the shared executable and connect
  via `-connect=127.0.0.1:port`; they don't need keys/mpmissions flags, so HC
  code is unaffected. Confirm during testing they still load the same `-mod` set.

**Tests:** unit-assert the parameter list contains the new flags pointing at the
instance dir; integration start of an Arma 3 server creates `configs/keys/mpmissions`.

---

### F2 — Per-instance bikeys at start ✅

Decouple mod install from key placement. Today both installers copy **every**
mod's bikeys into the single shared `servers/ARMA3/keys/` at install time. After
this change, the shared copy is removed and each instance's `keys/` is rebuilt
from its **active** mods at start.

**Remove shared-key writes:**
- `workshop/WorkshopInstallerService`: drop the `keys`-copy in `installNewBiKeys`
  and the delete in `deleteBiKeys` (the parts that write to
  `pathsFactory.getServerKeyPath(...)`). **Keep** recording bikey *names* on the
  mod entity (`mod.addBiKey(name)`) — still needed to know which keys a mod owns.
- `localmod/LocalModInstallerService`: same — stop writing to
  `getServerKeyPath`, keep `mod.addBiKey(name)`.
- `PathsFactory.getServerKeysPath/getServerKeyPath` (type-scoped) become unused
  for Arma 3 — leave for DayZ or remove if no remaining callers.

**Add start-time key rebuild** (new `Arma3KeyService` or method invoked from the
F1 `prepareLaunchEnvironment` hook):
1. Recreate/clear the instance `keys/` dir.
2. Copy the **game's own** bikeys (`servers/ARMA3/keys/*.bikey`) — vanilla clients
   fail signature check (`verifySignatures=2`) without `a3.bikey` et al.
3. For each active mod (workshop + local, from `activeMods`/`activeLocalMods`),
   locate its `.bikey` files in the mod store dir
   (`getModInstallationPath` / `getLocalModPath`) and copy into instance `keys/`.
   Iterate `*.bikey` recursively as the installers do today.

**Why store→instance, not name lookup:** copying from the mod store dir at start
guarantees the key matches the currently-installed mod version and only active
mods land in `keys/` (tighter signature surface than the old copy-all).

**Tests:** start a server with N active mods → `keys/` contains exactly those
mods' bikeys + game bikeys; mod install no longer writes into any shared keys dir.

---

### F3 — Per-instance scenarios (backend) ✅

Make scenario CRUD instance-scoped against `getInstanceMpmissionsPath(id)`.

**OpenAPI (`openapi/paths/scenarios.yaml`, `openapi/openapi.yaml`):**
- Replace global Arma 3 scenario ops with server-scoped paths, e.g.:
  - `GET    /servers/{id}/scenarios`            (list)
  - `POST   /servers/{id}/scenarios`            (upload, multipart)
  - `GET    /servers/{id}/scenarios/{name}`     (download)
  - `DELETE /servers/{id}/scenarios/{name}`     (delete)
- Leave the Reforger listing endpoint (`/scenarios/REFORGER`) as-is — it parses
  the server executable's `-listScenarios` output, unrelated to mpmissions dirs.
- Keep `Arma3ScenarioDto`/`Arma3ScenariosDto` schemas. Regenerate both clients
  (`cd frontend && npm run generate`; backend regenerates on build).

**Backend (`scenario/ScenarioService`, `ScenarioController`):**
- All Arma 3 methods take `long serverId`; resolve dir via
  `getInstanceMpmissionsPath(serverId)`.
- `uploadScenarioToServer(id, file)`, `getAllScenarios(id)`,
  `deleteScenario(id, name)`, `downloadScenario(id, name)`.
- Drop `PathsFactory.getScenariosBasePath()/getScenarioPath()` (global) once
  unused.
- Keep `.pbo`-only validation and existing permission annotations
  (`SCENARIO_VIEW/MODIFY/DELETE`).
- `ServerNotInitializedException` when the install or instance dir is missing.

**Tests:** upload to server A doesn't appear under server B; delete scoped to the
right instance dir.

---

### F4 — Frontend overhaul (summary; expand when reached) ✅

- Remove `pages/ScenariosPage.tsx` + route + nav entry + `scenarioService` global calls.
- Add a scenarios tab/section inside server detail (`ServerSettingsPage` /
  `ServersPage`) calling the new `{id}`-scoped endpoints.
- Update generated client usage (request-object style: `scenariosApi.xxx({id, ...})`).

---

### F5 — Manual per-instance key management (future)

On top of F2's instance `keys/`: upload/enable/disable individual bikeys per
instance. New endpoints + UI. Out of scope for first delivery.

---

### F6 — Migration (auto on startup; part of the F1–F4 release) ✅

What regenerates vs what must move:
- **Configs** (`*.cfg`) — FreeMarker-generated from the DB at start
  (`ServerConfig.writeNewConfig` does `forceMkdirParent`), so they reappear in
  the new `profiles/ARMA3_<id>/configs/` location automatically. **No migration.**
- **Keys** — derived at start (F2). **No migration.**
- **Profiles** — the `.Arma3Profile` *difficulty* file regenerates, BUT the
  profile directory also holds **savegame / persistence data** (persistent
  mission saves, `*.vars.Arma3Profile`, etc.) that is **runtime state, not
  DB-derived**. This **must be moved** or users lose saves. ⚠️
- **Scenarios** — previously shared; **must be copied** per instance.

So migration moves two things: profile dirs (savegames) and scenarios.

**Old → new profile paths** (see `Arma3Server.getProfileFile`):
- Old: `servers/ARMA3/custom_profiles/<sub>/ARMA3_<id>/` where `<sub>` is `home`
  on Linux, `Users` on Windows.
- New: `servers/ARMA3/profiles/ARMA3_<id>/<sub>/ARMA3_<id>/` (engine creates
  `<sub>/<name>/` under `-profiles`; `-name=ARMA3_<id>` unchanged).
- The per-instance dir name (`ARMA3_<id>`) is unchanged, so the move is a
  straight relocation of the existing profile dir into the new instance base.

**Startup migration task** (`@Component` + `ApplicationRunner`, or a Flyway
Java migration if you want it version-tracked):
1. Guard so it runs once (marker file e.g. `servers/ARMA3/.method5-migrated`, or
   a row in a migration-tracking table).
2. For each existing Arma 3 server in the DB:
   a. **Move profile dir (savegames):** if
      `custom_profiles/<sub>/ARMA3_<id>/` exists, move it to
      `profiles/ARMA3_<id>/<sub>/ARMA3_<id>/`. Check both `home` and `Users`
      subdirs (don't assume current OS — a config could have been created on the
      other platform). Prefer **move** over copy to avoid doubling disk and
      stale-save confusion; create parent dirs first.
   b. **Copy scenarios:** copy `servers/ARMA3/mpmissions/*.pbo` →
      `profiles/ARMA3_<id>/mpmissions/`. (Previously all instances shared one
      mpmissions, so every instance inherits the full set; users prune per
      instance afterward.)
3. **No action** for configs (regenerate) or keys (derived).
4. Leave legacy shared `mpmissions/`, `keys/`, stray `ARMA3_<id>.cfg`, and the
   now-empty `custom_profiles/` in place (harmless). Optional later cleanup pass.

**Edge cases:** no Arma 3 install present → skip; no shared `mpmissions/` →
skip scenario copy; no old profile dir for a server → skip its profile move
(fresh server, nothing to preserve); per-file failures logged, don't abort
startup; if a destination profile dir already exists (re-run despite guard
failure), skip to avoid clobbering newer saves.

**Tests:** seed legacy layout (`custom_profiles/home/ARMA3_<id>/` with a fake
save file + shared `mpmissions/*.pbo`) + 2 servers → after startup each
instance's `<sub>/ARMA3_<id>/` contains its save file and each `mpmissions/`
contains the scenarios; second startup is a no-op (no move/copy, no clobber).

---

## Key source references

- `common/PathsFactory.java` — all path logic (the chokepoint).
- `serverinstance/entities/Arma3Server.java` — `getLaunchParameters`,
  `getConfigFiles`, config/profile path helpers.
- `serverinstance/process/ServerProcess.java#start` — config gen + process launch
  (F1 dir-prep / F2 key-rebuild hook site).
- `serverinstance/process/ServerProcessService.java#startServer` — orchestration.
- `serverinstance/headlessclient/HeadlessClient.java` — HC launch (verify unaffected).
- `workshop/WorkshopInstallerService.java`, `localmod/LocalModInstallerService.java`
  — bikey copy to remove (F2).
- `scenario/ScenarioService.java`, `ScenarioController.java` — scope to instance (F3).
- `openapi/paths/scenarios.yaml`, `openapi/openapi.yaml` — scenario contract (F3).
- `serverinstance/ServerConfig.java` — confirms configs regenerate from DB (F6).
