package blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.block.core.BlockManager;
import com.md.block.core.service.CallerNotificationListenerService;
import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.NumberInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ContactManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.RingManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.RomUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.SpecialPermissionsUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.SystemInfoUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CallFlashAvatarInfoView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.callflash.CallFlashView;

import static android.media.AudioManager.FLAG_ALLOW_RINGER_MODES;


/**
 * Created by zhangjinwei on 2017/4/2.
 */

public class CallFlashDialog implements View.OnClickListener {
    private static final String TAG = "CallFlashDialog";

    private static CallFlashDialog sInstance;
    private BlockManager mBlockManager;
    private String mNumber;
    private RelativeLayout mRootView;
    private ImageView mEndCallButton;
    private ImageView mAnswerButton;
    private ApplicationEx appEx;
    private WindowManager mWindowMgr;
    private TelephonyManager mTele;
    private WindowManager.LayoutParams mFloatViewLayoutParams;
    private AtomicBoolean isFloatViewShow = new AtomicBoolean(false);
    private RelativeLayout mLayoutMenuRoot;
    private TextView mTvDisable;
    private boolean mOriginalMute;
    private int mRingOldMode = -1;
    private CallFlashAvatarInfoView mCallFlashAvatarInfoView;
    private CallFlashView mCallFlashView;
    private FontIconView mFivMenu;
    private CallFlashInfo mCallFlashInfo;

