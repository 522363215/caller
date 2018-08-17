package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.content.Context;
import android.os.Build;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;

public class GuideUtil {
    /**
     * 首次启动的引导
     */
    public static void toFirstBootGuide(Context context) {
        boolean isShowCallFlashSetGuide = PreferenceHelper.getBoolean(PreferenceHelper.PREF_KEY_IS_SHOW_CALL_FLASH_SET_GUIDE, true);
        if (isShowCallFlashSetGuide) {
            toCallFlashSetGuide(context);
        } else {
            toPermissionGuide(context);
        }
    }

    public static void toPermissionGuide(Context context) {
        boolean isShowPermissionActivity = PreferenceHelper.getLong(PreferenceHelper.PREF_LAST_REQUEST_PERMISSION_TIME, 0) <= 0;
        if (isShowPermissionActivity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_REQUEST_PERMISSION_TIME, System.currentTimeMillis());
            if (!PermissionUtils.isHaveAllPermission(context)) {
                ActivityBuilder.toPermissionActivity(context, true);
            }
        }
    }

    private static void toCallFlashSetGuide(Context context) {
        boolean isShowGuideActivity = PreferenceHelper.getLong(PreferenceHelper.PREF_LAST_SHOW_FIRST_GUIDE_TIME, 0) <= 0;
        if (isShowGuideActivity) {
            PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_SHOW_FIRST_GUIDE_TIME, System.currentTimeMillis());
            ActivityBuilder.toCallFlashSetGuideActivity(context);
        }
    }

}
