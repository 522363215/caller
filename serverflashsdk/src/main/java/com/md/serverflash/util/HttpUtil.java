package com.md.serverflash.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Category;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.callback.CategoryCallback;
import com.md.serverflash.callback.SingleTopicThemeCallback;
import com.md.serverflash.callback.ThemeNormalCallback;
import com.md.serverflash.callback.ThemeSyncCallback;
import com.md.serverflash.callback.TopicThemeCallback;
import com.md.serverflash.local.ThemeSyncLocal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ChenR on 2018/5/7.
 */

public class HttpUtil {
    private static final String THEME_REQUEST_URL = "https://material.topsearchdomain.info/api.php";
    private static final String KEY_HTTP = "*2od2S!#%s";

    private static final String ACTION_CATEGORY_LIST = "get_category_list"; // 获取分类素材列表
    private static final String ACTION_PAGE_DATA = "get_page_data"; // 获取素材列表;
    private static final String ACTION_TOPIC_DATA = "get_topic_data"; // 获取素材列表;
    private static final String ACTION_JUST_LIKE = "just_like"; // 点赞;
    private static final String ACTION_TYPE_VERSION = "get_type_version"; // 获取分类版本;
    private static final String ACTION_TOPIC_LIST = "get_topic_list"; // 获取指定专题下的子专题列表;
    private static final String ACTION_TAG_LIST = "get_tag_list"; // 获取标签列表;
    private static final String ACTION_HOME_PAGE_DATA = "get_homepage_data"; // 获取主页数据, SL首页的组合数据, 包含 主题 + 推荐;
    private static final String ACTION_REPORT = "report"; // 素材举报;
    private static final String ACTION_SEARCH = "search"; // 搜索;
    private static final String ACTION_HOT_KEY = "get_hot_keys"; // 获取热门关键字;
    private static final String ACTION_EVENT_STATISTICS = "event_statistics"; // 事件统计;

    private static final Map<String, Boolean> mRequestController = new HashMap<>();

    private static String mLanguage;
    private static String mChannel;
    private static String mSubChannel;
    private static int mTestMode;

    public static void initHttpConfig(String language, String channel, String subChannel, int testMode) {
        mLanguage = language;
        mChannel = channel;
        mSubChannel = subChannel;
        mTestMode = testMode;
    }

    private static JSONObject getCommonJsonObject() {
        Map<String, String> params = new HashMap<>();

        params.put("language", mLanguage);
        params.put("cid", getCid());//
        params.put("aid", getAndroidId());
        params.put("ver", getVersionCode());
        params.put("os_ver", String.valueOf(Build.VERSION.SDK_INT));
        params.put("timezone", getTimeZoneId());
        params.put("pkg_name", getPkgName());
        params.put("mode_code", Build.MODEL);
        params.put("ch", mChannel);
        params.put("sub_ch", mSubChannel);

        return new JSONObject(params);
    }

