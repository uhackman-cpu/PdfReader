"""UMC 2.0 bridge tools for the existing Termux MCP server.

Copy this module beside server_http_v3.py, then apply the small integration
snippet in MCP_INTEGRATION.md. No third-party Python dependency is required.
"""
from __future__ import annotations
import base64
import json
import os
import urllib.error
import urllib.parse
import urllib.request

BASE_URL = os.environ.get("UMC_BASE_URL", "http://127.0.0.1:8765").rstrip("/")
TOKEN_ENV = "UMC_TOKEN"

UMC_TOOL_SCHEMAS = [
    {"name":"phone_get_screen","description":"Read the latest combined OCR and Android accessibility UI tree.","inputSchema":{"type":"object","properties":{}}},
    {"name":"phone_get_screen_state","description":"Alias for the latest structured screen state (OCR, UI tree and active app context).","inputSchema":{"type":"object","properties":{}}},
    {"name":"phone_get_live_frame","description":"Retrieve the latest user-consented MediaProjection frame as an image.","inputSchema":{"type":"object","properties":{}}},
    {"name":"phone_get_live_status","description":"Get Live Vision capture and frame-difference status.","inputSchema":{"type":"object","properties":{}}},
    {"name":"phone_tap","description":"Tap a screen coordinate. Use current screen dimensions/state first.","inputSchema":{"type":"object","properties":{"x":{"type":"number"},"y":{"type":"number"}},"required":["x","y"]}},
    {"name":"phone_tap_text","description":"Click a visible accessibility element matching text.","inputSchema":{"type":"object","properties":{"text":{"type":"string"}},"required":["text"]}},
    {"name":"phone_swipe","description":"Swipe between screen coordinates.","inputSchema":{"type":"object","properties":{"x1":{"type":"number"},"y1":{"type":"number"},"x2":{"type":"number"},"y2":{"type":"number"},"duration":{"type":"integer"}},"required":["x1","y1","x2","y2"]}},
    {"name":"phone_type","description":"Set text in the currently focused editable field.","inputSchema":{"type":"object","properties":{"text":{"type":"string"}},"required":["text"]}},
    {"name":"phone_launch_app","description":"Launch an installed Android app by package name.","inputSchema":{"type":"object","properties":{"package":{"type":"string"}},"required":["package"]}},
    {"name":"phone_press_back","description":"Dispatch Android Back through AccessibilityService.","inputSchema":{"type":"object","properties":{}}},
    {"name":"phone_get_device_info","description":"Get basic Android device and OS information.","inputSchema":{"type":"object","properties":{}}},
    {"name":"phone_get_notifications","description":"Read active notifications only if the user enabled UMC Notification Access in Android Settings.","inputSchema":{"type":"object","properties":{}}},
    {"name":"phone_get_session_history","description":"Read the most recent local UMC action and flow audit events.","inputSchema":{"type":"object","properties":{}}},
    {"name":"phone_execute_flow","description":"Run up to 20 bounded non-destructive steps: tap_text, tap, swipe, back, wait, launch.","inputSchema":{"type":"object","properties":{"steps":{"type":"array","minItems":1,"maxItems":20,"items":{"type":"object"}}},"required":["steps"]}},
    {"name":"phone_get_flow_status","description":"Get the current UMC flow runner state.","inputSchema":{"type":"object","properties":{}}},
    {"name":"phone_stop_execution","description":"Stop the current UMC flow runner.","inputSchema":{"type":"object","properties":{}}},
    {"name":"phone_emergency_stop","description":"Emergency stop: request cancellation of flow, continuous OCR and Live Vision capture.","inputSchema":{"type":"object","properties":{}}},
]
UMC_TOOL_NAMES = {x["name"] for x in UMC_TOOL_SCHEMAS}


def _request(path: str, params: dict | None = None) -> dict:
    token = os.environ.get(TOKEN_ENV, "").strip()
    if not token:
        raise RuntimeError("UMC_TOKEN is not set. Copy the bearer token shown in the UMC app into the Termux MCP environment.")
    query = "?" + urllib.parse.urlencode(params or {}, doseq=True) if params else ""
    req = urllib.request.Request(BASE_URL + path + query, headers={"Authorization": "Bearer " + token, "Accept": "application/json"})
    try:
        with urllib.request.urlopen(req, timeout=12) as response:
            payload = response.read(16 * 1024 * 1024)
    except urllib.error.HTTPError as exc:
        payload = exc.read(1024 * 1024)
        try: detail = json.loads(payload.decode("utf-8", "replace"))
        except Exception: detail = {"message": payload.decode("utf-8", "replace")}
        raise RuntimeError(f"UMC HTTP {exc.code}: {detail}") from exc
    except Exception as exc:
        raise RuntimeError(f"UMC bridge unavailable at {BASE_URL}: {exc}") from exc
    try: return json.loads(payload.decode("utf-8"))
    except Exception as exc: raise RuntimeError("UMC returned invalid JSON") from exc


def umc_mcp_call(name: str, args: dict | None = None) -> dict:
    args = args or {}
    routes = {
        "phone_get_screen": ("/screen", {}),
        "phone_get_screen_state": ("/screen", {}),
        "phone_get_live_frame": ("/live/frame", {}),
        "phone_get_live_status": ("/live/status", {}),
        "phone_tap": ("/tap", {k: args[k] for k in ("x", "y") if k in args}),
        "phone_tap_text": ("/tap_text", {"text": args.get("text", "")}),
        "phone_swipe": ("/swipe", {k: args[k] for k in ("x1", "y1", "x2", "y2", "duration") if k in args}),
        "phone_type": ("/type", {"text": args.get("text", "")}),
        "phone_launch_app": ("/launch", {"package": args.get("package", "")}),
        "phone_press_back": ("/back", {}),
        "phone_get_device_info": ("/device/info", {}),
        "phone_get_notifications": ("/notifications", {}),
        "phone_get_session_history": ("/session/history", {}),
        "phone_get_flow_status": ("/flow/status", {}),
        "phone_stop_execution": ("/flow/stop", {}),
        "phone_emergency_stop": ("/emergency/stop", {}),
    }
    if name == "phone_execute_flow":
        steps = args.get("steps")
        if not isinstance(steps, list) or not 1 <= len(steps) <= 20:
            raise ValueError("steps must be a list containing 1 to 20 items")
        allowed = {"tap_text", "tap", "swipe", "back", "wait", "launch"}
        for index, step in enumerate(steps, 1):
            if not isinstance(step, dict) or step.get("action") not in allowed:
                raise ValueError(f"step {index}: unsupported action")
        return _request("/flow/run", {"steps": json.dumps(steps, ensure_ascii=False, separators=(",", ":"))})
    if name not in routes:
        raise ValueError("Unknown UMC tool: " + name)
    path, params = routes[name]
    result = _request(path, params)
    if name == "phone_get_live_frame" and result.get("frame_base64"):
        raw = base64.b64decode(result["frame_base64"], validate=True)
        return {"content":[
            {"type":"text","text":json.dumps({k:v for k,v in result.items() if k != "frame_base64"}, ensure_ascii=False)},
            {"type":"image","data":base64.b64encode(raw).decode("ascii"),"mimeType":result.get("mime_type", "image/jpeg")}
        ],"isError":False}
    return {"content":[{"type":"text","text":json.dumps(result, ensure_ascii=False)}],"isError":result.get("status") == "error"}
