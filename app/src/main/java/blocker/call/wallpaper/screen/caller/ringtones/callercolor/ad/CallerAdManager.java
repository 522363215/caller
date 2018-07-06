package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


import com.md.serverflash.util.LogUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventInterstitialAdLoadSuccess;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.AdPreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.FullScreenAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.AppUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import event.EventBus;

/**
 * Created by Leo on 17/5/23.
 */

public class CallerAdManager {
    private static final String TAG = "CallerAdManager";
    public static boolean isShowSplashAds = false;
    public final static String PARAM_ALL_COUNTRIES = "all_countries";

    public final static String KEY_SERVER_RANDOM_ADID_BASE = "key_server_random_adid_base_";

    public static final String AD_FACEBOOK = "facebook";
    public static final String AD_ADMOB = "admob";

    //du ads position
    public final static int DU_ADS_SPLASH = 1;
    public final static int DU_ADS_RESULT = 2;
    public final static int DU_ADS_CALL_FLASH = 3;
    public final static int DU_ADS_CALL_COMMON = 66;

    //用新的share preference file, AdPreferenceHelper
    //baidu sdk ads, 百度广告控制
    public static final String DU_AD_MASTER_SWITCH = "du_ad_master_switch"; //baidu 广告总开关, 0 关闭 ; 1 打开

    public static final String DU_AD_RESULT_SWITCH = "du_ad_result_switch"; //baidu 结果页广告开关, 0 关闭 ; 1 打开
    public static final String DU_AD_RESULT_COUNTRIES = "du_ad_result_countries"; //baidu 结果页广告显示国家
    public static final String DU_AD_RESULT_LIMIT = "du_ad_result_limit"; //次数限制， 默认99， 不限制
    public static final String DU_AD_RESULT_COUNT = "du_ad_result_count"; //当天已经请求的次数
    public static final String DU_AD_RESULT_DAY = "du_ad_result_day"; //请求的日期

    public static final String DU_AD_CALL_FLASH_SWITCH = "du_ad_call_flash_switch"; //baidu 来电秀设置页广告开关
    public static final String DU_AD_CALL_FLASH_COUNTRIES = "du_ad_call_flash_countries"; //baidu 来电秀设置页广告显示国家
    public static final String DU_AD_CALL_FLASH_LIMIT = "du_ad_call_flash_limit"; //次数限制， 默认99， 不限制
    public static final String DU_AD_CALL_FLASH_COUNT = "du_ad_call_flash_count"; //当天已经请求的次数
    public static final String DU_AD_CALL_FLASH_DAY = "du_ad_call_flash_day"; //请求的日期

    public static final String DU_AD_STARTUP_SWITCH = "du_ad_startup_switch"; //baidu 启动页广告开关
    public static final String DU_AD_STARTUP_COUNTRIES = "du_ad_startup_countries"; //baidu 启动页广告显示国家
    public static final String DU_AD_STARTUP_LIMIT = "du_ad_startup_limit"; //次数限制， 默认99， 不限制
    public static final String DU_AD_STARTUP_COUNT = "du_ad_startup_count"; //当天已经请求的次数
    public static final String DU_AD_STARTUP_DAY = "du_ad_startup_day"; //请求的日期

    // mopub advertisement id
    // mopub banner id (原生, 不可自定义)
//    public static final String MOPUB_ADV_BANNER_CALL_LOG_ID = "6fa0f7bd9caf48babcb5796eafbdd5ce";
    public static final String MOPUB_ADV_BANNER_CALL_LOG_ID = "2524ab8dcf7e47af85afdfc0c3c81ad0";
    // MoPub native banner id
    public static final String MOPUB_NATIVE_ADV_BANNER_CONTACT_ID = "177032700ca64e4c9f46fb60af5e5bf8";
    public static final String MOPUB_NATIVE_ADV_BANNER_CALL_LOG_ID = "7356345a752545228b9f685928943c6d";
    public static final String MOPUB_NATIVE_ADV_BANNER_BLOCK_ID = "4189b1d7b9d246419c04d352fb549cd4";
    public static final String MOPUB_NATIVE_ADV_BANNER_CONVERSATION_ID = "df23845475f94fb596239575e7a9de20";
    public static final String MOPUB_NATIVE_ADV_BANNER_CALL_FLASH_ID = "022886c74ee94807a7fe5769edd78e5c";
    public static final String MOPUB_NATIVE_ADV_BANNER_MESSAGE_FLASH_ID = "29f60e3bf8ee405bb1df63b4dd51e5cb";
    public static final String MOPUB_NATIVE_ADV_BANNER_PHONE_DETAIL_ID = "ff0eba224649445bb8abf664f60f5395";
    public static final String MOPUB_NATIVE_ADV_BANNER_FAKE_CALL_ID = MOPUB_NATIVE_ADV_BANNER_PHONE_DETAIL_ID;
    // MoPub native big id
    public static final String MOPUB_NATIVE_ADV_BIG_NUMBER_SCAN_RESULT_ID = "ea2967fbfe344dfe8e07321554915022";
    public static final String MOPUB_NATIVE_ADV_BIG_SPAM_UPDATE_RESULT_ID = MOPUB_NATIVE_ADV_BIG_NUMBER_SCAN_RESULT_ID;
    public static final String MOPUB_NATIVE_ADV_BIG_CALL_FLASH_RESULT_ID = "a8ec14daee5243659f4fea8679d0e797";
    public static final String MOPUB_NATIVE_ADV_BIG_MESSAGE_FLASH_RESULT_ID = MOPUB_NATIVE_ADV_BIG_CALL_FLASH_RESULT_ID;
    public static final String MOPUB_NATIVE_ADV_BIG_CONTACT_ID = "6d4c81de9ca2483787cefd2161440077";
    public static final String MOPUB_NATIVE_ADV_BIG_SPLASH_ID = "1fb5f7a55c3249b5b63741aa087c452b";
    public static final String MOPUB_NATIVE_ADV_BIG_CALL_AFTER_ID = "35eeca29eec048f78638e99f9f1ee5b6";
    public static final String MOPUB_NATIVE_ADV_BIG_MESSAGE_RECEIVE_ID = MOPUB_NATIVE_ADV_BIG_CALL_AFTER_ID;
    public static final String MOPUB_NATIVE_ADV_BIG_MESSAGE_COMPOSE_ID = MOPUB_NATIVE_ADV_BIG_CALL_AFTER_ID;
    public static final String MOPUB_NATIVE_ADV_BIG_MESSAGE_FLOAT_WINDOW_ID = MOPUB_NATIVE_ADV_BIG_SPLASH_ID;

    //Mopub banner small, 320*50
    public static final String MOPUB_BANNER_SMALL_SMS_ID = "2524ab8dcf7e47af85afdfc0c3c81ad0";

    //Mopub banner big, 300*250
    public static final String MOPUB_BANNER_BIG_END_CALL_ID = "3b3c94e9a91d4a91a849d4a0710f073b";


    // baidu ad banner id
    public static final int BAIDU_ADV_BANNER_CALL_FLASH_ID = 142285;
    public static final int BAIDU_ADV_BANNER_MESSAGE_FLASH_ID = BAIDU_ADV_BANNER_CALL_FLASH_ID;

    public static final int BAIDU_ADV_BANNER_CONTACT_ID = 148756;
    public static final int BAIDU_ADV_BANNER_CALL_LOG_ID = 142249;
    public static final int BAIDU_ADV_BANNER_BLOCK_ID = 148755;
    public static final int BAIDU_ADV_BANNER_CONVERSATION_ID = 148752;
    public static final int BAIDU_ADV_BANNER_PHONE_DETAIL_ID = 148751;
    public static final int BAIDU_ADV_BANNER_FAKE_CALL_ID = BAIDU_ADV_BANNER_CALL_FLASH_ID;
    // baidu ad big id
    public static final int BAIDU_ADV_BIG_SPLASH_ID = 140957;
    public static final int BAIDU_ADV_BIG_CALL_FLASH_RESULT_ID = 148750;
    public static final int BAIDU_ADV_BIG_MESSAGE_FLASH_RESULT_ID = BAIDU_ADV_BIG_CALL_FLASH_RESULT_ID;

    public static final int BAIDU_ADV_BIG_NUMBER_SCAN_RESULT_ID = 142286;
    public static final int BAIDU_ADV_BIG_UPDATE_SAPM_RESULT_ID = BAIDU_ADV_BIG_NUMBER_SCAN_RESULT_ID;
    public static final int BAIDU_ADV_BIG_CONTACT_ID = 148757;
    public static final int BAIDU_ADV_BIG_CALL_AFTER_ID = 148754;
    public static final int BAIDU_ADV_BIG_MESSAGE_RECEIVE_ID = BAIDU_ADV_BIG_CALL_AFTER_ID;
    public static final int BAIDU_ADV_BIG_MESSAGE_COMPOSE_ID = 148753;
    public static final int BAIDU_ADV_BIG_MESSAGE_FLOAT_WINDOW_ID = BAIDU_ADV_BIG_CALL_AFTER_ID;

    // admob banner big ad id (size: 300x250);
    public static final String ADMOB_BIG_BANNER_CONTACT_BIG_ID = "ca-app-pub-3275593620830282/4419738739";

    //通话记录详情页fb_id
    public static final String PREF_IS_PHONE_DETAIL_NEW = "pref_is_phone_detail_new";
    public static final String PREF_NEW_PHONE_DETAIL_FB_ID = "pref_new_phone_detail_fb_id";

    //联系人大头像fb_id
    public static final String PREF_IS_CONTACT_BIG_NEW = "pref_is_contact_big_new";
    public static final String PREF_NEW_CONTACT_BIG_FB_ID = "pref_new_contact_big_fb_id";

