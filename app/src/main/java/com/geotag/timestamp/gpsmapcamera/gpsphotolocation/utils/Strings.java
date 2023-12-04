package com.geotag.timestamp.gpsmapcamera.gpsphotolocation.utils;

import android.text.TextUtils;

import com.google.gson.Gson;

public final class Strings {
    public static final boolean DEBUG = Boolean.parseBoolean("true");
    public static void log(Object o) {
        if (DEBUG) log("", o);
    }

    public static void log(String message, Object o) {

        String json = "";
        if (o instanceof String) {
            json = (String) o;
        } else {
            json = (o == null ? "NULL" : new Gson().toJson(o));
        }
        int maxLogSize = 2600;
        int partSize = json.length() / maxLogSize;
        if (partSize > 0 && DEBUG)
            android.util.Log.d("TESTAPP", (TextUtils.isEmpty(message) ? "" : (message + ": ")) + "Size: " + json.length());

        for (int i = 0; i <= partSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = Math.min(end, json.length());
            if (DEBUG) android.util.Log.d("TESTAPP",
                    (TextUtils.isEmpty(message)
                            ? "" : (message + ": "))
                            + (partSize > 0 ? "PART " + (i + 1) + ": " : "")
                            + json.substring(start, end));
        }
    }
}
