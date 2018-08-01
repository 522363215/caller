package blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.bumptech.glide.Glide;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.callback.SingleTopicThemeCallback;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.NotifyInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.NotifyManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

public class CallerCommonReceiver extends BroadcastReceiver {
    private static final String TAG = "cpservice";
    public final static String RANDOM_NOTIFY_TASK_24 = "android.intent.action.RANDOM_CALLER_24";
    public final static String RANDOM_NOTIFY_CALL_FLASH = "android.intent.action.RANDOM_CALL_FLASH";
    public final static String COMMON_CHECK_24 = "android.intent.action.COMMON_CHECK_24";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 接收通知广播
        try {
            if (COMMON_CHECK_24.equals(intent.getAction())) {
                LogUtil.d(TAG, "backgroundDownloadOnlionCallFlash COMMON_CHECK_24");
                updateNewFlash(context);
            }

            if (RANDOM_NOTIFY_TASK_24.equals(intent.getAction())) {
            }

            //day dream
            if (Intent.ACTION_DREAMING_STARTED.equals(intent.getAction())) {
                LogUtil.d(TAG, "daydream started.");
                setDaydreamStatus(true);
                showNewFlashNotification(context);
            }
            //day dream
            if (Intent.ACTION_DREAMING_STOPPED.equals(intent.getAction())) {
                LogUtil.d(TAG, "daydream stoped.");
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

    private void showNewFlashNotification(Context context) {
        if (context == null)
            return;
        Theme theme = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.PREF_CALL_FLASH_NEWEST_INSTANCE, Theme.class);
        if (theme == null)
            return;

        if (isShowNewestFlashNotify(theme)) {
            NotifyInfo info = new NotifyInfo();
            info.setNotifyId(NotifyInfo.NotifyId.NOTIFY_NEW_FLASH);
            info.setTitle(context.getString(R.string.notification_new_flash_title));
            info.arg1 = theme.getImg_v();
            info.arg2 = theme.getImg_h();

            CallFlashPreferenceHelper.setObject(CallFlashPreferenceHelper.PREF_CALL_FLASH_LAST_SEND_NOTIFY_NEWEST_INSTANCE, info);
            NotifyManager.getInstance(context).showNewFlashWithBigStyle(info);
        }
    }

    private boolean isShowNewestFlashNotify (Theme newest) {
        boolean bool = false;

        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 15 && hourOfDay <= 22) {
            long installTime = PreferenceHelper.getLong(PreferenceHelper.PREF_KEY_INSTALL_TIME, System.currentTimeMillis());
            boolean isTodayInstall = Stringutil.isToday(installTime);

            long lastEnterAppTime = PreferenceHelper.getLong(PreferenceHelper.PREF_KEY_LAST_ENTER_APP_TIME, System.currentTimeMillis());
            boolean isTodayEnter = Stringutil.isToday(lastEnterAppTime);

            Theme lastSend = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.PREF_CALL_FLASH_LAST_SEND_NOTIFY_NEWEST_INSTANCE, Theme.class);
            boolean isSend = lastSend != null && newest.equals(lastSend);

            // 不是当天安装, 并且当天没有进入过App, 并且没有发送过当前最新来电秀通知;
            bool = !isTodayInstall && !isTodayEnter && !isSend;
        }
        return bool;
    }

    private void updateNewFlash(final Context context) {
        ThemeSyncManager.getInstance().syncTopicData(CallFlashManager.ONLINE_THEME_TOPIC_NAME_NEW_FLASH, 12, new SingleTopicThemeCallback() {
            @Override
            public void onSuccess(int code, List<Theme> data) {
                if (data == null || data.isEmpty()) {
                    return;
                }

                Theme newest = data.get(0);
                Theme savedNewest = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.PREF_CALL_FLASH_NEWEST_INSTANCE, Theme.class);
                if (savedNewest != null && newest != null && !savedNewest.equals(newest)) {
                    try {
                        int width = DeviceUtil.getScreenWidth();
                        int height = DeviceUtil.dp2Px(200);
                        Glide.with(context).load(newest.getImg_h()).downloadOnly(width, height).get();
                        Glide.with(context).load(newest.getImg_v()).downloadOnly(width, height).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    CallFlashPreferenceHelper.setObject(CallFlashPreferenceHelper.PREF_CALL_FLASH_NEWEST_INSTANCE, newest);
                }
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    private void setDaydreamStatus(boolean isDaydream) {
        PreferenceHelper.putBoolean(PreferenceHelper.PREF_KEY_DAY_DREAM_STATUS, isDaydream);
    }
}