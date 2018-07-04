package com.md.serverflash.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Category;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.util.LogUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ChenR on 2018/5/9.
 */

public class ThemeSyncLocal {
    private static final String PREF_FILE_NAME_FLASH_SHOW_SDK = "flash_show_sdk";
    private static final String PREF_SUFFIX_GENERAL_PARAMETER = "_general_parameter";
    private static final String PREF_SUFFIX_THEME_RESOURCES_DOWNLOADED = "_theme_resources_downloaded";
    private static final String PREF_SUFFIX_TOPIC_LIST = "_topic_list";
    public static final String PREF_SUFFIX_CATEGORY_LIST = "_category_list";
    public static final String PREF_SUFFIX_PAGE_DATA = "_page_data";
    public static final String PREF_SUFFIX_TOPIC_DATA = "_topic_data";
    public static final String PREF_SUFFIX_HOME_PAGE_DATA = "_home_page_data";
    public static final String PREF_SUFFIX_HOT_KEYS = "_hot_keys";

    private static final String PREF_KEY_PARAMS_FILE_LIST = "flash_show_sdk_parameter_file_list";
    public static final String PREF_KEY_UPDATE_TYPE_VERSION_TIME = "flash_show_sdk_update_type_version_time";
    public static final String PREF_KEY_CURRENT_TYPE_VERSION = "flash_show_sdk_current_type_version";
    public static final String PREF_KEY_UPDATE_TOPIC_DATA_TIME = "flash_show_sdk_update_topic_data_time";

    private static ThemeSyncLocal instance = null;

    public static ThemeSyncLocal getInstance () {
        if (instance == null) {
            synchronized (ThemeSyncLocal.class) {
                if (instance == null) {
                    instance = new ThemeSyncLocal();
                }
            }
        }
        return instance;
    }

    private Gson mGson = null;
    private Context mContext = null;

    private ThemeSyncLocal () {
        mGson = new Gson();
        mContext = ThemeSyncManager.getInstance().getContext();
    }

    private SharedPreferences getSharePreferences(String prefKeyName) {
        if (mContext == null) {
            LogUtil.e(LogUtil.TAG, "context is null.");
            return null;
        }
        SharedPreferences pref = mContext.getSharedPreferences(PREF_FILE_NAME_FLASH_SHOW_SDK + prefKeyName, Context.MODE_PRIVATE);
        return pref;
    }

