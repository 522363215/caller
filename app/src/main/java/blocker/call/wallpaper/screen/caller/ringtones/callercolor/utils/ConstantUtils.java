package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.content.Context;
import android.text.TextUtils;


import java.util.HashMap;
import java.util.HashSet;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;

/**
 * Created by cattom on 11/25/2015.
 */
public class ConstantUtils {
    public final static String NM_TAG = "lmcaller";
    public final static String PREF_FILE = "com_coolcaller_pref";
    public final static String AD_PREF_FILE = "ad_com_coolcaller_pref";
    public final static boolean TEST_ADMOB = false;

    public static final int NOTIFICATION_KEEP_ALIVE_ID = 100;

    // email
//    public final static String DEVELOPER_EMAIL = "Dev.David.Bi@gmail.com";
//    public final static String EMAIL_TITLE = "Caller Rating Suggestion";


    //caller show
    public static final String ONLINE_THEME_URL = "http://callershow.lionmobi.com/api.php";


    //share_url
    public final static String URL_SHARE_APP = "https://goo.gl/3M5A2k"; //https://goo.gl/, https://play.google.com/store/apps/details?id=com.hiblock.caller

    //about
    public final static String URL_PRIVACY = "http://caller.lionmobi.com/privacy.html";
    public final static String URL_FACEBOOK_PAGE = "https://www.facebook.com/";

    //show guide time
    public static final String PREF_KEY_SHOW_GUIDE_TIME = "cc_app_show_guide_time";//新手引导显示时间
    //install time
    public static final String PREF_KEY_INSTALL_TIME = "cc_app_install_time";//安装时间
    //visit caller time
    public static final String PREF_KEY_ENTER_TIME = "cc_app_enter_time";//上次进入app时间
    //visit count
    public static final String PREF_KEY_VISIT_COUNT = "cc_app_visit_count";//用户进入app的次数
    // is visit on desktop
    public static final String PREF_KEY_IS_VISIT_ON_DESKTOP = "cc_app_is_visit_on_desktop"; // 程序是否後台運行;
    //contact shortcut time
    public static final String PREF_KEY_CONTACT_SHORTCUT_TIME = "cc_app_contact_shortcut_time";
    //    public static final String PREF_KEY_CONTACT_SHORTCUT_TIME_NEW = "cc_app_contact_shortcut_time_new";
    //if show whats new time
    public static final String PREF_KEY_SHOW_WHAT_NEW_TIME = "cc_app_show_what_new_time";
    //daydream status
    public static final String PREF_KEY_DAY_DREAM_STATUS = "cc_day_dream_status";
    // total memory size
    public static final String PREF_KEY_SYSTEM_TOTAL_MEMORY_SIZE = "system_total_memory_size";

    //control param key
    public static final String PREF_KEY_REFRESH_TAG_INTERVAL = "refresh_tag_interval";
    public static final String PREF_KEY_UPDATE_SPAM_LOCAL_INTERVAL = "update_spam_local_interval";
    public static final String PREF_KEY_REFRESH_TAG_NO_TAG_INTERVAL = "refresh_tag_no_tag_interval";
    public static final String PREF_KEY_BENCH_SPAM_TAG_COUNT = "bench_spam_tag_count"; //block
    public static final String PREF_KEY_TOP_SPAM_TAG_COUNT = "pref_top_spam_count_from_server"; //top spam
    public static final String PREF_KEY_BENCH_SHOW_SPAM_TAG_COUNT = "bench_show_spam_tag_count"; //show but not block
    public static final String PREF_KEY_URL_SPAM_THIRD = "url_spam_third";
    public static final String PREF_KEY_URL_SEARCH_CC = "url_search_cc";
    public static final String PREF_KEY_SWITCH_SAPM_SERVER = "switch_sapm_server";
    public static final String PREF_KEY_SWITCH_USE_SAPM_THIRD = "switch_use_sapm_third";

    public static final String PREF_KEY_SETTING_OLD_USER_UPDATED = "setting_old_user_updated";

    //update parm time key
    public static final String PREF_KEY_UPDATE_PARAM_TIME = "update_server_param_time";

    //老用户收到新的电话闪屏通知, 默认关闭
    public static final String PREF_SWITCH_FLASH_CALL_NOTIFY = "switch_flash_call_notify";
    //电话后全屏, 默认开启
    public static final String PREF_SWITCH_FULL_AFTER_CALL = "switch_full_after_call";
    //电话后全屏, 关闭后， 预埋打开天数, 默认0， 不打开
    public static final String PREF_SWITCH_FULL_AFTER_CALL_DELAYS = "switch_full_after_call_delays";

    //tel number count
    public static final String PREF_KEY_TEL_NUMBER_COUNT = "tel_number_count";//从服务端获取的号码库总数
    public static final String PREF_KEY_TEL_NUMBER_COUNT_OLD = "tel_number_count_old";//从服务端获取的号码库总数
    public static final String PREF_KEY_TEL_NUMBER_COUNT_UPDATE_TIME = "tel_number_count_update_time";
    public static final int PREF_KEY_REQ_TOP_SPAM_COUNT = PreferenceHelper.getInt(PREF_KEY_TOP_SPAM_TAG_COUNT, 200); //默认从服务端获取的topspam 条数, 默认200
    public static final int REQ_TEL_NUMBER_COUNT_BASE = 2188532;

    //ads display control key
    public static final String PREF_KEY_NOT_SHOW_ADS_SPLASH = "not_show_ads_splash_list";//不显示启动页广告的国家列表
    public static final String PREF_KEY_NOT_SHOW_INTERSTITIAL = "not_show_interstitial"; //不显示插屏广告的国家列表
    public static final String PREF_KEY_IS_SHOW_INTERSTITIAL = "is_show_interstitial"; //是否显示插屏, 总开关
    public static final String PREF_KEY_SHOW_INTERSTITIAL_POSITION = "show_interstitial_position"; //显示插屏的位置开关列表

    public static final String PREF_KEY_NOT_SHOW_ADS_SPLASH_SET = "not_show_ads_splash_list_set";//不显示启动页广告的国家列表set
    public static final String PREF_KEY_NOT_SHOW_INTERSTITIAL_SET = "not_show_ads_splash_list_set";//不显示插屏广告的国家列表set
    public static final String PREF_KEY_SHOW_INTERSTITIAL_POSITION_SET = "not_show_ads_splash_list_set";//显示插屏的位置开关列表set

    // 是否将老版本monkey铃声重置;
    public static final String PREF_KEY_IS_RESET_MONKEY_FLASH = "is_reset_monkey_flash";

    //插屏位置
    public static final String P_INTERSTITIAL_MAIN = "p_in_main"; //应用退出插屏. //not_show_interstitial": "US,CA,GB,JP,DK,FI,NL,AT,BE,NO,CH
    public static final String P_INTERSTITIAL_CALLLOG = "p_in_calllog"; //通话记录详情页退出插屏
    public static final String P_INTERSTITIAL_ENDCALL = "p_in_endcall"; //电话详情页退出插屏
    public static final String P_INTERSTITIAL_SMSCOMING = "p_in_smscoming"; //短信详情页退出插屏


    //fb 大图广告位置, 控制大图空白区域可点击
    public static final String P_BIG_SPLASH = "p_big_splash"; //启动页
    public static final String P_BIG_END_CALL = "p_big_end_call"; //电话结束页
    public static final String P_BIG_SCAN_RESULT = "p_big_scan_result"; //扫描结果页
    public static final String P_BIG_SMS_CLEAN = "p_big_sms_clean"; //短信清理页
    public static final String P_BIG_FLASH_SET = "p_big_flash_set"; //来电秀设置结果页
    public static final String FB_BIG_WHITE_SPACES_CLICKABLE = "fb_big_white_spaces_clickable"; //fb 大图广告空白区域是否可以点击, 默认0 不可点击
    public static final String PREF_KEY_BIG_FB_CLICKABLE_POSITION = "big_fb_clickable_position"; //可点击空白区域的fb大图广告位置列表
    public static final String PREF_KEY_BIG_FB_CLICKABLE_COUNTRIES = "big_fb_clickable_countries"; //可点击空白区域的fb大图广告国家列表

    public static final String DIALING_NUMBER = "dialing_number";
    public static final String MESSAGE_RECIPIENS = "message_recipients";
    public static final String MESSAGE_THREADID = "message_threadid";
    public static final String MESSAGE_CONVERSATION = "message_conversation";
    public static final String MESSAGE_NUMBER = "message_number";

