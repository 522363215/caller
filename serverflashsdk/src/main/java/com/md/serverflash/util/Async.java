package com.md.serverflash.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by ChenR on 2018/5/10.
 */

public class Async {
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    public static void runOnUiThread(Runnable runnable) {
        sHandler.post(runnable);
    }
}
