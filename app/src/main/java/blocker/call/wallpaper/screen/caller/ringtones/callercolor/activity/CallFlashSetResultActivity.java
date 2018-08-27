package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.FirstShowAdmobUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventShowPemisssionTip;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.FullScreenAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.DrawHookView;

/**
 * Created by ChenR on 2017/9/20.
 */

public class CallFlashSetResultActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CallFlashGifShowActivity";

    // setting result
    private View mLayoutContent;
    private View mLayoutTop;
    private View mLayoutCenter;
    private View mLayoutSuccessBig;
    private TextView mTvTopResultTitle;
    private TextView mTvTopResultDes;
    private TextView mTvCenterResultTitle;
    private TextView mTvCenterResultDes;
    private ImageView mIvSuccessSmall;
    private ImageView mIvSuccessBig;
    private DrawHookView mDrawHookView;

    private View mLayoutAd;
    private Advertisement mAdvertisement;

    private boolean mIsAdLoaded;
    private boolean mIsPlayAnimAfterAdLoaded;
    private boolean mIsEndAnimWhenLoadingAd;

    private boolean isFromSystem = false;
    private boolean isShowResult = true;
    private boolean isLoading = false;

    private AnimatorSet mShowResultAnimatorSet;

    private boolean mIsEndAnimAfterAdLoaded = false;

    private long mStartTime;
    private CallFlashInfo mCallFlashInfo;
    private boolean mIsFromDesktop;
    private boolean mIsFirstShowAdmob;
    private MyAdvertisementAdapter mMyAdvertisementAdapter;
    private boolean mIsShowInterstitialAd;
    private boolean mIsSetFailed;
    private boolean mIsComeGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStartTime = 0;
        mIsComeGuide = getIntent().getBooleanExtra(ActivityBuilder.IS_COME_GUIDE, false);
        mIsFromDesktop = getIntent().getBooleanExtra(ActivityBuilder.IS_COME_FROM_DESKTOP, false);
        mIsShowInterstitialAd = getIntent().getBooleanExtra("is_show_interstitial_ad", false);
        mCallFlashInfo = (CallFlashInfo) getIntent().getSerializableExtra(ActivityBuilder.CALL_FLASH_INFO);
        mIsFirstShowAdmob = FirstShowAdmobUtil.isShowFirstAdMob(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_RESULT_FLASH_SET);
        init();
        listener();
        EventBus.getDefault().post(new EventShowPemisssionTip());
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this);
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_call_flash_set_result;
    }

    private void callFlashStatistics() {
        boolean isFlashSwitchOn = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
        if (isFlashSwitchOn) {
            if (mCallFlashInfo == null) return;
            String id = mCallFlashInfo.id;
            String key = id;
            Map<String, String> eventParams = new HashMap<>();
            eventParams.put(key, key);
            FlurryAgent.logEvent("callflash_set_success_count_id", eventParams);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsComeGuide) {
            FlurryAgent.logEvent("CallFlashSetResultActivity---comeGuide--show_main");
        } else {
            FlurryAgent.logEvent("CallFlashSetResultActivity---Normal--show_main");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdvertisement != null) {
            mAdvertisement.close();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_scan_result_des:
            case R.id.tv_scan_result_des2:
                if (mIsComeGuide && mIsSetFailed) {
                    FlurryAgent.logEvent("CallFlashSetResultActivity---comeGuide--click_set_permission");
                    ActivityBuilder.toPermissionActivity(this, false);
                    finish();
                }
                break;
        }
    }

    private void listener() {
        ((ActionBar) findViewById(R.id.actionbar)).setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showResult() {
        mStartTime = System.currentTimeMillis();
        mLayoutTop.setVisibility(View.GONE);
        mLayoutSuccessBig.setVisibility(View.VISIBLE);

        alphaAnimation2();
        setResultAnimator2(mLayoutSuccessBig);
        mShowResultAnimatorSet.start();
        mShowResultAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isFinishing()) {
                    return;
                }
                try {
                    mDrawHookView.setVisibility(View.VISIBLE);
                    if (mIsSetFailed) {
                        mDrawHookView.setDrawType(DrawHookView.TYPE_DRAW_CROSS);
                    } else {
                        mDrawHookView.setDrawType(DrawHookView.TYPE_DRAW_HOOK);
                    }
                    mDrawHookView.setAnimListener(new DrawHookView.AnimListener() {
                        @Override
                        public void onAnimFinish() {
                            mIsEndAnimWhenLoadingAd = true;
                            if (mIsAdLoaded && !mIsPlayAnimAfterAdLoaded) startAnimAfterAdLoaded();
                            //显示插屏
//                showInterstitialAd(true);
                        }
                    });
                    mDrawHookView.Start();
                } catch (Exception e) {

                }
            }
        });
    }

    private void setResultAnimator2(View oldView) {
        mShowResultAnimatorSet = new AnimatorSet();
        Animator[] showAnimator = new Animator[2];
        showAnimator[0] = ObjectAnimator.ofFloat(oldView, "scaleX", 0.0f, 1.0f);
        showAnimator[1] = ObjectAnimator.ofFloat(oldView, "scaleY", 0.0f, 1.0f);
        mShowResultAnimatorSet.playTogether(showAnimator);
        mShowResultAnimatorSet.setDuration(1000);
    }

    private void alphaAnimation2() {
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(1500);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                try {
                    mTvCenterResultTitle.setVisibility(View.VISIBLE);
                    mTvCenterResultDes.setVisibility(View.VISIBLE);
                } catch (Exception e) {

                }

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (mTvCenterResultTitle != null && mTvCenterResultDes != null) {
            mTvCenterResultTitle.startAnimation(anim);
            mTvCenterResultDes.startAnimation(anim);
        }
    }

    private void init() {
        initAds();
        initView();
    }

    private void initView() {
        mLayoutContent = findViewById(R.id.layout_content);
        mLayoutTop = findViewById(R.id.layout_top);
        mTvTopResultTitle = (TextView) findViewById(R.id.tv_scan_result_title);
        mTvTopResultDes = (TextView) findViewById(R.id.tv_scan_result_des);
        mIvSuccessSmall = (ImageView) findViewById(R.id.iv_success_small);

        mLayoutCenter = findViewById(R.id.layout_result);
        mTvCenterResultTitle = (TextView) findViewById(R.id.tv_scan_result_title2);
        mTvCenterResultDes = (TextView) findViewById(R.id.tv_scan_result_des2);

        mIvSuccessBig = (ImageView) findViewById(R.id.iv_success_big_bg);
        mDrawHookView = (DrawHookView) findViewById(R.id.drawHookView);
        mDrawHookView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mLayoutSuccessBig = findViewById(R.id.layout_success_big);

        mLayoutAd = findViewById(R.id.layout_ad_view);

        mTvCenterResultDes.setOnClickListener(this);
        mTvTopResultDes.setOnClickListener(this);

        String showContent = getIntent().getStringExtra("result_des");
        mTvCenterResultDes.setText(showContent);
        mTvTopResultDes.setText(showContent);

        if (getString(R.string.permission_denied_txt2).equals(showContent)) {
            mIsSetFailed = true;
            mTvTopResultTitle.setText(R.string.call_flash_gif_show_setting_des2);
            mTvCenterResultTitle.setText(R.string.call_flash_gif_show_setting_des2);
            mIvSuccessBig.setBackgroundResource(R.drawable.shape_circle_red);
            mIvSuccessSmall.setBackgroundResource(R.drawable.call_flash_set_failed_bg);

            mTvTopResultDes.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            mTvTopResultDes.getPaint().setAntiAlias(true);//抗锯齿
            mTvCenterResultDes.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            mTvCenterResultDes.getPaint().setAntiAlias(true);//抗锯齿
            if (mIsComeGuide) {
                FlurryAgent.logEvent("CallFlashSetResultActivity---comeGuide--setFailed");
            }
        }

        showResult();
        callFlashStatistics();
    }

    //广告出来后的动画
    private void startAnimAfterAdLoaded() {
        mIsPlayAnimAfterAdLoaded = true;
        alphaAnimation();
        setResultAnimator(mLayoutSuccessBig);
        mShowResultAnimatorSet.start();
        mShowResultAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                backgroundViewSlideAnimation();
            }
        });
    }

    private void backgroundViewSlideAnimation() {
        ValueAnimator anim = ValueAnimator.ofInt(DeviceUtil.getScreenHeight(), 0);
        anim.setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                try {
                    if (!isFinishing() && mLayoutContent != null) {
                        mLayoutContent.setVisibility(View.VISIBLE);
                        int value = (Integer) arg0.getAnimatedValue();
                        mLayoutContent.animate().translationY(value)
                                .setDuration(0).start();
                    }
                } catch (Exception e) {
                    LogUtil.e("numberscan", "backgroundViewSlideAnimation exception: " + e.getMessage());
                }
            }
        });

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator arg0) {
                if (!isFinishing() && mLayoutTop != null) {
                    mLayoutTop.setVisibility(View.VISIBLE);
                }
                if (!isFinishing() && mLayoutAd != null) {
                    mLayoutAd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                mIsEndAnimAfterAdLoaded = true;
//                if (!isFinishing() && mLayoutAd != null) {
//                    mLayoutAd.setVisibility(View.VISIBLE);
////                    startAdAnim();
//                }
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
            }
        });
        anim.setStartDelay(100);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim);
        animatorSet.start();
    }

    private void setResultAnimator(View oldView) {
        mShowResultAnimatorSet = new AnimatorSet();
        Animator[] showAnimator = new Animator[2];
        showAnimator[0] = ObjectAnimator.ofFloat(oldView, "scaleX", 1.0f, 0.0f);
        showAnimator[1] = ObjectAnimator.ofFloat(oldView, "scaleY", 1.0f, 0.0f);
        mShowResultAnimatorSet.playTogether(showAnimator);
        mShowResultAnimatorSet.setDuration(500);
    }

    private void alphaAnimation() {
        AlphaAnimation anim = new AlphaAnimation(1, 0);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    mTvCenterResultTitle.setVisibility(View.GONE);
                    mTvCenterResultDes.setVisibility(View.GONE);
                } catch (Exception e) {

                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTvCenterResultTitle.startAnimation(anim);
        mTvCenterResultDes.startAnimation(anim);
    }

    @Override
    public void finish() {
        if (!isFromSystem && isLoading) return;
        if (mIsEndAnimWhenLoadingAd) {
            LogUtil.d(TAG, "onDestroy InitInterstitialAd");
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (mIsEndAnimAfterAdLoaded || (System.currentTimeMillis() - mStartTime > 2000)) {
            if (InterstitialAdUtil.isShowFullScreenAd(InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL)) {
                showFullScreenAd();
            } else {
                showInterstitialAd();
            }
        }
    }

    private void onFinish() {
        isShowResult = getIntent().getBooleanExtra("is_show_result", true);
        if (isShowResult && !mIsFromDesktop) {
            ActivityBuilder.toMain(this, ActivityBuilder.FRAGMENT_HOME);
        } else {
            finish();
        }
    }

    //**************************************AD****************************************//
    private void showInterstitialAd() {
        InterstitialAdvertisement interstitialAdvertisement = ApplicationEx.getInstance().getInterstitialAdvertisement(InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
        if (interstitialAdvertisement == null || mIsShowInterstitialAd) {
            onFinish();
        } else {
            onFinish();
            interstitialAdvertisement.show(new InterstitialAdvertisement.InterstitialAdShowListener() {
                @Override
                public void onAdClosed() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdClosed");
                    onFinish();
                    ApplicationEx.getInstance().setInterstitialAdvertisement(null, InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
                }

                @Override
                public void onAdShow() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdShow");
                }

                @Override
                public void onAdError() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdError");
                    ApplicationEx.getInstance().setInterstitialAdvertisement(null, InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
                }
            });
        }
    }

    private void showFullScreenAd() {
        if (FullScreenAdManager.getInstance().isAdLoaded() && !mIsShowInterstitialAd) {
            FullScreenAdManager.getInstance().showAd(this, InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL, new FullScreenAdManager.AdListener() {
                @Override
                public void onAdShow() {
                    LogUtil.d(TAG, "showFullScreenAd  onAdShow");
                }


                @Override
                public void onAdClose() {
                    LogUtil.d(TAG, "showFullScreenAd  onAdClosed");
                    FullScreenAdManager.getInstance().clear();
                    onFinish();
                }

                @Override
                public void onAdClick() {
                    onAdClose();
                }
            });
        } else {
            onFinish();
        }
    }

    private void initAds() {
        String placementId = AdvertisementSwitcher.SERVER_KEY_SET_RESULT;
        String admobId = CallerAdManager.getAdmob_id(CallerAdManager.POSITION_ADMOB_RESULT_NORMAL);
        if (mIsFirstShowAdmob) {
            placementId = AdvertisementSwitcher.SERVER_KEY_FIRST_SHOW_ADMOB;
            admobId = FirstShowAdmobUtil.getAdmobIdForFirst(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_RESULT_FLASH_SET);
        }
        mMyAdvertisementAdapter = new MyAdvertisementAdapter(getWindow().getDecorView(),
                CallerAdManager.getFacebook_id(CallerAdManager.POSITION_FB_RESULT_NORMAL),
                admobId,
                Advertisement.ADMOB_TYPE_NATIVE,
                "",
                Advertisement.MOPUB_TYPE_NATIVE,
                -1,
                "",
                placementId,
                false);

        mAdvertisement = new Advertisement(mMyAdvertisementAdapter);

        mAdvertisement.setRefreshWhenClicked(false);
        mAdvertisement.setIsResultPage(true);
        mAdvertisement.refreshAD(true);
        mAdvertisement.enableFullClickable();
        if (CallerAdManager.isOnlyBtnClickable(CallerAdManager.POSITION_FB_ADS_SCAN_RESULT_BIG)) {
            mAdvertisement.enableOnlyBtnClickable();
        }
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {
        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, String placementId, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner, placementId);
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baidukey, String eventKey, String placementId, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baidukey, eventKey, placementId, isBanner);
        }

        @Override
        public int getFbAdsHight() {
            return DeviceUtil.getScreenHeight() - getResources().getDimensionPixelOffset(R.dimen.dp56) - mLayoutTop.getHeight();
        }

        @Override
        public void onAdClicked(boolean isAdmob) {
            super.onAdClicked(isAdmob);
            if (mIsFirstShowAdmob) {
                mIsFirstShowAdmob = false;
                initAds();
            }
        }

        @Override
        public void onAdLoaded() {
            LogUtil.d(TAG, "MyAdvertisementAdapter onAdLoaded");
            mIsAdLoaded = true;
            try {
                if (!mIsPlayAnimAfterAdLoaded) {
                    startAnimAfterAdLoaded();
                }
            } catch (Exception e) {
            }
        }


        @Override
        public void onAdShow() {
            super.onAdShow();
            if (mIsFirstShowAdmob) {
                FirstShowAdmobUtil.saveFirstShowAdmobTime(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_RESULT_FLASH_SET);
            }
        }

        @Override
        public int getAdmobHeight() {
            return DeviceUtil.getScreenHeight() - getResources().getDimensionPixelOffset(R.dimen.dp56) - mLayoutTop.getHeight();

        }

        @Override
        public int getFbViewRes() {
            return isBanner() ? R.layout.facebook_native_ads_banner_50 : R.layout.facebook_native_ads_flash_set_result;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            if (mIsFirstShowAdmob) {
                return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_result_first_show : R.layout.layout_admob_advanced_content_ad_result_first_show;
            }
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_result : R.layout.layout_admob_advanced_content_ad_result;
        }
    }
    //**************************************AD****************************************//
}
