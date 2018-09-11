package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.text.TextUtils;

import java.util.HashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.AdPreferenceHelper;

public class FirstShowAdmobUtil {
    public final static int POSITION_FIRST_ADMOB_SPLASH = 1;//启动页
    public final static int POSITION_FIRST_ADMOB_RESULT_FLASH_SET = 2;//来电秀设置结果页
    public final static int POSITION_FIRST_ADMOB_CALL_FLASH_PREVIEW = 3;//call flash preview 界面
    public final static int POSITION_FIRST_ADMOB_CALL_FLASH_DETAIL = 4;//call flash detail 界面
    public final static int POSITION_FIRST_ADMOB_FULL_SCREEN_CALL_FLASH_DETAIL = 5;//call flash detail 界面全屏广告

    public static String getAdmobIdForFirst(int position_first) {
        String admob_id = "";
        switch (position_first) {
            case POSITION_FIRST_ADMOB_SPLASH:
                admob_id = CallerAdManager.ADMOB_ID_ADV_SPLASH_FIRST;
                break;
            case POSITION_FIRST_ADMOB_RESULT_FLASH_SET:
                admob_id = CallerAdManager.getAdmob_id(CallerAdManager.POSITION_ADMOB_RESULT_FIRST);
                if (TextUtils.isEmpty(admob_id)) {
                    admob_id = CallerAdManager.ADMOB_ID_ADV_RESULT_FIRST;
                }
                break;
            case POSITION_FIRST_ADMOB_CALL_FLASH_PREVIEW:
                break;
            case POSITION_FIRST_ADMOB_CALL_FLASH_DETAIL:
                break;
            case POSITION_FIRST_ADMOB_FULL_SCREEN_CALL_FLASH_DETAIL:
                admob_id = CallerAdManager.getAdmob_id(CallerAdManager.POSITION_ADMOB_IN_DETAIL_FIRST);//call flash detail 界面全屏广告. 自定义插屏
                if (TextUtils.isEmpty(admob_id)) {
                    admob_id = CallerAdManager.INTERSTITIAL_ADMOB_ID_IN_CALL_FALSH_DETAIL_FIRST;
                }
                break;
        }
        return admob_id;
    }

    public static boolean isShowFirstAdMob(int position_first) {
        if (ApplicationEx.getInstance() == null) {
            return false;
        }

//        if (AppUtils.isUpdateUser()) {
//            return false;
//        }

        String admobId = getAdmobIdForFirst(position_first);
        if (TextUtils.isEmpty(admobId)) {
            return false;
        }

        //该位置上一次显示的时间
        HashMap<String, Long> data = AdPreferenceHelper.getHashMapData(AdPreferenceHelper.PREF_LAST_SHOW_FIRST_ADMOB_TIME_MAP, Long.class);
        long lastShowTime = data.get(String.valueOf(position_first)) == null ? 0 : data.get(String.valueOf(position_first));
        if (lastShowTime > 0) {
            return false;
        }

        if (FirstShowAdmobUtil.POSITION_FIRST_ADMOB_SPLASH == position_first) {
            return true;
        } else {
            // TODO: 2018/9/11 只有启动页才才采用首次启动模式，其他位置的暂时取消首次启动模式，故返回false 
            return false;
        }
    }

    public static void saveFirstShowAdmobTime(int position_first) {
        HashMap<String, Long> data = AdPreferenceHelper.getHashMapData(AdPreferenceHelper.PREF_LAST_SHOW_FIRST_ADMOB_TIME_MAP, Long.class);
        if (data == null) {
            data = new HashMap<>();
        }
        data.put(String.valueOf(position_first), System.currentTimeMillis());
        AdPreferenceHelper.putHashMapData(AdPreferenceHelper.PREF_LAST_SHOW_FIRST_ADMOB_TIME_MAP, data);
    }
}
