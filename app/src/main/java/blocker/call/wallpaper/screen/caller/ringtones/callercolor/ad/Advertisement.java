package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duapps.ad.DuAdListener;
import com.duapps.ad.DuNativeAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd.Image;
import com.google.android.gms.ads.formats.NativeAdView;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.md.serverflash.util.LogUtil;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.BaseNativeAd;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.NativeImageHelper;
import com.mopub.nativeads.StaticNativeAd;
import com.mopub.nativeads.ViewBinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide.GlideHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

/**
 * Created by luowp on 2016/5/19.
 */
public class Advertisement {
    public static final int ADMOB_TYPE_NONE = 0;
    public static final int ADMOB_TYPE_NATIVE = 1;
    public static final int ADMOB_TYPE_NATIVE_ADVANCED = 1 << 1;

    public static final int MOPUB_TYPE_BANNER = 0;
    public static final int MOPUB_TYPE_NATIVE = 1;

    public static final int DEFAULT_FB_REFRESH_INTERVAL = 2 * 60 * 1000;
    public static final int DEFAULT_ADMOB_REFRESH_INTERVAL = 2 * 60 * 1000;
    private static final int DEFAULT_SET_TEXTVIEW_SCROLL = 1000;
    private static final String TAG = "Advertisement";
    private String LOG_TAG = "Advertisement";//dynamic name
    private Context mContextForFb, mContextForAdmob, mContextForMoPub, mContextBaiDu;
    private AdvertisementAdapter mAdapter;

    private boolean mFacebookInited;
    private boolean mAdmobInited;
    private boolean mMopubInited;
    private boolean mBaiDuInited;
    private boolean mAdMobBannerInited;
    private boolean mIsClosed;
    private long mADId;

    private boolean mIsAdMobLoadingOrShow = false;//current is loading admob or has shown admob
    private int mAdmobType = ADMOB_TYPE_NONE;
    private boolean mRefreshWhenClicked = true;
    private boolean mIsBanner = false;

    private boolean mIsLoading;
    private boolean mForceLoadAdFromCache;
    private boolean mShouldCacheFbAd = false;

    /*facebook*/
    private long mRefreshIntervalFb = DEFAULT_FB_REFRESH_INTERVAL;
    private long mRefreshIntervalAdmob = DEFAULT_ADMOB_REFRESH_INTERVAL;
    private long mLastRefreshFB;
    private long mLastRefreshAdmob;
    private long mAdShownTime;
    private NativeAd mFacebookNativeAd;
    private Map<NativeAd, Long> mUsableAdMap = new HashMap<>();//list for fb ad can re usable
    private LinearLayout mFacebookeNativeAdContainer;
    private LinearLayout mLayoutFacebook;
    private AdChoicesView mAdChoicesView;
    private View mAdRootView;//广告布局文件的最顶层View，统一使用@+id/layout_ad_view_root,该View用来控制广告整体的Margin和Padding
    /*facebook*/

    // MoPub
    private long mLastRefreshMopub;
    private long mRefreshIntervalMopub = mRefreshIntervalFb;
    private int mMoPubType;
    private boolean mIsLoadMoPubSuc;
    private boolean mIsLoadMoPubBannerSuc;
    private MoPubNative mMoPubNative;
    private com.mopub.nativeads.NativeAd mMoPubNativeAd;
    private LinearLayout mLayoutMoPub;
    private LinearLayout mMoPubNativeAdContainer;

    // mopub banner
    private boolean mMopubBannerInited;
    private MoPubView mMopubView;

    // BaiDu
    private long mLastRefreshBaiDu;
    private long mRefreshIntervalBaiDu = mRefreshIntervalFb;
    private boolean mIsLoadBaiDuSuc;
    private DuNativeAd mBaiDuNative;
    private LinearLayout mLayoutBaiDu;
    private LinearLayout mBaiDuAdContainer;

    // Admob banner
    private long mLastRefreshAdmobBanner;
    private long mRefreshIntervalAdmobBanner = mRefreshIntervalFb;
    private boolean mIsLoadAdmobBannerSuc;
    private AdView mAdmobBannerAdView;

    private AdLoader mAdmobNativeAdvancedLoader;
    private FrameLayout mLayoutAdmob;
    private AdView mAdmobView;

    private Handler mMainHandler;

    private boolean mIsLoadSuc;
    private boolean mIsAdShown;
    private boolean mIsLoadFbADSuc;
    private boolean mCurrentDisplayingAdIsAdmob = false;
    private int mDefaultMediaViewHeight;//px, valid after onAdShow called
    private List<String> mAdPriority;//priority for this ad placement
    private int mCurrentAdLoadIndex = -1;//current ad index for this placement
    private boolean mIsCalllog;

    private boolean isFullClickable = false;
    private NativeAppInstallAd mNativeAppInstallAd;
    private NativeContentAd mNativeContentAd;

    private String mCurrentLoadingAdType = "";
    private String mCurrentShowAdType = "";

    private boolean isOnlyBtnClickable = false;
    private boolean mIsOnlyBtnAndTextClickable = false;
    private boolean mIsResultPage;
    private boolean mIsFbClosed;
    private boolean mIsAdaptiveSize;

    public Advertisement(AdvertisementAdapter adapter) {
//        if (BuildConfig.DEBUG)
//            LogUtil.d(LogFilterDef.MEMORY_DETECT, this.hashCode() + "-" + this.getClass().getName() + "-" + LogHelper._FUNC_());
        mContextForFb = ApplicationEx.getInstance();
        mContextForAdmob = mContextForFb;
        mContextForMoPub = mContextForFb;
        mContextBaiDu = mContextForFb;
        mAdapter = adapter;
        mIsBanner = mAdapter.isBanner();
        mMainHandler = new Handler(Looper.getMainLooper());
        isFullClickable = false;
        mMoPubType = adapter.getMoPubType();
        isOnlyBtnClickable = false;
        mIsOnlyBtnAndTextClickable = false;
        initAd();
    }

    public Advertisement(AdvertisementAdapter adapter, Context contextForFb, Context contextForAdmob,
                         Context contextForMopub, Context contextForBaiDu) {
        mContextForFb = contextForFb;
        mContextForAdmob = contextForAdmob;
        mContextForMoPub = contextForMopub;
        mContextBaiDu = contextForBaiDu;
        mAdapter = adapter;
        mIsBanner = mAdapter.isBanner();
        mMainHandler = new Handler(Looper.getMainLooper());
        isFullClickable = false;
        isOnlyBtnClickable = false;
        mIsOnlyBtnAndTextClickable = false;
        initAd();
    }

    /**
     * 大图fb广告空白区域可点击，默认不可点
     */
    public void enableFullClickable() {
        isFullClickable = true;
    }

    public void enableOnlyBtnClickable() {
        isOnlyBtnClickable = true;
    }

    public void enableOnlyBtnAndTextClickable() {
        mIsOnlyBtnAndTextClickable = true;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
//        if (BuildConfig.DEBUG)
//            LogUtil.d(LogFilterDef.MEMORY_DETECT, this.hashCode() + "-" + this.getClass().getName() + "-" + LogHelper._FUNC_());
    }

    //default true
    public void setRefreshWhenClicked(boolean refresh) {
        mRefreshWhenClicked = refresh;
    }

    public void setRefreshInterval(long facebookInternal, long admobInternal) {
        mRefreshIntervalFb = facebookInternal;
        mRefreshIntervalAdmob = admobInternal;
    }

    public void setCacheFbAd(boolean shouldCacheAd) {
        mShouldCacheFbAd = shouldCacheAd;
    }

    public boolean isLoaded() {
        return mIsLoadSuc;
    }

    public boolean isAdmobAd() {
        return mIsAdMobLoadingOrShow;
    }

    public boolean isFacebookAd() {
        return !mIsAdMobLoadingOrShow;
    }

    public long getAdId() {
        return mADId;
    }

    public Map<NativeAd, Long> getUsableFbAdCache() {
        return mUsableAdMap;
    }

    /**
     * @return may be null
     */
    public NativeAd getFbAd() {
        return mFacebookNativeAd != null && mFacebookNativeAd.isAdLoaded() ? mFacebookNativeAd : null;
    }

    public void close() {
        mAdapter = null;

        if (mFacebookNativeAd != null) {
            //AdCacheManager.getInstance().unlockCachedAd(mADId);
            mFacebookNativeAd.setAdListener(null);
//            mFacebookNativeAd.setImpressionListener(null);
            mFacebookNativeAd = null;
        }

        if (mMoPubNative != null) {
            mMoPubNative.destroy();
            mMoPubNative = null;
        }

        if (mMopubView != null) {
            mMopubView.destroy();
            mMopubView = null;
        }

        if (mFacebookeNativeAdContainer != null) {
            mFacebookeNativeAdContainer.removeAllViews();
            mFacebookeNativeAdContainer = null;
        }

        if (mMoPubNativeAdContainer != null) {
            mMoPubNativeAdContainer.removeAllViews();
            mMoPubNativeAdContainer = null;
        }

        if (mBaiDuNative != null) {
            mBaiDuNative.destory();
            mBaiDuNative = null;
        }

        if (mBaiDuAdContainer != null) {
            mBaiDuAdContainer.removeAllViews();
            mBaiDuAdContainer = null;
        }

        mLayoutFacebook = null;
        mAdChoicesView = null;
//        mLayoutMoPub = null;
        mLayoutBaiDu = null;
        mAdRootView = null;
        mAdmobNativeAdvancedLoader = null;

        if (mLayoutAdmob != null) {
            mLayoutAdmob.removeAllViews();
            mLayoutAdmob = null;
        }

        if (mAdmobView != null) {
            mAdmobView.setAdListener(null);
            mAdmobView = null;
        }

        if (mMopubView != null) {
            mMopubView.destroy();
            mMopubView = null;
        }

        if (mLayoutBaiDu != null) {
            mLayoutBaiDu.removeAllViews();
            mLayoutBaiDu = null;
        }
        if (mAdmobBannerAdView != null) {
            mAdmobBannerAdView.destroy();
            mAdmobBannerAdView = null;
        }

        mIsClosed = true;
        mContextForFb = null;
        mContextForAdmob = null;
        mContextForMoPub = null;
        mContextBaiDu = null;
    }

    public AdvertisementAdapter getAdapter() {
        return mAdapter;
    }

    public Context getFbContext() {
        return mContextForFb;
    }

    public Context getAdmobContext() {
        return mContextForAdmob;
    }

    //---AD---
    public boolean performClick() {
        if (!mIsAdShown || mIsClosed) {
            return false;
        }

        boolean suc = false;

        if (!mCurrentDisplayingAdIsAdmob) {
            if (mLayoutFacebook.findViewById(R.id.nativeAdTitle).getVisibility() == View.VISIBLE) {
                mLayoutFacebook.findViewById(R.id.nativeAdTitle).performClick();
                suc = true;
            } else if (mLayoutFacebook.findViewById(R.id.nativeAdMedia).getVisibility() == View.VISIBLE) {
                mLayoutFacebook.findViewById(R.id.nativeAdMedia).performClick();
                suc = true;
            } else if (mLayoutFacebook.findViewById(R.id.nativeAdCallToAction).getVisibility() == View.VISIBLE) {
                mLayoutFacebook.findViewById(R.id.nativeAdCallToAction).performClick();
                suc = true;
            }
        } else {
            //mAdmobView.performClick();
        }

        return suc;
    }

//    public void forceRefreshAD(boolean showLoading) {
//        if (mPbLoading != null && showLoading) {
//            mPbLoading.setVisibility(View.VISIBLE);
//        }
//
//        mLastRefreshFB = System.currentTimeMillis() - mRefreshIntervalFb;
//        mLastRefreshAdmob = System.currentTimeMillis() - DEFAULT_ADMOB_REFRESH_INTERVAL;
//        refreshAD();
//        mCurrentIsForceRefresh = true;
//    }