    //带量开关, 0打开
    public static final String PREF_IS_ENABLE_LF_INTRO = "pref_is_enable_lf_intro";

    //返回 显示显示启动页的位置，目前只有联系人 activity
    public final static int P_CONTACT_AC = 1; //position
    public static final String IS_SHOW_SPLASH_CURRENT_SWITCH = "is_show_splash_current_switch"; //广告开关, 0 关闭 ; 1 打开
    public static final String IS_SHOW_SPLASH_CURRENT_COUNTRIES = "is_show_splash_current_countries"; //广告显示国家

    //插页广告控制
    // Interstitial 插页广告, 0 not show, 1 show,
    public static final String IN_ADS_RESULT_LIMIT = "in_ads_result_limit"; //次数限制， 默认99， 不限制
    public static final String IN_ADS_RESULT_COUNT = "in_ads_result_count"; //当天已经请求的次数
    public static final String IN_ADS_RESULT_DAY = "in_ads_result_day"; //请求的日期
    public static final String PREF_IS_IN_ADS_RESULT_NEW = "pref_is_in_ads_result_new";
    public static final String PREF_NEW_IN_ADS_RESULT_FB_ID = "pref_new_in_ads_result_fb_id";
    public final static int IN_ADS_RESULT = 1; //result
    public final static int IN_ADS_CALL_FLASH = 2; //fb call flash detail/download
    public final static int IN_ADS_CALL_FLASH_RESULT = 3; //fb call flash result
    public static final String PREF_KEY_IS_INTERSTITIAL_RESULT = "is_interstitial_result"; //0 not show, 是否显示结果页插页总开关, 新， 原来还有一个
    public static final String IN_ADS_RESULT_COUNTRIES = "in_ads_result_countries"; //插页页广告显示国家, all_countries 则不限制


    //group 插屏
    public static final String KEY_IN_ADS_GROUP_IDS = "cid_in_ads_group_ids";

    //结果页插屏
    public static final String KEY_IN_GROUP_FACEBOOK_HIGH = "pref_in_ads_group_facebook_high";
    public static final String KEY_IN_GROUP_FACEBOOK_MEDIUM = "pref_in_ads_group_facebook_medium";
    public static final String KEY_IN_GROUP_FACEBOOK_NORMAL = "pref_in_ads_group_facebook_normal";

    public static final String KEY_IN_GROUP_ADMOB_HIGH = "pref_in_ads_group_admob_high";
    public static final String KEY_IN_GROUP_ADMOB_MEDIUM = "pref_in_ads_group_admob_medium";
    public static final String KEY_IN_GROUP_ADMOB_NORMAL = "pref_in_ads_group_admob_normal";

    public static final String KEY_IN_GROUP_ADMOB_ADX_HIGH = "pref_in_ads_group_admob_adx_high";
    public static final String KEY_IN_GROUP_ADMOB_ADX_MEDIUM = "pref_in_ads_group_admob_adx_medium";
    public static final String KEY_IN_GROUP_ADMOB_ADX_NORMAL = "pref_in_ads_group_admob_adx_normal";

    public static final String IN_GROUP_FACEBOOK_HIGH_ID = "";  //cid_ad138:198653420649711_377239949457723
    public static final String IN_GROUP_FACEBOOK_MEDIUM_ID = ""; //cid_ad137:198653420649711_377239736124411
    public static final String IN_GROUP_FACEBOOK_NORMAL_ID = ""; //cid_ad136

    public static final String IN_GROUP_ADMOB_HIGH_ID = "";//ADMOB_ADV_SMS_DISPLAY_Interstitial_ID:ca-app-pub-3275593620830282/7721179484
    public static final String IN_GROUP_ADMOB_MEDIUM_ID = "";//ADMOB_ADV_PHONE_DETAIL_Interstitial_ID；ca-app-pub-3275593620830282/9474557930
    public static final String IN_GROUP_ADMOB_NORMAL_ID = "ca-app-pub-3275593620830282/6465251217"; //ADMOB_ADV_AFTER_CALL_Interstitial_ID

    public static final String IN_GROUP_ADMOB_ADX_HIGH_ID = "";//cid_screen_插屏:ca-mb-app-pub-9321850975912681/3362869408
    public static final String IN_GROUP_ADMOB_ADX_MEDIUM_ID = "";//cid_set_插屏:ca-mb-app-pub-9321850975912681/3187177264
    public static final String IN_GROUP_ADMOB_ADX_NORMAL_ID = "ca-mb-app-pub-9321850975912681/7481625871"; //cid_result_插屏:ca-mb-app-pub-9321850975912681/7481625871


    //来电秀相关插屏
    public static final String KEY_FLASH_IN_GROUP_FACEBOOK_HIGH = "pref_flash_in_ads_group_facebook_high";
    public static final String KEY_FLASH_IN_GROUP_FACEBOOK_MEDIUM = "pref_flash_in_ads_group_facebook_medium";
    public static final String KEY_FLASH_IN_GROUP_FACEBOOK_NORMAL = "pref_flash_in_ads_group_facebook_normal";

    public static final String KEY_FLASH_IN_GROUP_ADMOB_HIGH = "pref_flash_in_ads_group_admob_high";
    public static final String KEY_FLASH_IN_GROUP_ADMOB_MEDIUM = "pref_flash_in_ads_group_admob_medium";
    public static final String KEY_FLASH_IN_GROUP_ADMOB_NORMAL = "pref_flash_in_ads_group_admob_normal";

    public static final String KEY_FLASH_IN_GROUP_ADMOB_ADX_HIGH = "pref_flash_in_ads_group_admob_adx_high";
    public static final String KEY_FLASH_IN_GROUP_ADMOB_ADX_MEDIUM = "pref_flash_in_ads_group_admob_adx_medium";
    public static final String KEY_FLASH_IN_GROUP_ADMOB_ADX_NORMAL = "pref_flash_in_ads_group_admob_adx_normal";

    public static final String FLASH_IN_GROUP_FACEBOOK_HIGH_ID = "";  //cid_ad138:198653420649711_377239949457723
    public static final String FLASH_IN_GROUP_FACEBOOK_MEDIUM_ID = ""; //cid_ad137:198653420649711_377239736124411
    public static final String FLASH_IN_GROUP_FACEBOOK_NORMAL_ID = ""; //cid_ad136

    public static final String FLASH_IN_GROUP_ADMOB_HIGH_ID = "";//ADMOB_ADV_SMS_DISPLAY_Interstitial_ID:ca-app-pub-3275593620830282/7721179484
    public static final String FLASH_IN_GROUP_ADMOB_MEDIUM_ID = "";//ADMOB_ADV_PHONE_DETAIL_Interstitial_ID；ca-app-pub-3275593620830282/9474557930
    public static final String FLASH_IN_GROUP_ADMOB_NORMAL_ID = ""; //ADMOB_ADV_AFTER_CALL_Interstitial_ID

    public static final String FLASH_IN_GROUP_ADMOB_ADX_HIGH_ID = "";//cid_screen_插屏:ca-mb-app-pub-9321850975912681/3362869408
    public static final String FLASH_IN_GROUP_ADMOB_ADX_MEDIUM_ID = "";//cid_set_插屏:ca-mb-app-pub-9321850975912681/3187177264
    public static final String FLASH_IN_GROUP_ADMOB_ADX_NORMAL_ID = ""; //cid_result_插屏:ca-mb-app-pub-9321850975912681/7481625871

    //group 插屏 end

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

    //ca-mb-app-pub-9321850975912681/9201762660, cid_banner_one, high
    //ca-mb-app-pub-9321850975912681/1379169930, cid_banner_two, medium
    //ca-mb-app-pub-9321850975912681/7027630739, cid_banner_three, normal

    //group banner end

    public static final String FIRST_SHOW_ADMOB_SPLASH = "first_show_admob_splash"; //启动页第一次显示admob

    static {
        if (!ConstantUtils.getNotShowSplashAds().contains(ApplicationEx.getInstance().country)) {
            isShowSplashAds = true;//显示启动广告
        }

    }

    /**
     * @param position               插屏的的位置
     * @param interstitialAdPriority 插屏的优先级
     * @param adType                 插屏的广告类型
     */
    public static String getInGroupIdByKey(int position, String interstitialAdPriority, String adType) {
        String adid = "";
        String key = "";
        switch (position) {
            case IN_ADS_RESULT:
                key = "pref_in_ads_group_" + adType + "_" + interstitialAdPriority.toLowerCase();
                adid = getInGroupAdIdResultByKey(key);
                break;
            case IN_ADS_CALL_FLASH:
            case IN_ADS_CALL_FLASH_RESULT:
                key = "pref_flash_in_ads_group_" + adType + "_" + interstitialAdPriority.toLowerCase();
                adid = getInGroupAdIdFlashByKey(key);
                break;
        }
        LogUtil.d(TAG, "loadInterstitialAd getInGroupIdByKey key: " + key + ", adid: " + adid + ",position:" + position);
        return adid;
    }


