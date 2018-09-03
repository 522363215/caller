package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventInterstitialAdLoadSuccess;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.AdPreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.FullScreenAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

public class InterstitialAdUtil {
    private static final String TAG = "InterstitialAdvertisement";

    public final static int POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL = 0;
    public final static int POSITION_INTERSTITIAL_AD_IN_EXTERNAL_MAGIC = 1;
    public static final int POSITION_INTERSTITIAL_AD_IN_SPLASH = 2; //启动页插屏
    public static final int POSITION_INTERSTITIAL_AD_IN_CALL_AFTER = 3; //电话结束页插屏

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

    public static void loadInterstitialAd(Context context, final int position) {
        if (isShowInterstitial(position)) {
            if (isShowFullScreenAd(position)) {
                FullScreenAdManager.getInstance().loadAd(position);
            } else {
                LogUtil.d(TAG, "loadInterstitialAd load start.");
                InterstitialAdvertisement.FbAdId fbAdId = new InterstitialAdvertisement.FbAdId();
                fbAdId.highId = "";// getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.HIGH, InterstitialAdvertisement.AdType.FACEBOOK);
                fbAdId.mediumId = "";//getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.MEDIUM, InterstitialAdvertisement.AdType.FACEBOOK);
                fbAdId.normalId = getNormalInterstitialFbAdId(position);//getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.NORMAL, InterstitialAdvertisement.AdType.FACEBOOK);

                InterstitialAdvertisement.AdmobAdId admobAdId = new InterstitialAdvertisement.AdmobAdId();
                admobAdId.highId = "";// getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.HIGH, InterstitialAdvertisement.AdType.ADMOB);
                admobAdId.mediumId = "";// getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.MEDIUM, InterstitialAdvertisement.AdType.ADMOB);
                admobAdId.normalId = getNormalInterstitialAdmobAdId(position);//getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.NORMAL, InterstitialAdvertisement.AdType.ADMOB);

                InterstitialAdvertisement.AdmobAdxId admobAdxId = new InterstitialAdvertisement.AdmobAdxId();
                admobAdxId.highId = "";//getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.HIGH, InterstitialAdvertisement.AdType.ADMOB_ADX);
                admobAdxId.mediumId = "";//getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.MEDIUM, InterstitialAdvertisement.AdType.ADMOB_ADX);
                admobAdxId.normalId = "";// getInGroupIdByKey(position, InterstitialAdvertisement.InterstitialAdPriority.NORMAL, InterstitialAdvertisement.AdType.ADMOB_ADX);


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
                            EventBus.getDefault().post(new EventInterstitialAdLoadSuccess(position));
                        }
                    });
                }
            }
        }
    }

    private static String getNormalInterstitialFbAdId(int position) {
        String fbId = "";
        switch (position) {
            case POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL:
                fbId = CallerAdManager.getFacebook_id(CallerAdManager.POSITION_FB_IN_DETAIL_NORMAL);
                break;
            case POSITION_INTERSTITIAL_AD_IN_EXTERNAL_MAGIC:
                break;
            case POSITION_INTERSTITIAL_AD_IN_SPLASH:
                break;
            case POSITION_INTERSTITIAL_AD_IN_CALL_AFTER:
                break;
            default:
                break;
        }
        return fbId;
    }

    private static String getNormalInterstitialAdmobAdId(int position) {
        String admobId = "";
        switch (position) {
            case POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL:
                admobId = CallerAdManager.getAdmob_id(CallerAdManager.POSITION_ADMOB_IN_DETAIL_NORMAL);
                break;
            case POSITION_INTERSTITIAL_AD_IN_EXTERNAL_MAGIC:
                admobId = CallerAdManager.INTERSTITIAL_ADMOB_ID_IN_EXT_NORMAL;
                break;
            case POSITION_INTERSTITIAL_AD_IN_SPLASH:
                admobId = CallerAdManager.INTERSTITIAL_ADMOB_ID_IN_SPLASH;
                break;
            case POSITION_INTERSTITIAL_AD_IN_CALL_AFTER:
                admobId = CallerAdManager.INTERSTITIAL_ADMOB_ID_IN_END_CALL;
                break;
            default:
                break;
        }
        return admobId;
    }

    /**
     * @param position, CallerAdManager.IN_ADS_RESULT
     * @return
     */
    public static boolean isShowInterstitial(int position) {
        boolean show = false;
        int i_show = 0;
        switch (position) {
            case POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL:
                int show_call_flash = AdPreferenceHelper.getInt("pref_show_interstitial_call_flash", 1); //0 not show, 1 show
                if (show_call_flash == 1) {
                    LogUtil.d("isShowInterstitial", "loadInterstitial IN_ADS_CALL_FLASH true: ");
                    show = true;
                }
                break;
            case POSITION_INTERSTITIAL_AD_IN_EXTERNAL_MAGIC:
                i_show = ApplicationEx.getInstance().getGlobalADPreference().getInt("pref_ext_show_in_ads_on_close", 0);
                if (i_show == 1) {
                    show = true;
                }
                break;
            case POSITION_INTERSTITIAL_AD_IN_SPLASH:
                i_show= ApplicationEx.getInstance().getGlobalADPreference().getInt("pref_show_in_ads_on_splash", 0);
                if (i_show == 1) {
                    show = true;
                }
                break;
            case POSITION_INTERSTITIAL_AD_IN_CALL_AFTER:
                i_show= ApplicationEx.getInstance().getGlobalADPreference().getInt("pref_show_in_ads_on_end_call", 0);
                if (i_show == 1) {
                    show = true;
                }
                break;
            default:
                break;
        }

        return show;
    }

    public static boolean isShowFullScreenAd(int position) {
        boolean isShow = false;
        switch (position) {
            case POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL:
                isShow = FirstShowAdmobUtil.isShowFirstAdMob(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_FULL_SCREEN_CALL_FLASH_DETAIL);
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

    /**
     * @param position               插屏的的位置
     * @param interstitialAdPriority 插屏的优先级
     * @param adType                 插屏的广告类型
     */
    public static String getInGroupIdByKey(int position, String interstitialAdPriority, String adType) {
        String adid = "";
        String key = "";
        switch (position) {
            case POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL:
                key = "pref_flash_in_ads_group_" + adType + "_" + interstitialAdPriority.toLowerCase();
                adid = getInGroupAdIdFlashByKey(key);
                break;
        }
        LogUtil.d(TAG, "loadInterstitialAd getInGroupIdByKey key: " + key + ", adid: " + adid + ",position:" + position);
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

}