    /*
    internal: refresh action called by Advertisement internal or user,
    set true in activity refreshAd();
     */
    public boolean refreshAD(boolean resetRequestIndex) {
        if (!mIsFbClosed && !canRefreshAd()) {
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-not reach refresh time or ad disabled");
            return false;
        }

        //refreshAd() by user, we reset internal ad request index for newest request loop
        if (resetRequestIndex) {
            mCurrentAdLoadIndex = -1;
        }

        String adType = getAndUpdateAdIndex();
        mCurrentLoadingAdType = adType;

        if (BuildConfig.DEBUG) {
            LogUtil.d(LOG_TAG, "refreshAD adType: " + adType);
        }
        LogUtil.d("ad_check", "refreshAD adType: " + adType);

        if (adType.equals(AdvertisementSwitcher.AD_FACEBOOK)) {
            if (!mFacebookInited) {
                initFacebook();
            }

            if (!loadFacebookAd()) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshAD(false);
                    }
                });
            }
        } else if (adType.equals(AdvertisementSwitcher.AD_ADMOB)) {
            if (!mAdmobInited) {
                initAdmob();
            }

            if (!loadAdmob()) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshAD(false);
                    }
                });
            }
        } else if (adType.equals(AdvertisementSwitcher.AD_MOPUB) || CallerAdManager.isMopubAll()) {
            if (!mMopubInited) {
                initMopub();
            }

            if (!loadMopubAd()) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshAD(false);
                    }
                });
            }
        } else if (adType.equals(AdvertisementSwitcher.AD_BAIDU)) {
            if (!mBaiDuInited) {
                initBaiDu();
            }

            if (!loadBaiDuAd()) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshAD(false);
                    }
                });
            }
        } else if (adType.equals(AdvertisementSwitcher.AD_MOPUB_BANNER)) {
            if (!mMopubBannerInited) {
                initMopubBanner();
            }

            if (!loadMopubBannerAd()) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshAD(false);
                    }
                });
            }
        } else if (adType.equals(AdvertisementSwitcher.AD_ADMOB_BIG_BANNER)) {
            if (!mAdMobBannerInited) {
                initAdMobBanner();
            }

            if (!loadAdmobBanner()) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshAD(false);
                    }
                });
            }
        }

        return true;
    }

    private boolean loadAdmobBanner() {
        boolean isSuc = false;
        if (!TextUtils.isEmpty(mAdapter.getAdmobBannerKey())) {
            isSuc = mIsLoading = true;
            mLastRefreshAdmobBanner = System.currentTimeMillis();

            AdRequest.Builder builder = new AdRequest.Builder();
            mAdmobBannerAdView.loadAd(builder.build());

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-load: " + mCurrentLoadingAdType);
        }
        return isSuc;
    }

    private void initAdMobBanner() {
        if (mIsClosed || mAdMobBannerInited) {
            return;
        }

        if (mLayoutAdmob == null) {
            mLayoutAdmob = mAdapter.getAdmobContainerView();
        }

        String admobBannerKey = mAdapter.getAdmobBannerKey();
        if (TextUtils.isEmpty(admobBannerKey)) {
            LogUtil.e(LOG_TAG, Advertisement.this.hashCode() + "- AdMob_Banner init error: AdMob_Banner key may not be null.");
        } else {
            mAdMobBannerInited = true;
            mAdmobBannerAdView = new AdView(mContextForAdmob);
            mAdmobBannerAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            mAdmobBannerAdView.setAdUnitId(admobBannerKey);
            mAdmobBannerAdView.setAdListener(new AdMobBannerAdListener());

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-init " + mCurrentLoadingAdType);
        }

    }

    private boolean loadMopubBannerAd() {
        boolean isLoadSuc = false;
        //change mopubview height size
        if (!TextUtils.isEmpty(mAdapter.getMopubKey(mCurrentLoadingAdType)) && mMopubBannerInited) {
//            ViewGroup.LayoutParams params = mMopubView.getLayoutParams();
//            if (params == null) {
//                params = new ViewGroup.LayoutParams(0, 0);
//            }
//            if (mIsBanner) {
//                params.height = Utils.dp2Px(50);
//                params.width = Utils.dp2Px(320);
//            } else {
//                params.height = Utils.dp2Px(250);
//                params.width = Utils.dp2Px(300);
//            }
//            mMopubView.setLayoutParams(params);

            mLastRefreshMopub = System.currentTimeMillis();
            mMopubView.loadAd();
            isLoadSuc = mIsLoading = true;

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-load: " + mCurrentLoadingAdType);
        }
        return isLoadSuc;
    }

    private void initMopubBanner() {
        if (mIsClosed || mMopubBannerInited) {
            return;
        }

        if (mMoPubNativeAdContainer == null) {
            mMoPubNativeAdContainer = mAdapter.getMoPubNativeContainerView();
        }
        String mopubBannerKey = mAdapter.getMopubKey(mCurrentLoadingAdType);
        if (!TextUtils.isEmpty(mopubBannerKey)) {
            mMopubView = new MoPubView(mContextForMoPub);
            mMopubView.setAdUnitId(mopubBannerKey);
            mMopubView.setBannerAdListener(new MopubBannerListener());
            mMopubBannerInited = true;

            mMopubView.setAutorefreshEnabled(false); //add for support cpm sdk on 20180123

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-init " + mCurrentLoadingAdType);
        } else {
            mMopubBannerInited = false;
            LogUtil.e(LOG_TAG, Advertisement.this.hashCode() + "- mopub_banner init error: MoPub_Banner key may not be null.");
        }
    }

    private boolean loadBaiDuAd() {
        boolean isSuc = false;
        if (mBaiDuAdContainer != null && mContextBaiDu != null) {
            mBaiDuNative = new DuNativeAd(mContextBaiDu, mAdapter.getBaiDuKey(), false);
            mBaiDuNative.setMobulaAdListener(new BaiduAdListenerImpl());
            mBaiDuNative.load();
            isSuc = true;
        }
        return isSuc;
    }

    private void initBaiDu() {
        if (mIsClosed || mBaiDuInited) {
            return;
        }
        mBaiDuAdContainer = mAdapter.getBaiDuContaionerView();
        if (BuildConfig.DEBUG)
            LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-init " + mCurrentLoadingAdType);
    }

    private void initMopub() {
        if (mIsClosed || mMopubInited) {
            return;
        }

        if (mMoPubNativeAdContainer == null)
            mMoPubNativeAdContainer = mAdapter.getMoPubNativeContainerView();

        String mopubKey = mAdapter.getMopubKey(AdvertisementSwitcher.AD_MOPUB);
        if (!TextUtils.isEmpty(mopubKey)) {
            mMoPubNative = new MoPubNative(mContextForMoPub,
                    mopubKey,
                    new MoPubNativeNetworkListenerImpl());
            mMopubInited = true;
        } else {
            mMopubInited = false;
            LogUtil.e(LOG_TAG, Advertisement.this.hashCode() + "- mopub init error: MoPub key may not be null.");
        }

        if (BuildConfig.DEBUG)
            LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-init : " + mCurrentLoadingAdType);
    }

    private boolean loadMopubAd() {
        boolean isLoadSuc = false;

        if (!TextUtils.isEmpty(mAdapter.getMopubKey(mCurrentLoadingAdType))) {
            ViewBinder viewBinder = new ViewBinder.Builder(mAdapter.getMoPubViewRes())
                    .mainImageId(R.id.native_ad_main_image)
                    .iconImageId(R.id.native_ad_icon_image)
                    .titleId(R.id.native_ad_title)
                    .textId(R.id.native_ad_text)
                    .callToActionId(R.id.native_ad_call_to_action)
                    .build();
            //ViewBinder viewBinder = new ViewBinder.Builder(0).build();
            MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(viewBinder);
            mMoPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);
            mMoPubNative.makeRequest();
            isLoadSuc = mIsLoading = true;
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-load mopub : " + mCurrentLoadingAdType);
        }
        return isLoadSuc;
    }

    /*--- 广告初始化入口 begin ---*/
    private boolean canRefreshAd() {
        if (mIsClosed || !AdvertisementSwitcher.getInstance().isAdEnabled(mAdapter.getPlacementId())) {
            return false;
        }

        //first call
        if (TextUtils.isEmpty(mCurrentLoadingAdType) && TextUtils.isEmpty(mCurrentShowAdType)) {
            return true;
        }

        if (mCurrentLoadingAdType.equals(AdvertisementSwitcher.AD_ADMOB) ||
                mCurrentShowAdType.equals(AdvertisementSwitcher.AD_ADMOB)) {
            return mContextForAdmob != null && !mIsLoading && System.currentTimeMillis() - mLastRefreshAdmob >= mRefreshIntervalAdmob;
        } else if (mCurrentLoadingAdType.equals(AdvertisementSwitcher.AD_FACEBOOK) ||
                mCurrentShowAdType.equals(AdvertisementSwitcher.AD_FACEBOOK)) {
            return mContextForFb != null && !mIsLoading && System.currentTimeMillis() - mLastRefreshFB >= mRefreshIntervalFb;
        } else if (mCurrentLoadingAdType.equals(AdvertisementSwitcher.AD_MOPUB) ||
                mCurrentShowAdType.equals(AdvertisementSwitcher.AD_MOPUB) || mCurrentLoadingAdType.equals(AdvertisementSwitcher.AD_MOPUB_BANNER) ||
                mCurrentLoadingAdType.equals(AdvertisementSwitcher.AD_MOPUB_50) ||
                mCurrentShowAdType.equals(AdvertisementSwitcher.AD_MOPUB_50)) {
            return !mIsLoading && System.currentTimeMillis() - mLastRefreshMopub >= mRefreshIntervalMopub;
        }
        return false;
    }

    private String getAndUpdateAdIndex() {
        mCurrentAdLoadIndex++;

        if (mAdPriority.get(mCurrentAdLoadIndex % mAdPriority.size()).equals(AdvertisementSwitcher.AD_FACEBOOK) &&
                !AdvertisementSwitcher.isFacebookEnable()) {
            //jump facebook ad
            mCurrentAdLoadIndex++;
        }
        return mCurrentAdLoadIndex >= mAdPriority.size() ? AdvertisementSwitcher.AD_DISABLE_FLAG : mAdPriority.get(mCurrentAdLoadIndex % mAdPriority.size());
    }

    private void initAd() {
        if (mContextForFb == null || mContextForAdmob == null) {
            return;
        }

        //get this information even if we don't load admob
        mAdmobType = mAdapter.getAdmobType();
        mAdPriority = AdvertisementSwitcher.getInstance().getAdPriority(mAdapter.getPlacementId());
        mForceLoadAdFromCache = mAdapter.didForceLoadAdFromCache();
        LOG_TAG = LOG_TAG + "-" + mAdapter.getPlacementId();
    }

    //初始化Facebook广告视图
    private void initFacebook() {
        try {
            mFacebookeNativeAdContainer = mAdapter.getFbContainerView();
            LayoutInflater inflater = (LayoutInflater) mContextForFb.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayoutFacebook = (LinearLayout) inflater.inflate(mAdapter.getFbViewRes(), null);
            mAdRootView = mLayoutFacebook.findViewById(R.id.layout_ad_view_root);

            if (mAdRootView == null) {
                throw new AdviewIDException("must be declare layout_ad_view_root in ad xml");
            }

            mFacebookInited = true;
        } catch (Exception e) {
            LogUtil.e(TAG, "initFacebook error: " + e.getMessage());
            LogUtil.error(e);

            /*if (AdviewIDException.class.isAssignableFrom(e.getClass())) {
                //make crash here
                if (android.support.v4.BuildConfig.DEBUG) {
                    int temp = 1 / 0;
                }
            }*/
        }
    }


    private boolean loadFacebookAd() {
        if (mIsClosed) {
            return false;
        }

//        if (!loadFBFromCache()) {
        return loadFBFromNetwork();
//        }

//        return true;
    }

