#!/usr/bin/env bash
set -euo pipefail

# Reads RELEASE_TYPE and RC_NUMBER from environment.
# Reads current version from gradle.properties.
# Updates gradle.properties and frontend/package.json to the release version.
# Writes RELEASE_VERSION and NEXT_SNAPSHOT to GITHUB_ENV.

RELEASE_TYPE="${RELEASE_TYPE:?RELEASE_TYPE is required}"
RC_NUMBER="${RC_NUMBER:-1}"

CURRENT=$(grep '^appVersion=' gradle.properties | cut -d= -f2)
BASE=$(echo "$CURRENT" | sed 's/-.*$//')
MAJOR=$(echo "$BASE" | cut -d. -f1)
MINOR=$(echo "$BASE" | cut -d. -f2)
PATCH=$(echo "$BASE" | cut -d. -f3)

case "$RELEASE_TYPE" in
  patch)
    RELEASE_VERSION="${MAJOR}.${MINOR}.${PATCH}"
    NEXT_SNAPSHOT="${MAJOR}.${MINOR}.$((PATCH + 1))-SNAPSHOT"
    ;;
  minor)
    NEW_MINOR=$((MINOR + 1))
    RELEASE_VERSION="${MAJOR}.${NEW_MINOR}.0"
    NEXT_SNAPSHOT="${MAJOR}.${NEW_MINOR}.1-SNAPSHOT"
    ;;
  major)
    NEW_MAJOR=$((MAJOR + 1))
    RELEASE_VERSION="${NEW_MAJOR}.0.0"
    NEXT_SNAPSHOT="${NEW_MAJOR}.0.1-SNAPSHOT"
    ;;
  rc)
    RELEASE_VERSION="${MAJOR}.${MINOR}.${PATCH}-RC.${RC_NUMBER}"
    NEXT_SNAPSHOT="${CURRENT}"
    ;;
  *)
    echo "Unknown release type: $RELEASE_TYPE" >&2
    exit 1
    ;;
esac

echo "RELEASE_VERSION=${RELEASE_VERSION}" >> "$GITHUB_ENV"
echo "NEXT_SNAPSHOT=${NEXT_SNAPSHOT}" >> "$GITHUB_ENV"

sed -i "s/^appVersion=.*/appVersion=${RELEASE_VERSION}/" gradle.properties
sed -i "s/\"version\": \"[^\"]*\"/\"version\": \"${RELEASE_VERSION}\"/" frontend/package.json

echo "Release version: ${RELEASE_VERSION}"
echo "Next snapshot:   ${NEXT_SNAPSHOT}"