    //caller server param
    public static long REFRESH_TAG_INTERVAL = PreferenceHelper.getLong(PREF_KEY_REFRESH_TAG_INTERVAL, 24 * 60 * 60 * 1000); //1 day default
    public static long UPDATE_SPAM_LOCAL_INTERVAL = PreferenceHelper.getLong(PREF_KEY_UPDATE_SPAM_LOCAL_INTERVAL, 24 * 60 * 60 * 1000); //1 day default, update spam db interval
    public static long REFRESH_TAG_NO_TAG_INTERVAL = PreferenceHelper.getLong(PREF_KEY_REFRESH_TAG_NO_TAG_INTERVAL, 2 * 60 * 60 * 1000); //2 hours default, no tag or name yet
    public static int BENCH_SPAM_TAG_COUNT = PreferenceHelper.getInt(PREF_KEY_BENCH_SPAM_TAG_COUNT, 40); //getTagcount 40
    public static int BENCH_SHOW_SPAM_TAG_COUNT = PreferenceHelper.getInt(PREF_KEY_BENCH_SHOW_SPAM_TAG_COUNT, 20); //getTagcount 40

    public static int SWITCH_FLASH_CALL_NOTIFY = PreferenceHelper.getInt(PREF_SWITCH_FLASH_CALL_NOTIFY, 0); //default 0, off
    public static int SWITCH_FULL_AFTER_CALL = PreferenceHelper.getInt(PREF_SWITCH_FULL_AFTER_CALL, 0); //default 0, on
    public static int SWITCH_FULL_AFTER_CALL_DELAYS = PreferenceHelper.getInt(PREF_SWITCH_FULL_AFTER_CALL_DELAYS, 0); //default 0, off

    //    public static String URL_SPAM_THIRD = ""; //test
    public static String URL_SPAM_THIRD = PreferenceHelper.getString(PREF_KEY_URL_SPAM_THIRD, ""); //https://www.show-caller.com/，https://www.shouldianswer.com/phone-number/
    public static String URL_SEARCH_CC = PreferenceHelper.getString(PREF_KEY_URL_SEARCH_CC, ""); //used by search page
    public final static String SERVER_API_ANALYSIS_INTERFACE = "http://analysis.lionmobi.com/api.php";
    public final static String SERVER_API_UPLOAD = "http://caller.lionmobi.com/api.php";
    public final static String SERVER_API_CALLER_SHOW = "http://caller_show.lionmobi.com/api.php";
    public final static String SERVER_API_PARAM = "http://parameter.lionmobi.com/api.php";
    public final static int CALLER_STATISTICS_CHANNEL = 33; //
    public final static String NOTIFICATION_API_INTERFACE = "http://notification.lionmobi.com/viewSelected/portal/api.php";
    public final static String NOTIFICATION_ACTION = "notification_static";
    public final static String SERVER_API_CALLER_SHOW_LIKE = "http://callershow.lionmobi.com/api.php";
    public final static String KEY_HTTP = "*2od2S!#";

    public final static String SERVER_API_PARAM_CPM = "http://param.lionmobi.com";
    public final static String SERVER_API_BASE_CPM = "http://info.lionmobi.com";

    //ads control from server

    //

    //caller fb ads
    public static final String FB_IN_NUM_RESULT_ID = "198653420649711_290516238130095"; //号码扫描结果页插屏cid_123. 作废
    public static final String FB_CONTACT_FRG_AD_ID = "198653420649711_276126146235771"; //contact fragment new
    public static final String FB_FLASH_CALL_SETTING_ID = "198653420649711_198688860646167";  //来电秀设置页
    public static final String FB_GIFT_ID = "198653420649711_198698210645232";   //首页礼物
    public static final String FB_UPDATE_SPAM_RESULT_ID = "198653420649711_198699110645142"; //号码骚扰库升级结果页
    public static final String FB_SEARCH_PAGE_ID = "198653420649711_198693050645748";    //号码搜索页
    public static final String FB_BLOCK_PAGE_ID = "198653420649711_198697063978680";    //号码拦截页
    public static final String FB_BLOCK_SETTING_ID = "198653420649711_201296660385387";     //号码拦截设置页
    public static final String FB_PHONE_DETAIL_ID = "198653420649711_198697537311966";  //通话记录详情页
    public static final String FB_BLOCK_LOGS_ID = "198653420649711_198699683978418";    //拦截历史页
    public static final String FB_AFTER_CALL_ID = "198653420649711_203519680163085";  //号码详情页, 通话后详情
    public static final String FB_SMART_LOCK_ID = "198653420649711_278560192659033";  //锁屏界面
    public static final String FB_SPAM_LIST_ID = "198653420649711_198700457311674";     //cid_ad122, 骚扰号码列表页 - call flash gif list
    public static final String FB_CALL_LOG_ID = "198653420649711_205648766616843";  //通话记录列表首页
    public static final String FB_SMS_DISPLAY_ID = "198653420649711_208851786296541"; //短信接收后显示, 短信详情页 cid_ad104 已暂停-被封
    public static final String FB_SPLASH_ID = "198653420649711_218023398712713"; //启动页广告
    public static final String FB_SMS_LIST_ID = "198653420649711_221369771711409"; //短信列表页广告
    public static final String FB_SMS_EDIT_ID = "198653420649711_221370051711381"; //短信编辑页广告
    public static final String FB_FAKE_CALL_ID = "198653420649711_232914513890268"; //虚拟来电首页, 新来电秀设置页, 短信列表， 号码拦截页,calllog 首页 fakecall 首页
    public static final String FB_FAKE_CALL_RESULT_ID = "198653420649711_232914683890251"; //虚拟来电结果页
    public static final String FB_CONTACT_LIST_ID = "198653420649711_241883616326691"; //联系人列表, cid_ad110
    public static final String FB_FAKE_CALL_TIME_ID = "198653420649711_241883872993332"; //虚拟来电时间设置
    public static final String FB_FAKE_CALL_FLASH_ID = "198653420649711_248674818980904";//虚拟来电秀设置, 用于独立的来电秀设置(暂时没有使用), 短信秀设置
    public static final String FB_SCAN_RESULT_ID = "198653420649711_257498651431854"; //扫描结果页广告
    public static final String FB_SMS_CLEAN_RESULT_ID = "198653420649711_278250716023314"; //短信清理结果页, cid_ad116, cid_ad118
    public static final String FB_SPAM_UPDATE_RESULT_ID = "198653420649711_257498761431843"; // 号码库升级结果页
    public static final String FB_CALL_FLASH_SET_ID = "198653420649711_278636665984719"; // 来电秀设置结果页, ad120
    //fb low price, disable on 06/09
    public static final String FB_AFTER_CALL_LP_ID = "198653420649711_216427002205686";  //号码详情页, 通话后详情
    public static final String FB_FLASH_CALL_SETTING_LP_ID = "198653420649711_216426362205750";  //来电秀设置页
    public static final String FB_CALL_LOG_LP_ID = "198653420649711_216426595539060";  //通话记录列表首页，

    //caller admob quick
//    public static final String ADMOB_AFTER_CALL_NATIVE_ID = "ca-app-pub-5980661201422605/4085149176"; //号码详情页, 通话后详情
//    public static final String ADMOB_FLASH_CALL_SETTING_ID = "ca-app-pub-5980661201422605/3716719171"; //来电秀设置页
//    public static final String ADMOB_SMS_DISPLAY_NATIVE_ID = "ca-app-pub-5980661201422605/1516472375"; //短信详情页
//    public static final String ADMOB_STARTUP_NATIVE_ID = "ca-app-pub-5980661201422605/7912489176"; //启动页快速
//    public static final String ADMOB_FAKE_HOME_ID = "ca-app-pub-5980661201422605/4512233978"; //虚拟电话首页快速原生
//    public static final String ADMOB_FAKE_RESULT_ID = "ca-app-pub-5980661201422605/5988967173"; //虚拟电话结果页快速原生
//    public static final String ADMOB_CALL_LOG_NATIVE_ID = "ca-app-pub-5980661201422605/9690273578"; //通话记录列表快速， Size range:Width: 280dp—1200dp Height: 80dp—612dp
//    public static final String ADMOB_SCAN_RESULT_ID = "ca-app-pub-5980661201422605/5228040836"; //扫描结果页广告
//    public static final String ADMOB_SPAM_UPDATE_RESULT_ID = "ca-app-pub-5980661201422605/3248922366"; // 号码库升级结果页
//    public static final String ADMOB_CLEAN_RESULT_ID = "ca-app-pub-5980661201422605/9873862851"; // 号码库升级结果页
//    public static final String ADMOB_SET_CALL_FLASH_RESULT_ID = "ca-app-pub-5980661201422605/6154282105"; //来电秀设置结果页
//    //插屏 quick admob
//    public static final String ADMOB_PHONE_DETAIL_Interstitial_ID = "ca-app-pub-5980661201422605/2447501978"; //通话记录详情页插屏原生
//    public static final String ADMOB_AFTER_CALL_Interstitial_ID = "ca-app-pub-5980661201422605/8387800775"; //号码详情页关闭插屏
//    public static final String ADMOB_SMS_DISPLAY_Interstitial_ID = "ca-app-pub-5980661201422605/2341267173"; //短信详情页关闭插屏
//    public static final String ADMOB_MAIN_EXIT_Interstitial_ID = "ca-app-pub-5980661201422605/7207811970"; //首页退出插屏

