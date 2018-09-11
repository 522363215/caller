package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
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

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.formats.NativeAd.Image;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

public class PreloadAdvertisement {
    public static final int ADMOB_TYPE_NONE = 0;
    public static final int ADMOB_TYPE_NATIVE = 1;
    public static final int ADMOB_TYPE_NATIVE_ADVANCED = 1 << 1;

    public static final int ADMOB_ADX_TYPE_NONE = 0;
    public static final int ADMOB_ADX_TYPE_NATIVE = 1;
    public static final int ADMOB_ADX_TYPE_NATIVE_ADVANCED = 1 << 1;

    public static final String IN_CALL_FLASH_SHOW = "in_call_flash_show";

    public static final int DEFAULT_FB_REFRESH_INTERVAL = 2 * 60 * 1000;
    public static final int DEFAULT_ADMOB_REFRESH_INTERVAL = 2 * 60 * 1000;
    private static final int DEFAULT_SET_TEXTVIEW_SCROLL = 1000;
    private static final long OUT_DATE_TIME = 40 * 60 * 1000;

    private static String TAG = "PreloadAdvertisement";
    private static final long BANNER_AD_PERIOD_SHOW_TIME = 30 * 1000;
    private final String mAdShowPriority;
    private final String mAdId;
    private String mAdType;
    private int mAdmobAdxType = ADMOB_ADX_TYPE_NONE;
    private int mAdmobType = ADMOB_ADX_TYPE_NONE;
    private Context mContext;
    private PreloadAdvertisementAdapter mAdapter;
    private AdTypeInfo mAdTypeInfo;
    private FacebookAdInfo mFbAdInfo;
    private AdmobAdInfo mAdmobAdInfo;
    private AdmobAdxAdInfo mAdmobAdxInfo;
    private boolean mIsLoadSuccess;
    private boolean mIsClosed;
    private boolean mRefreshWhenClicked = true;

    /*facebook*/
    private LinearLayout mFacebookeNativeAdContainer;
    private LinearLayout mLayoutFacebook;
    private View mFbAdRootView;//广告布局文件的最顶层View，统一使用@+id/layout_ad_view_root,该View用来控制广告整体的Margin和Padding
    private AdChoicesView mAdChoicesView;

    private Handler mMainHandler;
    private int mDefaultMediaViewHeight;//px, valid after onAdShow called
    private boolean mIsLoading;
    private boolean mIsBanner;
    private FrameLayout mLayoutAdmob;
    private FrameLayout mLayoutAdmobAdx;
    private boolean mIsInitAdmob;
    private boolean mIsInitAdmobAdx;
    private boolean mIsInitView;
    private boolean isOnlyBtnClickable = false;
    private boolean mIsOnlyBtnAndTextClickable = false;
    private boolean isFullClickable = false;
    private boolean mIsAdaptiveSize;


    public PreloadAdvertisement(String tag, @NonNull PreloadAdvertisementAdapter adapter) {
        TAG = tag;
        mContext = ApplicationEx.getInstance();
        mAdapter = adapter;
        mMainHandler = new Handler(Looper.getMainLooper());
        mIsBanner = mAdapter.isBanner();
        mAdId = mAdapter.getAdId();
        mAdShowPriority = mAdapter.getAdShowPriority();
        mAdmobType = mAdapter.getAdTypeInfo() != null ? mAdapter.getAdTypeInfo().admobType : ADMOB_TYPE_NONE;
        mAdmobAdxType = mAdapter.getAdTypeInfo() != null ? mAdapter.getAdTypeInfo().admobAdxType : ADMOB_TYPE_NONE;
        isOnlyBtnClickable = false;
        mIsOnlyBtnAndTextClickable = false;
        isFullClickable = false;
        init();
    }

