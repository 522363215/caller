package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
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

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flurry.android.FlurryAgent;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.manager.CallFlashManager;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.callback.OnDownloadListener;
import com.md.serverflash.callback.ThemeNormalCallback;
import com.md.serverflash.download.ThemeResourceHelper;

import java.io.File;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.FirstShowAdmobUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bednotdisturb.BedsideAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashDownloadCount;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCollection;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshPreviewDowloadState;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.GlideView;
import event.EventBus;

public class CallFlashPreviewActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "CallFlashPreviewActivity";
    private ActionBar mActionBar;
    private CallFlashInfo mInfo;
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
    private FrameLayout mLayoutAd;
    private boolean mIsStartEnterAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_flash_preview);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setTranslucentStatusBar();
        mIsShowFirstAdMob = FirstShowAdmobUtil.isShowFirstAdMob(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_CALL_FLASH_PREVIEW, true);
//        initAds();
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }
        onNewIntent(getIntent());
        FlurryAgent.logEvent("CallFlashPreviewActivity-start");
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        mInfo = (CallFlashInfo) getIntent().getSerializableExtra(ActivityBuilder.CALL_FLASH_INFO);
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
        setBackground();
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

    private void setBackground() {
        if (mInfo.isOnlionCallFlash) {
            LogUtil.d(TAG, "setView loadBackground img_vUrl:" + mInfo.img_vUrl + ",\nthumbnail_imgUrl:" + mInfo.thumbnail_imgUrl);
            mGvPreview.showImageWithThumbnailAndShareAnim(mInfo.img_vUrl, mInfo.thumbnail_imgUrl, new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    LogUtil.d(TAG,"setBackground onResourceReady");
                    startEnterAnim();
                    return false;
                }
            });
        } else {
            mGvPreview.showImage(mInfo.imgResId);
            startEnterAnim();
        }

        //共享动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setTransitionName(mGvPreview, ActivityBuilder.CALL_FLASH_SHARE_PREVIEW);
            //防止没有回调onResourceReady()，造成永远不跳转
            Async.scheduleTaskOnUiThread(200, new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG,"setBackground scheduleTaskOnUiThread");
                    startEnterAnim();
                }
            });
        }
    }

    private void startEnterAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !mIsStartEnterAnim) {
            mIsStartEnterAnim = true;
            startPostponedEnterTransition();
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
                ActivityBuilder.toCallFlashDetail(this, mInfo, getIntent().getBooleanExtra(ActivityBuilder.IS_COME_FROM_DESKTOP, false));
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

    public void onEventMainThread(EventRefreshCallFlashDownloadCount event) {
        setLikeAndDownload();
    }
}
