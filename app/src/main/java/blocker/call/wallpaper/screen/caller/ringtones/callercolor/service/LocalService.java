package blocker.call.wallpaper.screen.caller.ringtones.callercolor.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ServerManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ServiceProcessingManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.CallerCommonReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.MessageReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver.NetworkConnectChangedReceiver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.StatisticsUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

public class LocalService extends Service {
    private static final String TAG = "LocalService";
    private static LocalService sInstance;
    private NetworkConnectChangedReceiver mNetworkChangeListener;
    private MessageReceiver mMessageReceiver;
    private CallerCommonReceiver mCallerCommonReceiver;

    private static ExecutorService caServiceThreadPool = Executors.newFixedThreadPool(4);

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

        sendData(); //Statistics
        ServerManager.getInstance().getParamFromServer();
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        LogUtil.d(TAG, "LocalService onStartCommand");
//        Notification notification = null;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            notification = new Notification();
//            notification.icon = R.drawable.ic_launcher;
//            try {
//                Method deprecatedMethod = notification.getClass().getMethod("setLatestEventInfo", Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
//                deprecatedMethod.invoke(notification, this, "Foreground Service Started.", "Foreground service", null);
//            } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
//                    | InvocationTargetException e) {
//                LogUtil.e(TAG, "Local service onstart Method not found");
//            }
//        } else {
//            // Use new API
//            Notification.Builder builder = new Notification.Builder(this)
//                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
//                    .setSmallIcon(R.drawable.notification_small_icon)
//                    .setAutoCancel(true)
//                    .setContentTitle(getString(R.string.app_name));
//            notification = builder.build();
//        }
//
//        startForeground(0, notification);
        return Service.START_STICKY;
    }

    private class sendBaseThread implements Runnable {
        @Override
        public void run() {
            StatisticsUtil.sendBaseData(ApplicationEx.getInstance());
            LogUtil.d("ca_service", "sendBaseThread sent");
        }
    }

    private class sendMainThread implements Runnable {
        @Override
        public void run() {
            StatisticsUtil.sendMainData(ApplicationEx.getInstance());
            LogUtil.d("ca_service", "sendMainThread sent");
        }
    }

    private void sendData() {
        if (ApplicationEx.getInstance() != null) {
            //发送统计
            SharedPreferences setting = ApplicationEx.getInstance().getGlobalSettingPreference();
            int used_day = setting.getInt("used_day_base", 0);
            if (used_day != Stringutil.getTodayDayInYearGMT8()) {
                CommonUtils.wrappedSubmit(caServiceThreadPool, new sendBaseThread());
            }

            //发送统计 main
            int used_day_main = setting.getInt("used_day", 0);
            if (used_day_main != Stringutil.getTodayDayInYearGMT8()) {

                CommonUtils.wrappedSubmit(caServiceThreadPool, new sendMainThread());
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "LocalService onDestroy");
        ServiceProcessingManager.getInstance().destroy(getApplicationContext());
    }
}
