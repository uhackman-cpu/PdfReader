package com.gpsrginfo.universalcontrol;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/** User-consented MediaProjection capture backend. Frames stay on-device until requested over localhost. */
public class LiveVisionService extends Service {
    public static final String ACTION_START = "com.gpsrginfo.universalcontrol.LIVE_START";
    public static final String ACTION_STOP = "com.gpsrginfo.universalcontrol.LIVE_STOP";
    public static final String EXTRA_RESULT_CODE = "result_code";
    public static final String EXTRA_RESULT_DATA = "result_data";
    public static final String EXTRA_FPS = "fps";
    private static final String CHANNEL = "umc_live_vision";
    private static final int NOTIFICATION_ID = 2301;
    private static volatile byte[] latestJpeg;
    private static volatile long latestFrameAt;
    private static volatile boolean running;
    private static volatile boolean changed = false;
    private static volatile int visualDiffPercent = 0;
    private static volatile int configuredFps = 1;
    private static volatile int screenWidth, screenHeight;
    private static volatile String lastError = "";
    private MediaProjection projection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private HandlerThread workerThread;
    private Handler worker;
    private int width, height, density;
    private long lastAcceptedAt;
    private int previousSignature;
    private int[] previousSamples;
    private long lastOcrDispatchAt;

    @Override public void onCreate() {
        super.onCreate();
        workerThread = new HandlerThread("UMC-LiveVision");
        workerThread.start();
        worker = new Handler(workerThread.getLooper());
        createNotificationChannel();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || ACTION_STOP.equals(intent.getAction())) { stopCapture(); stopSelf(); return START_NOT_STICKY; }
        if (!ACTION_START.equals(intent.getAction())) return START_NOT_STICKY;
        if (running || projection != null || virtualDisplay != null) stopCapture();
        previousSamples = null; lastAcceptedAt = 0; lastOcrDispatchAt = 0;
        int resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
        Intent data = getIntentExtra(intent, EXTRA_RESULT_DATA);
        configuredFps = Math.max(1, Math.min(5, intent.getIntExtra(EXTRA_FPS, 1)));
        if (data == null || resultCode == 0) { stopSelf(); return START_NOT_STICKY; }
        try {
            if (Build.VERSION.SDK_INT >= 29) startForeground(NOTIFICATION_ID, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
            else startForeground(NOTIFICATION_ID, buildNotification());
            MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            projection = manager.getMediaProjection(resultCode, data);
            if (projection == null) throw new IllegalStateException("MediaProjection permission was not granted");
            projection.registerCallback(new MediaProjection.Callback() {
                @Override public void onStop() { projection = null; stopCapture(); stopSelf(); }
            }, worker);
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (Build.VERSION.SDK_INT >= 30) {
                android.view.Display display = getDisplay();
                if (display != null) display.getRealMetrics(metrics); else wm.getDefaultDisplay().getRealMetrics(metrics);
            } else wm.getDefaultDisplay().getRealMetrics(metrics);
            width = metrics.widthPixels; height = metrics.heightPixels; density = metrics.densityDpi;
            screenWidth = width; screenHeight = height; lastError = "";
            imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 3);
            imageReader.setOnImageAvailableListener(this::onImageAvailable, worker);
            virtualDisplay = projection.createVirtualDisplay("UMC-LiveVision", width, height, density,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, worker);
            running = true;
        } catch (Exception e) {
            lastError = e.getClass().getSimpleName() + ": " + String.valueOf(e.getMessage());
            latestJpeg = null; running = false;
            stopCapture(); stopSelf();
        }
        return START_NOT_STICKY;
    }

    private Intent getIntentExtra(Intent source, String key) {
        if (Build.VERSION.SDK_INT >= 33) return source.getParcelableExtra(key, Intent.class);
        //noinspection deprecation
        return source.getParcelableExtra(key);
    }

    private void onImageAvailable(ImageReader reader) {
        Image image = null;
        try {
            image = reader.acquireLatestImage();
            if (image == null) return;
            long now = System.currentTimeMillis();
            long interval = 1000L / Math.max(1, configuredFps);
            if (now - lastAcceptedAt < interval) return;
            lastAcceptedAt = now;
            Image.Plane plane = image.getPlanes()[0];
            ByteBuffer buffer = plane.getBuffer();
            int pixelStride = plane.getPixelStride();
            int rowStride = plane.getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap padded = Bitmap.createBitmap(width + Math.max(0, rowPadding / pixelStride), height, Bitmap.Config.ARGB_8888);
            buffer.rewind(); padded.copyPixelsFromBuffer(buffer);
            Bitmap frame = Bitmap.createBitmap(padded, 0, 0, width, height);
            padded.recycle();
            int[] samples = samplePixels(frame);
            visualDiffPercent = calculateDiffPercent(previousSamples, samples);
            changed = previousSamples == null || visualDiffPercent >= 7;
            previousSamples = samples;
            previousSignature = visualSignature(frame);
            if (changed && now - lastOcrDispatchAt >= 1100L) {
                VisionAccessibilityService service = VisionAccessibilityService.instance;
                if (service != null) {
                    Bitmap ocrFrame = frame.copy(Bitmap.Config.ARGB_8888, false);
                    if (ocrFrame != null) {
                        lastOcrDispatchAt = now;
                        service.processLiveFrame(ocrFrame, now, visualDiffPercent);
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            frame.compress(Bitmap.CompressFormat.JPEG, 62, out);
            latestJpeg = out.toByteArray(); latestFrameAt = now;
            frame.recycle(); out.close();
        } catch (Exception e) {
            lastError = e.getClass().getSimpleName() + ": " + String.valueOf(e.getMessage());
        } finally { if (image != null) image.close(); }
    }

    // A compact 16x16 luminance sample is used for inexpensive frame-difference detection.
    private int[] samplePixels(Bitmap bitmap) {
        Bitmap sample = Bitmap.createScaledBitmap(bitmap, 16, 16, true);
        int[] values = new int[256];
        for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++) {
            int c = sample.getPixel(x, y);
            int r = (c >> 16) & 255, g = (c >> 8) & 255, b = c & 255;
            values[y * 16 + x] = (r * 30 + g * 59 + b * 11) / 100;
        }
        sample.recycle();
        return values;
    }
    private int calculateDiffPercent(int[] oldValues, int[] newValues) {
        if (oldValues == null || newValues == null || oldValues.length != newValues.length) return 100;
        int changedPixels = 0;
        for (int i = 0; i < oldValues.length; i++) if (Math.abs(oldValues[i] - newValues[i]) >= 18) changedPixels++;
        return (changedPixels * 100) / newValues.length;
    }

    // Hash retained for diagnostics only; this is not semantic/object detection.
    private int visualSignature(Bitmap bitmap) {
        Bitmap sample = Bitmap.createScaledBitmap(bitmap, 24, 24, true);
        int hash = 1;
        for (int y = 0; y < 24; y += 2) for (int x = 0; x < 24; x += 2) hash = 31 * hash + sample.getPixel(x, y);
        sample.recycle(); return hash;
    }

    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= 26) return new Notification.Builder(this, CHANNEL)
                .setContentTitle("UMC Live Vision active").setContentText("Screen capture is running with your permission")
                .setSmallIcon(android.R.drawable.ic_menu_view).setOngoing(true).build();
        //noinspection deprecation
        return new Notification.Builder(this).setContentTitle("UMC Live Vision active")
                .setContentText("Screen capture is running with your permission")
                .setSmallIcon(android.R.drawable.ic_menu_view).setOngoing(true).build();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL, "UMC Live Vision", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }
    }
    private void stopCapture() {
        running = false; latestJpeg = null; latestFrameAt = 0; previousSignature = 0; previousSamples = null; visualDiffPercent = 0;
        if (imageReader != null) { imageReader.setOnImageAvailableListener(null, null); imageReader.close(); imageReader = null; }
        if (virtualDisplay != null) { virtualDisplay.release(); virtualDisplay = null; }
        if (projection != null) { try { projection.stop(); } catch (Exception ignored) {} projection = null; }
        if (Build.VERSION.SDK_INT >= 24) stopForeground(STOP_FOREGROUND_REMOVE); else { //noinspection deprecation
            stopForeground(true); }
    }
    public static JSONObject statusJson() {
        JSONObject o = new JSONObject();
        try { o.put("running", running); o.put("fps_target", configuredFps); o.put("width", screenWidth); o.put("height", screenHeight);
            o.put("last_error", lastError); o.put("latest_frame_at_ms", latestFrameAt); o.put("frame_age_ms", latestFrameAt == 0 ? JSONObject.NULL : System.currentTimeMillis() - latestFrameAt);
            o.put("visual_change_detected", changed); o.put("visual_diff_percent", visualDiffPercent); o.put("ocr_dispatch_interval_ms", 1100); o.put("frame_available", latestJpeg != null); }
        catch (Exception ignored) {} return o;
    }
    public static JSONObject latestFrameJson() {
        JSONObject o = statusJson();
        try { byte[] frame = latestJpeg; if (frame == null) { o.put("status", "no_frame"); return o; }
            o.put("status", "ok"); o.put("mime_type", "image/jpeg"); o.put("encoding", "base64");
            o.put("frame_base64", Base64.encodeToString(frame, Base64.NO_WRAP)); }
        catch (Exception ignored) {} return o;
    }
    @Override public void onDestroy() { stopCapture(); if (workerThread != null) workerThread.quitSafely(); super.onDestroy(); }
    @Override public IBinder onBind(Intent intent) { return null; }
}
