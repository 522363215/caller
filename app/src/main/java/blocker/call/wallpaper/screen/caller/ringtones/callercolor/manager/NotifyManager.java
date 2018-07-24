package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import android.app.NotificationManager;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;

public class NotifyManager {

    private Context mContext;
    private NotificationManager mNotificationManager;

    private static NotifyManager instance;
    public static NotifyManager getInstance (Context context) {
        if (instance == null) {
            synchronized (NotifyManager.class) {
                if (instance == null) {
                    instance = new NotifyManager(context);
                }
            }
        }
        return instance;
    }
    private NotifyManager(Context context) {
        if (context == null) {
            throw new NullPointerException("NotifyManager context parameter is null.");
        }
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private RemoteViews createRemoteViews () {
        String pkgName = ApplicationEx.getInstance().getPackageName();
        RemoteViews remoteViews = new RemoteViews(pkgName, R.layout.push_tools_bar);

        boolean isLayoutReverse = LanguageSettingUtil.isLayoutReverse(mContext);
        remoteViews.setInt(R.id.layout_root, "setLayoutDirection", isLayoutReverse ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        return remoteViews;
    }

    public void showNotify () {

    }

}
