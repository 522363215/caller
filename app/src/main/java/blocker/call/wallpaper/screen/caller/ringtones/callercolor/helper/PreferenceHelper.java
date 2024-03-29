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

final public class PreferenceHelper {
    private static final String TAG = "PreferenceHelper";
    private static final String PREF_FILE_NAME = "com_callercolor_pref";

    public static final boolean DEFAULT_VALUE_FOR_CALL_FLASH = false;
    public static final boolean DEFAULT_VALUE_FOR_CALLER_ID = true;
    public static final boolean DEFAULT_VALUE_FOR_MESSAGE = false;

    /**
     * 获取权限是否由设置界面返回
     */
    public static final String PREF_KEY_IS_RETURN_FROM_SETTING_ACTIIVTY = "android_is_return_from_setting_activity";

    /**
     * 上一次CALL FLASh SHOW 2 显示GROUP banner 广告的时间
     */
    public static final String PREF_LAST_CALL_FLASH_SHOW_GROUP_BANNER_AD_TIME = "last_call_flash_show_group_banner_ad_time";
    /**
     * 上一次CALL FLASh SHOW 2 停止循环显示GROUP banner的 广告的时间
     */
    public static final String PREF_LAST_CALL_FLASH_STOP_LOOPER_SHOW_AD_TIME = "last_call_flash_stop_looper_show_group_banner_ad_time";

    /**
     * 联系人是否查询完成
     */
    public static final String PREF_CONTACT_IS_QUERY_COMPLETE = "android_contact_is_query_complete";

    /**
     * 联系人的id对应的Version的map,用于判断联系人是否变化
     */
    public static final String PREF_CONTACT_VERSION_FOR_ID_MAP = "android_contact_version_for_id_map";

    public final static String PREF_KEY_LAST_FEEDBACK_TIME = "pref_key_last_feedback_time";

    public static final String PREF_KEY_IS_AGREE_SHOW_DIALOG = "android_rate_is_agree_show_dialog";

    public static final String PREF_CALL_SHOW_ANSWER_CLICK_END = "android_call_show_answer_click_end";

    public static final String PREF_CALL_SHOW_ANSWER_CLICK_START = "android_call_show_answer_click_start";

    /**
     * 当前媒体音量大小
     */
    public static final String PREF_CURRENT_MUSIC_VOLUEM = "android_current_music_coluem";

    /**
     * 上一次的铃声
     */
    public static final String PREF_LAST_RING_PATH = "android_last_ring_path";

    /**
     * 当前铃声的音量值
     */
    public static final String PREF_CURRENT_RING_VOLUME = "android_current_ring_volume";

    /**
     * 设为静音模式以前的模式
     */
    public static final String PREF_KEY_RING_MODE_BEFORE_SET_SILENT = "android_ring_mode_before_set_silent";

    // 是否开启callAfter;
    public static final String PREF_KEY_ENABLE_SHOW_CALL_AFTER = "caller_color_enable_show_call_after";

    /**
     * 来短信时是否显示短信界面
     */
    public static final String SHOW_MESSAGE_COME = "show_message_come";

    //daydream status
    public static final String PREF_KEY_DAY_DREAM_STATUS = "cc_day_dream_status";

    /**
     * 安装时间
     */
    public static final String PREF_KEY_INSTALL_TIME = "android_install_time";

    /**
     * 上一次显示启动页广告的时间
     */
    public static final String PREF_LAST_SHOW_SPLASH_AD_TIME = "android_last_show_splash_ad_time";
    /**
     * 上一次显示启动页初始化的时间
     */
    public static final String PREF_LAST_SHOW_SPLASH_INIT_TIME = "android_last_show_splash_init_time";
    /**
     * 上一次显示主动申请权限界面的时间
     */
    public static final String PREF_LAST_REQUEST_PERMISSION_TIME = "android_last_request_permission_time";

    /**
     * 上一次显示新手引导的时间
     */
    public static final String PREF_LAST_SHOW_FIRST_GUIDE_TIME = "android_last_show_first_guide_time";

    /**
     * 上传文件是否同意隐私政策;
     */
    public static final String PREF_KEY_MEDIA_UPLOAD_IS_AGREE_PRIVACY_POLICY = "caller_pref_key_call_flash_upload_is_agree_privacy_policy";

    /**
     * 上一次跳转到小米获取show on lock 权限的时间;
     */
    public static final String PREF_KEY_LAST_TO_XIAO_MI_SHOW_ON_LOCK_PERMISSION_ACTIVITY = "caller_pref_key_last_to_xiao_mi_show_on_lock_permission_activity";

    /**
     * 上一次跳转到小米获取自启动权限的时间;
     */
    public static final String PREF_KEY_LAST_TO_XIAO_MI_AUTO_START_BOOT_PERMISSION_ACTIVITY = "caller_pref_key_last_to_xiao_mi_auto_start_boot_permission_activity";

    /**
     * 最近一次进入App的时间
     */
    public static final String PREF_KEY_LAST_ENTER_APP_TIME = "caller_pref_key_last_enter_app_time";

    /**
     * 是否显示新手 来电秀设置 引导
     */
    public static final String PREF_KEY_IS_SHOW_CALL_FLASH_SET_GUIDE = "caller_pref_key_is_show_call_flash_set_guide";

    /**
     * 上一次swipe 界面广告加载成功的时间
     */
    public static final String PREF_LAST_LOADED_SWIPE_AD_TIME = "caller_pref_key_last_loaded_swipe_ad_time";

    /**
     * 视频加密对应的md5 map
     */
    public static final String PREF_VIDEO_ENCRYPT_MD5_MAP = "caller_pref_key_off_set_bytes_map";

    /**
     * 上一次初始化外部变现的时间
     */
    public static final String PREF_LAST_INIT_EXTERNAL_MAGIC_TIME = "caller_pref_key_last_init_external_magic_time";


    private PreferenceHelper() {
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
