package com.gpsrginfo.universalcontrol;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import org.json.JSONArray;
import org.json.JSONObject;

/** Optional, user-enabled notification reader. No notification data leaves the device automatically. */
public class NotificationAccessService extends NotificationListenerService {
    public static volatile NotificationAccessService instance;

    @Override public void onListenerConnected() {
        super.onListenerConnected();
        instance = this;
    }

    @Override public void onListenerDisconnected() {
        instance = null;
        super.onListenerDisconnected();
    }

    @Override public void onDestroy() {
        if (instance == this) instance = null;
        super.onDestroy();
    }

    public static JSONObject snapshot() {
        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        NotificationAccessService service = instance;
        try {
            result.put("status", service == null ? "permission_not_enabled_or_disconnected" : "ok");
            if (service != null) {
                StatusBarNotification[] active = service.getActiveNotifications();
                if (active != null) {
                    for (StatusBarNotification sbn : active) {
                        if (sbn == null || sbn.getNotification() == null) continue;
                        Notification n = sbn.getNotification();
                        JSONObject item = new JSONObject();
                        item.put("package", sbn.getPackageName());
                        item.put("key", sbn.getKey());
                        item.put("posted_at_ms", sbn.getPostTime());
                        item.put("title", text(n.extras.getCharSequence(Notification.EXTRA_TITLE)));
                        CharSequence body = n.extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
                        if (body == null) body = n.extras.getCharSequence(Notification.EXTRA_TEXT);
                        item.put("text", text(body));
                        item.put("sub_text", text(n.extras.getCharSequence(Notification.EXTRA_SUB_TEXT)));
                        items.put(item);
                    }
                }
            }
            result.put("count", items.length());
            result.put("notifications", items);
        } catch (Exception e) {
            try { result.put("status", "error"); result.put("message", e.getMessage()); } catch (Exception ignored) { }
        }
        return result;
    }

    private static String text(CharSequence value) {
        return value == null ? "" : value.toString();
    }
}