    //##############
    //caller admob advanced 高级原生, scan result group
    public static final String ADMOB_ADV_SCAN_RESULT_ID = "ca-app-pub-3275593620830282/1569692347"; //扫描结果页广告
    public static final String ADMOB_ADV_SMS_DISPLAY_NATIVE_ID = "ca-app-pub-3275593620830282/5479472606"; //短信详情页, 新来电秀解锁提示
    public static final String ADMOB_ADV_SET_CALL_FLASH_RESULT_ID = "ca-app-pub-3275593620830282/1012300900"; //来电秀设置结果页, 短信秀设置结果页


    //虚拟电话结果页 end call group
    public static final String ADMOB_ADV_AFTER_CALL_NATIVE_ID = "ca-app-pub-3275593620830282/3894928192"; //号码详情页, 通话后详情


    //call flash setting group
    public static final String ADMOB_ADV_FLASH_CALL_SETTING_ID = "ca-app-pub-3275593620830282/1923370975"; //来电秀设置页



    public static final String ADMOB_ADV_CALL_LOG_DETAIL_ID = "ca-app-pub-3275593620830282/2166913552"; //通话记录详情页

    public static final String ADMOB_ADV_STARTUP_NATIVE_ID = "ca-app-pub-3275593620830282/6870120482"; //启动页

    public static final String ADMOB_ADV_CONTACT_ACTIVITY_ID = "ca-app-pub-3275593620830282/1084700515"; //联系人列表activity

    //短信秀，重用以前没有使用的
    public static final String ADMOB_ADV_CONTACT_FRAGMENT_ID = "ca-app-pub-3275593620830282/4337549816"; //联系人列表fragment,  短信秀设置
    public static final String ADMOB_ADV_SMS_CLEAN_RESULT_ID = "ca-app-pub-3275593620830282/5665861782";//短信清理结果页，短信秀收到



    public static final String ADMOB_ADV_CLEAN_RESULT_ID = "ca-app-pub-5980661201422605/9873862851"; // 结果页

    //首次

    public static final String ADMOB_ADV_SMS_EDIT_ID = "ca-app-pub-3275593620830282/4190441668"; //来电秀设置结果 首次 //短信发送编辑页面

    public static final String ADMOB_ADV_BLOCK_SCAN_ID = "ca-app-pub-3275593620830282/9837288509"; //扫描结果页 首次

    public static final String ADMOB_ADV_FAKE_RESULT_ID = "ca-app-pub-3275593620830282/9493426154"; //启动页 新用户首次启动页，避免二次点击
    public static final String ADMOB_ADV_SMS_LIST_ID = "ca-app-pub-3275593620830282/7008176694"; //短信列表, 短信秀设置 首次

    public static final String ADMOB_ADV_CALL_LOG_NATIVE_ID = "ca-app-pub-3275593620830282/1236975265"; // 新用户首次来电秀下载 首次// 通话记录列表， Size range:Width: 280dp—1200dp Height: 80dp—612dp
    public static final String ADMOB_ADV_CHARGE_LOCK_ID = "ca-app-pub-3275593620830282/9030391723"; //来电秀预览 首次 //充电锁屏
    public static final String ADMOB_ADV_FAKE_HOME_ID = "ca-app-pub-3275593620830282/2739303780"; //结果页插页 首次//虚拟电话首页, cid_ad122, 自定义flash首页
    public static final String ADMOB_ADV_SPAM_UPDATE_RESULT_ID = "ca-app-pub-3275593620830282/9001459808"; // 未接来电
    //首次 end



    //插屏 advanced admob高级原生
    public static final String ADMOB_ADV_PHONE_DETAIL_Interstitial_ID = "ca-app-pub-3275593620830282/9474557930"; //通话记录详情页插屏原生
    public static final String ADMOB_ADV_AFTER_CALL_Interstitial_ID = "ca-app-pub-3275593620830282/6465251217"; //号码详情页关闭插屏, 其他结果页
    public static final String ADMOB_ADV_SMS_DISPLAY_Interstitial_ID = "ca-app-pub-3275593620830282/7721179484"; //短信详情页关闭插屏
    public static final String ADMOB_ADV_MAIN_EXIT_Interstitial_ID = "ca-app-pub-3275593620830282/3781934472"; //首页退出插屏

    // mopub banner id
//    public static final String MOPUB_ADV_BANNER_CALL_LOG_ID = "6fa0f7bd9caf48babcb5796eafbdd5ce";
//    public static final String MOPUB_ADV_BANNER_CALL_LOG_ID = "2524ab8dcf7e47af85afdfc0c3c81ad0";

    // MoPub big id
//    public static final String MOPUB_ADV_NUMBER_SCAN_RESULT_ID = "ea2967fbfe344dfe8e07321554915022";
//    public static final String MOPUB_ADV_NUMBER_SCAN_RESULT_ID = "a8ec14daee5243659f4fea8679d0e797";

    //adwords
    public static final String ADWORDS_CONVERTION_ID = "870455137";
    public static final String ADWORDS_LABEL = "NTwDCISO33AQ4a6InwM";
    public static final String ADWORDS_PRICE = "0.00";


    public static int SWITCH_SAPM_SERVER = PreferenceHelper.getInt(PREF_KEY_SWITCH_SAPM_SERVER, 0); //是否从服务端获取spam tag总开关， 默认0打开
    public static int SWITCH_USE_SAPM_THIRD = PreferenceHelper.getInt(PREF_KEY_SWITCH_USE_SAPM_THIRD, 0); //是否从第三方获取tag开关， 默认0打开

