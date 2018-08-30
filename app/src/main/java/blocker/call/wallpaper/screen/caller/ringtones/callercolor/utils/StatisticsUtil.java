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
    public final static String SERVER_API_ANALYSIS_INTERFACE = "http://analysis.jedimobi.com/api.php";

    public static void sendBaseData(final Context context) {
        sendData(context, false);
    }

    public static void sendMainData(final Context context) {
        sendData(context, true);
    }

    public static void sendData(final Context context, final boolean bMain) {
        final ApplicationEx app = (ApplicationEx) context.getApplicationContext();
        SharedPreferences setting = app.getGlobalSettingPreference();
        final int used_day = bMain ? setting.getInt("used_day", 0) : setting.getInt("used_day_base", 0);
        if (used_day != Stringutil.getTodayDayInYearGMT8()) {
            if (!(bMain ? isPostingMainData : isPostingBaseData)) {
                if (bMain)
                    isPostingMainData = true;
                else
                    isPostingBaseData = true;
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        //如果是第一次进入的时候，延迟15秒发送
                        if (used_day == 0)
                            SystemClock.sleep(15000);
                        postData(app, bMain);
                    }
                }).start();
            }
        }
    }

    private static void postData(ApplicationEx context, boolean bMain) {
        JSONObject object = new JSONObject();

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            object.put("aid", androidId);

            SharedPreferences setting = ApplicationEx.getInstance().getGlobalSettingPreference();
            String channel = getChannel();
            LogUtil.d("postData channel", channel);
            String from = setting.getString("from", "");
            String sub_ch = setting.getString("sub_ch", "");
            object.put("sub_ch", sub_ch);
            String referrer = setting.getString("referrer", "");

            if (!channel.equals("")) {
                object.put("ch", channel);
            }

            if (TextUtils.isEmpty(from)) {
                if (TextUtils.isEmpty(channel)) {
                    from = "empty";
                } else {
                    from = channel;
                }

                setting.edit().putString("from", from).commit();
            }

            object.put("from", from);

            if (!"".equals(referrer)) {
                object.put("referrer", referrer);
            }

            //给AdWords推广用的标志
            if (bMain) {
                try {
                    String gaid = "";
                    AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    boolean isLimitTrackEnabled = adInfo.isLimitAdTrackingEnabled();
                    if (!isLimitTrackEnabled) {
                        gaid = adInfo.getId();
                    }
                    object.put("gaid", gaid);
                    object.put("ad_tracking", !isLimitTrackEnabled);
                } catch (Exception e) {
                    object.put("gaid", "");
                    object.put("ad_tracking", false);
                }
            }

            if (bMain)
                object.put("type", "aid_sig");
            else
                object.put("type", "aid_sig_base");
            object.put("client", ConstantUtils.CALLER_STATISTICS_CHANNEL);
            PackageInfo pkg;
            try {
                pkg = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                int version = pkg.versionCode;
                object.put("ver", version);
            } catch (PackageManager.NameNotFoundException e1) {
                e1.printStackTrace();
            }

            object.put("model", DeviceUtil.getDeviceModel());
//            object.put("imei", DBAssistant.encrypt(DeviceUtil.getIMEI(context)));
            object.put("osver", DeviceUtil.getOSVersion());

            String sig = Stringutil.MD5Encode(ConstantUtils.KEY_HTTP + object.toString());

            RequestBody formBody = new FormBody.Builder()
                    .add("data", object.toString())
                    .add("sig", sig)
                    .build();
            Request request = new Request.Builder().url(SERVER_API_ANALYSIS_INTERFACE).post(formBody).build();

            OkHttpClient client = new OkHttpClient();
            Response res = client.newCall(request).execute();
            if (res.isSuccessful()) {
                String result = res.body().string();
                if (result.equals("0")) {
                    if (bMain)
                        setting.edit().putInt("used_day", Stringutil.getTodayDayInYearGMT8()).commit();
                    else
                        setting.edit().putInt("used_day_base", Stringutil.getTodayDayInYearGMT8()).commit();
                    LogUtil.d("ccooler", "post statistic result: " + result);
                }
                res.body().close();
//                LogUtil.d("get channel", "statistics postData get channel: "+StatisticsUtil.getChannel());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bMain)
            isPostingMainData = false;
        else
            isPostingBaseData = false;
    }

    public static String getChannel() {
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
                ApplicationInfo info = ApplicationEx.getInstance().getPackageManager().getApplicationInfo(ApplicationEx.getInstance().getPackageName(), 128);
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

    public static String getSubChannel() {
        String sub_ch = "";
        if (ApplicationEx.getInstance() != null) {
            sub_ch = PreferenceHelper.getString("sub_ch", "");
        }

        return sub_ch;
    }
}
