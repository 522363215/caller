package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.update.Util;

public class FunctionUtil {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String getTopPackageName(Context context) {
        String topPackageName = null;
        UsageStatsManager usage = (UsageStatsManager) context.getSystemService("usagestats");
        long time = System.currentTimeMillis();
        List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 20000 * 10, time);
        if (stats != null) {
            SortedMap<Long, UsageStats> runningTask = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : stats) {
                runningTask.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (runningTask.isEmpty()) {
                return null;
            }

            UsageStats temp = runningTask.get(runningTask.lastKey());
            int mint = 0;
            try {
                topPackageName = temp.getPackageName();
                mint = (Integer) temp.getClass().getDeclaredField("mLastEvent").get(temp);
                //Log.i("ads","EVENTmm:"+mint+"|TOPKNANE:"+topPackageName+"|LastNAME:"+mLastPackageName);
                if (2 == mint) { // 2 代表后台进程  从代码看应该如果不等于1 就没有效果
                    topPackageName = null;
                }
                // Log.i("ads","EVENTmm:"+mint+"|APKNANE:"+topPackageName);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return topPackageName;
    }

    public static String getTopName(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                String topTask = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                if (!TextUtils.isEmpty(topTask))
                    return topTask;
            } else if (Util.isUsageStatsPermissionGranted(context)) {
                String mPkgName = getTopPackageName(context);
                if (!TextUtils.isEmpty(mPkgName))
                    return mPkgName;
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static void sendMail(String mailAddr) {
        try {
            Intent email = new Intent(Intent.ACTION_SEND);
            // 设置邮件默认地址
            email.putExtra(Intent.EXTRA_EMAIL, mailAddr);
            // 设置邮件默认标题
            email.putExtra(Intent.EXTRA_SUBJECT, ResourceUtil.getString(R.string.app_name));
            email.putExtra(Intent.EXTRA_CC, mailAddr);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{mailAddr});

            // 设置要默认发送的内容
            email.putExtra(Intent.EXTRA_TEXT, "");
            email.setType("message/rfc822");
            Intent intent = Intent.createChooser(email, "");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ApplicationEx.getInstance().startActivity(intent);
        }catch (Exception e) {
            if (BuildConfig.DEBUG) LogUtil.error(e);
        }
    }

}