//    private boolean loadFBFromCache() {
//        boolean suc = false;
//        try {
//            if (mForceLoadAdFromCache && AdCacheManager.getInstance().hasCachedAd()) {
//                mFacebookNativeAd = AdCacheManager.getInstance().getCachedAd();
//                NativeFBListener listener = new NativeFBListener();
//                FBImpressionListener impListener = new FBImpressionListener();
//
//                //this may happen in multi-thread, so check NULL again
//                if (mFacebookNativeAd != null) {
//                    mFacebookNativeAd.setAdListener(listener);
//                    mFacebookNativeAd.setImpressionListener(impListener);
//
//                    mLastRefreshFB = System.currentTimeMillis();
//                    mIsLoading = true;
//                    suc = true;
//                    if (BuildConfig.DEBUG)
//                        LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-load Facebook Ad from cache");
//
//                    simulateFBCacheAdLoadEvent(mFacebookNativeAd, listener, impListener, true);
//                }
//            }
//        } catch (Exception e) {
//            if (BuildConfig.DEBUG) LogUtil.error(e);
//        } finally {
//            return suc;
//        }
//    }

    private boolean loadFBFromNetwork() {
        LogUtil.d("ad_check", "fb common loadNativeAd start.");
        if (TextUtils.isEmpty(mAdapter.getFbKey())) {
            return false;
        }

        boolean suc = false;
        try {
            mFacebookNativeAd = new NativeAd(mContextForFb, mAdapter.getFbKey());
            // Set a listener to get notified when the ad was loaded.
            mFacebookNativeAd.setAdListener(new NativeFBListener());
//            mFacebookNativeAd.setImpressionListener(new FBImpressionListener());
            mFacebookNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
            mLastRefreshFB = System.currentTimeMillis();
            mIsLoading = true;
            suc = true;
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-load Facebook Ad");
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                LogUtil.e(TAG, "loadFBFromNetwork error: " + e.getMessage());
                LogUtil.error(e);
            }
        } finally {
            LogUtil.d("ad_check", "fb common loadNativeAd end.");
            return suc;
        }
    }

//    private void simulateFBCacheAdLoadEvent(final NativeAd ad, final NativeFBListener listener, final FBImpressionListener impListener, final boolean loadSuc) {
//        if (mForceLoadAdFromCache) {
//            mMainHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (loadSuc) {
//                        listener.onAdLoaded(ad);
//                    } else {
//                        listener.onError(ad, new AdError(40, "simulate fail"));
//                    }
//                }
//            }, 500);
//        }
//    }

    private void initAdmob() {
        if (BuildConfig.DEBUG) {
            LogUtil.d(LOG_TAG, "initAdmob 111");
        }
        if (mIsClosed) {
            return;
        }

        if (mAdmobInited) {
            return;
        }

        switch (mAdmobType) {
            case ADMOB_TYPE_NATIVE:
                if (BuildConfig.DEBUG) {
                    LogUtil.d(LOG_TAG, "initAdmobForNativeType ADMOB_TYPE_NATIVE");
                }
                initAdmobForNativeType();
                break;
            case ADMOB_TYPE_NATIVE_ADVANCED:
                if (BuildConfig.DEBUG) {
                    LogUtil.d(LOG_TAG, "initAdmobForNativeType ADMOB_TYPE_NATIVE_ADVANCED");
                }
                initAdmobForNativeAdvancedType();
                break;
        }
    }
    /*--- 广告初始化入口 end ---*/

    /*
    初始化Admob快速原生广告
     */
    private void initAdmobForNativeType() {
        if (BuildConfig.DEBUG) {
            LogUtil.d(LOG_TAG, "initAdmobForNativeType start");
        }
        mLayoutAdmob = mAdapter.getAdmobContainerView();
        mAdmobView = new AdView(mContextForAdmob);
        int width = 0;
        int height = 0;

        width = Stringutil.pxToDp(mContextForFb, mAdapter.getAdmobWidth());
        height = Stringutil.pxToDp(mContextForFb, mAdapter.getAdmobHeight());

        AdSize nativeAdSize = new AdSize(width, height);
        mAdmobView.setAdSize(nativeAdSize);
        mAdmobView.setAdUnitId(mAdapter.getAdmobKey());
        mAdmobView.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdLoaded() {
                if (BuildConfig.DEBUG)
                    LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-on Admob native Loaded");
                if (mIsClosed) {
                    return;
                }

                mADId = System.currentTimeMillis();
                mIsAdShown = true;
                mIsLoading = false;
                mAdShownTime = System.currentTimeMillis();
                mCurrentDisplayingAdIsAdmob = true;
                mIsAdMobLoadingOrShow = true;

                if (mLayoutFacebook != null) {
                    mLayoutFacebook.setVisibility(View.GONE);
                }

                mLayoutAdmob.setVisibility(View.VISIBLE);

                mAdapter.onAdLoaded(AdvertisementSwitcher.AD_ADMOB);
                mAdapter.onAdShow();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                if (mIsClosed) {
                    return;
                }

                if (BuildConfig.DEBUG)
                    LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-load admob native error " + errorCode);
                mAdapter.onAdError(mCurrentAdLoadIndex >= (mAdPriority.size() - 1));
                mIsLoading = false;
                mLastRefreshAdmob = System.currentTimeMillis() - mRefreshIntervalAdmob;
                mCurrentShowAdType = "";
                refreshAD(false);
            }

            @Override
            public void onAdOpened() {
                if (mIsClosed) {
                    return;
                }

                //NewUserStatisticsActionManager.getInstance().onAdClicked();
                mAdapter.onAdClicked(mIsAdMobLoadingOrShow);
                mLastRefreshAdmob = System.currentTimeMillis() - mRefreshIntervalAdmob;
                if (mRefreshWhenClicked) {
                    refreshAD(true);
                }

//                if (mAdapter.shouldLogClickTime()) {
//                    Map<String, String> params = new HashMap<>();
//                    params.put(StatisticsUtil.P_COMMON_KEY, (System.currentTimeMillis() - mAdShownTime) + "");
//                    StatisticsUtil.logEvent(StatisticsUtil.E_RESULT_PAGE_AD_CLICK_DELAY, params);
//                }

                if (BuildConfig.DEBUG)
                    LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "Admob native ad clicked");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the application after tapping on an ad.
            }
        });


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mLayoutAdmob.addView(mAdmobView, params);

        mAdmobInited = true;
    }

    /*
    初始化admob高级原生广告
     */
    private void initAdmobForNativeAdvancedType() {
        try {
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, hashCode() + "-initAdmobForNativeAdvancedType begin");
            mLayoutAdmob = mAdapter.getAdmobContainerView();
            AdLoader.Builder builder = new AdLoader.Builder(mContextForAdmob, mAdapter.getAdmobKey());

            builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                @Override
                public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                    mNativeAppInstallAd = ad;
                    if (mIsClosed) {
                        return;
                    }

                    if (ad == null) {
                        return;
                    }

                    if (mLayoutAdmob == null) {
                        return;
                    }

                    if (BuildConfig.DEBUG)
                        LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-admob advanced - app install ad loaded");
                    mADId = System.currentTimeMillis();
                    mIsLoading = false;
                    mIsAdShown = true;
                    mCurrentDisplayingAdIsAdmob = true;
                    mAdShownTime = System.currentTimeMillis();
                    //mLayoutAdmob.setVisibility(View.VISIBLE);
                    mCurrentShowAdType = mCurrentLoadingAdType;
                    mCurrentLoadingAdType = "";
                    inflateAppInstallView(ad);
                    adjustAdmobView(mLayoutAdmob);
                    mAdapter.onAdShow();
                    mAdapter.onAdLoaded(AdvertisementSwitcher.AD_ADMOB);
                }
            });

            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                @Override
                public void onContentAdLoaded(NativeContentAd ad) {
                    mNativeContentAd = ad;
                    if (mIsClosed) {
                        return;
                    }

                    if (ad == null) {
                        return;
                    }

                    if (mLayoutAdmob == null) {
                        return;
                    }

                    if (BuildConfig.DEBUG)
                        LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-admob advanced - content ad loaded");

                    mADId = System.currentTimeMillis();
                    mIsLoading = false;
                    mIsAdShown = true;
                    mCurrentDisplayingAdIsAdmob = true;
                    mAdShownTime = System.currentTimeMillis();
                    mCurrentShowAdType = mCurrentLoadingAdType;
                    mCurrentLoadingAdType = "";
                    mLayoutAdmob.setVisibility(View.VISIBLE);
                    inflateContentAdView(ad);
                    adjustAdmobView(mLayoutAdmob);

                    mAdapter.onAdShow();
                    mAdapter.onAdLoaded(AdvertisementSwitcher.AD_ADMOB);
                }
            });

            mAdmobNativeAdvancedLoader = builder.withAdListener(new com.google.android.gms.ads.AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    if (mIsClosed) {
                        return;
                    }

                    if (BuildConfig.DEBUG)
                        LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-admob advanced load fail: " + errorCode);
                    mIsLoading = false;
                    mLastRefreshAdmob = System.currentTimeMillis() - mRefreshIntervalAdmob;
                    mAdapter.onAdError(mCurrentAdLoadIndex >= (mAdPriority.size() - 1));
                    mCurrentShowAdType = "";
                    refreshAD(false);
                }

                @Override
                public void onAdOpened() {
                    if (mIsClosed) {
                        return;
                    }

                    //StatisticsUtil.logAdEvent(mAdapter.getPlacementId(), "admob", "点击");

                    //NewUserStatisticsActionManager.getInstance().onAdClicked();
                    mAdapter.onAdClicked(mIsAdMobLoadingOrShow);
                    mLastRefreshAdmob = System.currentTimeMillis() - mRefreshIntervalAdmob;
                    if (mRefreshWhenClicked) {
                        loadAdmob();
                    }

//                    if (mAdapter.shouldLogClickTime()) {
//                        Map<String, String> params = new HashMap<>();
//                        params.put(StatisticsUtil.P_COMMON_KEY, (System.currentTimeMillis() - mAdShownTime) + "");
//                        StatisticsUtil.logEvent(StatisticsUtil.E_RESULT_PAGE_AD_CLICK_DELAY, params);
//                    }

                    if (BuildConfig.DEBUG)
                        LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "Admob native advanced ad clicked");
                }

                @Override
                public void onAdLoaded() {
                    if (mIsClosed) {
                        return;
                    }

                    mIsLoading = false;
                    mIsAdMobLoadingOrShow = true;

                    //StatisticsUtil.logAdEvent(mAdapter.getPlacementId(), "admob", "展示");
                }
            }).build();

            mAdmobInited = true;
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, hashCode() + "-initAdmobForNativeAdvancedType end");
        } catch (Throwable e) {
//            if (BuildConfig.DEBUG) LogUtil.error(e);
        }
    }

    /*
    请求Admob广告
     */
    private boolean loadAdmob() {
        //admob init may throw exception, so we check init status
        if (mIsClosed || TextUtils.isEmpty(mAdapter.getAdmobKey()) || !mAdmobInited) {
            return false;
        }

        boolean suc = false;
        if (ADMOB_TYPE_NATIVE == mAdmobType) {
            AdRequest adRequest = new AdRequest.Builder().build();
            try {
                //admob sdk may be crashed
                mAdmobView.loadAd(adRequest);
                mLastRefreshAdmob = System.currentTimeMillis();
                mIsLoading = true;
                mIsAdMobLoadingOrShow = true;
                suc = true;
            } catch (Exception e) {
            }

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-loadAdmob quick native");
        } else if (ADMOB_TYPE_NATIVE_ADVANCED == mAdmobType) {
            mAdmobNativeAdvancedLoader.loadAd(new AdRequest.Builder().build());
            mLastRefreshAdmob = System.currentTimeMillis();
            mIsLoading = true;
            mIsAdMobLoadingOrShow = true;
            suc = true;
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-loadAdmob advanced native");
        }

        return suc;
    }

    /*
    创建Admob高级原生广告布局-App安装类型
     */
    private void inflateAppInstallView(NativeAppInstallAd ad) {
        if (mIsClosed) {
            return;
        }

        if (mLayoutFacebook != null) {
            mLayoutFacebook.setVisibility(View.GONE);
        }

        mLayoutAdmob.setVisibility(View.VISIBLE);

        final NativeAppInstallAdView adView = (NativeAppInstallAdView) LayoutInflater.from(mContextForAdmob).inflate(mAdapter.getAdmobViewRes(mAdmobType, true), null);
        {
            TextView tvTitle = (TextView) adView.findViewById(R.id.tv_title);
            TextView tvContent = (TextView) adView.findViewById(R.id.tv_content);
            ImageView ivIcon = (ImageView) adView.findViewById(R.id.iv_icon);
            ImageView ivContent = (ImageView) adView.findViewById(R.id.iv_content);

            //banner ad has no iv_ad_flag
            if (adView.findViewById(R.id.iv_ad_flag) != null) {
                adView.findViewById(R.id.iv_ad_flag).setVisibility(View.VISIBLE);
            }

            adView.setHeadlineView(adView.findViewById(R.id.tv_title));
            adView.setIconView(adView.findViewById(R.id.iv_icon));
            adView.setBodyView(adView.findViewById(R.id.tv_content));
            adView.setImageView(ivContent);
            adView.setCallToActionView(adView.findViewById(R.id.btn_callToAction));
            LogUtil.d(TAG, "inflateAppInstallView title:" + ad.getHeadline() + ",body:" + ad.getBody());
            tvTitle.setText(ad.getHeadline());
            tvContent.setText(ad.getBody());
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TextView tv = ((TextView) adView.findViewById(R.id.tv_content));
                    if (tv != null) {
                        tv.setSelected(true);
                    }
                }
            }, DEFAULT_SET_TEXTVIEW_SCROLL);

            //LionFontManager.setFontType(((TextView) adView.findViewById(R.id.tv_title)));

            List<Image> contentImages = ad.getImages();
            if (contentImages != null && contentImages.size() > 0) {
                ivContent.setImageDrawable(contentImages.get(0).getDrawable());
            }

            Image icon = ad.getIcon();
            if (ivIcon != null) {
                if (icon == null) {
                    if (mAdapter.hideIconViewWhenNone()) {
                        ivIcon.setVisibility(View.GONE);
                    }
                } else {
                    ivIcon.setImageDrawable(icon.getDrawable());
                    adView.findViewById(R.id.iv_icon).setVisibility(View.VISIBLE);
                }
            }

            ((Button) adView.findViewById(R.id.btn_callToAction)).setText(ad.getCallToAction());
            if (mAdapter.shouldShowActionButton()) {
                adView.findViewById(R.id.btn_callToAction).setVisibility(View.VISIBLE);
            } else {
                adView.findViewById(R.id.btn_callToAction).setVisibility(View.GONE);
            }
            adView.setNativeAd(ad);
        }

        mLayoutAdmob.removeAllViews();
        mLayoutAdmob.addView(adView);
    }

    /*
    创建Admob高级原生广告布局-内容类型
     */
    private void inflateContentAdView(NativeContentAd ad) {
        if (mIsClosed) {
            return;
        }

        if (mLayoutFacebook != null) {
            mLayoutFacebook.setVisibility(View.GONE);
        }

        mLayoutAdmob.setVisibility(View.VISIBLE);

        final NativeContentAdView adView = (NativeContentAdView) LayoutInflater.from(mContextForAdmob).inflate(mAdapter.getAdmobViewRes(mAdmobType, false), null);
        {
            TextView tvTitle = (TextView) adView.findViewById(R.id.tv_title);
            TextView tvContent = (TextView) adView.findViewById(R.id.tv_content);
            ImageView ivIcon = (ImageView) adView.findViewById(R.id.iv_icon);
            ImageView ivContent = (ImageView) adView.findViewById(R.id.iv_content);

            //banner ad has no iv_ad_flag
            if (adView.findViewById(R.id.iv_ad_flag) != null) {
                adView.findViewById(R.id.iv_ad_flag).setVisibility(View.VISIBLE);
            }

            adView.setHeadlineView(tvTitle);
            adView.setLogoView(ivIcon);
            adView.setBodyView(tvContent);
            adView.setImageView(ivContent);
            adView.setCallToActionView(adView.findViewById(R.id.btn_callToAction));
            LogUtil.d(TAG, "inflateContentAdView title:" + ad.getHeadline() + ",body:" + ad.getBody());
            tvTitle.setText(ad.getHeadline());
            tvContent.setText(ad.getBody());
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TextView tv = ((TextView) adView.findViewById(R.id.tv_content));
                    if (tv != null) {
                        tv.setSelected(true);
                    }
                }
            }, DEFAULT_SET_TEXTVIEW_SCROLL);
            //LionFontManager.setFontType(((TextView) adView.findViewById(R.id.tv_title)));
            Image logo = ad.getLogo();

            List<Image> contentImages = ad.getImages();
            if (contentImages != null && contentImages.size() > 0) {
                ivContent.setImageDrawable(contentImages.get(0).getDrawable());
            }

            if (ivIcon != null) {
                if (logo == null) {
                    if (mAdapter.hideIconViewWhenNone()) {
                        ivIcon.setVisibility(View.GONE);
                    }
                } else {
                    ivIcon.setImageDrawable(logo.getDrawable());
                    adView.findViewById(R.id.iv_icon).setVisibility(View.VISIBLE);
                }
            }

            ((Button) adView.findViewById(R.id.btn_callToAction)).setText(ad.getCallToAction());
            if (mAdapter.shouldShowActionButton()) {
                adView.findViewById(R.id.btn_callToAction).setVisibility(View.VISIBLE);
            } else {
                adView.findViewById(R.id.btn_callToAction).setVisibility(View.GONE);
            }

            adView.setNativeAd(ad);
        }

        mLayoutAdmob.removeAllViews();
        mLayoutAdmob.addView(adView);
    }

    /*
      Admob大图广告布局-调整
       */
    private void adjustAdmobView(FrameLayout layoutAdmob) {
        try {
            mAdapter.adjustAdmobView(layoutAdmob);
        } catch (Exception e) {
            LogUtil.e(TAG, "adjustAdmobView error: " + e.getMessage());
            LogUtil.error(e);
        }
    }

    /*
    创建Facebook的banner广告
     */
    private void inflateBannerAd(NativeAd nativeAd, final View adView) {
        if (mIsClosed) {
            return;
        }

        if (mLayoutAdmob != null) {
            mLayoutAdmob.setVisibility(View.GONE);
        }

        mLayoutFacebook.setVisibility(View.VISIBLE);

        // Create native UI using the ad metadata.
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.nativeAdIcon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.nativeAdTitle);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.nativeAdBody);
        TextView nativeAdCallToAction = (TextView) adView.findViewById(R.id.nativeAdCallToAction);
        FrameLayout nativeAdImage = (FrameLayout) adView.findViewById(R.id.layout_fb_image);

        // Setting the Text
