package com.md.block.util;

import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.md.block.core.BlockManager;

import java.lang.reflect.Method;

/**
 * Created by Zhq on 2017/12/21.
 */

public class SpecialPermissionsUtil {
    private static final String TAG = "SpecialPermissionsUtil";
    public static final int REQUEST_CODE_FLOAT_WINDAOW_PERMISSION = 2525;
    private static NotificationManager mNotificationManager;

    /**
     * 判断是否获取drawoverlay 权限
     */
    public static boolean canDrawOverlays(Context context) {
        boolean isCanDrawOverlays = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isCanDrawOverlays = Settings.canDrawOverlays(context);
            LogUtil.d(TAG, "canDrawOverlays:" + isCanDrawOverlays);
        }
        return isCanDrawOverlays;
    }

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
        boolean is = false;
        Context context = BlockManager.getInstance().getAppContext();
        if (context != null) {
            SharedPreferences pref = context.getSharedPreferences("com_coolcaller_pref", Context.MODE_PRIVATE);
            is = pref.getBoolean("is_cid_callernotificationlistenerservice_running", false);
        }
        return is;
    }

    /**
     * 判断是否获取WriteSetting 权限
     */
    public static boolean canWriteSetting(Context context) {
        boolean isCanWrite = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isCanWrite = Settings.System.canWrite(context);
            LogUtil.d(TAG, "canWrite:" + isCanWrite);
        }
        return isCanWrite;
    }

    /**
     * 免打扰权限
     */
    public static boolean isHaveNotificationPolicyAccess(Context context) {
        boolean isHaveNotificationPolicyAccess = true;
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        assert mNotificationManager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isHaveNotificationPolicyAccess = mNotificationManager.isNotificationPolicyAccessGranted();
        }
        return isHaveNotificationPolicyAccess;
    }

    /**
     * 检测 Huawei 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class<?> clazz = manager.getClass();
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                LogUtil.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            LogUtil.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }

    /**
     * 去华为权限申请页面
     */
    /*public static void applyFloatWindowPermission(Activity context) {
        try {
            Intent intent = new Intent();
            //  ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            //  ComponentName comp = new ComponentName("com.huawei.systemmanager",
            //   "com.huawei.permissionmanager.ui.SingleAppActivity");//华为权限管理，跳转到指定app的权限管理位置需要华为接口权限，未解决
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            if (RomUtils.getEmuiVersion() == 3.1) {
                //emui 3.1 的适配
                context.startActivityForResult(intent, REQUEST_CODE_FLOAT_WINDAOW_PERMISSION);
            } else if (RomUtils.getEmuiVersion() == 3.0) {
                //emui 3.0 的适配
                comp = new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");//悬浮窗管理页面
                intent.setComponent(comp);
                context.startActivityForResult(intent, REQUEST_CODE_FLOAT_WINDAOW_PERMISSION);
            } else {
                comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面
                intent.setComponent(comp);
                context.startActivityForResult(intent, REQUEST_CODE_FLOAT_WINDAOW_PERMISSION);
            }
            ToastUtils.showToast(context, context.getResources().getString(R.string.call_flash_gif_show_setting_des), Toast.LENGTH_SHORT);
        } catch (SecurityException e) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //  ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理，跳转到本app的权限管理页面,这个需要华为接口权限，未解决
            //   ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            context.startActivityForResult(intent, REQUEST_CODE_FLOAT_WINDAOW_PERMISSION);
            ToastUtils.showToast(context, context.getResources().getString(R.string.call_flash_gif_show_setting_des), Toast.LENGTH_SHORT);
            LogUtil.e(TAG, "applyFloatWindowPermission e:" + e.getMessage());
        } catch (ActivityNotFoundException e) {
            *//**
             * 手机管家版本较低 HUAWEI SC-UL10
             *//*
            //  Toast.makeText(MainActivity.this, "act找不到", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.Android.settings", "com.android.settings.permission.TabItem");//权限管理页面 android4.4
            //  ComponentName comp = new ComponentName("com.android.settings","com.android.settings.permission.single_app_activity");//此处可跳转到指定app对应的权限管理页面，但是需要相关权限，未解决
            intent.setComponent(comp);
            context.startActivityForResult(intent, REQUEST_CODE_FLOAT_WINDAOW_PERMISSION);
            ToastUtils.showToast(context, context.getResources().getString(R.string.call_flash_gif_show_setting_des), Toast.LENGTH_SHORT);
            LogUtil.e(TAG, "applyFloatWindowPermission e:" + e.getMessage());
        } catch (Exception e) {
            //抛出异常时提示信息
            ToastUtils.showToast(context, context.getResources().getString(R.string.call_flash_gif_show_setting_des2), Toast.LENGTH_SHORT);
            LogUtil.e(TAG, "applyFloatWindowPermission e:" + e.getMessage());
        }
    }*/
}
