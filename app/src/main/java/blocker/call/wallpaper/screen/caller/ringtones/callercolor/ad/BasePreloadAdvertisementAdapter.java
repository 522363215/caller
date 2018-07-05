package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;


import android.content.Context;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

public class BasePreloadAdvertisementAdapter implements PreloadAdvertisement.PreloadAdvertisementAdapter {
    private final String mAdId;
    private final PreloadAdvertisement.AdTypeInfo mAdTypeInfo;
    private final String mAdShowPriority;
    protected boolean mIsBanner;
    private String mPlacementServerKey;//placement identifier in lion server
    private Context mContext;

    public BasePreloadAdvertisementAdapter(String adId, PreloadAdvertisement.AdTypeInfo adTypeInfo, String adShowPriority, String placementId, boolean isBanner) {
        mAdId = adId;
        mAdTypeInfo = adTypeInfo;
        mIsBanner = isBanner;
        mAdShowPriority = adShowPriority;
        mPlacementServerKey = placementId;
        mContext = ApplicationEx.getInstance();
    }

    @Override
    public String getAdShowPriority() {
        return mAdShowPriority;
    }

    @Override
    public String getPlacementId() {
        return mPlacementServerKey;
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
    public int getAdmobAdxViewRes(int type, boolean isAppInstall) {
        if (mIsBanner) {
            return isAppInstall ? R.layout.layout_admob_banner_app_install : R.layout.layout_admob_banner_content;
        } else {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad : R.layout.layout_admob_advanced_content_ad;
        }
    }

    @Override
    public String getAdId() {
        return mAdId;
    }

    @Override
    public int getAdWidth(String adType) {
        if (AdvertisementSwitcher.AD_FACEBOOK.equals(adType)) {
        } else if (AdvertisementSwitcher.AD_ADMOB.equals(adType)) {
            return DeviceUtil.getScreenWidth();
        } else if (AdvertisementSwitcher.AD_ADMOB_ADX.equals(adType)) {
            return DeviceUtil.getScreenWidth();
        }
        return DeviceUtil.getScreenWidth();
    }

    @Override
    public int getAdHeight(String adType) {
        if (AdvertisementSwitcher.AD_FACEBOOK.equals(adType)) {
            return 64;
        } else if (AdvertisementSwitcher.AD_ADMOB.equals(adType)) {
            return mIsBanner ? Stringutil.dpToPx(mContext, 80) : Stringutil.dpToPx(mContext, 250);
        } else if (AdvertisementSwitcher.AD_ADMOB_ADX.equals(adType)) {
            return mIsBanner ? Stringutil.dpToPx(mContext, 80) : Stringutil.dpToPx(mContext, 250);
        }
        return mIsBanner ? Stringutil.dpToPx(mContext, 80) : Stringutil.dpToPx(mContext, 250);
    }

    @Override
    public void onAdClicked(String adType, String adShowPriority) {

    }

    @Override
    public void onAdLoaded(PreloadAdvertisement preloadAdvertisement) {

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
    public PreloadAdvertisement.AdTypeInfo getAdTypeInfo() {
        return TextUtils.isEmpty(mAdId) ? null : mAdTypeInfo;
    }

    @Override
    public boolean hideIconViewWhenNone() {
        return true;
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
    public void onAdShow(String adType, String adShowPriority) {
    }

    @Override
    public void onAdError(PreloadAdvertisement preloadAdvertisement) {

    }

    @Override
    public void adjustAdmobView(FrameLayout layoutAdmob) {
    }

    @Override
    public void adjustAdmobAdxView(FrameLayout layoutAdmob) {

    }

    @Override
    public void adjustFbContainerView(LinearLayout layoutFacebook) {
    }
}
