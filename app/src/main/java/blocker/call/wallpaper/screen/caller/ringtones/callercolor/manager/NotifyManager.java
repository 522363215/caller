package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import com.flurry.android.FlurryAgent;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.BlockActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.MainActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.NotifyInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

public class NotifyManager {
    private static final String TAG = "NotifyManager";

    private static final String ANDROID_O_CHANNEL_ID = "subscribe";
    private static final String GROUP_ID = "blocker.call.wallpaper.screen.caller.ringtones.callercolor.custom_notify";

    private Context mContext;
    private NotificationManager mNotificationManager;

    private static NotifyManager instance;

    public static NotifyManager getInstance(Context context) {
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
            LogUtil.e(TAG, "NotifyManager context parameter is null.");
        }
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String notifyChannelName = mContext.getString(R.string.notify_channel_subscribe);
            NotificationChannel channel = new NotificationChannel(ANDROID_O_CHANNEL_ID, notifyChannelName, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    private RemoteViews createRemoteViews() {
        String pkgName = ApplicationEx.getInstance().getPackageName();
        RemoteViews remoteViews = new RemoteViews(pkgName, R.layout.push_tools_bar);

        boolean isLayoutReverse = LanguageSettingUtil.isLayoutReverse(mContext);
        remoteViews.setInt(R.id.layout_root, "setLayoutDirection", isLayoutReverse ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        return remoteViews;
    }

    public void showNotify(NotifyInfo info) {
        if (info == null) {
            LogUtil.e(TAG, "NotifyInfo is null.");
            return;
        }

        String title = info.getTitle();
        String content = info.getContent();
        RemoteViews views = createRemoteViews();
        PendingIntent pendingIntent = null;
        Intent jumpIntent = null;
        switch (info.getNotifyId()) {
            case NotifyInfo.NotifyId.NOTIFY_BLOCK_CALL:
                views.setTextViewText(R.id.tv_notify_title, title);
                views.setTextViewText(R.id.tv_notify_content, content);

                views.setViewVisibility(R.id.layout_notify_action, View.VISIBLE);
                views.setViewVisibility(R.id.btn_notify, View.GONE);
                views.setTextViewText(R.id.tv_notify_time, info.arg1);

                jumpIntent = new Intent();
                jumpIntent.setClass(mContext, BlockActivity.class);

                pendingIntent = PendingIntent.getActivity(mContext, NotifyInfo.NotifyId.NOTIFY_BLOCK_CALL,
                        jumpIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                FlurryAgent.logEvent("notification_show_block");
                break;
            case NotifyInfo.NotifyId.NOTIFY_NEW_FLASH:
                views.setTextViewText(R.id.tv_notify_title, title);
                views.setTextViewText(R.id.tv_notify_content, content);

                jumpIntent = new Intent();
                jumpIntent.setClass(mContext, MainActivity.class);

                pendingIntent = PendingIntent.getActivity(mContext, NotifyInfo.NotifyId.NOTIFY_BLOCK_CALL,
                        jumpIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                FlurryAgent.logEvent("notification_new_call_flash");
                break;
        }

        Notification.Builder builder = new Notification.Builder(mContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setCustomContentView(views);
        } else {
            builder.setContent(views);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(ANDROID_O_CHANNEL_ID);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            builder.setGroup(GROUP_ID);
        }
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            builder.setSmallIcon(R.drawable.notification_small_icon);
//        } else {
//            builder.setSmallIcon(R.drawable.notification_icon);
//        }

        builder.setAutoCancel(true)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent);

        Notification notify = builder.build();
        mNotificationManager.notify(info.getNotifyId(), notify);

    }

}
