package blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

final public class AdPreferenceHelper {
    private static final String TAG = "AdPreferenceHelper";
    private static final String PREF_FILE_NAME = "ad_com_callercolor_pref";


    /**
     * 上一次加载第一次显示admob的时间的map
     */
    public static final String PREF_LAST_SHOW_FIRST_ADMOB_TIME_MAP = "android_last_show_first_admob_time_map";

    private AdPreferenceHelper() {
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static long getLong(String key, long defValue) {
        if (ApplicationEx.getInstance() == null) {
            return defValue;
        }
        return getPreferences(ApplicationEx.getInstance()).getLong(key, defValue);
    }

    public static void putLong(String key, long value) {
        getPreferencesEditor(ApplicationEx.getInstance()).putLong(key, value).commit();
    }

    public static String getString(String key, String defValue) {
        if (ApplicationEx.getInstance() == null) {
            return defValue;
        }
        return getPreferences(ApplicationEx.getInstance()).getString(key, defValue);
    }

    public static void putString(String key, String value) {
        getPreferencesEditor(ApplicationEx.getInstance()).putString(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getPreferences(ApplicationEx.getInstance()).getBoolean(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        getPreferencesEditor(ApplicationEx.getInstance()).putBoolean(key, value).commit();
    }

    public static int getInt(String key, int defValue) {
        return getPreferences(ApplicationEx.getInstance()).getInt(key, defValue);
    }

    public static void putInt(String key, int value) {
        getPreferencesEditor(ApplicationEx.getInstance()).putInt(key, value).commit();
    }

    /**
     * 用于保存集合
     *
     * @param key key
     * @param map map数据
     * @return 保存是否成功
     */
    public static <K, V> boolean putHashMapData(String key, Map<K, V> map) {
        boolean result;
        Editor editor = getPreferencesEditor(ApplicationEx.getInstance());
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
        String json = getPreferences(ApplicationEx.getInstance()).getString(key, "");
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


    /**
     * 保存List
     *
     * @param key
     * @param datalist
     */
    public static <T> void putDataList(String key, List<T> datalist) {
        Editor editor = getPreferencesEditor(ApplicationEx.getInstance());
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
        String strJson = getPreferences(ApplicationEx.getInstance()).getString(key, null);
        if (null == strJson) {
            return null;
        }
        T[] arr = new Gson().fromJson(strJson, clazz);
        return new ArrayList<>(Arrays.asList(arr));
    }

}
