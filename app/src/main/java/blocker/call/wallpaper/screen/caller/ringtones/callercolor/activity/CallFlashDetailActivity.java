package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.download.DownloadState;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;
import com.md.flashset.volume.VolumeChangeListenAdapter;
import com.md.flashset.volume.VolumeChangeObserver;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.callback.ThemeNormalCallback;
import com.md.serverflash.download.ThemeResourceHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallFlashDetailGroupAdHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.PreloadAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.PermissionInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog.SavingDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventCallFlashDetailGroupAdShow;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventInterstitialAdLoadSuccess;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashDownloadCount;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashEnable;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashList;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCollection;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshPreviewDowloadState;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.FullScreenAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.SwipeManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.EncryptionUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.GuideUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.SpecialPermissionsUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.BatteryProgressBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CallFlashAvatarInfoView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.OKCancelDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.callflash.CallFlashView;

public class CallFlashDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CallFlashDetailActivity";
    private static final long SAVING_DIALOG_MAX_TIME = 3500;
    private static final long SAVING_DIALOG_MIN_TIME = 1200;

    private CallFlashInfo mInfo;

    private boolean isFlashSwitchOn = false;
    private String mDynamicFlashPath = "";
    private int mSaveFlashType = -1;

    private float mLastValue = 1.0f;

    private boolean isCurrentFlashUsing;

    private boolean mIsShowPermissionTipDialog;
    private Advertisement mAdvertisement;
    private CallFlashAvatarInfoView mCallFlashAvatarInfoView;
    private boolean mIsShowInterstitialAd;
    private SavingDialog mSavingDialog;
    private boolean mIsBack;
    private boolean mIsShow;

    private long enter_time;
    private boolean mIsShowGroupAd;
    private View layout_above_ad;
    private TextView tv_setting_action_below_ad;
    private TextView tv_download_action_below_ad;
    private BatteryProgressBar pb_downloading_below_ad;
    private TextView tv_downloading_below_ad;
    private View layout_progress_below_ad;
    private View layout_below_ad;
    private TextView tv_setting_action_above_ad;
    private TextView tv_download_action_above_ad;
    private BatteryProgressBar pb_downloading_above_ad;
    private TextView tv_downloading_above_ad;
    private View layout_progress_above_ad;
    private boolean mIsShowAboveAdBtn;
    private boolean mIsShowFirstAdmob;
    private boolean mIsShowFullScreenAd;
    private OnDownloadListener mOnDownloadListener;
    private boolean mIsToResult;
    private CallFlashView mCallFlashView;
    private GlideView mGvCallFlashBg;
    private View mLayoutCallFlashOthers;
    private LinearLayout mLayoutAd;
    private boolean mIsResume;
    private VolumeChangeObserver mVolumeChangeObserver;
    private VolumeChangeListenAdapter mVolumeChangeListenAdapter;
    private LinearLayout mLavoutLikeAndDownload;
    private FontIconView mFivLike;
    private TextView mTvLikeCount;
    private FontIconView mFivDownload;
    private TextView mTvDownloadCount;
    private List<PermissionInfo> mPermissions;
    private boolean mIsComeGuide;
    private ImageView mIvSound;
    private boolean mIsMute;
    private View mLayoutLike;
    private View mLayoutRewardVideoLoading;
    private View mLayoutFlashLockTip;

    private boolean isNeedRestartSwipe;

    private RewardedVideoAd mRewardVideoAd = null;
    private boolean isShowRewardedVideo;
    private boolean isLoadingRewardVideo;
    private View mLayoutLikeForLock;
    private TextView mTvLikeCountForLock;

    private RewardedVideoAdListener mRewardVideoAdListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {
            if (isFinishing() || mRewardVideoAd == null) {
                return;
            }
            mLayoutRewardVideoLoading.setVisibility(View.GONE);
            mRewardVideoAd.show();
        }

        @Override
        public void onRewardedVideoAdOpened() {
        }

        @Override
        public void onRewardedVideoStarted() {
        }

        @Override
        public void onRewardedVideoAdClosed() {
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            if (isFinishing() || mInfo == null) {
                return;
            }
            isShowRewardedVideo = false;
            List<String> dataList = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.PREF_CALL_FLASH_WATCH_REWARD_VIDEO_ID, String[].class);
            if (dataList == null) {
                dataList = new ArrayList<>();
            }
            if (mInfo != null && !dataList.contains(mInfo.id)) {
                dataList.add(mInfo.id);
                CallFlashPreferenceHelper.setDataList(CallFlashPreferenceHelper.PREF_CALL_FLASH_WATCH_REWARD_VIDEO_ID, dataList);
            }

            if (mCallFlashView != null) {
                mCallFlashView.setVisibility(View.VISIBLE);
            }
            if (mLayoutCallFlashOthers != null) {
                mLayoutCallFlashOthers.setVisibility(View.VISIBLE);
            }
            if (mLayoutFlashLockTip != null) {
                mLayoutFlashLockTip.setVisibility(View.GONE);
            }

            if (mLayoutLikeForLock != null) {
                mLayoutLikeForLock.setVisibility(View.GONE);
            }

            if (mInfo != null && !TextUtils.isEmpty(mInfo.url)) {
                try {
                    downloadFlashResourceFile();
                } catch (Exception e) {
                    LogUtil.e(TAG, "onRewardedVideoAdClosed e:" + e.getMessage());
                }
            }

            sendRewardedAdmobFlurry();
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            if (isFinishing() || mLayoutRewardVideoLoading == null) {
                return;
            }
            ToastUtils.showToast(CallFlashDetailActivity.this, getString(R.string.call_flash_detail_load_rewarded_failed));
            mLayoutRewardVideoLoading.setVisibility(View.GONE);
        }

        @Override
        public void onRewardedVideoCompleted() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        super.onCreate(savedInstanceState);
        enter_time = System.currentTimeMillis();
        //定义全屏参数
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mIsComeGuide = getIntent().getBooleanExtra(ActivityBuilder.IS_COME_GUIDE, false);
        mInfo = (CallFlashInfo) getIntent().getSerializableExtra(ActivityBuilder.CALL_FLASH_INFO);
        initIsWatchRewardedVideo();
        initView();
        initAds();
        initVolumeChangeObserver(true);
        listener();
        downloadListener();
        initSaveFlash();
        updateUI();
        loadInterstitialAd();
        isNeedRestartSwipe = false;
        if (!SpecialPermissionsUtil.canDrawOverlays(this)) {
            boolean isDisableByUser = ApplicationEx.getInstance().getGlobalSettingPreference().getBoolean("swipe_disable_by_user", false);
            boolean isEnableByUser = ApplicationEx.getInstance().getGlobalSettingPreference().getBoolean("swipe_enable_by_user", false);
            int isEnableByServer = ApplicationEx.getInstance().getGlobalADPreference().getInt("pref_swipe_toogle_by_server", 0);
            if (!isDisableByUser && (isEnableByUser || isEnableByServer == 1)) {
                isNeedRestartSwipe = true;
            }
        }
    }

    @Override
    protected void translucentStatusBar() {
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_call_flash_detail;
    }

    private void updateUI() {
        setFlashBackground();
        setLanguageBack();
        setCallFlashLayout(48);
        showDownloadProgress();
        setSound();
        showCallFlash();
        startAnswerAnim();
        setLikeAndDownload();
    }

    private void setSound() {
        if (mInfo == null) return;
        File file = ThemeSyncManager.getInstance().getFileByUrl(ApplicationEx.getInstance().getApplicationContext(), mInfo.url);
        if ((file != null && file.exists()) || (!TextUtils.isEmpty(mInfo.path) && new File(mInfo.path).exists()) || CallFlashManager.CALL_FLASH_START_SKY_ID.equals(mInfo.id)) {
            mIvSound.setVisibility(View.VISIBLE);
        } else {
            mIvSound.setVisibility(View.GONE);
        }
        mIsMute = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.PREF_CALL_FLASH_IS_MUTE_WHEN_PREVIEW, true);
        if (mIsMute) {
            mIvSound.setImageDrawable(getResources().getDrawable(R.drawable.icon_mute));
            FlurryAgent.logEvent("CallFlashDetailActivity-click_mute");
        } else {
            mIvSound.setImageDrawable(getResources().getDrawable(R.drawable.icon_sound));
            FlurryAgent.logEvent("CallFlashDetailActivity-click_sound");
        }
        mCallFlashView.setVideoMute(mIsMute);
    }

    private void initView() {
        mRewardVideoAd = MobileAds.getRewardedVideoAdInstance(CallFlashDetailActivity.this);
        mRewardVideoAd.setRewardedVideoAdListener(mRewardVideoAdListener);

        mLayoutAd = findViewById(R.id.layout_ad_view);

        mCallFlashView = findViewById(R.id.call_flash_view);
        mCallFlashAvatarInfoView = (CallFlashAvatarInfoView) findViewById(R.id.callFlashAvatarInfoView);
        mGvCallFlashBg = findViewById(R.id.gv_call_flash_bg);
        mLayoutCallFlashOthers = findViewById(R.id.layout_call_flash_others);
        mLayoutRewardVideoLoading = findViewById(R.id.layout_reward_video_loading);
        mLayoutFlashLockTip = findViewById(R.id.layout_flash_lock_tip);

        //点赞数和下载数
        mLavoutLikeAndDownload = (LinearLayout) mCallFlashView.findViewById(R.id.layout_like_and_download);
        mLayoutLike = mCallFlashView.findViewById(R.id.layout_like);
        mFivLike = (FontIconView) mCallFlashView.findViewById(R.id.fiv_like);
        mTvLikeCount = (TextView) mCallFlashView.findViewById(R.id.tv_like_count);
        mFivDownload = (FontIconView) mCallFlashView.findViewById(R.id.fiv_download);
        mTvDownloadCount = (TextView) mCallFlashView.findViewById(R.id.tv_download_count);
        //声音按钮
        mIvSound = (ImageView) mCallFlashView.findViewById(R.id.iv_sound);
        mLavoutLikeAndDownload.setVisibility(View.VISIBLE);

        mLayoutLikeForLock = findViewById(R.id.layout_like_for_lock);
        mTvLikeCountForLock = (TextView) findViewById(R.id.tv_like_count_for_lock);

        //above ad
        layout_above_ad = findViewById(R.id.layout_above_ad);
        tv_setting_action_above_ad = findViewById(R.id.tv_setting_action_above_ad);
        tv_download_action_above_ad = findViewById(R.id.tv_download_action_above_ad);
        pb_downloading_above_ad = findViewById(R.id.pb_downloading_above_ad);
        tv_downloading_above_ad = findViewById(R.id.tv_downloading_above_ad);
        layout_progress_above_ad = findViewById(R.id.layout_progress_above_ad);
        pb_downloading_above_ad.setMaxProgress(100);
        tv_downloading_above_ad.setText(getString(R.string.call_flash_gif_show_connecte));

        //below ad
        layout_below_ad = findViewById(R.id.layout_below_ad);
        tv_setting_action_below_ad = findViewById(R.id.tv_setting_action_below_ad);
        tv_download_action_below_ad = findViewById(R.id.tv_download_action_below_ad);
        pb_downloading_below_ad = findViewById(R.id.pb_downloading_below_ad);
        tv_downloading_below_ad = findViewById(R.id.tv_downloading_below_ad);
        layout_progress_below_ad = findViewById(R.id.layout_progress_below_ad);
        pb_downloading_below_ad.setMaxProgress(100);
        tv_downloading_below_ad.setText(getString(R.string.call_flash_gif_show_connecte));

        ((FontIconView) findViewById(R.id.fiv_back)).setShadowLayer(10f, 0, 0, 0xff000000);

        if (mIsComeGuide) {
            ((FontIconView) findViewById(R.id.fiv_back)).setVisibility(View.GONE);
            ((FontIconView) findViewById(R.id.fiv_close)).setVisibility(View.VISIBLE);
        } else {
            ((FontIconView) findViewById(R.id.fiv_back)).setVisibility(View.VISIBLE);
            ((FontIconView) findViewById(R.id.fiv_close)).setVisibility(View.GONE);
        }

        if (isShowRewardedVideo) {
            mLayoutCallFlashOthers.setVisibility(View.INVISIBLE);
            mCallFlashView.setVisibility(View.INVISIBLE);
            mLayoutFlashLockTip.setVisibility(View.VISIBLE);
        }

        mIsShowAboveAdBtn = isShowAboveAdBtn();
        if (mIsShowAboveAdBtn) {
            layout_above_ad.setVisibility(View.VISIBLE);
            layout_below_ad.setVisibility(View.GONE);
        } else {
            layout_above_ad.setVisibility(View.GONE);
            layout_below_ad.setVisibility(View.VISIBLE);
        }
    }

    private void initIsWatchRewardedVideo() {
        List<String> list = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.PREF_CALL_FLASH_WATCH_REWARD_VIDEO_ID, String[].class);
        boolean isWatchRewardedVideo = list != null && mInfo != null && mInfo.isDownloadSuccess && list.contains(mInfo.id);
        if (mInfo != null && !mInfo.isDownloaded && mInfo.isLock && !isWatchRewardedVideo) {
            isShowRewardedVideo = true;
        }
    }

    private void downloadListener() {
        ThemeResourceHelper.getInstance().addGeneralListener(mOnDownloadListener = new OnDownloadListener() {
            @Override
            public void onConnecting(String url) {
                if (mIsShowAboveAdBtn) {
                    pb_downloading_above_ad.setProgress(0);
                    tv_downloading_above_ad.setText(R.string.call_flash_gif_show_connecte);
                } else {
                    pb_downloading_below_ad.setProgress(0);
                    tv_downloading_below_ad.setText(R.string.call_flash_gif_show_connecte);
                }
            }

            @Override
            public void onFailure(String url) {
                if (isFinishing()) {
                    return;
                }
                if (mInfo != null && !TextUtils.isEmpty(url) && url.equals(mInfo.url)) {
                    mInfo.isDownloadSuccess = false;
                    mInfo.isDownloaded = true;
                    if (mIsShowAboveAdBtn) {
                        tv_setting_action_above_ad.setVisibility(View.GONE);
                        tv_download_action_above_ad.setVisibility(View.VISIBLE);
                        layout_progress_above_ad.setVisibility(View.GONE);
                    } else {
                        tv_setting_action_below_ad.setVisibility(View.GONE);
                        tv_download_action_below_ad.setVisibility(View.VISIBLE);
                        layout_progress_below_ad.setVisibility(View.GONE);
                    }
                    layout_progress_above_ad.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(CallFlashDetailActivity.this, getString(R.string.call_flash_gif_show_load_failed));
                        }
                    });
                }
            }

            @Override
            public void onFailureForIOException(String url) {
                onFailure(url);
            }

            @Override
            public void onProgress(String url, final int progress) {
                if (isFinishing()) {
                    return;
                }
                if (mInfo != null && !TextUtils.isEmpty(url) && url.equals(mInfo.url)) {
                    layout_progress_above_ad.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (isFinishing()) {
                                    return;
                                }
                                if (mIsShowAboveAdBtn) {
                                    tv_setting_action_above_ad.setVisibility(View.GONE);
                                    tv_download_action_above_ad.setVisibility(View.GONE);
                                    layout_progress_above_ad.setVisibility(View.VISIBLE);

                                    pb_downloading_above_ad.setProgress(progress);
                                    tv_downloading_above_ad.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, progress)));
                                } else {
                                    tv_setting_action_below_ad.setVisibility(View.GONE);
                                    tv_download_action_below_ad.setVisibility(View.GONE);
                                    layout_progress_below_ad.setVisibility(View.VISIBLE);

                                    pb_downloading_below_ad.setProgress(progress);
                                    tv_downloading_below_ad.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, progress)));
                                }
                            } catch (Exception e) {
                            }
                        }
                    });
                }
            }

            @Override
            public void onSuccess(String url, File file) {
                try {
                    if (isFinishing()) {
                        return;
                    }
                    if (mInfo != null && !TextUtils.isEmpty(url) && url.equals(mInfo.url)) {
                        mInfo.isDownloadSuccess = true;
                        mInfo.isDownloaded = true;
                        mInfo.path = file.getAbsolutePath();

                        mIvSound.setVisibility(View.VISIBLE);

                        showCallFlash();

                        CallFlashManager.getInstance().saveCallFlashDownloadCount(mInfo);
                        CallFlashManager.getInstance().saveDownloadedCallFlash(mInfo);
                        EventBus.getDefault().post(new EventRefreshCallFlashDownloadCount());

                        layout_progress_above_ad.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mIsShowAboveAdBtn) {
                                        tv_setting_action_above_ad.setVisibility(View.VISIBLE);
                                        tv_download_action_above_ad.setVisibility(View.GONE);
                                        layout_progress_above_ad.setVisibility(View.GONE);
                                    } else {
                                        tv_setting_action_below_ad.setVisibility(View.VISIBLE);
                                        tv_download_action_below_ad.setVisibility(View.GONE);
                                        layout_progress_below_ad.setVisibility(View.GONE);
                                    }
                                    EventBus.getDefault().post(new EventRefreshPreviewDowloadState());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isShowAboveAdBtn() {
        return /*CallerAdManager.isUseMagicButton() && */mIsShowFirstAdmob;
    }

    /**
     * 由于广告的原因所以离底部的margin 在变化，没广告时是48dp, 有广告时是150dp
     */
    private void setCallFlashLayout(int marginBottom) {
        float screenHeight = DeviceUtil.getScreenHeight();
        float screenWidth = DeviceUtil.getScreenWidth();
        float topHeight = Stringutil.dpToPx(CallFlashDetailActivity.this, 48);

        //callflashview 高宽定死，否则视频显示不全
        float bottomHeight = Stringutil.dpToPx(CallFlashDetailActivity.this, marginBottom);
        float viewHeight = screenHeight - topHeight - bottomHeight;
        float scale = viewHeight / screenHeight;
        int viewWidth = (int) (screenWidth * scale);
        LogUtil.d(TAG, "setCallFlashLayout viewWidth:" + viewWidth + ",viewHeight:" + viewHeight);
        ViewGroup.LayoutParams layoutParams = mCallFlashView.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = (int) viewHeight;
        mCallFlashView.setLayoutParams(layoutParams);

        //其他的采用缩小
        float bottomHeight2 = Stringutil.dpToPx(CallFlashDetailActivity.this, marginBottom + 2);
        float viewHeight2 = screenHeight - topHeight - bottomHeight2;
        float scale2 = viewHeight2 / screenHeight;
        float pivotY = screenHeight - bottomHeight2 / (1 - scale2);
        float pivotX = screenWidth / 2;
        mLayoutCallFlashOthers.setPivotX(pivotX);
        mLayoutCallFlashOthers.setPivotY(pivotY);
        mLayoutCallFlashOthers.setScaleX(scale2);
        mLayoutCallFlashOthers.setScaleY(scale2);
    }

    private void loadInterstitialAd() {
        if (mIsComeGuide || isShowRewardedVideo) return;
        InterstitialAdUtil.loadInterstitialAd(this, InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
//        LogUtil.d(TAG, "call_flash_detail loadInterstitialAd:" + CallerAdManager.getResultInFbId());
    }

    // 设置阿拉伯语返回键翻转;
    private void setLanguageBack() {
        if (LanguageSettingUtil.isLayoutReverse(this)) {
            findViewById(R.id.fiv_back).setRotation(180);
        }
    }

    private void initSaveFlash() {
        isFlashSwitchOn = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
        mSaveFlashType = CallFlashPreferenceHelper.getInt(CallFlashPreferenceHelper.CALL_FLASH_TYPE, -1);
        mDynamicFlashPath = CallFlashPreferenceHelper.getString(mSaveFlashType == FlashLed.FLASH_TYPE_DYNAMIC ?
                CallFlashPreferenceHelper.CALL_FLASH_TYPE_DYNAMIC_PATH : CallFlashPreferenceHelper.CALL_FLASH_CUSTOM_BG_PATH, "");
        if (isFlashSwitchOn && mInfo != null) {
            if (mInfo.flashType == FlashLed.FLASH_TYPE_CUSTOM || mInfo.flashType == FlashLed.FLASH_TYPE_DYNAMIC) {
                isCurrentFlashUsing = mSaveFlashType == mInfo.flashType && mInfo.path != null
                        && mDynamicFlashPath.equals(mInfo.path);
            } else {
                isCurrentFlashUsing = mSaveFlashType == mInfo.flashType;
            }
        }
    }

    private void setActionButtonText(TextView textView) {
        if (textView != null) {
            if (isCurrentFlashUsing && isFlashSwitchOn) {
                textView.setText(R.string.call_flash_detail_setting_action_to_cancel);
            } else {
                textView.setText(R.string.call_flash_gif_show_save);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mIsComeGuide = getIntent().getBooleanExtra(ActivityBuilder.IS_COME_GUIDE, false);
        mInfo = (CallFlashInfo) intent.getSerializableExtra(ActivityBuilder.CALL_FLASH_INFO);
        showCallFlash();
        setFlashBackground();
        showDownloadProgress();
        setLikeAndDownload();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResume = true;
        setVolumeChange(true);
        FlurryAgent.logEvent("CallFlashDetailActivity-start");
        if (mCallFlashView != null)
            mCallFlashView.continuePlay();
        if (mRewardVideoAd != null) {
            mRewardVideoAd.resume(CallFlashDetailActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mRewardVideoAd != null) {
            mRewardVideoAd.pause(CallFlashDetailActivity.this);
        }

        mIsResume = false;
        setVolumeChange(false);
        if (mCallFlashView != null)
            mCallFlashView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        userStayStatics();
        CallFlashDetailGroupAdHelper.getInstance().Clear();
        if (mSavingDialog != null && mSavingDialog.isShowing()) {
            mSavingDialog.dismiss();
        }
        if (mOnDownloadListener != null) {
            ThemeResourceHelper.getInstance().removeGeneralListener(mOnDownloadListener);
        }
        if (mCallFlashView != null && !mCallFlashView.isStopVideo()) {
            mCallFlashView.stop();
        }
        if (mRewardVideoAd != null) {
            mRewardVideoAd.destroy(CallFlashDetailActivity.this);
        }
        if (mRewardVideoAdListener != null) {
            mRewardVideoAdListener = null;
        }
    }

    private void userStayStatics() {
        long stay_time = System.currentTimeMillis() - enter_time;
        Map<String, String> eventParams = new HashMap<String, String>();
        String strStaySec;
        if (stay_time < 10 * 1000)
            strStaySec = "less than 10";  //stay less 10 seconds
        else if (stay_time < 20 * 1000)
            strStaySec = "10-19";
        else if (stay_time < 30 * 1000)
            strStaySec = "20-29";
        else if (stay_time < 60 * 1000)
            strStaySec = "30-59";
        else if (stay_time < 120 * 1000)
            strStaySec = "60-120";
        else
            strStaySec = " greater than 120";
        eventParams.put("param_call_flash_download", strStaySec);
        FlurryAgent.logEvent("user_stay_call_flash_download", eventParams);
    }

    @Override
    public void onBackPressed() {
        if (isLoadingRewardVideo) {
            return;
        }

        final boolean isShowInterstitialAd = InterstitialAdUtil.isShowInterstitial(InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
        File file = ThemeSyncManager.getInstance().getFileByUrl(ApplicationEx.getInstance().getApplicationContext(), mInfo.url);
        boolean fileExist = file != null && file.exists();
//        boolean isDownloadComplete = mInfo != null && (!mInfo.isOnlionCallFlash || fileExist || (!TextUtils.isEmpty(mInfo.path) && new File(mInfo.path).exists()));
        if (InterstitialAdUtil.isShowFullScreenAd(InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL) /*&& isDownloadComplete*/) {
            showFullScreenAd(true);
        } else if (isShowInterstitialAd /*&& isDownloadComplete*/) {
            showInterstitialAd(true);
        } else {
            onFinish();
        }
    }

    public void onFinish() {
        finish();
        if (!mIsShowInterstitialAd && !mIsShowFullScreenAd) {
            mCallFlashView.stop();
        }
    }

    private void listener() {
        tv_setting_action_below_ad.setOnClickListener(this);
        tv_download_action_below_ad.setOnClickListener(this);
        tv_setting_action_above_ad.setOnClickListener(this);
        tv_download_action_above_ad.setOnClickListener(this);
        findViewById(R.id.fiv_back).setOnClickListener(this);
        findViewById(R.id.fiv_close).setOnClickListener(this);
        mFivLike.setOnClickListener(this);
        mIvSound.setOnClickListener(this);

        mLayoutRewardVideoLoading.setOnClickListener(this);
        mRewardVideoAd.setRewardedVideoAdListener(mRewardVideoAdListener);
    }

    private void setLikeAndDownload() {
        //点赞
        if (mInfo == null) return;
        if (!mInfo.isOnlionCallFlash && (!CallFlashManager.CALL_FLASH_START_SKY_ID.equals(mInfo.id) || mIsComeGuide)) {
            mLayoutLike.setVisibility(View.GONE);
            return;
        }
        mLayoutLike.setVisibility(View.VISIBLE);
        CallFlashInfo cacheCallFlashInfo = CallFlashManager.getInstance().getCacheJustLikeFlashList(mInfo.id);
        if (cacheCallFlashInfo != null) {
            mInfo.likeCount = cacheCallFlashInfo.likeCount;
            mInfo.downloadCount = cacheCallFlashInfo.downloadCount;
            mInfo.isLike = cacheCallFlashInfo.isLike;
        }
        setLocalFlashLikeCount();

        mTvLikeCount.setText("" + mInfo.likeCount);
        mTvDownloadCount.setText("" + mInfo.downloadCount);
        if (mInfo.isLike) {
            mFivLike.setTextColor(getResources().getColor(R.color.color_FFE05A52));
        } else {
            mFivLike.setTextColor(getResources().getColor(R.color.whiteSmoke));
        }

        if (isShowRewardedVideo && mInfo.likeCount > 1000) {
            mLayoutLikeForLock.setVisibility(View.VISIBLE);
            mTvLikeCountForLock.setText("" + mInfo.likeCount);
        } else {
            mLayoutLikeForLock.setVisibility(View.GONE);
        }
    }

    private void setLocalFlashLikeCount() {
        if (mInfo.likeCount <= 0 && CallFlashManager.CALL_FLASH_START_SKY_ID.equals(mInfo.id)) {
            List<CallFlashInfo> downloadedCallFlash = CallFlashManager.getInstance().getDownloadedCallFlash();
            if (downloadedCallFlash != null && downloadedCallFlash.contains(mInfo)) {
                mInfo = downloadedCallFlash.get(downloadedCallFlash.indexOf(mInfo));
                CallFlashManager.getInstance().saveCallFlashDownloadCount(mInfo);
                CallFlashManager.saveFlashJustLike(mInfo);
            }
        }
    }

    private void showCallFlash() {
        if (mInfo == null) return;
        mCallFlashView.setElevation();
        mCallFlashView.showCallFlashView(mInfo);
    }

    private void startAnswerAnim() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.answer_button_anim);
        findViewById(R.id.iv_call_answer).startAnimation(animation);
    }

    private void setFlashBackground() {
        if (mInfo == null) return;
        if (mInfo.isOnlionCallFlash) {
            String url = mInfo.img_vUrl;
            if (!TextUtils.isEmpty(mInfo.thumbnail_imgUrl) && !url.equals(mInfo.thumbnail_imgUrl)) {
                url = mInfo.thumbnail_imgUrl;
            }
            mGvCallFlashBg.showImageWithBlur(url);
        } else {
            if (CallFlashManager.CALL_FLASH_START_SKY_ID.equals(mInfo.id)) {
                mGvCallFlashBg.showImageWithBlur(R.drawable.img_star_sky_v);
            } else {
                mGvCallFlashBg.showImageWithBlur(mInfo.imgResId);
            }
        }
    }

    private void showDownloadProgress() {
        if (mInfo == null) return;
        File file = ThemeSyncManager.getInstance().getFileByUrl(ApplicationEx.getInstance().getApplicationContext(), mInfo.url);
        if ((file != null && file.exists()) || (!TextUtils.isEmpty(mInfo.path) && new File(mInfo.path).exists()) || CallFlashManager.CALL_FLASH_START_SKY_ID.equals(mInfo.id)) {
            if (mIsShowAboveAdBtn) {
                tv_setting_action_above_ad.setVisibility(View.VISIBLE);
                tv_download_action_above_ad.setVisibility(View.GONE);
                layout_progress_above_ad.setVisibility(View.GONE);
                setActionButtonText(tv_setting_action_above_ad);
            } else {
                tv_setting_action_below_ad.setVisibility(View.VISIBLE);
                tv_download_action_below_ad.setVisibility(View.GONE);
                layout_progress_below_ad.setVisibility(View.GONE);
                setActionButtonText(tv_setting_action_below_ad);
            }
        } else {
            int downloadState = mInfo.downloadState;
            switch (downloadState) {
                case DownloadState.STATE_DOWNLOAD_CONNECTING:
                    if (mIsShowAboveAdBtn) {
                        pb_downloading_above_ad.setProgress(0);
                        tv_downloading_above_ad.setText(R.string.call_flash_gif_show_connecte);
                    } else {
                        pb_downloading_below_ad.setProgress(0);
                        tv_downloading_below_ad.setText(R.string.call_flash_gif_show_connecte);
                    }
                    break;
                case DownloadState.STATE_DOWNLOADING:
                    if (mIsShowAboveAdBtn) {
                        tv_setting_action_above_ad.setVisibility(View.GONE);
                        tv_download_action_above_ad.setVisibility(View.GONE);
                        layout_progress_above_ad.setVisibility(View.VISIBLE);

                        pb_downloading_above_ad.setProgress(mInfo.progress);
                        tv_downloading_above_ad.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, mInfo.progress)));
                    } else {
                        tv_setting_action_below_ad.setVisibility(View.GONE);
                        tv_download_action_below_ad.setVisibility(View.GONE);
                        layout_progress_below_ad.setVisibility(View.VISIBLE);

                        pb_downloading_below_ad.setProgress(mInfo.progress);
                        tv_downloading_below_ad.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, mInfo.progress)));
                    }
                    break;
                default:
                    if (mIsShowAboveAdBtn) {
                        tv_setting_action_above_ad.setVisibility(View.GONE);
                        layout_progress_above_ad.setVisibility(View.GONE);
                        tv_download_action_above_ad.setVisibility(View.VISIBLE);
                        tv_download_action_above_ad.setText(isShowRewardedVideo
                                ? R.string.call_flash_detail_theme_unlock : R.string.lion_family_active_download);
                    } else {
                        tv_setting_action_below_ad.setVisibility(View.GONE);
                        layout_progress_below_ad.setVisibility(View.GONE);
                        tv_download_action_below_ad.setVisibility(View.VISIBLE);
                        tv_download_action_below_ad.setText(isShowRewardedVideo
                                ? R.string.call_flash_detail_theme_unlock : R.string.lion_family_active_download);
                    }
            }
        }
    }

    private void downloadFlashResourceFile() {
        FlurryAgent.logEvent("CallFlashDetailActivity-----download");
        LogUtil.d("chenr", "start download flash file.");
        ThemeResourceHelper.getInstance().isCanWriteInStorage(PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
        ThemeResourceHelper.getInstance().downloadThemeResources(mInfo.id, mInfo.url, new OnDownloadListener() {
            @Override
            public void onConnecting(String url) {
                if (mIsShowAboveAdBtn) {
                    layout_progress_above_ad.setVisibility(View.VISIBLE);
                    tv_setting_action_above_ad.setVisibility(View.GONE);
                    tv_download_action_above_ad.setVisibility(View.GONE);
                } else {
                    layout_progress_below_ad.setVisibility(View.VISIBLE);
                    tv_setting_action_below_ad.setVisibility(View.GONE);
                    tv_download_action_below_ad.setVisibility(View.GONE);
                }
                if (mIsShowAboveAdBtn) {
                    pb_downloading_above_ad.setProgress(0);
                    tv_downloading_above_ad.setText(R.string.call_flash_gif_show_connecte);
                } else {
                    pb_downloading_below_ad.setProgress(0);
                    tv_downloading_below_ad.setText(R.string.call_flash_gif_show_connecte);
                }
            }

            @Override
            public void onFailure(String url) {
                if (isFinishing()) {
                    return;
                }
                mInfo.isDownloadSuccess = false;
                mInfo.isDownloaded = true;
                if (mIsShowAboveAdBtn) {
                    tv_setting_action_above_ad.setVisibility(View.GONE);
                    tv_download_action_above_ad.setVisibility(View.VISIBLE);
                    layout_progress_above_ad.setVisibility(View.GONE);
                    tv_download_action_above_ad.setText(isShowRewardedVideo
                            ? R.string.call_flash_detail_theme_unlock : R.string.lion_family_active_download);
                } else {
                    tv_setting_action_below_ad.setVisibility(View.GONE);
                    tv_download_action_below_ad.setVisibility(View.VISIBLE);
                    layout_progress_below_ad.setVisibility(View.GONE);
                    tv_download_action_below_ad.setText(isShowRewardedVideo
                            ? R.string.call_flash_detail_theme_unlock : R.string.lion_family_active_download);
                }
                layout_progress_above_ad.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(CallFlashDetailActivity.this, getString(R.string.call_flash_gif_show_load_failed));
                    }
                });
            }

            @Override
            public void onFailureForIOException(String url) {
                onFailure(url);
                getSdCardPermission();
            }

            @Override
            public void onProgress(String url, final int progress) {
                if (isFinishing()) {
                    return;
                }

                layout_progress_above_ad.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isFinishing()) {
                                return;
                            }

                            if (mIsShowAboveAdBtn) {
                                tv_setting_action_above_ad.setVisibility(View.GONE);
                                tv_download_action_above_ad.setVisibility(View.GONE);
                                layout_progress_above_ad.setVisibility(View.VISIBLE);

                                pb_downloading_above_ad.setProgress(progress);
                                tv_downloading_above_ad.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, progress)));
                            } else {
                                tv_setting_action_below_ad.setVisibility(View.GONE);
                                tv_download_action_below_ad.setVisibility(View.GONE);
                                layout_progress_below_ad.setVisibility(View.VISIBLE);

                                pb_downloading_below_ad.setProgress(progress);
                                tv_downloading_below_ad.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, progress)));
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }

            @Override
            public void onSuccess(final String url, final File file) {
                if (mInfo != null && !TextUtils.isEmpty(url) && url.equals(mInfo.url)) {
                    try {
                        if (isFinishing()) {
                            return;
                        }
                        mInfo.isDownloadSuccess = true;
                        mInfo.isDownloaded = true;
                        mInfo.path = file.getAbsolutePath();

                        Async.scheduleTaskOnUiThread(300, new Runnable() {
                            @Override
                            public void run() {
                                //保存第一帧图片
                                if (!EncryptionUtil.isEncrypted(file.getAbsolutePath())) {
                                    CallFlashManager.getInstance().saveVideoFirstFrame(url);
                                }

                                //加密
                                if (file != null && !TextUtils.isEmpty(file.getAbsolutePath())) {
                                    EncryptionUtil.encrypt(mInfo);
                                }

                                showCallFlash();
                            }
                        });

                        layout_progress_above_ad.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mIsShowAboveAdBtn) {
                                        tv_setting_action_above_ad.setVisibility(View.VISIBLE);
                                        tv_download_action_above_ad.setVisibility(View.GONE);
                                        layout_progress_above_ad.setVisibility(View.GONE);
                                    } else {
                                        tv_setting_action_below_ad.setVisibility(View.VISIBLE);
                                        tv_download_action_below_ad.setVisibility(View.GONE);
                                        layout_progress_below_ad.setVisibility(View.GONE);
                                    }
                                    EventBus.getDefault().post(new EventRefreshPreviewDowloadState());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting_action_above_ad:
                setOrCancelFlash();
                FlurryAgent.logEvent("CallFlashDetailActivity-setting_click");
                break;
            case R.id.tv_setting_action_below_ad:
                setOrCancelFlash();
                FlurryAgent.logEvent("CallFlashDetailActivity-setting_click");
                break;
            case R.id.fiv_back:
                onBackPressed();
                break;
            case R.id.fiv_close:
                if (mIsComeGuide) {
                    FlurryAgent.logEvent("CallFlashDetailActivity-----click----skip");
                    GuideUtil.toPermissionGuide(this);
                    finish();
                }
                break;
            case R.id.tv_download_action_above_ad:
                if (!TextUtils.isEmpty(mInfo.url)) {
                    downloadFlashResourceFile();
                }
                break;
            case R.id.tv_download_action_below_ad:
                List<String> watchRewardedVideo = CallFlashPreferenceHelper
                        .getDataList(CallFlashPreferenceHelper.PREF_CALL_FLASH_WATCH_REWARD_VIDEO_ID, String[].class);
                if (mInfo != null && !mInfo.isDownloaded && mInfo.isLock &&
                        (watchRewardedVideo == null || !watchRewardedVideo.contains(mInfo.id))) {
                    isLoadingRewardVideo = true;
                    tv_download_action_below_ad.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isLoadingRewardVideo = false;
                        }
                    }, 2500);

                    mLayoutRewardVideoLoading.setVisibility(View.VISIBLE);
                    AdRequest.Builder builder = new AdRequest.Builder();
                    mRewardVideoAd.loadAd(CallerAdManager.INTERSTITIAL_ADMOB_ID_JL_NEW_FLASH, builder.build());
                } else {
                    if (!TextUtils.isEmpty(mInfo.url)) {
                        downloadFlashResourceFile();
                    }
                }
                break;
            case R.id.fiv_like:
                if (mInfo != null) {
                    File file = ThemeSyncManager.getInstance().getFileByUrl(ApplicationEx.getInstance().getApplicationContext(), mInfo.url);
                    if ((file != null && file.exists()) || (!TextUtils.isEmpty(mInfo.path) && new File(mInfo.path).exists()) || CallFlashManager.CALL_FLASH_START_SKY_ID.equals(mInfo.id)) {
                        if (mInfo.isLike) {
                            mInfo.isLike = false;
                            mInfo.likeCount = mInfo.likeCount - 1;
                        } else {
                            mInfo.isLike = true;
                            mInfo.likeCount = mInfo.likeCount + 1;
                        }
                        mTvLikeCount.setText("" + mInfo.likeCount);
                        CallFlashManager.saveFlashJustLike(mInfo);
                        startLikeAnim(mInfo.isLike);
                        uploadLike(mInfo.isLike);
                        //刷新收藏界面
                        EventBus.getDefault().post(new EventRefreshCollection());
                    } else {
                        ToastUtils.showToast(this, R.string.click_like_tip);
                    }
                }
                break;
            case R.id.iv_sound:
                CallFlashPreferenceHelper.putBoolean(CallFlashPreferenceHelper.PREF_CALL_FLASH_IS_MUTE_WHEN_PREVIEW, !mIsMute);
                setSound();

                break;
        }
    }

    /**
     * @param isLike true:上传收藏，flash:上传取消收藏
     */
    private void uploadLike(boolean isLike) {
        if (mInfo == null) return;
        ThemeSyncManager.getInstance().syncJustLike(Long.parseLong(mInfo.id), isLike, new ThemeNormalCallback() {
            @Override
            public void onSuccess(int code, String msg) {
                LogUtil.d(TAG, "uploadLike onSuccess msg: " + msg + ", code: " + code);
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtil.d(TAG, "uploadLike onFailure msg: " + msg + ", code: " + code);
            }
        });
    }

    /**
     * @param isLike true:收藏动画，flash:取消动画
     */
    private void startLikeAnim(final boolean isLike) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0, 1f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mFivLike.setScaleX(value);
                mFivLike.setScaleY(value);
                LogUtil.d(TAG, "startLikeAnim value:" + value);
                if (value > mLastValue) {
                    if (isLike) {
                        mFivLike.setTextColor(getResources().getColor(R.color.color_FFE05A52));
                    } else {
                        mFivLike.setTextColor(getResources().getColor(R.color.whiteSmoke));
                    }
                }
                mLastValue = value;
            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.start();
    }

    private void showSaveDialog() {
        mSavingDialog = new SavingDialog(this);
        mSavingDialog.show();
        Async.scheduleTaskOnUiThread(SAVING_DIALOG_MIN_TIME, new Runnable() {
            @Override
            public void run() {
                final boolean isShowInterstitialAd = InterstitialAdUtil.isShowInterstitial(InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
                if (InterstitialAdUtil.isShowFullScreenAd(InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL) && !mIsComeGuide) {
                    showFullScreenAd(false);
                    Async.scheduleTaskOnUiThread(SAVING_DIALOG_MAX_TIME, new Runnable() {
                        @Override
                        public void run() {
                            if (!mIsShowFullScreenAd) {
                                LogUtil.d(TAG, "toResultOkAction 7");
                                toResultOkAction();
                            }
                        }
                    });
                } else if (isShowInterstitialAd && !mIsComeGuide) {
                    showInterstitialAd(false);
                    Async.scheduleTaskOnUiThread(SAVING_DIALOG_MAX_TIME, new Runnable() {
                        @Override
                        public void run() {
                            if (!mIsShowInterstitialAd) {
                                LogUtil.d(TAG, "toResultOkAction 1");
                                toResultOkAction();
                            }
                        }
                    });
                } else {
                    LogUtil.d(TAG, "toResultOkAction 2");
                    toResultOkAction();
                }
            }
        });
    }

    private void setOrCancelFlash() {
        if (!isFinishing()) {
            if (!isCurrentFlashUsing || !isFlashSwitchOn) {
                //单个权限请求，一个权限成功之后继续续请求下一个
                mPermissions = PermissionUtils.getRequestPermissions();
                requestPermissions();
            } else {
                toResult(getString(R.string.call_flash_gif_show_setting_suc_reset));
                CallFlashPreferenceHelper.putBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
                CallFlashPreferenceHelper.putInt(CallFlashPreferenceHelper.CALL_FLASH_TYPE, FlashLed.FLASH_TYPE_DEFAULT);
                CallFlashPreferenceHelper.putString(CallFlashPreferenceHelper.CALL_FLASH_TYPE_DYNAMIC_PATH, "");
                CallFlashPreferenceHelper.putString(CallFlashPreferenceHelper.CALL_FLASH_CUSTOM_BG_PATH, "");

                CallFlashInfo localFlash = CallFlashManager.getInstance().getLocalFlash();
                CallFlashPreferenceHelper.setObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, localFlash);
            }
        }
    }

    private void requestPermissions() {
        if (mPermissions != null) {
            for (PermissionInfo permission : mPermissions) {
                if (!PermissionUtils.hasPermission(this, permission.permission)) {
                    if (permission.isSpecialPermission) {
                        if (!TextUtils.isEmpty(permission.permission) && !permission.isRequested) {
                            requestSpecialPermission(permission.permission);
                            permission.isRequested = true;
                            return;
                        }
                    } else {
                        if (permission.permissions != null && permission.permissions.length > 0 && !permission.isRequested) {
                            requestPermission(permission.permissions, permission.requestCode);
                            permission.isRequested = true;
                            return;
                        }
                    }
                }
            }
        }
        if (isShowSuccessResult(this)) {
            showSaveDialog();
        } else {
            toResult(getString(R.string.permission_denied_txt2));
        }
    }

    public static boolean isShowSuccessResult(Context context) {
        boolean canDrawOverlays = SpecialPermissionsUtil.canDrawOverlays(context);
        boolean isHavePhoneAndContact = PermissionUtils.hasPermissions(context, PermissionUtils.PERMISSION_GROUP_PHONE_AND_CONTACT);
        return canDrawOverlays && isHavePhoneAndContact;
    }

    private void toResultOkAction() {
        if (mIsToResult) {
            return;
        }
//        RingManager.reductionRing(this);
        int resultDesId = -1;
        if (!isCurrentFlashUsing) {
            CallFlashPreferenceHelper.putBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, true);
            CallFlashPreferenceHelper.putInt(CallFlashPreferenceHelper.CALL_FLASH_TYPE, mInfo.flashType);

            if (!TextUtils.isEmpty(mInfo.path)) {
                if (mInfo.flashType == FlashLed.FLASH_TYPE_CUSTOM) {
                    CallFlashPreferenceHelper.putString(CallFlashPreferenceHelper.CALL_FLASH_CUSTOM_BG_PATH, mInfo.path);
                    CallFlashPreferenceHelper.putString(CallFlashPreferenceHelper.CALL_FLASH_TYPE_DYNAMIC_PATH, "");
                } else {
                    CallFlashPreferenceHelper.putString(CallFlashPreferenceHelper.CALL_FLASH_CUSTOM_BG_PATH, "");
                    CallFlashPreferenceHelper.putString(CallFlashPreferenceHelper.CALL_FLASH_TYPE_DYNAMIC_PATH, mInfo.path);
                }
            }

            CallFlashPreferenceHelper.setObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, mInfo);
            CallFlashManager.getInstance().saveSetRecordCallFlash(mInfo);
            resultDesId = R.string.call_flash_gif_show_setting_suc;
            EventBus.getDefault().post(new EventRefreshCallFlashEnable(true));
        } else {
            resultDesId = R.string.call_flash_gif_show_setting_suc_reset;
        }
        toResult(getString(resultDesId));
    }

    private void toResult(String resultDes) {
        Intent intent = new Intent(CallFlashDetailActivity.this, CallFlashSetResultActivity.class);
        intent.putExtra(ActivityBuilder.IS_COME_FROM_DESKTOP, getIntent().getBooleanExtra(ActivityBuilder.IS_COME_FROM_DESKTOP, false));
        intent.putExtra(ActivityBuilder.CALL_FLASH_INFO, mInfo);
//        intent.putStringArrayListExtra(ConstantUtils.NUMBER_FOR_CALL_FLASH, mNumbersForCallFlash);
        intent.putExtra("is_show_result", true);
        intent.putExtra("result_des", resultDes);
        intent.putExtra(ActivityBuilder.IS_COME_GUIDE, mIsComeGuide);
        intent.putExtra("is_show_interstitial_ad", mIsShowFullScreenAd || mIsShowInterstitialAd);
        startActivity(intent);
        onFinish();
        mIsToResult = true;
        EventBus.getDefault().post(new EventRefreshCallFlashList());
    }

    private void showInterstitialAd(final boolean isBack) {
        try {
            mIsBack = isBack;
            InterstitialAdvertisement interstitialAdvertisement = ApplicationEx.getInstance().getInterstitialAdvertisement(InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
            if (interstitialAdvertisement == null || isShowRewardedVideo) {
                if (isBack) {
                    onFinish();
                }
            } else {
                interstitialAdvertisement.show(new InterstitialAdvertisement.InterstitialAdShowListener() {
                    @Override
                    public void onAdClosed() {
                        LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdClosed");
                        ApplicationEx.getInstance().setInterstitialAdvertisement(null, InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
                        if (isBack) {
                            finish();
                        } else {
                            LogUtil.d(TAG, "toResultOkAction 4");
                            toResultOkAction();
                        }
                    }

                    @Override
                    public void onAdShow() {
                        LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdShow");
                        if (mCallFlashView != null)
                            mCallFlashView.pause();//显示admob 插屏广告时不会调用onPause,需要手动调用
                        mIsShowInterstitialAd = true;
                        Async.scheduleTaskOnUiThread(200, new Runnable() {
                            @Override
                            public void run() {
                                if (mSavingDialog != null) {
                                    mSavingDialog.dismiss();
                                }
                            }
                        });
                    }

                    @Override
                    public void onAdError() {
                        LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdError");
                        ApplicationEx.getInstance().setInterstitialAdvertisement(null, InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
                        if (isBack) {
                            onFinish();
                        } else {
                            LogUtil.d(TAG, "toResultOkAction 5");
                            toResultOkAction();
                        }
                    }
                });
                if (isBack) {
                    onFinish();
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "showInterstitialAd e:" + e.getMessage());
            if (isBack) {
                onFinish();
            }
        }
        mIsShow = true;
    }

    private void showFullScreenAd(final boolean isBack) {
        mIsBack = isBack;
        if (FullScreenAdManager.getInstance().isAdLoaded() && !isShowRewardedVideo) {
            FullScreenAdManager.getInstance().showAd(this, InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL, new FullScreenAdManager.AdListener() {
                @Override
                public void onAdShow() {
                    LogUtil.d(TAG, "showFullScreenAd  onAdShow");
                    mIsShowFullScreenAd = true;
                    if (mCallFlashView != null) {
                        mCallFlashView.pause();
                    }
                    Async.scheduleTaskOnUiThread(200, new Runnable() {
                        @Override
                        public void run() {
                            if (mSavingDialog != null) {
                                mSavingDialog.dismiss();
                            }
                        }
                    });
                }

                @Override
                public void onAdClose() {
                    LogUtil.d(TAG, "showFullScreenAd  onAdClosed");
                    FullScreenAdManager.getInstance().clear();
                    if (isBack) {
                        onFinish();
                    } else {
                        LogUtil.d(TAG, "toResultOkAction 6");
                        toResultOkAction();
                    }
                }

                @Override
                public void onAdClick() {
                    onAdClose();
                }
            });
        } else {
            if (isBack) {
                onFinish();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(EventInterstitialAdLoadSuccess event) {
        int position = event.getPosition();
        if (position == InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL && !mIsShowInterstitialAd && mIsShow && !isFinishing()) {
            LogUtil.d(TAG, "InterstitialAdvertisement EventInterstitialAdLoadSuccess showInterstitialAd");
            showInterstitialAd(mIsBack);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(EventCallFlashDetailGroupAdShow event) {
        if (!isFinishing()) {
            LogUtil.d(TAG, "event EventCallFlashDetailGroupAdShow");
            showGroupAd(true);
            mIsShowGroupAd = true;
        }
    }

    private void showGroupAd(boolean isResetShowAd) {
        LogUtil.d(TAG, "showBannerGroupAd showGroupAd 1");
        CallFlashDetailGroupAdHelper.getInstance().showGroupAd(
                isResetShowAd,
                mLayoutAd,
                AdvertisementSwitcher.SERVER_KEY_CALL_FLASH_DOWN_GROUP,
                false,
                PreloadAdvertisement.ADMOB_TYPE_NATIVE_ADVANCED,
                PreloadAdvertisement.ADMOB_ADX_TYPE_NATIVE_ADVANCED, false);
    }

    //---------------------------获取权限----------------------------------
    private void getSdCardPermission() {
        FlurryAgent.logEvent("CallFlashDetailActivity-downloadFailed-noSdCardPermission");
        OKCancelDialog dialog = new OKCancelDialog(CallFlashDetailActivity.this);
        dialog.show();
        dialog.setTvTitle(R.string.download_failed_for_no_sd_permission);
        dialog.setOKCancel(R.string.ok_string, R.string.no_string);
        dialog.setOkClickListener(new OKCancelDialog.OKClickListener() {
            @Override
            public void Ok() {
                requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtils.REQUEST_CODE_STORAGE_PERMISSION);
            }
        });
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        LogUtil.d(TAG, "MyPermissionGrant onPermissionGranted requestCode：" + requestCode);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_STORAGE_PERMISSION:
                if (!TextUtils.isEmpty(mInfo.url)) {
                    downloadFlashResourceFile();
                }
                break;
            case PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION:
                requestPermissions();
                if (isNeedRestartSwipe) {
                    SwipeManager.getInstance().restartEasySwipe();
                }
                break;
            case PermissionUtils.REQUEST_CODE_PHONE_AND_CONTACT_PERMISSION:
            case PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS:
            case PermissionUtils.REQUEST_CODE_AUTO_START:
            case PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK:
                requestPermissions();
                break;
        }
    }

    @Override
    public void onPermissionNotGranted(int requestCode) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_PHONE_AND_CONTACT_PERMISSION:
            case PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION:
                if (mIsComeGuide) {
                    requestPermissions();
                } else {
                    ToastUtils.showToast(this, getString(R.string.permission_denied_txt));
                }
                break;
            case PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS:
            case PermissionUtils.REQUEST_CODE_AUTO_START:
            case PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK:
                requestPermissions();
                break;
        }
    }
    //---------------------------获取权限----------------------------------


    //*********************************音量设置*********************************************
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mIsComeGuide) {
            return true;
        }

        try {
            if (mInfo != null && mInfo.isHaveSound && mIsResume) {
                int ringMode = mVolumeChangeObserver.getRingerMode();
                AudioManager am = mVolumeChangeObserver.getAudioManager();
                switch (keyCode) {
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        if (ringMode == AudioManager.RINGER_MODE_SILENT && !SpecialPermissionsUtil.isNotificationServiceRunning()) {
                            ToastUtils.showToast(this, R.string.mute_no_permission_tip2);
                        } else {
                            am.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                            LogUtil.d(TAG, "onKeyDown before ringMode:" + ringMode + ",after ringMode:" + am.getRingerMode());
                        }
                        return true;
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        am.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                        LogUtil.d(TAG, "onKeyDown before ringMode:" + ringMode + ",after ringMode:" + am.getRingerMode());
                        return true;
                }
            }
        } catch (Exception e) {

        }
        return super.onKeyDown(keyCode, event);
    }

    private void initVolumeChangeObserver(boolean isSaveMusicVolume) {
        //实例化对象并设置监听器
        mVolumeChangeObserver = new VolumeChangeObserver(this);
        mVolumeChangeObserver.setVolumeChangeListener(mVolumeChangeListenAdapter = new VolumeChangeListenAdapter() {
            @Override
            public void onRingVolumeChanged(int ringVolume) {
                super.onRingVolumeChanged(ringVolume);
                //系统媒体音量改变时的回调
                int maxMusicVolume = mVolumeChangeObserver.getMaxStreamVolume(AudioManager.STREAM_MUSIC);
                int maxRingVolume = mVolumeChangeObserver.getMaxStreamVolume(AudioManager.STREAM_RING);
                //修改媒体音量
                mVolumeChangeObserver.setStreamVolume(AudioManager.STREAM_MUSIC, (ringVolume * maxMusicVolume) / maxRingVolume);
                LogUtil.d(TAG, "onVolumeChanged()--->currentMusicVolume:" + mVolumeChangeObserver.getStreamVolume(AudioManager.STREAM_MUSIC) + ",maxMusicVolume:" + maxMusicVolume + ",currentRingVolume:" + ringVolume + ",maxRingVolume:" + maxRingVolume);
            }
        });
        if (isSaveMusicVolume) {
            //保存媒体声用于还原
            LogUtil.d(TAG, "initVolumeChangeObserver SaveMusicVolume:" + mVolumeChangeObserver.getStreamVolume(AudioManager.STREAM_MUSIC));
            CallFlashPreferenceHelper.putInt(CallFlashPreferenceHelper.PREF_CURRENT_MUSIC_VOLUME_WHEN_SET_CALL_FLASH, mVolumeChangeObserver.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    }

    private void setVolumeChange(boolean isResume) {
        if (isResume) {
            if (mVolumeChangeObserver.getVolumeChangeListener() == null) {
                mVolumeChangeObserver.setVolumeChangeListener(mVolumeChangeListenAdapter);
            }
            mVolumeChangeObserver.registerReceiver();
            //媒体音设为铃声音量大小
            int maxMusicVolume = mVolumeChangeObserver.getMaxStreamVolume(AudioManager.STREAM_MUSIC);
            int maxRingVolume = mVolumeChangeObserver.getMaxStreamVolume(AudioManager.STREAM_RING);
            mVolumeChangeObserver.setStreamVolume(AudioManager.STREAM_MUSIC, (mVolumeChangeObserver.getStreamVolume(AudioManager.STREAM_RING) * maxMusicVolume) / maxRingVolume);
        } else {
            mVolumeChangeObserver.unregisterReceiver();
            //还原媒体音量
            reStoreMusicVolume();
        }
    }

    /**
     * 退出callflash设置界面后还原媒体铃声
     */
    public static void reStoreMusicVolume() {
        AudioManager am = (AudioManager) ApplicationEx.getInstance().getBaseContext().getSystemService(Service.AUDIO_SERVICE);
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int lastMusicVolume = CallFlashPreferenceHelper.getInt(CallFlashPreferenceHelper.PREF_CURRENT_MUSIC_VOLUME_WHEN_SET_CALL_FLASH, currentVolume);
        LogUtil.d(TAG, "reStoreMusicVoluem lastMusicVolume:" + lastMusicVolume + ",currentVolume:" + currentVolume);
        if (lastMusicVolume != currentVolume) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, lastMusicVolume, 0);
        }
    }
    //*********************************音量设置*********************************************


    //******************************************AD******************************************//
    private void initAds() {
        if (mIsComeGuide) return;

        if (!isShowRewardedVideo) {
            MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(getWindow().getDecorView(),
                    CallerAdManager.getFacebook_id(CallerAdManager.POSITION_FB_CALL_FLASH_DETAIL_NORMAL),//ConstantUtils.FB_AFTER_CALL_ID
                    CallerAdManager.getAdmob_id(CallerAdManager.POSITION_ADMOB_MINE_NORMAL),//ConstantUtils.ADMOB_AFTER_CALL_NATIVE_ID
                    Advertisement.ADMOB_TYPE_NATIVE,//Advertisement.ADMOB_TYPE_NATIVE, Advertisement.ADMOB_TYPE_NONE
                    "",
                    Advertisement.MOPUB_TYPE_NATIVE,
                    -1,
                    "",
                    true);
            mAdvertisement = new Advertisement(adapter);
            mAdvertisement.setRefreshWhenClicked(false);
            mAdvertisement.refreshAD(true);
        }
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner, AdvertisementSwitcher.SERVER_KEY_FLASH_DETAIL);
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baiduKey, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey, eventKey, AdvertisementSwitcher.SERVER_KEY_FLASH_DETAIL, isBanner);
        }

        @Override
        public void onAdLoaded() {
            mLayoutAd.setVisibility(View.VISIBLE);
            setCallFlashLayout(128);
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.facebook_native_ads_banner_call_flash_detail : R.layout.facebook_no_icon_native_ads_call_after_big;
        }

        @Override
        public int getAdmobHeight() {
            return Stringutil.dpToPx(80);
        }

        @Override
        public int getFbAdsHight() {
            return 80;
        }

        @Override
        public int getAdmobWidth() {
            return DeviceUtil.getScreenWidth();
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_mine : R.layout.layout_admob_advanced_content_ad_mine;
        }
    }
    //******************************************AD******************************************//

    private void sendRewardedAdmobFlurry() {
        try {
            if (mInfo != null) {
                String flash_id = mInfo.id;
                String flash_title = mInfo.title;
                Map<String, String> eventParams = new HashMap<>();
                eventParams.put(flash_id, flash_title);
                FlurryAgent.logEvent("rewarded_ads_click", eventParams);
                LogUtil.d(TAG, "sendRewardedAdmobFlurry flash id: " + mInfo.id + ", title: " + mInfo.title);
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "sendRewardedAdmobFlurry exception:" + e.getMessage());
        }

    }

}
