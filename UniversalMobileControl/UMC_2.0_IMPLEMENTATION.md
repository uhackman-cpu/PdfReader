# Universal Mobile Control 2.0 — Hybrid implementation

## Included in this runtime-built source package

### Vision
- User-consented MediaProjection foreground service with 1 / 3 / 5 FPS target modes.
- Latest JPEG frame retained in memory and returned only when requested from the loopback bridge.
- 16x16 luminance sampling computes a coarse visual-difference percentage. It is a change detector, not object detection.
- OCR dispatch is gated on a sampled visual difference (>= 7% of sampled pixels) and throttled to at most one dispatch per 1.1 seconds.
- Changed MediaProjection frames are passed to ML Kit Latin + Devanagari OCR. OCR output is merged with the Accessibility UI tree into `/screen` state.
- Existing Accessibility screenshot OCR path remains available.

### Actions and app context
- Existing coordinate `/tap`, `/swipe`, `/back`, `/type` routes preserved.
- `/tap_text?text=...` searches visible accessibility nodes and performs ACTION_CLICK on the node or nearest clickable ancestor.
- `/launch?package=...` launches an installed app through its normal launcher intent.
- `/device/info` returns basic Android build/device metadata.
- `/notifications` returns active notification metadata/text only after the user enables the optional NotificationListenerService in Android Settings.
- `/session/history` returns the latest 100 locally recorded action/flow events.
- `/flow/run?steps=<URL-encoded JSON array>` accepts up to 20 bounded steps. Supported actions: `tap_text`, `tap`, `swipe`, `back`, `wait`, `launch`.
- `/flow/status`, `/flow/stop`, `/emergency/stop` expose task state and cancellation. Non-wait steps trigger a fresh scan and wait briefly for state change; this is best-effort verification, not proof that the user's intended business outcome occurred.

### Android service requirements
- Manifest declares `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PROJECTION`, optional notification permission, and the mediaProjection foreground service type.
- MediaProjection still requires Android's explicit consent dialog. Android may revoke capture at any time.
- Accessibility must be manually enabled in Android Settings.

## Local bridge endpoints

Every endpoint other than `/health` requires the bearer token shown in the UMC app. The Termux adapter sends this header automatically when `UMC_TOKEN` is configured.

Every endpoint other than `/health` requires the bearer token shown in the UMC app. The Termux adapter sends this header automatically when `UMC_TOKEN` is configured.

Bound only to `127.0.0.1:8765`:
- `GET /health` (unauthenticated health check)
- `GET /screen`
- `GET /live/status`
- `GET /live/frame`
- `GET /scan`
- `GET /action/start`, `GET /action/stop`, `GET /action/status`
- `GET /tap?x=&y=`, `GET /tap_text?text=`
- `GET /swipe?x1=&y1=&x2=&y2=&duration=`
- `GET /back`, `GET /type?text=`
- `GET /launch?package=`, `GET /device/info`, `GET /notifications`, `GET /session/history`
- `GET /flow/run?steps=`, `GET /flow/status`, `GET /flow/stop`, `GET /emergency/stop`

Flow JSON example (URL-encode the JSON value):
```json
[
  {"action":"launch","package":"com.android.chrome"},
  {"action":"wait","ms":900},
  {"action":"tap_text","text":"Search or type URL"}
]
```

## Security boundaries and limitations
- The HTTP bridge binds to loopback only. Do not expose port 8765 directly to the internet. A remote MCP gateway must authenticate requests and use encrypted transport.
- The bridge is intended for local development; it has a single per-install bearer token, but does not provide per-client scopes or TLS. The companion Termux MCP adapter and guarded installer are included in `mcp/`; the installer is not run automatically from this build workspace.
- Flow execution is bounded to 20 steps and supports only the listed non-destructive actions. Payment, deletion, sending, login submission, permission changes, and other sensitive actions are intentionally not automated by this runner.
- Notification access is included as an optional Android NotificationListenerService and requires manual enablement in Android Settings. The Shizuku backend is not included yet; it requires a separate integration and permission review.
- Frame-diff is a low-cost visual-change heuristic; it is not semantic understanding and can miss subtle changes.
- 1/3/5 FPS are capture targets. Actual frame delivery depends on hardware, Android policy, and workload.
- Live OCR is event/diff-gated and throttled; continuous OCR is not run on every frame.
- Verification is best-effort: an updated screen snapshot is observed after an action, but it cannot infer whether a remote website accepted a form or completed a transaction.

## Build / device validation status

Source was modified and statically checked in the build workspace. Android SDK / Gradle are not installed in this runtime, so an APK compile, install, and Vivo Android 16 device test have not yet been performed. Do not treat this package as a verified release APK.
