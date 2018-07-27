package blocker.call.wallpaper.screen.caller.ringtones.callercolor.service;

import android.app.Service;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.md.block.core.BlockManager;
import com.md.flashset.CallFlashSet;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PhoneStateListenImpl;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ServiceProcessingManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.CallerCommonReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.MessageReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.NetworkConnectChangedReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

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
        LogUtil.d(TAG, "LocalService onCreate");
        sInstance = this;
        //服务中所有的初始化工作都要写在这里里面，8.0以上是在另一个服务中进行，防止漏写
        ServiceProcessingManager.getInstance().create(getApplicationContext());
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "LocalService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "LocalService onDestroy");
        ServiceProcessingManager.getInstance().destroy(getApplicationContext());
    }
}
