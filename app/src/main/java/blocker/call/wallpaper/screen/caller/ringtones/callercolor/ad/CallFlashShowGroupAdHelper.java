package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.text.TextUtils;
import android.view.View;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventGroupAdShow;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import event.EventBus;

public class CallFlashShowGroupAdHelper {
    public static final long SHOW_AD_PERIOD = 30 * 1000;
    private static final String TAG = "CallFlashShowGroupAdHelper-PreloadAdvertisement";
    private static CallFlashShowGroupAdHelper sInstance;
    private Map<String, PreloadAdvertisement> mPreloadAdvertisementMap;
    private Map<String, Map<String, Boolean>> mLoadedAdMep;
    private List<AdIdInfo> mAdShowPriority;//按广告显示顺序排列的广告ID信息
    private int mCurrentShowAdIndex = -1;
    private boolean mIsShowGroupAd;
    private Runnable mLooperShowAdRunnable;

    public static CallFlashShowGroupAdHelper getInstance() {
        if (sInstance == null) {
            synchronized (CallFlashShowGroupAdHelper.class) {
                sInstance = new CallFlashShowGroupAdHelper();
                return sInstance;
            }
        }
        return sInstance;
    }

    private CallFlashShowGroupAdHelper() {
        mPreloadAdvertisementMap = new ConcurrentHashMap<>();
        mAdShowPriority = new ArrayList<>();
        mLoadedAdMep = new ConcurrentHashMap<>();
    }

    public void loadGroupAd(String key, boolean isBanner, int admobType, int admobAdxType) {
        try {
            PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_CALL_FLASH_SHOW_GROUP_BANNER_AD_TIME, 0);
            PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_CALL_FLASH_STOP_LOOPER_SHOW_AD_TIME, 0);
            mLoadedAdMep.clear();
            mIsShowGroupAd = false;
            setAdShowPriority(key);
            loadFbAd(key, isBanner);
            loadAdmobAd(key, isBanner, admobType);
            loadAdmobAdxAd(key, isBanner, admobAdxType);
        } catch (Exception e) {
            LogUtil.e(TAG, "loadGroupAd exception: " + e.getMessage());
        }

    }

    public void showGroupAd(final boolean isResetShowAd, final View adView, final String key, final boolean isBanner, final int admobType, final int admobAdxType) {
        try {
            if (isResetShowAd) {
                mCurrentShowAdIndex = -1;
            }
            long nextShowDelay = 0;
            AdIdInfo adIdInfo = getAndUpdateAdShowId(key);
            if (adIdInfo == null) {
                if (mCurrentShowAdIndex <= 27) {
                    Async.scheduleTaskOnUiThread(nextShowDelay, mLooperShowAdRunnable = new Runnable() {
                        @Override
                        public void run() {
                            showGroupAd(false, adView, key, isBanner, admobType, admobAdxType);
                        }
                    });
                }
                return;
            }
//        LogUtil.d(TAG, "showGroupAd adIdInfo adtype:" + adIdInfo.adType + ",AdId:" + adIdInfo.adId + ",mPreloadAdvertisementMap:" + mPreloadAdvertisementMap);
            if (mPreloadAdvertisementMap != null) {
                PreloadAdvertisement preloadAdvertisement = mPreloadAdvertisementMap.get(adIdInfo.adId);
                if (preloadAdvertisement == null || !preloadAdvertisement.isValid(false)) {
//                PreloadAdvertisement.AdTypeInfo adTypeInfo = getAdTypeInfo(adIdInfo.adType, admobType, admobAdxType);
//                loadAd(adIdInfo.adId, key, isBanner, adIdInfo.adShowPriority, adTypeInfo);
                } else {
                    LogUtil.d(TAG, "---------------------------------------------show " + adIdInfo.adType + " " + adIdInfo.adShowPriority + "------------------------------------------");
                    mIsShowGroupAd = true;
                    PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_CALL_FLASH_SHOW_GROUP_BANNER_AD_TIME, System.currentTimeMillis());
                    if (CallerAdManager.isOnlyBtnClickable(CallerAdManager.POSITION_FB_ADS_CALLFLASH)) {
                        preloadAdvertisement.enableOnlyBtnAndTextClickable();
                    }
                    preloadAdvertisement.showAd(adView);
                    nextShowDelay = SHOW_AD_PERIOD;
                }

//            if (mPreloadAdvertisementMap.size() > 1) {
                Async.scheduleTaskOnUiThread(nextShowDelay, mLooperShowAdRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showGroupAd(false, adView, key, isBanner, admobType, admobAdxType);
                        } catch (Exception e) {
                            LogUtil.e(TAG, "showGroupAd nextShowDelay:" + e.getMessage());
                        }
                    }
                });
