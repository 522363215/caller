package com.md.block.core.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.md.block.R;
import com.md.block.core.local.BlockLocal;
import com.md.block.util.LogUtil;

import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CallerNotificationListenerService extends NotificationListenerService {
    public final static String CHINESE = "zh_rCN";

    private static PendingIntent answer = null;

    private Messenger myMessenger = new Messenger(new MyHandler());

    @Override
    public void onCreate() {
        super.onCreate();
        BlockLocal.setPreferencesData("is_cc_callernotificationlistenerservice_running", true);
        LogUtil.d("cpservice", "start.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myMessenger.getBinder();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        Notification.Action[] actions = notification.actions;
        if (actions != null) {

            String defaultAnswer = "";
            String hwAnswer = "";

            try {
                defaultAnswer = getString(R.string.call_answer_string);
                hwAnswer = getString(R.string.call_answer_string_HW);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("notify_answer", "CallerNotificationListenerService exception: " + e.getMessage());
            }

            for (Notification.Action action : actions) {
                if (action == null) {
                    continue;
                }
                PendingIntent intent = action.actionIntent;
                String strTitle = String.valueOf(action.title);
                boolean isHwAnswer = !TextUtils.isEmpty(hwAnswer) && strTitle.equalsIgnoreCase(hwAnswer);

                if (Locale.getDefault().getLanguage().equalsIgnoreCase(CHINESE)) {
                    if (strTitle.equalsIgnoreCase("接听")) {
                        isHwAnswer = true;
                    }
                }

                if (strTitle.equalsIgnoreCase(defaultAnswer) || isHwAnswer) {
                    LogUtil.d("cpservice", "get answer button action.");
                    answer = intent;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private static class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                // answer call
                if (answer != null) {
                    try {
                        answer.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            } else if (msg.arg1 == 0) {
                // reject call
            }
        }

    }
}
