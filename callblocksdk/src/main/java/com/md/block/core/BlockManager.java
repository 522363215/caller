package com.md.block.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;
import com.md.block.beans.BlockInfo;
import com.md.block.callback.PhoneStateChangeCallback;
import com.md.block.core.local.BlockLocal;
import com.md.block.core.receiver.PhoneStateReceiver;
import com.md.block.core.service.CallerNotificationListenerService;
import com.md.block.util.BlockPermissionsUtil;
import com.md.block.util.LogUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ChenR on 2018/7/2.
 */

public class BlockManager {

    public static volatile BlockManager instance = null;
    private static final String TAG = "BlockManager";

    private static final int NOTIFICATION_BAR_FOR_REJECT_CALL = 0;
    private static final int NOTIFICATION_BAR_FOR_ANSWER_CALL = 1;

    private static List<PhoneStateChangeCallback> mPhoneStateListenerList = new ArrayList<>();

    private BlockManager() {
    }

    public static BlockManager getInstance() {
        if (instance == null) {
            synchronized (BlockManager.class) {
                if (instance == null) {
                    instance = new BlockManager();
                }
            }
        }
        return instance;
    }

    private Context appContext = null;
    private static ITelephony mITelephony;
    private TelephonyManager mTele;
    private static Messenger mMessenger;
    private PhoneStateReceiver phoneStateReceiver;

    static {
        try {
            Class<?> loadClass = BlockManager.class.getClass().getClassLoader().loadClass("android.os.ServiceManager");
            Method getService = loadClass.getMethod("getService", String.class);
            IBinder iBinder = (IBinder) getService.invoke(null, Context.TELEPHONY_SERVICE);
            mITelephony = ITelephony.Stub.asInterface(iBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addPhoneStateListener(PhoneStateChangeCallback callback) {
        if (callback != null) {
            mPhoneStateListenerList.add(callback);
        }
    }

    public List<PhoneStateChangeCallback> getPhoneStateListenerList() {
        List<PhoneStateChangeCallback> list = (List<PhoneStateChangeCallback>) ((ArrayList<PhoneStateChangeCallback>) mPhoneStateListenerList).clone();
        return list;
    }

    public void removePhoneStateChangeCallback(PhoneStateChangeCallback callback) {
        if (mPhoneStateListenerList.contains(callback)) {
            mPhoneStateListenerList.remove(callback);
        }
    }

    public void clearPhoneStateChangeCallbackList() {
        mPhoneStateListenerList.clear();
    }

    private static ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
        }
    };

    public void initialize(Context applicationContext) {
        this.appContext = applicationContext;
        mTele = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
        ;

        Intent intent = new Intent();
        intent.setClass(applicationContext, CallerNotificationListenerService.class);
//        intent.setAction("android.service.notification.NotificationListenerService");
        applicationContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void registerPhoneReceiver(Context context) {
        if (context == null) {
            throw new NullPointerException("The context used to register the receiver must not be empty.");
        }

        IntentFilter filter = new IntentFilter();//for block
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        phoneStateReceiver = new PhoneStateReceiver();
        context.registerReceiver(phoneStateReceiver, filter);
    }

    public Context getAppContext() {
        return appContext;
    }

    public boolean setBlockContact(BlockInfo contact) {
        return BlockLocal.setBlockContacts(contact);
    }

    public void setBlockContact(List<BlockInfo> blockContacts) {
        BlockLocal.setBlockContacts(blockContacts);
    }

    public boolean removeBlockContact(BlockInfo info) {
        return BlockLocal.removeBlockContact(info);
    }

    public boolean removeBlockContact(String number) {
        return BlockLocal.removeBlockContact(number);
    }

    public void clearBlockContacts() {
        BlockLocal.clearBlockContacts();
    }

    public void setBlockHistory(BlockInfo history) {
        BlockLocal.setBlockHistory(history);
    }

    public List<BlockInfo> getBlockContacts() {
        return BlockLocal.getBlockContacts();
    }

    public List<BlockInfo> getBlockHistory() {
        return BlockLocal.getBlockHistory();
    }

    public boolean removeBlockHistory(BlockInfo history) {
        return BlockLocal.removeBlockHistory(history);
    }

    public void clearBlockHistory() {
        BlockLocal.clearBlockHistory();
    }

    public boolean setBlockSwitchState(boolean bool) {
        return BlockLocal.setBlockSwitchState(bool);
    }

    public boolean getBlockSwitchState() {
        return BlockLocal.getBlockSwitchState();
    }

    public void answerCall() {
        try {
            if (mTele == null) {
                throw new NullPointerException("tm == null");
            }
            mTele.getClass().getMethod("answerRingingCall").invoke(mTele);
            LogUtil.d("notify_answer", "answerCall 1");
        } catch (Throwable th) {
            LogUtil.d("notify_answer", "answerCall 2");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                boolean isNotificationServiceRunning = BlockPermissionsUtil.isNotificationServiceRunning();
                boolean isCallerNotificationServiceRunning = BlockPermissionsUtil.isCallerNotificationServiceRunning();
                if (isNotificationServiceRunning && isCallerNotificationServiceRunning) {
                    try {
                        acceptCall(appContext);
                    } catch (Throwable th2) {
                        LogUtil.e("notify_answer", "all 22api acceptCall error: " + th2.getMessage());
                    }
                } else {
                    answerNow();
                }
            } else {
                answerNow();
            }
        }
    }

    private void answerNow() {
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
                //long uptimeMillis = SystemClock.uptimeMillis() - 1;
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
                KeyEvent keyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
                audioManager.dispatchMediaKeyEvent(keyEvent);
                audioManager.dispatchMediaKeyEvent(keyEvent2);
                LogUtil.d("callflashnew", "answerNow 19 up: ");
            } else {
                LogUtil.d("callflashnew", "answerNow 19 down: ");
                Runtime.getRuntime().exec("input keyevent " + Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
            }
        } catch (Throwable th) {
            answerByEarPhone(appContext);
            LogUtil.e("callflashnew", "answerNow error: " + th.getMessage());
        } finally {
//            if (mTelephonyManager != null
//                    && mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_RINGING){
//                answerByAIDL();
//            }
        }
    }

