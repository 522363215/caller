package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.text.TextUtils;

import java.util.HashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.AdPreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.AppUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;

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
                admob_id = AdPreferenceHelper.getString("pref_first_mode_splash_admob_id", "");//ConstantUtils.ADMOB_ADV_FAKE_RESULT_ID
                break;
            case POSITION_FIRST_ADMOB_RESULT_FLASH_SET:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_result_flashset_admob_id", "");//ConstantUtils.ADMOB_ADV_SMS_EDIT_ID
                break;
            case POSITION_FIRST_ADMOB_CALL_FLASH_PREVIEW:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_callflash_preview_admob_id", "");//来电秀预览preview
                break;
            case POSITION_FIRST_ADMOB_CALL_FLASH_DETAIL:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_callflash_download_admob_id", "");//来电秀下载detail
                break;
            case POSITION_FIRST_ADMOB_FULL_SCREEN_CALL_FLASH_DETAIL:
                admob_id = AdPreferenceHelper.getString("pref_first_mode_flash_down_onback_admob_id", "");//call flash detail 界面全屏广告. 自定义插屏
                break;
        }
        return admob_id;
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
}