//        nativeAdCallToAction.setBackgroundResource(Math.random() > 0.5 ? R.drawable.ic_share : R.drawable.ico_arrow_right);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        if (mAdapter.shouldShowActionButton()) {
            nativeAdCallToAction.setVisibility(View.VISIBLE);
        } else {
            nativeAdCallToAction.setVisibility(View.GONE);
        }
        nativeAdTitle.setText(nativeAd.getAdTitle());
        nativeAdBody.setText(nativeAd.getAdBody());
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tv = ((TextView) adView.findViewById(R.id.nativeAdBody));
                if (tv != null) {
                    tv.setSelected(true);
                }
            }
        }, DEFAULT_SET_TEXTVIEW_SCROLL);

        ImageView smallIconView = mAdapter.getSmallIconView();
        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, smallIconView != null ? smallIconView : nativeAdIcon);

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable
//        ArrayList<View> viewList = new ArrayList<>();
//        if (isOnlyBtnClickable) {
//            if (nativeAdIcon != null) {
//                viewList.add(nativeAdIcon);
//            }
//            if (nativeAdTitle != null) {
//                viewList.add(nativeAdTitle);
//            }
//            if (nativeAdBody != null) {
//                viewList.add(nativeAdBody);
//            }
//            if (nativeAdCallToAction != null) {
//                viewList.add(nativeAdCallToAction);
//            }
//            if (nativeAdImage != null) {
//                viewList.add(nativeAdImage);
//            }
//        }
//        nativeAd.registerViewForInteraction(adView, viewList); //只有按钮和图片文字可点

        ArrayList<View> viewList = new ArrayList<>();
        if (isOnlyBtnClickable) {
            if (nativeAdCallToAction != null) {
                nativeAd.registerViewForInteraction(nativeAdCallToAction);
            } else {
                nativeAd.registerViewForInteraction(adView);
            }
        } else if (mIsOnlyBtnAndTextClickable) {
            if (nativeAdIcon != null) {
                viewList.add(nativeAdIcon);
            }
            if (nativeAdTitle != null) {
                viewList.add(nativeAdTitle);
            }
            if (nativeAdBody != null) {
                viewList.add(nativeAdBody);
            }
            if (nativeAdCallToAction != null) {
                viewList.add(nativeAdCallToAction);
            }
//            if (nativeAdImage != null) {
//                viewList.add(nativeAdImage);
//            }
            nativeAd.registerViewForInteraction(adView, viewList); //只有按钮和图片文字可点
        } else {
            nativeAd.registerViewForInteraction(adView);
        }

        if (mAdChoicesView == null) {
            mAdChoicesView = new AdChoicesView(mContextForFb, nativeAd, true);
            FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(Stringutil.dpToPx(ApplicationEx.getInstance(), 16), Stringutil.dpToPx(ApplicationEx.getInstance(), 16));
            layoutParam.gravity = mIsCalllog ? Gravity.RIGHT | Gravity.BOTTOM | Gravity.END : Gravity.LEFT | Gravity.BOTTOM | Gravity.START;
            nativeAdImage.addView(mAdChoicesView, layoutParam);
        }

        // Or you can replace the above call with the following function to specify the clickable areas.
        // nativeAd.registerViewForInteraction(adView,
        //     Arrays.asList(nativeAdCallToAction, nativeAdMedia));

        mFacebookeNativeAdContainer.removeAllViews();
        mFacebookeNativeAdContainer.addView(adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Stringutil.dpToPx(mContextForFb, mAdapter.getFbAdsHight())));//64 is default height
    }

    /*
    创建Facebook的通用大图广告
     */
    private void inflateAd(NativeAd nativeAd, final View adView) {
        LogUtil.d(LOG_TAG, "inflateAd big common start.");
        if (mIsClosed) {
            return;
        }
        if (mLayoutAdmob != null) {
            mLayoutAdmob.setVisibility(View.GONE);
        }

        mLayoutFacebook.setVisibility(View.VISIBLE);

        // Create native UI using the ad metadata.
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.nativeAdIcon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.nativeAdTitle);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.nativeAdBody);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.nativeAdMedia);
        View nativeAdCallToAction = (View) adView.findViewById(R.id.nativeAdCallToAction);
        FrameLayout nativeAdImage = (FrameLayout) adView.findViewById(R.id.layout_fb_image);
        //ImageView ivGradientSplashLine = (ImageView) adView.findViewById(R.id.ivGradientSplashLine);

        ArrayList<View> viewList = new ArrayList<>();
        if (isOnlyBtnClickable) {
            if (nativeAdCallToAction != null) {
                viewList.add(nativeAdCallToAction);
            }
        } else if (mIsOnlyBtnAndTextClickable) {
            if (nativeAdTitle != null) {
                viewList.add(nativeAdTitle);
            }
            if (nativeAdBody != null) {
                viewList.add(nativeAdBody);
            }

            if (nativeAdCallToAction != null) {
                viewList.add(nativeAdCallToAction);
            }
        } else {
            if (nativeAdIcon != null) {
                viewList.add(nativeAdIcon);
            }
            if (nativeAdTitle != null) {
                viewList.add(nativeAdTitle);
            }
            if (nativeAdBody != null) {
                viewList.add(nativeAdBody);
            }
            if (nativeAdMedia != null) {
                viewList.add(nativeAdMedia);
            }
            if (nativeAdCallToAction != null) {
                viewList.add(nativeAdCallToAction);
            }
            if (nativeAdImage != null) {
                viewList.add(nativeAdImage);
            }
        }

