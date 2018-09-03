package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Random;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.ExternalParam;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.AdPreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

public class CallerAdManager {
    public final static String PARAM_ALL_COUNTRIES = "all_countries";

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

    //admob id

    public static final int POSITION_ADMOB_SPLASH_FIRST = 1;
    public static final int POSITION_ADMOB_SPLASH_NORMAL = 2;
    public static final int POSITION_ADMOB_RESULT_FIRST = 3;
    public static final int POSITION_ADMOB_RESULT_NORMAL = 4;
    public static final int POSITION_ADMOB_IN_DETAIL_FIRST = 5;
    public static final int POSITION_ADMOB_IN_DETAIL_NORMAL = 6;
    public static final int POSITION_ADMOB_MINE_NORMAL = 7;
    public static final int POSITION_ADMOB_END_CALL_NORMAL = 8;
    public static final int POSITION_ADMOB_ADV_SWIPE = 9;


    public static final String ADMOB_ID_ADV_SPLASH_FIRST = "ca-app-pub-4922304484386262/2775707274";//启动页首次colorphone-1
    public static final String ADMOB_ID_ADV_SPLASH_NORMAL = "ca-app-pub-4922304484386262/1118068726";//启动页普通colorphone-2
    public static final String ADMOB_ID_ADV_RESULT_FIRST = "ca-app-pub-4922304484386262/7412014335";//结果页首次colorphone-3
    public static final String ADMOB_ID_ADV_RESULT_NORMAL = "ca-app-pub-4922304484386262/3085756519";//结果页普通colorphone-4
    public static final String ADMOB_ID_ADV_MINE_NORMAL = "ca-app-pub-4922304484386262/1430836018";//mine页普通colorphone-7
    public static final String ADMOB_ID_ADV_END_CALL_NORMAL = "ca-app-pub-4922304484386262/1752493548";//通话结束页colorphone-8
    public static final String ADMOB_ID_ADV_SWIPE = "ca-app-pub-4922304484386262/4735231174"; //swipe,colorphone-9


    //插屏
    public static final String INTERSTITIAL_ADMOB_ID_IN_CALL_FALSH_DETAIL_FIRST = "ca-app-pub-4922304484386262/5328776475"; //自定义插屏, 来电秀设置首次colorphone-5
    public static final String INTERSTITIAL_ADMOB_ID_IN_CALL_FALSH_DETAIL_NORMAL = "ca-app-pub-4922304484386262/6230794603";//来电秀设置插屏colorphone-in-6
    public static final String INTERSTITIAL_ADMOB_ID_IN_EXT_NORMAL = "ca-app-pub-4922304484386262/7141186057";//外部弹窗插屏colorphone-in-10
    public static final String INTERSTITIAL_ADMOB_ID_IN_SPLASH = "ca-app-pub-4922304484386262/6779886100"; //启动页插屏colorphone-in-11
    public static final String INTERSTITIAL_ADMOB_ID_IN_END_CALL = "ca-app-pub-4922304484386262/1287886277"; //通话结束页插屏

    //多建了一个colorphone-6 ca-app-pub-4922304484386262/6762628719

    //admob id end

    //facebook id

    public static final int POSITION_FB_SPLASH_NORMAL = 11; //启动页
    public static final int POSITION_FB_RESULT_NORMAL = 22; //结果页
    public static final int POSITION_FB_MINE_NORMAL = 33;  //mine
    public static final int POSITION_FB_END_CALL_NORMAL = 44; //end call
    public static final int POSITION_FB_IN_DETAIL_NORMAL = 66; //插屏


    private static final String FB_ID_SPLASH_NORMAL = ""; //启动页
    private static final String FB_ID_RESULT_NORMAL = ""; //结果页
    private static final String FB_ID_MINE_NORMAL = "";  //mine
    private static final String FB_ID_END_CALL_NORMAL = ""; //end call
    private static final String FB_IN_DETAIL_NORMAL = ""; //插屏


    //facebook id end

