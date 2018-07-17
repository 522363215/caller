package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.LocalService;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.update.HttpUtilUpdate;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.update.Util;

public class AppUtils {
    public static final String NOTIFICATION_URL = "http://battery-analysis.lionmobi.com/viewSelected/portal/api.php";
    public static final String NOTIFICATION_ACTION = "notification_static";
    public static final int INVALID_PID = -1;
    public static final int PID_COLUNM_INDEX = 1;
    public static final int NAME_COLUNM_INDEX = 8;
    public static final String CMD = "ps";
    public static final String SPACES_FILTER_REG = "\\s+";
    public static final String USER_APP_NAME_PREFIX = "com.";
    public static final String KEY_NETWORK_PROTECT = "network_protect";
    public static final String KEY_DEVICE_PROTECT = "device_protect";
    public static final String KEY_LAST_SCREEN_LOCK_OFF_SHOW_TIME = "last_screen_lock_off_show_time";
    public static final String KEY_LAST_SCREEN_LOCK_OFF_PRESENT_TIME = "last_screen_lock_off_present_time";
    public static final String KEY_LAST_SCREEN_LOCK_PRESENT_TIME = "last_screen_lock_show_time";
    public static final String KEY_LAST_SCREEN_LOCK_PRESENT_FREQUENCY = "last_screen_lock_show_frequency";
    public static final String KEY_IS_SCREEN_LOCK_SHOWING = "is_screen_lock_showing";
    public static final String KEY_LAST_SCREEN_FAILED_TIME = "last_screen_failed_time";
    public static final String KEY_LAST_SCREEN_FAILED_FREQUENCY = "last_screen_failed_frequency";
    public static final String KEY_IS_QUICK_CHARGE_SHOWING = "is_quick_charge_showing";
    public static final String KEY_IS_QUICK_CHARGE_ON = "is_quick_charge_on";
    public static final String KEY_IS_WHATS_NEW_SHOW = "is_whats_new_show";
    public static final String KEY_LAST_QUICK_CHARGE_PRESENT_TIME = "last_quick_charge_present_time";

    public static final String SP_NAME_APP_CONFIG = "app_utils_config";

    //防火墙提醒开启对话框
    public static final String KEY_LAST_FIREWALL_REMIND_DIALOG_TIME = "last_firewall_remind_dialog_time";
    public static final String KEY_LAST_FIREWALL_REMIND_DIALOG_FREQUENCY = "last_firewall_remind_dialog_frequency";

    public static final String KEY_SCREEN_LOCK_ACTIVE_CLOSE = "screen_lock_active_close";
    public static final String KEY_CHARGE_LOCK_ACTIVE_CLOSE = "charge_lock_active_close";

    public static final String KEY_INIT_VERCODE = "init_vercode";
    public static final String KEY_CUR_VERCODE = "cur_vercode";

    /**
     * 当前用户是否升级用户
     *
     * @return
     */
    public static boolean isUpdateUser() {
        int initVercode = PreferenceHelper.getInt(KEY_INIT_VERCODE, -1);
        if (initVercode == -1) {// 没有initVercode的情况，说明是从之前的版本升级到174才会出现的，故认作升级用户
            return true;
        }
        int curVercode = PreferenceHelper.getInt(KEY_CUR_VERCODE, -1);
        return initVercode != curVercode;
    }


