package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.provider.Settings;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;

public class HttpUtil {
    public static final int ACTION_TYPE_SHOW = 1;
    public static final int ACTION_TYPE_CLICK = 2;
    public final static String NOTIFICATION_TYPE_SHOW = "notification_show";
    public final static String NOTIFICATION_TYPE_CLICK = "notification_click";
    public final static String NOTIFICATION_ACTION = "notification_static";
    public final static String NOTIFICATION_API_INTERFACE = "http://notification.lionmobi.com/viewSelected/portal/api.php";


    public static void postNofityData(final Context context, final int actionType, final String notificationType, final AjaxCallback<JSONObject> ajaxCallback) {
        JSONObject object = new JSONObject();
        try {
            if (makeBasicParam4Json(context, object)) {
                TimeZone tz = Calendar.getInstance().getTimeZone();
                object.put("action", NOTIFICATION_ACTION);
                object.put("action_type", actionType);
                object.put("notification_type", notificationType);
                object.put("client", ConstantUtils.CALLER_STATISTICS_CHANNEL);
                object.put("timezone", tz.getID());

                String sig = Stringutil.MD5Encode(ConstantUtils.KEY_HTTP + object.toString());

                AQuery aq = new AQuery(context);
                Map<String, Object> params = new HashMap<>();
                params.put("data", object.toString());
                params.put("v", sig);
                aq.ajax(NOTIFICATION_API_INTERFACE, params, JSONObject.class, ajaxCallback);
            }
        } catch (Exception e) {
            LogUtil.e("liontools", "postNofityData exception: " + e.getMessage());

        }
    }

//	public static void  postWifiConfig(Context context, WifiConfigurationBean bean, AjaxCallback<JSONObject> ajaxCallback) {
//		AQuery aq = new AQuery(context);
//		Map<String, Object> params = new HashMap<>();
//		JSONObject object = new JSONObject();
//		try {
//			if(makeBasicParam4Json(context, object)) {
//				object.put("action", ConstantUtils.WIFI_INFO_ACTION);
//				object.put("b_ssid", bean.getRSSID());
//				object.put("ssid", bean.getSsid());
//				object.put("frequency", bean.getFrequency());
//				object.put("password", bean.getPassWord());
//				object.put("history_speed", bean.getHistorySpeed());
//				object.put("history_speed_time", bean.getHistorySpeedTime());
//				object.put("safe_test_result", bean.getSafeTestResult());
//				object.put("safe_test_time", bean.getSafeTestTime());
//				object.put("latitude", bean.getLatitude());
//				object.put("longitude", bean.getLongitude());
//
//				params.put("data", object.toString());
//				String sig = Stringutil.MD5Encode(object.toString() + "");
//				params.put("v", sig);
//				aq.ajax(ConstantUtils.SERVER_API_INTERFACE, params, JSONObject.class, ajaxCallback);
//			}
//		} catch (Exception e) {
//		}
//	}

    public static int pkgVersion(Context context) {
        PackageInfo pkg;
        int version = 0;
        try {
            pkg = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            version = pkg.versionCode;
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return version;
    }

    public static String getChannel(Context context) {
        String channel = PreferenceHelper.getString("channel", "");

        if ("".equals(channel)) {

            try {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
                channel = info.metaData.getString("channel");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!channel.equals("")) {
            if ("null".equals(channel))
                return "googleplay";
            else
                return channel;
        } else {
            return "googleplay";
        }
    }

    public static boolean makeBasicParam4Json(Context context, JSONObject object) {
        try {
            object.put("android_id", Settings.Secure.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID));
            object.put("ch", getChannel(context));
            object.put("pkg_name", context.getPackageName());
            object.put("ver", pkgVersion(context));
            object.put("os_ver", DeviceUtil.getOSVersion());
            object.put("api_level", Build.VERSION.SDK_INT);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}