    private void init() {
        mAdTypeInfo = mAdapter.getAdTypeInfo();
        if (mAdTypeInfo != null) {
            mAdType = mAdTypeInfo.adType;
            if (!TextUtils.isEmpty(mAdType)) {
                if (mAdType.equals(AdvertisementSwitcher.AD_FACEBOOK)) {
                    mFbAdInfo = new FacebookAdInfo();
                } else if (mAdType.equals(AdvertisementSwitcher.AD_FACEBOOK)) {
                    mAdmobAdInfo = new AdmobAdInfo();
                    mAdmobType = mAdTypeInfo.admobType;
                } else if (mAdType.equals(AdvertisementSwitcher.AD_FACEBOOK)) {
                    mAdmobAdxInfo = new AdmobAdxAdInfo();
                    mAdmobAdxType = mAdTypeInfo.admobAdxType;
                }
            }
        }
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

    //default true
    public void setRefreshWhenClicked(boolean refresh) {
        mRefreshWhenClicked = refresh;
    }

    public void close() {
        mAdapter = null;
        if (mFacebookeNativeAdContainer != null) {
            mFacebookeNativeAdContainer.removeAllViews();
            mFacebookeNativeAdContainer = null;
        }

        mAdapter = null;
        mAdType = null;
        mFbAdInfo = null;
        mAdmobAdInfo = null;
        mAdmobAdxInfo = null;

        if (mFacebookeNativeAdContainer != null) {
            mFacebookeNativeAdContainer.removeAllViews();
            mFacebookeNativeAdContainer = null;
        }


        mLayoutFacebook = null;
        mAdChoicesView = null;

        if (mLayoutAdmob != null) {
            mLayoutAdmob.removeAllViews();
            mLayoutAdmob = null;
        }


        mContext = null;

        mLayoutFacebook = null;
        mAdChoicesView = null;

        mIsClosed = true;


    }

    public PreloadAdvertisementAdapter getAdapter() {
        return mAdapter;
    }

    public String getAdShowPriority() {
        return mAdShowPriority;
    }

    public String getAdId() {
        return mAdId;
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public void isAdaptiveSize(boolean isAdaptiveSize) {
        mIsAdaptiveSize = isAdaptiveSize;
    }

    /**
     * @param isJudgeShown 是否判断已经显示，在轮播显示界面就不用判断是否显示，其他加载界面需要判断是否显示
     */
    public boolean isValid(boolean isJudgeShown) {
        if (mAdType.equals(AdvertisementSwitcher.AD_FACEBOOK)) {
            if (mFbAdInfo != null) {
                boolean isOutDate = (System.currentTimeMillis() - mFbAdInfo.loadSuccessTime) > OUT_DATE_TIME;
                if (isOutDate || (isJudgeShown && mFbAdInfo.isShow)) {
                    return false;
                } else {
                    if (isJudgeShown) {
                        LogUtil.d(TAG, "last loaded fb is valid,do not reload fb adId:" + mAdId + ",adShowPriority:" + mAdShowPriority);
                    }
                    return true;
                }
            }
        } else if (mAdType.equals(AdvertisementSwitcher.AD_ADMOB)) {
            if (mAdmobAdInfo != null) {
                boolean isOutDate = (System.currentTimeMillis() - mAdmobAdInfo.loadSuccessTime) > OUT_DATE_TIME;
                if (isOutDate || (isJudgeShown && mAdmobAdInfo.isShow)) {
                    mIsInitAdmob = false;
                    return false;
                } else {
                    if (isJudgeShown) {
                        LogUtil.d(TAG, "last loaded admob ad is valid,do not reload admob adId:" + mAdId + ",adShowPriority:" + mAdShowPriority);
                    }
                    return true;
                }
            }
        } else if (mAdType.equals(AdvertisementSwitcher.AD_ADMOB_ADX)) {
            if (mAdmobAdxInfo != null) {
                boolean isOutDate = (System.currentTimeMillis() - mAdmobAdxInfo.loadSuccessTime) > OUT_DATE_TIME;
                if (isOutDate || (isJudgeShown && mAdmobAdxInfo.isShow)) {
                    mIsInitAdmobAdx = false;
                    return false;
                } else {
                    if (isJudgeShown) {
                        LogUtil.d(TAG, "last loaded admob adx ad is valid,do not reload admob adx adId:" + mAdId + ",adShowPriority:" + mAdShowPriority);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean loadAd() {
        if (!canRefreshAd()) {
            if (BuildConfig.DEBUG)
                LogUtil.d(TAG, "not reach refresh time or ad disabled");
            return false;
        }

        if (!isValid(true) && !mIsLoading) {
            if (mAdType.equals(AdvertisementSwitcher.AD_FACEBOOK)) {
                if (mIsBanner) {
                    loadFacebookBannerAd();
                } else {
                    loadFacebookAd();
                }
            } else if (mAdType.equals(AdvertisementSwitcher.AD_ADMOB)) {
                initAdmob();
                loadAdmob();
            } else if (mAdType.equals(AdvertisementSwitcher.AD_ADMOB_ADX)) {
                initAdmobAdx();
                loadAdmobAdx();
            }
            return true;
        }
        return false;
    }

    public boolean showAd(final View adView) {
        if (isValid(false)) {
            initView(adView);
            if (mAdType.equals(AdvertisementSwitcher.AD_FACEBOOK)) {
                showFb(adView);
            } else if (mAdType.equals(AdvertisementSwitcher.AD_ADMOB)) {
                showAdmob(adView);
            } else if (mAdType.equals(AdvertisementSwitcher.AD_ADMOB_ADX)) {
                showAdmobAdx(adView);
            }
        } else {
            loadAd();
            return true;
        }
        return false;
    }

    private void initView(View adView) {
        if (mIsInitView) return;
        initFbView(adView);
        mLayoutAdmob = (FrameLayout) adView.findViewById(R.id.layout_admob);
        mLayoutAdmobAdx = (FrameLayout) adView.findViewById(R.id.layout_admob);
        mIsInitView = true;
    }

    /*--- 广告初始化入口 begin ---*/
    private boolean canRefreshAd() {
        if (mIsClosed || !AdvertisementSwitcher.getInstance().isAdEnabled(mAdapter.getPlacementId())) {
            return false;
        }
        return true;
    }

    //初始化Facebook广告视图
    private void initFbView(View adView) {
        try {
            mFacebookeNativeAdContainer = (LinearLayout) adView.findViewById(R.id.nativeAdContainer);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mLayoutFacebook = (LinearLayout) inflater.inflate(mAdapter.getFbViewRes(), null);
            mFbAdRootView = mLayoutFacebook.findViewById(R.id.layout_ad_view_root);

            if (mFbAdRootView == null) {
                throw new AdviewIDException("must be declare layout_ad_view_root in ad xml");
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "initFacebook error: " + e.getMessage());
            LogUtil.error(e);
        }
    }

    private boolean loadFacebookAd() {
        if (mIsClosed) {
            return false;
        }
        if (TextUtils.isEmpty(mAdId)) {
            onAdLoadFailed();
            return false;
        }
        boolean suc = false;
        try {
            NativeAd facebookNativeAd = new NativeAd(mContext, mAdId);
            // Set a listener to get notified when the ad was loaded.
            NativeFBListener nativeFBListener = new NativeFBListener();
            facebookNativeAd.setAdListener(nativeFBListener);
//            facebookNativeAd.setImpressionListener(new FBImpressionListener());
            facebookNativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
            if (mFbAdInfo != null) {
                mFbAdInfo.facebookNativeAd = facebookNativeAd;
                mFbAdInfo.isShow = false;
            }
            mIsLoading = true;
            suc = true;
        } catch (Exception e) {
            onAdLoadFailed();
            if (BuildConfig.DEBUG) {
                LogUtil.e(TAG, "loadFBFromNetwork error: " + e.getMessage());
                LogUtil.error(e);
            }
        } finally {
            return suc;
        }
    }

    private boolean loadFacebookBannerAd() {
        if (mIsClosed) {
            return false;
        }
        if (TextUtils.isEmpty(mAdId)) {
            onAdLoadFailed();
            return false;
        }
        boolean suc = false;
        try {
            NativeBannerAd facebookNativeBannerAd = new NativeBannerAd(mContext, mAdId);
            // Set a listener to get notified when the ad was loaded.
            NativeFBListener nativeFBListener = new NativeFBListener();
            facebookNativeBannerAd.setAdListener(nativeFBListener);
//            facebookNativeAd.setImpressionListener(new FBImpressionListener());
            facebookNativeBannerAd.loadAd(NativeBannerAd.MediaCacheFlag.ALL);
            if (mFbAdInfo != null) {
                mFbAdInfo.facebookNativeBannerAd = facebookNativeBannerAd;
                mFbAdInfo.isShow = false;
            }
            mIsLoading = true;
            suc = true;
        } catch (Exception e) {
            onAdLoadFailed();
            if (BuildConfig.DEBUG) {
                LogUtil.e(TAG, "loadFBFromNetwork error: " + e.getMessage());
                LogUtil.error(e);
            }
        } finally {
            return suc;
        }
    }

    private void initAdmob() {
        if (mIsClosed || mAdapter == null) {
            return;
        }

        if (mIsInitAdmob) {
            return;
        }

        if (TextUtils.isEmpty(mAdId)) {
            onAdLoadFailed();
            return;
        }

        switch (mAdmobType) {
            case ADMOB_TYPE_NATIVE:
                initAdmobForNativeType();
                break;
            case ADMOB_TYPE_NATIVE_ADVANCED:
                initAdmobForNativeAdvancedType();
                break;
        }
    }

    private void initAdmobAdx() {
        if (mIsClosed || mAdapter == null) {
            return;
        }
        if (mIsInitAdmobAdx) {
            return;
        }

        if (TextUtils.isEmpty(mAdId)) {
            onAdLoadFailed();
            return;
        }
//        LogUtil.d(TAG, "initAdmobAdx mAdmobAdxType: " + mAdmobAdxType);
        switch (mAdmobAdxType) {
            case ADMOB_ADX_TYPE_NATIVE:
                initAdmobAdxForNativeType();
                break;
            case ADMOB_ADX_TYPE_NATIVE_ADVANCED:
                initAdmobAdxForNativeAdvancedType();
                break;
        }
    }

    /**
     * 初始化Admob快速原生广告
     */
    private void initAdmobForNativeType() {
        AdView admobView = new AdView(mContext);
        int width = 0;
        int height = 0;

        width = Stringutil.pxToDp(mContext, mAdapter.getAdWidth(mAdType));
        height = Stringutil.pxToDp(mContext, mAdapter.getAdHeight(mAdType));

        AdSize nativeAdSize = new AdSize(width, height);
        admobView.setAdSize(nativeAdSize);
        admobView.setAdUnitId(mAdId);
        NativeAdmobAdListener nativeAdmobAdListener = new NativeAdmobAdListener();
        admobView.setAdListener(nativeAdmobAdListener);
        nativeAdmobAdListener.setAdmobView(admobView);

        if (mAdmobAdInfo == null) {
            mAdmobAdInfo = new AdmobAdInfo();
        }
        mAdmobAdInfo.admobType = ADMOB_TYPE_NATIVE;
        mAdmobAdInfo.admobNativeView = admobView;

        mIsInitAdmob = true;
    }

    /**
     * 初始化AdmobAdx快速原生广告
     */
    private void initAdmobAdxForNativeType() {
        AdView admobAdxView = new AdView(mContext);
        int width = 0;
        int height = 0;

        width = Stringutil.pxToDp(mContext, mAdapter.getAdWidth(mAdType));
        height = Stringutil.pxToDp(mContext, mAdapter.getAdHeight(mAdType));

        AdSize nativeAdSize = new AdSize(width, height);
        admobAdxView.setAdSize(nativeAdSize);
        admobAdxView.setAdUnitId(mAdId);
        NativeAdmobAdxAdListener nativeAdmobAdxAdListener = new NativeAdmobAdxAdListener();
        admobAdxView.setAdListener(nativeAdmobAdxAdListener);
        nativeAdmobAdxAdListener.setAdmobAdxView(admobAdxView);

        if (mAdmobAdxInfo == null) {
            mAdmobAdxInfo = new AdmobAdxAdInfo();
        }
        mAdmobAdxInfo.admobAdxType = ADMOB_ADX_TYPE_NATIVE;
        mAdmobAdxInfo.admobAdxNativeView = admobAdxView;

        mIsInitAdmobAdx = true;
    }

    /**
     * 初始化admob高级原生广告
     */
    private void initAdmobForNativeAdvancedType() {
        try {
            AdLoader.Builder builder = new AdLoader.Builder(mContext, mAdId);
            builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                @Override
                public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                    if (mIsClosed) {
                        return;
                    }
                    if (ad == null) {
                        onAdLoadFailed();
                    } else {
                        if (mAdmobAdInfo == null) {
                            mAdmobAdInfo = new AdmobAdInfo();
                        }
                        mAdmobAdInfo.admobType = ADMOB_TYPE_NATIVE_ADVANCED;
                        mAdmobAdInfo.isAppInstall = true;
                        mAdmobAdInfo.nativeAdvanceAppInstallAd = ad;
                        mAdmobAdInfo.loadSuccessTime = System.currentTimeMillis();

                        if (BuildConfig.DEBUG)
                            LogUtil.d(TAG, "loadAdmob advanced - app install ad loaded ,mAdType:" + mAdType + " ,adShowPriority:" + mAdShowPriority + ",adId:" + mAdId);
                        mIsLoading = false;
                        mAdapter.onAdLoaded(PreloadAdvertisement.this);
                    }
                }
            });

            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                @Override
                public void onContentAdLoaded(NativeContentAd ad) {
                    if (mIsClosed) {
                        return;
                    }

                    if (ad == null) {
                        onAdLoadFailed();
                    } else {
                        if (mAdmobAdInfo == null) {
                            mAdmobAdInfo = new AdmobAdInfo();
                        }
                        mAdmobAdInfo.admobType = ADMOB_TYPE_NATIVE_ADVANCED;
                        mAdmobAdInfo.isAppInstall = false;
                        mAdmobAdInfo.nativeAdvanceContentAd = ad;
                        mAdmobAdInfo.loadSuccessTime = System.currentTimeMillis();

                        if (BuildConfig.DEBUG)
                            LogUtil.d(TAG, "loadAdmob advanced - content ad loaded ,mAdType:" + mAdType + " ,adShowPriority:" + mAdShowPriority + ",adid:" + mAdId);
                        mIsLoading = false;
                        mAdapter.onAdLoaded(PreloadAdvertisement.this);
                    }
                }
            });

            AdLoader admobNativeAdvancedLoader = builder.withAdListener(new com.google.android.gms.ads.AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    if (mIsClosed) {
                        return;
                    }
                    if (BuildConfig.DEBUG)
                        LogUtil.d("loadAdmob advanced", "loadAdmob advanced load fail errorCode: " + errorCode);
                    onAdLoadFailed();
                }

                @Override
                public void onAdOpened() {
                    if (mIsClosed) {
                        return;
                    }
                    onAdClick();
                    if (BuildConfig.DEBUG)
                        LogUtil.d(TAG, PreloadAdvertisement.this.hashCode() + "loadAdmob native advanced ad clicked ,mAdType:" + mAdType + " ,adShowPriority:" + mAdShowPriority + ",adid:" + mAdId);
                }

                @Override
                public void onAdLoaded() {
                    if (mIsClosed) {
                        return;
                    }
                    mIsLoading = false;
                }
            }).build();
            if (mAdmobAdInfo == null) {
                mAdmobAdInfo = new AdmobAdInfo();
            }
            mAdmobAdInfo.admobType = ADMOB_TYPE_NATIVE_ADVANCED;
            mAdmobAdInfo.admobNativeAdvancedLoader = admobNativeAdvancedLoader;

            mIsInitAdmob = true;
        } catch (Throwable e) {
//            if (BuildConfig.DEBUG) LogUtil.error(e);
        }
    }