    public static String getAdmob_id(int position) {
        String ad_id = "";
        SharedPreferences ad_pref = ApplicationEx.getInstance().getGlobalADPreference();
        try {
            String str_id = ad_pref.getString("normal_admob_id", null);
            if (TextUtils.isEmpty(str_id)) {
                return ad_id;
            }
            JSONObject jsonObject = new JSONObject(str_id);
            if (jsonObject != null) {
                switch (position) {
                    case POSITION_ADMOB_SPLASH_NORMAL:
                        ad_id = jsonObject.optString("ad_id_2");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = ADMOB_ID_ADV_SPLASH_NORMAL;
                        }
                        break;
                    case POSITION_ADMOB_RESULT_FIRST:
                        ad_id = jsonObject.optString("ad_id_3");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = ADMOB_ID_ADV_RESULT_FIRST;
                        }
                        break;
                    case POSITION_ADMOB_RESULT_NORMAL:
                        ad_id = jsonObject.optString("ad_id_4");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = ADMOB_ID_ADV_RESULT_NORMAL;
                        }
                        break;
                    case POSITION_ADMOB_MINE_NORMAL:
                        ad_id = jsonObject.optString("ad_id_7");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = ADMOB_ID_ADV_MINE_NORMAL;
                        }
                        break;
                    case POSITION_ADMOB_END_CALL_NORMAL:
                        ad_id = jsonObject.optString("ad_id_8");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = ADMOB_ID_ADV_END_CALL_NORMAL;
                        }
                        break;
                    case POSITION_ADMOB_ADV_SWIPE:
                        ad_id = jsonObject.optString("ad_id_9");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = ADMOB_ID_ADV_SWIPE;
                        }
                        break;
                    case POSITION_ADMOB_IN_DETAIL_FIRST:
                        ad_id = jsonObject.optString("ad_id_5");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = INTERSTITIAL_ADMOB_ID_IN_CALL_FALSH_DETAIL_FIRST;
                        }
                        break;
                    case POSITION_ADMOB_IN_DETAIL_NORMAL:
                        ad_id = jsonObject.optString("ad_id_6");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = INTERSTITIAL_ADMOB_ID_IN_CALL_FALSH_DETAIL_NORMAL;
                        }
                        break;
                }

                LogUtil.d("adv_id", "getAdmob_id ad_id: " + ad_id);
            }
        } catch (Exception e) {
            LogUtil.e("adv_id", " getAdmob_id exception: " + e.getMessage());
        }
        return ad_id;
    }

    public static String getFacebook_id(int position) {
        String ad_id = "";
        SharedPreferences ad_pref = ApplicationEx.getInstance().getGlobalADPreference();
        try {
            String str_id = ad_pref.getString("normal_facebook_id", null);
            if (TextUtils.isEmpty(str_id)) {
                return ad_id;
            }
            JSONObject jsonObject = new JSONObject(str_id);
            if (jsonObject != null) {
                switch (position) {
                    case POSITION_FB_SPLASH_NORMAL:
                        ad_id = jsonObject.optString("ad_id_1");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = FB_ID_SPLASH_NORMAL;
                        }
                        break;
                    case POSITION_FB_RESULT_NORMAL:
                        ad_id = jsonObject.optString("ad_id_2");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = FB_ID_RESULT_NORMAL;
                        }
                        break;
                    case POSITION_FB_MINE_NORMAL:
                        ad_id = jsonObject.optString("ad_id_3");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = FB_ID_MINE_NORMAL;
                        }
                        break;
                    case POSITION_FB_END_CALL_NORMAL:
                        ad_id = jsonObject.optString("ad_id_4");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = FB_ID_END_CALL_NORMAL;
                        }
                        break;
                    case POSITION_FB_IN_DETAIL_NORMAL:
                        ad_id = jsonObject.optString("ad_id_5");
                        if (TextUtils.isEmpty(ad_id)) {
                            ad_id = FB_IN_DETAIL_NORMAL;
                        }
                        break;
                }

                LogUtil.d("adv_id", "getFacebook_id: " + ad_id);
            }
        } catch (Exception e) {
            LogUtil.e("adv_id", " getFacebook_id: " + e.getMessage());
        }
        return ad_id;
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

    public static boolean isAutoGoMain() {
        boolean autoGoMain = true; //默认自动跳转到main
        SharedPreferences ad_pref = ApplicationEx.getInstance().getGlobalADPreference();
        int is = ad_pref.getInt("pref_is_auto_go_main", 0);//欧美发达国家 渠道量，不自动跳过is=1
        if (is == 1) {
            autoGoMain = false;
        }
        return autoGoMain;
    }

    public static boolean isShowAdOnEndCall() {
        boolean show = false;
        SharedPreferences ad_pref = ApplicationEx.getInstance().getGlobalADPreference();
        int is = ad_pref.getInt("pref_is_show_ad_end_call", 0);//1-渠道量才显示，默认不显示
        if (is == 1) {
            show = true;
        }
        return show;
    }

    //swipe fb id
    public static String getSwipeFbId() {
        String fb_id = ApplicationEx.getInstance().getGlobalADPreference().getString("pref_swipe_fb_id", "");
        return fb_id;
    }

    public static boolean isExternalOn() {
        boolean isOn = false;
        isOn = ApplicationEx.getInstance().getGlobalADPreference().getBoolean("pref_ext_isCommercialValid", false);
        return isOn;
    }

    public static int getExternalInterval() {
        int mins = 30;
        mins = ApplicationEx.getInstance().getGlobalADPreference().getInt("pref_ext_show_interval", 30);
        return mins;
    }

    public static ExternalParam getExternalParam(String key) {
        ExternalParam externalParam = new ExternalParam();
        SharedPreferences ext_pref = ApplicationEx.getInstance().getExtPreference();
        String str_param = ext_pref.getString(key, "");
        LogUtil.d("cp_external_param", "getExternalParam ext_type: " + str_param);
        if (!TextUtils.isEmpty(str_param)) {
            try {
                JSONObject jsonObject = new JSONObject(str_param);
                externalParam.mEnable = jsonObject.optBoolean("mEnable");
                externalParam.mDelayedDisplayRate = jsonObject.optInt("mDelayedDisplayRate");
                externalParam.mDelayTime = jsonObject.optInt("mDelayTime");
                externalParam.mSelfInterval = jsonObject.optInt("mSelfInterval");
                externalParam.mPopupNumber = jsonObject.optInt("mPopupNumber");
                externalParam.mDelayedDisplayTime = jsonObject.optInt("mDelayedDisplayTime");
                externalParam.mRestartDay = jsonObject.optInt("mRestartDay");
                externalParam.fb_id = jsonObject.optString("fb_id");
                externalParam.admob_id = jsonObject.optString("admob_id");
                LogUtil.d("cp_external_param", "getExternalParam mRestartDay: " + externalParam.mRestartDay);
                LogUtil.d("cp_external_param", "getExternalParam mEnable: " + externalParam.mEnable);
                LogUtil.d("cp_external_param", "getExternalParam fb_id: " + externalParam.fb_id);
                LogUtil.d("cp_external_param", "getExternalParam admob_id: " + externalParam.admob_id);
            } catch (Exception e) {
                LogUtil.e("cp_external_param", "getExternalParam ext_type exception: " + e.getMessage());
            }
        }
        return externalParam;
    }
}
