package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;

public class Stringutil {
    public static String getDateStringFromLong(long date_time) {
        // SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        Date date = new Date(date_time);
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT,
                Locale.getDefault());
        String datsString = df.format(date);
        return datsString;
    }

    public static Date getDateFromFormatString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
                Locale.ENGLISH);
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateStringForToday() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
                Locale.ENGLISH);
        Date date = new Date();
        return format.format(date);
    }

    public static String getTimeString(long time) {//dd-MMM-yyyy HH:mm:ss:SSS
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
                Locale.ENGLISH);
        Date date = new Date(time);
        return format.format(date);
    }

    public static int getTodayDayInYearGMT8() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        c.setTimeInMillis(System.currentTimeMillis());
        return c.get(Calendar.DAY_OF_YEAR);
    }

    public static int getTodayDayInYearLocal() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        return c.get(Calendar.DAY_OF_YEAR);
    }

    public static int getTodayDayByServer() {
        long tm_server = PreferenceHelper.getLong("true_time_from_server", 0);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(tm_server);
        return c.get(Calendar.DAY_OF_YEAR);
    }

    public static int getCurrentYearOfLocal(long ms) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);
        return c.get(Calendar.YEAR);
    }

    public static int getTodayDayByMs(long ms) {
        if (ms <= 0) {
            return -1;
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);
        return c.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isHalloween() {
        boolean is = false;

        int real_today = getTodayDayByMs(PreferenceHelper.getLong("true_time_from_server", 0));
//        int real_today = getTodayDayInYearLocal(); //for test

        Calendar c = Calendar.getInstance();
        c.set(2017, Calendar.NOVEMBER, 1);

        int hallowDay = c.get(Calendar.DAY_OF_YEAR);

        int dis = Math.abs(real_today - hallowDay);
        if (dis <= 5) {
            is = true;
        }

        return is;
    }

    public static final int HOLIDAY_CHRIST = 1; // 圣诞
    public static final int HOLIDAY_NEW_YEAR = 2;  // 新年
    public static final int HOLIDAY_SV = 3; //情人节
    public static final int HOLIDAY_SV_WHITE = 4;  //白色情人节
    public static final int HOLIDAY_FOOLS = 5;  //愚人节
    public static final int HOLIDAY_MATHER = 6;
    public static final int HOLIDAY_FATHER = 7;
    public static final int HOLIDAY_INDEPENDENCE = 8; //独立日
    public static final int HOLIDAY_HALLOWEEN = 9; //万圣节前夜
    public static final int HOLIDAY_SUN = 10; //复活节
    public static final int HOLIDAY_MILITARY = 11; //退伍军人
    public static final int HOLIDAY_THANKFUL = 12; //感恩节
    public static final int HOLIDAY_ZHONGGUOYEAR = 13; //春节

    private static int getSpecialDays(long ms, int dayType) {
        int day = -1;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);//currentTime

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
//        int thatday =


        switch (dayType) {
            case HOLIDAY_MATHER://母亲节，日期是每年的5月第二个星期日

                break;
        }

        return day;
    }

    public static boolean isWeekend() {
        boolean is = false;
        Calendar calendar = Calendar.getInstance();
//        long tm_server = PreferenceHelper.getLong("true_time_from_server", 0);
        long tm_server = System.currentTimeMillis(); // for test
        if (tm_server > 0) {
            calendar.setTimeInMillis(tm_server);
            int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1; //0 is sunday, 6 is saturday
            if (day_of_week == 0 || day_of_week == 6) {
                is = true;
            }
        }
        return is;
    }

    public static boolean isWeekend(long ms) {
        boolean is = false;
        if (ms > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ms);
            int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1; //0 is sunday, 6 is saturday
            if (day_of_week == 0 || day_of_week == 6) {
                is = true;
            }
        }
        return is;
    }

    /**
     * @param ms
     * @return 0 is sunday, 6 is saturday
     */
    public static int getDayOfWeek(long ms) {
        int day_of_week = -1;
        if (ms > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ms);
            day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1; //0 is sunday, 6 is saturday
        }
        return day_of_week;
    }

    public static boolean isTodayNew(long time) {
        if (time <= 0) {
            return false;
        }
        int real_today = getTodayDayInYearLocal();

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);

        int time_today = c.get(Calendar.DAY_OF_YEAR);
        return real_today == time_today;
    }

    public static boolean isSameMonth(long ms) {
        boolean issame = false;
        Calendar current = Calendar.getInstance();
        int mon = current.get(Calendar.MONTH);

        Calendar com = Calendar.getInstance();
        com.setTimeInMillis(ms);
        int mon_com = com.get(Calendar.MONTH);

        if (mon == mon_com) {
            issame = true;
        }

        return issame;
    }


    public static int getDayByTime(long ms) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ms);
        return c.get(Calendar.DAY_OF_YEAR);
    }

    public static int getTodayDayInYear(int days) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        return (c.get(Calendar.DAY_OF_YEAR) - days);
    }

    public static boolean isToday(long time) {
        if (System.currentTimeMillis() - time < 24 * 60 * 60 * 1000) {
            Calendar now = Calendar.getInstance();
            Calendar tmpCalendar = Calendar.getInstance();
            tmpCalendar.setTimeInMillis(time);
            return now.get(Calendar.DAY_OF_YEAR) == tmpCalendar
                    .get(Calendar.DAY_OF_YEAR);

        }
        return false;
    }

    public static boolean isYesterday(long time) {
        if (System.currentTimeMillis() - time < 48 * 60 * 60 * 1000) {
            Calendar now = Calendar.getInstance();
            Calendar tmpCalendar = Calendar.getInstance();
            tmpCalendar.setTimeInMillis(time);
            return (now.get(Calendar.DAY_OF_YEAR) - 1) == tmpCalendar
                    .get(Calendar.DAY_OF_YEAR);

        }
        return false;
    }

    public static int dpToPx(int dp) {
        final float scale = ApplicationEx.getInstance().getResources().getDisplayMetrics().density;
        return dp == 0 ? 0 : (int) (dp * scale + 0.5f);
    }

    public static int dpToPx(Context context, int dp) {
        final float scale = ApplicationEx.getInstance().getResources().getDisplayMetrics().density;
        return dp == 0 ? 0 : (int) (dp * scale + 0.5f);
    }

    public static int pxToDp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return px == 0 ? 0 : (int) (px / scale + 0.5f);
    }

    public static Drawable getApkIcon(Context context, String apkPath) {
        Drawable dIcon = null;
        if (TextUtils.isEmpty(apkPath)) {
            return null;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageArchiveInfo(apkPath,
                            PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                if (Build.VERSION.SDK_INT >= 8) {
                    appInfo.sourceDir = apkPath;
                    appInfo.publicSourceDir = apkPath;
                }
                dIcon = appInfo.loadIcon(context.getPackageManager());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }

        if (null == dIcon) {
            dIcon = context.getResources().getDrawable(
                    android.R.drawable.sym_def_app_icon);
        }

        return dIcon;
    }

    public static String getNameByPackage(Context context, String packname) {
        PackageManager pm;
        String name = null;
        if (TextUtils.isEmpty(packname)) {
            return null;
        }
        try {
            pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packname,
                    PackageManager.GET_UNINSTALLED_PACKAGES);

            if (null == info)
                return null;
            name = info.loadLabel(pm).toString();

            return name;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getApkName(Context context, String apkPath) {
        String name = "";
        PackageInfo packageInfo = context.getPackageManager()
                .getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            if (Build.VERSION.SDK_INT >= 8) {
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
            }
            name = appInfo.loadLabel(context.getPackageManager()).toString();
        }

        if (name.equals("")) {
            File f = new File(apkPath);
            name = f.getName();
        }

        return name;
    }

    public static int getAppVersion(Context context, String pkgName) {
        int v = 0;

        if (TextUtils.isEmpty(pkgName)) {
            return 0;
        }

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(pkgName,
                    PackageManager.GET_CONFIGURATIONS);

            if (null == info)
                return 0;

            v = info.versionCode;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return v;
    }

    public static String getApkVersion(Context context, String apkPath) {
        String v = "";

        if (TextUtils.isEmpty(apkPath)) {
            return "";
        }

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                    PackageManager.GET_ACTIVITIES);

            if (null == info)
                return "";

            v = info.versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    public static String replacepathutilchar(String str, String packagename) {
        char c = str.charAt(0);
        char c1 = str.charAt(1);
        String path = "";
        String pathutil1 = "/Android/data/" + packagename + "/cache";
        String pathutil2 = "/Android/data/" + packagename + "/files";
        String pathutil3 = "/Android/data/" + packagename + "/";
        if (c == '@') {
            switch (c1) {
                case '1':
                    path = new StringBuilder(str.length() - 2 + pathutil1.length())
                            .append(pathutil1).append(str.substring(2)).toString();
                    break;
                case '2':
                    path = new StringBuilder(str.length() - 2 + pathutil2.length())
                            .append(pathutil2).append(str.substring(2)).toString();
                    break;
                case '3':
                    path = new StringBuilder(str.length() - 2 + pathutil3.length())
                            .append(pathutil3).append(str.substring(2)).toString();
                    break;
                default:
                    path = str;
                    break;
            }
        } else {
            path = str;
        }
        return path;
    }

    /**
     * MD5加密
     *
     * @param originalStr ---- 需要加密的字符串
     * @return ---- 加密后的字符串
     */
    public static String MD5Encode(String originalStr) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] result = messageDigest.digest(originalStr.getBytes()); // 得到加密后的字符组数
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                int num = b & 0xff; // 这里的是为了将原本是byte型的数向上提升为int型，从而使得原本的负数转为了正数
                String hex = Integer.toHexString(num); // 这里将int型的数直接转换成16进制表
                // 16进制可能是为1的长度，这种情况下，需要在前面补0，
                if (hex.length() == 1) {
                    sb.append(0);
                }
                sb.append(hex);
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String filter(String content) {
        if (content != null && content.length() > 0) {
            char[] contentCharArr = content.toCharArray();
            for (int i = 0; i < contentCharArr.length; i++) {
                if (contentCharArr[i] < 0x20 || contentCharArr[i] == 0x7F) {
                    contentCharArr[i] = 0x20;
                }
            }
            return new String(contentCharArr);
        }
        return "";
    }

    public static Intent openFile(String filePath) {

        File file = new File(filePath);
        if (!file.exists())
            return null;
        /* 取得扩展名 */
        String end = file
                .getName()
                .substring(file.getName().lastIndexOf(".") + 1,
                        file.getName().length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            return getAudioFileIntent(filePath);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            return getAudioFileIntent(filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            return getImageFileIntent(filePath);
        } else if (end.equals("apk")) {
            return getApkFileIntent(filePath);
        } else if (end.equals("ppt")) {
            return getPptFileIntent(filePath);
        } else if (end.equals("xls")) {
            return getExcelFileIntent(filePath);
        } else if (end.equals("doc")) {
            return getWordFileIntent(filePath);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(filePath);
        } else if (end.equals("chm")) {
            return getChmFileIntent(filePath);
        } else if (end.equals("txt") || end.equals("json")) {
            return getTextFileIntent(filePath, false);
        } else {
            return getAllIntent(filePath);
        }
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {

        Uri uri = Uri.parse(param).buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    public static long[] getHMStringByTime(long time) {
        long HOUR = 60 * 60 * 1000;
        long MINUTE = 60 * 1000;
        long hour = time / HOUR;
        long minute = ((time % HOUR) / MINUTE) + 1;
        return new long[]{hour, minute};
    }

    public static String getTimeString(long time, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString,
                Locale.ENGLISH);
        Date date = new Date(time);
        return format.format(date);
    }

    public static String getTodayString() {
        try {
            Date today = new Date();
            String a = String.format(Locale.US, "%tb", today);
            String h = String.format(Locale.US, "%te", today);
            String e = String.format("%tY", today);
            return a + " " + h + ", " + e;
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    packagename, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }


    public static boolean isAppRunning(Context context, String packagename) {
        boolean result = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (RunningAppProcessInfo info : list) {
            if (info.processName.equals(packagename)) {
                result = true;
                //find it, break
                break;
            }
        }
        if (!result) {
            List<RunningServiceInfo> list1 = am.getRunningServices(500);
            for (RunningServiceInfo info : list1) {
                ComponentName comp = info.service;
                if (packagename.equals(comp.getPackageName())) {
                    result = true;
                    break;
                }
            }
        }
        //pc
//        if (packagename.equals("")) {
//            result = false;
//        }
        return result;
    }

   /* public static String formatLelel(Context context, int level) {
        return WifiManager.calculateSignalLevel(level, 101) + context.getString(R.string.percent);
    }*/

    public static String formatFileSize(long size) {
        if (size < 0) return "0b";
        if (size < 1024) return size + "b";
        if (size < 1024 * 1024) return size / 1024 + "Kb";

        if (size < 1024 * 1024 * 1024) {
            int x = (int) (size / 1024 / 1024);
            if (x < 3) {
                return String.format(Locale.US, "%.1fMb", size * 1f / 1024 / 1024);
            } else {
                return size / 1024 / 1024 + "Mb";
            }
        }
        if (size < 1024 * 1024 * 1024 * 1024) return size / 1024 / 1024 / 1024 + "Gb";
        return size / 1024 / 1024 / 1024 / 1024 + "Tb";
    }

    public static String formatSpeed(long size) {
        float i = 0;
        //将单位转换成bps
        size = size * 8;
        String format;
        if (size < 0) {
            return "0.00bps";
        } else if (size < 1000) {
            return size + "bps";
        } else if (size < 1024 * 1000) {
            i = size / 1024f;
            if (i < 10) {
                format = "%.2f";
            } else if (i < 100) {
                format = "%.1f";
            } else {
                format = "%.0f";
            }
            return String.format(format + "Kbps", i);
        } else if (size < 1024 * 1024 * 1000) {
            i = size / 1024f / 1024;
            if (i < 10) {
                format = "%.2f";
            } else if (i < 100) {
                format = "%.1f";
            } else {
                format = "%.0f";
            }
            return String.format(format + "Mbps", i);
        } else if (size < 1024 * 1024 * 1024 * 1000L) {
            i = size / 1024f / 1024 / 1024;
            if (i < 10) {
                format = "%.2f";
            } else if (i < 100) {
                format = "%.1f";
            } else {
                format = "%.0f";
            }
            return String.format(format + "Gbps", i);
        } else {
            i = size / 1024f / 1024 / 1024 / 1024;
            if (i < 10) {
                format = "%.2f";
            } else if (i < 100) {
                format = "%.1f";
            } else {
                format = "%.0f";
            }
            return String.format(format + "Tbps", i);
        }
    }

    public static String formatDataUseSize(long size) {
        float i = 0;
        String format;
        if (size < 0) {
            return "0.00B";
        } else if (size < 1000) {
            return size + "B";
        } else if (size < 1024 * 1000) {
            i = size / 1024f;
            if (i < 10) {
                format = "%.2f";
            } else if (i < 100) {
                format = "%.1f";
            } else {
                format = "%.0f";
            }
            return String.format(format + "KB", i);
        } else if (size < 1024 * 1024 * 1000) {
            i = size / 1024f / 1024;
            if (i < 10) {
                format = "%.2f";
            } else if (i < 100) {
                format = "%.1f";
            } else {
                format = "%.0f";
            }
            return String.format(format + "MB", i);
        } else if (size < 1024 * 1024 * 1024 * 1000L) {
            i = size / 1024f / 1024 / 1024;
            if (i < 10) {
                format = "%.2f";
            } else if (i < 100) {
                format = "%.1f";
            } else {
                format = "%.0f";
            }
            return String.format(format + "GB", i);
        } else {
            i = size / 1024f / 1024 / 1024 / 1024;
            if (i < 10) {
                format = "%.2f";
            } else if (i < 100) {
                format = "%.1f";
            } else {
                format = "%.0f";
            }
            return String.format(format + "TB", i);
        }
    }

    /**
     * 判断一个时间是不是今天
     *
     * @param time
     * @return
     */
    public static boolean thisTimeIsToday(long time) {
        return System.currentTimeMillis() / DAY == time / DAY;
    }

    /*public static String formatDataUseSize(long size){

        String str;
        if (size<1024) str = "B";
        else if (size<1024*1024) str = "KB";
        else if (size<1024*1024*1024) str = "MB";
        else if (size<1024*1024*1024*1024) str = "GB";
        else str = "TB";
        float i = size;
        if (size>0)
            i = size%1024f;
        while (i>1024){
            i = i/1024f;
        }
        if (i<10) return  String.format("%.2f"+str,i);
        if (i<100) return  String.format("%.1f"+str,i);
        return (int)i+str;
    }*/


    public static final long MINUTE = 60000;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long MONTH = 30 * DAY;
    public static final long JUST_DURING = MINUTE;// 1分鐘(剛剛)
    public static final SimpleDateFormat duringFormat = new SimpleDateFormat("dd MMM yyy HH:mm", Locale.ENGLISH);

    /**
     * 获取动态时间格式（刚刚、xx分钟前、xx小时前...）
     *
     * @param context
     * @param time
     * @return
     */
    public static String getDuringTime(Context context, long time) {
        long during = System.currentTimeMillis() - time;
        if (during < 0) {
            return duringFormat.format(new Date(time));
        }
        if (during <= JUST_DURING) {
            return context.getString(R.string.device_time_just);
        } else if (during <= HOUR) {
            return context.getString(R.string.device_time_minutes, during / MINUTE);
        } else if (during <= DAY) {
            int hour = (int) (during / HOUR);
            if (hour > 1) {   // 复数形式
                return context.getString(R.string.device_time_hour, hour);
            } else {          // 单数形式
                return context.getString(R.string.device_time_one_hour);
            }
        } else if (during <= MONTH) {
            int day = (int) (during / DAY);
            if (day > 1) {   // 复数形式
                return context.getString(R.string.device_time_day, day);
            } else {         // 单数形式
                return context.getString(R.string.device_time_one_day);
            }
        } else {
            return duringFormat.format(new Date(time));
        }
    }

    /**
     * 根据语言格式化数字
     *
     * @param context
     * @param number
     * @return
     */
    public static String formatNumber(Context context, float number) {
        if (context == null)
            return "";
        try {
            return String.format(LanguageSettingUtil.getLocale(context), "%.0f", number);
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * 根据语言格式化数字
     *
     * @param context
     * @param number
     * @return
     */
    public static String formatNumber(Context context, String format, float number) {
        if (context == null)
            return "";

        try {
            return String.format(LanguageSettingUtil.getLocale(context), format, number);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取文本长度
     *
     * @param text
     * @return
     */
    public static float getTextWidth(String text) {
        Paint paint = new Paint();
        return paint.measureText(text);
    }


    public static String formatTime(Context context, long time) {
        String result;
        if (time / 1000 < 60) {
            result = String.format(context.getString(R.string.time_s), time / 1000);
        } else if (time / 1000 / 60 < 60) {
            result = String.format(context.getString(R.string.time_m), time / 1000 / 60, time / 1000 % 60);
        } else {
            result = String.format(context.getString(R.string.time_h), time / 1000 / 60 / 60, time / 1000 / 60 % 60, time / 1000 % 3600);
        }
        return result;
    }

    public static int getBoostPercent(long flow) {
        int result;
        int M = 1024 * 1024;
        /*if (flow>100*M){
            result = 5;
        }else */
        if (flow > 100 * M) {
            result = 3;
        } else if (flow > 10 * M) {
            result = 2;
        } else {
            result = 1;
        }
        return result;
    }

    /**
     * 将阿拉伯数字string转换成正常阿拉伯数字
     *
     * @param chineseNumber
     */
    public static String Arabic2IntString(String chineseNumber) {
        boolean flag;
        StringBuffer stringBuffer = new StringBuffer();
        char[] arabicArrs = new char[]{'٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};
        char[] Integers = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] chars = chineseNumber.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            flag = true;
            for (int j = 0; j < arabicArrs.length; j++) {
                if (chars[i] == arabicArrs[j]) {
                    stringBuffer.append(Integers[j]);
                    flag = false;
                }
            }

            if (flag) {
                stringBuffer.append(chars[i]);
            }

        }


        return stringBuffer.toString();
    }

//    public static void setbackSvg(View viewSelected, Context mcontext, int svg, float size) {
//        FontIconDrawable svgDrawable = FontIconDrawable.inflate(mcontext, svg);
//        svgDrawable.setTextSize(Stringutil.dpToPx(mcontext, size));
//        viewSelected.setBackgroundDrawable(svgDrawable);
//    }
//
//    public static void setSvg(View viewSelected, Context mcontext, int svg, float size) {
//        TypedArray styles = mcontext.getTheme().obtainStyledAttributes(new int[]{R.attr.icon_color});
//        int iconColor = styles.getColor(0, mcontext.getResources().getColor(R.color.battery_green_light));
//        styles.recycle();
//        FontIconDrawable svgDrawable = FontIconDrawable.inflate(mcontext, svg);
//        svgDrawable.setTextColor(iconColor);
//        svgDrawable.setTextSize(Stringutil.dpToPx(mcontext, size));
//        viewSelected.setBackgroundDrawable(svgDrawable);
//    }


    /**
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
     *
     * @param sortKey 数据库中读取出的sort key
     * @return 英文字母或者#
     */
    public static String getSortKey(String sortKey) {
        String key = "";
        if (!TextUtils.isEmpty(sortKey) && sortKey.length() > 0) {
            key = sortKey.substring(0, 1).toUpperCase();
            if (key.matches("[A-Z]")) {
                return key;
            }
        }

        return "#";
    }

    public static int getASCII(String sortKey) {
        if ("#".equals(sortKey)) {
            return "Z".toUpperCase().charAt(0) + 1;
        } else if ("★".equals(sortKey)) {
            return "A".toUpperCase().charAt(0) - 1;
        } else {
            if (sortKey.length() > 0) {
                return sortKey.toUpperCase().charAt(0);
            } else {
                return "Z".toUpperCase().charAt(0) + 1;
            }
        }
    }

    public static boolean isEnglisthLetter(String txt) {
        if (TextUtils.isEmpty(txt)) {
            return false;
        }
        Pattern p = Pattern.compile("^[a-zA-Z]+$");
        Matcher m = p.matcher(txt.substring(0, 1));
        if (m.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是纯数字
     */
    public static boolean isNumeric(String str) {
        if (str.startsWith("+")) {
            str = str.substring(1);
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean equalsWithoutNull(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        return TextUtils.equals(str1, str2);
    }


    /**
     * 合并多个数组为一个数组
     */
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * @param paramArray :被抽取数组
     * @param count      :抽取元素的个数
     * @function:从数组中随机抽取若干不重复元素
     * @return:由抽取元素组成的新数组
     */
    public static int[] getRandomArray(int[] paramArray, int count) {
        if (paramArray.length < count) {
            return paramArray;
        }
        int[] newArray = new int[count];
        Random random = new Random();
        int temp = 0;//接收产生的随机数
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= count; i++) {
            temp = random.nextInt(paramArray.length);//将产生的随机数作为被抽数组的索引
            if (!(list.contains(temp))) {
                newArray[i - 1] = paramArray[temp];
                list.add(temp);
            } else {
                i--;
            }
        }

        List<Integer> newList = new ArrayList<>();
        for (int a : newArray) {
            newList.add(a);
        }
        Collections.shuffle(newList);
        int[] newArray2 = new int[newList.size()];
        for (int i = 0; i < newList.size(); i++) {
            newArray2[i] = newList.get(i);
        }

        return newArray2;
    }

    /**
     * 从一段字符串中获取随机长度的字符串
     */
    public static String getRandomStr(String str) {
        if (TextUtils.isEmpty(str) || str.length() < 5) return null;
        int randomInt = 5 + new Random().nextInt(str.length());
        if (randomInt >= str.length()) {
            return str;
        } else {
            return str.substring(randomInt);
        }
    }
}
