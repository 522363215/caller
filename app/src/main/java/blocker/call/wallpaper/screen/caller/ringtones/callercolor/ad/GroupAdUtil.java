package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.content.SharedPreferences;

import com.md.serverflash.util.LogUtil;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.AdPreferenceHelper;

public class GroupAdUtil {
    private static final String TAG = "GroupAdUtil";
    //来电秀下载页  group
    public static final String KEY_FLASH_DOWN_GROUP_FACEBOOK_HIGH = "pref_flash_down_ads_group_facebook_high";
    public static final String KEY_FLASH_DOWN_GROUP_FACEBOOK_MEDIUM = "pref_flash_down_ads_group_facebook_medium";
    public static final String KEY_FLASH_DOWN_GROUP_FACEBOOK_NORMAL = "pref_flash_down_ads_group_facebook_normal";

    public static final String KEY_FLASH_DOWN_GROUP_ADMOB_HIGH = "pref_flash_down_ads_group_admob_high";
    public static final String KEY_FLASH_DOWN_GROUP_ADMOB_MEDIUM = "pref_flash_down_ads_group_admob_medium";
    public static final String KEY_FLASH_DOWN_GROUP_ADMOB_NORMAL = "pref_flash_down_ads_group_admob_normal";

    public static final String KEY_FLASH_DOWN_GROUP_ADMOB_ADX_HIGH = "pref_flash_down_ads_group_admob_adx_high";
    public static final String KEY_FLASH_DOWN_GROUP_ADMOB_ADX_MEDIUM = "pref_flash_down_ads_group_admob_adx_medium";
    public static final String KEY_FLASH_DOWN_GROUP_ADMOB_ADX_NORMAL = "pref_flash_down_ads_group_admob_adx_normal";

    //group banner
    public static final String KEY_BANNER_GROUP_FACEBOOK_HIGH = "pref_banner_ads_group_facebook_high";
    public static final String KEY_BANNER_GROUP_FACEBOOK_MEDIUM = "pref_banner_ads_group_facebook_medium";
    public static final String KEY_BANNER_GROUP_FACEBOOK_NORMAL = "pref_banner_ads_group_facebook_normal";

    public static final String KEY_BANNER_GROUP_ADMOB_HIGH = "pref_banner_ads_group_admob_high";
    public static final String KEY_BANNER_GROUP_ADMOB_MEDIUM = "pref_banner_ads_group_admob_medium";
    public static final String KEY_BANNER_GROUP_ADMOB_NORMAL = "pref_banner_ads_group_admob_normal";

    public static final String KEY_BANNER_GROUP_ADMOB_ADX_HIGH = "pref_banner_ads_group_admob_adx_high";
    public static final String KEY_BANNER_GROUP_ADMOB_ADX_MEDIUM = "pref_banner_ads_group_admob_adx_medium";
    public static final String KEY_BANNER_GROUP_ADMOB_ADX_NORMAL = "pref_banner_ads_group_admob_adx_normal";

    public static final String KEY_BIG_GROUP_FACEBOOK_HIGH = "pref_big_ads_group_facebook_high";
    public static final String KEY_BIG_GROUP_FACEBOOK_MEDIUM = "pref_big_ads_group_facebook_medium";
    public static final String KEY_BIG_GROUP_FACEBOOK_NORMAL = "pref_big_ads_group_facebook_normal";


    //来电秀下载页
    public static String getBigGroupIdByKey(String key) {
        String adid = "";
        switch (key) {
            case KEY_FLASH_DOWN_GROUP_FACEBOOK_HIGH:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_FLASH_DOWN_GROUP_FACEBOOK_MEDIUM:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_FLASH_DOWN_GROUP_FACEBOOK_NORMAL:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_FLASH_DOWN_GROUP_ADMOB_HIGH:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_FLASH_DOWN_GROUP_ADMOB_MEDIUM:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_FLASH_DOWN_GROUP_ADMOB_NORMAL:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_FLASH_DOWN_GROUP_ADMOB_ADX_HIGH:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_FLASH_DOWN_GROUP_ADMOB_ADX_MEDIUM:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_FLASH_DOWN_GROUP_ADMOB_ADX_NORMAL:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            default:
                break;
        }
        LogUtil.d(TAG, "_FLASH_DOWN getBannerGroupIdByKey key: " + key + ", adid: " + adid);
        return adid;
    }

    public static String getBannerGroupIdByKey(String key) {
        String adid = "";
        switch (key) {
            case KEY_BANNER_GROUP_FACEBOOK_HIGH:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_BANNER_GROUP_FACEBOOK_MEDIUM:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_BANNER_GROUP_FACEBOOK_NORMAL:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_BANNER_GROUP_ADMOB_HIGH:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_BANNER_GROUP_ADMOB_MEDIUM:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_BANNER_GROUP_ADMOB_NORMAL:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_BANNER_GROUP_ADMOB_ADX_HIGH:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_BANNER_GROUP_ADMOB_ADX_MEDIUM:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            case KEY_BANNER_GROUP_ADMOB_ADX_NORMAL:
                adid = AdPreferenceHelper.getString(key, "");
                break;
            default:
                break;
        }
        LogUtil.d(TAG, "loadBannerGroupId getBannerGroupIdByKey key: " + key + ", adid: " + adid);
        return adid;
    }

}