    private void answerByEarPhone(Context context) {
        String str = !((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isWiredHeadsetOn() ?
                Build.VERSION.SDK_INT >= 15 ? null : "android.permission.CALL_PRIVILEGED" : null;
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
        context.sendOrderedBroadcast(intent, str);
    }

    private void answerByNotificationBar() {
        Message msg = new Message();
        msg.arg1 = NOTIFICATION_BAR_FOR_ANSWER_CALL;
        try {
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void acceptCall(Context context) {
        MediaSessionManager mediaSessionManager = (MediaSessionManager)
                context.getApplicationContext().getSystemService(Context.MEDIA_SESSION_SERVICE);
        List<MediaController> mediaControllerList = mediaSessionManager
                .getActiveSessions(new ComponentName(context.getApplicationContext(), CallerNotificationListenerService.class));
        for (MediaController m : mediaControllerList) {
            if ("com.android.server.telecom".equals(m.getPackageName())) {
                m.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
                m.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                LogUtil.d("notify_answer", "samsung CallSchemeAcceptAPI26 acceptCall end:");
                break;
            }
        }
    }

    public boolean blockCall(String phoneNumber) {
        boolean suc = false;
        if (TextUtils.isEmpty(phoneNumber)) {
            return suc;
        }

        List<BlockInfo> blockContacts = getBlockContacts();
        if (blockContacts == null) {
            return suc;
        }

        for (BlockInfo contact : blockContacts) {
            if (PhoneNumberUtils.compare(contact.getNumber(), phoneNumber)) {
                suc = endCall();
                break;
            }
        }

        return suc;
    }

    public boolean endCall() {
        if (mITelephony == null) {
            LogUtil.e(TAG, "ITelephony is null reference.");
            return false;
        }

        try {
            boolean isEnd = false;
            try {
                isEnd = mITelephony.endCall();
            } catch (Throwable e) {
                LogUtil.e(TAG, "endCall Throwable: " + e.getMessage());
            }
            if (!isEnd) {
                try {
                    isEnd = mITelephony.endCallForSubscriber(1);
                } catch (Throwable e) {
                    LogUtil.e(TAG, "endCallForSubscriber Throwable: " + e.getMessage());
                }
            }
            return isEnd;
        } catch (Exception e) {
            return false;
        }
    }
}