    /**
     * 初始化admob adx高级原生广告
     */
    private void initAdmobAdxForNativeAdvancedType() {
        try {
            AdLoader.Builder builder = new AdLoader.Builder(mContext, mAdId);
            builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                @Override
                public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                    if (mIsClosed) {
                        return;
                    }
                    if (ad == null) {
                        onAdLoadFailed();
                    } else {
                        if (mAdmobAdxInfo == null) {
                            mAdmobAdxInfo = new AdmobAdxAdInfo();
                        }
                        mAdmobAdxInfo.admobAdxType = ADMOB_ADX_TYPE_NATIVE_ADVANCED;
                        mAdmobAdxInfo.isAppInstall = true;
                        mAdmobAdxInfo.nativeAdvanceAppInstallAd = ad;
                        mAdmobAdxInfo.loadSuccessTime = System.currentTimeMillis();

                        if (BuildConfig.DEBUG)
                            LogUtil.d(TAG, "loadAdmobAdx advanced - app install ad loaded adType:" + mAdType + ", adShowPriority:" + mAdShowPriority + ",adid:" + mAdId);
                        mIsLoading = false;
                        mAdapter.onAdLoaded(PreloadAdvertisement.this);
                    }
                }
            });

            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                @Override
                public void onContentAdLoaded(NativeContentAd ad) {
                    if (mIsClosed) {
                        return;
                    }

                    if (ad == null) {
                        onAdLoadFailed();
                    } else {
                        if (mAdmobAdxInfo == null) {
                            mAdmobAdxInfo = new AdmobAdxAdInfo();
                        }
                        mAdmobAdxInfo.admobAdxType = ADMOB_ADX_TYPE_NATIVE_ADVANCED;
                        mAdmobAdxInfo.isAppInstall = false;
                        mAdmobAdxInfo.nativeAdvanceContentAd = ad;
                        mAdmobAdxInfo.loadSuccessTime = System.currentTimeMillis();

                        if (BuildConfig.DEBUG)
                            LogUtil.d(TAG, "loadAdmobAdx advanced - content ad loaded adType :" + mAdType + ",adShowPriority:" + mAdShowPriority + ",adid:" + mAdId);
                        mIsLoading = false;
                        mAdapter.onAdLoaded(PreloadAdvertisement.this);
                    }
                }
            });

            AdLoader admobAdxNativeAdvancedLoader = builder.withAdListener(new com.google.android.gms.ads.AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    if (mIsClosed) {
                        return;
                    }
                    if (BuildConfig.DEBUG)
                        LogUtil.d("loadAdmobAdx advanced", "loadAdmobAdx advanced load fail errorCode : " + errorCode);
                    onAdLoadFailed();
                }

                @Override
                public void onAdOpened() {
                    if (mIsClosed) {
                        return;
                    }
                    onAdClick();
                    if (BuildConfig.DEBUG)
                        LogUtil.d(TAG, PreloadAdvertisement.this.hashCode() + "loadAdmobAdx native advanced ad clicked adType :" + mAdType + ",adShowPriority:" + mAdShowPriority + ",adid:" + mAdId);
                }

                @Override
                public void onAdLoaded() {
                    if (mIsClosed) {
                        return;
                    }
                    mIsLoading = false;
                }
            }).build();
            if (mAdmobAdxInfo == null) {
                mAdmobAdxInfo = new AdmobAdxAdInfo();
            }
            mAdmobAdxInfo.admobAdxType = ADMOB_ADX_TYPE_NATIVE_ADVANCED;
            mAdmobAdxInfo.admobAdxNativeAdvancedLoader = admobAdxNativeAdvancedLoader;

            mIsInitAdmobAdx = true;
        } catch (Throwable e) {
            LogUtil.d(TAG, "initAdmobAdxForNativeAdvancedType e:" + e.getMessage());
//            if (BuildConfig.DEBUG) LogUtil.error(e);
        }
    }

    /**
     * 请求Admob广告
     */
    private boolean loadAdmob() {
        //admob init may throw exception, so we check init status
        if (mIsClosed || TextUtils.isEmpty(mAdId) || !mIsInitAdmob) {
            if (TextUtils.isEmpty(mAdId)) {
                onAdLoadFailed();
            }
            return false;
        }

        boolean suc = false;
        switch (mAdmobType) {
            case ADMOB_TYPE_NATIVE:
                try {
                    //admob sdk may be crashed
                    AdView admobNativeView = mAdmobAdInfo != null ? mAdmobAdInfo.admobNativeView : null;
                    if (admobNativeView != null) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        admobNativeView.loadAd(adRequest);
                        mIsLoading = true;
                        suc = true;
                    } else {
                        onAdLoadFailed();
                    }
                } catch (Exception e) {
                    onAdLoadFailed();
                }
                break;
            case ADMOB_TYPE_NATIVE_ADVANCED:
                AdLoader admobNativeAdvancedLoader = mAdmobAdInfo != null ? mAdmobAdInfo.admobNativeAdvancedLoader : null;
                if (admobNativeAdvancedLoader != null) {
                    admobNativeAdvancedLoader.loadAd(new AdRequest.Builder().build());
                    mIsLoading = true;
                    suc = true;
                } else {
                    onAdLoadFailed();
                }
                break;
        }
        if (mAdmobAdInfo != null) mAdmobAdInfo.isShow = false;
        return suc;
    }

    private boolean loadAdmobAdx() {
        //admob Adx init may throw exception, so we check init status
        if (mIsClosed || TextUtils.isEmpty(mAdId) || !mIsInitAdmobAdx) {
            if (TextUtils.isEmpty(mAdId)) {
                onAdLoadFailed();
            }
            return false;
        }

        boolean suc = false;
        switch (mAdmobAdxType) {
            case ADMOB_ADX_TYPE_NATIVE:
                try {
                    //admob Adx sdk may be crashed
                    AdView admobAdxNativeView = mAdmobAdxInfo != null ? mAdmobAdxInfo.admobAdxNativeView : null;
                    if (admobAdxNativeView != null) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        admobAdxNativeView.loadAd(adRequest);
                        mIsLoading = true;
                        suc = true;
                    } else {
                        onAdLoadFailed();
                    }
                } catch (Exception e) {
                    onAdLoadFailed();
                }
                break;
            case ADMOB_ADX_TYPE_NATIVE_ADVANCED:
                AdLoader admobAdxNativeAdvancedLoader = mAdmobAdxInfo != null ? mAdmobAdxInfo.admobAdxNativeAdvancedLoader : null;
                if (admobAdxNativeAdvancedLoader != null) {
                    admobAdxNativeAdvancedLoader.loadAd(new AdRequest.Builder().build());
                    mIsLoading = true;
                    suc = true;
                } else {
                    onAdLoadFailed();
                }
                break;
        }
        if (mAdmobAdxInfo != null) mAdmobAdxInfo.isShow = false;
        return suc;
    }

    private void onAdClick() {
        if (mAdapter != null) {
            mAdapter.onAdClicked(mAdType, mAdShowPriority);
        }
        if (mRefreshWhenClicked) {

        }
    }

    /**
     * 显示Admob高级原生广告布局-App安装类型
     */
    private boolean inflateAdmobAppInstallView(AdmobAdInfo admobAdInfo, FrameLayout layoutAdmob) {
        if (mIsClosed || mAdapter == null || layoutAdmob == null || admobAdInfo == null) {
            LogUtil.d(TAG, "showAdmobAppInstallAd wrong mIsClosed:" + mIsClosed + ",mAdapter:" + mAdapter + ",layoutAdmob:" + layoutAdmob + ",admobAdInfo:" + admobAdInfo);
            return false;
        }
        layoutAdmob.setVisibility(View.VISIBLE);
        int admobType = admobAdInfo.admobType;
        NativeAppInstallAd ad = admobAdInfo.nativeAdvanceAppInstallAd;
        if (ad == null) return false;
        final NativeAppInstallAdView adView = (NativeAppInstallAdView) LayoutInflater.from(mContext).inflate(mAdapter.getAdmobViewRes(admobType, true), null);
        if (adView == null) return false;
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
            LogUtil.d(TAG, "showAdmobAppInstallAd title:" + ad.getHeadline() + ",body:" + ad.getBody());
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

        layoutAdmob.removeAllViews();
        layoutAdmob.addView(adView);
        return true;
    }

    /**
     * 显示Admob adx高级原生广告布局-App安装类型
     */
    private boolean inflateAdmobAdxAppInstallView(AdmobAdxAdInfo admobAdxAdInfo, FrameLayout
            layoutAdmobAdx) {
        if (mIsClosed || mAdapter == null || layoutAdmobAdx == null || admobAdxAdInfo == null) {
            LogUtil.d(TAG, "showAdmobAdxAppInstallAd wrong mIsClosed:" + mIsClosed + ",mAdapter:" + mAdapter + ",layoutAdmobAdx:" + layoutAdmobAdx + ",admobAdxAdInfo:" + admobAdxAdInfo);
            return false;
        }
        layoutAdmobAdx.setVisibility(View.VISIBLE);
        int admobAdxType = admobAdxAdInfo.admobAdxType;
        NativeAppInstallAd ad = admobAdxAdInfo.nativeAdvanceAppInstallAd;
        if (ad == null) return false;
        final NativeAppInstallAdView adView = (NativeAppInstallAdView) LayoutInflater.from(mContext).inflate(mAdapter.getAdmobAdxViewRes(admobAdxType, true), null);
        if (adView == null) return false;
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
            LogUtil.d(TAG, "showAdmobAdxAppInstallAd title:" + ad.getHeadline() + ",body:" + ad.getBody());
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

        layoutAdmobAdx.removeAllViews();
        layoutAdmobAdx.addView(adView);
        return true;
    }

    /**
     * 显示Admob高级原生广告布局-内容类型
     */
    private boolean inflateAdmobContentAdView(AdmobAdInfo admobAdInfo, FrameLayout layoutAdmob) {
        if (mIsClosed || mAdapter == null || layoutAdmob == null || admobAdInfo == null) {
            LogUtil.d(TAG, "showAdmobContentAd wrong mIsClosed:" + mIsClosed + ",mAdapter:" + mAdapter + ",layoutAdmob:" + layoutAdmob + ",admobAdInfo:" + admobAdInfo);
            return false;
        }
        int admobType = admobAdInfo.admobType;
        NativeContentAd ad = admobAdInfo.nativeAdvanceContentAd;
        if (ad == null) return false;
        layoutAdmob.setVisibility(View.VISIBLE);

        final NativeContentAdView adView = (NativeContentAdView) LayoutInflater.from(mContext).inflate(mAdapter.getAdmobViewRes(admobType, false), null);
        if (adView == null) return false;
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
            LogUtil.d(TAG, "showAdmobContentAd title:" + ad.getHeadline() + ",body:" + ad.getBody());
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

        layoutAdmob.removeAllViews();
        layoutAdmob.addView(adView);
        return true;
    }

    /**
     * 显示Admob adx高级原生广告布局-内容类型
     */
    private boolean inflateAdmobAdxContentAdView(AdmobAdxAdInfo admobAdxAdInfo, FrameLayout
            layoutAdmobAdx) {
        if (mIsClosed || mAdapter == null || layoutAdmobAdx == null || admobAdxAdInfo == null) {
            LogUtil.d(TAG, "showAdmobAdxContentAd wrong mIsClosed:" + mIsClosed + ",mAdapter:" + mAdapter + ",layoutAdmobAdx:" + layoutAdmobAdx + ",admobAdxAdInfo:" + admobAdxAdInfo);
            return false;
        }
        int admobAdxType = admobAdxAdInfo.admobAdxType;
        NativeContentAd ad = admobAdxAdInfo.nativeAdvanceContentAd;
        if (ad == null) return false;
        layoutAdmobAdx.setVisibility(View.VISIBLE);

        final NativeContentAdView adView = (NativeContentAdView) LayoutInflater.from(mContext).inflate(mAdapter.getAdmobAdxViewRes(admobAdxType, false), null);
        if (adView == null) return false;
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
            LogUtil.d(TAG, "showAdmobAdxContentAd title:" + ad.getHeadline() + ",body:" + ad.getBody());
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

        layoutAdmobAdx.removeAllViews();
        layoutAdmobAdx.addView(adView);
        return true;
    }

    /**
     * Admob大图广告布局-调整
     */
    private void adjustAdmobView(FrameLayout layoutAdmob) {
        try {
            mAdapter.adjustAdmobView(layoutAdmob);
        } catch (Exception e) {
            LogUtil.e(TAG, "adjustAdmobView error: " + e.getMessage());
            LogUtil.error(e);
        }
    }

    /**
     * Admob adx大图广告布局-调整
     */
    private void adjustAdmobAdxView(FrameLayout layoutAdmobAdx) {
        try {
            mAdapter.adjustAdmobAdxView(layoutAdmobAdx);
        } catch (Exception e) {
            LogUtil.e(TAG, "adjustAdmobAdxView error: " + e.getMessage());
            LogUtil.error(e);
        }
    }

    /**
     * 创建Facebook的banner广告
     */
    private boolean inflateBannerAd(NativeBannerAd nativeAd, final View adView) {
        if (mIsClosed) {
            return false;
        }
        if (mLayoutAdmob != null) {
            mLayoutAdmob.setVisibility(View.GONE);
        }
        if (mLayoutAdmobAdx != null) {
            mLayoutAdmobAdx.setVisibility(View.GONE);
        }

        mLayoutFacebook.setVisibility(View.VISIBLE);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = (AdIconView) adView.findViewById(R.id.nativeAdIcon);
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
        nativeAdTitle.setText(nativeAd.getAdHeadline());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tv = ((TextView) adView.findViewById(R.id.nativeAdBody));
                if (tv != null) {
                    tv.setSelected(true);
                }
            }
        }, DEFAULT_SET_TEXTVIEW_SCROLL);

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable
        ArrayList<View> viewList = new ArrayList<>();
