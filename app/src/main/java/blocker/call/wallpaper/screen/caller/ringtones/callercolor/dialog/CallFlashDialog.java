package blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.sdk.analytics.AnalyticsManager;
import com.flurry.android.FlurryAgent;
import com.md.block.core.BlockManager;
import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;

import java.util.concurrent.atomic.AtomicBoolean;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.NumberInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ContactManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.RingManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
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
                        BlockManager.getInstance().answerCall();
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
                        mBlockManager.endCall();
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

                CardView cardView = mCallFlashView.findViewById(R.id.layout_card_view);
                cardView.setCardElevation(0);
                cardView.setPreventCornerOverlap(false);
                cardView.setUseCompatPadding(false);

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

        if (Build.VERSION.SDK_INT >= 26) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = DeviceUtil.getScreenHeightIncludeNavigateBar();

        mFloatViewLayoutParams = layoutParams;
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
        mCallFlashView.setVideoMute(false);
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
                AnalyticsManager.onUserActive();
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

        if (mCallFlashView != null) {
            mCallFlashView.stop();
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
                if (!SystemInfoUtil.isMiui()) {
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
                //静音时媒体声音不静音
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, FLAG_ALLOW_RINGER_MODES);
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_RING, true);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!mOriginalMute) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, FLAG_ALLOW_RINGER_MODES);
                    if (!SystemInfoUtil.isMiui()) {
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
