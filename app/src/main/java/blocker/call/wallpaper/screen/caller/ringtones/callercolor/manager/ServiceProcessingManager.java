package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.text.TextUtils;

import com.md.block.core.BlockManager;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PhoneStateListenImpl;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.CallerCommonReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.MessageReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.NetworkConnectChangedReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.EncryptionUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.FileUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

/**
 * 服务中数据处理管理类
 */
public class ServiceProcessingManager {
    private static final String TAG = "cpservice";
    private static ServiceProcessingManager sInstance;
    private NetworkConnectChangedReceiver mNetworkChangeListener;
    private MessageReceiver mMessageReceiver;
    private CallerCommonReceiver mCallerCommonReceiver;
    private Context mContext;

    private ServiceProcessingManager() {

    }

    public static ServiceProcessingManager getInstance() {
        if (sInstance == null) {
            synchronized (ServiceProcessingManager.class) {
                sInstance = new ServiceProcessingManager();
            }
        }
        return sInstance;
    }

    public void create(Context context) {
        mContext = context;
        //保存安装时间
        if (PreferenceHelper.getLong(PreferenceHelper.PREF_KEY_INSTALL_TIME, 0) <= 0) {
            PreferenceHelper.putLong(PreferenceHelper.PREF_KEY_INSTALL_TIME, System.currentTimeMillis());
        }
        registerReceivers(context);
        initBlock();

        startWaketask2(); //long task

        //缓存首页数据
        cacheHomeData();

        //加密所有已经下载的视频
        encryptAllVideo();
    }

    private void encryptAllVideo() {
        Async.run(new Runnable() {
            @Override
            public void run() {
                File oldMediaDir = null;
                File newMediaDir = null;
                if (CommonUtils.isOldForFlash()) {
                    try {
                        oldMediaDir = mContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
                        newMediaDir = mContext.getExternalFilesDir(ThemeSyncManager.toHexStr(Environment.DIRECTORY_MOVIES));
                        if (oldMediaDir == null || !oldMediaDir.exists()) {
                            newMediaDir.mkdir();
                        }
                        File pictureDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        if (pictureDir != null && pictureDir.exists()) {
                            boolean delete = pictureDir.delete();
                            LogUtil.d("chenr", "Color Phone: delete old pictures directory is success, " + delete);
                        }
                    } catch (Exception e) {
                    }
                }

                List<CallFlashInfo> downloadedCallFlashs = CallFlashManager.getInstance().getDownloadedCallFlash();
                for (CallFlashInfo info : downloadedCallFlashs) {

                    boolean isMoveSuc = false;

                    File oldFile = ThemeSyncManager.getInstance().getOldThemeFileByUrl(mContext, info.url);
                    File nowFile = ThemeSyncManager.getInstance().getFileByUrl(mContext, info.url);
                    if (oldFile != null && oldFile.exists() && nowFile != null && !nowFile.exists()) {
                        try {
                            if (nowFile.createNewFile()) {
                                FileUtil.copyFile(oldFile, nowFile);
                                isMoveSuc = true;
                            }
                        } catch (Exception e) {
                        }
                    }

                    LogUtil.d("chenr", "Color Phone: move old media file is success, " + isMoveSuc);

                    if (isMoveSuc && !TextUtils.isEmpty(info.path) && !EncryptionUtil.isEncrypted(info.path)) {
                        //保存视频第一帧图片
                        CallFlashManager.getInstance().saveVideoFirstFrame(info.url);
                        //加密
                        EncryptionUtil.encrypt(info.path);
                    }

                    if (nowFile != null && oldFile != null && !nowFile.getAbsolutePath().equals(oldFile.getAbsolutePath())) {
                        CallFlashManager.getInstance().saveCallFlashDownloadCount(info);
                    }
                }

                if (oldMediaDir != null && oldMediaDir.exists()) {
                    boolean delete = oldMediaDir.delete();
                    LogUtil.d("chenr", "Color Phone: delete old media file is success, " + delete);
                }
            }
        });
    }

