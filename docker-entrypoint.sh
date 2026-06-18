#!/bin/sh
set -e

DATA_DIR="/home/steam/armaservermanager"

# Detect the owner of the bind-mounted data directory.
# On Linux this reflects the host user who created STORAGE_PATH — so the app
# writes files owned by that user without any explicit configuration.
DET_UID="$(stat -c '%u' "$DATA_DIR" 2>/dev/null || echo 0)"
DET_GID="$(stat -c '%g' "$DATA_DIR" 2>/dev/null || echo 0)"

# Precedence: explicit PUID/PGID env override > detected owner > default 1000.
# A detected uid/gid of 0 (root-owned dir or undetectable) falls back to 1000.
TARGET_UID="${PUID:-$DET_UID}"
TARGET_GID="${PGID:-$DET_GID}"
[ "$TARGET_UID" = "0" ] && TARGET_UID=1000
[ "$TARGET_GID" = "0" ] && TARGET_GID=1000

MARKER="$DATA_DIR/.asm-owner"
WANT="${TARGET_UID}:${TARGET_GID}"

# Remap the steam user/group to the resolved ids.
# -o allows non-unique ids; this is a no-op when the ids are already correct.
groupmod -o -g "$TARGET_GID" steam
usermod  -o -u "$TARGET_UID" -g "$TARGET_GID" steam

# Cheaply fix the mount-point dirs themselves (covers fresh/empty volumes).
chown steam:steam /home/steam "$DATA_DIR" /home/steam/config /home/steam/Steam/config 2>/dev/null || true

# One-time (or on uid/gid change) deep ownership migration.
# Tracked by a marker file storing the last-applied uid:gid so subsequent
# container starts skip the potentially large recursive chown.
if [ "$(cat "$MARKER" 2>/dev/null)" != "$WANT" ]; then
    echo "[entrypoint] Applying ownership ${WANT} to /home/steam (one-time migration)..."
    chown -R steam:steam /home/steam
    echo "$WANT" > "$MARKER"
fi

exec gosu steam "$@"
