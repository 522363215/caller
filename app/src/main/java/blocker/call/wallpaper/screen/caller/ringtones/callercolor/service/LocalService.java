package blocker.call.wallpaper.screen.caller.ringtones.callercolor.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.md.block.core.BlockManager;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PhoneStateListenImpl;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.CallerCommonReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.MessageReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.NetworkConnectChangedReceiver;
import event.EventBus;

public class LocalService extends Service {
    private static final String TAG = "LocalService";
    private static LocalService sInstance;
    private NetworkConnectChangedReceiver mNetworkChangeListener;
    private MessageReceiver mMessageReceiver;
    private CallerCommonReceiver mCallerCommonReceiver;

    public LocalService() {
    }

    public static LocalService getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        registerReceivers();
        initBlock();
    }

    private void registerReceivers() {
        //网络变化广播
        mNetworkChangeListener = new NetworkConnectChangedReceiver();
        IntentFilter networkChangefilter = new IntentFilter();
        networkChangefilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangefilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        networkChangefilter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(mNetworkChangeListener, networkChangefilter);

        //接收短信的广播
        mMessageReceiver = new MessageReceiver();
        IntentFilter messageFilter = new IntentFilter();
        messageFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mMessageReceiver, messageFilter);


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
        this.registerReceiver(mCallerCommonReceiver, commonFilter);
    }

    private void initBlock() {
        Context appContext = ApplicationEx.getInstance().getApplicationContext();
        BlockManager.getInstance().initialize(appContext);
        BlockManager.getInstance().registerPhoneReceiver(appContext);
        BlockManager.addPhoneStateListener(new PhoneStateListenImpl(appContext));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkChangeListener);
    }
}
