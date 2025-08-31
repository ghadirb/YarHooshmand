#!/usr/bin/env bash
set -euo pipefail

# 1) Set your Android SDK path (or ensure ANDROID_HOME / ANDROID_SDK_ROOT is set)
#    If needed, create local.properties with:
#      sdk.dir=/absolute/path/to/Android/Sdk
if [ ! -f "local.properties" ]; then
  echo "local.properties not found. Create one with your Android SDK path, e.g.:"
  echo "sdk.dir=/home/you/Android/Sdk" > local.properties
  echo "# Please edit the path above to your actual SDK path."
fi

chmod +x ./gradlew || true

# 2) Clean & build release APK
./gradlew clean assembleRelease

echo
echo "Build finished. Release APKs should be in app/build/outputs/apk/release/"
