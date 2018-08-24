package com.md.wallpaper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zhq on 2018/1/10.
 */

public class WallpaperPreferenceHelper {
    private static final String TAG = "WallpaperPreferenceHelper";
    private static final String PREF_FILE_NAME = "android_wall_paper_pref_file";

    /**
     * 所有已下载 的壁纸对象
     */
    public static final String DOWNLOADED_WALLPAPERS = "downloaded_wallpaper";

    /**
     * 当前设置的壁纸对象
     */
    public static final String SETED_WALLPAPERS = "SETED_wallpaper";

    /**
     * 保存设置动态壁纸的路径
     */
    public static final String FILE_NAME = "file_name";

    /**
     * 保存视频壁纸声音
     */
    public static final String PREF_WALL_IS_MUTE_WHEN_PREVIEW = "pref_wall_is_mute_when_preview";

    /**
     * 点赞壁纸集合
     */
    public static final String PREF_JUST_LIKE_WALLPAPER_LIST = "caller_id_pref_just_like_wallpaper_list";

    /**
     * 已经保存过下载数的wallpaper url
     */
    public static final String WALLPAPER_SAVE_DOWNLOAD_COUNT_URLS = "wallpaper_save_download_count_urls";

    /**
     * 保存所有的下载的对象
     */
    public static final String COOKIE = "cookie";

    public static final String CALL_FLASH_ON = "call_flash_on";//call flash 是否开启


    private static Context mContext;

    private WallpaperPreferenceHelper() {
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
    public static <T> void putDataList(String key, List<T> datalist) {
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
    public static <T> void putObject(String key, T data) {
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
            editor.putString(key, json);
            result = true;
        } catch (Exception e) {
            result = false;
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

        } catch (Exception e) {

        }

        return map;
    }
}


