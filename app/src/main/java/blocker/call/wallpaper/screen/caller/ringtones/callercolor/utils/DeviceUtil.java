package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;

public class DeviceUtil {
    public static final double DOUBLE = 10.;
    private static final String TAG = "DeviceUtil";
    private Context context;


    public DeviceUtil(Context c) {
        context = c;
    }

    public static String getDeviceModel() {
        String model = Build.MODEL;
        String manufacture = Build.MANUFACTURER;

        if (manufacture != null && manufacture.isEmpty()) {
            manufacture = Build.BRAND;

            if (manufacture != null && manufacture.isEmpty()) {
                manufacture = "UNKNOWN";
            }
        }

        if (model.toLowerCase().startsWith(manufacture.toLowerCase() + " ") || model.toLowerCase().startsWith(manufacture.toLowerCase() + "_") || model.toLowerCase().startsWith(manufacture.toLowerCase() + "-")) {
            model = model.substring(manufacture.length() + 1);
        } else if (model.toLowerCase().startsWith(manufacture.toLowerCase())) {
            model = model.substring(manufacture.length());
        }

        return manufacture + " " + model;
    }

    //dp 转化成px
    public static int dp2Px(int dp) {
        final float scale = ApplicationEx.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    public static String getIMEI(Context context) {
        return "";
    }

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    //检查设备BonjourName和设备类型的对应关系  by wen
    public static String checkCategory(String bonjourBiosName) {
        String categoryName = "";
        if (bonjourBiosName != null) {
            if (bonjourBiosName.toLowerCase().contains("pro") && bonjourBiosName.toLowerCase().contains("macbook")) {
                return "MacBook-Pro";
            }
            if (bonjourBiosName.toLowerCase().contains("air") && bonjourBiosName.toLowerCase().contains("macbook")) {
                return "MacBook-Air";
            }
            if (bonjourBiosName.toLowerCase().contains("macmini")) {
                return "MacMini";
            }
            if (bonjourBiosName.toLowerCase().contains("imac")) {
                return "iMac";
            }
            if (bonjourBiosName.toLowerCase().contains("iphone")) {
                return "iPhone";
            }
            if (bonjourBiosName.toLowerCase().contains("ipad")) {
                return "iPad";
            }
        }

        return categoryName;
    }

    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) ApplicationEx.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return Math.max(metrics.widthPixels, metrics.heightPixels);
    }

    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) ApplicationEx.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return Math.min(metrics.widthPixels, metrics.heightPixels);
    }

    public static int getStatusBarHeight() {
        /**
         * 获取状态栏高度——方法1
         * */
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = ApplicationEx.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = ApplicationEx.getInstance().getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("WangJ", "状态栏-方法1:" + statusBarHeight);
        return statusBarHeight;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }

    //判断当前应用是否处于最前端
    public static boolean isTopActivity(Context context, String $packageName) {
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if (list.size() == 0) return false;
        for (ActivityManager.RunningAppProcessInfo process : list) {
            LogUtil.d(TAG, "isTopActivity name:" + process.processName + ",importance:" + process.importance);
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    process.processName.equals($packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether or not the user has Android 4.4 KitKat
     *
     * @return true if version code on device is >= kitkat
     */
    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * Determines whether or not the app is the default SMS app on a device
     *
     * @param context
     * @return true if app is default
     */
    public static boolean isDefaultSmsApp(Context context) {
        return !hasKitKat() || context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
    }

    /**
     * final Activity activity  ：调用该方法的Activity实例
     * long milliseconds ：震动的时长，单位是毫秒
     */
    public static void Vibrate(final Activity activity, long milliseconds) {
        try {
            Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(milliseconds);
        } catch (Exception e) {
            LogUtil.e("deviceUtil", "Vibrate1 e:" + e.getMessage());
        }

    }

    /**
     * long[] pattern  ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     * boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */
    public static void Vibrate(final Context context, long[] pattern, boolean isRepeat) {
        try {
            Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(pattern, isRepeat ? 1 : -1);
        } catch (Exception e) {
            LogUtil.e("deviceUtil", "Vibrate2 e:" + e.getMessage());
        }

    }

    /**
     * 播放系统通知提示音
     */
    public static void startAlarm(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (notification == null) return;
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            LogUtil.e("deviceutil", "startAlarm e:" + e.getMessage());
        }


    }

    /**
     * 直接拨打电话
     */
    public static void callOut(Context context, final String number) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + NumberUtil.getLocalizationNumber(number)));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + NumberUtil.getLocalizationNumber(number)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e2) {
                try {
                    Intent intent = new Intent();
                    intent.setData(Uri.parse("tel:" + NumberUtil.getLocalizationNumber(number)));
                    intent.setClassName("com.google.android.dialer", "com.android.dialer.DialtactsActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e3) {
                    LogUtil.e("ccaller", "callout exception e3: " + e3.getMessage());
                }
            }
        }
    }

    /**
     * 调换到系统短信界面
     */
    public static void toSystemSms(Context context, String number) {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
            sendIntent.setData(Uri.parse("smsto:" + NumberUtil.getLocalizationNumber(number)));
            context.startActivity(sendIntent);
        } catch (Exception e) {
            LogUtil.e("deviceutil", "end call to sms:" + e.getMessage());
            try {
                String defaultApplication = Settings.Secure.getString(context.getContentResolver(), "sms_default_application");
                PackageManager pm = context.getPackageManager();
                Intent intent_sms = pm.getLaunchIntentForPackage(defaultApplication);
                if (intent_sms != null) {
                    context.startActivity(intent_sms);
                }
            } catch (Exception e1) {
                LogUtil.e("lmcaller", "end call to sms 2: " + e.getMessage());
            }
        }
    }

    /**
     * 获取手机SIM
     */
    public static String getSimNmber(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number();
        } catch (Exception e) {
            LogUtil.d("DeviceUtil", "getSimNmber e:" + e.getMessage());
        }
        return null;
    }


    /**
     * 忽略电池优化
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void ignoreBatteryOptimization(Context context) {
        Intent intent = new Intent();
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        LogUtil.d("DeviceUtil", "ignoreBatteryOptimization ada:" + pm.isIgnoringBatteryOptimizations(packageName));
        if (pm.isIgnoringBatteryOptimizations(packageName)) {
//            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        } else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);
        }
    }


    public static void hideSystemUiBarFromView(View m) {
        try {
            int visibility = m.getWindowVisibility();
            boolean isImmersiveModeNoEnabled =
                    ((visibility | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) != visibility);
            if (isImmersiveModeNoEnabled) {
                visibility |= View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

                if (Build.VERSION.SDK_INT >= 14) {
                    visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                }

                if (Build.VERSION.SDK_INT >= 16) {
                    visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
                }

                if (Build.VERSION.SDK_INT >= 18) {
                    visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                }
                m.setSystemUiVisibility(visibility);
            }
        } catch (Exception e) {

        }
    }

    public static void hideStatuBar(View m) {
        try {
            int visibility = m.getWindowVisibility();
            boolean isImmersiveModeNoEnabled =
                    ((visibility | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) != visibility);
            if (isImmersiveModeNoEnabled) {
                visibility |= View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

                if (Build.VERSION.SDK_INT >= 16) {
                    visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
                }

                if (Build.VERSION.SDK_INT >= 18) {
                    visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                }
                m.setSystemUiVisibility(visibility);
            }
        } catch (Exception e) {

        }
    }

    public static void wakeAndUnlock(Context context, boolean isUnlock) {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //得到键盘锁管理器对象
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        if (isUnlock) {
            //点亮屏幕
            wl.acquire();
            //解锁
            kl.disableKeyguard();
        } else {
            //锁屏
            kl.reenableKeyguard();
            //释放wakeLock，关灯
            wl.release();
        }

    }

    //读取总内存
    public static String getTotalMem() {
        String strTotal = "";
        try {
            long totalMem = 0;
            byte[] mem = FileUtil.readFile("/proc/meminfo");
            Scanner scanner = new Scanner(new String(mem));
            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();
                if (totalMem == 0 && line.startsWith("MemTotal:")) {
                    String[] split = line.split("[ ]+", 3);
                    totalMem = Long.parseLong(split[1]);
                }
            }
            LogUtil.d("nmlogs", "totalMem: " + totalMem);
            if (totalMem > 1000 * 1000) {
                totalMem = totalMem / (1000);
                if (totalMem > 5000) {
                    totalMem = 6;
                } else if (totalMem > 3500) {
                    totalMem = 4;
                } else if (totalMem > 2500) {
                    totalMem = 3;
                } else if (totalMem > 1700) {
                    totalMem = 2;
                } else {
                    totalMem = 1;
                }

                strTotal = totalMem + "GB";
            } else if (totalMem > 1000) {
                totalMem = totalMem / 1000;
                if (totalMem >= 400) {
                    totalMem = 512;
                } else if (totalMem >= 170) {
                    totalMem = 256;
                } else if (totalMem >= 80) {
                    totalMem = 128;
                }
                strTotal = totalMem + "MB";
            } else {
                strTotal = totalMem + "KB";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strTotal;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 获取虚拟按键的高度
     *
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    public static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    /**
     * 获取控件的高度或者宽度  isHeight=true则为测量该控件的高度，isHeight=false则为测量该控件的宽度
     *
     * @param view
     * @param isHeight
     * @return
     */
    public static int getViewHeight(View view, boolean isHeight) {
        int result;
        if (view == null) return 0;
        if (isHeight) {
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(h, 0);
            result = view.getMeasuredHeight();
        } else {
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(0, w);
            result = view.getMeasuredWidth();
        }
        return result;
    }

    /**
     * 设置透明状态栏
     **/
    public static void setWindow(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Android 5.0 以上 全透明
            Window window = act.getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 状态栏（以上几行代码必须，参考setStatusBarColor|setNavigationBarColor方法源码）
            window.setStatusBarColor(Color.TRANSPARENT);
            // 虚拟导航键
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Android 4.4 以上 半透明
            Window window = act.getWindow();
            // 状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 虚拟导航键
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        hh 在某些手机上，如果浮窗的type优先级低，会导致和锁屏抢占闪烁的情况，所以不能加这个标志位，
//        此处的activity也不需要显示在锁屏之上
        act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    public static void wakeUpScreen(Context context) {
        PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);// 电源管理器
        final PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Tag"); //唤醒锁
        mWakeLock.acquire();
        mWakeLock.release();
    }

    /**
     * 设置当前界面的亮度
     */
    public static void changeAppBrightness(Activity activity, int brightness) {
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            if (brightness == -1) {
                lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
            } else {
                lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
            }
            window.setAttributes(lp);
        } catch (Exception e) {
        }
    }

    public static boolean isScreenOff(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();
        boolean isDayDream = PreferenceHelper.getBoolean(PreferenceHelper.PREF_KEY_DAY_DREAM_STATUS, false);
        if (Build.VERSION.SDK_INT > 19) {
            LogUtil.d(TAG, "isInteractive :" + pm.isInteractive() + ",flag:" + flag + "isDayDream," + isDayDream);
            LogUtil.d("deviceUtil", "isScreenOff:" + (!pm.isInteractive() || flag));
            return !pm.isInteractive() || flag || isDayDream;
        } else {
            return !pm.isScreenOn() || flag || isDayDream;
        }
    }

    public static float getViewShowPercent(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Rect localRect = new Rect();
        view.getLocalVisibleRect(localRect);
        if (localRect.bottom < 0) return 0;
        float showHeight = localRect.bottom - localRect.top;
        float viewHeight = view.getHeight();
//        LogUtil.d(TAG, "getViewShowPercent bottom:" + localRect.bottom + ",top:" + localRect.top + ",showHeight:" + showHeight + ",viewHeight:" + viewHeight);
        if (viewHeight == 0) return 0;
        return showHeight / viewHeight;
    }

}
