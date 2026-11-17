#!/data/data/com.termux/files/usr/bin/bash
set -eu
PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
MCP_DIR="$HOME/termux-mcp"
mkdir -p "$HOME/.config/umc" "$HOME/bin" "$MCP_DIR"
cp "$PROJECT_DIR/mcp/umc_mcp_extension.py" "$MCP_DIR/umc_mcp_extension.py"
python "$PROJECT_DIR/mcp/apply_umc_mcp_patch.py"
if [ ! -f "$HOME/.config/umc/bridge.env" ]; then
  printf 'Paste the bearer token shown inside the UMC app: '
  stty -echo
  IFS= read -r TOKEN
  stty echo
  printf '\n'
  umask 077
  printf 'export UMC_TOKEN=%q\n' "$TOKEN" > "$HOME/.config/umc/bridge.env"
fi
chmod 600 "$HOME/.config/umc/bridge.env"
cat > "$HOME/bin/umc-mcp-start" <<'START'
#!/data/data/com.termux/files/usr/bin/bash
set -eu
. "$HOME/.config/umc/bridge.env"
exec "$HOME/bin/mcp-start"
START
chmod 700 "$HOME/bin/umc-mcp-start"
printf '\nUMC MCP adapter installed. Start/restart the server with: umc-mcp-start\n'