    //获取非来新秀相关界面的结果页插屏id
    private static String getInGroupAdIdResultByKey(String key) {
        String adid = "";
        switch (key) {
            case KEY_IN_GROUP_FACEBOOK_HIGH:
                adid = AdPreferenceHelper.getString(key, IN_GROUP_FACEBOOK_HIGH_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = IN_GROUP_FACEBOOK_HIGH_ID;
                }
                break;
            case KEY_IN_GROUP_FACEBOOK_MEDIUM:
                adid = AdPreferenceHelper.getString(key, IN_GROUP_FACEBOOK_MEDIUM_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = IN_GROUP_FACEBOOK_MEDIUM_ID;
                }
                break;
            case KEY_IN_GROUP_FACEBOOK_NORMAL:
                adid = AdPreferenceHelper.getString(key, IN_GROUP_FACEBOOK_NORMAL_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = IN_GROUP_FACEBOOK_NORMAL_ID;
                }
                break;
            case KEY_IN_GROUP_ADMOB_HIGH:
                adid = AdPreferenceHelper.getString(key, IN_GROUP_ADMOB_HIGH_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = IN_GROUP_ADMOB_HIGH_ID;
                }
                break;
            case KEY_IN_GROUP_ADMOB_MEDIUM:
                adid = AdPreferenceHelper.getString(key, IN_GROUP_ADMOB_MEDIUM_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = IN_GROUP_ADMOB_MEDIUM_ID;
                }
                break;
            case KEY_IN_GROUP_ADMOB_NORMAL:
                adid = AdPreferenceHelper.getString(key, IN_GROUP_ADMOB_NORMAL_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = IN_GROUP_ADMOB_NORMAL_ID;
                }
                break;
            case KEY_IN_GROUP_ADMOB_ADX_HIGH:
                adid = AdPreferenceHelper.getString(key, IN_GROUP_ADMOB_ADX_HIGH_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = IN_GROUP_ADMOB_ADX_HIGH_ID;
                }
                break;
            case KEY_IN_GROUP_ADMOB_ADX_MEDIUM:
                adid = AdPreferenceHelper.getString(key, IN_GROUP_ADMOB_ADX_MEDIUM_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = IN_GROUP_ADMOB_ADX_MEDIUM_ID;
                }
                break;
            case KEY_IN_GROUP_ADMOB_ADX_NORMAL:
                adid = AdPreferenceHelper.getString(key, IN_GROUP_ADMOB_ADX_NORMAL_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = IN_GROUP_ADMOB_ADX_NORMAL_ID;
                }
                break;
            default:
                break;
        }
        return adid;
    }

    //获取来电秀相关界面插屏id(包括设置插屏结果页的插屏)
    private static String getInGroupAdIdFlashByKey(String key) {
        String adid = "";
        switch (key) {
            case KEY_FLASH_IN_GROUP_FACEBOOK_HIGH:
                adid = AdPreferenceHelper.getString(key, FLASH_IN_GROUP_FACEBOOK_HIGH_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = FLASH_IN_GROUP_FACEBOOK_HIGH_ID;
                }
                break;
            case KEY_FLASH_IN_GROUP_FACEBOOK_MEDIUM:
                adid = AdPreferenceHelper.getString(key, FLASH_IN_GROUP_FACEBOOK_MEDIUM_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = FLASH_IN_GROUP_FACEBOOK_MEDIUM_ID;
                }
                break;
            case KEY_FLASH_IN_GROUP_FACEBOOK_NORMAL:
                adid = AdPreferenceHelper.getString(key, FLASH_IN_GROUP_FACEBOOK_NORMAL_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = FLASH_IN_GROUP_FACEBOOK_NORMAL_ID;
                }
                break;
            case KEY_FLASH_IN_GROUP_ADMOB_HIGH:
                adid = AdPreferenceHelper.getString(key, FLASH_IN_GROUP_ADMOB_HIGH_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = FLASH_IN_GROUP_ADMOB_HIGH_ID;
                }
                break;
            case KEY_FLASH_IN_GROUP_ADMOB_MEDIUM:
                adid = AdPreferenceHelper.getString(key, FLASH_IN_GROUP_ADMOB_MEDIUM_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = FLASH_IN_GROUP_ADMOB_MEDIUM_ID;
                }
                break;
            case KEY_FLASH_IN_GROUP_ADMOB_NORMAL:
                adid = AdPreferenceHelper.getString(key, FLASH_IN_GROUP_ADMOB_NORMAL_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = FLASH_IN_GROUP_ADMOB_NORMAL_ID;
                }
                break;
            case KEY_FLASH_IN_GROUP_ADMOB_ADX_HIGH:
                adid = AdPreferenceHelper.getString(key, FLASH_IN_GROUP_ADMOB_ADX_HIGH_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = FLASH_IN_GROUP_ADMOB_ADX_HIGH_ID;
                }
                break;
            case KEY_FLASH_IN_GROUP_ADMOB_ADX_MEDIUM:
                adid = AdPreferenceHelper.getString(key, FLASH_IN_GROUP_ADMOB_ADX_MEDIUM_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = FLASH_IN_GROUP_ADMOB_ADX_MEDIUM_ID;
                }
                break;
            case KEY_FLASH_IN_GROUP_ADMOB_ADX_NORMAL:
                adid = AdPreferenceHelper.getString(key, FLASH_IN_GROUP_ADMOB_ADX_NORMAL_ID);
                if (adid == null || adid.trim().equals("")) {
                    adid = FLASH_IN_GROUP_ADMOB_ADX_NORMAL_ID;
                }
                break;
            default:
                break;
        }
        return adid;
    }

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

    //大图广告空白区域默认不可点
    public static boolean isWhiteSpacesClickable(String position) {
        boolean clickable = false;
        if (PreferenceHelper.getInt(ConstantUtils.FB_BIG_WHITE_SPACES_CLICKABLE, 0) != 0) {
            if (ConstantUtils.getBigFbClickablePosition().contains(position)) {
                if (ConstantUtils.getBigFbClickableCountries().contains(ApplicationEx.getInstance().country)) {
                    clickable = true;
                }
            }
            //channel control
            if (clickable) {
                if (isClickableByChannel(1)) {
                    clickable = true;
                } else {
                    clickable = false;
                }
            }
        }
        return clickable;
    }

    public static boolean isShowMopub(String key) {
        boolean show = false;
        if (isMopubAll() || isMopub(key)) {
            show = true;
        }
        return show;
    }

    public static boolean isMopubAll() {
        boolean is = false; //false not show, 默认关, test is true
        //get mopub all from server

        if (AdPreferenceHelper.getInt("pref_mopub_master_switcher", 0) != 0) { //mopub 总开关
            is = true; //show
        }
        if (is) {
            String countires = AdPreferenceHelper.getString("pref_mopub_show_ads_countries", "");
            if (!TextUtils.isEmpty(countires) && (countires.equals(PARAM_ALL_COUNTRIES) || getShowMopubAdsCountries().contains(ApplicationEx.getInstance().country))) {
                is = true;
            }
        }
        return is;
    }

    public static boolean isMopub(String key) {
        boolean is = false;
        List<String> ads_key = AdvertisementSwitcher.getInstance().getAdPriority(key);
        if (ads_key != null && ads_key.size() > 0 && ads_key.get(0) != null) {
            if (ads_key.get(0).equalsIgnoreCase(AdvertisementSwitcher.AD_MOPUB)) {
                is = true;
                LogUtil.d("isMopub", "ads key use: " + ads_key);
            }
        }
        return is;
    }

    public static boolean isMopubBannerSelf(String key) {
        boolean is = false;
        List<String> ads_key = AdvertisementSwitcher.getInstance().getAdPriority(key);
        if (ads_key != null && ads_key.size() > 0 && ads_key.get(0) != null) {
            if (ads_key.get(0).equalsIgnoreCase(AdvertisementSwitcher.AD_MOPUB_BANNER)) {
                is = true;
                LogUtil.d("mopub_self", "ads key use: " + ads_key);
            }
        }
        return is;
    }

    public static boolean isAdmobAdx(String key) {
        boolean is = false;
        List<String> ads_key = AdvertisementSwitcher.getInstance().getAdPriority(key);
        if (ads_key != null && ads_key.size() > 0 && ads_key.get(0) != null) {
            if (ads_key.get(0).equalsIgnoreCase(AdvertisementSwitcher.AD_ADMOB_ADX)) {
                is = true;
                LogUtil.d("isAdmobAdx", "ads key use: " + ads_key);
            }
        }
        return is;
    }


    public static boolean isAdmob(String key) {
        boolean is = false;
        List<String> ads_key = AdvertisementSwitcher.getInstance().getAdPriority(key);
        if (ads_key != null && ads_key.size() > 0 && ads_key.get(0) != null) {
            if (ads_key.get(0).equalsIgnoreCase(AD_ADMOB)) {
                is = true;
                LogUtil.d("isAdmob", "ads key use: " + ads_key);
            }
        }
        return is;
    }

    public static boolean isFBAds(String key) {
        boolean is = false;
        List<String> ads_key = AdvertisementSwitcher.getInstance().getAdPriority(key);
        if (ads_key != null && ads_key.size() > 0 && ads_key.get(0) != null) {
            if (ads_key.get(0).equalsIgnoreCase(AD_FACEBOOK)) {
                is = true;
                LogUtil.d("isFBAds", "ads key use: " + ads_key);
            }
        }
        return is;
    }

    public static boolean isDuAll() {
        boolean is = true;
        return is;
    }

