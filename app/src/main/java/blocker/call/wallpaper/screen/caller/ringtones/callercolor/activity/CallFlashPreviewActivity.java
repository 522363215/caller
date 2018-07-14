package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.individual.sdk.BaseAdContainer;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.callback.ThemeNormalCallback;
import com.md.serverflash.download.ThemeResourceHelper;
import com.mopub.test.manager.TestManager;

import java.io.File;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bednotdisturb.BedsideAdContainer;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bednotdisturb.BedsideAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashDownloadCount;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCollection;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshPreviewDowloadState;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import event.EventBus;

public class CallFlashPreviewActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "CallFlashPreviewActivity";
    private ActionBar mActionBar;
    private Advertisement mAdvertisement;
    private CallFlashInfo mInfo;
    private boolean mIsOnlineCallFlash;
    private FrameLayout mLayoutAd;
    private CallFlashInfo recommendCallFlashInfo1;
    private CallFlashInfo recommendCallFlashInfo2;
    private FontIconView mFivBack;
    private LinearLayout mLavoutLikeAndDownload;
    private FontIconView mFivLike;
    private TextView mTvLikeCount;
    private float mLastValue = 1.0f;
    //    private ThemeDownloadListener mThemeDownloadListener;
    private OnDownloadListener mOnDownloadListener;
    private boolean mIsDownloading;
    private BedsideAdManager mAdManager;
    private boolean mIsShowAd;
    private boolean mIsShowFirstAdMob;
    private ProgressBar mPbDownloadingAboveAd;
    private TextView mTvDownloadingAboveAd;
    private View mLayoutDownloadingAboveAd;
    private ProgressBar mPbDownloadingBelowAd;
    private TextView mTvDownloadingBelowAd;
    private View mLayoutDownloadingBelowAd;
    private View mLayoutButtonAboveAd;
    private View mLayoutButtonBelowAd;
    private TextView mTvDownloadBtnAboveAd;
    private TextView mTvDownloadBtnBelowAd;
    private LinearLayout mLayoutAdNormal;
    private FrameLayout mLayoutAdMopub;
    private FontIconView mFivDownload;
    private TextView mTvDownloadCount;
    private GlideView mGvPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_flash_preview);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setTranslucentStatusBar();
        mIsShowFirstAdMob = CallerAdManager.isShowFirstAdMob(CallerAdManager.POSITION_FIRST_ADMOB_CALL_FLASH_PREVIEW, true);
