package blocker.call.wallpaper.screen.caller.ringtones.callercolor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.SmsComeActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.Message;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

public class MessageReceiver extends BroadcastReceiver {
    private final String TAG = "MessageReceiver";
    private Context mContext;
    private String mAddress;
    private String mBody;
    private long mDate;
    private long mDatesent;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG, "onReceive");
        String action = intent.getAction();
        if ("android.provider.Telephony.SMS_RECEIVED".equals(action)) {
            LogUtil.d(TAG, "onReceive show sms android.provider.Telephony.SMS_RECEIVED");
            getMessageData(context, intent);
        }
    }

    private void getMessageData(Context context, Intent intent) {
        try {
            abortBroadcast();
            mContext = context;
            LogUtil.d(TAG, "onReceive show getMessageData");
            if (intent.getExtras() != null) {
                Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < messages.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                SmsMessage sms = messages[0];
                if (messages.length == 1 || sms.isReplace()) {
                    mBody = sms.getDisplayMessageBody();
                } else {
                    StringBuilder bodyText = new StringBuilder();
                    for (SmsMessage message : messages) {
                        bodyText.append(message.getMessageBody());
                    }
                    mBody = bodyText.toString();
                }

                mAddress = sms.getDisplayOriginatingAddress();
                mDate = System.currentTimeMillis();
                mDatesent = sms.getTimestampMillis();
                LogUtil.d(TAG, "datedatedate  messsagingreceiver:" + mDatesent);
            }
            setMessage();
        } catch (Exception e) {
            LogUtil.e(TAG, "MessagingReceiver exception: " + e.getMessage());
        }
    }

    private void setMessage() {
        boolean isMessageOn = PreferenceHelper.getBoolean(PreferenceHelper.SHOW_MESSAGE_COME, false);
        if (isMessageOn) {
            if (DeviceUtil.isScreenOff(mContext) || SmsComeActivity.isForeground(mContext)) {
                //跳转到短信界面
                LogUtil.d(TAG, "onReceive showsmsComeActvity isScreenOff:" + DeviceUtil.isScreenOff(mContext)
                        + ",isForeground:" + SmsComeActivity.isForeground(mContext));
                Intent smsIntent = new Intent(mContext, SmsComeActivity.class);
                Message message = new Message();
                message.address = mAddress;
                message.body = mBody;
                message.date = mDate;
                message.dateSent = mDatesent;
                smsIntent.putExtra(ActivityBuilder.SMS_COME_MESSAGE, message);
                smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(smsIntent);
            }
        } else {
            if (SmsComeActivity.isForeground(mContext)) {
                //跳转到短信界面
                LogUtil.d(TAG, "onReceive showsmsComeActvity");
                Intent smsIntent = new Intent(mContext, SmsComeActivity.class);
                Message message = new Message();
                message.address = mAddress;
                message.body = mBody;
                message.date = mDate;
                message.dateSent = mDatesent;
                smsIntent.putExtra(ActivityBuilder.SMS_COME_MESSAGE, message);
                smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(smsIntent);
            }
        }
    }

}