    private static String getTimeZoneId() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getID();
    }

    private static String getPkgName() {
        Context context = ThemeSyncManager.getInstance().getContext();
        return context == null ? "" : context.getPackageName();
    }

    private static String getVersionCode() {
        int versionCode = 0;
        try {
            Context context = ThemeSyncManager.getInstance().getContext();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 128);
            versionCode = packageInfo.versionCode;
        } catch (Exception e) {

        }
        return String.valueOf(versionCode);
    }

    private static String getCid() {
        int clientId = 0;
        try {
            Context context = ThemeSyncManager.getInstance().getContext();
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            clientId = info.metaData.getInt("ClientID");
        } catch (Exception e) {

        }
        return String.valueOf(clientId);
    }

    private static String getAndroidId() {
        String androidId = "";
        try {
            Context context = ThemeSyncManager.getInstance().getContext();
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
        }
        return androidId;
    }

    public static void requestCategoryList(final CategoryCallback callback) {
        if (ThemeSyncManager.getInstance().getContext() == null) {
            return;
        }

        JSONObject jsonObject = getCommonJsonObject();
        try {
            jsonObject.put("action", ACTION_CATEGORY_LIST)
                    .put("test", mTestMode);
        } catch (JSONException e) {
            LogUtil.error(e);
        }
        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                Async.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onFailed(0, e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                int responseCode = response.code();
                LogUtil.d(LogUtil.TAG, "request category list response code: " + responseCode);
                if (responseCode == 200) {
                    try {
                        String string = response.body().string();
                        if (!TextUtils.isEmpty(string)) {
                            JSONObject responseObj = new JSONObject(string);
                            JSONObject status = responseObj.getJSONObject("status");
                            int statusCode = status.getInt("code");
                            String msg = status.getString("msg");
                            LogUtil.d(LogUtil.TAG, "requestCategoryList response, code: " + statusCode + ", msg: " + msg);

                            if (statusCode == 0) {
                                String data = responseObj.getString("data");
                                LogUtil.d(LogUtil.TAG, "requestCategoryList data: " + data);
                                ThemeSyncLocal.getInstance().markSingleJsonData(ThemeSyncLocal.PREF_SUFFIX_CATEGORY_LIST, data);

                                final List<Category> categoryList = new Gson().fromJson(data, new TypeToken<List<Category>>() {
                                }.getType());
                                if (callback != null) {
                                    Async.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onSuccess(200, categoryList);
                                        }
                                    });
                                }
                            } else {
                                onFailed(statusCode, msg);
                            }
                        } else {
                            onFailed(responseCode, "response body string is empty.");
                        }

                    } catch (Exception e) {
                        onFailed(0, e.getMessage());
                    }
                } else {
                    onFailed(responseCode, response.message());
                }
            }

            private void onFailed(final int statusCode, final String msg) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(statusCode, msg);
                        }
                    });
                }
            }
        });
    }

    /**
     * 素材列表;
     *
     * @param pageNumber     每页记录的数量
     * @param whichPage      第几页
     * @param categoryListId 对应分类列表id
     * @param fileType       文件类型 0=>全部 100 => 普通 102 => 全景 103=>视频 104=>3D
     */
    public static void requestPageData(int pageNumber, int whichPage, int categoryListId, int fileType, final ThemeSyncCallback callback) {
        JSONObject jsonObject = getCommonJsonObject();

        try {
            jsonObject.put("ps", pageNumber)
                    .put("pg", whichPage)
                    .put("obj_id", categoryListId)
                    .put("type", fileType)
                    .put("action", ACTION_PAGE_DATA)
                    .put("test", mTestMode);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(0, e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        String responseString = response.body().string();
                        if (!TextUtils.isEmpty(responseString)) {
                            JSONObject responseObj = new JSONObject(responseString);
                            JSONObject status = responseObj.getJSONObject("status");
                            int statusCode = status.getInt("code");
                            if (statusCode == 0) {
                                String data = responseObj.getString("data");
                                ThemeSyncLocal.getInstance().markSingleJsonData(ThemeSyncLocal.PREF_SUFFIX_PAGE_DATA, data);

                                if (callback != null) {
                                    final List<Theme> themeList = new Gson().fromJson(data, new TypeToken<List<Theme>>() {
                                    }.getType());

                                    if (mTestMode == 0) {
                                        Iterator<Theme> iterator = themeList.iterator();
                                        while (iterator.hasNext()) {
                                            Theme next = iterator.next();
                                            if (next.getIs_test().equals("1")) {
                                                iterator.remove();
                                            }
                                        }
                                    }

                                    Async.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onSuccess(themeList);
                                        }
                                    });
                                }
                            } else {
                                onFailed(statusCode, status.getString("msg"));
                            }

                        } else {
                            onFailed(response.code(), "response body string is empty.");
                        }
                    } catch (Exception e) {
                        onFailed(0, "requestPageData response exception: " + e.getMessage());
                    }
                } else {
                    onFailed(response.code(), response.message());
                }
            }

            private void onFailed(final int code, final String msg) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(code, msg);
                        }
                    });
                }
            }
        });
    }

    public static void requestTopicData (final String topic, int pageNumber, int pageLength, final SingleTopicThemeCallback callback) {
        if (TextUtils.isEmpty(topic)) {
            return;
        }

        StringBuffer topicList = new StringBuffer(topic);
        topicList.append(",").append(pageNumber).append(",").append(pageLength);
        JSONObject jsonObject = getCommonJsonObject();
        try {
            jsonObject.put("topic_list", topicList.toString())
                    .put("action", ACTION_TOPIC_DATA)
                    .put("test", mTestMode);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                callFailed(0, e.getMessage());
            }

            private void callFailed(final int code, final String msg) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(code, msg);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int responseCode = response.code();
                if (responseCode == 200) {
                    try {
                        String string = response.body().string();
                        JSONObject responseObj = new JSONObject(string);
                        JSONObject status = responseObj.optJSONObject("status");
                        int statusCode = status.getInt("code");
                        if (statusCode == 0) {
                            JSONObject data = responseObj.optJSONObject("data");
                            Gson gson = new Gson();
                            String dataJson = data.getString(topic);
                            final List<Theme> topicList = gson.fromJson(dataJson, new TypeToken<List<Theme>>(){}.getType());
                            if (topicList != null) {
                                // save data for current topic;
                                ThemeSyncLocal.getInstance().markTopicData(topic, gson.toJson(topicList));

                                if (callback != null) {
                                    Async.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onSuccess(200, topicList);
                                        }
                                    });
                                }
                                LogUtil.d("initFlashData", "initFlashData end.");
                            }
                        } else {
                            callFailed(statusCode, status.getString("msg"));
                        }
                    } catch (Exception e) {
                        callFailed(0, e.getMessage());
                    }
                } else {
                    callFailed(responseCode, response.message());
                }
            }
        });
    }

    /**
     * 获取专题列表
     *
     * @param topicName     专题名称;
     * @param topicWhichNum 该专题的第多少页;
     * @param pageNumber    该专题每页多少条数据;
     */
    public static void requestTopicData(String[] topicName, int[] topicWhichNum, final int[] pageNumber, final TopicThemeCallback callback) {
        if (topicName == null || topicWhichNum == null || pageNumber == null) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        int length = Math.min(topicName.length, Math.min(topicWhichNum.length, pageNumber.length));
        for (int i = 0; i < length; i++) {
            sb.append(topicName[i]).append(",").append(topicWhichNum[i]).append(",").append(pageNumber[i]).append(";");
        }
        LogUtil.d(LogUtil.TAG, "requestTopicData topicName: "+sb.toString());
        String topicList = sb.substring(0, sb.lastIndexOf(";"));
        JSONObject jsonObject = getCommonJsonObject();
        try {
            jsonObject.put("topic_list", topicList)
                    .put("action", ACTION_TOPIC_DATA)
                    .put("test", mTestMode);
        } catch (Exception e) {
            LogUtil.error(e);
        }
        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(0, e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                int code = response.code();
                if (code == 200) {
                    try {
                        String string = response.body().string();
                        if (!TextUtils.isEmpty(string)) {
                            JSONObject responseObj = new JSONObject(string);
                            JSONObject status = responseObj.getJSONObject("status");
                            int statusCode = status.getInt("code");
                            LogUtil.d(LogUtil.TAG, "requestTopicData response, code: " + statusCode + ", msg: " + status.getString("msg"));
                            if (statusCode == 0) {
                                JSONObject dataObject = responseObj.getJSONObject("data");
//                                LogUtil.d(LogUtil.TAG, "requestTopicData data: " + dataObject.toString());

                                final Map<String, List<Theme>> topicMap = new HashMap<String, List<Theme>>();
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<Theme>>() {}.getType();
                                Iterator<String> keys = dataObject.keys();

                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    String value = dataObject.getString(key);
                                    List<Theme> topicList = gson.fromJson(value, type);

                                    if (mTestMode == 0 && topicList != null) {
                                        Iterator<Theme> iterator = topicList.iterator();
                                        while (iterator.hasNext()) {
                                            Theme next = iterator.next();
                                            if (next.getIs_test().equals("1")) {
                                                iterator.remove();
                                            }
                                        }
                                    }

                                    topicMap.put(key, topicList);

                                    if (key.equals("Featured") && pageNumber[0] == 6) {
                                        List<Theme> oldTopicList = ThemeSyncLocal.getInstance().getTopicDataList(key);
                                        if (oldTopicList != null && oldTopicList.size() > 0) {
                                            List<Theme> newAdd = new ArrayList<Theme>();
                                            Iterator<Theme> iterator = topicList.iterator();
                                            while (iterator.hasNext()) {
                                                Theme temp = iterator.next();
                                                if (!oldTopicList.contains(temp)) {
                                                    newAdd.add(temp);
                                                }
                                            }
                                            if (newAdd.size() > 0) {
                                                ThemeSyncLocal.getInstance().markRecommendNew(gson.toJson(newAdd));
                                            }
                                        }
                                    }
                                    ThemeSyncLocal.getInstance().markTopicData(key, gson.toJson(topicList));
                                }
                                ThemeSyncLocal.getInstance().makeGeneralParameter(ThemeSyncLocal.PREF_KEY_UPDATE_TOPIC_DATA_TIME,
                                        String.valueOf(System.currentTimeMillis()));

                                if (callback != null) {
                                    Async.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onSuccess(200, topicMap);
                                        }
                                    });
                                }
                            } else {
                                onFailed(status.getString("msg"), statusCode);
                            }
                        } else {
                            onFailed("response body string is null.", code);
                        }
                    } catch (Exception e) {
                        onFailed("requestTopicData exception: " + e.getMessage(), code);
                    }
                } else {
                    onFailed("response code unusual: " + response.message(), code);
                }
            }

            private void onFailed(final String msg, final int statusCode) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(statusCode, msg);
                        }
                    });
                }
            }
        });
    }

    /**
     * @param themeId 素材id
     * @param like    是否点赞; 0 => 取消点赞, 1 => 点赞
     */
    public static void requestJustLike(long themeId, boolean like, final ThemeNormalCallback callback) {
        JSONObject jsonObject = getCommonJsonObject();
        try {
            jsonObject.put("obj_id", themeId).put("is_like", like ? 1 : 0).put("action", ACTION_JUST_LIKE);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(0, e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        String json = response.body().string();
                        JSONObject responseObj = new JSONObject(json);
                        JSONObject status = responseObj.getJSONObject("status");
                        final int statusCode = status.getInt("code");
                        LogUtil.d(LogUtil.TAG, "requestJustLike response, code: " + statusCode + ", msg: " + status.getString("msg"));

                        if (statusCode == 0) {
                            final int numOfLikes = responseObj.getInt("num_of_likes");
//                            LogUtil.d(LogUtil.TAG, "requestJustLike data: " + responseObj.getString("data"));

                            if (callback != null) {
                                Async.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onFailure(statusCode, String.valueOf(numOfLikes));
                                    }
                                });
                            }
                        } else {
                            onFailed(statusCode, status.getString("msg"));
                        }

                    } catch (Exception e) {
                        onFailed(200, "requestJustLike response exception: " + e.getMessage());
                    }
                } else {
                    onFailed(response.code(), response.message());
                }
            }

            private void onFailed(final int code, final String msg) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(code, msg);
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取分类版本
     *
     * @param typeName 专题版本的名字???
     * @param type     请求的flash版本类型; 1 => 分类 3 => 专题
     */
    public static void requestTypeVersion(final String typeName, final int type, final ThemeNormalCallback callback) {
        JSONObject jsonObject = getCommonJsonObject();
        try {
            jsonObject.put("obj_name", typeName).put("type", type).put("action", ACTION_TYPE_VERSION);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(0, e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        String string = response.body().string();
                        if (!TextUtils.isEmpty(string)) {
                            JSONObject responseObj = new JSONObject(string);
                            JSONObject status = responseObj.getJSONObject("status");
                            int statusCode = status.getInt("code");
                            LogUtil.d(LogUtil.TAG, "requestTypeVersion response, code: " + statusCode + ", msg: " + status.getString("msg"));

                            if (statusCode == 0) {
                                String currentTypeVersion = responseObj.getString("no");
                                LogUtil.d(LogUtil.TAG, "requestTypeVersion data: " + responseObj.getString("data"));

                                ThemeSyncLocal.getInstance().makeGeneralParameter(
                                        ThemeSyncLocal.PREF_KEY_CURRENT_TYPE_VERSION + "_" + typeName + "_" + type,
                                        currentTypeVersion);
                                ThemeSyncLocal.getInstance().makeGeneralParameter(
                                        ThemeSyncLocal.PREF_KEY_UPDATE_TYPE_VERSION_TIME + "_" + typeName + "_" + type,
                                        String.valueOf(System.currentTimeMillis()));

                                if (callback != null) {
                                    final String msg = status.getString("msg");
                                    Async.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onSuccess(200, msg);
                                        }
                                    });
                                }
                            } else {
                                onFailed(statusCode, status.getString("msg"));
                            }
                        }
                    } catch (Exception e) {
                        onFailed(response.code(), "requestTypeVersion" + e.getMessage());
                    }
                } else {
                    onFailed(response.code(), response.message());
                }
            }

            private void onFailed(final int code, final String msg) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(code, msg);
                        }
                    });
                }
            }
        });
    }

    /**
     * @param topicName 父专题名称;
     */
    public static void requestTopicList(final String topicName, final ThemeSyncCallback callback) {
        JSONObject jsonObject = getCommonJsonObject();
        try {
            jsonObject.put("action", ACTION_TOPIC_LIST)
                    .put("test", mTestMode)
                    .put("name", topicName);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(0, e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                int responseCode = response.code();
                if (responseCode == 200) {
                    try {
                        String json = response.body().string();
                        if (!TextUtils.isEmpty(json)) {
                            JSONObject responseObj = new JSONObject(json);
                            JSONObject status = responseObj.getJSONObject("status");
                            int statusCode = status.getInt("code");
                            LogUtil.d(LogUtil.TAG, "requestTopicList response, code: " + statusCode + ", msg: " + status.getString("msg"));
                            if (statusCode == 0) {
                                String data = responseObj.getString("data");
                                ThemeSyncLocal.getInstance().markTopicList(topicName, data);
                                LogUtil.d(LogUtil.TAG, "requestTopicList, topic name: " + topicName + ", data: " + data);

                                final List<Theme> topicList = new Gson().fromJson(data, new TypeToken<List<Theme>>() {
                                }.getType());

                                if (mTestMode == 0) {
                                    Iterator<Theme> iterator = topicList.iterator();
                                    while (iterator.hasNext()) {
                                        Theme next = iterator.next();
                                        if (next.getIs_test().equals("1")) {
                                            iterator.remove();
                                        }
                                    }
                                }

                                if (callback != null) {
                                    Async.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onSuccess(topicList);
                                        }
                                    });
                                }
                            } else {
                                onFailed(statusCode, status.getString("msg"));
                            }
                        } else {
                            onFailed(0, "response body string is null.");
                        }
                    } catch (Exception e) {
                        onFailed(200, "requestTopicList response exception: " + e.getMessage());
                    }
                } else {
                    onFailed(responseCode, response.message());
                }
            }

            private void onFailed(final int code, final String msg) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(code, msg);
                        }
                    });
                }
            }
        });
    }

    public static void requestTagList(final CategoryCallback callback) {
        JSONObject jsonObject = getCommonJsonObject();

        try {
            jsonObject.put("action", ACTION_TAG_LIST).put("test", mTestMode);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(0, e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Async.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.code() == 200) {
                            try {
                                LogUtil.d(LogUtil.TAG, "requestTagList response msg: " + response.body().string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    public static void requestHomePageData(final TopicThemeCallback callback) {
        JSONObject jsonObject = getCommonJsonObject();
        try {
            jsonObject.put("action", ACTION_HOME_PAGE_DATA).put("test", mTestMode);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(0, e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                int code = response.code();
                if (code == 200) {
                    try {
                        String string = response.body().string();
                        if (!TextUtils.isEmpty(string)) {
                            JSONObject responseObj = new JSONObject(string);
                            JSONObject status = responseObj.getJSONObject("status");
                            int statusCode = status.getInt("code");
                            LogUtil.d(LogUtil.TAG, "requestHomePageData response, code: " + statusCode + ", msg: " + status.getString("msg"));
                            if (statusCode == 0) {
                                JSONObject dataObject = responseObj.getJSONObject("data");

//                                LogUtil.d(LogUtil.TAG, "requestHomePageData data: " + dataObject.toString());

                                final Map<String, List<Theme>> topicMap = new HashMap<String, List<Theme>>();
                                Gson gson = new Gson();
                                Type type = new TypeToken<List<Theme>>() {
                                }.getType();
                                Iterator<String> keys = dataObject.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    String value = dataObject.getString(key);
                                    List<Theme> topicList = gson.fromJson(value, type);
                                    if (mTestMode == 0) {
                                        Iterator<Theme> themeIterator = topicList.iterator();
                                        while (themeIterator.hasNext()) {
                                            Theme next = themeIterator.next();
                                            if ("1".equals(next.getIs_test())) {
                                                themeIterator.remove();
                                            }
                                        }
                                    }
                                    topicMap.put(key, topicList);
                                }

                                String json = gson.toJson(topicMap);
                                ThemeSyncLocal.getInstance().markSingleJsonData(ThemeSyncLocal.PREF_SUFFIX_HOME_PAGE_DATA, json);

                                if (callback != null) {
                                    Async.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onSuccess(200, topicMap);
                                        }
                                    });
                                }
                            } else {
                                onFailed(code, status.getString("msg"));
                            }
                        }
                    } catch (Exception e) {
                        onFailed(0, "requestHomePageData response exception: " + e.getMessage());
                    }
                } else {
                    onFailed(code, response.message());
                }
            }

            private void onFailed (final int code, final String msg) {
                if (callback == null) {
                    return;
                }

                Async.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(code, msg);
                    }
                });
            }
        });
    }

    /**
     * 举报素材
     *
     * @param themeId 被举报的素材id
     * @param reason  被举报的原因;
     */
    public static void reportTheme(long themeId, String reason, final ThemeNormalCallback callback) {
        JSONObject jsonObject = getCommonJsonObject();
        try {
            jsonObject.put("action", ACTION_REPORT).put("obj_id", themeId).put("reason", reason);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(0, e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        String json = response.body().string();
                        if (!TextUtils.isEmpty(json)) {
                            JSONObject responseObj = new JSONObject(json);
                            int code = responseObj.getInt("code");
                            LogUtil.d(LogUtil.TAG, "reportTheme response, code: " + code + ", msg: " + responseObj.getString("msg"));

                            if (callback != null) {
                                final String msg = responseObj.getString("msg");
                                if (code == 0) {
                                    Async.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onSuccess(200, msg);
                                        }
                                    });
                                } else {
                                    onFailed(code, msg);
                                }
                            }
                        } else {
                            onFailed(0, "response message is null.");
                        }
                    } catch (Exception e) {
                        onFailed(0, "reportTheme response exception" + e.getMessage());
                    }
                } else {
                    onFailed(response.code(), response.message());
                }
            }

            private void onFailed (final int code, final String msg) {
                if (callback == null) return;

                Async.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(code, msg);
                    }
                });
            }
        });
    }

    /**
     * todo 2018/5/10 需要添加新的回调接口(Search 相关)
     * 搜索
     *
     * @param keyword    关键字
     * @param fileType   文件类型
     * @param pageNumber 每页数量
     * @param whichPage  第几页
     */
    public static void search(String keyword, int fileType, int pageNumber, int whichPage, final ThemeSyncCallback callback) {
        if (TextUtils.isEmpty(keyword)) return;

        JSONObject jsonObject = getCommonJsonObject();
        try {
            jsonObject.put("action", ACTION_SEARCH)
                    .put("key", keyword)
                    .put("type", fileType)
                    .put("ps", pageNumber)
                    .put("pg", whichPage);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(0, e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        String json = response.body().string();
                        JSONObject responseObj = new JSONObject(json);
                        JSONObject status = responseObj.getJSONObject("status");
                        int statusCode = status.getInt("code");
                        LogUtil.d(LogUtil.TAG, "search response, code: " + statusCode + ", msg: " + responseObj.getString("msg"));

                        if (statusCode == 0) {
                            String data = responseObj.getString("data");

                            final List<Theme> searchList = new Gson().fromJson(data, new TypeToken<List<Theme>>() {
                            }.getType());

                            if (mTestMode == 0) {
                                Iterator<Theme> iterator = searchList.iterator();
                                while (iterator.hasNext()) {
                                    Theme next = iterator.next();
                                    if ("1".equals(next.getIs_test())) {
                                        iterator.remove();
                                    }
                                }
                            }

                            if (callback != null) {
                                Async.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(searchList);
                                    }
                                });
                            }
                        } else {
                            onFailed(statusCode, status.getString("msg"));
                        }
                    } catch (Exception e) {
                        onFailed(200, "search response exception: " + e.getMessage());
                    }
                } else {
                    onFailed(response.code(), response.message());
                }
            }

            private void onFailed (final int code, final String msg) {
                if (callback == null) return;

                Async.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(code, msg);
                    }
                });
            }
        });
    }


    public static void requestHotKeys(final ThemeNormalCallback callback) {
        JSONObject jsonObject = getCommonJsonObject();
        try {
            jsonObject.put("action", ACTION_HOT_KEY);
        } catch (Exception e) {
            LogUtil.error(e);
        }

        makeRequest(jsonObject, new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    Async.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(0, "requestHotKeys onFailure exception: " + e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Async.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            try {
                                String string = response.body().string();
                                JSONObject responseObj = new JSONObject(string);
                                JSONObject status = responseObj.getJSONObject("status");
                                int statusCode = status.getInt("code");
                                LogUtil.d(LogUtil.TAG, "requestHotKeys response, code: " + statusCode + ", msg: " + responseObj.getString("msg"));
                                if (statusCode == 0) {
                                    String data = responseObj.getString("data");
                                    ThemeSyncLocal.getInstance().markSingleJsonData(ThemeSyncLocal.PREF_SUFFIX_HOT_KEYS, data);
                                    LogUtil.d(LogUtil.TAG, "requestHotKeys data: " + data);

                                    List<String> hotKeyList = new Gson().fromJson(data, new TypeToken<List<String>>() {
                                    }.getType());
                                    String hotKey = "";
                                    if (hotKeyList != null && hotKeyList.size() > 0) {
                                        StringBuffer sb = new StringBuffer();
                                        for (String str : hotKeyList) {
                                            sb.append(str).append(",");
                                        }
                                        hotKey = sb.substring(0, sb.lastIndexOf(","));
                                    }
                                    if (callback != null) {
                                        callback.onSuccess(200, hotKey);
                                    }
                                }

                            } catch (Exception e) {
                                LogUtil.error(e);
                                if (callback != null) {
                                    callback.onFailure(0, e.getMessage());
                                }
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailure(response.code(), response.message());
                            }
                        }
                    }
                });
            }
        });
    }

    public static void requestEventStatistics (final String eventName, final long objId) {
        JSONObject object = getCommonJsonObject();

        try {
            object.put("action", ACTION_EVENT_STATISTICS)
                    .put("type", eventName)
                    .put("obj_id", objId);
        } catch (Exception e) {
        }

        makeRequest(object, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                LogUtil.d(LogUtil.TAG, "requestEventStatistics failed. object_id: " + objId + ", event_name: " + eventName);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                LogUtil.d(LogUtil.TAG, "requestEventStatistics result: " + response.body().string()
//                        + ", object_id: " + objId + ", event_name: " + eventName);
            }
        });

    }

    private static void makeRequest(JSONObject jsonObject, Callback callback) {
        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
        String jsonData = jsonObject.toString();
        String sig = MD5Encode(KEY_HTTP.replace("%s", jsonData));

        RequestBody body = new FormBody.Builder()
                .add("data", jsonData)
                .add("sig", sig)
                .build();

        Request request = new Request.Builder()
                .url(THEME_REQUEST_URL)
                .post(body)
                .build();

        mClient.newCall(request).enqueue(callback);
    }

    public static String MD5Encode(String originalStr) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] result = messageDigest.digest(originalStr.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                int num = b & 0xff;
                String hex = Integer.toHexString(num);
                if (hex.length() == 1) {
                    sb.append(0);
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}
