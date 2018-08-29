package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;


import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;


/**
 * Created by luowp on 2016/5/29.
 * default advertisement adapter
 */
public class BaseAdvertisementAdapter implements Advertisement.AdvertisementAdapter {
    private String mAdPlacementKey;//key for ad click event stat used by flurry, pass null means no need stat
    private String mFbKey;//facebook ad request key
    private String mAdmobKey;//admob ad request key
    private String mMopubKey; // mopub ad request key
    private String mMopubBannerKey = ""; // mopub banner ad request key
    private String mAdMobBannerKey; // admob big banner key
    private int mBaiduKey;
    private String mPlacementServerKey;//placement identifier in lion server
    private int mAdmobType;
    private int mMoPubType;
    private View mContext;
    protected boolean mIsBanner;
    private ApplicationEx appEx;

    protected String mLoadedAdType;

    public BaseAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, boolean isBanner, String placementId) {
        mContext = context;
        mFbKey = facebookKey;
        mAdmobKey = admobKey;
        mAdmobType = admobType;
        mAdPlacementKey = eventKey;
        mIsBanner = isBanner;
        mPlacementServerKey = placementId;
        appEx = ApplicationEx.getInstance();

//        if (BuildConfig.DEBUG) {
//            if (TextUtils.isEmpty(placementId)) {
//                CommonUtil.makeCrash();
//            }
//        }
    }

    public BaseAdvertisementAdapter(View context,
                                    String facebookKey,
                                    String admobKey,
                                    int admobType,
                                    String mopubKey,
                                    int moPubType,
                                    int baiduKey,
                                    String eventKey,
                                    String placementId,
                                    boolean isBanner) {
        this.mAdPlacementKey = eventKey;
        this.mFbKey = facebookKey;
        this.mAdmobKey = admobKey;
        this.mMopubKey = mopubKey;
        this.mPlacementServerKey = placementId;
        this.mAdmobType = admobType;
        this.mBaiduKey = baiduKey;
        this.mContext = context;
        this.mIsBanner = isBanner;
        this.mMoPubType = moPubType;
    }

    public void close() {
        mContext = null;
    }

    @Override
    public String getPlacementId() {
        return mPlacementServerKey;
    }

    @Override
    public FrameLayout getAdmobContainerView() {
        return (FrameLayout) mContext.findViewById(R.id.layout_admob);
    }

    @Override
    public LinearLayout getFbContainerView() {
        return (LinearLayout) mContext.findViewById(R.id.nativeAdContainer);
    }

    @Override
    public LinearLayout getMoPubNativeContainerView() {
        return (LinearLayout) mContext.findViewById(R.id.layout_mopub_native_view);
    }

    @Override
    public LinearLayout getBaiDuContaionerView() {
        return (LinearLayout) mContext.findViewById(R.id.layout_baidu_contaioner);
    }

    @Override
    public int getFbViewRes() {
        return mIsBanner ? FacebookConstant.FB_AD_BANNER_VIEW_RES_ID : FacebookConstant.FB_AD_BIG_VIEW_RES_ID;
    }

    @Override
    public int getAdmobViewRes(int type, boolean isAppInstall) {
        if (mIsBanner) {
            return isAppInstall ? R.layout.layout_admob_banner_app_install : R.layout.layout_admob_banner_content;
        } else {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad : R.layout.layout_admob_advanced_content_ad;
        }
    }

    @Override
    public int getMoPubViewRes() {
        return mIsBanner ? R.layout.layout_mopub_ad_banner : R.layout.layout_mopub_no_icon_native_ads;
    }

    @Override
    public int getBaiDuViewRes() {
        return mIsBanner ? R.layout.layout_du_ad_banner : R.layout.layout_du_ad_big;
    }

    @Override
    public String getAdmobKey() {
        return mAdmobKey;
    }

    @Override
    public String getAdmobBannerKey() {
        return mAdMobBannerKey;
    }

    public void setAdMobBannerKey(String admobBannerKey) {
        this.mAdMobBannerKey = admobBannerKey;
    }

    @Override
    public String getFbKey() {
        return mFbKey;
    }

    @Override
    public String getMopubKey(String mopubType) {
        return mopubType.equals(AdvertisementSwitcher.AD_MOPUB) ? mMopubKey : mMopubBannerKey;
    }

    public void setMopubBannerKey(String bannerKey) {
        LogUtil.d("random_adid", "setMopubBannerKey: " + bannerKey);
        this.mMopubBannerKey = bannerKey;
    }

    @Override
    public int getBaiDuKey() {
        return mBaiduKey;
    }

    @Override
    public int getAdmobWidth() {
        return DeviceUtil.getScreenWidth();
    }

    @Override
    public int getAdmobHeight() {
        return mIsBanner ? Stringutil.dpToPx(80) : Stringutil.dpToPx(250);
    }

    @Override
    public int getFbAdsHight() {
        return 64; //default banner height
    }

    @Override
    public void onAdClicked(boolean isAdmob) {

    }

    @Override
    final public void onAdLoaded(String adType) {
        mLoadedAdType = adType;
        onAdLoaded();
        updateAdView(adType);
    }

    private void updateAdView(final String adType) {
        ViewGroup fbContainerView = getFbContainerView();
        ViewGroup admobContainerView = getAdmobContainerView();
        ViewGroup mopubView = getMoPubNativeContainerView();
        ViewGroup baiduView = getBaiDuContaionerView();

        setGone(fbContainerView);
        setGone(admobContainerView);
        setGone(mopubView);
        setGone(baiduView);

        if (Stringutil.equalsWithoutNull(adType, AdvertisementSwitcher.AD_FACEBOOK)) {
            setVisible(fbContainerView);
        } else if (Stringutil.equalsWithoutNull(adType, AdvertisementSwitcher.AD_ADMOB)
                || Stringutil.equalsWithoutNull(adType, AdvertisementSwitcher.AD_ADMOB_BIG_BANNER)) {
            setVisible(admobContainerView);
        } else if (Stringutil.equalsWithoutNull(adType, AdvertisementSwitcher.AD_MOPUB)
                || Stringutil.equalsWithoutNull(adType, AdvertisementSwitcher.AD_MOPUB_BANNER)) {
            setVisible(mopubView);
        } else if (Stringutil.equalsWithoutNull(adType, AdvertisementSwitcher.AD_BAIDU)) {
            setVisible(baiduView);
        }
    }

    private void setGone(ViewGroup view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private void setVisible(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean isBanner() {
        return mIsBanner;
    }

    @Override
    public boolean shouldLogClickTime() {
        return true;
    }

    @Override
    public boolean shouldShowActionButton() {
        return true;
    }

    @Override
    public int getAdmobType() {
        return TextUtils.isEmpty(mAdmobKey) ? Advertisement.ADMOB_TYPE_NONE : mAdmobType;
    }

    @Override
    public int getMoPubType() {
        return mMoPubType;
    }

    public void onAdLoaded() {

    }

    public String getLoadedAdType() {
        return mLoadedAdType;
    }

    @Override
    public void onRefreshClicked() {

    }

    @Override
    public boolean hideIconViewWhenNone() {
        return true;
    }

    @Override
    public boolean didForceLoadAdFromCache() {
        return false;
    }

    @Override
    public int getAdContainerSpaceX() {
        return 0;
    }

    @Override
    public ImageView getSmallIconView() {
        return null;
    }

    @Override
    public void onAdShow() {
    }

    @Override
    public void onAdError(boolean isLastRequestIndex) {

    }

    @Override
    public void adjustAdmobView(FrameLayout layoutAdmob) {
    }

    @Override
    public void adjustFbContainerView(LinearLayout layoutFacebook) {
    }

    @Override
    public void adjustMoPubContainerView(LinearLayout layoutMoPub) {

    }

    @Override
    public void adjustBaiDuContaionerView(LinearLayout layoutBaiDu) {

    }
}