    private static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    packagename, 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static int getRandomNums(int min, int max) {
        int i = 0;
        try {
            Random random = new Random();

            i = random.nextInt((max - min) + 1) + min;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public static boolean isAppAlive(Context context, String packageName) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName.equals(packageName)) {
                Log.i("NotificationLaunch",
                        String.format("the %s is running, isAppAlive return true", packageName));
                return true;
            }
        }
        Log.i("NotificationLaunch",
                String.format("the %s is not running, isAppAlive return false", packageName));
        return false;
    }


    public static void collapseNotification(Context context) {
        try {
            Object sbservice = context.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method showsb = null;
            try {
                showsb = statusbarManager.getMethod("collapse");
            } catch (NoSuchMethodException e) {
                showsb = statusbarManager.getMethod("collapsePanels");
            }
            showsb.invoke(sbservice);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static boolean isServiceRuning(Context context, String serviceName) {

        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // 获取系统中正在运行的服务，做多为设置100
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(100);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : list) {
            String className = runningServiceInfo.service.getClassName();// 获取服务的名称
            if (className.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    public static long getMemorySizebyPid(Context context, int pid) {
        int processPID[] = new int[1];
        processPID[0] = pid;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Debug.MemoryInfo[] memInfo = am.getProcessMemoryInfo(processPID);
        long MEM = memInfo[0].getTotalPss() * 1024;
        return MEM;
    }

    /**
     * 发送信息到服务器
     *
     * @param context
     * @param notificationType         ：通知内容
     * @param timeZone                 ：时区
     * @param actionType：1-->展示；2-->点击
     */
    public static void sendNotificationInfoToServer(Context context, String notificationType, String timeZone, String actionType) {
        try {
            Map param = Util.getBaseParam(context);
            param.put("action", NOTIFICATION_ACTION);
            param.put("notification_type", notificationType);
            param.put("time_zone", timeZone);
            param.put("action_type", actionType);

            HttpUtilUpdate.doPost(NOTIFICATION_URL, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isThisASystemPackage(PackageManager mPackageManager, PackageInfo mPackageInfo) {
        try {
            PackageInfo sys = mPackageManager.getPackageInfo("android", PackageManager.GET_SIGNATURES);
            return (mPackageInfo != null && mPackageInfo.signatures != null && sys.signatures[0]
                    .equals(mPackageInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isInputMethodApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean isInputMethodApp = false;
        try {
            PackageInfo pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_SERVICES);
            ServiceInfo[] sInfo = pkgInfo.services;
            if (sInfo != null) {
                for (int i = 0; i < sInfo.length; i++) {
                    ServiceInfo serviceInfo = sInfo[i];
                    if (serviceInfo.permission != null &&
                            serviceInfo.permission.equals("android.permission.BIND_INPUT_METHOD")) {
                        isInputMethodApp = true;
                        break;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isInputMethodApp;
    }


    public static String getCurrentPkg(Context context) {
        String pkg = "";
        try {
            if (Build.VERSION.SDK_INT > 20) {
                ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

                List<ActivityManager.RunningAppProcessInfo> tasks = manager.getRunningAppProcesses();
                pkg = tasks.get(0).processName;
            } else {
                ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
                pkg = rti.get(0).topActivity.getPackageName();
            }
        } catch (Exception e) {
            LogUtil.e("liontools", "getCurrentPkg exception: " + e.getMessage());

        }
        return pkg;
    }


    public static boolean checkPackage(String packageName, Context context) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

    }

    @SuppressLint("NewApi")
    public static boolean isUsageStatsPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return true;
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !hasUsageAccessSetting(context))
            return true;
        else {
            try {
                String GET_USAGE_STATS = AppOpsManager.OPSTR_GET_USAGE_STATS;

                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOpsManager.checkOpNoThrow(GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
                return (mode == AppOpsManager.MODE_ALLOWED);
            } catch (Throwable e) { //NoClassDefFoundError
                return false;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getProcessNew(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return null;
        String topPackageName = null;
        UsageStatsManager usage = (UsageStatsManager) context.getSystemService("usagestats");
        long time = System.currentTimeMillis();
        List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 20000 * 10, time);
        if (stats != null) {
            SortedMap<Long, UsageStats> runningTask = new TreeMap<>();
            for (UsageStats usageStats : stats) {
                runningTask.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (runningTask.isEmpty()) {
                return null;
            }

            UsageStats temp = runningTask.get(runningTask.lastKey());
            try {
                topPackageName = temp.getPackageName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return topPackageName;
    }

    public String getTopPackageName(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                String topTask = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                if (!TextUtils.isEmpty(topTask))
                    return topTask;
            } else if (AppUtils.isUsageStatsPermissionGranted(context)) {
                String mPkgName = getProcessNew(context);
                if (!TextUtils.isEmpty(mPkgName))
                    return mPkgName;
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static void killApp(Context context, String sprocess) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            amKillProcess(context, sprocess);
            am.killBackgroundProcesses(sprocess);
            Method forceStopPackage = am.getClass().getDeclaredMethod("forceStopPackage", String.class);
            if (forceStopPackage != null) {
                forceStopPackage.setAccessible(true);
                forceStopPackage.invoke(am, sprocess);
            }
        } catch (Exception e) {
            LogUtil.d("NETWORKBLOCK", "killapp Exception " + e.getMessage());
        }
    }

    private static void amKillProcess(Context context, String process) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningProcess : runningProcesses) {
            if (runningProcess.processName.equals(process)) {
                Process.sendSignal(runningProcess.pid, Process.SIGNAL_KILL);
            }
        }
    }


    public static long getAppFirstInstallTime(Context context, String pkgName) {
        PackageInfo packageInfo;
        try {

            if (context == null || TextUtils.isEmpty(pkgName)) {
                return 0;
            }
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
            return packageInfo.firstInstallTime;

        } catch (Exception e) {
            //should never happen
            return 0;
        }
    }

//    public static int getUidByPkg(Context mContext, String pkg) {
//        int uid = -1;
//        if (mContext == null || TextUtils.isEmpty(pkg)) {
//            return uid;
//        }
//        try {
//            PackageManager pm = mContext.getPackageManager();
//            ApplicationInfo ai = pm.getApplicationInfo(pkg, PackageManager.GET_ACTIVITIES);//PackageManager.GET_ACTIVITIES, 1
//            uid = ai.uid;
//        } catch (Exception e) {
//            LogUtil.e(ConstantUtils.NM_TAG, "getUidByPkg exception: " + e.getMessage());
//        }
//
//        return uid;
//    }


    //检测系统是否有App Usage权限设置
    public static boolean hasUsageAccessSetting(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 是否显示“推荐”speedBoost
     * (距离上一次boost成功不超过2小时 && 一天内不超过2次boost成功，则推荐显示)
     *
     * @param context
     * @return
     */
    public static boolean isRecommendBoost(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_APP_CONFIG, Context.MODE_PRIVATE);
        long boostTime = sp.getLong("last_boost_time", 0);
        // 距离上一次boost操作不超过2小时，则不推荐
        if (System.currentTimeMillis() - boostTime < 2 * Stringutil.HOUR) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        int currentDay = cal.get(Calendar.DAY_OF_YEAR);
        int currentYear = cal.get(Calendar.YEAR);
        cal.setTimeInMillis(boostTime);
        int boostDay = cal.get(Calendar.DAY_OF_YEAR);
        int boostYear = cal.get(Calendar.YEAR);

        if (currentYear == boostYear && currentDay == boostDay) {
            // 同一天内超出2次boost，则不推荐
            long frequency = sp.getLong("boost_frequency", 0);
            return frequency < 2;
        } else {
            return true;
        }

    }

    /**
     * boost计次
     *
     * @param context
     */
    public static void countBoostFrequency(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_APP_CONFIG, Context.MODE_PRIVATE);
        long boostTime = sp.getLong("last_boost_time", 0);
        Calendar cal = Calendar.getInstance();
        int currentDay = cal.get(Calendar.DAY_OF_YEAR);
        int currentYear = cal.get(Calendar.YEAR);
        cal.setTimeInMillis(boostTime);
        int boostDay = cal.get(Calendar.DAY_OF_YEAR);
        int boostYear = cal.get(Calendar.YEAR);

        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("last_boost_time", System.currentTimeMillis());
        if (currentYear == boostYear && currentDay == boostDay) {
            long frequency = sp.getLong("boost_frequency", 0);
            editor.putLong("boost_frequency", frequency + 1);
        } else {
            // 今天第一次计次
            editor.putLong("boost_frequency", 1);
        }
        editor.commit();
    }


    /**
     * 不过滤系统应用
     *
     * @param pkgName
     * @param context
     * @return
     */
    public static boolean isUserApp(String pkgName, Context context) {
        return isUserApp(pkgName, context, false);
    }

    public static boolean isUserApp(String pkgName, Context context, boolean filterPreloadedApp) {
        boolean isUser = false;
        if (TextUtils.isEmpty(pkgName) || context == null) {
            return isUser;
        }
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pInfo = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {
                isUser = false;
            } else {
                isUser = true;
            }
            if (!filterPreloadedApp && pkgName.contains("com.google.android"))
                if (pkgName.equals("com.google.android.gms"))
                    isUser = true;
                else
                    isUser = false;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return isUser;
    }


    public static boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static boolean isSystemUpdateApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }


    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusBasHeight(Context context) {
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return context.getResources().getDimensionPixelSize(resourceId);
        } else {
            try {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height")
                        .get(object).toString());
                return context.getResources().getDimensionPixelSize(height);
            } catch (Exception e) {
                return Math.round(26 * Resources.getSystem().getDisplayMetrics().density);
            }
        }
    }


}
