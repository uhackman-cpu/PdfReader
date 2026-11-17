package com.gpsrginfo.universalcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.SecureRandom;
import java.util.Base64;

/** Per-install bearer token for the loopback HTTP bridge. */
public final class BridgeAuth {
    private static final String PREFS = "umc_bridge_security";
    private static final String KEY = "bridge_token";
    private BridgeAuth() { }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY, null);
        if (token != null && token.length() >= 32) return token;
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        prefs.edit().putString(KEY, token).apply();
        return token;
    }

    public static boolean isAuthorized(Context context, String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) return false;
        String supplied = authorization.substring(7).trim();
        String expected = getToken(context);
        if (supplied.length() != expected.length()) return false;
        int diff = 0;
        for (int i = 0; i < supplied.length(); i++) diff |= supplied.charAt(i) ^ expected.charAt(i);
        return diff == 0;
    }
}