//        initAds();
        initView();
        onNewIntent(getIntent());
        FlurryAgent.logEvent("CallFlashPreviewActivity-start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
//        if (mThemeDownloadListener != null) {
//            ThemeDownloadApi.removeGeneralListener(mThemeDownloadListener);
//        }
        if (mOnDownloadListener != null) {
            ThemeResourceHelper.getInstance().removeGeneralListener(mOnDownloadListener);
        }
        destroyAd();
    }

    private void setTranslucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatus();
        } else {
            CommonUtils.translucentStatusBar(this, true);
        }
        setImmerseLayout((ActionBar) findViewById(R.id.actionbar));
    }

    protected void setImmerseLayout(View view) {// view为标题栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = DeviceUtil.getStatusBarHeight();
            view.setPadding(0, statusBarHeight, 0, 0);
        }
    }

    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            View statusView = createStatusView();
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            decorView.addView(statusView);
        }
    }

    private View createStatusView() {
        View view = new View(this);
        int statusBarHeight = DeviceUtil.getStatusBarHeight();
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
        view.setBackgroundColor(getResources().getColor(R.color.color_transparent));

        return view;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mInfo = (CallFlashInfo) getIntent().getSerializableExtra("flash_theme");
        mIsOnlineCallFlash = intent.getBooleanExtra(ConstantUtils.IS_ONLINE_FOR_CALL_FLASH, false);
        downloadListener();
        setView();
//        setRecommend();
        setLikeAndDownload();
    }

    private void downloadListener() {
        ThemeResourceHelper.getInstance().addGeneralListener(mOnDownloadListener = new OnDownloadListener() {
            @Override
            public void onFailure(String url) {
                if (mInfo != null && !TextUtils.isEmpty(url) && url.equals(mInfo.url)) {
                    mIsDownloading = false;
                    if (mIsShowFirstAdMob) {
                        mLayoutDownloadingBelowAd.setVisibility(View.GONE);
                        mTvDownloadBtnBelowAd.setVisibility(View.VISIBLE);
                        mTvDownloadBtnBelowAd.setText(R.string.lion_family_active_download);
                    } else {
                        mLayoutDownloadingAboveAd.setVisibility(View.GONE);
                        mTvDownloadBtnAboveAd.setVisibility(View.VISIBLE);
                        mTvDownloadBtnAboveAd.setText(R.string.lion_family_active_download);
                    }
                }
            }

            @Override
            public void onFailureForIOException(String url) {
                onFailure(url);
            }

            @Override
            public void onProgress(String url, int progress) {
                if (mInfo != null && !TextUtils.isEmpty(url) && url.equals(mInfo.url)) {
                    mIsDownloading = true;
                    if (mIsShowFirstAdMob) {
                        mLayoutDownloadingBelowAd.setVisibility(View.VISIBLE);
                        mTvDownloadBtnBelowAd.setVisibility(View.GONE);
                        mPbDownloadingBelowAd.setProgress(progress);
                        mTvDownloadingBelowAd.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, progress)));
                    } else {
                        mLayoutDownloadingAboveAd.setVisibility(View.VISIBLE);
                        mTvDownloadBtnAboveAd.setVisibility(View.GONE);
                        mPbDownloadingAboveAd.setProgress(progress);
                        mTvDownloadingAboveAd.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, progress)));
                    }
                }
            }

            @Override
            public void onSuccess(String url, File file) {
                if (mInfo != null && !TextUtils.isEmpty(url) && url.equals(mInfo.url)) {
                    mIsDownloading = false;
                    CallFlashManager.getInstance().saveCallFlashDownloadCount(mInfo);
                    EventBus.getDefault().post(new EventRefreshCallFlashDownloadCount());
                    if (mIsShowFirstAdMob) {
                        mLayoutDownloadingBelowAd.setVisibility(View.GONE);
                        mTvDownloadBtnBelowAd.setVisibility(View.VISIBLE);
                        mTvDownloadBtnBelowAd.setText(R.string.call_flash_preview_now);
                    } else {
                        mLayoutDownloadingAboveAd.setVisibility(View.GONE);
                        mTvDownloadBtnAboveAd.setVisibility(View.VISIBLE);
                        mTvDownloadBtnAboveAd.setText(R.string.call_flash_preview_now);
                    }
                }
            }
        });

