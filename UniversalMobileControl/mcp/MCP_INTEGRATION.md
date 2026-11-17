# Termux MCP integration (manual activation)

This source package includes `umc_mcp_extension.py`, a dependency-free adapter for the existing Termux MCP server, plus `activate_umc_mcp.sh` and a guarded patcher. The patcher checks known source anchors and creates a timestamped backup before editing.

## 1. Install adapter beside the server

From the extracted UMC project directory in Termux, you can run `bash mcp/activate_umc_mcp.sh`. It copies the adapter, applies the guarded patch, asks for the app token once, and creates `~/bin/umc-mcp-start` to load the private token and start your existing MCP server. If the server source has changed, the patcher aborts without editing it; then follow the manual steps below.

Copy `umc_mcp_extension.py` into `~/termux-mcp/` (same directory as `server_http_v3.py`). Set `UMC_TOKEN` in the Termux MCP server process environment to the bearer token displayed in the UMC app. Do not put the token in a public repo or log.

## 2. Add tools to the existing server

Near the imports in `server_http_v3.py`:

```python
from umc_mcp_extension import UMC_TOOL_SCHEMAS, UMC_TOOL_NAMES, umc_mcp_call
```

In the `tools/list` branch, immediately before returning the tool list:

```python
tools.extend(UMC_TOOL_SCHEMAS)
```

In the `tools/call` branch, after reading `name` and `args` and before the existing tool dispatch:

```python
if name in UMC_TOOL_NAMES:
    result = umc_mcp_call(name, args)
    return {"jsonrpc": "2.0", "id": rid, "result": result}
```

Set the environment variable before launching the server, e.g. from a private local shell profile:

```sh
export UMC_TOKEN='PASTE_THE_TOKEN_SHOWN_IN_UMC_APP'
```

Restart the Termux MCP server after making the integration. The adapter calls only `127.0.0.1:8765` and supplies `Authorization: Bearer ...`.

## 3. Available MCP tools

`phone_get_screen`, `phone_get_live_frame`, `phone_get_live_status`, `phone_tap`, `phone_tap_text`, `phone_swipe`, `phone_type`, `phone_launch_app`, `phone_press_back`, `phone_get_device_info`, `phone_get_notifications`, `phone_get_session_history`, `phone_execute_flow`, `phone_get_flow_status`, `phone_stop_execution`, `phone_emergency_stop`.

`phone_get_notifications` requires the user to enable UMC Notification Access in Android Settings. The tool does not bypass Android permission prompts.

## Safety

The app bridge listens only on `127.0.0.1`. Do not expose port 8765 directly through Cloudflare Tunnel or another public ingress. If remote ChatGPT access is required, route through an authenticated encrypted gateway and keep the UMC bearer token private. Rotate the token by clearing UMC app data (this also resets local app preferences) or implement a dedicated token-rotation UI before production deployment.
