package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.helper.CallFlashPreferenceHelper;

import java.util.HashMap;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.FirstShowAdmobUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventShowPemisssionTip;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.FullScreenAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.DrawHookView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import event.EventBus;

/**
 * Created by ChenR on 2017/9/20.
 */

public class CallFlashSetResultActivity extends BaseActivity {
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
    private FontIconView mFivSuccessBig;
    private DrawHookView mDrawHookView;

    private View mLayoutAd;
    private Advertisement mAdvertisement;

    // fbAd相关
    private View describeTextLayout;
    private View bigImage;
    private View titleContent;
    private View img_up_right;
    private View img_button1;
    private View img_button2;
    private View buttonLayout;
    private ImageView img_up_left;
    private ImageView img_bottom;
    private LinearLayout describeLayout;
    private LinearLayout titleLayout;
    private LinearLayout img_bottom_layout;
    private LinearLayout img_up_left_layout;
    private LinearLayout img_up_right_layout;

    private boolean mIsAdLoaded;
    private boolean mIsPlayAnimAfterAdLoaded;
    private boolean mIsEndAnimWhenLoadingAd;
    private boolean mIsPlayAdAnim;

    private boolean isFromSystem = false;
    private boolean isShowResult = true;
    private boolean isLoading = false;
    private boolean isSetGifToCallerShow = false;

    private String fb_id = "";

    private AnimatorSet mShowResultAnimatorSet;

    private boolean mIsEndAnimAfterAdLoaded = false;

    private long mStartTime;
    private Handler mHandler = new Handler();
    private boolean isAutoRefresh;
    private CallFlashInfo mCallFlashInfo;
    private boolean mIsFromDesktop;
    private boolean mIsFirstShowAdmob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_flash_set_result);
        mStartTime = 0;
        mIsFromDesktop = getIntent().getBooleanExtra(ActivityBuilder.IS_COME_FROM_DESKTOP, false);
        mCallFlashInfo = (CallFlashInfo) getIntent().getSerializableExtra(ActivityBuilder.CALL_FLASH_INFO);
        mIsFirstShowAdmob = FirstShowAdmobUtil.isShowFirstAdMob(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_RESULT_FLASH_SET, false);
        init();
        listener();
        EventBus.getDefault().post(new EventShowPemisssionTip());
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
        FlurryAgent.logEvent("GifShowActivity-----ShowMain");
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
                mIsEndAnimWhenLoadingAd = true;
                if (mIsAdLoaded && !mIsPlayAnimAfterAdLoaded) startAnimAfterAdLoaded();
                //显示插屏
//                showInterstitialAd(true);
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
        initView();
//        initAd();

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

        mFivSuccessBig = (FontIconView) findViewById(R.id.fiv_success_big);
        mDrawHookView = (DrawHookView) findViewById(R.id.drawHookView);
        mDrawHookView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mLayoutSuccessBig = findViewById(R.id.layout_success_big);

        mLayoutAd = findViewById(R.id.layout_ad_view);

        String showContent = getIntent().getStringExtra("result_des");
        mTvCenterResultDes.setText(showContent);
        mTvTopResultDes.setText(showContent);

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
            if (InterstitialAdUtil.isShowFullScreenAd(CallerAdManager.IN_ADS_CALL_FLASH)) {
                showFullScreenAd();
            } else {
                showInterstitialAd();
            }
        }
    }

    private void showFullScreenAd() {
        if (FullScreenAdManager.getInstance().isAdLoaded()) {
            FullScreenAdManager.getInstance().showAd(this, CallerAdManager.IN_ADS_CALL_FLASH, new FullScreenAdManager.AdListener() {
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

    private void onFinish() {
        isShowResult = getIntent().getBooleanExtra("is_show_result", true);
        if (isShowResult && !mIsFromDesktop) {
            //-1表示不改变mainactiivty的page 页面
            ActivityBuilder.toMain(this, ActivityBuilder.BACK_FROM_CALL_FLASH_RESULT);
        } else {
            finish();
        }
    }

    private void showInterstitialAd() {
        InterstitialAdvertisement interstitialAdvertisement = ApplicationEx.getInstance().getInterstitialAdvertisement(CallerAdManager.IN_ADS_CALL_FLASH);
        if (interstitialAdvertisement == null) {
            onFinish();
        } else {
            onFinish();
            interstitialAdvertisement.show(new InterstitialAdvertisement.InterstitialAdShowListener() {
                @Override
                public void onAdClosed() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdClosed");
                    ApplicationEx.getInstance().setInterstitialAdvertisement(null, CallerAdManager.IN_ADS_CALL_FLASH);
                }

                @Override
                public void onAdShow() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdShow");
                }

                @Override
                public void onAdError() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdError");
                    ApplicationEx.getInstance().setInterstitialAdvertisement(null, CallerAdManager.IN_ADS_CALL_FLASH);
                }
            });
        }
    }
}
