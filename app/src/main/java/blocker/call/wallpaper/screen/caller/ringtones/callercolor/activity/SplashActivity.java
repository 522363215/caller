package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
    private LinearLayout mLayoutNoInit;
    private View mLayoutLoadCompleted;
    private View mLayoutSkipRoot;
    private CircleProgressBar mPbSkip;
    private ValueAnimator mInitAnimator;
    private boolean mIsAdLoaded;
    private boolean mIsShowAd;

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
        mLayoutNoInit = (LinearLayout) findViewById(R.id.layout_no_initialization);

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
        Async.scheduleTaskOnUiThread(AD_MAX_LOAD_TIME, new Runnable() {
            @Override
            public void run() {
                toMain();
            }
        });
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
