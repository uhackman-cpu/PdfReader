# Universal Mobile Control 2.0 — Hybrid Android Control

UMC 2.0 upgrades the existing Java Android Accessibility project with a consent-based live vision pipeline, structured element actions, bounded task flows, optional notification access, and a bearer-authenticated loopback bridge.

## What is implemented

- **Live Vision:** MediaProjection foreground service; 1 / 3 / 5 FPS capture targets; newest JPEG frame held in memory; sampled visual-difference gate; ML Kit Latin + Devanagari OCR on changed frames with throttling.
- **Screen state:** OCR lines plus accessibility UI tree, active app context and stable element keys in `/screen`; existing Accessibility screenshot scan remains available.
- **Actions:** coordinate tap, swipe, Back, focused text entry, text-based element click, native app launch.
- **Flow runner:** up to 20 bounded steps (`tap_text`, `tap`, `swipe`, `back`, `wait`, `launch`), status, stop, emergency stop, and a best-effort fresh-screen observation after actions.
- **Session recorder:** app-private JSONL audit of action types, flow steps and verification events; text-entry values are not recorded. `/session/history` returns the latest 100 events.
- **System context:** device metadata and optional active-notification reading after the user enables Notification Access.
- **MCP bridge:** localhost-only HTTP endpoints on port 8765 with per-install bearer token; companion Termux MCP extension and integration guide in `mcp/`.

## First run on Android

1. Build and install the debug APK (see below).
2. Open UMC and enable **Vision Accessibility** in Android Settings.
3. If needed, enable **Notification Access** separately. It is optional.
4. Copy the bearer token displayed in the UMC app into the private Termux MCP environment variable `UMC_TOKEN`.
5. Press **Start Live Vision** and approve Android's screen-capture prompt. Android shows an ongoing foreground notification while capture is active.
6. Poll the bridge using the Termux MCP adapter. The bridge is bound to `127.0.0.1:8765`; it is not a public internet endpoint.

## Build

Requirements: JDK 17+, Gradle 8.13+, Android Gradle Plugin 8.13.2, Android SDK platform 36, and the ML Kit dependencies declared in `app/build.gradle`.

```sh
gradle assembleDebug --console plain
```

On Termux, `build-background.sh` uses `$HOME/gradle-8.13/bin/gradle` if present, otherwise `gradle` from PATH, and runs offline using the already-cached Android/Gradle dependencies.

## Important boundaries

- This runtime workspace does not contain an Android SDK or Gradle installation, so no APK has been compiled or device-tested here. Source validation is not a substitute for a successful Android build and a Vivo Android 16 test.
- 1/3/5 FPS are targets, not guaranteed rates. Battery, thermal load, device refresh and Android capture policy affect actual performance.
- Visual difference is a coarse sampled luminance heuristic, not YOLO, object recognition, or semantic understanding.
- OCR is triggered only when the sampled visual difference crosses its threshold and the throttle allows it; this reduces processing but can miss subtle visual changes.
- A successful action dispatch plus a refreshed screenshot does not prove a website accepted a form or completed a transaction.
- Login submission, payments, deletion, messaging, permission changes and other sensitive actions are outside the autonomous flow allowlist and require a separate explicit approval design.
- Notification contents may be private. The optional notification service reads active notifications only after Android's explicit notification-access grant; data is returned only through the authenticated local bridge when requested.
- The bridge has one shared per-install token, not per-client scopes. Keep it private. Do not expose port 8765 directly through a public tunnel. For remote use, place an authenticated encrypted gateway in front of it.
- Shizuku is intentionally deferred; no root or Shizuku permission is required for the implemented path.

## Validation

Run the included static checks:

```sh
python3 tests/validate_project.py
python3 -m unittest discover -s tests -v
```

Then run a real Android build and verify Accessibility, screen-capture consent, frame freshness, OCR, element click, app launch, notification permission and emergency stop on the target phone.


## Latest audit update

See [AUDIT_FIXES_2026-11-17.md](AUDIT_FIXES_2026-11-17.md) for the lifecycle, build portability, installer, and Flow status corrections. Android compilation and physical-device QA remain unverified.