    public static HashSet<String> getControlSet(String countries) {
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

    public static HashSet<String> getNotShowSplashAds() {
        String countries = PreferenceHelper.getString(PREF_KEY_NOT_SHOW_ADS_SPLASH, "");
        HashSet<String> sets = getControlSet(countries);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "NotShowSplashAds: " + cc);
            }
        }
        return sets;
    }

    public static HashSet<String> getBigFbClickablePosition() {
        String positions = PreferenceHelper.getString(PREF_KEY_BIG_FB_CLICKABLE_POSITION, "");
        HashSet<String> sets = getControlSet(positions);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "BigFbClickable_positions: " + cc);
            }
        }
        return sets;
    }

    public static HashSet<String> getBigFbClickableCountries() {
        String positions = PreferenceHelper.getString(PREF_KEY_BIG_FB_CLICKABLE_COUNTRIES, "");
        HashSet<String> sets = getControlSet(positions);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "BigFbClickable_countries: " + cc);
            }
        }
        return sets;
    }

    public static HashSet<String> getNotShowInterstitial() {
        String countries = PreferenceHelper.getString(PREF_KEY_NOT_SHOW_INTERSTITIAL, "");
        HashSet<String> sets = getControlSet(countries);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "NotShowInterstitial: " + cc);
            }
        }
        return sets;
    }


    public static HashSet<String> getShowInterstitialPosition() {
        String positions = PreferenceHelper.getString(PREF_KEY_SHOW_INTERSTITIAL_POSITION, "");
        HashSet<String> sets = getControlSet(positions);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "Interstitial_positions: " + cc);
            }
        }
        return sets;
    }

    public static boolean isShowInAdsPosition(String position) {
        boolean show = false;
        if (getShowInterstitialPosition().contains(position)) {
            show = true;
        }

        return show;
    }

    /**
     * @return 0 show, non zero not show
     */
    public static int getIsShowInterstitial() {
        int isShow = 1;
        isShow = PreferenceHelper.getInt(PREF_KEY_IS_SHOW_INTERSTITIAL, 1);
        return isShow;
    }


    public final static String KEY_FLURRY = BuildConfig.FLURRY_KEY;

    public static final long DAY = 24 * 60 * 60 * 1000;
    public static final String FB_LOCKSCREEN_NATIVE_ID = "1147571511946658_1147587681945041";
    public static final String FB_CHARGELOCK_NATIVE_ID = "1147571511946658_1162628010441008";
    public static final String FB_RESULT_NATIVE_ID = "1147571511946658_1147588051945004";
    public static final String FB_REALSPEED_NATIVE_ID = "1147571511946658_1147588308611645";
    public static final String FB_COLORFULEGG_NATIVE_ID = "1147571511946658_1147589061944903";
    public static final String FB_SHORTCUT_NATIVE_ID = "1147571511946658_1147588871944922";
    public static final String FB_HOME_NATIVE_ID = "1147571511946658_1147588661944943";
    public static final String FB_BOTTOM_INTERSTITIAL_ID = "1147571511946658_1147590088611467";
    //以下ID在本应用下面已经合并成一个广告ID
    public static final String FB_HOTSPOT_NATIVE_ID = "1147571511946658_1147589825278160";
    public static final String FB_SPOOFNET_NATIVE_ID = FB_HOTSPOT_NATIVE_ID;
    public static final String FB_DATADAILY_NATIVE_ID = FB_HOTSPOT_NATIVE_ID;
    public static final String FB_DATADAILY_DETAIL_NATIVE_ID = FB_HOTSPOT_NATIVE_ID;
    public static final String FB_WIFICONN_NATIVE_ID = FB_HOTSPOT_NATIVE_ID;//cpu
    public static final String FB_DATAUSAGE_NATIVE_ID = FB_HOTSPOT_NATIVE_ID;

    //高级原生ADMOB
    public static final String ADMOB_TEST_ID = "ca-app-pub-3940256099942544/2247696110";

    public static final String ADMOB_LOCKSCREEN_NATIVE_ID = "ca-app-pub-3275593620830282/2673555255";
    public static final String ADMOB_RESULT_NATIVE_ID = "ca-app-pub-3275593620830282/4150288456";
    public static final String ADMOB_REALSPEED_NATIVE_ID = "ca-app-pub-3275593620830282/4289889251";
    public static final String ADMOB_SHORTCUT_NATIVE_ID = "ca-app-pub-3275593620830282/8720088853";
    public static final String ADMOB_HOME_NATIVE_ID = "ca-app-pub-3275593620830282/7103754852";
    public static final String ADMOB_COLORFULEGG_NATIVE_ID = "ca-app-pub-3275593620830282/5766622450";
    public static final String ADMOB_SPLASH_NATIVE_ID = "ca-app-pub-3275593620830282/9917620454";
    public static final String ADMOB_BOTTOM_INTERSTITIAL_ID = "ca-app-pub-3275593620830282/1057221258";
    //以下ID在本应用下面已经合并成一个广告ID
    public static final String ADMOB_DATAUSAGE_NATIVE_ID = "ca-app-pub-3275593620830282/2813156058";
    public static final String ADMOB_DATADAILY_NATIVE_ID = ADMOB_DATAUSAGE_NATIVE_ID;
    public static final String ADMOB_DATADAILY_DETAIL_NATIVE_ID = ADMOB_DATAUSAGE_NATIVE_ID;
    public static final String ADMOB_SPOOFNET_NATIVE_ID = ADMOB_DATAUSAGE_NATIVE_ID;
    public static final String ADMOB_WIFICONN_NATIVE_ID = ADMOB_DATAUSAGE_NATIVE_ID;
    public static final String ADMOB_HOTSPOT_NATIVE_ID = ADMOB_DATAUSAGE_NATIVE_ID;

    public static final String ADMOB_WIFICONN_HIGH_ID = ADMOB_WIFICONN_NATIVE_ID;
    public static final String ADMOB_HOTSPOT_HIGH_ID = ADMOB_HOTSPOT_NATIVE_ID;
    public static final String ADMOB_REALSPEED_HIGH_ID = ADMOB_REALSPEED_NATIVE_ID;
    public static final String ADMOB_DATAUSAGE_HIGH_ID = ADMOB_DATAUSAGE_NATIVE_ID;
    public static final String ADMOB_DATADAILY_HIGH_ID = ADMOB_DATADAILY_NATIVE_ID;
    public static final String ADMOB_DATADAILY_DETAIL_HIGH_ID = ADMOB_DATADAILY_DETAIL_NATIVE_ID;
    public static final String ADMOB_RESULT_HIGH_ID = ADMOB_RESULT_NATIVE_ID;
    public static final String ADMOB_SPOOFNET_HIGH_ID = ADMOB_SPOOFNET_NATIVE_ID;
    public static final String ADMOB_HOME_HIGH_ID = ADMOB_HOME_NATIVE_ID;

    //广告优先级SDK广告位编号
    public static final String PRIORITY_RESULT = "RESULT";
    public static final String PRIORITY_WIFI_CONNECT = "WIFI_CONNECT";
    public static final String PRIORITY_REAL_SPEED = "REAL_SPEED";
    public static final String PRIORITY_DATA_USAGE = "DATA_USAGE";
    public static final String PRIORITY_HOME = "HOME";
    public static final String PRIORITY_HOTSPOT = "HOTSPOT";
    public static final String PRIORITY_SPOOF_NET = "SPOOF_NET";
    public static final String PRIORITY_DAILY_REPORT = "DAILY_REPORT";
    public static final String PRIORITY_CHARGE_LOCK_DETAIL = "DAILY_REPORT_DETAIL";
    public static final String PRIORITY_COLORFUL_EGG = "COLORFUL_EGG";
    public static final String PRIORITY_LOCKSCREEN_AUTO = "LOCK_SCREEN";
    public static final String PRIORITY_HOME_BOTTOM_INTERSTITIAL = "HOME_BOTTOM_INTERSTITIAL";
    public static final String PRIORITY_NEWS = "NEWS";
    public static final String PRIORITY_NEWS_AUTO = "NEWS_AUTO";    //默认关闭广告
    public static final String PRIORITY_SPLASH = "SPLASH";    //默认关闭广告

    public static final int RESULTAD_REFRESH_TIME = 60 * 1000;
    public static final int WIFFCONNAD_REFRESH_TIME = 60 * 1000;
    public static final int EGGAD_REFRESH_TIME = 60 * 1000;
    public static final int DATAUSAGE_REFRESH_TIME = 60 * 1000;
    public static final int HOME_REFRESH_TIME = 60 * 1000;
    public static final int HOTSPOT_REFRESH_TIME = 60 * 1000;
    public static final int SPOOFNET_REFRESH_TIME = 60 * 1000;
    public static final int LOCKSCREEN_REFRESH_TIME = 600 * 1000;

    public static final int RESULTAD_REFRESH_TIME_ADMOB = 120 * 1000;
    public static final int WIFFCONNAD_REFRESH_TIME_ADMOB = 120 * 1000;
    public static final int EGGAD_REFRESH_TIME_ADMOB = 120 * 1000;
    public static final int DATAUSAGE_REFRESH_TIME_ADMOB = 120 * 1000;
    public static final int HOME_REFRESH_TIME_ADMOB = 60 * 60 * 1000;
    public static final int HOTSPOT_REFRESH_TIME_ADMOB = 120 * 1000;
    public static final int SPOOFNET_REFRESH_TIME_ADMOB = 120 * 1000;
    public static final int LOCKSCREEN_REFRESH_TIME_ADMOB = 600 * 1000;

    public static final String BSSID_KEY = "KEY_BSSID";
    public static final String IMPROVE_KEY = "improve";
    public static final String SHAR_KEY = "wifiAcceleration";

    public final static int MAX_FRAGEMNTS = 4;
    public final static int FRAGMENT_SORT = 1;
    public final static int FRAGMENT_HOME = 0;

    public final static int FRAGMENT_DATAPLAN = 11;
    public final static int FRAGMENT_WIFILIST = 12;

    public static final long TIME_REPEATING_30_S = 60 * 1000;

    //最大删除流量上限
    public static final long MAX_OPTIMIZE_FLOWSIZE = 1000 * 1024 * 1024;
    //最小优化百分比
    public static final int MIN_OPTIMIZE_PERCENT = 2;
    //最大优化百分比
    public static final int MAX_OPTIMIZE_PERCENT = 30;
    //信号最大提升百分比
    public static final int MAX_SINGAL_IMPROVE_PERCENT = 15;
    //信号最小提升百分比
    public static final int MIN_SINGAL_IMPROVE_PERCENT = 2;
    //信号加强上限值
    public static final int MAX_SINGAL_CAN_IMPROVE = 95;
    //信号加强下限值
    public static final int MIN_SINGAL_CAN_IMPROVE = 5;


    // 拦截电话通知;
    public final static int PUSH_TOOLS_BLOCK_CALL = 13;
    // 陌生人来电;
    public final static int PUSH_TOOLS_NOT_IN_CONTACT = 14;
    // 响一声来电;
    public final static int PUSH_TOOLS_RING_ONE = 15;
    // 短暂通话;
    public final static int PUSH_TOOLS_LITTLE_TALK = 16;
    // 主动拒绝:
    public final static int PUSH_TOOLS_REJECT = 17;
    // missed;
    public final static int PUSH_TOOLS_MISSED = 18;
    // 来电;
    public final static int PUSH_TOOLS_CALL = 19;
    // 打开拦截Spam开关;
    public final static int PUSH_TOOLS_OPEN_SPAM_SWITCH = 20;
    // 更新Spam数据库;
    public final static int PUSH_TOOLS_UPDATE_SPAM_DB = 21;
    // 短信;
    public final static int PUSH_TOOLS_RECEIVER_MESSAGE = 22;
    // 查看CallFlash功能提示;
    public final static int PUSH_TOOLS_CALL_FLASH_TOAST = 23;
    //new call flash notify
    public final static int PUSH_TOOLS_NEW_CALL_FLASH = 24;

    // new call recording not checked;
    public final static int PUSH_TOOLS_NEW_CALL_RECORDING = 25;
    // night mode block call notify
    public final static int PUSH_TOOLS_NIGHT_MODE_BLOCK_CALL = 26;

    // 点击night mode missed call 通知;
    public final static String PUSH_TOOLS_CLICK_NIGHT_MODE_MISSED_CALL = "action_click_night_mode_missed_call_notify";

    // 点击短信通知;
    public final static String PUSH_TOOLS_CLICK_MESSAGE = "action_click_message_notify";
    //点击短信通知通知上的mark按钮;
    public final static String PUSH_TOOLS_CLICK_MARK_MESSAGE = "action_click_mark_message_notify";

    //点击短信通知通知上的mark按钮;
    public final static String PUSH_TOOLS_CLICK_REPLY_MESSAGE = "action_click_reply_message_notify";

    // 取消短信通知;
    public final static String PUSH_TOOLS_NIGHT_MODE_MISSED_CALL_DISMISS = "action_night_mode_missed_call_dismiss";

    // 取消短信通知;
    public final static String PUSH_TOOLS_MESSAGE_DISMISS = "action_message_dismiss";
    // 通知显示的未接来电数量;
    public final static String PUSH_TOOLS_CALL_MISSED_COUNT = "call_missed_count";

    //点击连接信号弱
    public final static String CLICK_NOTIFICATION_MISSED = "click_notification_missed";
    public final static String CLICK_NOTIFICATION_UPDATE_SPAM_DB = "click_notification_update_spam_db";
    //点击数据流量使用过多
    public final static String CLICK_NOTIFICATION_NOT_IN_CONTACT = "click_notification_not_in_contact";
    //点击连接速度慢
    public final static String CLICK_NOTIFICATION_BLOCKED = "click_notification_blocked";
    //点击有后台进程消耗流量
    public final static String CLICK_NOTIFICATION_ONE_RING = "click_notification_one_ring";

    //来电卡片点击详情按钮
    public final static String CLICK_CALL_COME_CARD_DETAILS = "click_call_come_card_details";

    public final static String CLICK_NOTIFICATION_CALL_FLASH_OLD_USER = "click_notification_call_flash_old_user";
    public final static String CLICK_SMS_MESSAGES_NOTIFICATION = "click_sms_messages_notification";

    //数据套餐消息子类
    public final static int PUSH_TOOLS_DATA_PALN_NONESET = 1;
    public final static int PUSH_TOOLS_DATA_PALN_OVERUSED = 2;
    public final static int PUSH_TOOLS_DATA_PALN_TODAYFLOW = 3;

    //Intent的key
    public final static String NOTIFICATION_FLAG = "NOTIFICATION_FLAG";
    public final static String CALL_COME_CARD_DETAILS_FLAG = "call_come_card_details_flag";
    public final static String WIDGET_FLAG = "WIDGET_FLAG";

    //消息推送部分统计
    //展示连接陌生热点
    public final static String SHOW_NOTIFICATION_CONNECT_STRANGE = "show_notification_connect_strange";
    //展示连接信号弱
    public final static String SHOW_NOTIFICATION_WEAK_SIGNALS = "show_notification_weak_signals";
    //展示数据流量使用过多
    public final static String SHOW_NOTIFICATION_3G_4G_TRAFFIC = "show_notification_3g_4g_traffic";
    //展示连接速度慢
    public final static String SHOW_NOTIFICATION_TRAFFIC_OCCUPATION = "show_notification_traffic_occupation";
    //点击有后台进程消耗流量
    public final static String SHOW_NOTIFICATION_BACK_TRAFFIC = "show_notification_back_traffic";
    //展示流量套餐
    public final static String SHOW_NOTIFICATION_DATA_PLAN = "show_notification_data_plan";
    //展示连接陌生热点
    public final static String CLICK_NOTIFICATION_CONNECT_STRANGE = "click_notification_connect_strange";
    //展示新增设备
    public final static String SHOW_NOTIFICATION_SCAN_NEWDEV = "show_notification_wifi_scan_newdev";
    //展示"蹭网保护未开启"
    public final static String SHOW_NOTIFICATION_DEVICE_PROTECT_NOTOPEN = "show_notification_device_protect_notopen";
    //展示日报
    public final static String SHOW_NOTIFICATION_DATA_USAGE = "show_notification_data_usage";
    //展示后台应用建立网络连接
    public final static String SHOW_NOTIFICATION_BACK_CONNECT = "show_notification_back_connect";
    //展示锁屏期间流量消耗
    public final static String SHOW_NOTIFICATION_LOCKSCREEN_APPS = "show_notification_lockscreen_apps";
    //展示cpu使用率高
    public final static String SHOW_NOTIFICATION_CPU_MAX = "show_notification_cpu";

    //点击连接信号弱
    public final static String CLICK_NOTIFICATION_WEAK_SIGNALS = "click_notification_weak_signals";
    //点击数据流量使用过多
    public final static String CLICK_NOTIFICATION_3G_4G_TRAFFIC = "click_notification_3g_4g_traffic";
    //点击连接速度慢
    public final static String CLICK_NOTIFICATION_TRAFFIC_OCCUPATION = "click_notification_traffic_occupation";
    //点击有后台进程消耗流量
    public final static String CLICK_NOTIFICATION_BACK_TRAFFIC = "click_notification_back_traffic";
    //点击流量套餐
    public final static String CLICK_NOTIFICATION_DATA_PLAN = "click_notification_data_plan";
    //点击新增设备
    public final static String CLICK_NOTIFICATION_SCAN_NEWDEV = "click_notification_wifi_scan_newdev";
    //点击"蹭网保护未开启"
    public final static String CLICK_NOTIFICATION_DEVICE_PROTECT_NOTOPEN = "click_notification_device_protect_notopen";
    //点击日报
    public final static String CLICK_NOTIFICATION_DATA_USAGE = "click_notification_data_usage";
    //点击widget，跳转到流量界面
    public final static String CLICK_WIDGET_DATA_PLAN = "click_widget_data_plan ";
    //点击widget，跳转到流量界面
    public final static String CLICK_WIDGET_DATA_PLAN_CIRCLE = "click_widget_data_plan_circle ";
    //点击widget，跳转到实时网速界面
    public final static String CLICK_WIDGET_NETWORK_SPEED = "click_widget_network_speed ";
    //点击widget，跳转到蹭网设备界面
    public final static String CLICK_WIDGET_DEVICE_NEO = "click_widget_device_neo";
    //点击后台应用建立网络连接
    public final static String CLICK_NOTIFICATION_BACK_CONNECT = "click_notification_back_connect";
    //点击锁屏期间流量消耗
    public final static String CLICK_NOTIFICATION_LOCKSCREEN_APPS = "click_notification_lockscreen_apps";
    //点击cpu使用率高
    public final static String CLICK_NOTIFICATION_CPU_MAX = "click_notification_cpu";
    //Toast部分统计


    //wifi加密类型
    public static final int UNKNOWN = 0;


    //三星，小米，联想，魅族，HTC,华为，中兴
    //Android设备厂商
