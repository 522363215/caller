package blocker.call.wallpaper.screen.caller.ringtones.callercolor.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventForeground;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import event.EventBus;

public class LocalService extends Service {
    private static final String TAG = "LocalService";
    private static LocalService sInstance;

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(LocalService.this)) {
            EventBus.getDefault().unregister(LocalService.this);
        }
    }

    private boolean isForeground = false;

    public boolean getIsForeground() {
        return isForeground;
    }

    public void onEvent(EventForeground event) {
        isForeground = event.m_bIsForeground;
    }

}