    public static boolean isDU(int position) {
        boolean is = false;

        int master_switch = AdPreferenceHelper.getInt(DU_AD_MASTER_SWITCH, 0);

        if (master_switch == 0) { //0 not show
            return is;
        }


        switch (position) {
            case DU_ADS_SPLASH:
                int sw1 = AdPreferenceHelper.getInt(DU_AD_STARTUP_SWITCH, 0);
                if (sw1 != 0) {
                    String countires = AdPreferenceHelper.getString(DU_AD_STARTUP_COUNTRIES, "");
                    if (!TextUtils.isEmpty(countires) && (countires.equals(PARAM_ALL_COUNTRIES) || getShowDUSplashAds().contains(ApplicationEx.getInstance().country))) {
                        if (isLessThanLimit(position)) {
                            is = true;
                        }
                    }
                }
                break;
            case DU_ADS_RESULT:
                int sw2 = AdPreferenceHelper.getInt(DU_AD_RESULT_SWITCH, 0);
                if (sw2 != 0) {
                    String countires = AdPreferenceHelper.getString(DU_AD_RESULT_COUNTRIES, "");
                    if (!TextUtils.isEmpty(countires) && (countires.equals(PARAM_ALL_COUNTRIES) || getShowDUResultsAds().contains(ApplicationEx.getInstance().country))) {
                        if (isLessThanLimit(position)) {
                            is = true;
                        }
                    }
                }
                break;
            case DU_ADS_CALL_FLASH:
                int sw3 = AdPreferenceHelper.getInt(DU_AD_CALL_FLASH_SWITCH, 0);
                if (sw3 != 0) {
                    String countires = AdPreferenceHelper.getString(DU_AD_CALL_FLASH_COUNTRIES, "");
                    if (!TextUtils.isEmpty(countires) && (countires.equals(PARAM_ALL_COUNTRIES) || getShowDUCallFlashAds().contains(ApplicationEx.getInstance().country))) {
                        if (isLessThanLimit(position)) {
                            is = true;
                        }
                    }
                }
                break;
            case DU_ADS_CALL_COMMON:
                break;
            default:
                break;
        }

        LogUtil.d("du_ads", "isDU position: " + position + ", countires: " + AdPreferenceHelper.getString(DU_AD_STARTUP_COUNTRIES, "") + ", switch: " + AdPreferenceHelper.getInt(DU_AD_STARTUP_SWITCH, 0));

        return is;
    }

    private static boolean isLessThanLimit(int position) {
        boolean is = false;

        int master_switch = AdPreferenceHelper.getInt(DU_AD_MASTER_SWITCH, 0);

        if (master_switch == 0) {
            return is;
        }

        switch (position) {
            case DU_ADS_SPLASH:
                int count_limit1 = AdPreferenceHelper.getInt(DU_AD_STARTUP_LIMIT, 99);
                int count_today1 = AdPreferenceHelper.getInt(DU_AD_STARTUP_COUNT, 0);
                long show_day1 = AdPreferenceHelper.getLong(DU_AD_STARTUP_DAY, 0);
                if (!Stringutil.isTodayNew(show_day1)) {
                    count_today1 = 0;
                }
                if (count_today1 < count_limit1) {
                    is = true;
                    count_today1++;
                }
                AdPreferenceHelper.putInt(DU_AD_STARTUP_COUNT, count_today1);
                AdPreferenceHelper.putLong(DU_AD_STARTUP_DAY, System.currentTimeMillis());
                break;
            case DU_ADS_RESULT:
                int count_limit2 = AdPreferenceHelper.getInt(DU_AD_RESULT_LIMIT, 99);
                int count_today2 = AdPreferenceHelper.getInt(DU_AD_RESULT_COUNT, 0);
                long show_day2 = AdPreferenceHelper.getLong(DU_AD_RESULT_DAY, 0);
                if (!Stringutil.isTodayNew(show_day2)) {
                    count_today2 = 0;
                }
                if (count_today2 < count_limit2) {
                    is = true;
                    count_today2++;
                }
                AdPreferenceHelper.putInt(DU_AD_RESULT_COUNT, count_today2);
                AdPreferenceHelper.putLong(DU_AD_RESULT_DAY, System.currentTimeMillis());
                break;
            case DU_ADS_CALL_FLASH:
                int count_limit3 = AdPreferenceHelper.getInt(DU_AD_CALL_FLASH_LIMIT, 99);
                int count_today3 = AdPreferenceHelper.getInt(DU_AD_CALL_FLASH_COUNT, 0);
                long show_day3 = AdPreferenceHelper.getLong(DU_AD_CALL_FLASH_DAY, 0);
                if (!Stringutil.isTodayNew(show_day3)) {
                    count_today3 = 0;
                }
                if (count_today3 < count_limit3) {
                    is = true;
                    count_today3++;
                }
                AdPreferenceHelper.putInt(DU_AD_CALL_FLASH_COUNT, count_today3);
                AdPreferenceHelper.putLong(DU_AD_CALL_FLASH_DAY, System.currentTimeMillis());
                break;
            default:
                break;
        }

        return is;
    }

    private static boolean isLessThanLimitIn(int position) {
        boolean is = false;
        switch (position) {
            case IN_ADS_RESULT:
                int count_limit1 = AdPreferenceHelper.getInt(IN_ADS_RESULT_LIMIT, 99);
                int count_today1 = AdPreferenceHelper.getInt(IN_ADS_RESULT_COUNT, 0);
                long show_day1 = AdPreferenceHelper.getLong(IN_ADS_RESULT_DAY, 0);
                if (!Stringutil.isTodayNew(show_day1)) {
                    count_today1 = 0;
                }
                if (count_today1 < count_limit1) {
                    is = true;
                    count_today1++;
                }
                AdPreferenceHelper.putInt(IN_ADS_RESULT_COUNT, count_today1);
                AdPreferenceHelper.putLong(IN_ADS_RESULT_DAY, System.currentTimeMillis());
                break;
            default:
                break;
        }

        return is;
    }

    //是否fb插页广告
    public static boolean isFBInFirstAds() {
        boolean is = false;
        List<String> ads_key = AdvertisementSwitcher.getInstance().getAdPriority(AdvertisementSwitcher.SERVER_KEY_IN_RESULT);
        if (ads_key != null && ads_key.size() > 0 && ads_key.get(0) != null) {
            if (ads_key.get(0).equalsIgnoreCase(AD_FACEBOOK)) {
                is = true;
                LogUtil.d("isFBInAds", "ads key use: " + ads_key);
            }
        }
        return is;
    }

    /**
     * @param position, CallerAdManager.IN_ADS_RESULT
     * @return
     */
    public static boolean isShowInterstitial(int position) {
        boolean show = false;
        switch (position) {
            case IN_ADS_RESULT:
                int sw = AdPreferenceHelper.getInt("pref_show_interstitial_all_result", 0); //0 not show, //2018-04-12, 修改key, 这样以前的版本不会开启错误的插屏广告, old: is_interstitial_result
                if (sw == 1) {
                    long show_guide = PreferenceHelper.getLong(ConstantUtils.PREF_KEY_SHOW_GUIDE_TIME, 0);
                    long interval_from_install_bench = AdPreferenceHelper.getLong("pref_interstitial_from_install_bench", 0); //us 等发达国家 , 自然量安装24小时后才显示插屏, 24 * 60 * 60 * 1000=86400000， 渠道量立即开
                    LogUtil.d("isShowInterstitial", "loadInterstitial IN_ADS_RESULT interval_from_install_bench: " + interval_from_install_bench);
                    if (Math.abs(System.currentTimeMillis() - show_guide) >= interval_from_install_bench) {
                        LogUtil.d("isShowInterstitial", "loadInterstitial IN_ADS_RESULT true: ");
                        show = true;
                    }
                }
                break;
            case IN_ADS_CALL_FLASH_RESULT:
            case IN_ADS_CALL_FLASH:
                int show_call_flash = AdPreferenceHelper.getInt("pref_show_interstitial_call_flash", 1); //0 not show, 1 show
                if (show_call_flash == 1) {
                    LogUtil.d("isShowInterstitial", "loadInterstitial IN_ADS_CALL_FLASH true: ");
                    show = true;
                }
                break;
            default:
                break;
        }

        return show;
    }

    private static HashSet<String> getControlSet(String countries) {
        HashSet<String> sets = new HashSet<>();
        if (!TextUtils.isEmpty(countries) && countries.indexOf(",") != -1) {
            String[] arrays = countries.split(",");
            if (arrays != null && arrays.length > 0) {
                for (int k = 0; k < arrays.length; k++) {
                    if (!TextUtils.isEmpty(arrays[k])) {
                        sets.add(arrays[k].trim());
                    }
                }
            }
        }
        return sets;
    }

