package com.md.flashset.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Zhq on 2018/1/10.
 */

public class CallFlashPreferenceHelper {
    private static final String TAG = "CallFlashPreferenceHelper";
    private static final String PREF_FILE_NAME = "android_call_flash_pref_file";


    /**
     * old path to new path time
     */
    public static final String PREF_MOVE_CALL_FLASH_TO_NEW_TIME = "move_call_flash_to_new_time";

    /**
     * reset wrong callflashinfo
     */
    public static final String PREF_RESET_CALL_FLASH_INFO_TIME = "key_reset_call_flash_info_time";

    /**
     * 推荐插入的位置
     */
    public static final String PREF_SET_INSERT_POSITION = "key_new_flash_insert_position";

    /**
     * 上一次callflash 刷新时间
     */
    public static final String PREF_LAST_CALL_FLASH_REFRESH_TIME = "android_last_call_flash_refresh_time";

    /**
     * 最热 callflash info
     */
    public static final String PREF_HOT_ONLION_CALL_FLASH_INFO = "android_hot_onlion_call_flash_info";

    /**
     * 最新  callflash info
     */
    public static final String PREF_NEW_ONLION_CALL_FLASH_INFO = "android_new_onlion_call_flash_info";

    /**
     * 推荐 info
     */
    public static final String PREF_RECOMMEND_ONLION_CALL_FLASH_INFO = "android_recommend_onlion_call_flash_info";

    /**
     * 设置的全局callflash
     */
    public static final String PREF_SET_GLOBAL_CALL_FLASH = "android_set_global_call_flash";

    /**
     * 上一次后台下载最新callflash成功的时间
     */
    public static final String PREF_LAST_DOWNLOAD_CALLFLASH_NEW_IN_BACKGROUND_TIME = "android_last_download_callflash_new_in_background_time";

    /**
     * 上一次后台下载最热callflash成功的时间
     */
    public static final String PREF_LAST_DOWNLOAD_CALLFLASH_HOT_IN_BACKGROUND_TIME = "android_last_download_callflash_hot_in_background_time";

    /**
     * 上一次后台下载推荐callflash成功的时间
     */
    public static final String PREF_LAST_DOWNLOAD_CALLFLASH_RECOMMEND_IN_BACKGROUND_TIME = "android_last_download_callflash_recommend_in_background_time";

    /**
     * 在线callflash请求刷新间隔时间refresh
     */
    public static final String PREF_REQUEST_ONLION_CALLFLASH_REFRESH_INTERVAL_TIME = "android_request_onlion_callflash_refresh_interval_time";

    /**
     * 后台是否在下载在线callflash
     */
    public static final String PREF_IS_DOWNLOADING_ONLINE_CALLFALASH_IN_BACKGROUND = "android_is_downloading_online_callflash_in_background";

    // 在线来电秀, 之前预制的部分;
    public static final String PREF_PRESET_ONLINE_FLASH = "preset_online_flash";
    // 最新的来电秀对象;
    public static final String PREF_NEWEST_FLASH_INSTANCE = "caller_flash_newest_instance";
    // 上一个发送通知的最新来电秀对象;
    public static final String PREF_PREVIOUS_NEWEST_FLASH_INSTANCE = "caller_flash_previous_newest_instance";

    /**
     * 上一次进入call flash more 的时间
     */
    public static final String PREF_LAST_TO_CALL_FLASH_MORE_TIME = "android_last_to_call_flash_more_time";
    /**
     * 所有在线的callflash
     */
    public static final String PREF_ALL_ONLINE_FlASH = "android_all_online_flash";
    /**
     * 点赞来电秀集合
     */
    public static final String PREF_JUST_LIKE_FLASH_LIST = "caller_id_pref_just_like_flash_list";

    public static final String IS_SHOW_TIP_CALLFLASH_EXIT = "is_show_tip_callflash_exit";//退出callflash setting是否弹窗提示, 0 默认弹出

    public static final String NOT_SHOW_TIP_CALLFLASH_EXIT_LIST = "not_show_tip_callflash_exit_list";//不显示弹窗提示的国家, 默认没有

    public static final String CALL_FLASH_ON = "call_flash_on";

    public static final String CALL_FLASH_TYPE = "call_flash_type";

    public static final String CALL_FLASH_TYPE_DYNAMIC_PATH = "call_flash_type_dynamic_path";

    public static final String CALL_FLASH_SHOW_TYPE_INSTANCE = "call_flash_show_type_instance"; // 当前使用的flash的实例(string形式)

    public static final String CALL_FLASH_LAST_ENTER_TIME = "call_flash_last_enter_time";

    public static final String IS_ENABLE_CALLFLASH_DEFAULT = "is_enable_callflash_default";//是否默认开启flashcall， 0 默认不开启
    public static final String NOT_ENABLE_CALLFLASH_DEFAULT_LIST = "not_enable_callflash_default_list";//不默认开启flash call的国家, 默认没有, example "US,"

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
            return ;
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
            return ;
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
            return ;
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
        if (null == data||mContext==null)
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
}
