package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.download.ThemeResourceHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallFlashDetailGroupAdHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.PreloadAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog.SaveingDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventCallFlashDetailGroupAdShow;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventInterstitialAdLoadSuccess;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashDownloadCount;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashEnable;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashList;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshPreviewDowloadState;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.FullScreenAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.BatteryProgressBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CallFlashAvatarInfoView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.OKCancelDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.callflash.CallFlashView;
import event.EventBus;

public class CallFlashDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1712;
    private static final int REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS = 1713;
    public static final int REQUEST_CODE_WRITE_SETTINGS = 1715;
    private static final int REQUEST_CODE_APP_DETAILS_SETTINGS = 1716;
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
    private SaveingDialog mSavingDialog;
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
    private LinearLayout mLayourAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enter_time = System.currentTimeMillis();
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.activity_call_flash_detail);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mInfo = (CallFlashInfo) getIntent().getSerializableExtra(ActivityBuilder.CALL_FLASH_INFO);
        initView();
        listener();
        downloadListener();
//        initAd();
        initSaveFlash();
        updateUI();
        loadInterstitialAd();
    }

    private void updateUI() {
        setLanguageBack();
        setCallFlashLayout(48);
        showCallFlash();
        startAnswerAnim();
        setFlashBackground();
        showDownloadProgress();
//        setLike();

        //test 模拟广告加载成功
        Async.scheduleTaskOnUiThread(5000, new Runnable() {
            @Override
            public void run() {
                setCallFlashLayout(148);
                mLayourAd.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initView() {
        mCallFlashView = findViewById(R.id.call_flash_view);
        mCallFlashAvatarInfoView = (CallFlashAvatarInfoView) findViewById(R.id.callFlashAvatarInfoView);
        mGvCallFlashBg = findViewById(R.id.gv_call_flash_bg);
        mLayoutCallFlashOthers = findViewById(R.id.layout_call_flash_others);

        mLayourAd = findViewById(R.id.layout_ad_view);

        //above ad
        layout_above_ad = findViewById(R.id.layout_above_ad);
        tv_setting_action_above_ad = findViewById(R.id.tv_setting_action_above_ad);
        tv_download_action_above_ad = findViewById(R.id.tv_download_action_above_ad);
        pb_downloading_above_ad = findViewById(R.id.pb_downloading_above_ad);
        tv_downloading_above_ad = findViewById(R.id.tv_downloading_above_ad);
        layout_progress_above_ad = findViewById(R.id.layout_progress_above_ad);
        pb_downloading_above_ad.setMaxProgress(100);
        tv_downloading_above_ad.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, 0)));

        //below ad
        layout_below_ad = findViewById(R.id.layout_below_ad);
        tv_setting_action_below_ad = findViewById(R.id.tv_setting_action_below_ad);
        tv_download_action_below_ad = findViewById(R.id.tv_download_action_below_ad);
        pb_downloading_below_ad = findViewById(R.id.pb_downloading_below_ad);
        tv_downloading_below_ad = findViewById(R.id.tv_downloading_below_ad);
        layout_progress_below_ad = findViewById(R.id.layout_progress_below_ad);
        pb_downloading_below_ad.setMaxProgress(100);
        tv_downloading_below_ad.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, 0)));

        ((FontIconView) findViewById(R.id.fiv_back)).setShadowLayer(10f, 0, 0, 0xff000000);

        mIsShowAboveAdBtn = isShowAboveAdBtn();
        if (mIsShowAboveAdBtn) {
            layout_above_ad.setVisibility(View.VISIBLE);
            layout_below_ad.setVisibility(View.GONE);
        } else {
            layout_above_ad.setVisibility(View.GONE);
            layout_below_ad.setVisibility(View.VISIBLE);
        }
    }

    private void downloadListener() {
        ThemeResourceHelper.getInstance().addGeneralListener(mOnDownloadListener = new OnDownloadListener() {
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
                        mInfo.isDownloaded = false;
                        mInfo.path = file.getAbsolutePath();

                        showCallFlash();

                        CallFlashManager.getInstance().saveCallFlashDownloadCount(mInfo);
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
        InterstitialAdUtil.loadInterstitialAd(this, CallerAdManager.IN_ADS_CALL_FLASH);
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
            if (isCurrentFlashUsing) {
                textView.setText(R.string.call_flash_detail_setting_action_to_cancel);
            } else {
                textView.setText(R.string.call_flash_detail_setting_action_to_set);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mInfo = (CallFlashInfo) intent.getSerializableExtra(ActivityBuilder.CALL_FLASH_INFO);
        showCallFlash();
        setFlashBackground();
        showDownloadProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("CallFlashDetailActivity-start");
        if (mCallFlashView != null)
            mCallFlashView.continuePlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        final boolean isShowInterstitialAd = InterstitialAdUtil.isShowInterstitial(CallerAdManager.IN_ADS_CALL_FLASH);
        File file = ThemeSyncManager.getInstance().getFileByUrl(ApplicationEx.getInstance().getApplicationContext(), mInfo.url);
        boolean fileExist = file != null && file.exists();
//        boolean isDownloadComplete = mInfo != null && (!mInfo.isOnlionCallFlash || fileExist || (!TextUtils.isEmpty(mInfo.path) && new File(mInfo.path).exists()));
        if (InterstitialAdUtil.isShowFullScreenAd(CallerAdManager.IN_ADS_CALL_FLASH) /*&& isDownloadComplete*/) {
            showFullScreenAd(true);
        } else if (isShowInterstitialAd /*&& isDownloadComplete*/) {
            showInterstitialAd(true);
        } else {
            onFinish();
        }
    }

    public void onFinish() {
        finish();
        mCallFlashView.stop();
    }

    private void listener() {
        tv_setting_action_below_ad.setOnClickListener(this);
        tv_download_action_below_ad.setOnClickListener(this);
        tv_setting_action_above_ad.setOnClickListener(this);
        tv_download_action_above_ad.setOnClickListener(this);
        findViewById(R.id.fiv_back).setOnClickListener(this);
    }

    private void showCallFlash() {
        if (mInfo == null) return;
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
            mGvCallFlashBg.showImageWithBlur(url, 15);
        } else {
            mGvCallFlashBg.showImageWithBlur(mInfo.imgResId, 15);
        }
    }

    private void showDownloadProgress() {
        if (mInfo == null) return;
        File file = ThemeSyncManager.getInstance().getFileByUrl(ApplicationEx.getInstance().getApplicationContext(), mInfo.url);
        if ((file != null && file.exists()) || (!TextUtils.isEmpty(mInfo.path) && new File(mInfo.path).exists())) {
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
            if (mIsShowAboveAdBtn) {
                tv_setting_action_above_ad.setVisibility(View.GONE);
                layout_progress_above_ad.setVisibility(View.GONE);
                tv_download_action_above_ad.setVisibility(View.VISIBLE);
                tv_download_action_above_ad.setText(R.string.lion_family_active_download);
            } else {
                tv_setting_action_below_ad.setVisibility(View.GONE);
                layout_progress_below_ad.setVisibility(View.GONE);
                tv_download_action_below_ad.setVisibility(View.VISIBLE);
                tv_download_action_below_ad.setText(R.string.lion_family_active_download);
            }
        }
    }

    private void downloadFlashResourceFile() {
        ThemeResourceHelper.getInstance().isCanWriteInStorage(PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
        ThemeResourceHelper.getInstance().downloadThemeResources(mInfo.id, mInfo.url, new OnDownloadListener() {
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
            public void onSuccess(String url, File file) {
                try {
                    if (isFinishing()) {
                        return;
                    }

                    mInfo.isDownloadSuccess = true;
                    mInfo.isDownloaded = false;
                    mInfo.path = file.getAbsolutePath();

                    showCallFlash();

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
            case R.id.tv_download_action_above_ad:
                layout_progress_above_ad.setVisibility(View.VISIBLE);
                tv_setting_action_above_ad.setVisibility(View.GONE);
                tv_download_action_above_ad.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(mInfo.url)) {
                    downloadFlashResourceFile();
                }
                break;
            case R.id.tv_download_action_below_ad:
                layout_progress_below_ad.setVisibility(View.VISIBLE);
                tv_setting_action_below_ad.setVisibility(View.GONE);
                tv_download_action_below_ad.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(mInfo.url)) {
                    downloadFlashResourceFile();
                }
                break;
        }
    }

    private void showSaveDialog() {
        mSavingDialog = new SaveingDialog(this);
        mSavingDialog.show();
        layout_progress_below_ad.postDelayed(new Runnable() {
            @Override
            public void run() {
                final boolean isShowInterstitialAd = InterstitialAdUtil.isShowInterstitial(CallerAdManager.IN_ADS_CALL_FLASH);
                if (InterstitialAdUtil.isShowFullScreenAd(CallerAdManager.IN_ADS_CALL_FLASH)) {
                    showFullScreenAd(false);
                    layout_progress_below_ad.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!mIsShowFullScreenAd) {
                                LogUtil.d(TAG, "toResultOkAction 7");
                                toResultOkAction();
                            }
                        }
                    }, SAVING_DIALOG_MAX_TIME);
                } else if (isShowInterstitialAd) {
                    showInterstitialAd(false);
                    layout_progress_below_ad.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!mIsShowInterstitialAd) {
                                LogUtil.d(TAG, "toResultOkAction 1");
                                toResultOkAction();
                            }
                        }
                    }, SAVING_DIALOG_MAX_TIME);
                } else {
                    LogUtil.d(TAG, "toResultOkAction 2");
                    toResultOkAction();
                }
            }
        }, SAVING_DIALOG_MIN_TIME);
    }

    private void setOrCancelFlash() {
        if (!isFinishing()) {
            if (!isCurrentFlashUsing) {
                //单个权限请求，一个权限成功之后继续续请求下一个
                requestSpecialPermission(PermissionUtils.PERMISSION_OVERLAY);
            } else {
                toResult(getString(R.string.call_flash_gif_show_setting_suc_reset));
                CallFlashPreferenceHelper.putBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
                CallFlashPreferenceHelper.putInt(CallFlashPreferenceHelper.CALL_FLASH_TYPE, FlashLed.FLASH_TYPE_DEFAULT);
                CallFlashPreferenceHelper.putString(CallFlashPreferenceHelper.CALL_FLASH_TYPE_DYNAMIC_PATH, "");
                CallFlashPreferenceHelper.putString(CallFlashPreferenceHelper.CALL_FLASH_CUSTOM_BG_PATH, "");

                ArrayList<CallFlashInfo> list = CallFlashManager.getInstance().getAllLocalFlashList();

                if (list != null && !list.isEmpty()) {
                    for (CallFlashInfo info : list) {
                        if (info.flashType == FlashLed.FLASH_TYPE_DEFAULT) {
                            CallFlashPreferenceHelper.setObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, info);
                            break;
                        }
                    }
                }
            }
        }
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
        startActivity(intent);
        onFinish();
        mIsToResult = true;
        EventBus.getDefault().post(new EventRefreshCallFlashList());
    }

    private void showInterstitialAd(final boolean isBack) {
        try {
            mIsBack = isBack;
            InterstitialAdvertisement interstitialAdvertisement = ApplicationEx.getInstance().getInterstitialAdvertisement(CallerAdManager.IN_ADS_CALL_FLASH);
            if (interstitialAdvertisement == null) {
                if (isBack) {
                    onFinish();
                }
            } else {
                interstitialAdvertisement.show(new InterstitialAdvertisement.InterstitialAdShowListener() {
                    @Override
                    public void onAdClosed() {
                        LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdClosed");
                        ApplicationEx.getInstance().setInterstitialAdvertisement(null, CallerAdManager.IN_ADS_CALL_FLASH);
                        if (isBack) {
                            onFinish();
                        } else {
                            LogUtil.d(TAG, "toResultOkAction 4");
                            toResultOkAction();
                        }
                    }

                    @Override
                    public void onAdShow() {
                        LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdShow");
                        mIsShowInterstitialAd = true;
                        tv_download_action_above_ad.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mSavingDialog != null) {
                                    mSavingDialog.dismiss();
                                }
                            }
                        }, 200);
                    }

                    @Override
                    public void onAdError() {
                        LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdError");
                        ApplicationEx.getInstance().setInterstitialAdvertisement(null, CallerAdManager.IN_ADS_CALL_FLASH);
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
        if (FullScreenAdManager.getInstance().isAdLoaded()) {
            FullScreenAdManager.getInstance().showAd(this, CallerAdManager.IN_ADS_CALL_FLASH, new FullScreenAdManager.AdListener() {
                @Override
                public void onAdShow() {
                    LogUtil.d(TAG, "showFullScreenAd  onAdShow");
                    mIsShowFullScreenAd = true;
                    tv_download_action_above_ad.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mSavingDialog != null) {
                                mSavingDialog.dismiss();
                            }
                        }
                    }, 200);
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

    public void onEventMainThread(EventInterstitialAdLoadSuccess event) {
        if (!mIsShowInterstitialAd && mIsShow) {
            LogUtil.d(TAG, "InterstitialAdvertisement EventInterstitialAdLoadSuccess showInterstitialAd");
            showInterstitialAd(mIsBack);
        }
    }

    public void onEventMainThread(EventCallFlashDetailGroupAdShow event) {
        if (!isFinishing()) {
            LogUtil.d(TAG, "onEventMainThread EventCallFlashDetailGroupAdShow");
            showGroupAd(true);
            mIsShowGroupAd = true;
        }
    }

    private void showGroupAd(boolean isResetShowAd) {
        LogUtil.d(TAG, "showBannerGroupAd showGroupAd 1");
        CallFlashDetailGroupAdHelper.getInstance().showGroupAd(
                isResetShowAd,
                mLayourAd,
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
                requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionUtils.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION);
            }
        });
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION:
                LogUtil.d(TAG, "MyPermissionGrant onPermissionGranted CODE_EXTERNAL_STORAGE_PERMISSION");
                if (mIsShowAboveAdBtn) {
                    layout_progress_above_ad.setVisibility(View.VISIBLE);
                    tv_setting_action_above_ad.setVisibility(View.GONE);
                    tv_download_action_above_ad.setVisibility(View.GONE);
                } else {
                    layout_progress_below_ad.setVisibility(View.VISIBLE);
                    tv_setting_action_below_ad.setVisibility(View.GONE);
                    tv_download_action_below_ad.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(mInfo.url)) {
                    downloadFlashResourceFile();
                }
                break;
            case PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION:
                requestSpecialPermission(PermissionUtils.PERMISSION_NOTIFICATION_POLICY_ACCESS);
                break;
            case PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS:
                showSaveDialog();
                break;
        }
    }

    @Override
    public void onPermissionNotGranted(int requestCode) {

    }
    //---------------------------获取权限----------------------------------

}