    //显示mopub广告的国家
    private static HashSet<String> getShowMopubAdsCountries() {
        String countries = AdPreferenceHelper.getString("pref_mopub_show_ads_countries", "");
        HashSet<String> sets = getControlSet(countries);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "getShowInAdsCountries countries: " + cc);
            }
        }
        return sets;
    }

    //显示插页广告的国家
    private static HashSet<String> getShowInAdsCountries() {
        String countries = AdPreferenceHelper.getString(CallerAdManager.IN_ADS_RESULT_COUNTRIES, "");
        HashSet<String> sets = getControlSet(countries);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "getShowInAdsCountries countries: " + cc);
            }
        }
        return sets;
    }

    private static HashSet<String> getShowDUSplashAds() {
        String countries = AdPreferenceHelper.getString(CallerAdManager.DU_AD_STARTUP_COUNTRIES, "");
        HashSet<String> sets = getControlSet(countries);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "ShowDUSplashAds countries: " + cc);
            }
        }
        return sets;
    }

    private static HashSet<String> getShowDUResultsAds() {
        String countries = AdPreferenceHelper.getString(CallerAdManager.DU_AD_RESULT_COUNTRIES, "");
        HashSet<String> sets = getControlSet(countries);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "ShowDUResultsAds countries: " + cc);
            }
        }
        return sets;
    }

    private static HashSet<String> getShowDUCallFlashAds() {
        String countries = AdPreferenceHelper.getString(CallerAdManager.DU_AD_CALL_FLASH_COUNTRIES, "");
        HashSet<String> sets = getControlSet(countries);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "ShowDUCallFlash countries: " + cc);
            }
        }
        return sets;
    }

    public static boolean isLoadFbInForOthers() {
        boolean load = false;
        int i_load = AdPreferenceHelper.getInt("pref_load_fb_in_for_adx", 1);
        if (i_load == 1) {
            load = true;
        }
        return load;
    }

    public synchronized static void loadInterstitialAd(Context context) {
        loadInterstitialAd(context, IN_ADS_RESULT);
    }

    public static void loadInterstitialAd(Context context, final int position) {
        if (isShowInterstitial(position)) {
            if (isShowFullScreenAd(position)) {
                FullScreenAdManager.getInstance().loadAd(position);
            } else {
                LogUtil.d(TAG, "loadInterstitialAd load start.");
                InterstitialAdvertisement.FbAdId fbAdId = new InterstitialAdvertisement.FbAdId();
                fbAdId.highId = getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.HIGH, InterstitialAdvertisement.AdType.FACEBOOK);
                fbAdId.mediumId = getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.MEDIUM, InterstitialAdvertisement.AdType.FACEBOOK);
                fbAdId.normalId = getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.NORMAL, InterstitialAdvertisement.AdType.FACEBOOK);

                InterstitialAdvertisement.AdmobAdId admobAdId = new InterstitialAdvertisement.AdmobAdId();
                admobAdId.highId = getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.HIGH, InterstitialAdvertisement.AdType.ADMOB);
                admobAdId.mediumId = getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.MEDIUM, InterstitialAdvertisement.AdType.ADMOB);
                admobAdId.normalId = getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.NORMAL, InterstitialAdvertisement.AdType.ADMOB);

                InterstitialAdvertisement.AdmobAdxId admobAdxId = new InterstitialAdvertisement.AdmobAdxId();
                admobAdxId.highId = getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.HIGH, InterstitialAdvertisement.AdType.ADMOB_ADX);
                admobAdxId.mediumId = getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.MEDIUM, InterstitialAdvertisement.AdType.ADMOB_ADX);
                admobAdxId.normalId = getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.NORMAL, InterstitialAdvertisement.AdType.ADMOB_ADX);


                InterstitialAdvertisement interstitialAdvertisement = ApplicationEx.getInstance().getInterstitialAdvertisement(position);
                boolean isInterstitialAdReload = isInterstitialAdReload(interstitialAdvertisement);
                if (isInterstitialAdReload) {
                    interstitialAdvertisement = new InterstitialAdvertisement(context, fbAdId, admobAdId, admobAdxId, AdvertisementSwitcher.SERVER_KEY_IN_RESULT);
                    ApplicationEx.getInstance().setInterstitialAdvertisement(interstitialAdvertisement, position);
                    interstitialAdvertisement.loadAd(new InterstitialAdvertisement.InterstitialAdLoadedListener() {
                        @Override
                        public void onAdLoaded(InterstitialAdvertisement interstitialAdvertisement1) {
//                        LogUtil.d(TAG, "InterstitialAdvertisement loadInterstitialAd Success");
                            ApplicationEx.getInstance().setInterstitialAdvertisement(interstitialAdvertisement1, position);
                            EventBus.getDefault().post(new EventInterstitialAdLoadSuccess());
                        }
                    });
                }
            }
        }
    }

    public static boolean isShowFullScreenAd(int position) {
        boolean isShow = false;
        switch (position) {
            case CallerAdManager.IN_ADS_RESULT:
                isShow = CallerAdManager.isShowFirstAdMob(CallerAdManager.POSITION_FIRST_ADMOB_FULL_SCREEN_RESULT_BACK, false);
                break;
            case CallerAdManager.IN_ADS_CALL_FLASH:
            case CallerAdManager.IN_ADS_CALL_FLASH_RESULT:
                isShow = CallerAdManager.isShowFirstAdMob(CallerAdManager.POSITION_FIRST_ADMOB_FULL_SCREEN_CALL_FLASH_DETAIL, false);
                break;
        }
        return isShow;
    }

    /**
     * 判断插屏是否重新load
     */
    public static boolean isInterstitialAdReload(InterstitialAdvertisement interstitialAdvertisement) {
        if (interstitialAdvertisement == null) return true;
        boolean isValid = interstitialAdvertisement.isValid(true);
        boolean isLoading = interstitialAdvertisement.isLoading();
        return !isValid && !isLoading;
    }

    //通话记录详情页
    public static String getPhoneDetailFbId() {
        String fb_id = "198653420649711_198697537311966";
        int use_id = AdPreferenceHelper.getInt(PREF_IS_PHONE_DETAIL_NEW, 0);
        if (use_id == 99) {
            fb_id = AdPreferenceHelper.getString(PREF_NEW_PHONE_DETAIL_FB_ID, "");
        }
        return fb_id;
    }

    public static final int POSITION_FB_ADS_CALLLOG = 1;
    public static final int POSITION_FB_ADS_SMSLIST = 2;
    public static final int POSITION_FB_ADS_BLOCKHOME = 3;
    public static final int POSITION_FB_ADS_CONTACTLIST = 4;
    public static final int POSITION_FB_ADS_CONTACTLIST_BIG = 14;
    public static final int POSITION_FB_ADS_CALLGIFCUSTOM = 5;
    public static final int POSITION_FB_ADS_SMS_EDIT = 6;
    public static final int POSITION_FB_ADS_SMS_FLASH_SHOW = 11;
    public static final int POSITION_FB_ADS_SMS_FLASH_SHOW_SET = 13;
    public static final int POSITION_FB_ADS_CALLFLASH = 10;
    public static final int POSITION_FB_ADS_CALLFLASH_HOT = 15;
    public static final int POSITION_FB_ADS_FAKECALL_HOME = 12;
    //big, 控制是否只有按钮可点
    public static final int POSITION_FB_ADS_SPLASH_BIG = 7;
    public static final int POSITION_FB_ADS_SCAN_RESULT_BIG = 8;
    public static final int POSITION_FB_ADS_ENDCALL_BIG = 9;


    public static String getCommonFbID(int position) {
        String fb_id = "";
        switch (position) {
            case POSITION_FB_ADS_CALLLOG:
                fb_id = ConstantUtils.FB_FAKE_CALL_ID;
                if (PreferenceHelper.getInt(ConstantUtils.IS_USE_CALLLOG_ID, 0) == 1) {
                    fb_id = ConstantUtils.FB_CALL_LOG_ID;
                } else if (PreferenceHelper.getInt(ConstantUtils.IS_USE_CALLLOG_ID, 0) == 99) {
                    fb_id = PreferenceHelper.getString("new_custom_calllog_fb_id", "");
                } else {//0
                    if (PreferenceHelper.getInt(ConstantUtils.IS_CALL_FLASH_ID, 0) == 99) {
                        fb_id = PreferenceHelper.getString(ConstantUtils.NEW_CALL_FLASH_ID, "");
                    }
                }

                break;
            case POSITION_FB_ADS_SMSLIST:
                fb_id = ConstantUtils.FB_FAKE_CALL_ID;
                int use_spec_id = PreferenceHelper.getInt(ConstantUtils.IS_USE_SMS_LIST, 0);
                if (use_spec_id == 1) {
                    fb_id = ConstantUtils.FB_SMS_LIST_ID;
                } else if (use_spec_id == 99) {
                    fb_id = PreferenceHelper.getString("new_custom_smslist_fb_id", "");
                } else {//0
                    int use_id = PreferenceHelper.getInt(ConstantUtils.IS_CALL_FLASH_ID, 0);
                    if (use_id == 0) {
                        fb_id = ConstantUtils.FB_FAKE_CALL_ID; //合并id
                    } else if (use_id == 99) {
                        fb_id = PreferenceHelper.getString(ConstantUtils.NEW_CALL_FLASH_ID, "");
                    }
                }

                break;
            case POSITION_FB_ADS_BLOCKHOME:
                fb_id = ConstantUtils.FB_BLOCK_PAGE_ID;
                int use_spec_id3 = PreferenceHelper.getInt(ConstantUtils.IS_USE_BLOCK_SCAN_HOME, 0);
                if (use_spec_id3 == 1) {
                    fb_id = ConstantUtils.FB_BLOCK_PAGE_ID;
                } else if (use_spec_id3 == 99) {
                    fb_id = PreferenceHelper.getString("new_custom_blockhome_fb_id", "");
                } else {//0
                    int use_id = PreferenceHelper.getInt(ConstantUtils.IS_CALL_FLASH_ID, 0);
                    if (use_id == 0) {
                        fb_id = ConstantUtils.FB_FAKE_CALL_ID;
                    } else if (use_id == 99) {
                        fb_id = PreferenceHelper.getString(ConstantUtils.NEW_CALL_FLASH_ID, "");
                    }
                }

                break;
            case POSITION_FB_ADS_CONTACTLIST:
                fb_id = ConstantUtils.FB_FAKE_CALL_ID;
                if (PreferenceHelper.getInt(ConstantUtils.IS_USE_CONTACT_ID, 1) == 1) {
                    fb_id = ConstantUtils.FB_CONTACT_LIST_ID;
                } else if (PreferenceHelper.getInt(ConstantUtils.IS_USE_CONTACT_ID, 1) == 99) {
                    fb_id = PreferenceHelper.getString("new_custom_contact_fb_id", "");
                } else {//0
                    if (PreferenceHelper.getInt(ConstantUtils.IS_CALL_FLASH_ID, 0) == 99) {
                        fb_id = PreferenceHelper.getString(ConstantUtils.NEW_CALL_FLASH_ID, "");
                    }
                }
                break;
            case POSITION_FB_ADS_CALLGIFCUSTOM:
                fb_id = ConstantUtils.FB_FAKE_CALL_ID;
                int use_spec_id2 = PreferenceHelper.getInt(ConstantUtils.IS_USE_CALL_GIF_LIST, 1);
                if (use_spec_id2 == 1) {
                    fb_id = ConstantUtils.FB_SPAM_LIST_ID;
                } else if (use_spec_id2 == 99) {
                    fb_id = PreferenceHelper.getString("new_custom_call_gif_fb_id", "");
                } else {//0
                    int use_id = PreferenceHelper.getInt(ConstantUtils.IS_CALL_FLASH_ID, 0);
                    if (use_id == 0) {
                        fb_id = ConstantUtils.FB_FAKE_CALL_ID; //合并id
                    } else if (use_id == 99) {
                        fb_id = PreferenceHelper.getString(ConstantUtils.NEW_CALL_FLASH_ID, "");
                    }
                }

                break;
            case POSITION_FB_ADS_SMS_EDIT:  //短信编辑 大图
                fb_id = ConstantUtils.FB_SCAN_RESULT_ID;
                int use_spec_id6 = PreferenceHelper.getInt(ConstantUtils.IS_USE_SMS_EDIT_SEND, 0);
                if (use_spec_id6 == 1) {
                    fb_id = ConstantUtils.FB_SMS_EDIT_ID;
                } else if (use_spec_id6 == 99) {
                    fb_id = PreferenceHelper.getString("new_custom_sms_edit_fb_id", "");
                } else {//0
                    int use_id = PreferenceHelper.getInt(ConstantUtils.IS_USE_SCAN_ID, 0);
                    if (use_id == 99) {
                        fb_id = PreferenceHelper.getString(ConstantUtils.NEW_USE_SCAN_ID, "");
                    }

                }
                break;
            case POSITION_FB_ADS_CALLFLASH:
                fb_id = ConstantUtils.FB_FAKE_CALL_ID;  //合并id
                int use_id = PreferenceHelper.getInt(ConstantUtils.IS_CALL_FLASH_ID, 0);
                if (use_id == 0) {
                    fb_id = ConstantUtils.FB_FAKE_CALL_ID;  //合并id
                } else if (use_id == 1) {
                    fb_id = ConstantUtils.FB_FAKE_CALL_FLASH_ID; //独立id
                } else if (use_id == 99) {
                    fb_id = PreferenceHelper.getString(ConstantUtils.NEW_CALL_FLASH_ID, "");
                }
                break;
            case POSITION_FB_ADS_FAKECALL_HOME:
                fb_id = ConstantUtils.FB_FAKE_CALL_ID;
                int id_fakecall = PreferenceHelper.getInt(ConstantUtils.IS_USE_FAKECALL_HOME, 0);
                if (id_fakecall == 1) {
                    fb_id = ConstantUtils.FB_FAKE_CALL_ID;
                } else if (id_fakecall == 2) {
                    fb_id = ConstantUtils.FB_FAKE_CALL_FLASH_ID;//add on 2018-01-29
                } else if (id_fakecall == 99) { //add on 2018-01-29
                    fb_id = PreferenceHelper.getString("perf_fake_call_home_fb_id", "");
                } else { //0
                    int id_fakecall2 = PreferenceHelper.getInt(ConstantUtils.IS_CALL_FLASH_ID, 0);
                    if (id_fakecall2 == 0) {
                        fb_id = ConstantUtils.FB_FAKE_CALL_ID;
                    } else if (id_fakecall2 == 99) {
                        fb_id = PreferenceHelper.getString(ConstantUtils.NEW_CALL_FLASH_ID, "");
                    }
                }
            default:
                break;
        }

        return fb_id;
    }

    //联系人列表小
    public static String getContactSmallFbId() {
        String fb_id = getCommonFbID(POSITION_FB_ADS_CONTACTLIST);
        return fb_id;
    }

    //联系人列表大
    public static String getContactBigFbId() {
        String fb_id = "198653420649711_335093823672336"; //cid_ad131(大图, 80版本后), cid_ad130(banner), 12/27 update, old is FB_FAKE_CALL_TIME_ID, 虚拟来电时间设置
        int use_id = AdPreferenceHelper.getInt(PREF_IS_CONTACT_BIG_NEW, 0);
        if (use_id == 99) {
            fb_id = AdPreferenceHelper.getString(PREF_NEW_CONTACT_BIG_FB_ID, "");
        }
        return fb_id;
    }

    //more来电秀下载页面
    public static String getCallFlashDownFbId() {
        String fb_id = "198653420649711_241883872993332"; //cid_ad111, 虚拟来电时间设置
        int use_id = AdPreferenceHelper.getInt("pref_call_flash_down_new", 0);
        if (use_id == 99) {
            fb_id = AdPreferenceHelper.getString("pref_call_flash_down_new_id", "");
        }
        return fb_id;
    }

    //swipe fb id
    public static String getSwipeFbId() {
        String fb_id = AdPreferenceHelper.getString("pref_swipe_fb_id", "198653420649711_241883872993332");
        return fb_id;
    }

    //开屏新的来电秀提示
    public static String getScreenOnCallFlash() {
        String fb_id = ""; //
        int use_id = AdPreferenceHelper.getInt("pref_screen_on_call_flash_new", 0);
        if (use_id == 99) {
            fb_id = AdPreferenceHelper.getString("pref_screen_on_call_flash_new_id", "");
        }
        return fb_id;
    }

    //结果页fb插页广告, call flash detail
    public static String getResultInFbId() {
        String fb_id = "198653420649711_372123056636079"; //cid_ad136,  cid_ad124, cid_ad125

        int use_id = AdPreferenceHelper.getInt(PREF_IS_IN_ADS_RESULT_NEW, 0);
        if (use_id == 99) {
            fb_id = AdPreferenceHelper.getString(PREF_NEW_IN_ADS_RESULT_FB_ID, "");
        }

        return fb_id;
    }

    //结果页admob插页广告, call flash detail
    public static String getResultInAdmobId() {
        String ad_id = ConstantUtils.ADMOB_ADV_AFTER_CALL_Interstitial_ID;

        int use_id = AdPreferenceHelper.getInt("pref_in_ads_admob_result_new", 0);
        if (use_id == 99) {
            ad_id = AdPreferenceHelper.getString("pref_in_ads_admob_result_new_id", "");
        }

        return ad_id;
    }

    public static boolean isShowAd(int position) {
        boolean show = false;
        int click = 0;
        switch (position) {
            case POSITION_FB_ADS_CONTACTLIST_BIG:
                click = AdPreferenceHelper.getInt("pref_is_contact_big_ad_show", 0);
                if (click != 0) {
                    show = true;
                }
                break;
            case POSITION_FB_ADS_CALLFLASH_HOT:
                click = AdPreferenceHelper.getInt("pref_is_call_flash_hot_big_ad_show", 0);
                if (click != 0) {
                    show = true;
                }
                break;
        }
        return show;
    }

    //number scan reuslt fb_id
    public static String getNumberScanResultFbId() {
        String fb_id = ConstantUtils.FB_SCAN_RESULT_ID;
        int use_id = PreferenceHelper.getInt(ConstantUtils.IS_USE_SCAN_ID, 0);
        if (use_id == 0) {
            fb_id = ConstantUtils.FB_SCAN_RESULT_ID;
        } else if (use_id == 2) {
            fb_id = ConstantUtils.FB_AFTER_CALL_ID;
        } else if (use_id == 99) {
            fb_id = PreferenceHelper.getString(ConstantUtils.NEW_USE_SCAN_ID, "");
        }
        return fb_id;
    }

    public static boolean isEnableLFProductIntro() {
        boolean is = false;
        int enable = AdPreferenceHelper.getInt(PREF_IS_ENABLE_LF_INTRO, 0);
        if (enable == 0) {
            is = true;
        }
        return is;
    }

    public static boolean isShowSplashCurrent() {

        boolean show = false;

        int sw = AdPreferenceHelper.getInt(IS_SHOW_SPLASH_CURRENT_SWITCH, 0); //0 not show
        if (sw == 1) {
            long show_guide = PreferenceHelper.getLong(ConstantUtils.PREF_KEY_SHOW_GUIDE_TIME, 0);
            if (show_guide == 0 || Stringutil.isTodayNew(show_guide)) {
                return show;
            }

            long show_interval = AdPreferenceHelper.getLong("pref_last_show_back_splash_interval", 2 * 60 * 60 * 1000);//默认间隔2小时
            long last_show = AdPreferenceHelper.getLong("last_show_back_splash_time", 0);

            if (last_show <= 0 || System.currentTimeMillis() - last_show >= show_interval) {


                String countires = AdPreferenceHelper.getString(IS_SHOW_SPLASH_CURRENT_COUNTRIES, "");
                if (!TextUtils.isEmpty(countires) && (countires.equals(PARAM_ALL_COUNTRIES) || getCommonShowAdsCountries(IS_SHOW_SPLASH_CURRENT_COUNTRIES).contains(ApplicationEx.getInstance().country))) {

                    show = true;
                    AdPreferenceHelper.putLong("last_show_back_splash_time", System.currentTimeMillis());
                }
            }
        }

        return show;
    }

    //从contact 入口返回时显示splash的国家
    private static HashSet<String> getCommonShowAdsCountries(String key) {
        String countries = AdPreferenceHelper.getString(key, "");
        HashSet<String> sets = getControlSet(countries);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "getBackShowSplashAdsCountries countries or or clickable channels: " + cc);
            }
        }
        return sets;
    }

    //短信秀设置, 小
    public static String getSMSFlashSetFbId() {
        String fb_id = "198653420649711_232914513890268"; // 来电秀， facke call 首页
        int use_id = AdPreferenceHelper.getInt("pref_is_sms_flash_set_new", 0);
        if (use_id == 99) {
            fb_id = AdPreferenceHelper.getString("pref_sms_flash_set_new_id", ""); //198653420649711_248674818980904,FB_FAKE_CALL_FLASH_ID //虚拟来电秀设置
        }
        return fb_id;
    }

    //短信秀收到, 大
    public static String getSMSFlashShowFbId() {
        String fb_id = "198653420649711_257498651431854"; //扫描结果页广告, smscomeactivity // 还可以用 call after, 还可以用ConstantUtils.FB_FAKE_CALL_FLASH_ID;
        int use_id = AdPreferenceHelper.getInt("pref_is_sms_flash_show_new", 0);
        if (use_id == 99) {
            fb_id = AdPreferenceHelper.getString("pref_sms_flash_show_new_id", "");
        }
        return fb_id;
    }

    //短信秀设置结果页, 大
    public static String getSMSFlashSetResultFbId() {
        String fb_id = "198653420649711_203519680163085"; //call after, 还可以用ConstantUtils.FB_FAKE_CALL_FLASH_ID;
        int use_id = AdPreferenceHelper.getInt("pref_is_sms_flash_set_result_new", 0);
        if (use_id == 99) {
            fb_id = AdPreferenceHelper.getString("pref_sms_flash_set_result_new_id", "");
        }
        return fb_id;
    }

    //来电秀, 短信秀 - 跳动的按钮是否可以点击，默认可以点击
    public static boolean isFlashBtClickable() {
        int click = AdPreferenceHelper.getInt("pref_is_flash_btn_clickable", 0);
        if (click == 0) {
            return true;
        } else {
            return false;
        }
    }

    //联系人大头像ad img背景默认不可点
    public static boolean isAdImgBgClickable() {
        int click = AdPreferenceHelper.getInt("pref_is_ad_img_background_clickable", 0);
        if (click == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * fb大图广告, 分渠道控制 空白区域， img背景可点
     *
     * @param condition, 1 空白区域 默认不可点(false), 2 img背景 默认不可点(false), 3 只有按钮可点 默认(false)
     * @return
     */
    public static boolean isClickableByChannel(int condition) {
        boolean is = false;
        String channel = PreferenceHelper.getString("channel", "");
        HashSet<String> ch = null;
        switch (condition) {
            case 1:
                ch = getCommonShowAdsCountries("pref_channel_fb_big_whitespaces");
                if (ch != null && ch.size() > 0 && ch.contains(channel)) {
                    is = true;
                }
                break;
            case 2:
                ch = getCommonShowAdsCountries("pref_channel_fb_big_image_background");
                if (ch != null && ch.size() > 0 && ch.contains(channel)) {
                    is = true;
                }
                break;
            case 3:
                ch = getCommonShowAdsCountries("pref_channel_fb_big_only_btn");
                if (ch != null && ch.size() > 0 && ch.contains(channel)) {
                    is = true;
                }
                break;
        }
        LogUtil.d("isClickableByChannel", "channel: " + channel + ", is: " + is);
        return is;
    }

    //fb大图广告 启动页 结果页空白区域可点, 默认否
    public static boolean isWhiteClickable(int position) {
        boolean is = false;
        int click = 0;
        switch (position) {
            case POSITION_FB_ADS_SCAN_RESULT_BIG:
                click = AdPreferenceHelper.getInt("pref_is_scan_result_white_clickable", 0);
                if (click != 0) {
                    is = true;
                }
                break;
            case POSITION_FB_ADS_SPLASH_BIG:
                click = AdPreferenceHelper.getInt("pref_is_splash_white_clickable", 0);
                if (click != 0) {
                    is = true;
                }
                break;
        }
        //percent control
        if (is) {
            int current_percent = getRandomNums(1, 100);
            int server_percent = getWhitespacesClickPercent(position);
            LogUtil.d("click_control", "isWhiteClickable position: " + position + ", is: " + is + ", c_p: " + current_percent + ", s_p: " + server_percent);
            if (current_percent > 0 && current_percent <= server_percent) {
                is = true;
            } else {
                is = false;
            }

        }

        return is;
    }

    private static int getWhitespacesClickPercent(int position) {
        int click = 100; //100 means all is spaces Clickable
        switch (position) {
            case POSITION_FB_ADS_SCAN_RESULT_BIG:
                click = AdPreferenceHelper.getInt("pref_is_scan_result_white_percent", 100);

                break;
            case POSITION_FB_ADS_SPLASH_BIG:
                click = AdPreferenceHelper.getInt("pref_is_splash_white_percent", 100);

                break;
        }
        return click;
    }

    public static boolean isUseSplashNewStyle() {
        boolean high = false;
        if (ApplicationEx.getInstance() != null) {
            int click = AdPreferenceHelper.getInt("pref_is_use_splash_new_style", 0);
            if (click == 1) {
                high = true;
            }
        }
        return high;
    }

    //fb大图广告 and call flash set, sms flash show set, 只有行动按钮可点, 默认否
    public static boolean isOnlyBtnClickable(int position) {
        boolean is = false;
        int click = 0;
        switch (position) {
            case POSITION_FB_ADS_ENDCALL_BIG:
                click = AdPreferenceHelper.getInt("pref_is_endcall_only_btn_clickable", 0);
                if (click != 0) {
                    is = true;
                }
                break;
            case POSITION_FB_ADS_SCAN_RESULT_BIG:
                click = AdPreferenceHelper.getInt("pref_is_scan_result_only_btn_clickable", 0);
                if (click != 0) {
                    is = true;
                }
                break;
            case POSITION_FB_ADS_SPLASH_BIG:
                click = AdPreferenceHelper.getInt("pref_is_splash_only_btn_clickable", 0);
                if (click != 0) {
                    is = true;
                }
                break;
            case POSITION_FB_ADS_CALLFLASH:
                click = AdPreferenceHelper.getInt("pref_is_call_flash_set_only_btn_clickable", 1);
                if (click != 0) {
                    is = true;
                }
                break;
            case POSITION_FB_ADS_SMS_FLASH_SHOW_SET:
                click = AdPreferenceHelper.getInt("pref_is_sms_flash_set_only_btn_clickable", 0);
                if (click != 0) {
                    is = true;
                }
                break;
        }
        //channel control
//        if(is){
//            if(isClickableByChannel(3)){
//                is = true;
//            }else{
//                is = false;
//            }
//        }
        //percent control
        if (is) {
            int current_percent = getRandomNums(1, 100);
            int server_percent = getOnlyBtnPercent(position);
            LogUtil.d("click_control", "isOnlyBtnClickable position: " + position + ", is: " + is + ", c_p: " + current_percent + ", s_p: " + server_percent);
            if (current_percent > 0 && current_percent <= server_percent) {
                is = true;
            } else {
                is = false;
            }

        }

        return is;
    }

    private static int getOnlyBtnPercent(int position) {
        int click = 100; //100 means all is only btn Clickable
        switch (position) {
            case POSITION_FB_ADS_ENDCALL_BIG:
                click = AdPreferenceHelper.getInt("pref_is_endcall_only_btn_percent", 100);

                break;
            case POSITION_FB_ADS_SCAN_RESULT_BIG:
                click = AdPreferenceHelper.getInt("pref_is_scan_result_only_btn_percent", 100);

                break;
            case POSITION_FB_ADS_SPLASH_BIG:
                click = AdPreferenceHelper.getInt("pref_is_splash_only_btn_percent", 100);

                break;
            case POSITION_FB_ADS_CALLFLASH:
                click = AdPreferenceHelper.getInt("pref_is_call_flash_set_only_btn_percent", 100);

                break;
            case POSITION_FB_ADS_SMS_FLASH_SHOW_SET:
                click = AdPreferenceHelper.getInt("pref_is_sms_flash_set_only_btn_percent", 100);
                break;
        }
        return click;
    }


    //fb大图广告, ad img作为背景时 , img 可点为false(不可点)， 只有文字和按钮可点, 默认true, 目前只有两个位置, 短信秀， 短信编辑
    public static boolean isImgBgClickable(int position) { //isOnlyBtnAndTextClickable
        boolean is = false;
        int click = 0;
        switch (position) {
            case POSITION_FB_ADS_SMS_EDIT:
                click = AdPreferenceHelper.getInt("pref_is_smsedit_img_bg_clickable", 0);
                if (click != 0) {
                    is = true;
                }
                break;
            case POSITION_FB_ADS_SMS_FLASH_SHOW:
                click = AdPreferenceHelper.getInt("pref_is_smsshow_img_bg_clickable", 0);
                if (click != 0) {
                    is = true;
                }
                break;
        }
        //channel control
        if (is) {//img 背景可点击
            if (isClickableByChannel(2)) {
                is = true;
            } else {
                is = false;
            }
        }
        return is;
    }

    //fb 自动刷新, 默认 0 不自动刷新
    public static boolean isAutoRefresh(int position, String key) {
        boolean is = false;
        int click = 0;
        switch (position) {
            case POSITION_FB_ADS_CALLFLASH:
                click = AdPreferenceHelper.getInt("pref_is_call_flash_auto_refresh", 0);
                if (click != 0) {
                    is = true;
                }
                break;
            case POSITION_FB_ADS_SCAN_RESULT_BIG:
                click = AdPreferenceHelper.getInt("pref_is_sc_result_auto_refresh", 0);
                if (click != 0) {
                    is = true;
                }
                break;
        }
        if (is) {
            if (isAdmob(key)) {
                is = false;
            }
        }
        return is;
    }

    //fb 广告自动刷新间隔时间， 秒， second
    public static int getAutoRefreshInterval(int position) {
        int sec = 15; //15 is default
        switch (position) {
            case POSITION_FB_ADS_CALLFLASH:
                sec = AdPreferenceHelper.getInt("interval_call_flash_auto_refresh", 15);
                break;
            case POSITION_FB_ADS_SCAN_RESULT_BIG:
                sec = AdPreferenceHelper.getInt("interval_sc_result_auto_refresh", 15);
                break;
        }
        return sec;

    }

    //fb 广告自动刷新最大次数，
    public static int getAutoRefreshMax(int position) {
        int count = 2;
        switch (position) {
            case POSITION_FB_ADS_CALLFLASH:
                count = AdPreferenceHelper.getInt("count_call_flash_auto_refresh", 2);
                break;
            case POSITION_FB_ADS_SCAN_RESULT_BIG:
                count = AdPreferenceHelper.getInt("count_sc_result_auto_refresh", 2);
                break;
        }
        return count;

    }

    public static String getAdIDByPostion(String position_key) {
        //random_adid - {“0,50":"ID_2", "50,100":"ID_3"}
        LogUtil.d("random_adid", "getAdIDByPostion:" + position_key);
        String mopub_banner_id = "";
        if (AdPreferenceHelper.getInt("pref_cid_random_adid_enable", 0) != 0) {
            return mopub_banner_id;
        }
        String random_adid = AdPreferenceHelper.getString(KEY_SERVER_RANDOM_ADID_BASE + position_key, null);

        if (!TextUtils.isEmpty(random_adid)) {
            LogUtil.d("random_adid", "getAdIDByPostion random_adid:" + random_adid);
            try {
                int number = new Random().nextInt(100);
                JSONObject obj = new JSONObject(random_adid);
                Iterator<String> keys = obj.keys();
                while (keys.hasNext()) {
                    String key = String.valueOf(keys.next());
                    int separatorIndex = key.indexOf(",");
                    int lowLimit = Integer.parseInt(key.substring(0, separatorIndex));
                    int upLimit = Integer.parseInt(key.substring(separatorIndex + 1, key.length()));
                    if (lowLimit <= number && number < upLimit) {
                        mopub_banner_id = obj.getString(key);
                        LogUtil.d("random_adid", "getAdIDByPostion mopub_banner_id:" + mopub_banner_id);
                        break;
                    }
                }
            } catch (Exception e) {
                LogUtil.e("random_adid", "getAdIDByPostion exception:" + e.getMessage());
            }
        }
        return mopub_banner_id;
    }

    /**
     * Get Random number whose range from min to max
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandomNums(int min, int max) {
        int i = -1;
        try {
            Random random = new Random();

            i = random.nextInt((max - min) + 1) + min;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public static boolean isShowSplashFromOthers() {
        boolean is = true;

        int show = AdPreferenceHelper.getInt("pref_is_show_splash_from_others", 0);
        if (show != 0) {
            is = false;
        }

        return is;
    }

    public static boolean isShowCallRecordOnHome() {
        boolean is = true;

        int show = AdPreferenceHelper.getInt("pref_is_show_record_on_home", 0);
        if (show != 0) {
            is = false;
        }

        return is;
    }

    /**
     * @param type, 1 - first load time,default is 6.5s; 2- ad load time, 4.5s; 3 - ad show time, default is 4s
     * @return
     */
    public static long getSplasAdTime(int type) {
        long adTime = 4000; // default 4s
        switch (type) {
            case 1:
                adTime = AdPreferenceHelper.getLong("pref_splash_ad_first_load", 6500);
                break;
            case 2:
                adTime = AdPreferenceHelper.getLong("pref_splash_ad_load", 4500);
                break;
            case 3:
                adTime = AdPreferenceHelper.getLong("pref_splash_ad_show", 4000);
                break;
        }
        return adTime;
    }

    /**
     * 来电秀接听后，接听状态返回时间内认定成功，默认1秒
     *
     * @return
     */
    public static long getAnswerCallSuccBench() {
        long bench = 1000;
        bench = AdPreferenceHelper.getLong("pref_answer_call_succ_bench", 1000);
        return bench;
    }

    public class InterstitialAdPriority {
        public static final int HIGH = 0;
        public static final int MEDIUM = 1;
        public static final int NORMAL = 2;
    }

    public static boolean isUseGroupBanner() {
        boolean group = true;
        int is = AdPreferenceHelper.getInt("pref_is_use_group_banner", 1);
        if (is == 0) {
            group = false;
        }
        return group;
    }

    public static boolean isUseGroupBannerNew() {
        boolean group = true;
        int is = AdPreferenceHelper.getInt("pref_is_use_group_banner_new", 1);
        if (is == 0) {
            group = false;
        }
        return group;
    }

    public static boolean isUseGroupAds() {
        boolean group = true;
        int is = AdPreferenceHelper.getInt("pref_is_use_group_ads", 1);
        if (is == 0) {
            group = false;
        }
        return group;
    }

    //开屏时弹出新来电秀提示
    public static boolean isShowNewFlashTips() {
        boolean show = false; //默认不显示, 1 不显示， 0 显示
        int is = AdPreferenceHelper.getInt("pref_is_show_new_flash_tips", 1);//1 不显示， 0 显示
        if (is == 0) {
            show = true;
        }
        return show;
    }

    public static boolean isAutoGoMain() {
        boolean autoGoMain = true; //默认自动跳转到main
        int is = AdPreferenceHelper.getInt("pref_is_auto_go_main", 0);//欧美发达国家，不自动跳过is=1
        if (is == 1) {
            autoGoMain = false;
        }
        return autoGoMain;
    }

    //第一次号码扫描结果页, 只对渠道量开启插屏
    public static boolean isShowInAdOnFirstScan() {
        boolean show = false;
        int is = AdPreferenceHelper.getInt("pref_is_show_in_ad_first_scan", 0);//0 not show, 1 show
        if (is == 1) {
            show = true;
        }
        return show;
    }

    public static int getDaysStartNightAfterInstall() {
        int days = 0;
        //安装几天以后夜间模式自动开启， 0 不开启
        if (ApplicationEx.getInstance() != null) {
            days = AdPreferenceHelper.getInt("pref_days_start_night_after_install", 0);//0 not enable, default is 3
        }
        return days;
    }

    //new missed call
    public static String getMissedCallFbId() {
        String fb_id = AdPreferenceHelper.getString("pref_missed_call_fb_id", "198653420649711_203519680163085");//default call after
        return fb_id;
    }

    public static boolean isShowFirstAdMob(int position_first, boolean isShowInOldUser) {
        if (ApplicationEx.getInstance() == null) {
            return false;
        }

        if (!isShowInOldUser && AppUtils.isUpdateUser()) {
            return false;
        }

        String admobId = getAdmobIdForFirst(position_first);
        if (TextUtils.isEmpty(admobId)) {
            return false;
        }

        //该位置上一次显示的时间
        HashMap<String, Long> data = PreferenceHelper.getHashMapData(PreferenceHelper.PREF_LAST_SHOW_FRIST_ADMOB_TIME_MAP, Long.class);
        long lastShowTime = data.get(String.valueOf(position_first)) == null ? 0 : data.get(String.valueOf(position_first));
        if (lastShowTime > 0) {
            return false;
        }
        return true;

    }

    public final static int POSITION_FIRST_ADMOB_SPLASH = 1;//启动页
    public final static int POSITION_FIRST_ADMOB_SMSFLASH = 2;//短信秀设置
    public final static int POSITION_FIRST_ADMOB_RESULT_SCAN_AND_UPDATE = 3;//号码扫描和spam 更新结果页
    public final static int POSITION_FIRST_ADMOB_RESULT_FLASH_SET = 4;//来电秀和短信秀设置结果页
    public final static int POSITION_FIRST_ADMOB_FULL_SCREEN_RESULT_BACK = 5;//号码扫描结果页返回全屏广告
    public final static int POSITION_FIRST_ADMOB_CALL_FLASH_PREVIEW = 6;//call flash preview 界面
    public final static int POSITION_FIRST_ADMOB_CALL_FLASH_DETAIL = 7;//call flash detail 界面
    public final static int POSITION_FIRST_ADMOB_FULL_SCREEN_CALL_FLASH_DETAIL = 8;//call flash detail 界面全屏广告


    public static String getAdmobIdForFirst(int position_first) {
        String admob_id = "";
        switch (position_first) {
            case POSITION_FIRST_ADMOB_SPLASH:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_splash_admob_id", ConstantUtils.ADMOB_ADV_FAKE_RESULT_ID);//ConstantUtils.ADMOB_ADV_FAKE_RESULT_ID
                break;
            case POSITION_FIRST_ADMOB_SMSFLASH:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_smsflash_admob_id", ConstantUtils.ADMOB_ADV_SMS_LIST_ID);//ConstantUtils.ADMOB_ADV_SMS_LIST_ID
                break;
            case POSITION_FIRST_ADMOB_RESULT_SCAN_AND_UPDATE:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_result_scan_admob_id", ConstantUtils.ADMOB_ADV_BLOCK_SCAN_ID);//ConstantUtils.ADMOB_ADV_BLOCK_SCAN_ID
                break;
            case POSITION_FIRST_ADMOB_RESULT_FLASH_SET:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_result_flashset_admob_id", ConstantUtils.ADMOB_ADV_SMS_EDIT_ID);//ConstantUtils.ADMOB_ADV_SMS_EDIT_ID
                break;
            case POSITION_FIRST_ADMOB_FULL_SCREEN_RESULT_BACK:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_result_onback_admob_id", "");//结果页插屏
                break;
            case POSITION_FIRST_ADMOB_CALL_FLASH_PREVIEW:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_callflash_preview_admob_id", ConstantUtils.ADMOB_ADV_CHARGE_LOCK_ID);//来电秀预览preview
                break;
            case POSITION_FIRST_ADMOB_CALL_FLASH_DETAIL:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_callflash_download_admob_id", ConstantUtils.ADMOB_ADV_CALL_LOG_NATIVE_ID);//来电秀下载detail
                break;
            case POSITION_FIRST_ADMOB_FULL_SCREEN_CALL_FLASH_DETAIL:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_flash_down_onback_admob_id", "");//call flash detail 界面全屏广告. 自定义插屏
                break;
        }
        return admob_id;
    }

    public static boolean isUseMagicButton() {
        boolean use = false;
        int i = AdPreferenceHelper.getInt("pref_is_use_magic_button", 0);
        if (i == 1) {
            use = true;
        }
        return use;
    }
}

