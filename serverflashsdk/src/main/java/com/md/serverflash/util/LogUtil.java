package com.md.serverflash.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.md.serverflash.ThemeSyncManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class LogUtil {

    public static final boolean IS_SHOW_LOG = true;
    public static final String TAG = "flash_show_sdk";

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

    public static void printToLocalFile (String msg) {
        Context context = ThemeSyncManager.getInstance().getContext();
        if (context == null || TextUtils.isEmpty(msg)) {
            return;
        }

        File file = new File(context.getExternalFilesDir(null), "logcat.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            out.write(msg, 0, msg.length());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
