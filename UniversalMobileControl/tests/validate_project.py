from pathlib import Path
import ast
import json
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
ET.parse(ROOT / "app/src/main/AndroidManifest.xml")
ET.parse(ROOT / "app/src/main/res/xml/accessibility_service_config.xml")

# Validate Python MCP adapter syntax without importing Android or making requests.
ast.parse((ROOT / "mcp/umc_mcp_extension.py").read_text(encoding="utf-8"))

manifest = (ROOT / "app/src/main/AndroidManifest.xml").read_text(encoding="utf-8")
for required in ("FOREGROUND_SERVICE", "FOREGROUND_SERVICE_MEDIA_PROJECTION", "foregroundServiceType=\"mediaProjection\"", "NotificationAccessService"):
    assert required in manifest, f"Manifest missing {required}"

vision = (ROOT / "app/src/main/java/com/gpsrginfo/universalcontrol/VisionAccessibilityService.java").read_text(encoding="utf-8")
for endpoint in ("/live/status", "/live/frame", "/tap_text", "/launch", "/device/info", "/notifications", "/session/history", "/flow/run", "/flow/status", "/flow/stop", "/emergency/stop"):
    assert endpoint in vision, f"Missing route {endpoint}"
assert "BridgeAuth.isAuthorized" in vision
assert "127.0.0.1" in vision
assert "private volatile ServerSocket bridgeServer" in vision
assert "activeServer.close()" in vision
assert "activeBridgeThread.interrupt()" in vision
assert "while (!Thread.currentThread().isInterrupted() && !server.isClosed())" in vision
assert 'stepEvent.put("verified", false)' in vision
assert '"screen refresh observed; target postcondition not checked"' in vision

installer = (ROOT / "mcp/activate_umc_mcp.sh").read_text(encoding="utf-8")
assert 'mkdir -p "$HOME/.config/umc" "$HOME/bin" "$MCP_DIR"' in installer
properties = (ROOT / "gradle.properties").read_text(encoding="utf-8")
assert "android.aapt2FromMavenOverride=/data/data/com.termux" not in properties

live = (ROOT / "app/src/main/java/com/gpsrginfo/universalcontrol/LiveVisionService.java").read_text(encoding="utf-8")
assert "calculateDiffPercent" in live and "processLiveFrame" in live
assert "lastOcrDispatchAt >= 1100L" in live

print("UMC static project validation: PASS")