    private CallFlashDialog() {
        appEx = ApplicationEx.getInstance();
        mBlockManager = BlockManager.getInstance();
        mWindowMgr = (WindowManager) appEx.getSystemService(Context.WINDOW_SERVICE);
        mTele = (TelephonyManager) appEx.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static CallFlashDialog getInstance() {
        synchronized (CallFlashDialog.class) {
            if (sInstance == null) {
                sInstance = new CallFlashDialog();
            }
        }
        return sInstance;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_call_answer:
                Async.run(new Runnable() {
                    @Override
                    public void run() {
                        PreferenceHelper.putLong(PreferenceHelper.PREF_CALL_SHOW_ANSWER_CLICK_START, System.currentTimeMillis());
                        answerCall();
                        LogUtil.d("callflashDialog", "answerCall answerCall");
                        Async.scheduleTaskOnUiThread(300, new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.d("callflashDialog", "answerCall hideFloatView");
                                hideFloatView();
                            }
                        });
                        FlurryAgent.logEvent("CallFlashDialog----answer_click");
                    }
                });
                break;
            case R.id.iv_call_hang:
                Async.run(new Runnable() {
                    @Override
                    public void run() {
                        mBlockManager.blockCall(mNumber);
                        Async.scheduleTaskOnUiThread(300, new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.d("callflashDialog", "hangCall hideFloatView");
                                hideFloatView();
                            }
                        });
                    }
                });
                break;
            case R.id.fiv_menu:
                mLayoutMenuRoot.setVisibility(View.VISIBLE);
                break;
            case R.id.layout_menu:
                mLayoutMenuRoot.setVisibility(View.GONE);
                break;
            case R.id.tv_disable:
                try {
                    mLayoutMenuRoot.setVisibility(View.GONE);
                    hideFloatView();
//                    PreferenceHelper.putBoolean(ConstantUtils.CALL_FLASH_ON, false);
                } catch (Exception e) {
                    LogUtil.e("callfreshdialog", "tv_disable e:" + e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    public void showFloatView(String number) {
        FlurryAgent.onStartSession(ApplicationEx.getInstance());
        mNumber = number;
        mCallFlashInfo = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, CallFlashInfo.class);
        initFloatView();
        initLayoutParams();
        setNumberInfo();
        startAnimations();
        showView();
        sendAnswercallEvent();
        FlurryAgent.logEvent("CallFlashDialog----showDialog");
    }

    private void setNumberInfo() {
        NumberInfo contact = ContactManager.getInstance().getContact(mNumber);
        String name = "";
        String photoId = "";
        if (contact != null) {
            name = contact.name;
            Bitmap photo = ContactManager.getInstance().getContactPhoto(contact.photoId);
            mCallFlashAvatarInfoView.setName(name);
            mCallFlashAvatarInfoView.setNumber(mNumber);
            mCallFlashAvatarInfoView.setAvatar(photo);
        } else {
            mCallFlashAvatarInfoView.setNumber(mNumber);
        }

    }

    private void initFloatView() {
        if (mRootView == null) {
            try {
                LayoutInflater inflater = (LayoutInflater) appEx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mRootView = (RelativeLayout) inflater.inflate(R.layout.layout_call_flash_dialog, null);
                //call flash
                mCallFlashView = mRootView.findViewById(R.id.call_flash_view);
                mCallFlashAvatarInfoView = (CallFlashAvatarInfoView) mRootView.findViewById(R.id.callFlashAvatarInfoView);
                mAnswerButton = (ImageView) mRootView.findViewById(R.id.iv_call_answer);
                mEndCallButton = (ImageView) mRootView.findViewById(R.id.iv_call_hang);

                //menu
                mFivMenu = (FontIconView) mRootView.findViewById(R.id.fiv_menu);
                mLayoutMenuRoot = (RelativeLayout) mRootView.findViewById(R.id.layout_menu_root);
                mTvDisable = (TextView) mRootView.findViewById(R.id.tv_disable);

                mFivMenu.setOnClickListener(this);
                mLayoutMenuRoot.setOnClickListener(this);
                mTvDisable.setOnClickListener(this);
                mAnswerButton.setOnClickListener(this);
                mEndCallButton.setOnClickListener(this);
            } catch (Exception e) {
            }
        }
    }

    private void initLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        if (Build.VERSION.SDK_INT >= 19) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST; // 2005
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 2002
        }

        if (Build.VERSION.SDK_INT < 23) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        } else if (Settings.canDrawOverlays(appEx)) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        mFloatViewLayoutParams = layoutParams;
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
                if (SpecialPermissionsUtil.isNotificationServiceRunning() && SpecialPermissionsUtil.isCallerNotificationServiceRunning()) {
                    try {
                        acceptCall(ApplicationEx.getInstance());
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void acceptCall(Context context) {
        /* 模拟耳机插入动作,用于接听电话 */
        /* 由于Android8.0的限制, 这里通过通知栏去触发模拟事件, 算是走了个大弯 */
        LogUtil.d("notify_answer", "22api samsung CallSchemeAcceptAPI26 acceptCall start:");
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

    private void answerNow() {
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                AudioManager audioManager = (AudioManager) ApplicationEx.getInstance().getSystemService(Context.AUDIO_SERVICE);
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
            answerByEarPhone(ApplicationEx.getInstance());
            LogUtil.e("callflashnew", "answerNow error: " + th.getMessage());
        } finally {
        }
    }

    private void answerByEarPhone(Context context) {
        String str = !((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isWiredHeadsetOn() ? Build.VERSION.SDK_INT >= 15 ? null : "android.permission.CALL_PRIVILEGED" : null;
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
        ApplicationEx.getInstance().sendOrderedBroadcast(intent, str);
        LogUtil.d("callflashnew", "answerByEarPhone: ");
    }

    private void startAnimations() {
        if (isFloatViewShow.get() || mCallFlashInfo == null) {
            return;
        }
        int flashType = mCallFlashInfo.flashType;
        if (flashType == FlashLed.FLASH_TYPE_FESTIVAL) {
            mEndCallButton.setImageResource(R.drawable.ic_call_hang_festival);
            mAnswerButton.setImageResource(R.drawable.ic_call_answer_festival);
        } else {
            mEndCallButton.setImageResource(R.drawable.ic_call_hang);
            mAnswerButton.setImageResource(R.drawable.ic_call_answer);
        }
        mCallFlashView.showCallFlashView(mCallFlashInfo);
        startAnswerAnim();
        setRingMode(true);
    }

    private void stopAnimations() {
        stopAnswerAnim();
    }

    private void startAnswerAnim() {
        Animation animation = AnimationUtils.loadAnimation(appEx, R.anim.answer_button_anim);
        mAnswerButton.startAnimation(animation);
    }

    private void stopAnswerAnim() {
        if (mAnswerButton != null) {
            mAnswerButton.clearAnimation();
        }
    }

    private void showView() {
        if (isFloatViewShow.get()) {
            return;
        }

        try {
            if (mWindowMgr != null) {
                mWindowMgr.addView(mRootView, mFloatViewLayoutParams);
                isFloatViewShow.set(true);
            }
        } catch (Exception e) {
        }
    }

    public void sendAnswercallEvent() {
        long interval = PreferenceHelper.getLong(PreferenceHelper.PREF_CALL_SHOW_ANSWER_CLICK_END, 0) - PreferenceHelper.getLong(PreferenceHelper.PREF_CALL_SHOW_ANSWER_CLICK_START, 0);
        long bench = PreferenceHelper.getLong("pref_answer_call_succ_bench", 1000);
        LogUtil.d("notify_answer", "sendAnswercallEvent interval: " + interval + ", bench: " + bench);
        if (interval > 0 && interval <= bench) {
            FlurryAgent.logEvent("CallFlashDialog----answer_click_success");
        }
    }

    public void hideFloatView() {
        try {
            if (!isFloatViewShow.get()) {
                LogUtil.d("callflashDialog", "hideFloatView isFloatViewShow");
                return;
            }
            LogUtil.d("callflashDialog", "hideFloatView hideFloatView");
            mWindowMgr.removeView(mRootView);
            isFloatViewShow.set(false);
        } catch (Exception e) {
            LogUtil.e("callflashDialog", "hideFloatView e:" + e.getMessage());
        }

        //还原为系统铃声
        setRingMode(false);

        FlurryAgent.onEndSession(ApplicationEx.getInstance());
        stopAnimations();
        mRootView = null;
        mCallFlashView = null;
        mEndCallButton = null;
        mAnswerButton = null;
    }

    /**
     * @param isSet true:设置call flash铃声，false:还原系统铃声
     */
    private void setRingMode(final boolean isSet) {
        if (mCallFlashInfo == null) return;
        LogUtil.d(TAG, "setRingMode isHaveSound:" + mCallFlashInfo.isHaveSound);
        if (!mCallFlashInfo.isHaveSound) return;
        AudioManager audioManager = (AudioManager) ApplicationEx.getInstance().getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
//        mSystemStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        RingManager.setVideoRingVolume(isSet, true);
        if (isSet) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mOriginalMute = audioManager.isStreamMute(AudioManager.STREAM_RING);
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, FLAG_ALLOW_RINGER_MODES);
                if (!SystemInfoUtil.isMiui() && !RomUtils.checkIsMiuiRom()) {
                    try {
                        mRingOldMode = audioManager.getRingerMode();
                        if (mRingOldMode == AudioManager.RINGER_MODE_NORMAL) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }
                        LogUtil.d("blockCall", "setRingerMode RINGER_MODE_SILENT");
                    } catch (Exception e) {
                        LogUtil.e("blockCall", "keepSilence e:" + e.getMessage());
                        // TODO: 2017/7/19  部分手机设置静音失效，异常： Not allowed to change Do Not Disturb state，需要打开应用程序的免打扰访问设置
                    }
                }
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_RING, true);
            }
        } else {
            if ((mCallFlashView.getVisibility() == View.VISIBLE) && (mCallFlashInfo != null && mCallFlashInfo.isHaveSound)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!mOriginalMute) {
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, FLAG_ALLOW_RINGER_MODES);
                        if (!SystemInfoUtil.isMiui() && !RomUtils.checkIsMiuiRom()) {
                            try {
                                if (mRingOldMode == -1) {
                                    mRingOldMode = AudioManager.MODE_NORMAL;
                                }
                                audioManager.setRingerMode(mRingOldMode);
                            } catch (Exception e) {
                                LogUtil.e("blockCall", "keepSilence e:" + e.getMessage());
                                // TODO: 2017/7/19  部分手机设置静音失效，异常： Not allowed to change Do Not Disturb state，需要打开应用程序的免打扰访问设置
                            }
                        }
                    }
                } else {
                    audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                }
                LogUtil.d(TAG, "setVideoRingVolume restore ring currentRingVoluem :" + audioManager.getStreamVolume(AudioManager.STREAM_RING));
            }
        }
    }
}
