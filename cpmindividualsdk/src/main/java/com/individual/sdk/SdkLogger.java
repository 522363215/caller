package com.individual.sdk;

import android.util.Log;

public class SdkLogger {

    // FIXME 提交时记得关闭log
    private static final boolean LOG_ENABLE = false;

    public static void d(String TAG, String msg) {
        if (LOG_ENABLE)
            Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (LOG_ENABLE)
            Log.e(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        if (LOG_ENABLE)
            Log.i(TAG, msg);
    }

    public static void v(String TAG, String msg) {
        if (LOG_ENABLE)
            Log.v(TAG, msg);
    }

}
