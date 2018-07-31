package com.md.flashset.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.md.serverflash.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zhq on 2018/1/10.
 */

public class CallFlashPreferenceHelper {
    private static final String TAG = "CallFlashPreferenceHelper";
    private static final String PREF_FILE_NAME = "android_call_flash_pref_file";

    /**
     * 点赞来电秀集合
     */
    public static final String PREF_JUST_LIKE_FLASH_LIST = "caller_id_pref_just_like_flash_list";

    public static final String CALL_FLASH_ON = "call_flash_on";//call flash 是否开启

    public static final String CALL_FLASH_TYPE = "call_flash_type";//当前设置的 call falsh type

    public static final String CALL_FLASH_TYPE_DYNAMIC_PATH = "call_flash_type_dynamic_path";

    public static final String CALL_FLASH_SHOW_TYPE_INSTANCE = "call_flash_show_type_instance"; // 当前使用的flash的实例(string形式)

    public static final String CALL_FLASH_CUSTOM_BG_PATH = "call_flash_custom_bg_path";

    /**
     * 已经保存过下载数的callflash url
     */
    public static final String CALL_FLASH_SAVE_DOWNLOAD_COUNT_URLS = "call_flash_save_download_count_urls";
    /**
     * 当前媒体音量大小
     */
    public static final String PREF_CURRENT_MUSIC_VOLUME_WHEN_SET_CALL_FLASH = "android_current_music_volume_when_set_call_flash";

    /**
     * 已经下载过的来电秀集合
     */
    public static final String PREF_DOWNLOADED_CALL_FLASH_LIST = "android_downloaded_call_flash_list";

    /**
     * 被设置过的来电秀几个
     */
    public static final String PREF_CALL_FLASH_SET_RECORD_LIST = "android_call_flash_set_record_list";

    private static Context mContext;

    private CallFlashPreferenceHelper() {
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static long getLong(String key, long defValue) {
        if (mContext == null) {
            return defValue;
        }
        return getPreferences(mContext).getLong(key, defValue);
    }

    public static void putLong(String key, long value) {
        if (mContext == null) {
            return;
        }
        getPreferencesEditor(mContext).putLong(key, value).commit();
    }

    public static String getString(String key, String defValue) {
        if (mContext == null) {
            return defValue;
        }
        return getPreferences(mContext).getString(key, defValue);
    }

    public static void putString(String key, String value) {
        if (mContext == null) {
            return;
        }
        getPreferencesEditor(mContext).putString(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        if (mContext == null) {
            return defValue;
        }
        return getPreferences(mContext).getBoolean(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        if (mContext == null) {
            return;
        }
        getPreferencesEditor(mContext).putBoolean(key, value).commit();
    }

    public static int getInt(String key, int defValue) {
        if (mContext == null) {
            return defValue;
        }
        return getPreferences(mContext).getInt(key, defValue);
    }

    public static void putInt(String key, int value) {
        if (mContext == null) {
            return;
        }
        getPreferencesEditor(mContext).putInt(key, value).commit();
    }

    /**
     * 保存List
     *
     * @param key
     * @param datalist
     */
    public static <T> void setDataList(String key, List<T> datalist) {
        if (mContext == null) {
            return;
        }
        SharedPreferences.Editor editor = getPreferencesEditor(mContext);
        if (null == datalist) {
            editor.putString(key, null);
            editor.commit();
            return;
        }
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.putString(key, strJson);
        editor.commit();
    }

    /**
     * 获取List
     *
     * @param key
     * @param clazz 比如：CallFlashInfo[].class
     * @return
     */
    public static <T> List<T> getDataList(String key, Class<T[]> clazz) {
        if (mContext == null) {
            return null;
        }
        String strJson = getPreferences(mContext).getString(key, null);
        if (null == strJson) {
            return null;
        }
        T[] arr = new Gson().fromJson(strJson, clazz);
        return new ArrayList<>(Arrays.asList(arr));
    }

    /**
     * 保存对象
     *
     * @param key
     * @param data
     */
    public static <T> void setObject(String key, T data) {
        if (null == data || mContext == null)
            return;
        SharedPreferences.Editor editor = getPreferencesEditor(mContext);
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(data);
        editor.putString(key, strJson);
        editor.commit();
    }

    /**
     * 获取对象
     *
     * @param key
     * @param clas 比如：CallFlashInfo.class
     * @return
     */
    public static <T> T getObject(String key, Class<T> clas) {
        if (mContext == null) {
            return null;
        }
        String strJson = getPreferences(mContext).getString(key, null);
        if (null == strJson) {
            return null;
        }
        T arr = new Gson().fromJson(strJson, clas);
        return arr;
    }

    /**
     * 用于保存集合
     *
     * @param key key
     * @param map map数据
     * @return 保存是否成功
     */
    public static <K, V> boolean putHashMapData(String key, Map<K, V> map) {
        if (mContext == null) {
            return false;
        }
        boolean result;
        SharedPreferences.Editor editor = getPreferencesEditor(mContext);
        try {
            Gson gson = new Gson();
            String json = gson.toJson(map);
            LogUtil.d(TAG, "putHashMapData json:" + json.toString());
            editor.putString(key, json);
            result = true;
        } catch (Exception e) {
            result = false;
            LogUtil.e(TAG, "putHashMapData e:" + e.getMessage());
        }
        editor.apply();
        return result;
    }

    /**
     * 用于保存集合
     *
     * @param key key
     * @return HashMap
     */
    public static <V> HashMap<String, V> getHashMapData(String key, Class<V> clsV) {
        if (mContext == null) {
            return null;
        }
        String json = getPreferences(mContext).getString(key, "");
        HashMap<String, V> map = new HashMap<>();
        try {
            Gson gson = new Gson();
            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = obj.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                String entryKey = entry.getKey();
                JsonElement value = entry.getValue();
                map.put(entryKey, gson.fromJson(value, clsV));
            }
            LogUtil.d(TAG, "getHashMapData obj:" + obj.toString());
        } catch (Exception e) {
            LogUtil.e(TAG, "getHashMapData e:" + e.getMessage());
        }

        return map;
    }
}


