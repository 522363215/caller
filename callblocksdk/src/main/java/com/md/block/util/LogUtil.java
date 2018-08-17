package com.md.block.util;

import android.util.Log;


public class LogUtil {

    public static final boolean IS_SHOW_LOG = false;

    public static void d(String TAG, String msg) {
        if (IS_SHOW_LOG)
            Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (IS_SHOW_LOG)
            Log.e(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        if (IS_SHOW_LOG)
            Log.i(TAG, msg);
    }

    public static void v(String TAG, String msg) {
        if (IS_SHOW_LOG)
            Log.v(TAG, msg);
    }
    public static void js(String msg) {
        if (IS_SHOW_LOG)
            Log.i("info---", msg);
    }

    public static void error (Exception e) {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
        StringBuffer toStringBuffer = new StringBuffer("[").append(
                traceElement.getFileName()).append(" | ").append(
                traceElement.getLineNumber()).append(" | ").append(
                traceElement.getMethodName()).append("]");
        String str = android.os.Process.myPid() + toStringBuffer.toString() + " ";
        LogUtil.e("error", str + e.getMessage());
    }
}
