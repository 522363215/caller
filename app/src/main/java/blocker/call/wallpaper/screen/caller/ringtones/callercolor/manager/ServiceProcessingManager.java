package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.md.block.core.BlockManager;
import com.md.flashset.CallFlashSet;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PhoneStateListenImpl;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.CallerCommonReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.MessageReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.NetworkConnectChangedReceiver;

/**
 * 服务中数据处理管理类
 */
public class ServiceProcessingManager {
    private static final String TAG = "ServiceProcessingManager";
    private static ServiceProcessingManager sInstance;
    private NetworkConnectChangedReceiver mNetworkChangeListener;
    private MessageReceiver mMessageReceiver;
    private CallerCommonReceiver mCallerCommonReceiver;

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
        //保存安装时间
        if (PreferenceHelper.getLong(PreferenceHelper.PREF_KEY_INSTALL_TIME, 0) <= 0) {
            PreferenceHelper.putLong(PreferenceHelper.PREF_KEY_INSTALL_TIME, System.currentTimeMillis());
        }
        registerReceivers(context);
        initBlock();
        CallFlashSet.init(context);
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
        context.unregisterReceiver(mNetworkChangeListener);
        context.unregisterReceiver(mCallerCommonReceiver);
        context.unregisterReceiver(mMessageReceiver);
    }

    private void initBlock() {
        Context appContext = ApplicationEx.getInstance().getApplicationContext();
        BlockManager.getInstance().initialize(appContext);
        BlockManager.getInstance().registerPhoneReceiver(appContext);
        BlockManager.addPhoneStateListener(new PhoneStateListenImpl(appContext));
    }

    public void destroy(Context context) {
        unregisterReceivers(context);
    }
}
