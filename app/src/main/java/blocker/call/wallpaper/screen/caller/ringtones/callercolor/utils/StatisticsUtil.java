package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.mopub.test.util.AdvertisingIdClient;

import org.json.JSONObject;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by cattom on 1/5/2016.
 */
public class StatisticsUtil {
    // post base data
    private static boolean isPostingBaseData = false;
    private static boolean isPostingMainData = false;
    public final static String SERVER_API_ANALYSIS_INTERFACE = "http://analysis.lionmobi.com/api.php";

    public static String getChannel(Context context) {
        String channel = "";
        if (ApplicationEx.getInstance() != null) {
            // get from 'channel'
            channel = PreferenceHelper.getString("channel", null);
            // get from 'from'
            if (TextUtils.isEmpty(channel)) {
                channel = PreferenceHelper.getString("from", null);
            }
        }
        // get from Manifest
        if (TextUtils.isEmpty(channel)) {
            try {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
                channel = info.metaData.getString("channel");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 如果上述全部值都获取失败,则返回 empty 渠道
        if (TextUtils.isEmpty(channel)) {
            channel = "empty";
        }

        return channel;
    }

    public static String getSubChannel(Context context) {
        String sub_ch = "";
        if (ApplicationEx.getInstance() != null) {
            sub_ch = PreferenceHelper.getString("sub_ch", "");
        }

        return sub_ch;
    }
}
