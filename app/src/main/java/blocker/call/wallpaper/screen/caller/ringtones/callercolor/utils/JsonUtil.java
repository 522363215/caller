package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;

/**
 * Created by zhq on 2017/1/24.
 * <p>
 * <p>
 * <p>
 * {
 * "action": "caller_number_update",
 * "numberinfolist": [
 * {
 * "number": "8602195512",
 * "ccode": "86",
 * "operators": "china telecom",
 * "country": "en",
 * "areacode": "028",
 * "location": "china chengdu",
 * "type": 0,
 * "numbertype": 0,
 * "title": "facebook",
 * "deviceid": "1232",
 * "block_type": 0,
 * "hide_type": 0,
 * "updatetime": "1485072833",
 * "userid": "",
 * "taglist": [
 * {
 * "tagcode": "sdf",
 * "tagcount": 11,
 * "tagaddcount": 111
 * },
 * {
 * "tagcode": "sdf",
 * "tagcount": 11,
 * "tagaddcount": 111
 * }
 * ]
 * }
 * ]
 * }
 */

public class JsonUtil {
    private static Context mContext = ApplicationEx.getInstance();

    public static JSONObject createSuggestionJson(String content, String mail, String score) {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "feedback");
            object.put("category", "feedback");

            String androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            object.put("aid", androidId);
            object.put("cid", ConstantUtils.CALLER_STATISTICS_CHANNEL);
            object.put("timezone", TimeZone.getDefault().getDisplayName(Locale.ENGLISH));
            object.put("lang", LanguageSettingUtil.getLocale(mContext).getLanguage());
            PackageInfo pkg;
            try {
                pkg = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                int version = pkg.versionCode;
                object.put("ver", version);
            } catch (PackageManager.NameNotFoundException e1) {
                e1.printStackTrace();
            }
            object.put("pkg_name", mContext.getPackageName());
            object.put("model_code", DeviceUtil.getDeviceModel());
            object.put("os_ver", DeviceUtil.getOSVersion());

            object.put("content", content);
            object.put("contact", mail);
            object.put("score", score);

        } catch (JSONException e) {
            LogUtil.e("JSONException", e.getMessage());
            e.printStackTrace();
        }
        return object;
    }


}
