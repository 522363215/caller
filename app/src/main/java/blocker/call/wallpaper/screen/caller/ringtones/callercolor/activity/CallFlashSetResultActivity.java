package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
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
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.FacebookAdDriver;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventShowPemisssionTip;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.FullScreenAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.AnimatorUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
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
    private MyAdvertisementAdapter mMyAdvertisementAdapter;
    private boolean mIsComeFromPhoneDetail;
    private boolean mIsComeFromCallAfter;
    private boolean mIsFromFlashDetail;
    private boolean isAutoRefresh;
    private FacebookAdDriver mDriver;
    private boolean mIsCustomCallFlash;
    private CallFlashInfo mCallFlashInfo;
    private boolean mIsFromDesktop;
    private boolean mIsFirstShowAdmob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_flash_set_result);
        mStartTime = 0;
        mIsComeFromPhoneDetail = getIntent().getBooleanExtra(ConstantUtils.COME_FROM_PHONEDETAIL, false);
//        mIsComeFromCallAfter = getIntent().getBooleanExtra(ConstantUtils.COME_FROM_CALLAFTER, false);
        mIsComeFromCallAfter = false;
        mIsCustomCallFlash = getIntent().getBooleanExtra(ConstantUtils.IS_CUSTOM_CALLFLASH, false);
        mIsFromFlashDetail = getIntent().getBooleanExtra(ConstantUtils.COME_FROM_FLASH_DETAIL, false);
        mIsFromDesktop = getIntent().getBooleanExtra(ConstantUtils.COME_FROM_DESKTOP, false);
        mCallFlashInfo = (CallFlashInfo) getIntent().getSerializableExtra(ConstantUtils.CALL_FLASH_INFO);
        mIsFirstShowAdmob = CallerAdManager.isShowFirstAdMob(CallerAdManager.POSITION_FIRST_ADMOB_RESULT_FLASH_SET, false);
        if (!mIsFromFlashDetail) {
            CallerAdManager.loadInterstitialAd(this, CallerAdManager.IN_ADS_CALL_FLASH_RESULT);
        }
        initFbid();
        init();
        listener();
        EventBus.getDefault().post(new EventShowPemisssionTip());
