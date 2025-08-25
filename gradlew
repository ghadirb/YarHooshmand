#!/usr/bin/env sh
echo "If wrapper jar is missing, Android Studio will regenerate it during Sync."
DIR="$(cd "$(dirname "$0")" && pwd)"
exec "$DIR/gradlew" "$@"