//        viewList.add(adView.findViewById(R.id.ll_adview));

        // Setting the Text
        if (mAdapter.shouldShowActionButton()) {
            nativeAdCallToAction.setVisibility(View.VISIBLE);
        } else {
            nativeAdCallToAction.setVisibility(View.GONE);
        }

        if (nativeAdCallToAction instanceof Button) {
            ((Button) nativeAdCallToAction).setText(nativeAd.getAdCallToAction());
        } else if (nativeAdCallToAction instanceof RelativeLayout) {
            ((TextView) adView.findViewById(R.id.tvNativeAdCallToAction)).setText(nativeAd.getAdCallToAction());
        } else if (nativeAdCallToAction instanceof TextView) {
            ((TextView) nativeAdCallToAction).setText(nativeAd.getAdCallToAction());
        }
        nativeAdTitle.setText(nativeAd.getAdTitle());
        nativeAdBody.setText(nativeAd.getAdBody());
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tv = ((TextView) adView.findViewById(R.id.nativeAdBody));
                if (tv != null) {
                    tv.setSelected(true);
                }
            }
        }, DEFAULT_SET_TEXTVIEW_SCROLL);

        ImageView smallIconView = mAdapter.getSmallIconView();
        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, smallIconView != null ? smallIconView : nativeAdIcon);

        // Downloading and setting the cover image.
        NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
        int bannerWidth = adCoverImage.getWidth();
        int bannerHeight = adCoverImage.getHeight();
        WindowManager wm = (WindowManager) mContextForFb.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        //Ad Root View layout params
        //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mAdRootView.getLayoutParams();
        int paddingLeft = mAdRootView.getPaddingLeft();
        int paddingRight = mAdRootView.getPaddingRight();
        int topParentSpaceX = mAdapter.getAdContainerSpaceX();

        int mediaViewWidth = metrics.widthPixels - paddingLeft - paddingRight - topParentSpaceX;
        int screenHeight = metrics.heightPixels;

        //MediaView has limit height that can't fill it when expanded this value
//        mDefaultMediaViewHeight = Math.min((int) (((double) mediaViewWidth / (double) bannerWidth) * bannerHeight), screenHeight / 3) - Stringutil.dpToPx(mContextForFb, 2);

        if (!mIsAdaptiveSize) {
            int height = Math.min((int) ((mediaViewWidth * 1.0 / bannerWidth) * bannerHeight), screenHeight / 3);
            nativeAdMedia.setLayoutParams(new FrameLayout.LayoutParams(mediaViewWidth, height));
        }
        nativeAdMedia.setNativeAd(nativeAd);

//        int mediaViewHeight = mDefaultMediaViewHeight;
//        FrameLayout.LayoutParams mediaViewParam = new FrameLayout.LayoutParams(
//                mediaViewWidth,//ViewGroup.LayoutParams.MATCH_PARENT,
//                mediaViewHeight
//        );

