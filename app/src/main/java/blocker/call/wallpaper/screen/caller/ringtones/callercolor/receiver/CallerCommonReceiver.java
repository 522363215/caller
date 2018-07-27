package blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

public class CallerCommonReceiver extends BroadcastReceiver {
    private static final String TAG = "CallerCommonReceiver";
    public final static String RANDOM_NOTIFY_TASK_24 = "android.intent.action.RANDOM_CALLER_24";
    public final static String RANDOM_NOTIFY_CALL_FLASH = "android.intent.action.RANDOM_CALL_FLASH";
    public final static String COMMON_CHECK_24 = "android.intent.action.COMMON_CHECK_24";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 接收通知广播
        try {
            if (COMMON_CHECK_24.equals(intent.getAction())) {
                LogUtil.d("CommonReceiver", "backgroundDownloadOnlionCallFlash COMMON_CHECK_24");
            }

            if (RANDOM_NOTIFY_TASK_24.equals(intent.getAction())) {

            }

            //day dream
            if (Intent.ACTION_DREAMING_STARTED.equals(intent.getAction())) {
                LogUtil.d("CommonReceiver", "daydream started.");
                setDaydreamStatus(true);
            }
            //day dream
            if (Intent.ACTION_DREAMING_STOPPED.equals(intent.getAction())) {
                LogUtil.d("CommonReceiver", "daydream stoped.");
                setDaydreamStatus(false);
            }
            //充电
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                switch (intent.getIntExtra("plugged", -1)) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                    case BatteryManager.BATTERY_PLUGGED_USB:
                    case BatteryManager.BATTERY_PLUGGED_WIRELESS:
//                        LogUtil.d("CommonReceiver", "BATTERY_CHANGED.");
//                            mIsCharging = true;
                        break;
                    case -1:
                    default:
//                            mIsCharging = false;
                        break;
                }
            }

            //解锁
            if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
                LogUtil.d("CommonReceiver", "Unlocked Screen");
                setDaydreamStatus(false);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "CommonReceiver execption: " + e.getMessage());
        }
    }

    private void setDaydreamStatus(boolean isDaydream) {
        PreferenceHelper.putBoolean(PreferenceHelper.PREF_KEY_DAY_DREAM_STATUS, isDaydream);
    }
}