    public void cacheHomeData() {
        ThemeSyncManager.getInstance().syncTopicData(new String[]{CallFlashManager.ONLINE_THEME_TOPIC_NAME_NEW_FLASH}, 6, null);

        ThemeSyncManager.getInstance().syncTopicData(new String[]{ConstantUtils.HOME_DATA_TYPE}, 150, null);
    }

    private void registerReceivers(Context context) {
        //网络变化广播
        mNetworkChangeListener = new NetworkConnectChangedReceiver();
        IntentFilter networkChangefilter = new IntentFilter();
        networkChangefilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangefilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        networkChangefilter.addAction("android.net.wifi.STATE_CHANGE");
        context.registerReceiver(mNetworkChangeListener, networkChangefilter);

        //接收短信的广播
        mMessageReceiver = new MessageReceiver();
        IntentFilter messageFilter = new IntentFilter();
        messageFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        context.registerReceiver(mMessageReceiver, messageFilter);

        //常用功能广播
        IntentFilter commonFilter = new IntentFilter(); //for notification check
        commonFilter.addAction(CallerCommonReceiver.RANDOM_NOTIFY_TASK_24);
        commonFilter.addAction(CallerCommonReceiver.COMMON_CHECK_24);
        commonFilter.addAction(CallerCommonReceiver.RANDOM_NOTIFY_CALL_FLASH);
        //daydream
        commonFilter.addAction(Intent.ACTION_DREAMING_STARTED);
        commonFilter.addAction(Intent.ACTION_DREAMING_STOPPED);
        commonFilter.addAction(Intent.ACTION_USER_PRESENT);
        commonFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mCallerCommonReceiver = new CallerCommonReceiver();
        context.registerReceiver(mCallerCommonReceiver, commonFilter);

    }

    private void unregisterReceivers(Context context) {
        try {
            context.unregisterReceiver(mNetworkChangeListener);
            context.unregisterReceiver(mCallerCommonReceiver);
            context.unregisterReceiver(mMessageReceiver);
        } catch (Exception e) {
            LogUtil.e(TAG, " unregisterReceivers: " + e.getMessage());
        }

    }

    private void initBlock() {
        Context appContext = ApplicationEx.getInstance().getApplicationContext();
        BlockManager.getInstance().initialize(appContext);
        BlockManager.getInstance().registerPhoneReceiver(appContext);
        BlockManager.addPhoneStateListener(new PhoneStateListenImpl(appContext));
    }

    private void startWaketask2() {
        long scheduled_interval = 2 * 45 * 60 * 1000; //2 hours - 90 mins
//        long scheduled_interval = 5 * 60 * 1000; //5, 20 mins // for test
//        long timeDelay = 18 * 60 * 1000 * 60 + 30 * 60 * 1000 + 30 * 1000; // 18:30 PM
//        long timeDelay = 60 * 1000;

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        ca.set(Calendar.HOUR_OF_DAY, 0);

        long commonTriggerAtTime = System.currentTimeMillis() + 60 * 1000; //start at 60 secs later

        String filter = CallerCommonReceiver.COMMON_CHECK_24;

        int requestCode = 22;

        startScheduledTask(filter, commonTriggerAtTime, scheduled_interval, requestCode);

    }

    private void startScheduledTask(String taskFilter, long startTime, long interval, int requestCode) {

        AlarmManager commonAlarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intentCommon1 = new Intent(taskFilter);
        int commonRequestCode1 = requestCode;

        PendingIntent pendIntentCommon1 = PendingIntent.getBroadcast(mContext, commonRequestCode1, intentCommon1,
                PendingIntent.FLAG_UPDATE_CURRENT);

        commonAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendIntentCommon1);
        LogUtil.d(TAG, " startScheduledTask: " + taskFilter);
    }

    public void destroy(Context context) {
        unregisterReceivers(context);
    }
}
