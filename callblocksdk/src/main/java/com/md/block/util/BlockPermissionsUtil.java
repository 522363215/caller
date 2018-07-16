package com.md.block.util;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.md.block.core.BlockManager;
import com.md.block.core.local.BlockLocal;

/**
 * Created by Zhq on 2017/12/21.
 */

public class BlockPermissionsUtil {
    private static final String TAG = "BlockPermissionsUtil";

    public static boolean isNotificationServiceRunning() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Context context = BlockManager.getInstance().getAppContext();
            if (context != null) {
                ContentResolver contentResolver = context.getContentResolver();
                String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
                String packageName = context.getPackageName();
                return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isCallerNotificationServiceRunning() {
        Boolean aBoolean = BlockLocal.getPreferencesData("is_cc_callernotificationlistenerservice_running", false);
        return aBoolean == null ? false : aBoolean;
    }

}