//        nativeAdMedia.setLayoutParams(mediaViewParam);
//        if (ivGradientSplashLine != null) {
//            ivGradientSplashLine.setLayoutParams(mediaViewParam);
//        }

        if (nativeAdMedia != null) {
            LogUtil.d(LOG_TAG, "inflateAd adCoverImage width: " + adCoverImage.getWidth());
        }

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable
        if (isFullClickable) {
            nativeAd.registerViewForInteraction(adView); //空白区域可点
        } else {
            nativeAd.registerViewForInteraction(adView, viewList); //只有按钮和图片文字可点
        }

        if (mAdChoicesView == null) {
            mAdChoicesView = new AdChoicesView(mContextForFb, nativeAd, true);
            FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(Stringutil.dpToPx(mContextForFb, 24), Stringutil.dpToPx(mContextForFb, 24));
            if (mIsResultPage) {
                layoutParam.gravity = Gravity.LEFT | Gravity.TOP;
                layoutParam.leftMargin = Stringutil.dpToPx(mContextForFb, 40);
                FrameLayout fyChoice = (FrameLayout) adView.findViewById(R.id.fly_adchoice);
                if (fyChoice != null) {
                    fyChoice.addView(mAdChoicesView, layoutParam);
                }
            } else {
                layoutParam.gravity = Gravity.RIGHT | Gravity.TOP;
                nativeAdImage.addView(mAdChoicesView, layoutParam);
            }

        }

        LogUtil.d(LOG_TAG, "inflateAd big common end.");

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (!mIsAdaptiveSize) {
            width = metrics.widthPixels - topParentSpaceX;
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        mFacebookeNativeAdContainer.removeAllViews();
        mFacebookeNativeAdContainer.addView(adView, params);
    }

    private void inflateMoPubNativeAd(com.mopub.nativeads.NativeAd nativeAd, LinearLayout layoutMoPub) {
        if (mIsClosed) {
            return;
        }

        if (mLayoutAdmob != null) {
            mLayoutAdmob.setVisibility(View.GONE);
        }

        if (mLayoutFacebook != null) {
            mLayoutFacebook.setVisibility(View.GONE);
        }

        BaseNativeAd baseNativeAd = nativeAd.getBaseNativeAd();
        if (baseNativeAd instanceof StaticNativeAd) {
            StaticNativeAd staticNativeAd = (StaticNativeAd) baseNativeAd;
            String iconImageUrl = staticNativeAd.getIconImageUrl();
            String mainImageUrl = staticNativeAd.getMainImageUrl();
            String callToAction = staticNativeAd.getCallToAction();
            String title = staticNativeAd.getTitle();
            String text = staticNativeAd.getText();

            ImageView nativeAdIcon = (ImageView) layoutMoPub.findViewById(R.id.native_ad_icon_image);
            ImageView nativeAdMainImage = (ImageView) layoutMoPub.findViewById(R.id.native_ad_main_image);
            TextView nativeAdTitle = (TextView) layoutMoPub.findViewById(R.id.native_ad_title);
            TextView nativeAdBody = (TextView) layoutMoPub.findViewById(R.id.native_ad_text);
            TextView nativeAdAction = (TextView) layoutMoPub.findViewById(R.id.native_ad_call_to_action);

//            ImageView nativeAdIcon = (ImageView) layoutMoPub.findViewById(R.id.nativeAdIcon);
//            ImageView nativeAdMainImage = (ImageView) layoutMoPub.findViewById(R.id.nativeAdImage);
//            TextView nativeAdTitle = (TextView) layoutMoPub.findViewById(R.id.nativeAdTitle);
//            TextView nativeAdBody = (TextView) layoutMoPub.findViewById(R.id.nativeAdBody);
//            TextView nativeAdAction = (TextView) layoutMoPub.findViewById(R.id.nativeAdCallToAction);

            if (nativeAdTitle != null) {
                nativeAdTitle.setText(title);
            }
            if (nativeAdBody != null) {
                nativeAdBody.setText(text);
                nativeAdBody.setSelected(true);
            }
            if (nativeAdAction != null) {
                nativeAdAction.setText(callToAction);
                if (mAdapter.shouldShowActionButton()) {
                    nativeAdAction.setVisibility(View.VISIBLE);
                } else {
                    nativeAdAction.setVisibility(View.GONE);
                }
            }
            if (nativeAdIcon != null) {
                NativeImageHelper.loadImageView(iconImageUrl, nativeAdIcon);
            }
            if (nativeAdMainImage != null) {
                NativeImageHelper.loadImageView(mainImageUrl, nativeAdMainImage);
            }

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(DeviceUtil.getScreenWidth() - mAdapter.getAdContainerSpaceX(), ViewGroup.LayoutParams.MATCH_PARENT);
            mMoPubNativeAdContainer.removeAllViews();
            mMoPubNativeAdContainer.addView(layoutMoPub, params);
            staticNativeAd.prepare(mMoPubNativeAdContainer);
            mMoPubNativeAdContainer.setVisibility(View.VISIBLE);
        }
    }

    private void inflateBaiduAd(DuNativeAd nativeAd) {
        if (mBaiDuAdContainer != null) {
            mBaiDuAdContainer.removeAllViews();

            mLayoutBaiDu = (LinearLayout) LayoutInflater.from(mContextBaiDu).inflate(mAdapter.getBaiDuViewRes(), mBaiDuAdContainer, false);
            mBaiDuAdContainer.addView(mLayoutBaiDu);

            ImageView nativeAdIcon = (ImageView) mLayoutBaiDu.findViewById(R.id.nativeAdIcon);
            ImageView nativeAdImage = (ImageView) mLayoutBaiDu.findViewById(R.id.nativeAdImage);
            TextView nativeAdTitle = (TextView) mLayoutBaiDu.findViewById(R.id.nativeAdTitle);
            TextView nativeAdBody = (TextView) mLayoutBaiDu.findViewById(R.id.nativeAdBody);
            TextView nativeAdCallToAction = (TextView) mLayoutBaiDu.findViewById(R.id.nativeAdCallToAction);

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            if (nativeAdTitle != null) {
                nativeAdTitle.setText(nativeAd.getTitle());
                clickableViews.add(nativeAdTitle);
            }
            if (nativeAdBody != null) {
                nativeAdBody.setText(nativeAd.getShortDesc());
                if (mIsBanner) {
                    nativeAdBody.setSelected(true);
                }
                clickableViews.add(nativeAdBody);
            }
            if (nativeAdCallToAction != null) {
                nativeAdCallToAction.setText(nativeAd.getCallToAction());
                clickableViews.add(nativeAdCallToAction);
                nativeAdCallToAction.setVisibility(mAdapter.shouldShowActionButton() ? View.VISIBLE : View.GONE);
            }
            if (nativeAdIcon != null) {
                GlideHelper.with(mContextBaiDu).load(nativeAd.getIconUrl()).into(nativeAdIcon);
                clickableViews.add(nativeAdIcon);
            }
            if (nativeAdImage != null) {
                GlideHelper.with(mContextBaiDu).load(nativeAd.getImageUrl()).into(nativeAdImage);
                clickableViews.add(nativeAdImage);
            }

            try {
                nativeAd.registerViewForInteraction(mBaiDuAdContainer);
//                nativeAd.registerViewForInteraction(mBaiDuAdContainer, clickableViews);
            } catch (Exception e) {
                LogUtil.e(TAG, "inflateBaiduAd error: " + e.getMessage());
                LogUtil.error(e);
            }
            mBaiDuAdContainer.setVisibility(View.VISIBLE);
            if (mAdapter != null) {
                mAdapter.onAdShow();
            }
        }
    }

    public void setFbClose(boolean isFbClosed) {
        mIsFbClosed = isFbClosed;
    }

    public void isAdaptiveSize(boolean isAdaptiveSize) {
        mIsAdaptiveSize = isAdaptiveSize;
    }

    // baidu ad load listener
    private class BaiduAdListenerImpl implements DuAdListener {

        @Override
        public void onError(DuNativeAd duNativeAd, com.duapps.ad.AdError adError) {
            if (mIsClosed) {
                return;
            }

            mIsLoading = false;
            mIsAdShown = false;
            mIsLoadBaiDuSuc = false;
            mCurrentDisplayingAdIsAdmob = false;
            mIsLoadSuc = false;

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-Du load error: " + adError.getErrorMessage());

            mAdapter.onAdError(mCurrentAdLoadIndex >= (mAdPriority.size() - 1));
            mCurrentLoadingAdType = "";
            mLastRefreshBaiDu = System.currentTimeMillis() - mRefreshIntervalBaiDu;

            refreshAD(false);
        }

        @Override
        public void onAdLoaded(DuNativeAd duNativeAd) {
            try {
                if (mIsClosed) return;

                if (BuildConfig.DEBUG)
                    LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-on BaiDu ad loaded");

                mIsLoading = false;
                mIsLoadSuc = true;
                mIsAdMobLoadingOrShow = false;
                mIsLoadBaiDuSuc = true;

                if (mBaiDuAdContainer != null) {
                    inflateBaiduAd(duNativeAd);
                    mAdapter.adjustBaiDuContaionerView(mLayoutBaiDu);

                    mAdShownTime = System.currentTimeMillis();
                    mIsAdShown = true;
                    mCurrentDisplayingAdIsAdmob = false;
                    mAdapter.onAdLoaded(AdvertisementSwitcher.AD_BAIDU);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "BaiDu advertisement load exception: " + e.getMessage());
            }
        }

        @Override
        public void onClick(DuNativeAd duNativeAd) {
            if (mIsClosed) {
                return;
            }
            mLastRefreshBaiDu = System.currentTimeMillis() - mRefreshIntervalBaiDu;
            if (mRefreshWhenClicked) {
                refreshAD(true);
            }
            mAdapter.onAdClicked(false);

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-BaiDuNative ad clicked");
        }
    }

    //Mopub listener begin
    private class MoPubNativeNetworkListenerImpl implements MoPubNative.MoPubNativeNetworkListener {

        @Override
        public void onNativeLoad(com.mopub.nativeads.NativeAd nativeAd) {
            try {
                if (mIsClosed) {
                    return;
                }

                if (BuildConfig.DEBUG)
                    LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-on MoPub ad loaded");

                mIsLoading = false;
                mIsLoadSuc = true;
                mIsAdMobLoadingOrShow = false;
                mIsLoadMoPubSuc = true;

                if (mMoPubNativeAdContainer != null) {
                    mMoPubNativeAd = nativeAd;

                    mLayoutMoPub = (LinearLayout) LayoutInflater.from(mContextForMoPub).inflate(mAdapter.getMoPubViewRes(), mMoPubNativeAdContainer, false);

                    nativeAd.setMoPubNativeEventListener(new MoPubNativeEventListenerImpl());
                    inflateMoPubNativeAd(nativeAd, mLayoutMoPub);
                    mAdapter.adjustMoPubContainerView(mLayoutMoPub);

                    mAdShownTime = System.currentTimeMillis();
                    mIsAdShown = true;
                    mCurrentDisplayingAdIsAdmob = false;
                }

                mCurrentShowAdType = mCurrentLoadingAdType;
                mCurrentLoadingAdType = "";
                mAdapter.onAdLoaded(AdvertisementSwitcher.AD_MOPUB);
            } catch (Exception e) {
                LogUtil.e(TAG, "MoPub advertisement load exception: " + e.getMessage());
            }
        }

        @Override
        public void onNativeFail(NativeErrorCode errorCode) {
            mIsLoading = false;
            mIsLoadMoPubSuc = false;
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-MoPub load error: " + errorCode.toString());
            mAdapter.onAdError(mCurrentAdLoadIndex >= (mAdPriority.size() - 1));
            mCurrentLoadingAdType = "";
            mLastRefreshMopub = System.currentTimeMillis() - mRefreshIntervalMopub;
            refreshAD(false);
        }
    }

    private class MoPubNativeEventListenerImpl implements com.mopub.nativeads.NativeAd.MoPubNativeEventListener {

        @Override
        public void onImpression(View view) {
            if (mIsClosed) {
                return;
            }
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-MopubNative ad onImpression");
            mAdapter.onAdShow();
        }

        @Override
        public void onClick(View view) {
            if (mIsClosed) {
                return;
            }
            mLastRefreshMopub = System.currentTimeMillis() - mRefreshIntervalMopub;
            if (mRefreshWhenClicked) {
                refreshAD(true);
            }
            mAdapter.onAdClicked(false);

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-MopubNative ad clicked");
        }
    }

    private class MopubBannerListener implements MoPubView.BannerAdListener {
        @Override
        public void onBannerLoaded(MoPubView banner) {
            if (mIsClosed || !(mCurrentLoadingAdType.equals(AdvertisementSwitcher.AD_MOPUB_BANNER)
                    || mCurrentLoadingAdType.equals(AdvertisementSwitcher.AD_MOPUB_50))) {
                return;
            }
            mIsLoadMoPubBannerSuc = true;
            mCurrentShowAdType = mCurrentLoadingAdType;
            banner.setVisibility(View.VISIBLE);
            mCurrentLoadingAdType = "";
            mIsLoading = false;

            if (mMoPubNativeAdContainer != null) {
                mMoPubNativeAdContainer.removeAllViews();
                mMoPubNativeAdContainer.addView(banner);
                mMoPubNativeAdContainer.setVisibility(View.VISIBLE);
            }

            if (mAdapter != null) {
                mAdapter.onAdLoaded(AdvertisementSwitcher.AD_MOPUB_BANNER);
            }
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-mopub_banner loaded.");
        }

        @Override
        public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
            if (mIsClosed || !(mCurrentLoadingAdType.equals(AdvertisementSwitcher.AD_MOPUB_BANNER) ||
                    mCurrentLoadingAdType.equals(AdvertisementSwitcher.AD_MOPUB_50))) {
                return;
            }

            mIsLoading = false;
            mLastRefreshMopub = System.currentTimeMillis() - mRefreshIntervalMopub;
            mAdapter.onAdError(mCurrentAdLoadIndex >= (mAdPriority.size() - 1));
            mCurrentLoadingAdType = "";
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-mopub load fail: " + errorCode);
            refreshAD(false);
        }

        @Override
        public void onBannerClicked(MoPubView banner) {
            if (mIsClosed) {
                return;
            }

            mLastRefreshMopub = System.currentTimeMillis() - mRefreshIntervalMopub;
            if (mRefreshWhenClicked) {
                refreshAD(true);
            }

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "mopub ad clicked");
            mAdapter.onAdClicked(false);
        }

        @Override
        public void onBannerExpanded(MoPubView banner) {
        }

        @Override
        public void onBannerCollapsed(MoPubView banner) {
        }
    }
    //Mopub listener end

    /*
    Facebook请求广告的网络回调
     */
    private class NativeFBListener implements AdListener {
        @Override
        public void onAdClicked(Ad arg0) {
            if (mShouldCacheFbAd) {
                mUsableAdMap.remove(arg0);
            }

            if (mIsClosed) {
                return;
            }

            //StatisticsUtil.logAdEvent(mAdapter.getPlacementId(), "facebook", "点击");
//            if (mAdapter.didForceLoadAdFromCache()) {
//                AdCacheManager.getInstance().cachedAdClicked(mADId);
//            }

            mLastRefreshFB = System.currentTimeMillis() - mRefreshIntervalFb;
            if (mRefreshWhenClicked) {
                refreshAD(true);
            }
            mAdapter.onAdClicked(mIsAdMobLoadingOrShow);
//            NewUserStatisticsActionManager.getInstance().onAdClicked();

//            if (mAdapter.shouldLogClickTime()) {
//                Map<String, String> params = new HashMap<>();
//                params.put(StatisticsUtil.P_COMMON_KEY, (System.currentTimeMillis() - mAdShownTime) + "");
//                StatisticsUtil.logEvent(StatisticsUtil.E_RESULT_PAGE_AD_CLICK_DELAY, params);
//            }

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "Facebook ad clicked");
        }

        @Override
        public void onAdLoaded(Ad ad) {
            try {
                if (mFacebookNativeAd == null || mFacebookNativeAd != ad) {
                    // Race condition, load() called again before last ad was displayed
                    return;
                }

                if (mIsClosed) {
                    return;
                }

                if (BuildConfig.DEBUG)
                    LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-on Facebook ad loaded");

                mIsLoading = false;
                mIsLoadSuc = true;
                mIsAdMobLoadingOrShow = false;
                mIsLoadFbADSuc = true;
//                mADId = AdCacheManager.getInstance().getCachedAdId(mFacebookNativeAd);
//                if (mADId == -1) {
//                    mADId = System.currentTimeMillis();
//                }
//
//                if (mShouldCacheFbAd) {
//                    mUsableAdMap.put((NativeAd) ad, mADId);
//                }

                if (mFacebookeNativeAdContainer != null /*&& !mContext.isFinishing()*/) {
                    mFacebookeNativeAdContainer.setVisibility(View.VISIBLE);
                    // Unregister last ad
                    mFacebookNativeAd.unregisterView();

                    if (mIsBanner) {
                        inflateBannerAd(mFacebookNativeAd, mLayoutFacebook);
                    } else {
                        inflateAd(mFacebookNativeAd, mLayoutFacebook);
                    }
                    mAdapter.adjustFbContainerView(mLayoutFacebook);

                    mAdShownTime = System.currentTimeMillis();
                    mIsAdShown = true;
                    mCurrentDisplayingAdIsAdmob = false;
                }
                mCurrentShowAdType = mCurrentLoadingAdType;
                mCurrentLoadingAdType = "";
                mAdapter.onAdLoaded(AdvertisementSwitcher.AD_FACEBOOK);
                LogUtil.d("advertisement", "mFacebookeNativeAdContainer height:" + mFacebookeNativeAdContainer.getHeight());
            } catch (Exception e) {
                LogUtil.e(TAG, "NativeFBListener onAdLoaded error: " + e.getMessage());
                LogUtil.error(e);
            }
        }

        @Override
        public void onError(Ad arg0, AdError arg1) {
            try {
                if (mIsClosed) {
                    return;
                }

                mIsLoading = false;
                mIsLoadFbADSuc = false;

                if (BuildConfig.DEBUG)
                    LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-facebook load error: " + arg1.getErrorMessage());
                mLastRefreshFB = System.currentTimeMillis() - mRefreshIntervalFb;
                mAdapter.onAdError(mCurrentAdLoadIndex >= (mAdPriority.size() - 1));

//                LogUtil.d("ad_check", "fb common onError mCurrentAdLoadIndex: " + mCurrentAdLoadIndex+", mAdPriority.size():"+mAdPriority.size());

                mCurrentLoadingAdType = "";
                //try load next ad type
                refreshAD(false);
            } catch (Exception e) {
                LogUtil.e(TAG, "NativeFBListener onError error: " + e.getMessage());
                LogUtil.error(e);
            }
            //send error flurry
            sendFbErrorFlurry(arg1);
        }

        @Override
        public void onLoggingImpression(Ad ad) {
            if (mAdapter != null) {
                if (BuildConfig.DEBUG)
                    LogUtil.d(LOG_TAG, "onLoggingImpression:" + ad.getPlacementId());
                mAdapter.onAdShow();
                //StatisticsUtil.logAdEvent(mAdapter.getPlacementId(), "facebook", "展示");
            }
        }
    }

    private class AdMobBannerAdListener extends com.google.android.gms.ads.AdListener {
        @Override
        public void onAdFailedToLoad(int i) {
            if (mIsClosed) {
                return;
            }

            mIsLoading = false;
            mIsLoadFbADSuc = false;

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-AdMob_Banner load error code: " + i);
        }

        @Override
        public void onAdLoaded() {
            if (mIsClosed) return;

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-on AdMob_Banner ad loaded");

            mIsLoadSuc = true;
            mIsLoading = false;
            mIsLoadFbADSuc = false;
            mIsLoadAdmobBannerSuc = true;
            mIsAdMobLoadingOrShow = false;

            if (mLayoutBaiDu != null) {
                mLayoutBaiDu.setVisibility(View.GONE);
            }

            if (mLayoutMoPub != null) {
                mLayoutMoPub.setVisibility(View.GONE);
            }

            if (mLayoutFacebook != null) {
                mLayoutFacebook.setVisibility(View.GONE);
            }

            if (mLayoutAdmob != null) {

                if (mAdmobBannerAdView != null && mAdmobBannerAdView.getParent() == null) {
                    mLayoutAdmob.removeAllViews();
                    mLayoutAdmob.addView(mAdmobBannerAdView);
                    mLayoutAdmob.setVisibility(View.VISIBLE);
                }

                mAdapter.onAdLoaded(AdvertisementSwitcher.AD_ADMOB_BIG_BANNER);

                mAdShownTime = System.currentTimeMillis();
                mIsAdShown = true;
                mCurrentDisplayingAdIsAdmob = true;

            }
            mCurrentShowAdType = mCurrentLoadingAdType;
            mCurrentLoadingAdType = "";
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();

            if (mIsClosed) {
                return;
            }
            mLastRefreshAdmobBanner = System.currentTimeMillis() - mRefreshIntervalAdmobBanner;

            mAdapter.onAdClicked(mIsAdMobLoadingOrShow);

            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "ADMob_Banner ad clicked");
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
        }
    }

    private void sendFbErrorFlurry(AdError arg1) {
        try {
            if (mContextForFb != null && mAdapter != null) {

                FlurryAgent.onStartSession(ApplicationEx.getInstance());
                String placement_id = mAdapter.getPlacementId();
                Map<String, String> eventParams = new HashMap<String, String>();
                eventParams.put(placement_id, "" + arg1.getErrorCode());
                FlurryAgent.logEvent("cid_fb_error", eventParams);
                FlurryAgent.onEndSession(ApplicationEx.getInstance());

            }
        } catch (Exception e) {

        }

    }

    public interface AdvertisementAdapter {
        String getPlacementId();

        FrameLayout getAdmobContainerView();

        LinearLayout getFbContainerView();

        LinearLayout getMoPubNativeContainerView();

        LinearLayout getBaiDuContaionerView();

        int getFbViewRes();

        int getAdmobViewRes(int type, boolean isAppInstall);

        int getMoPubViewRes();

        int getBaiDuViewRes();

        String getAdmobKey();

        String getAdmobBannerKey();

        String getFbKey();

        String getMopubKey(String mopubType);

        int getBaiDuKey();

        int getAdmobWidth();//PX

        int getAdmobHeight();//PX

        int getFbAdsHight(); //PX, banner default is 64

        boolean isBanner();

        boolean shouldLogClickTime();//default is true

        boolean shouldShowActionButton(); // default: true

        int getAdmobType();

        int getMoPubType();

        void onRefreshClicked();

        boolean hideIconViewWhenNone();//hide admob icon viewSelected when no data to show, default is true

        boolean didForceLoadAdFromCache();//default false

        int getAdContainerSpaceX();//返回layout/layout_advertisement父节点的x方向的空隙(px)

        ImageView getSmallIconView();

        void onAdClicked(boolean isAdmob);

        //        void onAdLoaded();
        void onAdLoaded(String adType);

        void onAdShow();

        void onAdError(boolean isLastRequestIndex);//多个广告的请求顺序，是否是最后的请求广告尝试失败

        void adjustAdmobView(FrameLayout layoutAdmob);

        void adjustFbContainerView(LinearLayout layoutFacebook);

        void adjustMoPubContainerView(LinearLayout layoutMoPub);

        void adjustBaiDuContaionerView(LinearLayout layoutBaiDu);

    }

    public class AdviewIDException extends Exception {
        public boolean canTry;

        public AdviewIDException(String e) {
            super(e);
        }
    }

    public void setShowType(boolean isCalllog) {
        mIsCalllog = isCalllog;
    }

    /**
     * 设置是够是结果页
     */
    public void setIsResultPage(boolean isResultPage) {
        mIsResultPage = isResultPage;
    }

    public LinearLayout showListViewFBAD() {
        LinearLayout adView = null;
        try {
            if (mIsClosed) {
                return null;
            }
            if (!mIsLoadFbADSuc) {
                return null;
            }

            mIsLoading = false;
            mIsLoadSuc = true;
            mIsAdMobLoadingOrShow = false;
            if (mFacebookeNativeAdContainer != null /*&& !mContext.isFinishing()*/) {
                mFacebookeNativeAdContainer.setVisibility(View.VISIBLE);
                // Unregister last ad
//                mFacebookNativeAd.unregisterView();

                if (mIsBanner) {
                    adView = inflateBannerAD(mFacebookNativeAd, mLayoutFacebook);
                } else {
                    adView = inflateBigAD(mFacebookNativeAd, mLayoutFacebook);
                }
                mAdapter.adjustFbContainerView(mLayoutFacebook);

                mAdShownTime = System.currentTimeMillis();
                mIsAdShown = true;
                mCurrentDisplayingAdIsAdmob = false;
                LogUtil.d("showListViewAD", "mFacebookeNativeAdContainer height：" + mFacebookeNativeAdContainer.getHeight());
            }
        } catch (Exception e) {
            LogUtil.e("showListViewAD", "showListViewAD e:" + e.getMessage());
        }

        return adView;
    }

    private LinearLayout inflateBannerAD(NativeAd nativeAd, final LinearLayout adView) {
        if (mLayoutAdmob != null) {
            mLayoutAdmob.setVisibility(View.GONE);
        }

        if (mMoPubNativeAdContainer != null) {
            mMoPubNativeAdContainer.setVisibility(View.GONE);
        }

        this.mLayoutFacebook.setVisibility(View.VISIBLE);

        // Create native UI using the ad metadata.
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.nativeAdIcon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.nativeAdTitle);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.nativeAdBody);
        TextView nativeAdCallToAction = (TextView) adView.findViewById(R.id.nativeAdCallToAction);
        FrameLayout nativeAdImage = (FrameLayout) adView.findViewById(R.id.layout_fb_image);

        // Setting the Text
