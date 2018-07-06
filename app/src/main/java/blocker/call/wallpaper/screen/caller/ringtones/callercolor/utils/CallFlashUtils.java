package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import com.md.flashset.helper.CallFlashPreferenceHelper;

import java.util.HashSet;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;

public class CallFlashUtils {
    public boolean isShowTipsExit() {
        boolean show = true;
        if (CallFlashPreferenceHelper.getInt(CallFlashPreferenceHelper.IS_SHOW_TIP_CALLFLASH_EXIT, 0) != 0) {
            show = false;
        }
        if (show) {
            if (getNotShowCallFlashTipsExit().contains(ApplicationEx.getInstance().country)) {
                show = false;
            }
        }
        return show;
    }

    public boolean isEnableCallFlashDefault() {
        boolean enable = false;
        if (CallFlashPreferenceHelper.getInt(CallFlashPreferenceHelper.IS_SHOW_TIP_CALLFLASH_EXIT, 0) != 0) {
            enable = true;
        }
        if (enable) {
            if (getNotEnableCallFlashDefault().contains(ApplicationEx.getInstance().country)) {
                enable = false;
            }
        }
        return enable;
    }

    //Newflash, 从通知
    public boolean isOldForFlash() {
        boolean old = false;
        if (CallFlashPreferenceHelper.getBoolean("cid_is_old_user_flash", false)) {
            old = true;
        } else {
            long install = CallFlashPreferenceHelper.getLong("cid_inStall_time", System.currentTimeMillis());
            long enter_flash = CallFlashPreferenceHelper.getLong(CallFlashPreferenceHelper.CALL_FLASH_LAST_ENTER_TIME, 0);
            int enter_flash_day = Stringutil.getDayByTime(enter_flash);
            int today = Stringutil.getTodayDayByServer();//real time
//                int today = Stringutil.getTodayDayInYearLocal();//not real for test
            int install_day = Stringutil.getDayByTime(install);
            if (today != 0 && enter_flash != 0 && Math.abs(today - install_day) >= 2) {
                old = true;
                CallFlashPreferenceHelper.putBoolean("cid_is_old_user_flash", true);
            }
        }
        return old;
    }

    private static HashSet<String> getNotShowCallFlashTipsExit() {
        String countries = CallFlashPreferenceHelper.getString(CallFlashPreferenceHelper.NOT_SHOW_TIP_CALLFLASH_EXIT_LIST, "");
        HashSet<String> sets = ConstantUtils.getControlSet(countries);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "NotShowCallFlashTipsExit: " + cc);
            }
        }
        return sets;
    }

    private static HashSet<String> getNotEnableCallFlashDefault() {
        String countries = CallFlashPreferenceHelper.getString(CallFlashPreferenceHelper.NOT_ENABLE_CALLFLASH_DEFAULT_LIST, "");
        HashSet<String> sets = ConstantUtils.getControlSet(countries);
        if (BuildConfig.DEBUG) {
            for (String cc : sets) {
                LogUtil.d("cidserver", "NotEnableCallFlashDefault: " + cc);
            }
        }
        return sets;
    }
}