//    public static Map<String, String> androidVendor = new HashMap<String, String>() {
//        {
//            put("samsung", "samsung");
//            put("xiaomi", "xiaomi");
//            put("lenovo", "lenovo");
//            put("meizu", "meizu");
//            put("htc", "htc");
//            put("huawei", "huawei");
//            put("zte", "zte");
//            put("vivo", "vivo");
//            put("oppo", "oppo");
//            put("lg", "lg");
//            put("sony", "sony");
//            put("oneplus", "oneplus");
//            put("smartisan", "smartisan");
//        }
//
//    };
//

//
//    //需要保留的系统缓存
//    public static List<String> reservedSystemCache = new ArrayList<String>() {
//        {
//            add("com.google.android.apps.maps");
//            add("com.here.app.maps");
//            add("com.ea.game.simcitymobile_row");
//            add("org.videolan.vlc.betav7neon");
//            add("com.disney.frozensaga_goo");
//            add("com.callapp.contacts");
//            add("com.com2us.acefishing.normal.freefull.google.global.android.common");
//            add("com.fitstar.pt");
//            add("com.linecorp.LGTOYS");
//            add("jp.naver.linecamera.android");
//            add("jp.colopl.wcatkr");
//            add("air.com.ea.game.monopolyslots_na");
//            add("air.com.ea.game.monopolyslots_row");
//            add("com.ea.game.dungeonkeeper_row");
//            add("com.ea.game.dungeonkeeper_na");
//            add("jp.colopl.slingshot");
//        }
//    };


    //读取短信的时间限制
    public static final long QUERY_SMS_DATE = System.currentTimeMillis() - (12 * 30 * 24 * 3600 * 1000l); //读取大于这个时间得短信
    //清除短信的时间限制
    public static final long CLEAN_SMS_DATE_ONE_MONTH_AGO = System.currentTimeMillis() - (1 * 30 * 24 * 3600 * 1000l);
    public static final long CLEAN_SMS_DATE_HALF_YEAR_AGO = System.currentTimeMillis() - (6 * 30 * 24 * 3600 * 1000l);
    public static final long CLEAN_SMS_DATE_TWO_WEEK_AGO = System.currentTimeMillis() - (14 * 24 * 3600 * 1000l);


    // 应用大字体最大scale
    public static final float MAX_FONT_SCALE = 1.2f;
    //联系人ID
    public static final String CONTACT_ID = "contact_ID";
    //from contactactiivty
    public static final String FROM_CONTACT = "from_contact";
    // from private number activity
    public static final String FROM_PRIVATE_NUMBER = "from_private_number";
    //联系人默认头像背景颜色id
    public static final String CONTACT_DEFALT_AVATAR_DRAWABLE_RESID = "contact_defalt_avatar_drawable_resid";
    //通话记录跳转到详情界面传的值
    public static final String CALLLOG_TO_DETAILS_INFO = "calllog_to_details_info";
    public static final String TO_DETAILS_IS_MESSAGE_NUMBER = "message_number";
    //号码
    public static final String PHONE_NUMBER = "phone_number";
    //通话记录
    public static final String CALL_LOG = "call_log";
    // blocklog list显示类型: 未读(1), 全部(0);
    public static final String BLOCK_LOG_LIST_TYPE = "list_type";
    // 来电陌生人悬浮窗;
    public static final String FlOATING_COMING_STRAN = "coming_stranger";
    // 手机联系人悬浮窗;
    public static final String FLOATING_COMING_CONTACTS = "coming_contacts";
    // 拨打电话悬浮窗;
    public static final String FLOATING_OUT_CALL = "outgoing_call";
    // 展示通話后頁面
    public static final String SHOW_CALL_AFTER = "call_after_with_call_id"; // call id;
    // 通话后页面和短信接收是否展示广告
    public static final String SHOW_CALL_AFTER_SMS_AD = "call_after_with_call_id_ad"; // call id ad; 默认 0 展示
    //广告页面独立id 合并id, new id 控制, 几个大广告位，万一被封后，可以从服务端获取新id, 启动页，电话结束页， 扫描结果页， 来电秀设置
    // 短信清理结果页是否使用独立fb id， 默认1使用
    public static final String IS_USE_SMS_RESULT_ID = "is_use_sms_result_id"; //  默认 1 使用, 0 - 合并, 99 new id
    public static final String NEW_USE_SMS_RESULT_ID = "new_use_sms_result_id"; //  对应 99 new id
    // 是否通话记录列表页独立fb id , 默认 1 使用
    public static final String IS_USE_CALLLOG_ID = "is_use_calllog_id"; //  默认 1 使用
    // 是否联系人列表页独立fb id , 默认 1 使用
    public static final String IS_USE_CONTACT_ID = "is_use_contact_id"; //  默认 1 使用 ,2 合并来电秀设置id
    // 是否splash页独立fb id , 默认 1 使用
    public static final String IS_USE_SPLASH_ID = "is_use_splash_id"; //  是否使用splash页独立fb id , 默认 0 使用合并联系人页面id， 1 使用独立id, 2 使用endcall id； 99:  new,
    public static final String NEW_USE_SPLASH_ID = "new_use_splash_id"; //  99-对应的 new id
    // 号码升级结果页是否使用独立fb id, 默认不使用
    public static final String IS_USE_SPAM_UPDATE_RESULT_ID = "is_use_spam_update_result_id"; //  默认 0 不使用
    //合并id与独立id
    public static final String IS_USE_CALL_GIF_LIST = "is_use_call_gif_list"; //  1 使用独立id, 0 使用合并id, call flash gif download list
    public static final String IS_USE_SMS_LIST = "is_use_sms_list"; //  1 使用独立id, 0 使用合并id
    public static final String IS_USE_BLOCK_SCAN_HOME = "is_use_block_scan_home"; //  1 使用独立id, 0 使用合并id
    public static final String IS_USE_FAKECALL_HOME = "is_use_fakecall_home"; //  1 使用独立id, 0 使用合并id
    public static final String IS_USE_SMS_COME = "is_use_sms_come"; //  1 使用独立id, 0 使用合并id
    public static final String IS_USE_SMS_EDIT_SEND = "is_use_sms_edit_send"; //  1 使用独立id, 0 使用合并id
    // 电话结束页是否使用新fb id, 默认不使用
    public static final String IS_USE_END_CAll_ID = "is_use_end_call_id"; //  默认 0 不使用, 99: use new
    public static final String NEW_USE_END_CAll_ID = "new_use_end_call_id"; //  99-对应的 new id
    // 来电秀设置结果页fb id, 默认不使用
    public static final String IS_USE_CALL_FLASH_ID = "is_use_call_flash_id"; //  默认 0 不使用, 99: use new
    public static final String NEW_USE_CALL_FLASH_ID = "new_use_call_flash_id"; //  99-对应的 new id
    public static final String CALL_FLASH_BOTTOM = "call_flash_bottom"; //  call flash fb广告距离底部边距， 默认0, 可能调整为4or8
    // 号码扫描结果页是否使用新fb id, 默认不使用
    public static final String IS_USE_SCAN_ID = "is_use_scan_id"; //  默认 0 不使用, 99: use new
    public static final String NEW_USE_SCAN_ID = "new_use_scan_id"; //  99-对应的 new id
    // 来电秀设置页是否使用新fb id, 默认不使用
    public static final String IS_CALL_FLASH_ID = "is_call_flash_id"; //  默认 0 不使用, 99: use new
    public static final String NEW_CALL_FLASH_ID = "new_call_flash_id"; //  99-对应的 new id
    public static final String CALL_FLASH_BG_COLOR = "call_flash_bg_color"; //  call flash 广告背景颜色， 默认黑色，可以从服务端取
    //广告页面独立id 合并id, new id 控制 end
    public static final String SPLASH_AD_NOT_SKIP = "splash_ad_not_skip"; //  否自动跳过splash ads， 1 否, 默认0， 跳过
    public static final String SPLASH_AD_NOT_SKIP_COUNT = "splash_ad_not_skip_count"; //  不自动跳过splash ads时默认显示次数 2
    public static final String USER_SPLASH_AD_NOT_SKIP_COUNT = "user_splash_ad_not_skip_count"; //
    public static final String USER_SPLASH_AD_NOT_SKIP_COUNT_TIME = "user_splash_ad_not_skip_count_time"; //
    //add on 09-15
    public static final String SPLASH_AD_MAX_COUNT = "splash_ad_max_count"; //  启动页最多广告次数，默认99 = 不限制
    public static final String USER_SPLASH_AD_COUNT = "user_splash_ad_count"; //  用户当日显示splash ad的次数
    public static final String USER_SPLASH_AD_COUNT_TIME = "user_splash_ad_count_time"; //
    public static final String USER_SPLASH_VISIT_TIME = "user_splash_visit_time"; //
    //call flash setting exit tips



    //非联系人是否显示名字, 默认0显示
    public static final String IS_SHOW_USER_SMART_ID = "is_show_user_smart_id";


    // call flash gif url
    public static final String CALL_FLASH_DOWNLOAD_URL = "http://d1flstfyhw1yx3.cloudfront.net/";
    public static final String CALL_FLASH_THEME_GIF_NAME_TOM = "tom";
    public static final String CALL_FLASH_THEME_GIF_NAME_CAT = "cat";
    public static final String CALL_FLASH_THEME_GIF_NAME_JERRY = "jerry";
    public static final String CALL_FLASH_THEME_GIF_NAME_CHICK = "chick";
    public static final String CALL_FLASH_THEME_GIF_NAME_PANDA = "gif_panda.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_LOVE1 = "love1.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_LOVE2 = "love2.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_HALLOWEEN = "gif_halloween.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_VISUAL_1 = "gif_visual_1.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_VISUAL_2 = "gif_visual_2.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_VISUAL_3 = "gif_visual_3.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_VISUAL_4 = "gif_visual_4.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_VISUAL_5 = "gif_visual_5.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_COLOR_FLASH = "gif_color_flash.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_BUTTERFLY = "butterfly.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_FIREWORKS = "fireworks.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_MELT = "melt.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_MONKEY = "monkey.gif";
    public static final String CALL_FLASH_THEME_GIF_NAME_SANTA_CLAUS = "santa_show.gif";

    public static final String CALL_FLASH_THEME_VIDEO_NAME_PIG = "pig.mp4";
    public static final String CALL_FLASH_THEME_VIDEO_NAME_RAP = "rap.mp4";
    public static final String CALL_FLASH_THEME_VIDEO_NAME_BEAR = "video_bear.mp4";

    // gif
    public static final String CALL_FLASH_THEME_GIF_URL_LOVE1 = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/lover1.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_LOVE2 = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/lover2.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_PANDA = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/gif_panda.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_HALLOWEEN = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/gif_halloween.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_VISUAL_1 = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/Photo_1013_5a.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_VISUAL_2 = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/Photo_1013_7a.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_VISUAL_3 = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/Photo_1013_6a.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_VISUAL_4 = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/Photo_1013_2a.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_VISUAL_5 = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/Photo_1013_1a.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_COLOR_FLASH = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/color_flash.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_MONKEY = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/monkey.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_MONKEY2 = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/monkey2.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_SANTA_CLAUS = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/santa_show.gif";

    public static final String CALL_FLASH_THEME_GIF_URL_BUTTERFLY = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/butterfly.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_FIREWORKS = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/fireworks.gif";
    public static final String CALL_FLASH_THEME_GIF_URL_MELT = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/melt.gif";

    // video
    public static final String CALL_FLASH_THEME_VIDEO_URL_PIG = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/pig.mp4";
    public static final String CALL_FLASH_THEME_VIDEO_URL_RAP = "http://d1flstfyhw1yx3.cloudfront.net/gif_out/rap.mp4";
    public static final String CALL_FLASH_THEME_VIDEO_URL_BEAR = "http://s3.amazonaws.com/lion-callerid/gif_out/video_bear.mp4";

    // 展示短信界面
    public static final String SHOW_MESSAGE = "show_message";
    // 展示智能识别
    public static final String SHOW_IDENTIFY = "show_identify";
    //初始化联系人的状态
    public static final String INIT_CONTACT = "init_contact";
    //初始化calllog的状态
    public static final String INIT_CALLLOG = "init_calllog";
    //初始化sms的状态
    public static final String INIT_SMS = "init_sms";
    //未知号码的个数
    public static final String UNKNOWN_NUMBER_COUNT = "unknown_number_count";


    //号码类型
    public static final HashMap<Integer, String> NUMBER_TYPE = new HashMap<Integer, String>() {
        {
            put(1, "FIXED_LINE");
            put(2, "MOBILE");
            put(3, "FIXED_LINE_OR_MOBILE");
            put(4, "TOLL_FREE");
            put(5, "PREMIUM_RATE");
            put(6, "SHARED_COST");
            put(7, "VOIP");
            put(8, "PERSONAL_NUMBER");
            put(9, "PAGER");
            put(10, "UAN");
            put(11, "VOICEMAIL");
            put(12, "UNKNOWN");
        }
    };

    // call flash;
    public static final String WEEKEND_CALL_FLASH_TYPE = "weekend_call_flash_type";
    public static final String CALL_FLASH_TYPE = "call_flash_type";
    public static final String CALL_FLASH_TYPE_DYNAMIC_PATH = "call_flash_type_dynamic_path";
    public static final String CALL_FLASH_ON = "call_flash_on";
    public static final String CALL_FLASH_SHOW_EXIT_INTRO = "call_flash_intro_dialog_is_show"; // 第一次进入应用引导弹窗弹出标志,
    public static final String CALL_FLASH_SHOW_DIALOG_TIME = "call_flash_show_dialog_time"; // call flash dialog 引导弹出时间,
    public static final String CALL_FLASH_SHOW_TYPE_INSTANCE = "call_flash_show_type_instance"; // 当前使用的flash的实例(string形式)
    //    public static final String CALL_FLASH_JUST_SHOW_EXIT_INTRO = "call_flash_just_show_exit_intro";