//        nativeAdCallToAction.setBackgroundResource(Math.random() > 0.5 ? R.drawable.ic_share : R.drawable.ico_arrow_right);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        if (mAdapter.shouldShowActionButton()) {
            nativeAdCallToAction.setVisibility(View.VISIBLE);
        } else {
            nativeAdCallToAction.setVisibility(View.GONE);
        }
        nativeAdTitle.setText(nativeAd.getAdTitle());
        nativeAdBody.setText(nativeAd.getAdBody());
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tv = ((TextView) adView.findViewById(R.id.nativeAdBody));
                if (tv != null) {
                    tv.setSelected(true);
                }
            }
        }, DEFAULT_SET_TEXTVIEW_SCROLL);

        ImageView smallIconView = mAdapter.getSmallIconView();
        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, smallIconView != null ? smallIconView : nativeAdIcon);

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable
//        nativeAd.registerViewForInteraction(adView);

        if (mAdChoicesView == null) {
            mAdChoicesView = new AdChoicesView(mContextForFb, nativeAd, true);
            FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(Stringutil.dpToPx(ApplicationEx.getInstance(), 16), Stringutil.dpToPx(ApplicationEx.getInstance(), 16));
            layoutParam.gravity = mIsCalllog ? Gravity.RIGHT | Gravity.BOTTOM : Gravity.LEFT | Gravity.BOTTOM;
            nativeAdImage.addView(mAdChoicesView, layoutParam);
        }

        // Or you can replace the above call with the following function to specify the clickable areas.
        // nativeAd.registerViewForInteraction(adView,
        //     Arrays.asList(nativeAdCallToAction, nativeAdMedia));

//        mFacebookeNativeAdContainer.removeAllViews();
//        mFacebookeNativeAdContainer.addView(adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Stringutil.dpToPx(mContextForFb, 64)));
        return adView;
    }

    private LinearLayout inflateBigAD(NativeAd nativeAd, final LinearLayout adView) {
        LogUtil.d(LOG_TAG, "listview ad inflateAd start.");
        if (mIsClosed) {
            return null;
        }

        if (mLayoutAdmob != null) {
            mLayoutAdmob.setVisibility(View.GONE);
        }

        if (mMoPubNativeAdContainer != null) {
            mMoPubNativeAdContainer.setVisibility(View.GONE);
        }

        mLayoutFacebook.setVisibility(View.VISIBLE);

        // Create native UI using the ad metadata.
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.nativeAdIcon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.nativeAdTitle);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.nativeAdBody);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.nativeAdMedia);
        View nativeAdCallToAction = (View) adView.findViewById(R.id.nativeAdCallToAction);
        FrameLayout nativeAdImage = (FrameLayout) adView.findViewById(R.id.layout_fb_image);
        //ImageView ivGradientSplashLine = (ImageView) adView.findViewById(R.id.ivGradientSplashLine);

//        ArrayList<View> viewList = new ArrayList<>();
//        viewList.add(nativeAdIcon);
//        viewList.add(nativeAdTitle);
//        viewList.add(nativeAdBody);
//        viewList.add(nativeAdMedia);
//        viewList.add(nativeAdCallToAction);
//        viewList.add(nativeAdImage);
//        viewList.add(adView.findViewById(R.id.ll_adview));

        // Setting the Text
        if (mAdapter.shouldShowActionButton()) {
            nativeAdCallToAction.setVisibility(View.VISIBLE);
        } else {
            nativeAdCallToAction.setVisibility(View.GONE);
        }

        if (nativeAdCallToAction instanceof Button) {
            ((Button) nativeAdCallToAction).setText(nativeAd.getAdCallToAction());
        } else if (nativeAdCallToAction instanceof RelativeLayout) {
            ((TextView) adView.findViewById(R.id.tvNativeAdCallToAction)).setText(nativeAd.getAdCallToAction());
        }
        nativeAdTitle.setText(nativeAd.getAdTitle());
        nativeAdBody.setText(nativeAd.getAdBody());
        LogUtil.d("advertisemrnt", " loading ad listview ad title:" + nativeAd.getAdTitle() + ",body:" + nativeAd.getAdBody());
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tv = ((TextView) adView.findViewById(R.id.nativeAdBody));
                if (tv != null) {
                    tv.setSelected(true);
                }
            }
        }, DEFAULT_SET_TEXTVIEW_SCROLL);

        ImageView smallIconView = mAdapter.getSmallIconView();
        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, smallIconView != null ? smallIconView : nativeAdIcon);

        // Downloading and setting the cover image.
        NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
        int bannerWidth = adCoverImage.getWidth();
        int bannerHeight = adCoverImage.getHeight();
        WindowManager wm = (WindowManager) mContextForFb.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        //Ad Root View layout params
        //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mAdRootView.getLayoutParams();
        int paddingLeft = mAdRootView.getPaddingLeft();
        int paddingRight = mAdRootView.getPaddingRight();
        int topParentSpaceX = mAdapter.getAdContainerSpaceX();

        int mediaViewWidth = metrics.widthPixels - paddingLeft - paddingRight - topParentSpaceX;
        int screenHeight = metrics.heightPixels;

        //MediaView has limit height that can't fill it when expanded this value
//        mDefaultMediaViewHeight = Math.min((int) (((double) mediaViewWidth / (double) bannerWidth) * bannerHeight), screenHeight / 3) - Stringutil.dpToPx(mContextForFb, 2);

        int height = Math.min((int) ((mediaViewWidth * 1.0 / bannerWidth) * bannerHeight), screenHeight / 3);
        nativeAdMedia.setLayoutParams(new FrameLayout.LayoutParams(mediaViewWidth, height));
        nativeAdMedia.setNativeAd(nativeAd);
        LogUtil.d(LOG_TAG, "listview ad inflateAd height: " + height);

//        int mediaViewHeight = mDefaultMediaViewHeight;
//        FrameLayout.LayoutParams mediaViewParam = new FrameLayout.LayoutParams(
//                mediaViewWidth,//ViewGroup.LayoutParams.MATCH_PARENT,
//                mediaViewHeight
//        );

//        nativeAdMedia.setLayoutParams(mediaViewParam);
//        if (ivGradientSplashLine != null) {
//            ivGradientSplashLine.setLayoutParams(mediaViewParam);
//        }

        if (nativeAdMedia != null) {
            LogUtil.d(LOG_TAG, "listview ad inflateAd adCoverImage width: " + adCoverImage.getWidth());
        }
        nativeAdMedia.setNativeAd(nativeAd);

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable
        //nativeAd.registerViewForInteraction(adView.findViewById(R.id.ll_adview));
//        nativeAd.registerViewForInteraction(adView, viewList);
//        nativeAd.registerViewForInteraction(adView);

        if (mAdChoicesView == null) {
            mAdChoicesView = new AdChoicesView(mContextForFb, nativeAd, true);
            FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(Stringutil.dpToPx(mContextForFb, 24), Stringutil.dpToPx(mContextForFb, 24));
            layoutParam.gravity = Gravity.RIGHT | Gravity.TOP;
            nativeAdImage.addView(mAdChoicesView, layoutParam);
        }

        LogUtil.d(LOG_TAG, "listview ad inflateAd end.");

