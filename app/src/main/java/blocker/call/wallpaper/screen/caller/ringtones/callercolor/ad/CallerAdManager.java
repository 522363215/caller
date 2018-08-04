package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.text.TextUtils;

import java.util.HashSet;
import java.util.Random;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
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
    public static final String ADMOB_ID_ADV_SPLASH_FIRST = "ca-app-pub-5980661201422605/3475594207";//启动页首次colorphone-1
    public static final String ADMOB_ID_ADV_SPLASH_NORMAL = "ca-app-pub-5980661201422605/5797261947";//启动页普通colorphone-2
    public static final String ADMOB_ID_ADV_RESULT_FIRST = "ca-app-pub-5980661201422605/6144638504";//结果页首次colorphone-3
    public static final String ADMOB_ID_ADV_RESULT_NORMAL = "ca-app-pub-5980661201422605/2125610249";//结果页普通colorphone-4
    public static final String ADMOB_ID_ADV_MINE_NORMAL = "ca-app-pub-5980661201422605/6560922541";//mine页普通colorphone-7
    public static final String ADMOB_ID_ADV_END_CALL_NORMAL = "ca-app-pub-5980661201422605/1776419593";//通话结束页colorphone-8


    //插屏
    public static final String INTERSTITIAL_ADMOB_ID_IN_CALL_FALSH_DETAIL_FIRST = "ca-app-pub-5980661201422605/4911447885"; //自定义插屏, 来电秀设置首次colorphone-5
    public static final String INTERSTITIAL_ADMOB_ID_IN_CALL_FALSH_DETAIL_NORMAL = "ca-app-pub-5980661201422605/5685581308";//来电秀设置插屏colorphone-6

    //admob id end

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
}
