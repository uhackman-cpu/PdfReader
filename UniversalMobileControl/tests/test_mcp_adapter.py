import importlib.util
import json
import pathlib
import unittest
from unittest.mock import patch

MODULE = pathlib.Path(__file__).resolve().parents[1] / "mcp" / "umc_mcp_extension.py"
spec = importlib.util.spec_from_file_location("umc_mcp_extension", MODULE)
mod = importlib.util.module_from_spec(spec)
spec.loader.exec_module(mod)

class AdapterTests(unittest.TestCase):
    def test_tool_names_unique(self):
        names = [x["name"] for x in mod.UMC_TOOL_SCHEMAS]
        self.assertEqual(len(names), len(set(names)))
        self.assertIn("phone_emergency_stop", names)
        self.assertIn("phone_get_screen_state", names)
        self.assertIn("phone_get_session_history", names)

    def test_flow_rejects_more_than_twenty(self):
        with self.assertRaises(ValueError):
            mod.umc_mcp_call("phone_execute_flow", {"steps": [{"action":"wait"}] * 21})

    def test_flow_rejects_unknown_action(self):
        with self.assertRaises(ValueError):
            mod.umc_mcp_call("phone_execute_flow", {"steps": [{"action":"delete_everything"}]})

    def test_flow_serializes_steps(self):
        expected = {"status":"ok","message":"flow accepted"}
        with patch.object(mod, "_request", return_value=expected) as req:
            result = mod.umc_mcp_call("phone_execute_flow", {"steps":[{"action":"wait","ms":200}]})
            self.assertEqual(result, expected)
            self.assertEqual(req.call_args.args[0], "/flow/run")
            self.assertEqual(json.loads(req.call_args.args[1]["steps"]), [{"action":"wait","ms":200}])

    def test_text_click_url_route(self):
        with patch.object(mod, "_request", return_value={"status":"dispatched"}) as req:
            mod.umc_mcp_call("phone_tap_text", {"text":"Settings & privacy"})
            self.assertEqual(req.call_args.args[0], "/tap_text")
            self.assertEqual(req.call_args.args[1]["text"], "Settings & privacy")

if __name__ == "__main__":
    unittest.main()