//    public static final String CALL_FLASH_FESTIVAL_EVER_SELECT = "call_flash_festival_ever_select";
//    public static final String CALL_FLASH_LOVE_EVER_SELECT = "call_flash_love_ever_select";
    public static final String CALL_FLASH_PUSH_TOOLS_SHOW = "call_flash_push_tools_show";
    public static final String CALL_FLASH_LAST_ENTER_TIME = "call_flash_last_enter_time";
    public static final String CALL_FLASH_NOTIFY_TIME_OLD_USER = "call_flash_notify_time_old_user";
    public static final String CALL_FLASH_CUSTOM_BG_PATH = "call_flash_custom_bg_path";
    public static final String CALL_FLASH_CUSTOM_BG_PATH_OLD = "call_flash_custom_bg_path_old";

    public static Context mContext = ApplicationEx.getInstance().getBaseContext();
    //标签对应的图片
    public static final HashMap<String, String> TAG = new HashMap<String, String>() {
        {
            put("num_tag_103", mContext.getResources().getString(R.string.icon_telemarkting));
            put("num_tag_104", mContext.getResources().getString(R.string.icon_insurance));
            put("num_tag_105", mContext.getResources().getString(R.string.icon_house));
            put("num_tag_106", mContext.getResources().getString(R.string.icon_hotel));
            put("num_tag_107", mContext.getResources().getString(R.string.icon_education));
            put("num_tag_108", mContext.getResources().getString(R.string.icon_fix));
            put("num_tag_109", mContext.getResources().getString(R.string.icon_travel));
            put("num_tag_110", mContext.getResources().getString(R.string.icon_express));
            put("num_tag_111", mContext.getResources().getString(R.string.icon_transportation));
            put("num_tag_112", mContext.getResources().getString(R.string.icon_video));
            put("num_tag_113", mContext.getResources().getString(R.string.icon_lunch));
            put("num_tag_114", mContext.getResources().getString(R.string.icon_publicservice));
        }
    };

    // 拦截常用骚扰电话开关标志；默认为 true;
    public static final String KEY_BLOCK_COMMON_SPAMMERS = "block_common_spammers";
    // 拦截陌生人;
    public static final String KEY_BLOCK_STRANGER = "block_stranger";
    // 拦截模式: 自动挂断;
    public static final String KEY_AUTO_END_CALL = "auto_end_call";
    // 拦截隐藏电话开关标志；默认为 true;
    public static final String KEY_BLOCK_HIDDEN = "block_hide";
    // 免打扰拦截;
    public static final String KEY_BLOCK_DO_NOT_DISTURB = "block_no_disturbing_time";
    // 免打扰截止时间(毫秒数);
    public static final String KEY_BLOCK_DO_NOT_DISTURB_VALUE = "block_no_disturbing_time_value";
    // 免打扰截止的名称;
    public static final String KEY_BLOCK_NO_DISTURBING_TIME_NAME = "block_no_disturbing_time_name";
    // 拦截国际号码;
    public static final String KEY_BLOCK_INTERNATIONAL = "block_international";
    // 设置通话提示:
    public static final String KEY_BLOCK_PROMPT = "block_prompt";
    // 拦截次数;
    public static final String BLOCK_CALL_COUNT = "block_count";
    // 手机响铃模式;
    public static final String BLOCK_AUDIO_RING_MODE = "block_ring_mode";

    // 保存搜索记录;
    public static final String SEARCH_HISTORY = "note_search_history";

    // 保存用的拦截类型键;
    public static final String KEY_BLOCK_TYPE_COMMON_SPAM = "block_type_001";
    public static final String KEY_BLOCK_TYPE_HIDE = "block_type_003";
    public static final String KEY_BLOCK_TYPE_STRANGER = "block_type_002";
    public static final String KEY_BLOCK_TYPE_DO_NOT_DISTURB = "block_type_004";
    public static final String KEY_BLOCK_TYPE_BLACK = "block_type_005";
    public static final String KEY_BLOCK_TYPE_INTER = "block_inter";
    public static final String KEY_UNBLOCK = "un_block";

