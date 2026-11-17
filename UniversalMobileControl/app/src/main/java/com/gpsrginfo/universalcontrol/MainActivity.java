package com.gpsrginfo.universalcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.media.projection.MediaProjectionManager;
import android.content.Context;
import android.os.Build;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int REQUEST_MEDIA_PROJECTION = 7402;
    private int requestedFps = 1;
    static volatile TextView output;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final Runnable autoRefresh = new Runnable() {
        @Override public void run() {
            if (output != null && VisionAccessibilityService.instance != null) {
                output.setText(VisionAccessibilityService.latestJson);
            }
            uiHandler.postDelayed(this, 2000L);
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 32, 24, 24);

        TextView title = new TextView(this);
        title.setText("Universal Mobile Control\nAI-Native Control • UMC 2.0 Preview");
        title.setTextSize(22);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0,0,0,20);
        root.addView(title);

        Button enable = new Button(this);
        enable.setText("1. Enable Vision Accessibility");
        enable.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        root.addView(enable);

        Button scan = new Button(this);
        scan.setText("2. Read Current Screen (OCR + Coordinates)");
        scan.setOnClickListener(v -> {
            VisionAccessibilityService s = VisionAccessibilityService.instance;
            if (s == null) setOutput("Accessibility service is not enabled yet. Enable it above, then return here.");
            else { setOutput("Scanning screen…"); s.scanScreen(); }
        });
        root.addView(scan);

        Button liveStart = new Button(this);
        liveStart.setText("3. Start Live Vision (user consent required)");
        liveStart.setOnClickListener(v -> {
            MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(manager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        });
        root.addView(liveStart);

        Spinner fps = new Spinner(this);
        String[] modes = {"Normal • 1 FPS", "Active • 3 FPS", "Fast • 5 FPS"};
        fps.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, modes));
        fps.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> p, android.view.View v, int position, long id) {
                requestedFps = position == 0 ? 1 : position == 1 ? 3 : 5;
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> p) { requestedFps = 1; }
        });
        root.addView(fps);

        Button liveStop = new Button(this);
        liveStop.setText("Stop Live Vision");
        liveStop.setOnClickListener(v -> {
            Intent stop = new Intent(this, LiveVisionService.class);
            stopService(stop);
            setOutput("Live Vision stop requested.");
        });
        root.addView(liveStop);

        Button notificationAccess = new Button(this);
        notificationAccess.setText("Enable Notification Access (optional)");
        notificationAccess.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        root.addView(notificationAccess);

        Button actionStart = new Button(this);
        actionStart.setText("4. Start Continuous OCR + UI State");
        actionStart.setOnClickListener(v -> {
            VisionAccessibilityService s = VisionAccessibilityService.instance;
            if (s == null) setOutput("Enable Accessibility first.");
            else { s.startControlSession(); setOutput("Continuous screen-state refresh started."); }
        });
        root.addView(actionStart);

        Button actionStop = new Button(this);
        actionStop.setText("Stop Continuous OCR");
        actionStop.setOnClickListener(v -> {
            VisionAccessibilityService s = VisionAccessibilityService.instance;
            if (s != null) s.stopControlSession();
            setOutput("Continuous OCR stopped.");
        });
        root.addView(actionStop);

        Button latest = new Button(this);
        latest.setText("Show Latest Screen Map");
        latest.setOnClickListener(v -> setOutput(VisionAccessibilityService.latestJson));
        root.addView(latest);

        Button history = new Button(this);
        history.setText("View Recent 20 OCR");
        history.setOnClickListener(v -> setOutput(VisionAccessibilityService.getRecentOcrForDisplay()));
        root.addView(history);

        TextView tokenLabel = new TextView(this);
        tokenLabel.setText("Local MCP bridge bearer token (keep private):\n" + BridgeAuth.getToken(this));
        tokenLabel.setTextIsSelectable(true);
        tokenLabel.setTextSize(12);
        tokenLabel.setPadding(0, 12, 0, 12);
        root.addView(tokenLabel);

        TextView hint = new TextView(this);
        hint.setText("Local bridge: http://127.0.0.1:8765/health | /screen | /live/status | /live/frame\nLive Vision requires the Android screen-capture consent dialog. Frames remain local until requested.\nAction Mode: OCR refresh every 3 seconds while actions are active.\nRecent OCR: last 20 scans are saved automatically.");
        hint.setPadding(0, 12, 0, 12);
        root.addView(hint);

        ScrollView scroll = new ScrollView(this);
        output = new TextView(this);
        output.setTextIsSelectable(true);
        output.setTextSize(12);
        output.setText("Ready. Enable Accessibility to allow screen inspection.");
        scroll.addView(output);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
        uiHandler.post(autoRefresh);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK && data != null) {
                Intent start = new Intent(this, LiveVisionService.class);
                start.setAction(LiveVisionService.ACTION_START);
                start.putExtra(LiveVisionService.EXTRA_RESULT_CODE, resultCode);
                start.putExtra(LiveVisionService.EXTRA_RESULT_DATA, data);
                start.putExtra(LiveVisionService.EXTRA_FPS, requestedFps);
                if (Build.VERSION.SDK_INT >= 26) startForegroundService(start); else startService(start);
                setOutput("Live Vision start requested. Android will show an ongoing capture notification.");
            } else setOutput("Live Vision permission was cancelled. No screen capture started.");
        }
    }

    @Override protected void onDestroy() {
        uiHandler.removeCallbacks(autoRefresh);
        super.onDestroy();
    }

    static void setOutput(String s) {
        TextView v = output;
        if (v != null) v.post(() -> v.setText(s == null ? "" : s));
    }
}