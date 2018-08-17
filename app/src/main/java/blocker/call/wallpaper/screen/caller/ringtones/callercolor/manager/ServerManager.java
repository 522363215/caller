package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.common.sdk.analytics.AnalyticsManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.ServerParamBean;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhq on 2017/1/24.
 */

public class ServerManager {
    private static final String TAG = "cpservice";
    private static ServerManager sInstance;
    private OkHttpClient mClient;

    private ServerManager() {
        init();
    }

    public static ServerManager getInstance() {
        if (sInstance == null) {
            synchronized (ServerManager.class) {
                sInstance = new ServerManager();
            }
        }
        return sInstance;
    }

    private void init() {
        mClient = new OkHttpClient.Builder()
                .connectTimeout(7, TimeUnit.SECONDS)
                .readTimeout(7, TimeUnit.SECONDS)
                .build();
    }

    public void requestData(JSONObject json, String uri, final CallBack callBack) {
        if (json == null) return;
        String sig = Stringutil.MD5Encode(ConstantUtils.KEY_HTTP + json.toString());
//        LogUtil.d("ccooler", "requestTag sig: " + sig);
//        LogUtil.d("ccooler", "requestTag req data: " + json.toString());
        RequestBody formBody = new FormBody.Builder()
                .add("data", json.toString())
                .add("sig", sig)
                .build();

        final Request request = new Request.Builder()
                .url(uri)
                .post(formBody)
                .build();

        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseError(callBack);
                LogUtil.d("cpservice", "ccooler onFailure:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                LogUtil.d("ccooler", "requestData response.code(): " + response.code());
                if (response.code() == 200) {
//                    LogUtil.d("ccooler", "requestData succ: " + response.body().string());
                    responseSuc(callBack, response.body().string());
                } else {
                    responseError(callBack);
                }
            }
        });
    }

    private void responseSuc(CallBack callBack, String data) {
        if (callBack != null) {
            callBack.onRequest(true, data);
        }
    }

    private void responseError(CallBack callBack) {
        if (callBack != null) {
            callBack.onRequest(false, null);
        }
    }

    public interface CallBack {
        void onRequest(boolean hasSuccess, String data);
    }

    //control params from server
    public void getParamFromServer() {
        try {
            long now = System.currentTimeMillis();
            long last_req = ApplicationEx.getInstance().getGlobalSettingPreference().getLong(ConstantUtils.PREF_KEY_UPDATE_PARAM_TIME, 0);
            long interval = 60 * 1000 * 60 * 1; //2 hours, >1
            if (last_req == 0 || Math.abs(now - last_req) > interval) {
                requestControlParam(ApplicationEx.getInstance(), ConstantUtils.SERVER_API_PARAM);
                LogUtil.d("cpservice", "getParamFromServer server: " + ConstantUtils.SERVER_API_PARAM);
            }
        } catch (Exception e) {
            LogUtil.e("cpservice", "getParamFromServer exception: " + e.getMessage());
        }
    }

    public void requestControlParam(Context context, String uri) {
        JSONObject object = new JSONObject();
        try {
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            LogUtil.d("cpservice", "requestControlParm androidId:" + androidId);
            object.put("action", "get_config");
            object.put("aid", androidId);
            object.put("cid", ConstantUtils.CALLER_STATISTICS_CHANNEL);
            object.put("timezone", TimeZone.getDefault().getDisplayName(Locale.ENGLISH));
            object.put("pkg_name", context.getPackageName());

            SharedPreferences setting = ApplicationEx.getInstance().getGlobalSettingPreference();
            String from = setting.getString("from", "");

            String referrer = setting.getString("referrer", "");

            object.put("from", from);

            if (!"".equals(referrer)) {
                object.put("referrer", referrer);
            }


            PackageInfo pkg;
            try {
                pkg = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                int version = pkg.versionCode;
                object.put("ver", version);
            } catch (PackageManager.NameNotFoundException e1) {
                e1.printStackTrace();
            }

            object.put("model_code", DeviceUtil.getDeviceModel());
            object.put("os_ver", DeviceUtil.getOSVersion());
            object.put("ch", AnalyticsManager.getCh());
            String sub_ch = setting.getString("sub_ch", "");
            object.put("sub_ch", AnalyticsManager.getSubCh());


        } catch (Exception e) {
            LogUtil.e("cpservice", "requestControlParm data excption:" + e.getMessage());
        }
        String sig = Stringutil.MD5Encode(ConstantUtils.KEY_HTTP + object.toString());
        LogUtil.d("cpservice", "sig: " + sig);
        LogUtil.d("cpservice", "data req: " + object.toString());
        RequestBody formBody = new FormBody.Builder()
                .add("data", object.toString())
                .add("sig", sig)
                .build();

        final Request request = new Request.Builder()
                .url(uri)
                .post(formBody)
                .build();

        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("cpservice", "requestControlParam onFailure:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    processParam(response.body().string());
                } else {
                    LogUtil.d("cpservice", "onResponse not 200: " + response.code());
                }
            }
        });
    }

    private void processParam(String data) {
        ServerParamBean bean = null;
        try {
            if (!TextUtils.isEmpty(data)) {
//                LogUtil.d("cidserver", "processParam data: " + data);

            }
            Gson gson = new Gson();
            bean = gson.fromJson(data, ServerParamBean.class);
            if (bean != null && bean.getStatus().getCode() == 0) {
                if (bean.getDataBean() != null) {

                    ServerParamBean.DataBean dataBean = bean.getDataBean();
                    SharedPreferences pref = ApplicationEx.getInstance().getGlobalSettingPreference();
                    SharedPreferences ad_pref = ApplicationEx.getInstance().getGlobalADPreference();


                    //server true time
                    String tm = bean.getStatus().getTimestamp();
                    String tm_ms = bean.getStatus().getMillisecond();
                    ad_pref.edit().putLong("true_time_from_server", Long.valueOf(tm_ms)).apply();

                    LogUtil.d("cpservice", "splash_fb_id:"+dataBean.splash_fb_id);

                    ad_pref.edit().putInt("pref_is_auto_go_main", dataBean.is_auto_go_main).apply();
                    ad_pref.edit().putInt("pref_is_show_ad_end_call", dataBean.is_show_ad_end_call).apply();

                    //first sync time
                    if (pref.getLong("key_cid_first_sync_server_time", 0) <= 0) {
                        pref.edit().putLong("key_cid_first_sync_server_time", Long.valueOf(tm_ms)).apply();
                    }


                } else {
                    LogUtil.d("cpservice", "processParam getDataBean is null.");
                }
            } else {
                LogUtil.d("cpservice", "processParam bean is null.");
            }
        } catch (Exception e) {
            LogUtil.e("cpservice", "processParam exception:" + e.getMessage());
        }
    }


}
