package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.PermissionTipActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;

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
            if (ApplicationEx.getInstance() != null) {
                ContentResolver contentResolver = ApplicationEx.getInstance().getContentResolver();
                String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
                String packageName = ApplicationEx.getInstance().getPackageName();
                return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
            } else {
                return false;
            }
        }
        return true;
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
     * 针对接电话获取权限，7.0以上才需要
     */
    public static boolean isHaveNotificationPolicyAccessForAnswerCall(Context context) {
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
    public static void applyFloatWindowPermission(Activity context) {
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
            /**
             * 手机管家版本较低 HUAWEI SC-UL10
             */
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
    }

    /**
     * 跳转到小米请求show on lock permission 界面
     */
    public static void toXiaomiShowOnLockPermssion(Activity activity, int requestCode) {
        try {
            String miuiVersion = SystemInfoUtil.getMiuiVersion();
            LogUtil.d("miui_setting", "miuiVersion: " + miuiVersion);
            Intent intent = null;
            if ("V8".equals(miuiVersion) || "V9".equals(miuiVersion)) {
                intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.putExtra("extra_pkgname", activity.getPackageName());
            } else if ("V6".equals(miuiVersion) || "V7".equals(miuiVersion)) {
                intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", activity.getPackageName());
            } else if ("V5".equals(miuiVersion)) {
                Uri packageURI = Uri.parse("package:" + activity.getApplicationInfo().packageName);
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            } else {
                intent = new Intent();
                intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                    ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//                    intent.setComponent(componentName);
                intent.putExtra("extra_pkgname", activity.getPackageName());
            }
            activity.startActivityForResult(intent, requestCode);
            showXiaoMiPermissionTipDialog(activity, "is_show_on_lock");
        } catch (Exception e) {
            e.printStackTrace();
        }
        PreferenceHelper.putLong(PreferenceHelper.PREF_KEY_LAST_TO_XIAO_MI_SHOW_ON_LOCK_PERMISSION_ACTIVITY, System.currentTimeMillis());
    }

    /**
     * 跳转到小米请求自启动的权限界面
     */
    public static void toXiaomiAutoStartPermission(Activity activity, int requestCode) {
        try {

            if (SystemInfoUtil.isMiui()) {//小米, autostart
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                intent.setComponent(componentName);
                activity.startActivityForResult(intent, requestCode);
                showXiaoMiPermissionTipDialog(activity, "is_auto_start_boot");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                LogUtil.d(TAG, "getPackageName(): " + activity.getPackageName());
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, requestCode);
                showXiaoMiPermissionTipDialog(activity, "is_auto_start_boot");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        PreferenceHelper.putLong(PreferenceHelper.PREF_KEY_LAST_TO_XIAO_MI_AUTO_START_BOOT_PERMISSION_ACTIVITY, System.currentTimeMillis());
    }

    private static void showXiaoMiPermissionTipDialog(final Context context, final String intentKey) {
        Async.scheduleTaskOnUiThread(200, new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, PermissionTipActivity.class);
                intent.putExtra(intentKey, true);
                context.startActivity(intent);
            }
        });
    }
}