    public void makeGeneralParameter (String key, String value) {
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_GENERAL_PARAMETER);
        if (pref == null) {
            return;
        }
        pref.edit().putString(key, value).commit();
    }

    public String getGeneralParameter (String key) {
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_GENERAL_PARAMETER);
        if (pref != null) {
            return pref.getString(key, "");
        }
        return "";
    }

    public void markDownloadedTheme (String url) {
        Theme theme = getThemeByUrl(url);
        if (theme==null) {
            return;
        }
        SharedPreferences preferences = getSharePreferences(PREF_SUFFIX_THEME_RESOURCES_DOWNLOADED);
        if (preferences != null) {
            String json = preferences.getString("json", "");
            Type type = new TypeToken<List<Theme>>() {}.getType();
            List<Theme> mDownloadedList = mGson.fromJson(json, type);
            if (mDownloadedList == null) {
                mDownloadedList = new ArrayList<>();
            }
            if (!mDownloadedList.contains(theme)) {
                mDownloadedList.add(theme);
                String data = mGson.toJson(mDownloadedList);
                markSingleJsonData(PREF_SUFFIX_THEME_RESOURCES_DOWNLOADED, data);
            }
        }

    }

    private Theme getThemeByUrl(String url) {
        Theme theme = null;

        String paramsFile = getGeneralParameter(PREF_KEY_PARAMS_FILE_LIST);
        if (!TextUtils.isEmpty(paramsFile)) {
            List<String> paramsName = mGson.fromJson(paramsFile, new TypeToken<List<String>>(){}.getType());
            if (paramsName != null) {
                TAG:
                for (String name : paramsName) {
                    if (name.equals(PREF_SUFFIX_TOPIC_DATA)) {
                        theme = getThemeByPreferencesFile(url, PREF_SUFFIX_TOPIC_DATA);
                    } else if (name.equals(PREF_SUFFIX_HOME_PAGE_DATA)) {
                        Map<String, List<Theme>> homePageData = getHomePageData();
                        if (homePageData != null && !homePageData.isEmpty()) {
                            Collection<List<Theme>> values = homePageData.values();
                            for (List<Theme> value : values) {
                                for (Theme temp : value) {
                                    String tempUrl = temp.getUrl();
                                    if (!TextUtils.isEmpty(tempUrl) && tempUrl.equals(url)) {
                                        theme = temp;
                                        break TAG;
                                    }
                                }
                            }
                        }
                    } else if (name.equals(PREF_SUFFIX_TOPIC_LIST)){
                        theme = getThemeByPreferencesFile(url, PREF_SUFFIX_TOPIC_LIST);
                    } else if(name.equals(PREF_SUFFIX_PAGE_DATA)) {
                        theme = getThemeByPreferencesFile(url, PREF_SUFFIX_PAGE_DATA);
                    }
                }
            }
        }
        return theme;
    }

    private Theme getThemeByPreferencesFile (String url, String prefName) {
        Theme theme = null;
        SharedPreferences pref = getSharePreferences(prefName);
        if (pref != null) {
            Map<String, ?> all = pref.getAll();
            Type type = new TypeToken<List<Theme>>() {}.getType();
            TAG:
            for (String key : all.keySet()) {
                if (key.contains("json")) {
                    String json = pref.getString(key, "");
                    List<Theme> list = mGson.fromJson(json, type);
                    if (list != null && list.size() > 0) {
                        for (Theme temp : list) {
                            String tempUrl = temp.getUrl();
                            if (!TextUtils.isEmpty(tempUrl) && tempUrl.equals(url)) {
                                theme = temp;
                                break TAG;
                            }
                        }
                    }
                }
            }
        }
        return theme;
    }

    public void markSingleJsonData(String prefKeyName, String data) {
        SharedPreferences pref = getSharePreferences(prefKeyName);
        if (pref == null) {
            return;
        }

        // 缓存theme相关的xml文件名称;
        if (prefKeyName.equals(PREF_SUFFIX_TOPIC_DATA) || prefKeyName.equals(PREF_SUFFIX_PAGE_DATA)
                || prefKeyName.equals(PREF_SUFFIX_HOME_PAGE_DATA)) {
            String paramsFile = getGeneralParameter(PREF_KEY_PARAMS_FILE_LIST);
            if (!TextUtils.isEmpty(paramsFile)) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> existPreferencesCache = mGson.fromJson(paramsFile, type);
                if (existPreferencesCache == null) {
                    existPreferencesCache = new ArrayList<>();
                }
                if (!existPreferencesCache.contains(prefKeyName)) {
                    existPreferencesCache.add(prefKeyName);
                    String json = mGson.toJson(existPreferencesCache);
                    makeGeneralParameter(PREF_KEY_PARAMS_FILE_LIST, json);
                }
            }
        }

        pref.edit().putString("json", data).putLong("date", System.currentTimeMillis()).commit();
    }

    public void markPageData (int categoryId, String data) {
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_PAGE_DATA);
        if (pref != null) {
            pref.edit().putString("json_" + categoryId, data).putLong("date_"+categoryId, System.currentTimeMillis()).commit();
        }
    }

    public List<Theme> getPageData (int categoryId) {
        List<Theme> list = null;
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_PAGE_DATA);
        if (pref != null) {
            Type type = new TypeToken<List<Theme>>() {}.getType();
            String value = pref.getString("json_" + categoryId, "");
            list = mGson.fromJson(value, type);
        }
        return list;
    }

    public long getPageDataUpdateTime (int categoryId) {
        long time = 0;
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_PAGE_DATA);
        if (pref != null) {
            time = pref.getLong("date_" + categoryId, 0);
        }
        return time;
    }

    public void markTopicData (String topicName, String data) {
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_TOPIC_DATA);
        if (pref!=null) {
            pref.edit().putString("json_" + topicName, data).putLong("date_" + topicName, System.currentTimeMillis()).commit();
        }
    }

    public void markRecommendNew(String data) {
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_TOPIC_DATA);
        if (pref != null) {
            pref.edit().putString("json_new_recommend", data)
                    .putLong("date_new_recommend", System.currentTimeMillis()).apply();
        }
    }

    public List<Theme> getRecommendNewList () {
        List<Theme> list = null;
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_TOPIC_DATA);
        if (pref != null) {
            String value = pref.getString("json_new_recommend", "");
            Type type = new TypeToken<List<Theme>>() {}.getType();
            list = mGson.fromJson(value, type);
        }
        return list;
    }

    public long getRecommendNewLastUpdateTime () {
        long time = 0;
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_TOPIC_DATA);
        if (pref != null) {
            time = pref.getLong("date_new_recommend", 0);
        }
        return time;
    }

    public void clearRecommendNew () {
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_TOPIC_DATA);
        if (pref != null) {
            pref.edit().putString("json_new_recommend", "").putLong("date_new_recommend", 0).commit();
        }
    }

    public List<Theme> getTopicDataList (String topicName) {
        List<Theme> list = null;
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_TOPIC_DATA);
        if (pref != null) {
            String value = pref.getString("json_" + topicName, "");
            Type type = new TypeToken<List<Theme>>() {}.getType();
            list = mGson.fromJson(value, type);
        }
        return list;
    }

    public void resetTopicDataCache () {
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_TOPIC_DATA);
        if (pref != null) {
            boolean bool = pref.getBoolean("flash_sdk_is_reset_topic_data_cache", false);
            if (bool) {
                return;
            }

            Map<String, ?> all = pref.getAll();
            pref.edit().clear().commit();

            String regex = "[a-zA-Z_-]";
            Pattern p = Pattern.compile(regex);
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String oldKey = entry.getKey();
                Matcher matcher = p.matcher(oldKey);

                StringBuffer key = new StringBuffer();
                while (matcher.find()) {
                    key.append(matcher.group());
                }

                if (oldKey.startsWith("json_")) {
                    // flash date
                    String oldValue = pref.getString(key.toString(), "");
                    String value = (String) entry.getValue();
                    pref.edit().putString(key.toString(),
                            value.length() >= oldValue.length() ? value : oldValue).commit();
                } else if (oldKey.startsWith("date_")) {
                    // flash update time
                    long oldValue = pref.getLong(key.toString(), 0);
                    Long value = (Long) entry.getValue();
                    pref.edit().putLong(key.toString(), value > oldValue ? oldValue : value).commit();
                } else {
                    Boolean value = (Boolean) entry.getValue();
                    pref.edit().putBoolean(key.toString(), value).commit();
                }
            }
            pref.edit().putBoolean("flash_sdk_is_reset_topic_data_cache", true).apply();
            Map<String, ?> afterAll = pref.getAll();
        }
    }

    public long getTopicDataListUpdateTime (String topicName) {
        SharedPreferences preferences = getSharePreferences(PREF_SUFFIX_TOPIC_DATA);
        long time = System.currentTimeMillis();
        if (preferences != null) {
            time = preferences.getLong("date_" + topicName, 0);
        }
        return time;
    }

    public void markTopicList (String topicName, String data) {
        SharedPreferences preferences = getSharePreferences(PREF_SUFFIX_TOPIC_LIST);
        if (preferences == null) {
            return;
        }

        // 保存已经更新过的接口列表;
        String paramsFile = getGeneralParameter(PREF_SUFFIX_TOPIC_LIST);
        if (!TextUtils.isEmpty(paramsFile)) {
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> existPreferencesCache = mGson.fromJson(paramsFile, type);
            if (existPreferencesCache == null) {
                existPreferencesCache = new ArrayList<>();
            }
            if (!existPreferencesCache.contains(PREF_SUFFIX_TOPIC_LIST)) {
                existPreferencesCache.add(PREF_SUFFIX_TOPIC_LIST);
                String json = mGson.toJson(existPreferencesCache);
                makeGeneralParameter(PREF_KEY_PARAMS_FILE_LIST, json);
            }
        }

        preferences.edit().putString("json_" + topicName, data).putLong("date_" + topicName, System.currentTimeMillis()).commit();
    }

    public long getTopicListUpdateTime (String topicName) {
        SharedPreferences preferences = getSharePreferences(PREF_SUFFIX_TOPIC_LIST);
        long time = System.currentTimeMillis();
        if (preferences != null) {
            time = preferences.getLong("date_" + topicName, time);
        }
        return time;
    }

    public List<Theme> getDownloadedList () {
        SharedPreferences preferences = getSharePreferences(PREF_SUFFIX_THEME_RESOURCES_DOWNLOADED);
        if (preferences != null) {
            Type type = new TypeToken<List<Theme>>(){}.getType();
            String json = preferences.getString("json", "");
            return mGson.fromJson(json, type);
        }
        return null;
    }

    public Map<String, List<Theme>> getHomePageData () {
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_HOME_PAGE_DATA);
        if (pref != null) {
            Type type = new TypeToken<Map<String, List<Theme>>>(){}.getType();
            String json = pref.getString("json", "");
            return mGson.fromJson(json, type);
        }
        return null;
    }

    public List<Theme> getTopicList (String topicName) {
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_TOPIC_LIST);
        if (pref != null) {
            Type type = new TypeToken<List<Theme>>(){}.getType();
            String json = pref.getString("json_" + topicName, "");
            return mGson.fromJson(json, type);
        }
        return null;
    }

    public List<Category> getCategoryList () {
        SharedPreferences preferences = getSharePreferences(PREF_SUFFIX_CATEGORY_LIST);
        if (preferences != null) {
            Type type = new TypeToken<List<Category>>(){}.getType();
            String json = preferences.getString("json", "");
            return mGson.fromJson(json, type);
        }
        return null;
    }

    public String getHotKeys () {
        SharedPreferences pref = getSharePreferences(PREF_SUFFIX_HOT_KEYS);
        String value = null;
        if (pref != null) {
            Type type = new TypeToken<List<String>>(){}.getType();
            String data = pref.getString("json", "");
            List<String> hotKeyList = mGson.fromJson(data, type);
            if (hotKeyList != null && hotKeyList.size() > 0) {
                StringBuffer sb = new StringBuffer();
                for (String str : hotKeyList) {
                    sb.append(str).append(",");
                }
                value = sb.substring(0, sb.lastIndexOf(","));
            }
        }
        return value;
    }

    public long getUpdateTime (String prefKeyName) {
        SharedPreferences pref = getSharePreferences(prefKeyName);
        long time = 0;
        if (pref != null) {
            time = pref.getLong("date", 0);
        }
        return time;
    }


}