//            }
            } else {
                loadGroupAd(key, isBanner, admobType, admobAdxType);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "showGroupAd exception:" + e.getMessage());
        }
    }

    public void Clear() {
        mIsShowGroupAd = false;
        mCurrentShowAdIndex = -1;
        mLoadedAdMep.clear();
        PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_CALL_FLASH_SHOW_GROUP_BANNER_AD_TIME, 0);
        PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_CALL_FLASH_STOP_LOOPER_SHOW_AD_TIME, 0);
        Async.removeScheduledTaskOnUiThread(mLooperShowAdRunnable);
    }

    public void stopLooperShowAd() {
        PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_CALL_FLASH_STOP_LOOPER_SHOW_AD_TIME, System.currentTimeMillis());
        Async.removeScheduledTaskOnUiThread(mLooperShowAdRunnable);
    }

    /**
     * 设置广告显示的顺序
     */
    private void setAdShowPriority(String key) {
        List<String> adPriority = AdvertisementSwitcher.getInstance().getAdPriority(key);
        if (adPriority == null) return;
        mAdShowPriority.clear();
        LogUtil.d(TAG, "setAdShowPriority adPriority:" + String.valueOf(adPriority));
        setAdShow(PreloadAdvertisement.AdShowPriority.HIGH, adPriority);
        setAdShow(PreloadAdvertisement.AdShowPriority.MEDIUM, adPriority);
        setAdShow(PreloadAdvertisement.AdShowPriority.NORMAL, adPriority);
//        for (AdIdInfo adIdInfo : mAdShowPriority) {
//            LogUtil.d(TAG, "adtype:" + adIdInfo.adType + ",mAdShowPriority:" + adIdInfo.adShowPriority + ",adid:" + adIdInfo.adId);
//        }
    }

    private void setAdShow(String adShowPriority, List<String> adPriority) {
        if (mAdShowPriority == null) {
            mAdShowPriority = new ArrayList<>();
        }
        for (String ad : adPriority) {
            if (AdvertisementSwitcher.AD_FACEBOOK.equals(ad)) {
                if (PreloadAdvertisement.AdShowPriority.HIGH.equals(adShowPriority)) {
                    String fbAdHighId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_FACEBOOK_HIGH);
                    if (!TextUtils.isEmpty(fbAdHighId)) {
                        AdIdInfo info = new AdIdInfo(fbAdHighId, PreloadAdvertisement.AdShowPriority.HIGH, AdvertisementSwitcher.AD_FACEBOOK);
                        mAdShowPriority.add(info);
                    }
                } else if (PreloadAdvertisement.AdShowPriority.MEDIUM.equals(adShowPriority)) {
                    String fbAdMediumId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_FACEBOOK_MEDIUM);
                    if (!TextUtils.isEmpty(fbAdMediumId)) {
                        AdIdInfo info = new AdIdInfo(fbAdMediumId, PreloadAdvertisement.AdShowPriority.MEDIUM, AdvertisementSwitcher.AD_FACEBOOK);
                        mAdShowPriority.add(info);
                    }
                } else if (PreloadAdvertisement.AdShowPriority.NORMAL.equals(adShowPriority)) {
                    String fbAdNormalId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_FACEBOOK_NORMAL);
                    if (!TextUtils.isEmpty(fbAdNormalId)) {
                        AdIdInfo info = new AdIdInfo(fbAdNormalId, PreloadAdvertisement.AdShowPriority.NORMAL, AdvertisementSwitcher.AD_FACEBOOK);
                        mAdShowPriority.add(info);
                    }
                }
            } else if (AdvertisementSwitcher.AD_ADMOB.equals(ad)) {
                if (PreloadAdvertisement.AdShowPriority.HIGH.equals(adShowPriority)) {
                    String admobAdHighId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_HIGH);
                    if (!TextUtils.isEmpty(admobAdHighId)) {
                        AdIdInfo info = new AdIdInfo(admobAdHighId, PreloadAdvertisement.AdShowPriority.HIGH, AdvertisementSwitcher.AD_ADMOB);
                        mAdShowPriority.add(info);
                    }
                } else if (PreloadAdvertisement.AdShowPriority.MEDIUM.equals(adShowPriority)) {
                    String admobAdMediumId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_MEDIUM);
                    if (!TextUtils.isEmpty(admobAdMediumId)) {
                        AdIdInfo info = new AdIdInfo(admobAdMediumId, PreloadAdvertisement.AdShowPriority.MEDIUM, AdvertisementSwitcher.AD_ADMOB);
                        mAdShowPriority.add(info);
                    }
                } else if (PreloadAdvertisement.AdShowPriority.NORMAL.equals(adShowPriority)) {
                    String admobAdNormalId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_NORMAL);
                    if (!TextUtils.isEmpty(admobAdNormalId)) {
                        AdIdInfo info = new AdIdInfo(admobAdNormalId, PreloadAdvertisement.AdShowPriority.NORMAL, AdvertisementSwitcher.AD_ADMOB);
                        mAdShowPriority.add(info);
                    }
                }
            } else if (AdvertisementSwitcher.AD_ADMOB_ADX.equals(ad)) {
                if (PreloadAdvertisement.AdShowPriority.HIGH.equals(adShowPriority)) {
                    String admobAdxAdHighId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_ADX_HIGH);
                    if (!TextUtils.isEmpty(admobAdxAdHighId)) {
                        AdIdInfo info = new AdIdInfo(admobAdxAdHighId, PreloadAdvertisement.AdShowPriority.HIGH, AdvertisementSwitcher.AD_ADMOB_ADX);
                        mAdShowPriority.add(info);
                    }
                } else if (PreloadAdvertisement.AdShowPriority.MEDIUM.equals(adShowPriority)) {
                    String admobAdxAdMediumId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_ADX_MEDIUM);
                    if (!TextUtils.isEmpty(admobAdxAdMediumId)) {
                        AdIdInfo info = new AdIdInfo(admobAdxAdMediumId, PreloadAdvertisement.AdShowPriority.MEDIUM, AdvertisementSwitcher.AD_ADMOB_ADX);
                        mAdShowPriority.add(info);
                    }
                } else if (PreloadAdvertisement.AdShowPriority.NORMAL.equals(adShowPriority)) {
                    String admobAdxAdNormalId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_ADX_NORMAL);
                    if (!TextUtils.isEmpty(admobAdxAdNormalId)) {
                        AdIdInfo info = new AdIdInfo(admobAdxAdNormalId, PreloadAdvertisement.AdShowPriority.NORMAL, AdvertisementSwitcher.AD_ADMOB_ADX);
                        mAdShowPriority.add(info);
                    }
                }
            }
        }
    }

    private void loadFbAd(String key, boolean isBanner) {
        PreloadAdvertisement.AdTypeInfo adTypeInfo = new PreloadAdvertisement.AdTypeInfo();
        adTypeInfo.adType = AdvertisementSwitcher.AD_FACEBOOK;

        String fbAdHighId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_FACEBOOK_HIGH);
        String fbAdMediumId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_FACEBOOK_MEDIUM);
        String fbAdNormalId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_FACEBOOK_NORMAL);
        LogUtil.d(TAG, "AdId\nfbAdHighId:" + fbAdHighId + "\nfbAdMediumId:" + fbAdMediumId + "\nfbAdNormalId:" + fbAdNormalId);

        loadAd(fbAdHighId, key, isBanner, PreloadAdvertisement.AdShowPriority.HIGH, adTypeInfo);
        loadAd(fbAdMediumId, key, isBanner, PreloadAdvertisement.AdShowPriority.MEDIUM, adTypeInfo);
        loadAd(fbAdNormalId, key, isBanner, PreloadAdvertisement.AdShowPriority.NORMAL, adTypeInfo);
    }

    private void loadAdmobAd(String key, boolean isBanner, int admobType) {
        PreloadAdvertisement.AdTypeInfo adTypeInfo = new PreloadAdvertisement.AdTypeInfo();
        adTypeInfo.adType = AdvertisementSwitcher.AD_ADMOB;
        adTypeInfo.admobType = admobType;

        String admobAdHighId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_HIGH);
        String admobAdMediumId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_MEDIUM);
        String admobAdNormalId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_NORMAL);
        LogUtil.d(TAG, "AdId\nadmobAdHighId:" + admobAdHighId + "\nadmobAdMediumId:" + admobAdMediumId + "\nadmobAdNormalId:" + admobAdNormalId);

        loadAd(admobAdHighId, key, isBanner, PreloadAdvertisement.AdShowPriority.HIGH, adTypeInfo);
        loadAd(admobAdMediumId, key, isBanner, PreloadAdvertisement.AdShowPriority.MEDIUM, adTypeInfo);
        loadAd(admobAdNormalId, key, isBanner, PreloadAdvertisement.AdShowPriority.NORMAL, adTypeInfo);
    }

    private void loadAdmobAdxAd(String key, boolean isBanner, int admobAdxType) {
        PreloadAdvertisement.AdTypeInfo adTypeInfo = new PreloadAdvertisement.AdTypeInfo();
        adTypeInfo.adType = AdvertisementSwitcher.AD_ADMOB_ADX;
        adTypeInfo.admobAdxType = admobAdxType;

        String admobAdxAdHighId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_ADX_HIGH);
        String admobAdxAdMediumId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_ADX_MEDIUM);
        String admobAdxAdNormalId = GroupAdUtil.getBannerGroupIdByKey(GroupAdUtil.KEY_BANNER_GROUP_ADMOB_ADX_NORMAL);
        LogUtil.d(TAG, "AdId\nadmobAdxAdHighId:" + admobAdxAdHighId + "\nadmobAdxAdMediumId:" + admobAdxAdMediumId + "\nadmobAdxAdNormalId:" + admobAdxAdNormalId);

        loadAd(admobAdxAdHighId, key, isBanner, PreloadAdvertisement.AdShowPriority.HIGH, adTypeInfo);
        loadAd(admobAdxAdMediumId, key, isBanner, PreloadAdvertisement.AdShowPriority.MEDIUM, adTypeInfo);
        loadAd(admobAdxAdNormalId, key, isBanner, PreloadAdvertisement.AdShowPriority.NORMAL, adTypeInfo);
    }

    private PreloadAdvertisement loadAd(String adId, String key, boolean isBanner, String
            adShowPriority, PreloadAdvertisement.AdTypeInfo adTypeInfo) {
        PreloadAdvertisement preloadAdvertisement = null;
        try {
            if (!TextUtils.isEmpty(adId)) {
                if (mPreloadAdvertisementMap == null) {
                    mPreloadAdvertisementMap = new ConcurrentHashMap<>();
                }
                preloadAdvertisement = mPreloadAdvertisementMap.get(adId);
                if (preloadAdvertisement == null || (!preloadAdvertisement.isValid(true) && !preloadAdvertisement.isLoading())) {
                    preloadAdvertisement = new PreloadAdvertisement(TAG,
                            new MyPreloadAdvertisementAdapter(adId, adTypeInfo, adShowPriority, key, isBanner));
                    mPreloadAdvertisementMap.put(adId, preloadAdvertisement);
                    preloadAdvertisement.loadAd();
                } else {
                    if (preloadAdvertisement.isValid(true)) {
                        addLoadStateAdShowPrioritysMap(preloadAdvertisement, true);
                        boolean isShowGroupAd = isShowGroupAd(key, preloadAdvertisement);
                        if (isShowGroupAd && !mIsShowGroupAd) {
                            mIsShowGroupAd = true;
                            EventBus.getDefault().post(new EventGroupAdShow());
                        }
                    }
                }
            } else {
                if (adTypeInfo != null) {
                    String adType = adTypeInfo.adType;
                    if (!TextUtils.isEmpty(adType) && !TextUtils.isEmpty(adShowPriority)) {
                        Map<String, Boolean> adLoadedStateMap = mLoadedAdMep.get(adShowPriority);
                        if (adLoadedStateMap == null) {
                            adLoadedStateMap = new ConcurrentHashMap<>();
                        }
                        adLoadedStateMap.put(adType, false);
                        if (mLoadedAdMep == null) {
                            mLoadedAdMep = new ConcurrentHashMap<>();
                        }
                        mLoadedAdMep.put(adShowPriority, adLoadedStateMap);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "loadAd exception:" + e.getMessage());
        }
        return preloadAdvertisement;
    }

    private AdIdInfo getAndUpdateAdShowId(String key) {
        mCurrentShowAdIndex++;
        if (mCurrentShowAdIndex > 27) return null;
        if (mAdShowPriority == null) return null;
        AdIdInfo adIdInfo = mAdShowPriority.size() <= 0 ? null : mAdShowPriority.get(mCurrentShowAdIndex % mAdShowPriority.size());
        boolean isJump = isJump(adIdInfo, key);
//        LogUtil.d(TAG, "getAndUpdateAdShowId mCurrentShowAdIndex:" + mCurrentShowAdIndex + ",adType:" + adIdInfo != null ? adIdInfo.adType : "null"
//                + ",adShowPriority:" + adIdInfo != null ? adIdInfo.adShowPriority : "null" + ",isJump:" + isJump);
        if (isJump) {
            adIdInfo = getAndUpdateAdShowId(key);
        }
        return adIdInfo;
    }

    /**
     * 同一种广告只显示其中加载成功的最高级的广告
     */
    private boolean isJump(AdIdInfo adIdInfo, String key) {
        try {
            if (adIdInfo == null) return true;
            String adType = adIdInfo.adType;
            String adShowPriority = adIdInfo.adShowPriority;
            if (TextUtils.isEmpty(adShowPriority) || TextUtils.isEmpty(adType)) return false;
            if (mLoadedAdMep == null) return false;
            Map<String, Boolean> adLoadStateHighMap = mLoadedAdMep.get(PreloadAdvertisement.AdShowPriority.HIGH);
            Map<String, Boolean> adLoadStateMediumMap = mLoadedAdMep.get(PreloadAdvertisement.AdShowPriority.MEDIUM);
            Map<String, Boolean> adLoadStateNormalMap = mLoadedAdMep.get(PreloadAdvertisement.AdShowPriority.NORMAL);

            boolean isHighSuccess = adLoadStateHighMap != null && adLoadStateHighMap.get(adType) != null ? adLoadStateHighMap.get(adType) : false;
            boolean isMediumSuccess = adLoadStateMediumMap != null && adLoadStateMediumMap.get(adType) != null ? adLoadStateMediumMap.get(adType) : false;
            boolean isNormalSuccess = adLoadStateNormalMap != null && adLoadStateNormalMap.get(adType) != null ? adLoadStateNormalMap.get(adType) : false;
            if (PreloadAdvertisement.AdShowPriority.HIGH.equals(adShowPriority)) {
                if (isHighSuccess) {
                    return false;
                }
            } else if (PreloadAdvertisement.AdShowPriority.MEDIUM.equals(adShowPriority)) {
                if (isHighSuccess) {
                    return true;
                }
            } else if (PreloadAdvertisement.AdShowPriority.NORMAL.equals(adShowPriority)) {
                if (isHighSuccess || isMediumSuccess) {
                    return true;
                }
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "isJump e:" + e.getMessage());
        }
        return false;
    }

    /**
     * 判断当前加载完的是否是优先显示广告中的HIGH,如果不是HIGH则判断当前广告的其他优先级是否加载失败
     */
    private boolean isShowGroupAd(String key, PreloadAdvertisement preloadAdvertisement) {
        try {
            String adType = preloadAdvertisement.getAdapter().getAdTypeInfo().adType;
            String adShowPriority = preloadAdvertisement.getAdShowPriority();
            List<String> adPriority = AdvertisementSwitcher.getInstance().getAdPriority(key);
            int adShowPriorityIndex = 0;
            return isShow(adPriority, adShowPriorityIndex);
        } catch (Exception e) {
            LogUtil.d(TAG, "isShowGroupAd e:" + e.getMessage());
        }
        return false;
    }

    private boolean isShow(List<String> adPriority, int adShowPriorityIndex) {
        if (adShowPriorityIndex >= 3) return true;
        if (mLoadedAdMep == null) return false;
        Map<String, Boolean> adLoadStateMap = mLoadedAdMep.get(getPriorityStr(adShowPriorityIndex));
        if (adLoadStateMap == null) {
            return false;
        } else {
            for (String ad : adPriority) {
                if (adLoadStateMap.get(ad) == null) {//没有加载完成包括加载成功或者失败
                    return false;
                } else {
                    boolean isSuccess = adLoadStateMap.get(ad);
                    if (isSuccess) {
                        return true;
                    }
                }
            }

            if (adLoadStateMap.size() >= 3) {
                adShowPriorityIndex++;
                return isShow(adPriority, adShowPriorityIndex);
            }
        }
        return false;
    }

    private String getPriorityStr(int adShowPriorityIndex) {
        switch (adShowPriorityIndex) {
            case 0:
                return PreloadAdvertisement.AdShowPriority.HIGH;
            case 1:
                return PreloadAdvertisement.AdShowPriority.MEDIUM;
            case 2:
                return PreloadAdvertisement.AdShowPriority.NORMAL;
        }
        return "";
    }

    private void addLoadStateAdShowPrioritysMap(PreloadAdvertisement preloadAdvertisement,
                                                boolean isSuccess) {
        try {
            if (preloadAdvertisement != null) {
                String adType = preloadAdvertisement.getAdapter().getAdTypeInfo().adType;
                String adShowPriority = preloadAdvertisement.getAdShowPriority();
                if (mLoadedAdMep == null) {
                    mLoadedAdMep = new ConcurrentHashMap<>();
                }
                Map<String, Boolean> adLoadStateMap = mLoadedAdMep.get(adShowPriority);
                if (adLoadStateMap == null) {
                    adLoadStateMap = new ConcurrentHashMap<>();
                }
                adLoadStateMap.put(adType, isSuccess);
                mLoadedAdMep.put(adShowPriority, adLoadStateMap);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "addLoadSuccessAdShowPrioritys e:" + e.getMessage());
        }
    }

    private class MyPreloadAdvertisementAdapter extends BasePreloadAdvertisementAdapter {
        private String mKey;

        private MyPreloadAdvertisementAdapter(String adId, PreloadAdvertisement.AdTypeInfo adTypeInfo, String adShowPriority, String placementId, boolean isBanner) {
            super(adId, adTypeInfo, adShowPriority, placementId, isBanner);
            mKey = placementId;
        }

        @Override
        public void onAdLoaded(PreloadAdvertisement preloadAdvertisement) {
            if (mPreloadAdvertisementMap == null) {
                mPreloadAdvertisementMap = new ConcurrentHashMap<>();
            }

            if (preloadAdvertisement != null && preloadAdvertisement.getAdId() != null) {
                mPreloadAdvertisementMap.put(preloadAdvertisement.getAdId(), preloadAdvertisement);
            }

            addLoadStateAdShowPrioritysMap(preloadAdvertisement, true);

            if (isShowGroupAd(mKey, preloadAdvertisement) && !mIsShowGroupAd) {
                mIsShowGroupAd = true;
                EventBus.getDefault().post(new EventGroupAdShow());
            }
        }

        @Override
        public void onAdError(PreloadAdvertisement preloadAdvertisement) {
            super.onAdError(preloadAdvertisement);
            addLoadStateAdShowPrioritysMap(preloadAdvertisement, false);
            if (isShowGroupAd(mKey, preloadAdvertisement) && !mIsShowGroupAd) {
                mIsShowGroupAd = true;
                EventBus.getDefault().post(new EventGroupAdShow());
            }
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.facebook_native_ads_banner_call_flash : FacebookConstant.FB_AD_BIG_VIEW_RES_ID;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_flash : R.layout.layout_admob_advanced_content_ad_flash;
        }

        @Override
        public int getAdmobAdxViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_flash : R.layout.layout_admob_advanced_content_ad_flash;
        }
    }

    private class AdIdInfo {
        String adId;
        String adShowPriority;
        String adType;

        public AdIdInfo(String adId, String adShowPriority, String adType) {
            this.adId = adId;
            this.adShowPriority = adShowPriority;
            this.adType = adType;
        }
    }

}