//    public static final Map<String, String> BLOCK_TYPE = new HashMap() {
//        {
//            put("block_type_001", "Common Spam");
//            put("block_type_002", "Stranger");
//            put("block_type_003", "Unknow");
//            put("block_type_004", "Do not Disturb");
//            put("block_type_005", "Black List");
//        }
//    };

    // 最近一次更新Spam数据时间;
    public static final String PREF_KEY_INIT_SPAM_TIME = "caller_init_spam_time";
    public static final String PREF_KEY_INIT_SPAM_TIME_NEW = "caller_init_spam_time_new3";
    // 最后一次点击更新Spam按钮的时间;
    public static final String PREF_KEY_LAST_SPAM_CLICK_TIME = "last_spam_click_time";
    // 最後一次彈出spam退出提示時間:
    public static final String PREF_KEY_LAST_EXIT_SPAM_SHOW_TIME = "last_exit_spam_show_time";
    // 是否需要更新spam db
    public static final String PREF_KEY_IS_UPDATE_SAPM_DB = "is_update_spam_db";
    // 最新呼入的电话
    public static final String LAST_CALL_NUMBER = "last_phone_number";
    // 最后一次发送通知的时间;
    public static final String LAST_PUSH_NOTIFY_TIME = "last_push_notify_time";

    // 電話號碼掃描最後展示時間
    public static final String PREF_KEY_LAST_EXIT_NUMBER_SCAN_SHOW_TIME = "last_exit_number_scan_show_time";
    // 號碼掃描引導最後展示時間
    public static final String PREF_KEY_LAST_NUMBER_SCAN_INTRO_SHOW_TIME = "last_number_scan_intro_show_time";

    // 退出引导弹窗;
    public static final String PREF_KEY_EXIT_GUIDE_LAST_CALL_FLASH_TIME = "exit_guide_call_flash_time";
    public static final String PREF_KEY_EXIT_GUIDE_LAST_NUMBER_SCAN_TIME = "exit_guide_number_scan_time";
    public static final String PREF_KEY_EXIT_GUIDE_LAST_SPAM_UPDATE_TIME = "exit_guide_spam_update_time";

    // 外部引导弹窗
    public static final String PREF_KEY_EXTERNAL_VOLUME_LAST_SHOW_TIME = "external_volume_last_show_time";
    public static final String PREF_KEY_EXTERNAL_VOLUME_SHOW_TIME_SPAM_UPDATE = "external_volume_show_time_spam_update";
    public static final String PREF_KEY_EXTERNAL_VOLUME_SHOW_TIME_NUMBER_SCAN = "external_volume_show_time_number_scan";
    public static final String PREF_KEY_EXTERNAL_VOLUME_SHOW_TIME_CALL_FLASH = "external_volume_show_time_call_flash";

    // 通话记录的类型
    public static final String ALL_CALLLOG = "all_calllog";
    public static final String IN_COMING_CALLLOG = "in_coming_calllog";
    public static final String OUT_GOING_CALLLOG = "out_going_calllog";
    public static final String MISSED_CALLLOG = "missed_calllog";
    public static final String BLOCKED_CALLOG = "blocked_calllog";
    public static final String DISCERNED_BY_LIONCALL = "discerned_by_lioncall";
    public static final String REFUSED_CALLLOG = "refused_calllog";

    public static final String TO_MAIN_CALLLOG = "to_main_calllog";
    public static final String TO_MAIN_BLOCK = "to_main_block";

    //接收到短信的key
    public static final String SMS_MESSAGES_LIST = "sms_message_list";
    public static final String SMS_ADDRESS = "sms_address";
    public static final String SMS_DATE = "sms_date";
    public static final String SMS_DATE_SENT = "sms_date_sent";
    public static final String SMS_BODY = "sms_body";
    public static final String SMS_MESSAGE_URI = "sms_message_uri";
    public static final String SMS_NUMBER_AND_NAME = "sms_number_and_name";

    public static final String MISSED_CALL_DATE = "missed_call_date";

    //broadcast
    public final static String SMS_SENT = "blocker.call.wallpaper.screen.caller.ringtones.callercolor.sms.SMS_SENT";
    public final static String SMS_DELIVERED = "blocker.call.wallpaper.screen.caller.ringtones.callercolor.sms.SMS_DELIVERED";
    public final static String SMS_SENT_FAIL = "blocker.call.wallpaper.screen.caller.ringtones.callercolor.sms.SMS_SENT_FAIL";


    public final static String PREF_KEY_LAST_FEEDBACK_TIME = "pref_key_last_feedback_time";

    public final static String ACTION_FINISH_ACTIVITY = "action_finish_actiivty";

