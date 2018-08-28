package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.AdPreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.JobLocalService;

/**
 * Created by John on 2015/12/17.
 */
public final class CommonUtils {
    // 应用大字体最大scale
    public static final float MAX_FONT_SCALE = 1.2f;

    private CommonUtils() {
    }

    //设置状态栏融合，5.0以上
    public static void translucentStatusBar(Activity context) {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //     Window window = context.getWindow();
        //     window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //     window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //     window.setStatusBarColor(context.getResources().getColor(R.color.status_bar_color));
        // }
        translucentStatusBar(context, false);
    }

    public static void translucentStatusBar(Activity context, boolean isFullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatusForLowVersion(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            translucentStatusBar(context, isFullScreen, Color.TRANSPARENT);
        }
    }

    private static void setTranslucentStatusForLowVersion(Activity activity) {
        if (activity != null) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View statusView = createStatusView(activity);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(statusView);
        }
    }

    private static View createStatusView(Activity activity) {
        View view = new View(activity);
        int statusBarHeight = DeviceUtil.getStatusBarHeight();
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
        view.setBackgroundColor(activity.getResources().getColor(R.color.color_transparent));
        return view;
    }

    //设置状态栏融合，5.0以上
    public static void translucentStatusBar(Activity context, boolean isFullScreen, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (isFullScreen) {
                visibility = visibility | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            }
            window.getDecorView().setSystemUiVisibility(visibility);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor);
//             window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    // 设置状态栏颜色， 5.0以上
    public static void setTranslucentStatusBarColor(Activity act, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    /**
     * 返回值为true:停止统计功能
     * 返回值为false:开启统计功能
     *
     * @return
     */
    public static boolean isStopHighStat(int time1, int time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        String currentHour = sdf.format(new Date());
        int hour = Integer.valueOf(currentHour);
        return time1 <= hour && hour < time2;
    }

    /**
     * 适应大字体到Large(1.15)，scale小于1.15的字体大小将跟随系统
     */
    public static void switchResourcesScale(Resources res) {
        if (res == null) {
            return;
        }
        Configuration config = res.getConfiguration();
        //LogUtil.e(ConstantUtils.NM_TAG, "fontScale:" + config.fontScale);
        if (config.fontScale > MAX_FONT_SCALE) {
            config.fontScale = MAX_FONT_SCALE;
            config.setTo(config);
            DisplayMetrics dm = res.getDisplayMetrics();
            res.updateConfiguration(config, dm);
        }
    }

    public static boolean isNetConnected(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetInfo != null && activeNetInfo.isAvailable() && activeNetInfo.isConnected());
    }

    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI && activeNetInfo.isConnected());
    }

    public static boolean isMobile(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetInfo != null && activeNetInfo.isConnected() && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * @param threadPools, fiexed pool
     * @param runnable
     */
    public static void wrappedSubmit(ExecutorService threadPools, Runnable runnable) {
        try {
            threadPools.submit(runnable);
        } catch (Exception e) {
            LogUtil.e("fiexed thread pool", "fiexed pool wrapedSubmit exception: " + e.getMessage());
        }
    }

    public static void wrappedScheduled(ScheduledExecutorService threadPools, int delay, TimeUnit unit, Runnable runnable) {
        try {
            threadPools.schedule(runnable, delay, unit);
        } catch (Exception e) {
            LogUtil.e("fiexed thread pool", "fiexed pool wrapedSubmit exception: " + e.getMessage());
        }
    }

    //get self processName
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static ExecutorService globalFixedPool = Executors.newFixedThreadPool(1);
    public static ExecutorService globalFixedPool2 = Executors.newFixedThreadPool(4);
    public static ScheduledExecutorService globalScheduledFixedPool = Executors.newScheduledThreadPool(1);

    /**
     * 获取一个范围内的随机数
     */
    public static int getRandom(int start, int end) {
        int number = (int) (Math.random() * (end - start + 1)) + start;
        return number;
    }

    /**
     * 获取一个范围内的随机数组
     *
     * @param length 获取数组的长度
     */
    public static Integer[] getRandomArray(int start, int end, int length) {
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i <= length - 1; i++) {
            int number;
            do {
                number = (int) (Math.random() * (end - start + 1)) + start;
            } while (integers.contains(number));
            integers.add(number);
        }

        return integers.toArray(new Integer[length]);
    }

    // schedule the start of the service every 10 - 30 seconds
    public static void scheduleJob(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(1001, new ComponentName(context.getPackageName(), JobLocalService.class.getName()))
                    .setPeriodic(60 * 1000 * 15)//2mins-120000, test 5000
//                    .setMinimumLatency(1 * 1000) // wait at least
//                    .setOverrideDeadline(3 * 1000) // maximum delay
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
            jobScheduler.schedule(jobInfo);
            LogUtil.d("JobLocalService", "scheduleJob startService: ");
        }
    }

    public static boolean isOldForFlash(){
        SharedPreferences pref = ApplicationEx.getInstance().getGlobalSettingPreference();
        boolean old = pref.getBoolean("cid_is_old_user_flash", false);
        if (!old) {
            //first sync time
            long first_sync = pref.getLong("key_cid_first_sync_server_time", 0);
//            int today = Stringutil.getTodayDayByServer();//real time
            int today = Stringutil.getTodayDayInYearLocal();//not real for test

            if (first_sync != 0 && (today - Stringutil.getDayByTime(first_sync)) >= 2) {
                old = true;
                pref.edit().putBoolean("cid_is_old_user_flash", true).apply();
            }
        }
        return old;
    }
}