//        if (isOnlyBtnClickable) {
//
//        }
//

        if (isOnlyBtnClickable) {
            if (nativeAdCallToAction != null) {
                viewList.add(nativeAdCallToAction);
            }
            nativeAd.registerViewForInteraction(adView, nativeAdIcon, viewList);
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
            nativeAd.registerViewForInteraction(adView, nativeAdIcon, viewList); //只有按钮和图片文字可点
        } else {
            nativeAd.registerViewForInteraction(adView, nativeAdIcon);
        }

        if (mAdChoicesView == null) {
            mAdChoicesView = new AdChoicesView(mContext, nativeAd, true);
            FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(Stringutil.dpToPx(ApplicationEx.getInstance(), 16), Stringutil.dpToPx(ApplicationEx.getInstance(), 16));
            layoutParam.gravity = Gravity.LEFT | Gravity.BOTTOM | Gravity.START;
            nativeAdImage.addView(mAdChoicesView, layoutParam);
        }

        // Or you can replace the above call with the following function to specify the clickable areas.
        // nativeAd.registerViewForInteraction(adView,
        //     Arrays.asList(nativeAdCallToAction, nativeAdMedia));

        mFacebookeNativeAdContainer.removeAllViews();
        mFacebookeNativeAdContainer.addView(adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Stringutil.dpToPx(mContext, mAdapter.getAdHeight(mAdType))));//64 is default height
        return true;
    }

    /**
     * 创建Facebook的通用大图广告
     */
    private boolean inflateAd(NativeAd nativeAd, final View adView) {
        if (mIsClosed) {
            return false;
        }
        if (mLayoutAdmob != null) {
            mLayoutAdmob.setVisibility(View.GONE);
        }
        if (mLayoutAdmobAdx != null) {
            mLayoutAdmobAdx.setVisibility(View.GONE);
        }
        mLayoutFacebook.setVisibility(View.VISIBLE);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = (AdIconView) adView.findViewById(R.id.nativeAdIcon);
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
        nativeAdTitle.setText(nativeAd.getAdHeadline());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tv = ((TextView) adView.findViewById(R.id.nativeAdBody));
                if (tv != null) {
                    tv.setSelected(true);
                }
            }
        }, DEFAULT_SET_TEXTVIEW_SCROLL);

        // Downloading and setting the cover image.
        NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
        int bannerWidth = adCoverImage.getWidth();
        int bannerHeight = adCoverImage.getHeight();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        //Ad Root View layout params
        //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mAdRootView.getLayoutParams();
        int paddingLeft = mFbAdRootView.getPaddingLeft();
        int paddingRight = mFbAdRootView.getPaddingRight();
        int topParentSpaceX = mAdapter.getAdContainerSpaceX();

        int mediaViewWidth = metrics.widthPixels - paddingLeft - paddingRight - topParentSpaceX;
        int screenHeight = metrics.heightPixels;

        //MediaView has limit height that can't fill it when expanded this value