//    //号码类型
//    public static Map<String, String> numberTypeMap = new HashMap<String, String>() {
//        {
//            put("FIXED_LINE", mContext.getString(R.string.FIXED_LINE));
//            put("MOBILE", mContext.getString(R.string.MOBILE));
//            put("FIXED_LINE_OR_MOBILE", mContext.getString(R.string.FIXED_LINE_OR_MOBILE));
//            put("TOLL_FREE", mContext.getString(R.string.TOLL_FREE));
//            put("PREMIUM_RATE", mContext.getString(R.string.PREMIUM_RATE));
//            put("SHARED_COST", mContext.getString(R.string.SHARED_COST));
//            put("VOIP", "UNKNOWN");
//            put("PERSONAL_NUMBER", mContext.getString(R.string.PERSONAL_NUMBER));
//            put("PAGER", "UNKNOWN");
//            put("UAN", "UNKNOWN");
//            put("VOICEMAIL", "UNKNOWN");
//            put("UNKNOWN", "UNKNOWN");
//        }
//    };


    public final static String NUMBER_SCAN_SPAMLIST = "number_scan_spamlist";
    public final static String NUMBER_SCAN_RESULT = "number_scan_result";
    public final static String NUMBER_SCAN_RESULTLIST = "number_scan_result_list";
    public static final String NUMBER_SCAN_COME_FROM = "number_scan_come_from";

    //设置默认短信返回码
    public static final int DEFAULT_SMS_REQUEST_CODE = 0x000111;
    public static final int DEFAULT_SMS_MARK_READ_REQUEST_CODE = 0x000256;
    public static final int DEFAULT_SMS_TO_MESSAGE_BLOCK_ACTIIVTY_REQUEST_CODE = 0x000369;

    public static final String COME_FROM_CALL_FLASH_PREVIEW = "come_from_call_flash_preview";
    public static final String NUMBER_FOR_CALL_FLASH = "number_for_call_flash";
    public static final String COME_FROM_PHONEDETAIL = "come_from_detail";
    public static final String COME_FROM_CALLAFTER = "come_from_call_after";
    public static final String IS_CUSTOM_CALLFLASH = "is_custom_call_flash";
    public static final String COME_FROM_FLASH_DETAIL = "come_from_falsh_detail";
    public static final String COME_FROM_CALL_FLASH_SHOW2 = "come_from_call_flash_show2";
    public static final String CALL_FLASH_INFO = "call_FLASH_INFO";
    public static final String COME_FROM_MESSAGE_BLOCK_ACTIVITY = "come_from_message_block_activity";
    public static final String IS_ONLINE_FOR_CALL_FLASH = "is_online_for_call_flash";
    public static final String COME_FROM_DESKTOP = "come_from_desktop";


    public static final String SHORTCUT_TO_MAINACTIVITY_FRAGMENT_INDEX = "shortcut_to_mainactivity_fragment_index";

    public static final long NUMBER_SCAN_SHOW_PS_AD_DELAY_TIME = 2 * 1000;
}
