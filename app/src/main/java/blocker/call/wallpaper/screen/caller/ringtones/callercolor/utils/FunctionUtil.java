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
    public static long getTotalMemory() {
        String str1 = "/proc/meminfo";// system Memory file
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();// read meminfo fist
            arrayOfString = str2.split("\\s+");
            initial_memory = Long.parseLong(arrayOfString[1]) * 1024;
            localBufferedReader.close();
            localFileReader.close();
        } catch (Exception e) {
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return initial_memory;// Byte to KB or MB，Format memory size
    }

    public static long getAvailMemory(Context context) {// get android
        // AvailMemory
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;// Formater Memory
    }

    public static long getMemorySizebyPid(Context context, int pid) {
        int processPID[] = new int[1];
        processPID[0] = pid;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Debug.MemoryInfo[] memInfo = am.getProcessMemoryInfo(processPID);
        long MEM = 0;
        if (memInfo != null && memInfo.length > 0)
            MEM = memInfo[0].getTotalPss() * 1024;
        return MEM;
    }

    public static String getCRC32(String fileUri) {
        CRC32 crc32 = new CRC32();
        FileInputStream fileinputstream = null;
        CheckedInputStream checkedinputstream = null;
        String crc = null;
        try {
            fileinputstream = new FileInputStream(new File(fileUri));
            checkedinputstream = new CheckedInputStream(fileinputstream, crc32);
            while (checkedinputstream.read() != -1) {
            }
            crc = Long.toHexString(crc32.getValue()).toUpperCase();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileinputstream != null) {
                try {
                    fileinputstream.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (checkedinputstream != null) {
                try {
                    checkedinputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return crc;
    }

    public static void CopyAssertFileToDisk(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[1000];
        int readLen = is.read(buf);
        int offset = 0;
        while (readLen > 0) {
            os.write(buf, offset, readLen);
            readLen = is.read(buf);
        }
    }

    public static long getDbversionAssets(Context connect, String fileName) {
        long dbversion = 0;
        String line = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(connect.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            line = bufReader.readLine();
            if (line != "")
                dbversion = Long.parseLong(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbversion;
    }

    public static String getPathPart(String fileName) {
        int point = getPathLsatIndex(fileName);
        int length = fileName.length();
        if (point == -1) {
            return "";
        } else if (point == length - 1) {
            int secondPoint = getPathLsatIndex(fileName, point - 1);
            if (secondPoint == -1) {
                return "";
            } else {
                return fileName.substring(0, secondPoint);
            }
        } else {
            return fileName.substring(0, point);
        }
    }

    public static int getPathLsatIndex(String fileName) {
        int point = fileName.lastIndexOf('/');
        if (point == -1) {
            point = fileName.lastIndexOf('\\');
        }
        return point;
    }

    public static int getPathLsatIndex(String fileName, int fromIndex) {
        int point = fileName.lastIndexOf('/', fromIndex);
        if (point == -1) {
            point = fileName.lastIndexOf('\\', fromIndex);
        }
        return point;
    }


    public static int getActivityCount(Context context, String url) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        List resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        if (resolveInfo != null) {
            return resolveInfo.size();
        }

        return 0;
    }

    public static Long getUploadNetSpeed() {
        long traffic_data = TrafficStats.getTotalTxBytes();
        return traffic_data / 1;
    }

    public  static Long getDownloadNetSpeed(){
        long traffic_data = TrafficStats.getTotalRxBytes() ;
        return  traffic_data / 1;
    }

    public static String getGmsVersion(Context context) {
        String versionName = "none";

        try {
            versionName = context.getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionName;
        } catch (Exception e) {

        }

        return  versionName;
    }

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
