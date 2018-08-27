package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.FirstShowAdmobUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.AdPreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CircleProgressBar;

public class SplashActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SplashActivity";
    protected static long AD_FIRST_INSTALL_LOAD_TIME = 6500;//6500
    protected static long AD_FIRST_INSTALL_LOAD_TIME_ADMOB = 10000;//10sec
    protected static long AD_MAX_LOAD_TIME = 4500;
    protected static long AD_SHOW_TIME = 4000;
    protected static long FIRST_AD_SHOW_TIME = 4000;
    private static final long SPLASH_AD_SHOW_INTERVAL_TIME = 10 * 60 * 1000;
    private LanguageSettingUtil languageSetting;
    Advertisement mAdvertisement;
    private boolean mIsShowFristAdMob;
    private View mLayoutLoading;
    private LinearLayout mLayoutInit;
    private ProgressBar mPbInit;
    private RelativeLayout mLayoutNoInit;
    private View mLayoutLoadCompleted;
    private View mLayoutSkipRoot;
    private CircleProgressBar mPbSkip;
    private ValueAnimator mInitAnimator;
    private boolean mIsAdLoaded;
    private boolean mIsShowAd;
    private RelativeLayout mLayoutAnim;
    private LinearLayout mLayoutAppInfo;
    private ImageView mCircleImageView1;
    private ImageView mCircleImageView2;
    private ImageView mCircleImageView3;
    private ImageView mCircleImageView4;
    private ImageView mCircleImageView1Blur;
    private ImageView mCircleImageView2Blur;
    private ImageView mCircleImageView3Blur;
    private ImageView mCircleImageView4Blur;
    private RelativeLayout mLayoutCircle1;
    private RelativeLayout mLayoutCircle2;
    private RelativeLayout mLayoutCircle3;
    private RelativeLayout mLayoutCircle4;
    private ImageView mCircleImageView5;
    private ImageView mCircleImageView6;
    private ImageView mCircleImageView5Blur;
    private ImageView mCircleImageView6Blur;
    private RelativeLayout mLayoutCircle5;
    private RelativeLayout mLayoutCircle6;
    private int[] mSplashAnimIconResIds;
    private int[] mSplashAnimIconBlurResIds;
    private boolean mIsShowInitialization;
    private ValueAnimator mAdCountDownAnim;
    private ObjectAnimator mAlphAnimator1;
    private AnimatorSet mTranslationAnimatorSet;
    private ObjectAnimator mAlphAnimator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //离上次显示Splash ad 的时间小于15分钟则自己跳过splash 进入MainActivity
        long lastShowAdTime = PreferenceHelper.getLong(PreferenceHelper.PREF_LAST_SHOW_SPLASH_AD_TIME, 0);
        if (System.currentTimeMillis() - lastShowAdTime <= SPLASH_AD_SHOW_INTERVAL_TIME) {
            toMain();
            return;
        }
        SwitchLang();
        initData();
        initAds();
        initView();
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this, false);
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_splash;
    }

    private void initData() {
        AD_FIRST_INSTALL_LOAD_TIME = AdPreferenceHelper.getLong("pref_splash_ad_first_load", 6500);
        AD_MAX_LOAD_TIME = AdPreferenceHelper.getLong("pref_splash_ad_load", 4500);
        AD_SHOW_TIME = AdPreferenceHelper.getLong("pref_splash_ad_show", 4000);

        mIsShowFristAdMob = FirstShowAdmobUtil.isShowFirstAdMob(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_SPLASH);
        if (mIsShowFristAdMob) {
            //不管首次广告是否加载出来下次都不显示首次admob
            FirstShowAdmobUtil.saveFirstShowAdmobTime(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_SPLASH);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("SplashActivity-----show_main");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layout_skip_root:
                if (mAdCountDownAnim != null) {
                    mAdCountDownAnim.end();
                    mAdCountDownAnim.cancel();
                }
                toMain();
                break;
        }
    }

    private void SwitchLang() {
        //加载语言
        languageSetting = LanguageSettingUtil.getInstance(getApplicationContext());
        if (languageSetting != null)
            languageSetting.refreshLanguage(this);
    }

    private void initView() {
        //正在加载
        mLayoutLoading = findViewById(R.id.rl_loading);
        mLayoutInit = (LinearLayout) findViewById(R.id.layout_initialization);
        mPbInit = findViewById(R.id.pb_init);
        mLayoutNoInit = (RelativeLayout) findViewById(R.id.layout_no_initialization);
        mLayoutAnim = (RelativeLayout) findViewById(R.id.layout_splash_anim);
        mLayoutAppInfo = (LinearLayout) findViewById(R.id.layout_app_info);


        //加载完成显示广告
        mLayoutLoadCompleted = findViewById(R.id.rl_load_completed);
        mLayoutSkipRoot = findViewById(R.id.layout_skip_root);
        mPbSkip = findViewById(R.id.pb_skip);
        mLayoutSkipRoot.setOnClickListener(this);

        mLayoutLoading.setVisibility(View.VISIBLE);
        mLayoutLoadCompleted.setVisibility(View.GONE);

        mIsShowInitialization = PreferenceHelper.getLong(PreferenceHelper.PREF_LAST_SHOW_SPLASH_INIT_TIME, 0) <= 0;
        if (mIsShowInitialization) {
            showInitialization();
        } else {
            showNoInitialization();
        }
    }

    private void showNoInitialization() {
        mLayoutInit.setVisibility(View.GONE);
        mLayoutNoInit.setVisibility(View.VISIBLE);
        setAnimColors();
        startSplashAnim();
    }

    private void startSplashAnim() {
        mAlphAnimator1 = ObjectAnimator.ofFloat(mLayoutAnim, "alpha", 0f, 1.0f);
        mAlphAnimator1.setDuration(1000);
        mAlphAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startTranslationAnim();
            }
        });
        mAlphAnimator1.start();
    }

    private void startTranslationAnim() {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mLayoutCircle1, "translationY", 0, -mLayoutAnim.getHeight());
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mLayoutCircle2, "translationY", 0, -mLayoutAnim.getHeight());
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mLayoutCircle3, "translationY", 0, -mLayoutAnim.getHeight());
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mLayoutCircle4, "translationY", 0, -mLayoutAnim.getHeight());
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mLayoutCircle5, "translationY", 0, -mLayoutAnim.getHeight());
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(mLayoutCircle6, "translationY", 0, -mLayoutAnim.getHeight());
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float alpha = -2 * value / mLayoutAnim.getHeight();//0~1
                LogUtil.d(TAG, "startTranslationAnim value:" + value + ",Y:" + mLayoutAnim.getHeight() + ",alpha:" + alpha);
                if (alpha <= 1) {
                    mCircleImageView1Blur.setAlpha(alpha);
                    mCircleImageView2Blur.setAlpha(alpha);
                    mCircleImageView3Blur.setAlpha(alpha);
                    mCircleImageView4Blur.setAlpha(alpha);
                    mCircleImageView5Blur.setAlpha(alpha);
                    mCircleImageView6Blur.setAlpha(alpha);
                    mCircleImageView1.setAlpha(1 - alpha);
                    mCircleImageView2.setAlpha(1 - alpha);
                    mCircleImageView3.setAlpha(1 - alpha);
                    mCircleImageView4.setAlpha(1 - alpha);
                    mCircleImageView5.setAlpha(1 - alpha);
                    mCircleImageView6.setAlpha(1 - alpha);
                }

            }
        });
        mTranslationAnimatorSet = new AnimatorSet();
        mTranslationAnimatorSet.setDuration(2000);
        mTranslationAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLayoutAnim.setVisibility(View.GONE);
                if (mIsAdLoaded) {
                    showAd();
                } else {
                    startAlphaAnim();
                }
            }
        });
        mTranslationAnimatorSet.play(animator1).with(animator2).with(animator3).with(animator4).with(animator5).with(animator6);
        mTranslationAnimatorSet.start();
    }

    private void startAlphaAnim() {
        mAlphAnimator2 = ObjectAnimator.ofFloat(mLayoutAppInfo, "alpha", 0f, 1.0f);
        mAlphAnimator2.setDuration(1500);
        mAlphAnimator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mLayoutAppInfo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Async.scheduleTaskOnUiThread(500, new Runnable() {
                    @Override
                    public void run() {
                        if (!mIsAdLoaded) {
                            toMain();
                        }
                    }
                });
            }
        });
        mAlphAnimator2.start();
    }

    private void setAnimColors() {
        mSplashAnimIconResIds = new int[]{R.drawable.icon_splash_anim_color_1, R.drawable.icon_splash_anim_color_2, R.drawable.icon_splash_anim_color_3,
                R.drawable.icon_splash_anim_color_4, R.drawable.icon_splash_anim_color_5, R.drawable.icon_splash_anim_color_6, R.drawable.icon_splash_anim_color_7};
        mSplashAnimIconBlurResIds = new int[]{R.drawable.icon_splash_anim_color_1_blur, R.drawable.icon_splash_anim_color_2_blur, R.drawable.icon_splash_anim_color_3_blur,
                R.drawable.icon_splash_anim_color_4_blur, R.drawable.icon_splash_anim_color_5_blur, R.drawable.icon_splash_anim_color_6_blur, R.drawable.icon_splash_anim_color_7_blur};

        mCircleImageView1 = (ImageView) findViewById(R.id.circle_ImageView1);
        mCircleImageView2 = (ImageView) findViewById(R.id.circle_ImageView2);
        mCircleImageView3 = (ImageView) findViewById(R.id.circle_ImageView3);
        mCircleImageView4 = (ImageView) findViewById(R.id.circle_ImageView4);
        mCircleImageView5 = (ImageView) findViewById(R.id.circle_ImageView5);
        mCircleImageView6 = (ImageView) findViewById(R.id.circle_ImageView6);
        mCircleImageView1Blur = (ImageView) findViewById(R.id.circle_ImageView1_blur);
        mCircleImageView2Blur = (ImageView) findViewById(R.id.circle_ImageView2_blur);
        mCircleImageView3Blur = (ImageView) findViewById(R.id.circle_ImageView3_blur);
        mCircleImageView4Blur = (ImageView) findViewById(R.id.circle_ImageView4_blur);
        mCircleImageView5Blur = (ImageView) findViewById(R.id.circle_ImageView5_blur);
        mCircleImageView6Blur = (ImageView) findViewById(R.id.circle_ImageView6_blur);
        mLayoutCircle1 = (RelativeLayout) findViewById(R.id.layout_circle_1);
        mLayoutCircle2 = (RelativeLayout) findViewById(R.id.layout_circle_2);
        mLayoutCircle3 = (RelativeLayout) findViewById(R.id.layout_circle_3);
        mLayoutCircle4 = (RelativeLayout) findViewById(R.id.layout_circle_4);
        mLayoutCircle5 = (RelativeLayout) findViewById(R.id.layout_circle_5);
        mLayoutCircle6 = (RelativeLayout) findViewById(R.id.layout_circle_6);
        int[] positions = Stringutil.getRandomArray(new int[]{0, 1, 2, 3, 4, 5, 6}, 6);
        if (positions == null) return;
        for (int i = 0; i <= positions.length - 1; i++) {
            int iconResId = mSplashAnimIconResIds[positions[i]];
            int iconBlurResId = mSplashAnimIconBlurResIds[positions[i]];
            if (i == 0) {
                mCircleImageView1.setBackgroundResource(iconResId);
                mCircleImageView1Blur.setBackgroundResource(iconBlurResId);
            } else if (i == 1) {
                mCircleImageView2.setBackgroundResource(iconResId);
                mCircleImageView2Blur.setBackgroundResource(iconBlurResId);
            } else if (i == 2) {
                mCircleImageView3.setBackgroundResource(iconResId);
                mCircleImageView3Blur.setBackgroundResource(iconBlurResId);
            } else if (i == 3) {
                mCircleImageView4.setBackgroundResource(iconResId);
                mCircleImageView4Blur.setBackgroundResource(iconBlurResId);
            } else if (i == 4) {
                mCircleImageView5.setBackgroundResource(iconResId);
                mCircleImageView5Blur.setBackgroundResource(iconBlurResId);
            } else if (i == 5) {
                mCircleImageView6.setBackgroundResource(iconResId);
                mCircleImageView6Blur.setBackgroundResource(iconBlurResId);
            }
        }
    }

    private void showInitialization() {
        PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_SHOW_SPLASH_INIT_TIME, System.currentTimeMillis());
        mLayoutInit.setVisibility(View.VISIBLE);
        mLayoutNoInit.setVisibility(View.GONE);
        showInitializationAnim();
    }

    private void toMain() {
        //在跳转的时候防止广告显示
        try {
            if (mAdvertisement != null && !mIsShowAd) {
                mAdvertisement.close();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "toMain close ad exception: " + e.getMessage());
        }
        if (!isFinishing()) {
            ActivityBuilder.toMain(this, ActivityBuilder.FRAGMENT_HOME);
            finish();
        }
    }

    private void showInitializationAnim() {
        mInitAnimator = ValueAnimator.ofInt(0, 100);
        mInitAnimator.setDuration(AD_FIRST_INSTALL_LOAD_TIME_ADMOB);
        mInitAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mPbInit.setProgress(animatedValue);
            }
        });
        mInitAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mIsAdLoaded) {
                    toMain();
                }
            }
        });
        mInitAnimator.start();
    }

    //****************************************AD********************************************//
    private void initAds() {
        String admob_id = CallerAdManager.getAdmob_id(CallerAdManager.POSITION_ADMOB_SPLASH_NORMAL);
        String placementId = AdvertisementSwitcher.SERVER_KEY_START_UP;
        if (mIsShowFristAdMob) {
            admob_id = FirstShowAdmobUtil.getAdmobIdForFirst(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_SPLASH);
            placementId = AdvertisementSwitcher.SERVER_KEY_FIRST_SHOW_ADMOB;
        }
        MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(getWindow().getDecorView(),
                CallerAdManager.getFacebook_id(CallerAdManager.POSITION_FB_SPLASH_NORMAL), //FB_SPLASH_ID,
                admob_id,
                Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,
                "",
                Advertisement.ADMOB_TYPE_NATIVE,
                -1,
                "",
                placementId,
                false);

        mAdvertisement = new Advertisement(adapter);
        mAdvertisement.setRefreshWhenClicked(false);
        mAdvertisement.refreshAD(true);

        mAdvertisement.enableFullClickable();
        if (CallerAdManager.isOnlyBtnClickable(CallerAdManager.POSITION_FB_ADS_SPLASH_BIG)) {
            mAdvertisement.enableOnlyBtnClickable();
        }
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {
        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, String placementId, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner, placementId); //SERVER_KEY_START_UP
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baiduKey, String eventKey, String placementId, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey, eventKey, placementId, isBanner);
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            mIsAdLoaded = true;
            showAd();
        }

        @Override
        public void onAdError(boolean isLastRequestIndex) {
            super.onAdError(isLastRequestIndex);
        }

        @Override
        public void onAdShow() {
            super.onAdShow();
            mIsShowAd = true;
            PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_SHOW_SPLASH_AD_TIME, System.currentTimeMillis());
        }

        @Override
        public void onAdClicked(boolean isAdmob) {
            super.onAdClicked(isAdmob);
            toMain();
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.layout_facebook_ad_splash : R.layout.layout_facebook_ad_splash;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_splash : R.layout.layout_admob_advanced_content_ad_splash;
        }
    }

    private void showAd() {
        if (mIsShowInitialization) {
            if (mInitAnimator != null) {
                mInitAnimator.end();
                mInitAnimator.cancel();
            }
        } else {
            if (mAlphAnimator1 != null && mAlphAnimator1.isRunning()) {
                mAlphAnimator1.end();
                mAlphAnimator1.cancel();
            }
            if (mAlphAnimator2 != null && mAlphAnimator2.isRunning()) {
                mAlphAnimator2.end();
                mAlphAnimator2.cancel();
            }
            if (mTranslationAnimatorSet != null && mTranslationAnimatorSet.isRunning()) {
                mTranslationAnimatorSet.end();
                mTranslationAnimatorSet.cancel();
            }
        }
        Async.scheduleTaskOnUiThread(200, new Runnable() {
            @Override
            public void run() {
                mLayoutLoading.setVisibility(View.GONE);
                mLayoutLoadCompleted.setVisibility(View.VISIBLE);

                // TODO: 2018/8/4  广告自动跳转倒计时 此处控制是否自动跳转
                if (CallerAdManager.isAutoGoMain() && !mIsShowFristAdMob) {
                    showAdCountDown();
                }
            }
        });
    }

    private void showAdCountDown() {
        mAdCountDownAnim = ValueAnimator.ofInt(0, 100);
        mAdCountDownAnim.setDuration(AD_SHOW_TIME);
        mAdCountDownAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mPbSkip.setProgress(animatedValue);
            }
        });
        mAdCountDownAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                toMain();
            }
        });
        mAdCountDownAnim.start();
    }
    //****************************************AD********************************************//
}
