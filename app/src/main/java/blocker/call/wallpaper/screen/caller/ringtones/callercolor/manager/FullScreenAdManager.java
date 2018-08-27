package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BasePreloadAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.FirstShowAdmobUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.PreloadAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog.FullScreenAdDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.AdPreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

public class FullScreenAdManager {
    private static final String TAG = "FullScreenAdManager";
    private static FullScreenAdManager sInstance;
    private PreloadAdvertisement mPreloadAdvertisement;
    private boolean mIsAdLoaded;
    private AdListener mAdListener;
    private FullScreenAdDialog mFullScreenAdDialog;
    private int mPosition;

    private FullScreenAdManager() {
    }

    public static FullScreenAdManager getInstance() {
        if (sInstance == null) {
            synchronized (FullScreenAdManager.class) {
                sInstance = new FullScreenAdManager();
                return sInstance;
            }
        }
        return sInstance;
    }

    public void clear() {
        mIsAdLoaded = false;
        mPreloadAdvertisement = null;
        mAdListener = null;
    }

    public boolean isAdLoaded() {
        return mIsAdLoaded;
    }

    public void loadAd(int position) {
        try {
            String adId = getAdId(position);
            if (!TextUtils.isEmpty(adId)) {
                if (mPreloadAdvertisement == null || (!mPreloadAdvertisement.isValid(true) && !mPreloadAdvertisement.isLoading())) {
                    PreloadAdvertisement.AdTypeInfo adTypeInfo = new PreloadAdvertisement.AdTypeInfo();
                    adTypeInfo.adType = AdvertisementSwitcher.AD_ADMOB;
                    adTypeInfo.admobType = Advertisement.ADMOB_TYPE_NATIVE;

                    mPreloadAdvertisement = new PreloadAdvertisement(TAG,
                            new MyPreloadAdvertisementAdapter(
                                    adId,
                                    adTypeInfo,
                                    PreloadAdvertisement.AdShowPriority.NORMAL,
                                    AdvertisementSwitcher.SERVER_KEY_FIRST_SHOW_ADMOB,
                                    false));

                    mPreloadAdvertisement.loadAd();
                }
                LogUtil.d(TAG, "load ad id: " + adId);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "loadAd exception:" + e.getMessage());
        }
    }

    private String getAdId(int position) {
        String adId = "";
        switch (position) {
            case InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL:
                adId = FirstShowAdmobUtil.getAdmobIdForFirst(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_FULL_SCREEN_CALL_FLASH_DETAIL);
                break;
        }
        return adId;
    }

    public void showAd(Context context, int position, AdListener adListener) {
        mPosition = position;
        if (!mIsAdLoaded || mPreloadAdvertisement == null) return;
        mAdListener = adListener;
        mFullScreenAdDialog = new FullScreenAdDialog(context);
        mFullScreenAdDialog.show(new FullScreenAdDialog.FullScreenAdListener() {
            @Override
            public void onAdClose() {
//                    LogUtil.d(TAG, " showAd onAdClosed");
                if (mAdListener != null) {
                    mAdListener.onAdClose();
                }
            }

            @Override
            public void onAdShow() {
//                    LogUtil.d(TAG, " showAd onAdShow");
                mPreloadAdvertisement.showAd(mFullScreenAdDialog.getWindow().getDecorView());
                if (mAdListener != null) {
                    mAdListener.onAdShow();
                }
            }
        });
    }

    private class MyPreloadAdvertisementAdapter extends BasePreloadAdvertisementAdapter {
        private String mKey;

        private MyPreloadAdvertisementAdapter(String adId, PreloadAdvertisement.AdTypeInfo adTypeInfo, String adShowPriority, String placementId, boolean isBanner) {
            super(adId, adTypeInfo, adShowPriority, placementId, isBanner);
            mKey = placementId;
        }

        @Override
        public void onAdLoaded(PreloadAdvertisement preloadAdvertisement) {
            mPreloadAdvertisement = preloadAdvertisement;
            mIsAdLoaded = true;
        }

        @Override
        public void onAdShow(String adType, String adShowPriority) {
            super.onAdShow(adType, adShowPriority);
            saveFirstShowAdmobTime();
        }

        @Override
        public void onAdClicked(String adType, String adShowPriority) {
            super.onAdClicked(adType, adShowPriority);
            LogUtil.d(TAG, "showFullScreenAd  onAdClick");
            if (mAdListener != null) {
                mAdListener.onAdClick();
            }
            if (mFullScreenAdDialog != null) {
                mFullScreenAdDialog.dismiss();
            }
        }

        private void saveFirstShowAdmobTime() {
            HashMap<String, Long> data = AdPreferenceHelper.getHashMapData(AdPreferenceHelper.PREF_LAST_SHOW_FIRST_ADMOB_TIME_MAP, Long.class);
            switch (mPosition) {
                case InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL:
                    if (data == null) {
                        data = new HashMap<>();
                    }
                    data.put(String.valueOf(FirstShowAdmobUtil.POSITION_FIRST_ADMOB_FULL_SCREEN_CALL_FLASH_DETAIL), System.currentTimeMillis());
                    AdPreferenceHelper.putHashMapData(AdPreferenceHelper.PREF_LAST_SHOW_FIRST_ADMOB_TIME_MAP, data);
                    break;
            }
        }

        @Override
        public void onAdError(PreloadAdvertisement preloadAdvertisement) {
            super.onAdError(preloadAdvertisement);
        }

        @Override
        public int getFbViewRes() {
            return isBanner() ? R.layout.facebook_native_ads_banner_50 : R.layout.facebook_native_ads_flash_set_result;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_full_screen : R.layout.layout_admob_advanced_content_ad_full_screen;
        }
    }

    public interface AdListener {
        void onAdShow();

        void onAdClose();

        void onAdClick();
    }

}
