package blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.bumptech.glide.Glide;
import com.common.sdk.analytics.AnalyticsManager;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.callback.SingleTopicThemeCallback;

import java.util.Calendar;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.NotifyInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.NotifyManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ServerManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ServiceProcessingManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.SwipeManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

public class CallerCommonReceiver extends BroadcastReceiver {
    private static final String TAG = "cpservice";
    public final static String RANDOM_NOTIFY_TASK_24 = "android.intent.action.RANDOM_CALLER_24";
    public final static String RANDOM_NOTIFY_CALL_FLASH = "android.intent.action.RANDOM_CALL_FLASH";
    public final static String COMMON_CHECK_24 = "android.intent.action.COMMON_CHECK_24";

    @Override
    public void onReceive(final Context context, Intent intent) {
        // 接收通知广播
        try {
            if (COMMON_CHECK_24.equals(intent.getAction())) {

                Async.run(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d(TAG, "backgroundDownloadOnlionCallFlash COMMON_CHECK_24");
                        try {
                            ServiceProcessingManager.getInstance().cacheHomeData();
                            if ((System.currentTimeMillis() - PreferenceHelper.getLong("colorphone_inStall_time", System.currentTimeMillis())) >= 4 * 60 * 1000) {
                                ServerManager.getInstance().getParamFromServer();
                            }

                            updateNewFlash(context);
                            AnalyticsManager.onServiceRestart();

                            //判断如果安装了call id  则关闭 swipe
                            if (AdvertisementSwitcher.isAppInstalled(ConstantUtils.PACKAGE_CID)) {
                                SwipeManager.getInstance().disableEasySwipe();
                            }else{
                                SwipeManager.getInstance().checkSwipeService();
                                LogUtil.d(TAG, "checkSwipeService: ");
                            }
                        } catch (Exception e) {
                            LogUtil.e(TAG, "backgroundDownloadOnlionCallFlash COMMON_CHECK_24 exception: " + e.getMessage());
                        }

                    }
                });
            }

            if (RANDOM_NOTIFY_TASK_24.equals(intent.getAction())) {
            }

            //day dream
            if (Intent.ACTION_DREAMING_STARTED.equals(intent.getAction())) {
                LogUtil.d(TAG, "daydream started.");
                setDaydreamStatus(true);
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
                LogUtil.d(TAG, "Unlocked Screen");
                setDaydreamStatus(false);

                try {

                    Async.schedule(2800, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showNewFlashNotification(context);
                            } catch (Exception e) {
                                LogUtil.e(TAG, "showCallFlashNotify exception: " + e.getMessage());
                            }

                        }
                    });
                } catch (Exception e) {
                    LogUtil.e(TAG, "scheduleTaskOnUiThread exception: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "CommonReceiver execption: " + e.getMessage());
        }
    }

    private void showNewFlashNotification(Context context) {
        LogUtil.d(TAG, "showNewFlashNotification start");
        if (context == null)
            return;
        Theme theme = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.PREF_CALL_FLASH_NEWEST_INSTANCE, Theme.class);
        if (theme == null)
            return;

        if (isShowNewestFlashNotify(theme)) {
            LogUtil.d(TAG, "showNewFlashNotification show true: " + theme.getId());
            NotifyInfo info = new NotifyInfo();
            info.setNotifyId(NotifyInfo.NotifyId.NOTIFY_NEW_FLASH);
            info.setTitle(context.getString(R.string.notification_new_flash_title));
            info.arg1 = theme.getImg_h();
            info.arg2 = theme.getImg_v();

            CallFlashPreferenceHelper.setObject(CallFlashPreferenceHelper.PREF_CALL_FLASH_LAST_SEND_NOTIFY_NEWEST_INSTANCE, theme);
            NotifyManager.getInstance().showNewFlashWithBigStyle(info);
        }
    }

    private boolean isShowNewestFlashNotify(Theme newest) {
//        return true;
        boolean bool = false;

        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 15 && hourOfDay <= 22) {
            long installTime = PreferenceHelper.getLong(PreferenceHelper.PREF_KEY_INSTALL_TIME, System.currentTimeMillis());
            boolean isTodayInstall = Stringutil.isToday(installTime);

            long lastEnterAppTime = PreferenceHelper.getLong(PreferenceHelper.PREF_KEY_LAST_ENTER_APP_TIME, 0);
            boolean isTodayEnter = Stringutil.isToday(lastEnterAppTime);

            Theme lastSend = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.PREF_CALL_FLASH_LAST_SEND_NOTIFY_NEWEST_INSTANCE, Theme.class);
            boolean isSend = lastSend != null && newest.equals(lastSend);

            CallFlashInfo current = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, CallFlashInfo.class);
            boolean isCurrent = current != null && current.id.equals(String.valueOf(newest.getId()));

            // 不是当天安装, 并且当天没有进入过App, 并且没有发送过当前最新来电秀通知;
            bool = !isTodayInstall && !isTodayEnter && !isCurrent && !isSend;
        }
        return bool;
    }

    private void updateNewFlash(final Context context) {
        if (!CommonUtils.isOldForFlash()) {
            return;
        }
        ThemeSyncManager.getInstance().syncTopicData(CallFlashManager.ONLINE_THEME_TOPIC_NAME_NEW_FLASH, 12, new SingleTopicThemeCallback() {
            @Override
            public void onSuccess(int code, List<Theme> data) {
                LogUtil.d(TAG, "updateNewFlash finish.");
                if (data == null || data.isEmpty()) {
                    LogUtil.d(TAG, "updateNewFlash finish empty.");
                    return;
                }

                final Theme newest = data.get(0);
                LogUtil.d(TAG, "updateNewFlash newest: " + newest.getId());
                Theme savedNewest = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.PREF_CALL_FLASH_NEWEST_INSTANCE, Theme.class);
//                LogUtil.d(TAG, "updateNewFlash savedNewest: "+savedNewest.getId());
                if (savedNewest == null || (savedNewest != null && newest != null && !savedNewest.equals(newest))) {


                    Async.run(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LogUtil.d(TAG, "updateNewFlash newest 1: " + newest.getId());
                                final int width = DeviceUtil.getScreenWidth();
                                final int height = DeviceUtil.dp2Px(200);
                                Glide.with(context).load(newest.getImg_h()).downloadOnly(width, height).get();
                                Glide.with(context).load(newest.getImg_v()).downloadOnly(width, height).get();
                                LogUtil.d(TAG, "updateNewFlash newest 2: " + newest.getId());
                            } catch (Exception e) {
                                LogUtil.e(TAG, "Async updateNewFlash Exception: " + e.getMessage());
                            }
                        }
                    });

//                    LogUtil.d(TAG, "updateNewFlash save: "+newest.getId());
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