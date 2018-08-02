package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.HashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.FirstShowAdmobUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.AdPreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.CircleProgressBar;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";
    protected static long AD_FIRST_INSTALL_LOAD_TIME = 6500;//6500
    protected static long AD_FIRST_INSTALL_LOAD_TIME_ADMOB = 10000;//10sec
    protected static long AD_MAX_LOAD_TIME = 4500;
    protected static long AD_SHOW_TIME = 4000;
    protected static long FIRST_AD_SHOW_TIME = 4000;
    private static final long SPLASH_AD_SHOW_INTERVAL_TIME = 15 * 60 * 1000;
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
    private int[] mSplashAnimColorResIds;
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
        initView();
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this, true);
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_splash;
    }

    private void initData() {
        AD_FIRST_INSTALL_LOAD_TIME = AdPreferenceHelper.getLong("pref_splash_ad_first_load", 6500);
        AD_MAX_LOAD_TIME = AdPreferenceHelper.getLong("pref_splash_ad_load", 4500);
        AD_SHOW_TIME = AdPreferenceHelper.getLong("pref_splash_ad_show", 4000);

        mIsShowFristAdMob = FirstShowAdmobUtil.isShowFirstAdMob(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_SPLASH, false);
        if (mIsShowFristAdMob) {
            HashMap<String, Long> data = PreferenceHelper.getHashMapData(PreferenceHelper.PREF_LAST_SHOW_FRIST_ADMOB_TIME_MAP, Long.class);
            if (data == null) {
                data = new HashMap<>();
            }
            data.put(String.valueOf(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_SPLASH), System.currentTimeMillis());
            PreferenceHelper.putHashMapData(PreferenceHelper.PREF_LAST_SHOW_FRIST_ADMOB_TIME_MAP, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void SwitchLang() {
        //加载语言
        languageSetting = LanguageSettingUtil.getInstance(getApplicationContext());
        if (languageSetting != null)
            languageSetting.refreshLanguage(this);
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

        mLayoutLoading.setVisibility(View.VISIBLE);
        mLayoutLoadCompleted.setVisibility(View.GONE);

        long lastShowInitTime = PreferenceHelper.getLong(PreferenceHelper.PREF_LAST_SHOW_SPLASH_INIT_TIME, 0);
        if (lastShowInitTime <= 0) {
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

//        Async.scheduleTaskOnUiThread(AD_MAX_LOAD_TIME, new Runnable() {
//            @Override
//            public void run() {
//                toMain();
//            }
//        });
    }

    private void startSplashAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLayoutAnim, "alpha", 0f, 1.0f);
        animator.setDuration(1500);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startTranslationAnim();
            }
        });
        animator.start();
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
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(2000);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLayoutAnim.setVisibility(View.GONE);
                startAlphaAnim();
            }
        });
        animatorSet.play(animator1).with(animator2).with(animator3).with(animator4).with(animator5).with(animator6);
        animatorSet.start();
    }

    private void startAlphaAnim() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLayoutAppInfo, "alpha", 0f, 1.0f);
        animator.setDuration(1500);
        animator.addListener(new AnimatorListenerAdapter() {
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
                        toMain();
                    }
                });
            }
        });
        animator.start();
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
}
