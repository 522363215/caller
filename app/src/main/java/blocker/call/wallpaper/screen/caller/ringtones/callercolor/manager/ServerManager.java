package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
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
    private static final String TAG = "ServerManager";
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
                LogUtil.d("ccooler", "ccooler onFailure:" + e.getMessage());
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

}
