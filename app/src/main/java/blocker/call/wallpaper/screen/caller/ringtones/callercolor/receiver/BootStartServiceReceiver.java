package blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.LocalService;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

public class BootStartServiceReceiver extends BroadcastReceiver {
    private final String TAG = "cpservice";


    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "BootStartServiceReceiver start:");
        try {
            startService(context);
        }catch (Exception e){
            LogUtil.d(TAG, "BootStartServiceReceiver startservcie exception:"+e.getMessage());
        }
    }

    private void startService(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            LogUtil.d(TAG, "BootStartServiceReceiver service start under 8:");
            Intent intent = new Intent(context, LocalService.class);
            context.startService(intent);
        }
    }


}