//        if (!isShowResult) {
//            attachToGif();
//        } else {
//            EventBus.getDefault().post(new EventShowPemisssionTip());
//        }

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

    private void initFbid() {
        fb_id = ConstantUtils.FB_AFTER_CALL_ID;
        int use_id = PreferenceHelper.getInt(ConstantUtils.IS_USE_CALL_FLASH_ID, 1);
        if (use_id == 1) {
            fb_id = ConstantUtils.FB_CALL_FLASH_SET_ID;
        } else if (use_id == 0) {
            fb_id = ConstantUtils.FB_SCAN_RESULT_ID;
        } else if (use_id == 2) {
            fb_id = ConstantUtils.FB_AFTER_CALL_ID; // used now
        } else if (use_id == 99) {
            fb_id = PreferenceHelper.getString(ConstantUtils.NEW_USE_CALL_FLASH_ID, "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAutoRefresh && mDriver != null && mDriver.paused()) {
            mDriver.resume();
        }
        FlurryAgent.logEvent("GifShowActivity-----ShowMain");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isAutoRefresh && mDriver != null) {
            mDriver.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isAutoRefresh && mDriver != null) {
            mDriver.stop();
        }

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

    private String getCallFlashName(int flashType) {
        String name = "unknownCallFlash";
        switch (flashType) {
            case FlashLed.FLASH_TYPE_STREAMER:
                name = "STREAMER";
                break;
            case FlashLed.FLASH_TYPE_FESTIVAL:
                name = "FESTIVAL";
                break;
            case FlashLed.FLASH_TYPE_LOVE:
                name = "LOVE";
                break;
            case FlashLed.FLASH_TYPE_KISS:
                name = "KISS";
                break;
            case FlashLed.FLASH_TYPE_ROSE:
                name = "ROSE";
                break;
            case FlashLed.FLASH_TYPE_CUSTOM:
                name = "CUSTOM";
                break;
            case FlashLed.FLASH_TYPE_PANDA:
                name = "PANDA";
                break;
            case FlashLed.FLASH_TYPE_LOVE1:
                name = "LOVE1";
                break;
            case FlashLed.FLASH_TYPE_VISUAL_1:
                name = "VISUAL_1";
                break;
            case FlashLed.FLASH_TYPE_VISUAL_2:
                name = "VISUAL_2";
                break;
            case FlashLed.FLASH_TYPE_VISUAL_3:
                name = "VISUAL_3";
                break;
            case FlashLed.FLASH_TYPE_MONKEY:
                name = "MONKEY";
                break;
            case FlashLed.FLASH_TYPE_RAP:
                name = "RAP";
                break;
            case FlashLed.FLASH_TYPE_DANCE_PIG:
                name = "PIG";
                break;
            case FlashLed.FLASH_TYPE_BEAR:
                name = "BEAR";
                break;
        }
        return name;
    }

    private void showResult() {
//        ((ActionBar) findViewById(R.id.actionbar)).setBackImg(R.string.icon_close);
        int flashType = CallFlashPreferenceHelper.getInt(CallFlashPreferenceHelper.CALL_FLASH_TYPE, FlashLed.FLASH_TYPE_DEFAULT);
        boolean isCallFlashOn = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
        if (isCallFlashOn) {
            FlurryAgent.logEvent("CallFlashShowActivity-call_screen_set_" + getCallFlashName(flashType));
        } else {
//            FlurryAgent.logEvent("CallFlashShowActivity-call_screen_cancel_" + getCallFlashName(flashType));
        }

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
                    fbAdCloseAction();
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

    //广告动画
    int startAdAnimCount;

    private void startAdAnim() {
        if (bigImage == null) {
            View admobView = mMyAdvertisementAdapter.getAdmobContainerView();
            if (admobView != null && startAdAnimCount <= 10) {
                mMyAdvertisementAdapter.initAdmobView(admobView);
                startAdAnim();
                startAdAnimCount++;
            }
            return;
        }
        mIsPlayAdAnim = true;
        AnimatorUtils.newValueAnimator(600, new TypeEvaluator() {
                    @Override
                    public Object evaluate(float v, Object o, Object t1) {
                        bigImage.setScaleX(0.6f + v * 0.4f);
                        bigImage.setScaleY(0.6f + v * 0.4f);
                        bigImage.setAlpha(v);
                        return null;
                    }
                }, new AnimatorUtils.OnAnimatorEnd() {
                    @Override
                    public void onAnimatorEnd() {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                backImgAnimation();
                            }
                        }, 100);
                    }
                }
        ).start();
    }

    private void backImgAnimation() {
        AnimatorUtils.newValueAnimator(500, new TypeEvaluator() {
            @Override
            public Object evaluate(float v, Object o, Object t1) {
                float f = 1 - v;
                img_bottom.setY(-img_bottom_layout.getHeight() * f);
                img_bottom.setAlpha(v);

                img_up_left.setY(img_up_left_layout.getHeight() * f);
                img_up_left.setX(img_up_left_layout.getWidth() * f);
                img_up_left.setAlpha(v);

                img_up_right.setY(img_up_right_layout.getHeight() * f);
                img_up_right.setX(-img_up_right_layout.getWidth() * f);
                img_up_right.setAlpha(v);
                return null;
            }
        }, new AnimatorUtils.OnAnimatorEnd() {
            @Override
            public void onAnimatorEnd() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textAnimation();
                    }
                }, 100);
            }
        }).start();
    }

    private void textAnimation() {
        AnimatorUtils.newValueAnimator(500, new TypeEvaluator() {
                    @Override
                    public Object evaluate(float v, Object o, Object t1) {
                        float f = 1 - v;
                        titleContent.setY(titleLayout.getHeight() * f);
                        describeTextLayout.setY(-describeLayout.getHeight() * f);
                        titleContent.setAlpha(v);
                        describeTextLayout.setAlpha(v);
                        return null;
                    }
                }, new AnimatorUtils.OnAnimatorEnd() {
                    @Override
                    public void onAnimatorEnd() {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                buttonAnimator();
                            }
                        }, 100);
                    }
                }
        ).start();
    }

    private void buttonAnimator() {
        AnimatorUtils.newValueAnimator(600, new TypeEvaluator() {
                    @Override
                    public Object evaluate(float v, Object o, Object t1) {
                        buttonLayout.setAlpha(v);
                        return null;
                    }
                }, new AnimatorUtils.OnAnimatorEnd() {
                    @Override
                    public void onAnimatorEnd() {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                buttonStarAnimation();
                            }
                        }, 100);
                    }
                }
        ).start();
    }

    private void buttonStarAnimation() {
        ValueAnimator valueAnimator = AnimatorUtils.newValueAnimator(1200, new TypeEvaluator() {
                    @Override
                    public Object evaluate(float v, Object o, Object t1) {
                        float abs = Math.abs(v - 0.5f);
                        img_button1.setAlpha(abs * 2);
                        img_button2.setAlpha(1 - abs * 2);
                        return null;
                    }
                }, new AnimatorUtils.OnAnimatorEnd() {
                    @Override
                    public void onAnimatorEnd() {
                    }
                }
        );
        valueAnimator.setRepeatCount(-1);
        valueAnimator.start();
    }

    @Override
    public void onBackPressed() {
        if (mIsEndAnimAfterAdLoaded || (System.currentTimeMillis() - mStartTime > 2000)) {
            if (CallerAdManager.isShowFullScreenAd(CallerAdManager.IN_ADS_CALL_FLASH_RESULT)) {
                showFullScreenAd();
            } else {
                showInterstitialAd();
            }
        }
    }

    private void showFullScreenAd() {
        if (FullScreenAdManager.getInstance().isAdLoaded() && !mIsFromFlashDetail) {
            FullScreenAdManager.getInstance().showAd(this, CallerAdManager.IN_ADS_CALL_FLASH_RESULT, new FullScreenAdManager.AdListener() {
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
        if (mIsFromFlashDetail) {
            onFinish();
            return;
        }

        InterstitialAdvertisement interstitialAdvertisement = ApplicationEx.getInstance().getInterstitialAdvertisement(CallerAdManager.IN_ADS_CALL_FLASH_RESULT);
        if (interstitialAdvertisement == null) {
            onFinish();
        } else {
            onFinish();
            interstitialAdvertisement.show(new InterstitialAdvertisement.InterstitialAdShowListener() {
                @Override
                public void onAdClosed() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdClosed");
                    ApplicationEx.getInstance().setInterstitialAdvertisement(null, CallerAdManager.IN_ADS_CALL_FLASH_RESULT);
                }

                @Override
                public void onAdShow() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdShow");
                }

                @Override
                public void onAdError() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdError");
                    ApplicationEx.getInstance().setInterstitialAdvertisement(null, CallerAdManager.IN_ADS_CALL_FLASH_RESULT);
                }
            });
        }
    }

    private void initAd() {
        initAds();
    }

    private void initAds() {
        String placementId = AdvertisementSwitcher.SERVER_KEY_SET_RESULT;
        String admobId = ConstantUtils.ADMOB_ADV_SCAN_RESULT_ID;
        if (mIsFirstShowAdmob) {
            placementId = AdvertisementSwitcher.SERVER_KEY_FIRST_SHOW_ADMOB;
            admobId = CallerAdManager.getAdmobIdForFirst(CallerAdManager.POSITION_FIRST_ADMOB_RESULT_FLASH_SET);
        }
        mMyAdvertisementAdapter = new MyAdvertisementAdapter(getWindow().getDecorView(),
                fb_id,
                admobId,
                Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,
                CallerAdManager.MOPUB_NATIVE_ADV_BIG_CALL_FLASH_RESULT_ID,
                Advertisement.MOPUB_TYPE_NATIVE,
                CallerAdManager.BAIDU_ADV_BIG_CALL_FLASH_RESULT_ID,
                "",
                placementId,
                false);

        mAdvertisement = new Advertisement(mMyAdvertisementAdapter);

        mAdvertisement.setRefreshWhenClicked(false);
        mAdvertisement.setIsResultPage(true);
        mAdvertisement.refreshAD(true);
        if (CallerAdManager.isWhiteClickable(CallerAdManager.POSITION_FB_ADS_SCAN_RESULT_BIG)) {
            mAdvertisement.enableFullClickable();
        }
        if (CallerAdManager.isOnlyBtnClickable(CallerAdManager.POSITION_FB_ADS_SCAN_RESULT_BIG)) {
            mAdvertisement.enableOnlyBtnClickable();
        }
//        AdTestManager.getAdRequestBuilder().build();
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
//            mLayoutAd.setVisibility(View.VISIBLE);
//            final View fbRoot = getFbContainerView();
//            final View admobRoot = getAdmobContainerView();
            try {
//                if (fbRoot != null) {
//                    initFbView(fbRoot);
//                    if (bigImage == null) {
//                        initAdmobView(admobRoot);
//                    }
//                } else if (admobRoot != null) {
//                    initAdmobView(admobRoot);
//                }

            /*如果没有加载出来广告也会平移，以后会用
            *if (mIsPlayAnim) {
                if (!mIsPlayAdAnim) {
                    startAdAnim();
                }
            } else {
                startAnimAfterAdLoaded();
            }*/

                if (!mIsPlayAnimAfterAdLoaded)
                    startAnimAfterAdLoaded();
            } catch (Exception e) {

            }

        }

        private void saveFirstShowAdmobTime() {
            if (mIsFirstShowAdmob) {
                HashMap<String, Long> data = PreferenceHelper.getHashMapData(PreferenceHelper.PREF_LAST_SHOW_FRIST_ADMOB_TIME_MAP, Long.class);
                if (data == null) {
                    data = new HashMap<>();
                }
                data.put(String.valueOf(CallerAdManager.POSITION_FIRST_ADMOB_RESULT_FLASH_SET), System.currentTimeMillis());
                PreferenceHelper.putHashMapData(PreferenceHelper.PREF_LAST_SHOW_FRIST_ADMOB_TIME_MAP, data);
            }
        }

        @Override
        public void onAdShow() {
            super.onAdShow();
            saveFirstShowAdmobTime();
        }

        @Override
        public int getAdmobHeight() {
            return DeviceUtil.getScreenHeight() - getResources().getDimensionPixelOffset(R.dimen.dp56) - mLayoutTop.getHeight();

        }

        private void initAdmobView(View admobRoot) {
            describeTextLayout = admobRoot.findViewById(R.id.tv_content);
            bigImage = admobRoot.findViewById(R.id.layout_admob_image);
            describeLayout = (LinearLayout) admobRoot.findViewById(R.id.describe_layout);
            titleLayout = (LinearLayout) admobRoot.findViewById(R.id.title_layout);
            titleContent = admobRoot.findViewById(R.id.title_content);
            img_up_right = admobRoot.findViewById(R.id.img_up_right);
            img_up_left = (ImageView) admobRoot.findViewById(R.id.img_up_left);
            img_bottom = (ImageView) admobRoot.findViewById(R.id.img_bottom);
            img_button1 = admobRoot.findViewById(R.id.img_button1);
            img_button2 = admobRoot.findViewById(R.id.img_button2);
            buttonLayout = admobRoot.findViewById(R.id.button_layout);
            img_up_left_layout = (LinearLayout) admobRoot.findViewById(R.id.img_up_left_layout);
            img_up_right_layout = (LinearLayout) admobRoot.findViewById(R.id.img_up_right_layout);
            img_bottom_layout = (LinearLayout) admobRoot.findViewById(R.id.img_bottom_layout);
            LogUtil.d(TAG, "ADAmin initAdmobView describeTextLayout:" + describeTextLayout + ",bigImage:" + bigImage + ",describeLayout:" + describeLayout
                    + ",titleLayout:" + titleLayout + ",titleContent:" + titleContent + ",img_up_right:" + img_up_right + ",img_up_left:" + img_up_left + ",img_bottom:"
                    + img_bottom + ",img_button1:" + img_button1 + ",img_button2:" + img_button2 + ",buttonLayout:" + buttonLayout + ",img_up_left_layout:" + img_up_left_layout
                    + ",img_up_right_layout:" + img_up_right_layout + ",img_bottom_layout:" + img_bottom_layout);
        }

        private void initFbView(final View fbRoot) {
            describeTextLayout = fbRoot.findViewById(R.id.nativeAdBody);
            bigImage = fbRoot.findViewById(R.id.layout_fb_image);
            describeLayout = (LinearLayout) fbRoot.findViewById(R.id.describe_layout);
            titleLayout = (LinearLayout) fbRoot.findViewById(R.id.title_layout);
            titleContent = fbRoot.findViewById(R.id.title_content);
            img_up_right = fbRoot.findViewById(R.id.img_up_right);
            img_up_left = (ImageView) fbRoot.findViewById(R.id.img_up_left);
            img_bottom = (ImageView) fbRoot.findViewById(R.id.img_bottom);
            img_button1 = fbRoot.findViewById(R.id.img_button1);
            img_button2 = fbRoot.findViewById(R.id.img_button2);
            buttonLayout = fbRoot.findViewById(R.id.button_layout);
            img_up_left_layout = (LinearLayout) fbRoot.findViewById(R.id.img_up_left_layout);
            img_up_right_layout = (LinearLayout) fbRoot.findViewById(R.id.img_up_right_layout);
            img_bottom_layout = (LinearLayout) fbRoot.findViewById(R.id.img_bottom_layout);
        }

        @Override
        public int getFbViewRes() {
            return isBanner() ? R.layout.facebook_native_ads_banner_50 : R.layout.facebook_native_ads_number_scan_result;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            if (mIsFirstShowAdmob) {
                return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_result_first_show : R.layout.layout_admob_advanced_content_ad_result_first_show;
            }
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_scan_result_new : R.layout.layout_admob_advanced_content_ad_scan_result_new;
        }

        @Override
        public int getMoPubViewRes() {
            return isBanner() ? R.layout.layout_mopub_ad_banner : R.layout.layout_mopub_native_big_spam_update_result;
        }

        @Override
        public int getBaiDuViewRes() {
            return isBanner() ? R.layout.layout_du_ad_banner : R.layout.layout_du_ad_big_spam_update_result;
        }
    }

    private void fbAdCloseAction() {
        final View fbRoot = mMyAdvertisementAdapter.getFbContainerView();
        if (fbRoot == null) return;
        fbRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                View fbAdCloseBtn = fbRoot.findViewById(R.id.fiv_ad_close);
                if (fbAdCloseBtn == null) return;
                fbAdCloseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAdvertisement != null) {
                            View fbView = findViewById(R.id.layout_ad_view).findViewById(R.id.nativeAdContainer);
                            if (fbView != null) fbView.setVisibility(View.GONE);
                            mAdvertisement.setFbClose(true);
                            mAdvertisement.refreshAD(false);
                            FlurryAgent.logEvent("CallFlashGifShowActivity--onclick--fbAdCloseAction");
                        }
                    }
                });
            }
        }, 500);

    }
}
