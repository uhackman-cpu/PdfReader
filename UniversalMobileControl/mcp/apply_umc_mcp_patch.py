#!/usr/bin/env python3
"""Safely patch the known Termux MCP server to expose UMC tools."""
from pathlib import Path
from datetime import datetime
import shutil
import sys

server = Path.home() / "termux-mcp" / "server_http_v3.py"
adapter = Path(__file__).with_name("umc_mcp_extension.py")
if not server.is_file():
    raise SystemExit(f"Termux MCP server not found: {server}")
if not adapter.is_file():
    raise SystemExit(f"UMC adapter not found: {adapter}")
source = server.read_text(encoding="utf-8")
if "UMC_MCP_EXTENSION_IMPORT" in source:
    print("UMC MCP integration is already present; no changes made.")
    raise SystemExit(0)

# Verify exact integration anchors before creating a backup or writing anything.
import_anchor = "def safe_storage_path(p=\".\"):"
list_anchor = 'return {"jsonrpc":"2.0","id":rid,"result":{"tools":tools}}'
call_anchor = '        name = params.get("name")\n        args = params.get("arguments") or {}\n        try:'
for anchor in (import_anchor, list_anchor, call_anchor):
    if anchor not in source:
        raise SystemExit("Patch aborted: server version did not match the expected integration anchors. No changes made.")

backup = server.with_name(server.name + ".pre-umc-" + datetime.now().strftime("%Y%m%d-%H%M%S") + ".bak")
shutil.copy2(server, backup)
shutil.copy2(adapter, server.parent / "umc_mcp_extension.py")

source = source.replace(
    import_anchor,
    "# UMC_MCP_EXTENSION_IMPORT\nfrom umc_mcp_extension import UMC_TOOL_SCHEMAS, UMC_TOOL_NAMES, umc_mcp_call\n\n" + import_anchor,
    1,
)
source = source.replace(
    list_anchor,
    "tools.extend(UMC_TOOL_SCHEMAS)\n        " + list_anchor,
    1,
)
source = source.replace(
    call_anchor,
    '''        name = params.get("name")
        args = params.get("arguments") or {}
        if name in UMC_TOOL_NAMES:
            try:
                return {"jsonrpc":"2.0","id":rid,"result":umc_mcp_call(name, args)}
            except Exception as exc:
                return {"jsonrpc":"2.0","id":rid,"result":tool_result("UMC tool error: " + str(exc), True)}
        try:''',
    1,
)
server.write_text(source, encoding="utf-8")
print("UMC MCP tools integrated.")
print("Backup:", backup)
print("Adapter:", server.parent / "umc_mcp_extension.py")
print("Set UMC_TOKEN in the MCP server process environment, then restart the server.")