//        mDefaultMediaViewHeight = Math.min((int) (((double) mediaViewWidth / (double) bannerWidth) * bannerHeight), screenHeight / 3) - Stringutil.dpToPx(mContextForFb, 2);

        if (!mIsAdaptiveSize) {
            int height = Math.min((int) ((mediaViewWidth * 1.0 / bannerWidth) * bannerHeight), screenHeight / 3);
            nativeAdMedia.setLayoutParams(new FrameLayout.LayoutParams(mediaViewWidth, height));
        }

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
            LogUtil.d(TAG, "inflateAd adCoverImage width: " + adCoverImage.getWidth());
        }

        // Wire up the View with the native ad, the whole nativeAdContainer will be clickable
        if (isFullClickable) {
            nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon); //空白区域可点
        } else {
            nativeAd.registerViewForInteraction(adView, nativeAdMedia, nativeAdIcon, viewList); //只有按钮和图片文字可点
        }

        if (mAdChoicesView == null) {
            mAdChoicesView = new AdChoicesView(mContext, nativeAd, true);
            FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(Stringutil.dpToPx(mContext, 24), Stringutil.dpToPx(mContext, 24));
            layoutParam.gravity = Gravity.RIGHT | Gravity.TOP;
            nativeAdImage.addView(mAdChoicesView, layoutParam);
        }

        LogUtil.d(TAG, "inflateAd big common end.");
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (!mIsAdaptiveSize) {
            width = metrics.widthPixels - topParentSpaceX;
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        mFacebookeNativeAdContainer.removeAllViews();
        mFacebookeNativeAdContainer.addView(adView, params);
        return true;
    }

    private void onAdLoadFailed() {
        mIsLoading = false;
        if (BuildConfig.DEBUG)
            LogUtil.d(TAG, "loadAd onError adType:" + mAdType + ",priority:" + mAdShowPriority + ",adId:" + mAdId);
        if (mAdapter != null) {
            mAdapter.onAdError(PreloadAdvertisement.this);
        }
    }

    private boolean showFb(View adView) {
        if (mFbAdInfo == null) return false;
        NativeAd facebookNativeAd = null;
        NativeBannerAd facebookNativeBannerAd = null;
        Ad ad = mFbAdInfo.ad;
        boolean isShow;
        if (mIsBanner) {
            facebookNativeBannerAd = mFbAdInfo.facebookNativeBannerAd;
            if (facebookNativeBannerAd == null || facebookNativeBannerAd != ad || mFacebookeNativeAdContainer == null || mAdapter == null) {
                // Race condition, load() called again before last ad was displayed
                return false;
            }
            mFacebookeNativeAdContainer.setVisibility(View.VISIBLE);
            // Unregister last ad
            facebookNativeBannerAd.unregisterView();
            isShow = inflateBannerAd(facebookNativeBannerAd, mLayoutFacebook);
        } else {
            facebookNativeAd = mFbAdInfo.facebookNativeAd;
            if (facebookNativeAd == null || facebookNativeAd != ad || mFacebookeNativeAdContainer == null || mAdapter == null) {
                // Race condition, load() called again before last ad was displayed
                return false;
            }
            mFacebookeNativeAdContainer.setVisibility(View.VISIBLE);
            // Unregister last ad
            facebookNativeAd.unregisterView();
            isShow = inflateAd(facebookNativeAd, mLayoutFacebook);
        }

        if (isShow) {
            mAdapter.adjustFbContainerView(mLayoutFacebook);
            mFbAdInfo.isShow = true;
            mAdapter.onAdShow(mAdType, mAdShowPriority);
            LogUtil.d(TAG, "showNativeFacebook success adType:" + mAdType + ", adShowPriority:" + mAdShowPriority + ",adId" + mAdId);
        }
        return isShow;
    }

    private boolean showAdmob(View adView) {
        if (mAdapter == null) return false;
        if (mLayoutFacebook != null) {
            mLayoutFacebook.setVisibility(View.GONE);
        }
        if (mFacebookeNativeAdContainer != null) {
            mFacebookeNativeAdContainer.setVisibility(View.GONE);
        }
        if (mLayoutAdmobAdx != null) {
            mLayoutAdmobAdx.setVisibility(View.GONE);
        }
        if (mLayoutAdmob == null) return false;
        if (mAdmobAdInfo == null) return false;
        boolean isShow = false;
        int admobType = mAdmobAdInfo.admobType;
        switch (admobType) {
            case ADMOB_TYPE_NATIVE:
                AdView admobNativeView = mAdmobAdInfo.admobNativeView;
                if (admobNativeView != null) {
                    mLayoutAdmob.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                    mLayoutAdmob.addView(admobNativeView, params);
                    mAdmobAdInfo.isShow = true;
                    mAdapter.onAdShow(mAdType, mAdShowPriority);
                    isShow = true;
                    LogUtil.d(TAG, "showNativeAdmob success adType:" + mAdType + ",adShowPriority:" + mAdShowPriority + ",adId" + mAdId);
                }
                break;
            case ADMOB_TYPE_NATIVE_ADVANCED:
                boolean isAppInstall = mAdmobAdInfo.isAppInstall;
                if (isAppInstall) {
                    isShow = inflateAdmobAppInstallView(mAdmobAdInfo, mLayoutAdmob);
                } else {
                    isShow = inflateAdmobContentAdView(mAdmobAdInfo, mLayoutAdmob);
                }
                if (isShow) {
                    adjustAdmobView(mLayoutAdmob);
                    mAdmobAdInfo.isShow = true;
                    mAdapter.onAdShow(mAdType, mAdShowPriority);
                    String str = isAppInstall ? "AppInstall" : "Content";
                    LogUtil.d(TAG, "showNativeAdvanceAdmob  adType:" + str + " success  adType: " + mAdType + ",adShowPriority:" + mAdShowPriority + ",adId" + mAdId);
                }
                break;
        }
        return isShow;
    }

    private boolean showAdmobAdx(View adView) {
        if (mAdapter == null) return false;
        if (mLayoutFacebook != null) {
            mLayoutFacebook.setVisibility(View.GONE);
        }
        if (mFacebookeNativeAdContainer != null) {
            mFacebookeNativeAdContainer.setVisibility(View.GONE);
        }
        if (mLayoutAdmob != null) {
            mLayoutAdmob.setVisibility(View.GONE);
        }
        if (mLayoutAdmobAdx == null) return false;
        if (mAdmobAdxInfo == null) return false;
        boolean isShow = false;
        int admobAdxType = mAdmobAdxInfo.admobAdxType;
        switch (admobAdxType) {
            case ADMOB_ADX_TYPE_NATIVE:
                AdView admobAdxNativeView = mAdmobAdxInfo.admobAdxNativeView;
                if (admobAdxNativeView != null) {
                    mLayoutAdmobAdx.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                    mLayoutAdmobAdx.addView(admobAdxNativeView, params);
                    mAdmobAdxInfo.isShow = true;
                    mAdapter.onAdShow(mAdType, mAdShowPriority);
                    isShow = true;
                    LogUtil.d(TAG, "showNativeAdmob adx success adType:" + mAdType + ",adShowPriority:" + mAdShowPriority + ",adId" + mAdId);
                }
                break;
            case ADMOB_ADX_TYPE_NATIVE_ADVANCED:
                boolean isAppInstall = mAdmobAdxInfo.isAppInstall;
                if (isAppInstall) {
                    isShow = inflateAdmobAdxAppInstallView(mAdmobAdxInfo, mLayoutAdmobAdx);
                } else {
                    isShow = inflateAdmobAdxContentAdView(mAdmobAdxInfo, mLayoutAdmobAdx);
                }
                if (isShow) {
                    adjustAdmobAdxView(mLayoutAdmobAdx);
                    mAdmobAdxInfo.isShow = true;
                    mAdapter.onAdShow(mAdType, mAdShowPriority);
                    String str = isAppInstall ? "AppInstall" : "Content";
                    LogUtil.d(TAG, "showNativeAdvanceAdmobAdx  adType:" + str + " success  adType: " + mAdType + ",adShowPriority:" + mAdShowPriority + ",adId" + mAdId);
                }
                return isShow;
        }
        return isShow;
    }

    private void sendFbErrorFlurry(AdError arg1) {
        try {
            if (mContext != null && mAdapter != null) {

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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public interface PreloadAdvertisementAdapter {
        int getFbViewRes();

        int getAdmobViewRes(int type, boolean isAppInstall);

        int getAdmobAdxViewRes(int type, boolean isAppInstall);

        String getAdId();

        int getAdWidth(String adType);//PX

        int getAdHeight(String adType);//PX

        boolean isBanner();

        boolean shouldLogClickTime();//default is true

        boolean shouldShowActionButton(); // default: true

        PreloadAdvertisement.AdTypeInfo getAdTypeInfo();

        boolean hideIconViewWhenNone();//hide admob icon view when no data to show, default is true

        int getAdContainerSpaceX();//返回layout/layout_advertisement父节点的x方向的空隙(px)

        ImageView getSmallIconView();

        void onAdClicked(String adType, String adShowPriority);

        void onAdLoaded(PreloadAdvertisement preloadAdvertisement);

        void onAdShow(String adType, String adShowPriority);

        void onAdError(PreloadAdvertisement preloadAdvertisement);

        void adjustAdmobView(FrameLayout layoutAdmob);

        void adjustAdmobAdxView(FrameLayout layoutAdmob);

        void adjustFbContainerView(LinearLayout layoutFacebook);

        String getAdShowPriority();

        String getPlacementId();
    }

    private class NativeAdmobAdListener extends com.google.android.gms.ads.AdListener {
        private AdView admobView;

        public void setAdmobView(AdView admobView) {
            this.admobView = admobView;
        }

        @Override
        public void onAdLoaded() {
            if (mIsClosed) {
                return;
            }

            if (mAdmobAdInfo == null) {
                mAdmobAdInfo = new AdmobAdInfo();
            }
            mAdmobAdInfo.admobType = ADMOB_TYPE_NATIVE;
            mAdmobAdInfo.admobNativeView = admobView;
            mAdmobAdInfo.loadSuccessTime = System.currentTimeMillis();
            if (BuildConfig.DEBUG)
                LogUtil.d(TAG, "loadNativeAdmob onAdLoaded adType:" + mAdType + ",mAdShowPriority:" + mAdShowPriority + ",adId:" + mAdId);
            mIsLoading = false;
            mAdapter.onAdLoaded(PreloadAdvertisement.this);
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            LogUtil.d(TAG, "loadNativeAdmob onAdFailed :" + errorCode);
            if (mIsClosed) {
                return;
            }
            onAdLoadFailed();
        }

        @Override
        public void onAdOpened() {
            if (mIsClosed) {
                return;
            }
            onAdClick();
            if (BuildConfig.DEBUG)
                LogUtil.d(TAG, PreloadAdvertisement.this.hashCode() + "Admob native ad clicked adType:" + mAdType + ",mAdShowPriority:" + mAdShowPriority + ",adId:" + mAdType);
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
    }

    private class NativeAdmobAdxAdListener extends com.google.android.gms.ads.AdListener {
        private AdView admobAdxView;

        public void setAdmobAdxView(AdView admobAdxView) {
            this.admobAdxView = admobAdxView;
        }

        @Override
        public void onAdLoaded() {
            if (mIsClosed) {
                return;
            }
            if (mAdmobAdxInfo == null) {
                mAdmobAdxInfo = new AdmobAdxAdInfo();
            }
            mAdmobAdxInfo.admobAdxType = ADMOB_ADX_TYPE_NATIVE;
            mAdmobAdxInfo.admobAdxNativeView = admobAdxView;
            mAdmobAdxInfo.loadSuccessTime = System.currentTimeMillis();
            if (BuildConfig.DEBUG)
                LogUtil.d(TAG, "loadNativeAdmobAdx onAdLoaded adType:" + mAdType + ", mAdShowPriority:" + mAdShowPriority + ",adId:" + mAdId);
            mIsLoading = false;
            mAdapter.onAdLoaded(PreloadAdvertisement.this);
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            LogUtil.d(TAG, "loadNativeAdmobAdx onAdFailed errorCode:" + errorCode);
            if (mIsClosed) {
                return;
            }
            onAdLoadFailed();
        }

        @Override
        public void onAdOpened() {
            if (mIsClosed) {
                return;
            }
            onAdClick();
            if (BuildConfig.DEBUG)
                LogUtil.d(TAG, PreloadAdvertisement.this.hashCode() + "AdmobAdx native ad clicked adType:" + mAdType + ", mAdShowPriority:" + mAdShowPriority + ",adId:" + mAdId);
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
    }

    /**
     * Facebook请求广告的网络回调
     */
    private class NativeFBListener implements NativeAdListener {

        @Override
        public void onAdClicked(Ad arg0) {
            if (mIsClosed) {
                return;
            }
            onAdClick();
            if (BuildConfig.DEBUG)
                LogUtil.d(TAG, "loadFacebookAd onAdClicked adType:" + mAdType + ", priority:" + mAdShowPriority + ",adId:" + mAdId);
        }

        @Override
        public void onAdLoaded(Ad ad) {
            try {
                if (mIsClosed) {
                    return;
                }
                if (mFbAdInfo == null) {
                    mFbAdInfo = new FacebookAdInfo();
                }
                mFbAdInfo.ad = ad;
                mFbAdInfo.loadSuccessTime = System.currentTimeMillis();
                if (BuildConfig.DEBUG)
                    LogUtil.d(TAG, "loadFacebookAd onAdLoaded adType:" + mAdType + ",priority:" + mAdShowPriority + ",adId:" + mAdId);
                mIsLoading = false;
                mAdapter.onAdLoaded(PreloadAdvertisement.this);
            } catch (Exception e) {
                LogUtil.e(TAG, "NativeFBListener onAdLoaded error: " + e.getMessage());
                LogUtil.error(e);
            }
        }

        @Override
        public void onError(Ad arg0, AdError arg1) {
            try {
                LogUtil.e(TAG, "loadFacebookAd onError error: " + arg1.getErrorMessage());
                if (mIsClosed) {
                    return;
                }
                onAdLoadFailed();
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
                    LogUtil.d(TAG, "onLoggingImpression:" + ad.getPlacementId());
                mAdapter.onAdShow(mAdType, mAdShowPriority);
                //StatisticsUtil.logAdEvent(mAdapter.getPlacementId(), "facebook", "展示");
            }
        }

        @Override
        public void onMediaDownloaded(Ad ad) {

        }
    }

    public class AdviewIDException extends Exception {
        public boolean canTry;

        public AdviewIDException(String e) {
            super(e);
        }
    }

    public class AdmobAdInfo {
        long loadSuccessTime;//加载成功的时间
        int admobType;//原生或者高级原生
        boolean isShow;//是否显示
        //原生
        AdView admobNativeView;

        //高级原生
        AdLoader admobNativeAdvancedLoader;
        boolean isAppInstall;//true:appinstall 类型， false:content类型
        NativeAppInstallAd nativeAdvanceAppInstallAd;//高级原生中的AppInstall广告
        NativeContentAd nativeAdvanceContentAd;////高级原生中的content广告

    }

    public class AdmobAdxAdInfo {
        long loadSuccessTime;//加载成功的时间
        int admobAdxType;//原生或者高级原生
        boolean isShow;//是否显示
        //原生
        AdView admobAdxNativeView;

        //高级原生
        AdLoader admobAdxNativeAdvancedLoader;
        boolean isAppInstall;//true:appinstall 类型， false:content类型
        NativeAppInstallAd nativeAdvanceAppInstallAd;//高级原生中的AppInstall广告
        NativeContentAd nativeAdvanceContentAd;////高级原生中的content广告

    }

    public class FacebookAdInfo {
        Ad ad;
        NativeAd facebookNativeAd;
        NativeBannerAd facebookNativeBannerAd;
        long loadSuccessTime;//加载成功的时间
        boolean isShow;//是否显示
    }

    /**
     * 某一类广告显示的优先级
     */
    public static class AdShowPriority {
        public static final String HIGH = "HIGH";
        public static final String MEDIUM = "MEDIUM";
        public static final String NORMAL = "NORMAL";
    }

    public static class AdTypeInfo {
        public String adType;//广告类型，fb,admob,admobAdx
        public int admobType;//原生，高级原生
        public int admobAdxType;//原生，高级原生
    }
}
