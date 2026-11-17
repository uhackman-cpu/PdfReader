# UMC 2.0 — Audit Fixes (2026-11-17)

This update addresses four static-audit findings without changing the app's public MCP routes.

## Changes

1. **Local bridge lifecycle:** `VisionAccessibilityService` now retains its `ServerSocket` and bridge thread. `onDestroy()` closes the socket and interrupts the thread; the accept loop exits on socket closure/interruption and clears lifecycle state in `finally`. This is intended to permit a clean service restart and release TCP port 8765.
2. **Portable AAPT2 configuration:** removed the hard-coded Termux absolute path from `gradle.properties`. `build-background.sh` now passes the local `aapt2` path as a Gradle project property only when that executable exists; other environments use the Android Gradle Plugin default.
3. **Fresh Termux installer:** installer creates `$HOME/bin` and the MCP destination directory before copying/creating scripts.
4. **Honest Flow status:** a post-action screen revision is now recorded as `screen_refresh_observed`, with `verified: false` and an explicit note that the target postcondition was not checked. This avoids reporting a mere screen refresh as proof that the intended action succeeded.

## Validation performed in ChatGPT runtime

- ZIP integrity: passed.
- Python adapter unit tests: 5/5 passed.
- Static project validator: passed, including lifecycle/installer/portability assertions.
- `bash -n` on installer and build script: passed.
- Manifest and accessibility XML parsing: passed through the project validator.

## Not verified

- Android Java compilation / Gradle `assembleDebug`: not run because this runtime has Java but no Gradle executable or Android SDK (`android.jar`).
- Installation, MediaProjection, Accessibility actions, and bridge restart on a physical Android device: not run.
- Shizuku backend remains outside this implementation.

This ZIP is an updated source bundle, **not a verified APK release**. Build and device QA are still required.
