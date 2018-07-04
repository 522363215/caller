package blocker.call.wallpaper.screen.caller.ringtones.callercolor.update;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/15.
 */
public class Util {
    public static Map getBaseParam(Context context) {
        Map param = new HashMap();
        param.put("ch", getChannel(context));
        param.put("pkg_name", context.getPackageName());// com.make.money
        param.put("ver", getApkVersion(context)); // 1-->GP; 2--> apk
        param.put("os_ver", getAndroidOsVersion());
        param.put("api_level", Build.VERSION.SDK_INT);
        param.put("android_id", getAndroidID(context));
        Locale locale = context.getResources().getConfiguration().locale;
    	param.put("language", locale.toString());
    	
        return param;
    }

    public static String getAndroidID(Context context) {
        String id = Settings.Secure.getString(context.getApplicationContext()
                .getContentResolver(), Settings.Secure.ANDROID_ID);
        if (id == null) {
            id = "";
        }
        return id;
    }

    public static int getApkVersion(final Context context) {
        int version = Integer.MAX_VALUE;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getAndroidOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getChannel(Context mContext) {
        String channel = "";
        try {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), 128);
            channel = info.metaData.getString("PREF_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == channel || "".equals(channel.trim())) {
            return "googleplay";
        } else {
            return channel;
        }
    }

    public static UpdateInfo getUpdateInfo(Context context, Locale locale) {
        UpdateInfo info = new UpdateInfo();
        if (isNetworkConnected(context)) {
        	try{
            	Map param = getBaseParam(context);
            	param.put("language", locale.toString());
            	
                JSONObject obj = HttpUtilUpdate.doPost(HttpUtilUpdate.UPDATE_URL, param);
                if (0 == obj.getInt("code")) {
                    JSONObject jsonData = obj.getJSONObject("data");
                    info.setUrl(jsonData.getString("url"));
                    info.setDescription(jsonData.getString("description"));
                    info.setForceUpdate(jsonData.getBoolean("forceUpdate"));
                    info.setIsGooglePlay(jsonData.getBoolean("isGooglePlay"));
                    info.setTitle(jsonData.getString("title"));
                    info.setUpdatable(jsonData.getBoolean("updatable"));
                    info.setNewestVersion(jsonData.getInt("newestVersion"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return info;
    }

    /**
     * 判断网络是否连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    //检测系统是否有App Usage权限设置
    public static boolean hasUsageAccessSetting(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }catch (Exception e){
            return true;
        }
    }

    @SuppressLint("NewApi")
    public static boolean isUsageStatsPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return true;
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !hasUsageAccessSetting(context))
            return true;
        else {
            String GET_USAGE_STATS = AppOpsManager.OPSTR_GET_USAGE_STATS;
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOpsManager.checkOpNoThrow(GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
                return (mode == AppOpsManager.MODE_ALLOWED);
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }
    }

    //@Target()
    public static boolean UsagetatsPermissionIsBlocking(Context context){
        if (Build.VERSION.SDK_INT < 21)
            return false;
        else if (Build.VERSION.SDK_INT >= 21 && !hasUsageAccessSetting(context))
            return false;
        else {
            String GET_USAGE_STATS= "android:get_usage_stats";
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOpsManager.checkOpNoThrow(GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
                return  (mode != AppOpsManager.MODE_ALLOWED);
            } catch (PackageManager.NameNotFoundException e) {
                return true;
            } catch (Exception e){
                return true;
            }
        }
    }
}