//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(metrics.widthPixels - topParentSpaceX, ViewGroup.LayoutParams.WRAP_CONTENT);
////        if (adView.getParent() != null) {
////            ((LinearLayout) adView.getParent()).removeView(adView);
////        }
//        mFacebookeNativeAdContainer.removeAllViews();
//        mFacebookeNativeAdContainer.addView(adView, params);
        LogUtil.d("showlistviewad", "mFacebookeNativeAdContainer height:" + mFacebookeNativeAdContainer.getHeight() + ",adView height:" + adView.getHeight());
        return adView;
    }

    public LinearLayout showListViewBaiduAd() {
        LinearLayout adView = null;
        try {
            if (!mIsClosed && mIsLoadBaiDuSuc) {
                mIsLoading = false;
                mIsLoadSuc = true;
                mIsAdMobLoadingOrShow = false;

                if (mLayoutBaiDu != null) {
                    mBaiDuAdContainer.removeAllViews();
                    adView = mLayoutBaiDu;
                    mAdapter.adjustBaiDuContaionerView(mLayoutBaiDu);

                    mAdShownTime = System.currentTimeMillis();
                    mIsAdShown = true;
                    mCurrentDisplayingAdIsAdmob = false;
                    LogUtil.d("showListViewAD", "mBaiduNativeAdContainer height：" + mBaiDuAdContainer.getHeight());
                }
            }
        } catch (Exception e) {
            LogUtil.e("showListViewAD", "showListViewAD e:" + e.getMessage());
        }
        return adView;
    }

    public LinearLayout showListViewMopubAd() {
        LinearLayout adView = null;
        try {
            if (mIsClosed) {
                return null;
            }
            if (mIsLoadMoPubSuc || mIsLoadMoPubBannerSuc) {


                mIsLoading = false;
                mIsLoadSuc = true;
                mIsAdMobLoadingOrShow = false;
//                if (mMoPubNativeAdContainer != null /*&& !mContext.isFinishing()*/) {
//                    mMoPubNativeAdContainer.setVisibility(View.VISIBLE);
//
//                    adView = infateListViewMoPubNativeAd(mMoPubNativeAd, mLayoutMoPub);
//                    mAdapter.adjustMoPubContainerView(mLayoutMoPub);
//
//                    mAdShownTime = System.currentTimeMillis();
//                    mIsAdShown = true;
//                    mCurrentDisplayingAdIsAdmob = false;
//                    LogUtil.d("showListViewAD", "mMoPubNativeAdContainer height：" + mMoPubNativeAdContainer.getHeight());
//                }
            }
        } catch (Exception e) {
            LogUtil.e("showListViewAD", "showListViewAD e:" + e.getMessage());
        }

        return adView;
    }

    public AdView showListViewAdMobBannerAd() {
        AdView adView = null;
        try {
            if (mIsClosed) return null;

            if (mIsLoadAdmobBannerSuc) {
                mIsLoadSuc = true;
                mIsLoading = false;
                mIsAdMobLoadingOrShow = false;

                if (mLayoutAdmob != null) {
                    adView = mAdmobBannerAdView;

                    mAdapter.adjustAdmobView(mLayoutAdmob);

                    mAdShownTime = System.currentTimeMillis();
                    mIsAdShown = true;
                    mCurrentDisplayingAdIsAdmob = false;
                    LogUtil.d("showListViewAD", "showListViewAdMobBannerAd height：" + mLayoutMoPub.getHeight());
                }
            }

        } catch (Exception e) {

        }
        return adView;
    }

    public MoPubView showListViewMopubBannerAd() {
        MoPubView adView = null;
        try {
            if (mIsClosed) {
                return null;
            }
            if (mIsLoadMoPubBannerSuc) {

                mIsLoading = false;
                mIsLoadSuc = true;
                mIsAdMobLoadingOrShow = false;
                if (mMoPubNativeAdContainer != null /*&& !mContext.isFinishing()*/) {
                    mMoPubNativeAdContainer.setVisibility(View.VISIBLE);

                    adView = mMopubView;

                    mAdapter.adjustMoPubContainerView(mLayoutMoPub);

                    mAdShownTime = System.currentTimeMillis();
                    mIsAdShown = true;
                    mCurrentDisplayingAdIsAdmob = false;
                    LogUtil.d("showListViewAD", "showListViewMopubBannerAd height：" + mMoPubNativeAdContainer.getHeight());
                }
            }
        } catch (Exception e) {
            LogUtil.e("showListViewAD", "showListViewAD e:" + e.getMessage());
        }

        return adView;
    }

    private LinearLayout infateListViewMoPubNativeAd(com.mopub.nativeads.NativeAd nativeAd, LinearLayout adView) {

        return adView;
    }

    public NativeAdView showListViewAdmobAd() {
        NativeAdView adView = null;
        if (mIsClosed) {
            return null;
        }

        if (mNativeAppInstallAd == null && mNativeContentAd != null) {
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-admob advanced - content ad loaded");
            mADId = System.currentTimeMillis();
//            mAdapter.onAdLoaded(AdvertisementSwitcher.AD_ADMOB);
            mIsLoading = false;
            mIsAdShown = true;
            mCurrentDisplayingAdIsAdmob = true;
            mAdShownTime = System.currentTimeMillis();
            mLayoutAdmob.setVisibility(View.VISIBLE);
            adView = inflateContentAd(mNativeContentAd);
            adjustAdmobView(mLayoutAdmob);
            mAdapter.onAdShow();
            return adView;
        } else if (mNativeAppInstallAd != null && mNativeContentAd == null) {
            if (BuildConfig.DEBUG)
                LogUtil.d(LOG_TAG, Advertisement.this.hashCode() + "-admob advanced - app install ad loaded");
            mADId = System.currentTimeMillis();
//            mAdapter.onAdLoaded(AdvertisementSwitcher.AD_ADMOB);
            mIsLoading = false;
            mIsAdShown = true;
            mCurrentDisplayingAdIsAdmob = true;
            mAdShownTime = System.currentTimeMillis();
            //mLayoutAdmob.setVisibility(View.VISIBLE);
            adView = inflateAppInstallAD(mNativeAppInstallAd);
            adjustAdmobView(mLayoutAdmob);
            mAdapter.onAdShow();
        } else {
            return null;
        }
        return adView;
    }

    private NativeAppInstallAdView inflateAppInstallAD(NativeAppInstallAd ad) {
        if (mIsClosed) {
            return null;
        }

        if (mLayoutFacebook != null) {
            mLayoutFacebook.setVisibility(View.GONE);
        }

        if (mMoPubNativeAdContainer != null) {
            mMoPubNativeAdContainer.setVisibility(View.GONE);
        }

        mLayoutAdmob.setVisibility(View.VISIBLE);

        final NativeAppInstallAdView adView = (NativeAppInstallAdView) LayoutInflater.from(mContextForAdmob).inflate(mAdapter.getAdmobViewRes(mAdmobType, true), null);
        {
            TextView tvTitle = (TextView) adView.findViewById(R.id.tv_title);
            TextView tvContent = (TextView) adView.findViewById(R.id.tv_content);
            ImageView ivIcon = (ImageView) adView.findViewById(R.id.iv_icon);
            ImageView ivContent = (ImageView) adView.findViewById(R.id.iv_content);

            //banner ad has no iv_ad_flag
            if (adView.findViewById(R.id.iv_ad_flag) != null) {
                adView.findViewById(R.id.iv_ad_flag).setVisibility(View.VISIBLE);
            }

            adView.setHeadlineView(adView.findViewById(R.id.tv_title));
            adView.setIconView(adView.findViewById(R.id.iv_icon));
            adView.setBodyView(adView.findViewById(R.id.tv_content));
            adView.setImageView(ivContent);
            adView.setCallToActionView(adView.findViewById(R.id.btn_callToAction));

            tvTitle.setText(ad.getHeadline());
            tvContent.setText(ad.getBody());
            LogUtil.d(TAG, "showListViewAdmobAd inflateAppInstallAD tvTitle:" + ad.getHeadline() + ",tvContent:" + ad.getBody());
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TextView tv = ((TextView) adView.findViewById(R.id.tv_content));
                    if (tv != null) {
                        tv.setSelected(true);
                    }
                }
            }, DEFAULT_SET_TEXTVIEW_SCROLL);

            //LionFontManager.setFontType(((TextView) adView.findViewById(R.id.tv_title)));

            List<Image> contentImages = ad.getImages();
            if (contentImages != null && contentImages.size() > 0) {
                ivContent.setImageDrawable(contentImages.get(0).getDrawable());
            }

            Image icon = ad.getIcon();
            if (icon == null) {
                if (mAdapter.hideIconViewWhenNone()) {
                    ivIcon.setVisibility(View.GONE);
                }
            } else {
                ivIcon.setImageDrawable(icon.getDrawable());
                adView.findViewById(R.id.iv_icon).setVisibility(View.VISIBLE);
            }

            ((Button) adView.findViewById(R.id.btn_callToAction)).setText(ad.getCallToAction());
            if (mAdapter.shouldShowActionButton()) {
                adView.findViewById(R.id.btn_callToAction).setVisibility(View.VISIBLE);
            } else {
                adView.findViewById(R.id.btn_callToAction).setVisibility(View.GONE);
            }
            adView.setNativeAd(ad);
        }
        return adView;
    }

    private NativeContentAdView inflateContentAd(NativeContentAd ad) {
        if (mIsClosed) {
            return null;
        }

        if (mLayoutFacebook != null) {
            mLayoutFacebook.setVisibility(View.GONE);
        }

        if (mMoPubNativeAdContainer != null) {
            mMoPubNativeAdContainer.setVisibility(View.GONE);
        }

        mLayoutAdmob.setVisibility(View.VISIBLE);

        final NativeContentAdView adView = (NativeContentAdView) LayoutInflater.from(mContextForAdmob)
                .inflate(mAdapter.getAdmobViewRes(mAdmobType, false), null);
        {
            TextView tvTitle = (TextView) adView.findViewById(R.id.tv_title);
            TextView tvContent = (TextView) adView.findViewById(R.id.tv_content);
            ImageView ivIcon = (ImageView) adView.findViewById(R.id.iv_icon);
            ImageView ivContent = (ImageView) adView.findViewById(R.id.iv_content);

            //banner ad has no iv_ad_flag
            if (adView.findViewById(R.id.iv_ad_flag) != null) {
                adView.findViewById(R.id.iv_ad_flag).setVisibility(View.VISIBLE);
            }

            adView.setHeadlineView(tvTitle);
            adView.setLogoView(ivIcon);
            adView.setBodyView(tvContent);
            adView.setImageView(ivContent);
            adView.setCallToActionView(adView.findViewById(R.id.btn_callToAction));

            tvTitle.setText(ad.getHeadline());
            tvContent.setText(ad.getBody());
            LogUtil.d(TAG, "showListViewAdmobAd inflateContentAd tvTitle:" + ad.getHeadline() + ",tvContent:" + ad.getBody());
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TextView tv = ((TextView) adView.findViewById(R.id.tv_content));
                    if (tv != null) {
                        tv.setSelected(true);
                    }
                }
            }, DEFAULT_SET_TEXTVIEW_SCROLL);
            //LionFontManager.setFontType(((TextView) adView.findViewById(R.id.tv_title)));
            Image logo = ad.getLogo();

            List<Image> contentImages = ad.getImages();
            if (contentImages != null && contentImages.size() > 0) {
                ivContent.setImageDrawable(contentImages.get(0).getDrawable());
            }

            if (logo == null) {
                if (mAdapter.hideIconViewWhenNone()) {
                    ivIcon.setVisibility(View.GONE);
                }
            } else {
                ivIcon.setImageDrawable(logo.getDrawable());
                adView.findViewById(R.id.iv_icon).setVisibility(View.VISIBLE);
            }

            ((Button) adView.findViewById(R.id.btn_callToAction)).setText(ad.getCallToAction());
            if (mAdapter.shouldShowActionButton()) {
                adView.findViewById(R.id.btn_callToAction).setVisibility(View.VISIBLE);
            } else {
                adView.findViewById(R.id.btn_callToAction).setVisibility(View.GONE);
            }

            adView.setNativeAd(ad);
        }
        return adView;
    }

}
