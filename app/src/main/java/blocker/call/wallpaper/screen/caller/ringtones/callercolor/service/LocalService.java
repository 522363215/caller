package blocker.call.wallpaper.screen.caller.ringtones.callercolor.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.md.block.core.BlockManager;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventForeground;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PhoneStateListenImpl;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.NetworkConnectChangedReceiver;
import event.EventBus;

public class LocalService extends Service {
    private static final String TAG = "LocalService";
    private static LocalService sInstance;
    private NetworkConnectChangedReceiver mNetworkChangeListener;

    public LocalService() {
    }

    public static LocalService getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        if (!EventBus.getDefault().isRegistered(LocalService.this)) {
            EventBus.getDefault().register(LocalService.this);
        }
        registerReceivers();
        initBlock();
    }

    private void registerReceivers() {
        //网络变化广告
        mNetworkChangeListener = new NetworkConnectChangedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(mNetworkChangeListener, filter);
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
        if (EventBus.getDefault().isRegistered(LocalService.this)) {
            EventBus.getDefault().unregister(LocalService.this);
        }
        unregisterReceiver(mNetworkChangeListener);
    }

    private boolean isForeground = false;

    public boolean getIsForeground() {
        return isForeground;
    }

    public void onEvent(EventForeground event) {
        isForeground = event.m_bIsForeground;
    }

}
