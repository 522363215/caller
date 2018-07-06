package com.md.flashset;

import android.content.Context;

import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;

public class CallFlashSet {
    public static void init(Context context) {
        CallFlashPreferenceHelper.init(context);
        CallFlashManager.getInstance().init(context);
    }
}
