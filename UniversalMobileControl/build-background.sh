#!/data/data/com.termux/files/usr/bin/bash
set -eu
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
if [ -x "$HOME/gradle-8.13/bin/gradle" ]; then
  GRADLE_BIN="$HOME/gradle-8.13/bin/gradle"
elif command -v gradle >/dev/null 2>&1; then
  GRADLE_BIN="$(command -v gradle)"
else
  echo "Gradle 8.13+ not found. Install Gradle in Termux, then rerun this script." >&2
  exit 127
fi
GRADLE_ARGS=(-p "$PROJECT_DIR" assembleDebug --offline --console plain --no-daemon)
# Prefer Termux's native AAPT2 when present, without baking a device-specific path
# into gradle.properties. Other environments use the Android Gradle Plugin default.
if command -v aapt2 >/dev/null 2>&1; then
  GRADLE_ARGS+=("-Pandroid.aapt2FromMavenOverride=$(command -v aapt2)")
fi
nohup "$GRADLE_BIN" "${GRADLE_ARGS[@]}" > "$PROJECT_DIR/umc-build.log" 2>&1 &
echo "BUILD_STARTED pid=$! project=$PROJECT_DIR"