//        ThemeDownloadApi.addGeneralListener(mThemeDownloadListener = new ThemeDownloadListener() {
//            @Override
//            public void onDownloadSuccess(String s, File file) {
//                if (mInfo != null && !TextUtils.isEmpty(s) && s.equals(mInfo.url)) {
//                    mIsDownloading = false;
//                    mLayoutDownloading.setVisibility(View.GONE);
//                    mTvDownload.setVisibility(View.VISIBLE);
//                    mTvDownload.setText(R.string.call_flash_preview_now);
//                }
//            }
//
//            @Override
//            public void onDownloading(String s, int i) {
//                if (mInfo != null && !TextUtils.isEmpty(s) && s.equals(mInfo.url)) {
//                    mIsDownloading = true;
//                    mLayoutDownloading.setVisibility(View.VISIBLE);
//                    mTvDownload.setVisibility(View.GONE);
//                    mPbDownloading.setProgress(i);
//                    mTvDownloading.setText(Html.fromHtml(getString(R.string.call_flash_gif_show_load, i)));
//                }
//            }
//
//            @Override
//            public void onDownloadFailed(String s) {
//                if (mInfo != null && !TextUtils.isEmpty(s) && s.equals(mInfo.url)) {
//                    mIsDownloading = false;
//                    mLayoutDownloading.setVisibility(View.GONE);
//                    mTvDownload.setVisibility(View.VISIBLE);
//                    mTvDownload.setText(R.string.lion_family_active_download);
//                }
//            }
//        });
    }

    private void initView() {
        mActionBar = (ActionBar) findViewById(R.id.actionbar);
        mFivBack = (FontIconView) mActionBar.findViewById(R.id.imgReturn);
        mGvPreview = (GlideView) findViewById(R.id.gv_preview);
        //点赞数和下载数
        mLavoutLikeAndDownload = (LinearLayout) findViewById(R.id.layout_like_and_download);
        mFivLike = (FontIconView) findViewById(R.id.fiv_like);
        mTvLikeCount = (TextView) findViewById(R.id.tv_like_count);
        mFivDownload = (FontIconView) findViewById(R.id.fiv_download);
        mTvDownloadCount = (TextView) findViewById(R.id.tv_download_count);

        //广告
        mLayoutAd = (FrameLayout) findViewById(R.id.layout_ad_view);
        mLayoutAdNormal = (LinearLayout) findViewById(R.id.layout_ad_view_normal);
        mLayoutAdMopub = (FrameLayout) findViewById(R.id.layout_ad_view_mopub);

        //above ad
        mLayoutButtonAboveAd = findViewById(R.id.layout_button_above_ad);
        mTvDownloadBtnAboveAd = (TextView) findViewById(R.id.tv_download_action_above_ad);
        mPbDownloadingAboveAd = (ProgressBar) findViewById(R.id.pb_downloading_above_ad);
        mTvDownloadingAboveAd = (TextView) findViewById(R.id.tv_downloading_above_ad);
        mLayoutDownloadingAboveAd = findViewById(R.id.layout_progress_above_ad);

        //below ad
        mLayoutButtonBelowAd = findViewById(R.id.layout_button_below_ad);
        mTvDownloadBtnBelowAd = (TextView) findViewById(R.id.tv_download_action_below_ad);
        mPbDownloadingBelowAd = (ProgressBar) findViewById(R.id.pb_downloading_below_ad);
        mTvDownloadingBelowAd = (TextView) findViewById(R.id.tv_downloading_below_ad);
        mLayoutDownloadingBelowAd = findViewById(R.id.layout_progress_below_ad);

        if (mIsShowFirstAdMob) {
            mLayoutButtonAboveAd.setVisibility(View.GONE);
            mLayoutButtonBelowAd.setVisibility(View.VISIBLE);
        } else {
            mLayoutButtonAboveAd.setVisibility(View.VISIBLE);
            mLayoutButtonBelowAd.setVisibility(View.GONE);
        }

        mActionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mGvPreview.setOnClickListener(this);
        mTvDownloadBtnAboveAd.setOnClickListener(this);
        mTvDownloadBtnBelowAd.setOnClickListener(this);
        mFivLike.setOnClickListener(this);
        mFivBack.setShadowLayer(10f, 0, 0, 0xff000000);
    }

    private void setView() {
        if (mInfo == null) return;
        if (mIsOnlineCallFlash) {
            LogUtil.d(TAG, "setView loadBackground img_vUrl:" + mInfo.img_vUrl + ",\nthumbnail_imgUrl:" + mInfo.thumbnail_imgUrl);
            mGvPreview.showImageWithThumbnail(mInfo.img_vUrl, mInfo.thumbnail_imgUrl);
        } else {
            mGvPreview.showImage(mInfo.imgResId);
        }

        File file = ThemeSyncManager.getInstance().getFileByUrl(ApplicationEx.getInstance().getApplicationContext(), mInfo.url);
        if ((file != null && file.exists()) || (!TextUtils.isEmpty(mInfo.path) && new File(mInfo.path).exists())) {
            if (mIsShowFirstAdMob) {
                mLayoutDownloadingBelowAd.setVisibility(View.GONE);
                mTvDownloadBtnBelowAd.setVisibility(View.VISIBLE);
                mTvDownloadBtnBelowAd.setText(R.string.call_flash_preview_now);
            } else {
                mLayoutDownloadingAboveAd.setVisibility(View.GONE);
                mTvDownloadBtnAboveAd.setVisibility(View.VISIBLE);
                mTvDownloadBtnAboveAd.setText(R.string.call_flash_preview_now);
            }
        } else {
            if (mIsDownloading) {
                if (mIsShowFirstAdMob) {
                    mTvDownloadBtnBelowAd.setVisibility(View.GONE);
                } else {
                    mTvDownloadBtnAboveAd.setVisibility(View.GONE);
                }
            } else {
                if (mIsShowFirstAdMob) {
                    mTvDownloadBtnBelowAd.setVisibility(View.VISIBLE);
                    mTvDownloadBtnBelowAd.setText(R.string.lion_family_active_download);
                } else {
                    mTvDownloadBtnAboveAd.setVisibility(View.VISIBLE);
                    mTvDownloadBtnAboveAd.setText(R.string.lion_family_active_download);
                }
            }
        }
    }

    private void setLikeAndDownload() {
        //点赞
        if (mInfo == null) return;
        if (!mInfo.isOnlionCallFlash) {
            mLavoutLikeAndDownload.setVisibility(View.GONE);
            return;
        }
        CallFlashInfo cacheCallFlashInfo = CallFlashManager.getInstance().getCacheJustLikeFlashList(mInfo.id);
        if (cacheCallFlashInfo != null) {
            mInfo.likeCount = cacheCallFlashInfo.likeCount;
            mInfo.downloadCount = cacheCallFlashInfo.downloadCount;
            mInfo.isLike = cacheCallFlashInfo.isLike;
        }
        mTvLikeCount.setText("" + mInfo.likeCount);
        mTvDownloadCount.setText("" + mInfo.downloadCount);
        if (mInfo.isLike) {
            mFivLike.setTextColor(getResources().getColor(R.color.color_FFE05A52));
        } else {
            mFivLike.setTextColor(getResources().getColor(R.color.whiteSmoke));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.gv_preview:
            case R.id.tv_download_action_above_ad:
            case R.id.tv_download_action_below_ad:
                ActivityBuilder.toCallFlashDetail(this, mInfo, getIntent().getBooleanExtra(ConstantUtils.COME_FROM_DESKTOP, false));
                break;
            case R.id.fiv_like:
                if (mInfo != null) {
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
                }
                break;

        }
    }

    /**
     * @param isLike true:上传收藏，flash:上传取消收藏
     */
    private void uploadLike(boolean isLike) {
        if (mInfo == null) return;
//        JSONObject callFlashLikeJson = JsonUtil.createCallFlashLikeJson(mInfo.id, isLike);
//        LogUtil.d(TAG, "uploadCLike callFlashLikeJson:" + callFlashLikeJson);
//        ServerManager.getInstance().requestData(callFlashLikeJson, ConstantUtils.SERVER_API_CALLER_SHOW_LIKE, new ServerManager.UploadNumberInfoCallBack() {
//            @Override
//            public void onRequest(boolean hasSuccess, String data) {
//                LogUtil.d(TAG, "uploadLike data:" + data + ",hasSuccess:" + hasSuccess);
//            }
//        });

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

    public void onEventMainThread(EventRefreshPreviewDowloadState event) {
        if (mIsShowFirstAdMob) {
            mTvDownloadBtnBelowAd.setText(R.string.call_flash_preview_now);
        } else {
            mTvDownloadBtnAboveAd.setText(R.string.call_flash_preview_now);
        }
    }

    private void initAds() {
        if (CallerAdManager.isMopubBannerSelf(AdvertisementSwitcher.SERVER_KEY_FLASH_PREVIEW)) {
            MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(getWindow().getDecorView(),
                    "",//ConstantUtils.FB_AFTER_CALL_ID
                    "",//ConstantUtils.ADMOB_AFTER_CALL_NATIVE_ID
                    Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,//Advertisement.ADMOB_TYPE_NATIVE, Advertisement.ADMOB_TYPE_NONE
                    CallerAdManager.MOPUB_NATIVE_ADV_BIG_CALL_AFTER_ID,
                    Advertisement.MOPUB_TYPE_NATIVE,
                    -1,
                    "",
                    false);

            String mopub_banner_id = TestManager.getInstance(this.getApplicationContext()).getMopubId(AdvertisementSwitcher.SERVER_KEY_FLASH_PREVIEW);
            LogUtil.d("mopub_self", "mopub_banner_id flash preview: " + mopub_banner_id);
            adapter.setMopubBannerKey(mopub_banner_id); //mopub banner id
            mAdvertisement = new Advertisement(adapter);

            mAdvertisement.setRefreshWhenClicked(false);
            mAdvertisement.refreshAD(true);

        } else {
            initAd();
        }
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner, AdvertisementSwitcher.SERVER_KEY_FLASH_PREVIEW);
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baiduKey, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey, eventKey, AdvertisementSwitcher.SERVER_KEY_FLASH_PREVIEW, isBanner);
        }

        @Override
        public void onAdLoaded() {
//            try {
//                LinearLayout fbContainerView = getFbContainerView();
//
//                TextView tvAction = (TextView) fbContainerView.findViewById(R.id.nativeAdCallToAction);
//                TextView tvActionVisible = (TextView) fbContainerView.findViewById(R.id.nativeAdCallToActionCopy);
//                tvActionVisible.setText(tvAction.getText());
//
//                final View viewSelected = fbContainerView.findViewById(R.id.nativeAdCallToDetails);
//
//                setLayoutReverse(viewSelected);
//                setHeartAnim(viewSelected);
//            } catch (Exception e) {
//                LogUtil.e(ConstantUtils.NM_TAG, "CallAfterActivity onAdLoaded: " + e.getMessage());
//            }
            try {
                mLayoutAd.setVisibility(View.VISIBLE);
                mLayoutAdMopub.setVisibility(View.GONE);
                mLayoutAdNormal.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                LogUtil.e(ConstantUtils.NM_TAG, "call flash preview onAdLoaded exception: " + e.getMessage());
            }

        }


        private void setLayoutReverse(View view) {
            if (LanguageSettingUtil.isLayoutReverse(CallFlashPreviewActivity.this)) {
                view.setRotation(180);
            }
        }


        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.facebook_native_ads_banner_50 : R.layout.facebook_no_icon_native_ads_call_after_big;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_callafter : R.layout.layout_admob_advanced_content_ad_callafter;
        }

        @Override
        public int getMoPubViewRes() {
            return mIsBanner ? R.layout.layout_mopub_ad_banner : R.layout.layout_mopub_native_big_call_after;
        }

        @Override
        public int getBaiDuViewRes() {
            return mIsBanner ? R.layout.layout_du_ad_banner : R.layout.layout_du_ad_call_after_big;
        }

        @Override
        public int getAdmobHeight() {
            return 180;
        }
    }

    private void initAd() {
        //LogUtil.e(TAG, "initAd, AD_SHOW_DELAY:" + AD_SHOW_DELAY);
        mAdManager = new BedsideAdManager(this, false, new BedsideAdContainer.Callback() {
            @Override
            public void onAdClick(BaseAdContainer ad) {

            }

            @Override
            public void onAdLoaded(BedsideAdContainer ad) {
                if (ad != null && !mIsShowAd) {
                    mLayoutAd.setVisibility(View.VISIBLE);
                    mLayoutAdMopub.setVisibility(View.VISIBLE);
                    mLayoutAdNormal.setVisibility(View.GONE);
                    mIsShowAd = true;
                    ad.show(mLayoutAdMopub);
                }
            }
        });
    }

    public void showAd() {
        // 正常展示在前端才进行展示&请求
        if (mAdManager != null && !mIsShowAd) {
            LogUtil.d(TAG, "showAN");
            mIsShowAd = mAdManager.show(this, mLayoutAdMopub);
        } else {
            LogUtil.d(TAG, "showAN failed! cause manager is " + (mAdManager == null ? "Null" : "NotNull"));
        }
    }

    private void destroyAd() {
        if (mAdManager != null) {
            mAdManager.destroy();
            mAdManager = null;
        }
    }

    public void onEventMainThread(EventRefreshCallFlashDownloadCount event) {
        setLikeAndDownload();
    }
}
