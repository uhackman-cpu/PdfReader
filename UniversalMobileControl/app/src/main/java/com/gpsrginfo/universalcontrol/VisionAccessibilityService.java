package com.gpsrginfo.universalcontrol;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityService.ScreenshotResult;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Path;
import android.accessibilityservice.GestureDescription;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class VisionAccessibilityService extends AccessibilityService {
    public static volatile VisionAccessibilityService instance;
    public static volatile String latestJson = "{\"status\":\"not_scanned\",\"elements\":[],\"ocr\":[]}";
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private HandlerThread gestureCallbackThread;
    private Handler gestureCallbackHandler;
    private final Handler actionOcrHandler = new Handler(Looper.getMainLooper());
    private volatile boolean actionMode = false;
    private volatile boolean actionOcrRunning = false;
    private volatile long actionOcrIntervalMs = 3000L;
    private volatile boolean serverStarted = false;
    private volatile ServerSocket bridgeServer;
    private volatile Thread bridgeThread;
    private volatile long screenRevision = 0L;

    @Override public void onServiceConnected() {
        super.onServiceConnected(); instance = this;
        gestureCallbackThread = new HandlerThread("UMC-GestureCallbacks");
        gestureCallbackThread.start();
        gestureCallbackHandler = new Handler(gestureCallbackThread.getLooper());
        startLocalBridge();
    }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) { }
    @Override public void onInterrupt() { }
    @Override public void onDestroy() {
        stopActionMode();
        instance = null;
        ServerSocket activeServer = bridgeServer;
        if (activeServer != null) {
            try { activeServer.close(); } catch (Exception ignored) { }
        }
        Thread activeBridgeThread = bridgeThread;
        if (activeBridgeThread != null) activeBridgeThread.interrupt();
        serverStarted = false;
        if (gestureCallbackThread != null) gestureCallbackThread.quitSafely();
        super.onDestroy();
    }

    public void scanScreen() {
        if (Build.VERSION.SDK_INT < 30) { MainActivity.setOutput("Screenshot API requires Android 11 or newer."); return; }
        try {
            takeScreenshot(Display.DEFAULT_DISPLAY, executor, new TakeScreenshotCallback() {
                @Override public void onSuccess(ScreenshotResult result) {
                    Bitmap bitmap = Bitmap.wrapHardwareBuffer(result.getHardwareBuffer(), result.getColorSpace());
                    result.getHardwareBuffer().close();
                    if (bitmap == null) { MainActivity.setOutput("Screenshot returned no bitmap."); return; }
                    Bitmap software = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                    bitmap.recycle();
                    JSONObject root = new JSONObject();
                    try { root.put("status","scanning"); root.put("width",software.getWidth()); root.put("height",software.getHeight()); root.put("timestamp_ms",System.currentTimeMillis()); appendAppContext(root); root.put("ui_elements",collectUiTree()); root.put("ocr",new JSONArray()); } catch (Exception ignored) { }
                    final JSONObject payload=root;
                    TextRecognizer latin=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                    TextRecognizer devanagari=TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
                    InputImage image=InputImage.fromBitmap(software,0);
                    latin.process(image).addOnSuccessListener(t->appendOcr(payload,t,"latin")).addOnFailureListener(e->appendError(payload,"latin",e.getMessage()))
                        .addOnCompleteListener(task1->devanagari.process(image).addOnSuccessListener(t->appendOcr(payload,t,"devanagari")).addOnFailureListener(e->appendError(payload,"devanagari",e.getMessage())).addOnCompleteListener(task2->{
                            try { payload.put("status","ok"); payload.put("completed_ms",System.currentTimeMillis()); latestJson=payload.toString(2); screenRevision++; saveRecentOcr(latestJson); } catch(Exception ignored) { latestJson=payload.toString(); screenRevision++; saveRecentOcr(latestJson); }
                            latin.close(); devanagari.close(); software.recycle(); MainActivity.setOutput(latestJson);
                        }));
                }
                @Override public void onFailure(int errorCode) { MainActivity.setOutput("Screenshot failed. Error code: " + errorCode + ". Check Accessibility screenshot capability."); }
            });
        } catch(Exception e) { MainActivity.setOutput("Scan error: " + e.getMessage()); }
    }

    /** Process a MediaProjection frame only after the live visual-diff gate has fired. */
    public void processLiveFrame(Bitmap frame, long capturedAt, int diffPercent) {
        if (frame == null) return;
        handler.post(() -> {
            JSONObject payload = new JSONObject();
            try {
                payload.put("status", "live_ocr_processing");
                payload.put("source", "media_projection");
                payload.put("width", frame.getWidth());
                payload.put("height", frame.getHeight());
                payload.put("timestamp_ms", capturedAt);
                payload.put("visual_diff_percent", diffPercent);
                appendAppContext(payload);
                payload.put("ui_elements", collectUiTree());
                payload.put("ocr", new JSONArray());
            } catch (Exception ignored) { }
            TextRecognizer latin = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            TextRecognizer devanagari = TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
            InputImage image = InputImage.fromBitmap(frame, 0);
            latin.process(image)
                .addOnSuccessListener(t -> appendOcr(payload, t, "latin"))
                .addOnFailureListener(e -> appendError(payload, "latin", e.getMessage()))
                .addOnCompleteListener(task1 -> devanagari.process(image)
                    .addOnSuccessListener(t -> appendOcr(payload, t, "devanagari"))
                    .addOnFailureListener(e -> appendError(payload, "devanagari", e.getMessage()))
                    .addOnCompleteListener(task2 -> {
                        try {
                            payload.put("status", "ok");
                            payload.put("completed_ms", System.currentTimeMillis());
                            latestJson = payload.toString(2);
                            screenRevision++;
                            saveRecentOcr(latestJson);
                        } catch (Exception ignored) { latestJson = payload.toString(); screenRevision++; }
                        latin.close(); devanagari.close();
                        frame.recycle();
                        MainActivity.setOutput(latestJson);
                    }));
        });
    }

    /** Click the first visible enabled node matching text/content-description. */
    private JSONObject clickByText(String query) {
        if (Looper.myLooper() == handler.getLooper()) return clickByTextOnMain(query);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<JSONObject> response = new AtomicReference<>(result(false, "element click timed out"));
        handler.post(() -> { try { response.set(clickByTextOnMain(query)); } finally { latch.countDown(); } });
        try { latch.await(4, TimeUnit.SECONDS); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); return result(false, "element click interrupted"); }
        return response.get();
    }

    private JSONObject clickByTextOnMain(String query) {
        JSONObject out = new JSONObject();
        if (query == null || query.trim().isEmpty()) return result(false, "text query is empty");
        startActionMode();
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return result(false, "active window is unavailable");
        try {
            java.util.List<AccessibilityNodeInfo> matches = root.findAccessibilityNodeInfosByText(query.trim());
            if (matches != null) {
                for (AccessibilityNodeInfo match : matches) {
                    if (match == null) continue;
                    AccessibilityNodeInfo target = match;
                    java.util.ArrayList<AccessibilityNodeInfo> ancestors = new java.util.ArrayList<>();
                    while (target != null && !target.isClickable()) {
                        AccessibilityNodeInfo parent = target.getParent();
                        if (target != match) ancestors.add(target);
                        target = parent;
                    }
                    if (target != null && target.isEnabled()) {
                        Rect bounds = new Rect(); target.getBoundsInScreen(bounds);
                        boolean clicked = target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        try {
                            out.put("status", clicked ? "dispatched" : "error");
                            out.put("message", clicked ? "click action dispatched" : "node rejected click action");
                            out.put("matched_text", match.getText() == null ? query : match.getText().toString());
                            out.put("bounds", new JSONObject().put("left", bounds.left).put("top", bounds.top).put("right", bounds.right).put("bottom", bounds.bottom));
                        } catch (Exception ignored) { }
                        if (target != match) target.recycle();
                        for (AccessibilityNodeInfo ancestor : ancestors) ancestor.recycle();
                        match.recycle();
                        recordControlEvent("tap_text", out);
                        actionCompletedRefresh();
                        return out;
                    }
                    if (target != null && target != match) target.recycle();
                    for (AccessibilityNodeInfo ancestor : ancestors) ancestor.recycle();
                    match.recycle();
                }
            }
        } catch (Exception e) {
            return result(false, "element search failed: " + e.getMessage());
        } finally { root.recycle(); }
        return result(false, "no clickable element matched: " + query);
    }

    private JSONObject launchApp(String packageName) {
        if (packageName == null || !packageName.matches("[A-Za-z0-9_]+(\\.[A-Za-z0-9_]+)+"))
            return result(false, "invalid package name");
        try {
            android.content.Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
            if (launch == null) return result(false, "no launchable activity for package: " + packageName);
            launch.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
            JSONObject event = new JSONObject(); event.put("package", packageName); recordControlEvent("launch", event);
            return result(true, "launch intent dispatched for " + packageName);
        } catch (Exception e) { return result(false, "app launch failed: " + e.getMessage()); }
    }

    private volatile boolean flowRunning = false;
    private volatile boolean flowStopRequested = false;
    private volatile String flowStatus = "idle";
    private volatile int flowStep = 0;
    private void startFlow(String encodedSteps) throws Exception {
        if (flowRunning) throw new IllegalStateException("a flow is already running");
        JSONArray steps = new JSONArray(encodedSteps);
        if (steps.length() == 0 || steps.length() > 20) throw new IllegalArgumentException("flow must contain 1..20 steps");
        flowStopRequested = false; flowRunning = true; flowStep = 0; flowStatus = "running";
        JSONObject flowStart = new JSONObject(); flowStart.put("step_count", steps.length()); recordControlEvent("flow_started", flowStart);
        Thread runner = new Thread(() -> {
            try {
                for (int i = 0; i < steps.length() && !flowStopRequested; i++) {
                    flowStep = i + 1;
                    JSONObject step = steps.optJSONObject(i);
                    if (step == null) throw new IllegalArgumentException("step " + (i + 1) + " is not an object");
                    String action = step.optString("action", "");
                    JSONObject stepEvent = new JSONObject(); stepEvent.put("step", i + 1); stepEvent.put("action", action); recordControlEvent("flow_step_started", stepEvent);
                    long beforeRevision = screenRevision;
                    String outcome;
                    if ("tap_text".equals(action)) {
                        JSONObject r = clickByText(step.optString("text", ""));
                        outcome = r.optString("status", "error");
                        if (!"dispatched".equals(outcome)) throw new IllegalStateException(r.optString("message", "tap_text failed"));
                    } else if ("tap".equals(action)) {
                        double x = step.optDouble("x", -1), y = step.optDouble("y", -1);
                        if (x < 0 || y < 0) throw new IllegalArgumentException("tap requires non-negative x and y");
                        outcome = tapAt((float) x, (float) y);
                        if (!"tap completed".equals(outcome)) throw new IllegalStateException(outcome);
                    } else if ("swipe".equals(action)) {
                        double x1=step.optDouble("x1",-1), y1=step.optDouble("y1",-1), x2=step.optDouble("x2",-1), y2=step.optDouble("y2",-1);
                        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0) throw new IllegalArgumentException("swipe requires non-negative x1,y1,x2,y2");
                        outcome = swipeFromTo((float)x1,(float)y1,(float)x2,(float)y2,step.optLong("duration",350));
                        if (!"swipe completed".equals(outcome)) throw new IllegalStateException(outcome);
                    } else if ("back".equals(action)) {
                        if (!performGlobalAction(GLOBAL_ACTION_BACK)) throw new IllegalStateException("back action failed");
                    } else if ("wait".equals(action)) {
                        Thread.sleep(Math.max(100, Math.min(10000, step.optLong("ms", 500))));
                    } else if ("launch".equals(action)) {
                        JSONObject r = launchApp(step.optString("package", ""));
                        if (!"ok".equals(r.optString("status"))) throw new IllegalStateException(r.optString("message", "launch failed"));
                    } else throw new IllegalArgumentException("unsupported flow action: " + action);
                    if (!"wait".equals(action)) {
                        Thread.sleep(650);
                        handler.post(this::scanScreen);
                        long until = System.currentTimeMillis() + 2500;
                        while (!flowStopRequested && System.currentTimeMillis() < until && screenRevision <= beforeRevision) Thread.sleep(100);
                        if (screenRevision <= beforeRevision) throw new IllegalStateException("post-action screen refresh was not observed");
                        stepEvent.put("screen_revision", screenRevision);
                        stepEvent.put("screen_refresh_observed", true);
                        stepEvent.put("verified", false);
                        stepEvent.put("verification", "screen refresh observed; target postcondition not checked");
                        recordControlEvent("flow_step_refresh_observed", stepEvent);
                    }
                }
                flowStatus = flowStopRequested ? "stopped" : "completed";
                recordControlEvent("flow_finished", new JSONObject().put("status", flowStatus));
            } catch (Exception e) { flowStatus = "error: " + e.getMessage(); recordControlEvent("flow_error", result(false, flowStatus)); }
            finally { flowRunning = false; }
        }, "UMC-FlowRunner");
        runner.setDaemon(true); runner.start();
    }

    public void startControlSession() { startActionMode(); }
    public void stopControlSession() { stopActionMode(); }

    private void startActionMode() {
        actionMode=true;
        if(!actionOcrRunning){ actionOcrRunning=true; runActionOcrNow(); }
    }
    private void runActionOcrNow(){
        if(!actionMode){ actionOcrRunning=false; return; }
        scanScreen();
        actionOcrHandler.postDelayed(this::runActionOcrNow, actionOcrIntervalMs);
    }
    private void stopActionMode(){
        actionMode=false; actionOcrRunning=false; actionOcrHandler.removeCallbacksAndMessages(null);
    }
    private void actionCompletedRefresh(){
        if(!actionMode) return;
        actionOcrHandler.removeCallbacksAndMessages(null);
        actionOcrHandler.postDelayed(()->{
            if(actionMode){ scanScreen(); actionOcrHandler.postDelayed(this::runActionOcrNow, actionOcrIntervalMs); }
        },400);
    }

    private void appendAppContext(JSONObject payload) {
        AccessibilityNodeInfo root = null;
        try {
            root = getRootInActiveWindow();
            if (root != null) {
                CharSequence pkg = root.getPackageName();
                CharSequence cls = root.getClassName();
                payload.put("active_package", pkg == null ? "" : pkg.toString());
                payload.put("active_class", cls == null ? "" : cls.toString());
            }
        } catch (Exception ignored) { }
        finally { if (root != null) root.recycle(); }
    }

    private void recordControlEvent(String event, JSONObject details) {
        try {
            java.io.File dir = new java.io.File(getExternalFilesDir(null), "sessions");
            if (!dir.exists()) dir.mkdirs();
            java.io.File file = new java.io.File(dir, "control_session.jsonl");
            JSONObject row = new JSONObject();
            row.put("timestamp_ms", System.currentTimeMillis());
            row.put("event", event);
            row.put("details", details == null ? new JSONObject() : details);
            java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file, true));
            writer.write(row.toString()); writer.newLine(); writer.close();
            if (file.length() > 1024 * 1024) {
                java.io.File rotated = new java.io.File(dir, "control_session.previous.jsonl");
                if (rotated.exists()) rotated.delete();
                file.renameTo(rotated);
            }
        } catch (Exception ignored) { }
    }

    private JSONObject recentControlEvents() {
        JSONObject result = new JSONObject(); JSONArray events = new JSONArray();
        try {
            java.io.File file = new java.io.File(new java.io.File(getExternalFilesDir(null), "sessions"), "control_session.jsonl");
            if (file.exists()) {
                java.util.ArrayList<String> lines = new java.util.ArrayList<>();
                BufferedReader reader = new BufferedReader(new java.io.FileReader(file));
                String line; while ((line = reader.readLine()) != null) lines.add(line); reader.close();
                int start = Math.max(0, lines.size() - 100);
                for (int i = start; i < lines.size(); i++) events.put(new JSONObject(lines.get(i)));
            }
            result.put("status", "ok"); result.put("count", events.length()); result.put("events", events);
        } catch (Exception e) { try { result.put("status", "error"); result.put("message", e.getMessage()); } catch (Exception ignored) { } }
        return result;
    }

    private JSONArray collectUiTree(){
        JSONArray arr=new JSONArray(); int[] count={0};
        try {
            for(AccessibilityWindowInfo window:getWindows()) {
                AccessibilityNodeInfo root=window.getRoot();
                if(root!=null){ walk(root,arr,count,0); root.recycle(); }
                window.recycle();
            }
        } catch(Exception ignored){}
        return arr;
    }
    private void walk(AccessibilityNodeInfo node,JSONArray arr,int[] count,int depth){
        if(node==null||count[0]>=500||depth>35)return;
        try {
            CharSequence txt=node.getText(),desc=node.getContentDescription(); Rect b=new Rect(); node.getBoundsInScreen(b);
            boolean meaningful=(txt!=null&&txt.length()>0)||(desc!=null&&desc.length()>0)||node.isClickable()||node.isEditable();
            if(meaningful){ String type=node.getClassName()==null?"unknown":node.getClassName().toString(); String text=txt==null?"":txt.toString(); String description=desc==null?"":desc.toString(); String stable=type+"|"+text+"|"+description+"|"+b.flattenToString(); JSONObject o=new JSONObject(); o.put("id","ui_"+count[0]++); o.put("stable_key",Integer.toHexString(stable.hashCode())); o.put("type",type); o.put("text",text); o.put("description",description); o.put("bounds",new JSONObject().put("left",b.left).put("top",b.top).put("right",b.right).put("bottom",b.bottom)); o.put("clickable",node.isClickable()); o.put("editable",node.isEditable()); o.put("enabled",node.isEnabled()); arr.put(o); }
            for(int i=0;i<node.getChildCount();i++){ AccessibilityNodeInfo child=node.getChild(i); if(child!=null){ walk(child,arr,count,depth+1); child.recycle(); } }
        } catch(Exception ignored){}
    }
    private void appendOcr(JSONObject payload,Text result,String engine){ try{ JSONArray arr=payload.optJSONArray("ocr"); if(arr==null){arr=new JSONArray();payload.put("ocr",arr);} for(Text.TextBlock block:result.getTextBlocks()) for(Text.Line line:block.getLines()){ Rect b=line.getBoundingBox(); if(b==null)continue; JSONObject item=new JSONObject(); item.put("text",line.getText()); item.put("engine",engine); item.put("bounds",new JSONObject().put("left",b.left).put("top",b.top).put("right",b.right).put("bottom",b.bottom)); item.put("center",new JSONObject().put("x",b.centerX()).put("y",b.centerY())); arr.put(item); } }catch(Exception ignored){} }
    private void appendError(JSONObject payload,String engine,String message){ try{payload.put(engine+"_error",message==null?"unknown":message);}catch(Exception ignored){} }
    public static String getRecentOcrForDisplay() { return readRecentOcr(); }
    private static String readRecentOcr() {
        try {
            VisionAccessibilityService s = instance;
            if (s == null) return "Accessibility service is not running.";
            java.io.File dir = new java.io.File(s.getExternalFilesDir(null), "ocr_history");
            java.io.File file = new java.io.File(dir, "recent_20_ocr.jsonl");
            if (!file.exists()) return "No OCR history saved yet.";
            StringBuilder out = new StringBuilder();
            BufferedReader br = new BufferedReader(new java.io.FileReader(file));
            String line; int n=0;
            while ((line=br.readLine()) != null) { n++; out.append("OCR #").append(n).append("\n").append(line).append("\n\n"); }
            br.close();
            return out.toString();
        } catch (Exception e) { return "Unable to read OCR history: " + e.getMessage(); }
    }
    private void saveRecentOcr(String json) {
        try {
            java.io.File dir = new java.io.File(getExternalFilesDir(null), "ocr_history");
            if (!dir.exists()) dir.mkdirs();
            java.io.File file = new java.io.File(dir, "recent_20_ocr.jsonl");
            java.util.ArrayList<String> lines = new java.util.ArrayList<>();
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new java.io.FileReader(file));
                String line; while ((line = br.readLine()) != null) lines.add(line); br.close();
            }
            lines.add(System.currentTimeMillis() + "\t" + json.replace("\n", "\\n"));
            while (lines.size() > 20) lines.remove(0);
            java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(file, false));
            for (String line : lines) { bw.write(line); bw.newLine(); }
            bw.close();
        } catch (Exception ignored) { }
    }


    private String tapAt(float x,float y){
        if(Build.VERSION.SDK_INT<24)return "tap unsupported on this Android version";
        startActionMode(); Path p=new Path(); p.moveTo(x,y); CountDownLatch done=new CountDownLatch(1); AtomicReference<String> outcome=new AtomicReference<>("tap callback timeout");
        boolean accepted=dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(p,0,80)).build(),new GestureResultCallback(){
            @Override public void onCompleted(GestureDescription gesture){ outcome.set("tap completed"); done.countDown(); actionCompletedRefresh(); }
            @Override public void onCancelled(GestureDescription gesture){ outcome.set("tap cancelled"); done.countDown(); actionCompletedRefresh(); }
        },gestureCallbackHandler);
        if(!accepted){actionCompletedRefresh();return "tap rejected by AccessibilityService";}
        try{done.await(3,TimeUnit.SECONDS);}catch(InterruptedException e){Thread.currentThread().interrupt();actionCompletedRefresh();return "tap wait interrupted";} JSONObject event=new JSONObject(); try{event.put("x",x);event.put("y",y);event.put("result",outcome.get());}catch(Exception ignored){} recordControlEvent("tap",event); return outcome.get();
    }
    private String swipeFromTo(float x1,float y1,float x2,float y2,long duration){
        if(Build.VERSION.SDK_INT<24)return "swipe unsupported on this Android version";
        startActionMode(); Path p=new Path(); p.moveTo(x1,y1); p.lineTo(x2,y2); long d=Math.max(100,Math.min(duration,5000)); CountDownLatch done=new CountDownLatch(1); AtomicReference<String> outcome=new AtomicReference<>("swipe callback timeout");
        boolean accepted=dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription(p,0,d)).build(),new GestureResultCallback(){
            @Override public void onCompleted(GestureDescription gesture){outcome.set("swipe completed");done.countDown();actionCompletedRefresh();}
            @Override public void onCancelled(GestureDescription gesture){outcome.set("swipe cancelled");done.countDown();actionCompletedRefresh();}
        },gestureCallbackHandler);
        if(!accepted){actionCompletedRefresh();return "swipe rejected by AccessibilityService";}
        try{done.await(6,TimeUnit.SECONDS);}catch(InterruptedException e){Thread.currentThread().interrupt();actionCompletedRefresh();return "swipe wait interrupted";} JSONObject event=new JSONObject(); try{event.put("x1",x1);event.put("y1",y1);event.put("x2",x2);event.put("y2",y2);event.put("duration",d);event.put("result",outcome.get());}catch(Exception ignored){} recordControlEvent("swipe",event); return outcome.get();
    }
    private boolean setFocusedText(String value){
        if(!actionMode)startActionMode(); AccessibilityNodeInfo node=findFocus(AccessibilityNodeInfo.FOCUS_INPUT); if(node==null||!node.isEditable()){if(node!=null)node.recycle();actionCompletedRefresh();return false;}
        Bundle args=new Bundle(); args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,value==null?"":value); boolean ok=node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,args); node.recycle(); JSONObject event=new JSONObject(); try{event.put("result",ok?"text_set":"failed");event.put("text_length",value==null?0:value.length());}catch(Exception ignored){} recordControlEvent("type",event); actionCompletedRefresh(); return ok;
    }
    private Map<String,String> queryParams(String target){ Map<String,String> out=new HashMap<>(); int q=target.indexOf("?"); if(q<0)return out; for(String part:target.substring(q+1).split("&")){String[] kv=part.split("=",2);try{out.put(URLDecoder.decode(kv[0],"UTF-8"),URLDecoder.decode(kv.length>1?kv[1]:"","UTF-8"));}catch(Exception ignored){}}return out; }
    private JSONObject result(boolean ok,String message){JSONObject o=new JSONObject();try{o.put("status",ok?"ok":"error");o.put("message",message);}catch(Exception ignored){}return o;}
    private float param(Map<String,String> q,String key){if(!q.containsKey(key))throw new IllegalArgumentException("missing parameter "+key);return Float.parseFloat(q.get(key));}
    private void startLocalBridge() {
        if (serverStarted) return;
        serverStarted = true;
        Thread thread = new Thread(() -> {
            ServerSocket server = null;
            try {
                server = new ServerSocket(8765, 20, java.net.InetAddress.getByName("127.0.0.1"));
                bridgeServer = server;
                while (!Thread.currentThread().isInterrupted() && !server.isClosed()) {
                    try (Socket client = server.accept()) {
                        client.setSoTimeout(2000);
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                        String request = in.readLine();
                        if (request == null) continue;
                        String[] parts = request.split(" ");
                        String target = parts.length > 1 ? parts[1] : "/health";
                        String line;
                        String authorization = null;
                        while ((line = in.readLine()) != null && !line.isEmpty()) {
                            if (line.regionMatches(true, 0, "Authorization:", 0, 14)) authorization = line.substring(14).trim();
                        }
                        String path = target.split("\\?", 2)[0];
                        Map<String, String> q = queryParams(target);
                        JSONObject body;
                        int httpStatus = 200;
                        try {
                            if (!path.equals("/health") && !BridgeAuth.isAuthorized(this, authorization)) {
                                httpStatus = 401;
                                body = result(false, "unauthorized: send Authorization: Bearer <token> from the UMC app");
                            } else if (path.equals("/health")) body = result(true, "universal-mobile-control ready");
                            else if (path.equals("/screen")) { try { body = new JSONObject(latestJson); } catch (Exception e) { body = result(false, "screen data unavailable"); } }
                            else if (path.equals("/live/status")) body = LiveVisionService.statusJson();
                            else if (path.equals("/live/frame")) body = LiveVisionService.latestFrameJson();
                            else if (path.equals("/tap_text")) body = clickByText(q.get("text"));
                            else if (path.equals("/launch")) body = launchApp(q.get("package"));
                            else if (path.equals("/device/info")) body = new JSONObject().put("status", "ok").put("manufacturer", Build.MANUFACTURER).put("model", Build.MODEL).put("android_release", Build.VERSION.RELEASE).put("sdk", Build.VERSION.SDK_INT).put("package", getPackageName());
                            else if (path.equals("/notifications")) body = NotificationAccessService.snapshot();
                            else if (path.equals("/session/history")) body = recentControlEvents();
                            else if (path.equals("/flow/run")) { startFlow(q.get("steps")); body = result(true, "flow accepted"); }
                            else if (path.equals("/flow/status")) { body = result(true, flowStatus); body.put("running", flowRunning); body.put("step", flowStep); }
                            else if (path.equals("/flow/stop")) { flowStopRequested = true; stopActionMode(); recordControlEvent("flow_stop_requested", null); body = result(true, "flow stop requested"); }
                            else if (path.equals("/emergency/stop")) { flowStopRequested = true; stopActionMode(); stopService(new android.content.Intent(this, LiveVisionService.class)); recordControlEvent("emergency_stop", null); body = result(true, "flow and live vision stop requested"); }
                            else if (path.equals("/scan")) { startActionMode(); handler.post(this::scanScreen); body = result(true, "scan requested; Action Mode active; poll /screen"); }
                            else if (path.equals("/action/start")) { startActionMode(); body = result(true, "Action Mode started; OCR refresh every " + actionOcrIntervalMs + " ms"); }
                            else if (path.equals("/action/stop")) { stopActionMode(); body = result(true, "Action Mode stopped"); }
                            else if (path.equals("/action/status")) { body = result(true, actionMode ? "Action Mode active" : "Action Mode inactive"); body.put("action_mode", actionMode); body.put("ocr_interval_ms", actionOcrIntervalMs); body.put("ocr_running", actionOcrRunning); }
                            else if (path.equals("/tap")) { String outcome = tapAt(param(q, "x"), param(q, "y")); body = result("tap completed".equals(outcome), outcome); }
                            else if (path.equals("/swipe")) { long duration = q.containsKey("duration") ? Long.parseLong(q.get("duration")) : 350; String outcome = swipeFromTo(param(q, "x1"), param(q, "y1"), param(q, "x2"), param(q, "y2"), duration); body = result("swipe completed".equals(outcome), outcome); }
                            else if (path.equals("/back")) { startActionMode(); boolean ok = performGlobalAction(GLOBAL_ACTION_BACK); recordControlEvent("back", result(ok, ok ? "dispatched" : "failed")); actionCompletedRefresh(); body = result(ok, ok ? "back dispatched" : "back failed"); }
                            else if (path.equals("/type")) { boolean ok = setFocusedText(q.get("text")); body = result(ok, ok ? "text set on focused field" : "no editable field focused or action unsupported"); }
                            else body = result(false, "unknown endpoint");
                        } catch (Exception e) { body = result(false, "invalid parameters: " + e.getMessage()); }
                        byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
                        OutputStream out = client.getOutputStream();
                        String reason = httpStatus == 200 ? "OK" : "Unauthorized";
                        String headers = "HTTP/1.1 " + httpStatus + " " + reason + "\r\nContent-Type: application/json; charset=utf-8\r\nCache-Control: no-store\r\nContent-Length: " + bytes.length + "\r\nConnection: close\r\n\r\n";
                        out.write(headers.getBytes(StandardCharsets.UTF_8)); out.write(bytes); out.flush();
                    } catch (java.net.SocketException closed) {
                        if (server.isClosed() || Thread.currentThread().isInterrupted()) break;
                    } catch (Exception ignored) { }
                }
            } catch (Exception e) {
                if (server == null || !server.isClosed()) latestJson = "{\"status\":\"bridge_error\",\"message\":\"local bridge could not bind port 8765\"}";
            } finally {
                if (server != null) { try { server.close(); } catch (Exception ignored) { } }
                if (bridgeServer == server) bridgeServer = null;
                if (bridgeThread == Thread.currentThread()) bridgeThread = null;
                serverStarted = false;
            }
        }, "UniversalMobileControl-LocalBridge");
        thread.setDaemon(true);
        bridgeThread = thread;
        thread.start();
    }
}