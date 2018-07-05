package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

/**
 * Created by zhq on 2018/4/17.
 */
public class InterstitialAdvertisement {
    private static final String TAG = "InterstitialAdvertisement";
    private static final long OUT_DATE_TIME = 50 * 60 * 1000;
    private List<String> mAdShowPriority;//广告显示的优先级，high,medium,normal
    private InterstitialAdShowListener mInterstitialAdShowListener;
    private InterstitialAdLoadedListener mInterstitialAdLoadedListener;
    private InterstitialAdmobAdListener mInterstitialAdmobAdListener;
    private InterstitialAdmobAdxAdListener mInterstitialAdmobAdxAdListener;
    private InterstitialFbAdListener mInterstitialFbAdListener;
    private List<String> mAdKeyPriority;//广告key 的优先级顺序
    private int mCurrentAdLoadIndex = -1;//current ad index for this placement
    private int mCurrentAdShowIndex = -1;//current ad index for this placement
    private int mCurrentAdValidIndex = -1;//判断广告是否有效的当前index
    private String mCurrentLoadingAdPriority;
    private String mCurrentShowAdPriority;
    private String mCurrentValidAdPriority;
    private Context mContext;
    private String mFbAdIdHigh;
    private String mFbAdIdMedium;
    private String mFbAdIdNormal;

    private String mAdmobAdIdHigh;
    private String mAdmobAdIdMedium;
    private String mAdmobAdIdNormal;

    private String mAdmobAdxIdHigh;
    private String mAdmobAdxIdMedium;
    private String mAdmobAdxIdNormal;

    private Map<String, Long> mInterstitialAdLoadedTimeMap;//广告加载完的时间，key为广告ID

    private Map<String, Map<String, Boolean>> mAdLoadStateMap;//key为优先级（high、medium、normal），value为adType对应的广告是否加载成功

    private Map<String, InterstitialAd> mFbAdMap;//fb插屏，key为显示的优先级
    private Map<String, com.google.android.gms.ads.InterstitialAd> mAdmobAdMap;
    private Map<String, com.google.android.gms.ads.InterstitialAd> mAdmobAdxMap;

    private boolean mIsShowAd;
    private boolean mIsLoading;

    public InterstitialAdvertisement(Context context, FbAdId fbAdId, AdmobAdId admobAdId, AdmobAdxId admobAdxId, String placementId) {
        mContext = context;

        mFbAdIdHigh = fbAdId.highId;
        mFbAdIdMedium = fbAdId.mediumId;
        mFbAdIdNormal = fbAdId.normalId;

        mAdmobAdIdHigh = admobAdId.highId;
        mAdmobAdIdMedium = admobAdId.mediumId;
        mAdmobAdIdNormal = admobAdId.normalId;

        mAdmobAdxIdHigh = admobAdxId.highId;
        mAdmobAdxIdMedium = admobAdxId.mediumId;
        mAdmobAdxIdNormal = admobAdxId.normalId;

        mInterstitialAdLoadedTimeMap = new ConcurrentHashMap<>();

        mAdLoadStateMap = new ConcurrentHashMap<>();

        mFbAdMap = new ConcurrentHashMap<>();
        mAdmobAdMap = new ConcurrentHashMap<>();
        mAdmobAdxMap = new ConcurrentHashMap<>();

        mAdKeyPriority = AdvertisementSwitcher.getInstance().getInterstitialAdPriority(placementId);
        mAdShowPriority = getAdLoadPriority();
        LogUtil.d(TAG, "mAdPriority mAdPriority:" + mAdKeyPriority != null ? String.valueOf(mAdKeyPriority) : "null");
        LogUtil.d(TAG, "AdId\nmFbAdIdHigh:" + mFbAdIdHigh + "\nmFbAdIdMedium:" + mFbAdIdMedium + "\nmFbAdIdNormal:" + mFbAdIdNormal
                + "\n\nmAdmobAdIdHigh:" + mAdmobAdIdHigh + "\nmAdmobAdIdMedium:" + mAdmobAdIdMedium + "\nmAdmobAdIdNormal:" + mAdmobAdIdNormal
                + "\n\nmAdmobAdxIdHigh:" + mAdmobAdxIdHigh + "\nmAdmobAdxIdMedium:" + mAdmobAdxIdMedium + "\nmAdmobAdxIdNormal:" + mAdmobAdxIdNormal);
    }

    private List<String> getAdLoadPriority() {
        List<String> list = new ArrayList<>();
        list.add(InterstitialAdPriority.HIGH);
        list.add(InterstitialAdPriority.MEDIUM);
        list.add(InterstitialAdPriority.NORMAL);
        return list;
    }

    public void loadAd(InterstitialAdLoadedListener interstitialAdLoadedListener) {
        mInterstitialAdLoadedListener = interstitialAdLoadedListener;
        refreshAd(true);
    }

    public boolean show(InterstitialAdShowListener interstitialAdClosedListener) {
        mInterstitialAdShowListener = interstitialAdClosedListener;
        return show(true);
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    /**
     * 只要high 的没有加载出来或者超时就算是无效
     */
    public boolean isValid(boolean resetRequestIndex) {
        if (resetRequestIndex) {
            mCurrentAdValidIndex = -1;
        }
//        mCurrentValidAdPriority = getAndUpdateValidAdPriority();
        mCurrentValidAdPriority = InterstitialAdPriority.HIGH;
        for (String ad : mAdKeyPriority) {
            if (AdvertisementSwitcher.AD_FACEBOOK.equals(ad)) {
                InterstitialAd interstitialFbAd = null;
                if (mFbAdMap != null) {
                    interstitialFbAd = mFbAdMap.get(mCurrentValidAdPriority);
                }
                if (interstitialFbAd != null && interstitialFbAd.isAdLoaded()) {
                    String adId = interstitialFbAd.getPlacementId();
                    boolean isOutDate = (System.currentTimeMillis() - mInterstitialAdLoadedTimeMap.get(adId)) > OUT_DATE_TIME;
                    if (isOutDate) {
                        return false;
                    } else {
                        LogUtil.d(TAG, "last loaded fb is valid,do not reload  fb adId:" + adId);
                        return true;
                    }
                }
            } else if (AdvertisementSwitcher.AD_ADMOB.equals(ad)) {
                com.google.android.gms.ads.InterstitialAd interstitialAdmobAd = null;
                if (mAdmobAdMap != null) {
                    interstitialAdmobAd = mAdmobAdMap.get(mCurrentValidAdPriority);
                }
                if (interstitialAdmobAd != null && interstitialAdmobAd.isLoaded()) {
                    String adId = interstitialAdmobAd.getAdUnitId();
                    boolean isOutDate = (System.currentTimeMillis() - mInterstitialAdLoadedTimeMap.get(adId)) > OUT_DATE_TIME;
                    if (isOutDate) {
                        return false;
                    } else {
                        LogUtil.d(TAG, "last loaded admob ad is valid,do not reload  admob adId:" + adId);
                        return true;
                    }
                }
            } else if (AdvertisementSwitcher.AD_ADMOB_ADX.equals(ad)) {
                com.google.android.gms.ads.InterstitialAd interstitialAdmobAdxAd = null;
                if (mAdmobAdxMap != null) {
                    interstitialAdmobAdxAd = mAdmobAdxMap.get(mCurrentValidAdPriority);
                }
                if (interstitialAdmobAdxAd != null && interstitialAdmobAdxAd.isLoaded()) {
                    String adId = interstitialAdmobAdxAd.getAdUnitId();
                    boolean isOutDate = (System.currentTimeMillis() - mInterstitialAdLoadedTimeMap.get(interstitialAdmobAdxAd.getAdUnitId())) > OUT_DATE_TIME;
                    if (isOutDate) {
                        return false;
                    } else {
                        LogUtil.d(TAG, "last loaded admob adx ad is valid,do not reload  admob adx adId:" + adId);
                        return true;
                    }
                }
            }
        }

//        if (mCurrentAdValidIndex < mAdShowPriority.size()) {
//            isValid(false);
//        }
        return false;
    }

    private boolean show(boolean resetRequestIndex) {
        if (resetRequestIndex) {
            mCurrentAdShowIndex = -1;
        }
        mCurrentShowAdPriority = getAndUpdateShowAdPriority();
        for (String ad : mAdKeyPriority) {
            if (AdvertisementSwitcher.AD_FACEBOOK.equals(ad)) {
                InterstitialAd interstitialFbAd = null;
                if (mFbAdMap != null) {
                    interstitialFbAd = mFbAdMap.get(mCurrentShowAdPriority);
                }
                if (interstitialFbAd != null && interstitialFbAd.isAdLoaded()) {
                    if (mInterstitialAdShowListener != null) {
                        mInterstitialAdShowListener.onAdShow();
                    }
                    interstitialFbAd.show();
                    mIsShowAd = true;
                    LogUtil.d(TAG, "showInterstitialAd FB Priority:" + mCurrentShowAdPriority);
                    return true;
                }
            } else if (AdvertisementSwitcher.AD_ADMOB.equals(ad)) {
                com.google.android.gms.ads.InterstitialAd interstitialAdmobAd = null;
                if (mAdmobAdMap != null) {
                    interstitialAdmobAd = mAdmobAdMap.get(mCurrentShowAdPriority);
                }
                if (interstitialAdmobAd != null && interstitialAdmobAd.isLoaded()) {
                    if (mInterstitialAdShowListener != null) {
                        mInterstitialAdShowListener.onAdShow();
                    }
                    interstitialAdmobAd.show();
                    mIsShowAd = true;
                    LogUtil.d(TAG, "showInterstitialAd admob Priority:" + mCurrentShowAdPriority);
                    return true;
                }
            } else if (AdvertisementSwitcher.AD_ADMOB_ADX.equals(ad)) {
                com.google.android.gms.ads.InterstitialAd interstitialAdmobAdxAd = null;
                if (mAdmobAdxMap != null) {
                    interstitialAdmobAdxAd = mAdmobAdxMap.get(mCurrentShowAdPriority);
                }
                if (interstitialAdmobAdxAd != null && interstitialAdmobAdxAd.isLoaded()) {
                    if (mInterstitialAdShowListener != null) {
                        mInterstitialAdShowListener.onAdShow();
                    }
                    interstitialAdmobAdxAd.show();
                    mIsShowAd = true;
                    LogUtil.d(TAG, "showInterstitialAd admob adx Priority:" + mCurrentShowAdPriority);
                    return true;
                }
            }
        }
        if (mCurrentAdShowIndex >= mAdShowPriority.size()) {
            if (isAllAdRequsetComplete()) {
                LogUtil.d(TAG, "showInterstitialAd none");
                if (mInterstitialAdShowListener != null) {
                    mInterstitialAdShowListener.onAdError();
                }
            }
            return false;
        } else {
            return show(false);
        }
    }

    private void refreshAd(boolean resetRequestIndex) {
        if (resetRequestIndex) {
            mCurrentAdLoadIndex = -1;
        }
        mCurrentLoadingAdPriority = getAndUpdateLoadAdPrority();
        if (mCurrentLoadingAdPriority.equals(InterstitialAdPriority.HIGH)) {
            loadAllHighAd();
        } else if (mCurrentLoadingAdPriority.equals(InterstitialAdPriority.MEDIUM)) {
            loadAllMediumAd();
        } else if (mCurrentLoadingAdPriority.equals(InterstitialAdPriority.NORMAL)) {
            loadAllNormalAd();
        }
    }

    private void loadAllHighAd() {
        mIsLoading = true;
        for (String ad : mAdKeyPriority) {
            if (AdvertisementSwitcher.AD_FACEBOOK.equals(ad)) {
                loadAd(mFbAdIdHigh, AdvertisementSwitcher.AD_FACEBOOK, InterstitialAdPriority.HIGH);
            } else if (AdvertisementSwitcher.AD_ADMOB.equals(ad)) {
                loadAd(mAdmobAdIdHigh, AdvertisementSwitcher.AD_ADMOB, InterstitialAdPriority.HIGH);
            } else if (AdvertisementSwitcher.AD_ADMOB_ADX.equals(ad)) {
                loadAd(mAdmobAdxIdHigh, AdvertisementSwitcher.AD_ADMOB_ADX, InterstitialAdPriority.HIGH);
            }
        }
    }

    private void loadAllMediumAd() {
        for (String ad : mAdKeyPriority) {
            if (AdvertisementSwitcher.AD_FACEBOOK.equals(ad)) {
                loadAd(mFbAdIdMedium, AdvertisementSwitcher.AD_FACEBOOK, InterstitialAdPriority.MEDIUM);
            } else if (AdvertisementSwitcher.AD_ADMOB.equals(ad)) {
                loadAd(mAdmobAdIdMedium, AdvertisementSwitcher.AD_ADMOB, InterstitialAdPriority.MEDIUM);
            } else if (AdvertisementSwitcher.AD_ADMOB_ADX.equals(ad)) {
                loadAd(mAdmobAdxIdMedium, AdvertisementSwitcher.AD_ADMOB_ADX, InterstitialAdPriority.MEDIUM);
            }
        }
    }

    private void loadAllNormalAd() {
        for (String ad : mAdKeyPriority) {
            if (AdvertisementSwitcher.AD_FACEBOOK.equals(ad)) {
                loadAd(mFbAdIdNormal, AdvertisementSwitcher.AD_FACEBOOK, InterstitialAdPriority.NORMAL);
            } else if (AdvertisementSwitcher.AD_ADMOB.equals(ad)) {
                loadAd(mAdmobAdIdNormal, AdvertisementSwitcher.AD_ADMOB, InterstitialAdPriority.NORMAL);
            } else if (AdvertisementSwitcher.AD_ADMOB_ADX.equals(ad)) {
                loadAd(mAdmobAdxIdNormal, AdvertisementSwitcher.AD_ADMOB_ADX, InterstitialAdPriority.NORMAL);
            }
        }
    }

    private void loadAd(String adId, String adType, String priority) {
        if (TextUtils.isEmpty(adId)) {
            onAdLoadFailed(priority, adType);
            return;
        }
        if (AdvertisementSwitcher.AD_FACEBOOK.equals(adType)) {
            InterstitialAd interstitialAdFB = new InterstitialAd(mContext, adId);
            mInterstitialFbAdListener = new InterstitialFbAdListener(interstitialAdFB, priority, adId);
            interstitialAdFB.setAdListener(mInterstitialFbAdListener);
            interstitialAdFB.loadAd();
        } else if (AdvertisementSwitcher.AD_ADMOB.equals(adType)) {
            com.google.android.gms.ads.InterstitialAd interstitialAd = new com.google.android.gms.ads.InterstitialAd(mContext);
            interstitialAd.setAdUnitId(adId);
            mInterstitialAdmobAdListener = new InterstitialAdmobAdListener(interstitialAd, priority, adId);
            interstitialAd.setAdListener(mInterstitialAdmobAdListener);
            interstitialAd.loadAd(AdTestManager.getAdRequestBuilder().build());
        } else if (AdvertisementSwitcher.AD_ADMOB_ADX.equals(adType)) {
            com.google.android.gms.ads.InterstitialAd interstitialAd = new com.google.android.gms.ads.InterstitialAd(mContext);
            interstitialAd.setAdUnitId(adId);
            mInterstitialAdmobAdxAdListener = new InterstitialAdmobAdxAdListener(interstitialAd, priority, adId);
            interstitialAd.setAdListener(mInterstitialAdmobAdxAdListener);
            interstitialAd.loadAd(AdTestManager.getAdRequestBuilder().build());
        }
    }

    private class InterstitialAdmobAdListener extends AdListener {
        private String priority;
        private com.google.android.gms.ads.InterstitialAd interstitialAdmobAd;
        private String adId;

        public InterstitialAdmobAdListener(com.google.android.gms.ads.InterstitialAd interstitialAd, String priority, String adId) {
            this.interstitialAdmobAd = interstitialAd;
            this.priority = priority;
            this.adId = adId;
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            LogUtil.d(TAG, "loadInterstitialAdAdmob onAdClosed priority:" + priority);
            if (mInterstitialAdShowListener != null) {
                mInterstitialAdShowListener.onAdClosed();
            }
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            if (interstitialAdmobAd != null && interstitialAdmobAd.isLoaded()) {
                LogUtil.d(TAG, "loadInterstitialAdAdmob onAdLoaded priority:" + priority + ",adId:" + adId);
                mAdmobAdMap.put(priority, interstitialAdmobAd);
                addAdLoadStateMap(AdvertisementSwitcher.AD_ADMOB, priority, true);
                mInterstitialAdLoadedTimeMap.put(adId, System.currentTimeMillis());
                if (isAllAdRequestCompleteForPriority(priority)) {
                    mIsLoading = false;
                }
                if (mInterstitialAdLoadedListener != null && !mIsShowAd) {
                    mInterstitialAdLoadedListener.onAdLoaded(InterstitialAdvertisement.this);
                }
            } else {
                addAdLoadStateMap(AdvertisementSwitcher.AD_ADMOB, priority, false);
            }
        }


        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            onAdLoadFailed(priority, AdvertisementSwitcher.AD_ADMOB);
        }
    }

    public class InterstitialAdmobAdxAdListener extends AdListener {
        private String adId;
        private String priority;
        private com.google.android.gms.ads.InterstitialAd interstitialAdmobAdx;

        public InterstitialAdmobAdxAdListener(com.google.android.gms.ads.InterstitialAd interstitialAdmobAdx, String priority, String adId) {
            this.interstitialAdmobAdx = interstitialAdmobAdx;
            this.priority = priority;
            this.adId = adId;
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            LogUtil.d(TAG, "loadInterstitialAdAdmobAdx onAdClosed priority:" + priority);
            if (mInterstitialAdShowListener != null) {
                mInterstitialAdShowListener.onAdClosed();
            }
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            if (interstitialAdmobAdx != null && interstitialAdmobAdx.isLoaded()) {
                LogUtil.d(TAG, "loadInterstitialAdAdmobAdx onAdLoaded priority:" + priority + ",adId:" + adId);
                mAdmobAdxMap.put(priority, interstitialAdmobAdx);
                addAdLoadStateMap(AdvertisementSwitcher.AD_ADMOB_ADX, priority, true);
                mInterstitialAdLoadedTimeMap.put(adId, System.currentTimeMillis());
                if (isAllAdRequestCompleteForPriority(priority)) {
                    mIsLoading = false;
                }
                if (mInterstitialAdLoadedListener != null && !mIsShowAd) {
                    mInterstitialAdLoadedListener.onAdLoaded(InterstitialAdvertisement.this);
                }
            } else {
                addAdLoadStateMap(AdvertisementSwitcher.AD_ADMOB_ADX, priority, false);
            }
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            onAdLoadFailed(priority, AdvertisementSwitcher.AD_ADMOB_ADX);
        }
    }

    public class InterstitialFbAdListener implements InterstitialAdListener {
        private String adId;
        public String priority;//该广告显示的优先级
        private InterstitialAd interstitialAdFB;

        public InterstitialFbAdListener(InterstitialAd interstitialAdFB, String priority, String adId) {
            this.interstitialAdFB = interstitialAdFB;
            this.priority = priority;
            this.adId = adId;
        }

        @Override
        public void onInterstitialDisplayed(Ad ad) {
            LogUtil.d(TAG, "loadInterstitialFBAd onInterstitialDisplayed  priority:" + priority);
        }

        @Override
        public void onInterstitialDismissed(Ad ad) {
            LogUtil.d(TAG, "loadInterstitialFBAd onInterstitialDismissed  priority:" + priority);
            if (mInterstitialAdShowListener != null) {
                mInterstitialAdShowListener.onAdClosed();
            }
        }

        @Override
        public void onError(Ad ad, AdError adError) {
            LogUtil.d("loadInterstitialFBAd advertisement", "loadInterstitialFBAd onError e:" + adError.getErrorMessage());
            onAdLoadFailed(priority, AdvertisementSwitcher.AD_FACEBOOK);
        }

        @Override
        public void onAdLoaded(Ad ad) {
            if (interstitialAdFB != null && interstitialAdFB.isAdLoaded()) {
                LogUtil.d(TAG, "loadInterstitialFBAd onAdLoaded priority:" + priority + ",adId:" + adId);
                addAdLoadStateMap(AdvertisementSwitcher.AD_FACEBOOK, priority, true);
                mFbAdMap.put(priority, interstitialAdFB);
                mInterstitialAdLoadedTimeMap.put(adId, System.currentTimeMillis());
                if (isAllAdRequestCompleteForPriority(priority)) {
                    mIsLoading = false;
                }
                if (mInterstitialAdLoadedListener != null && !mIsShowAd) {
                    mInterstitialAdLoadedListener.onAdLoaded(InterstitialAdvertisement.this);
                }
            } else {
                addAdLoadStateMap(AdvertisementSwitcher.AD_FACEBOOK, priority, false);
            }
        }

        @Override
        public void onAdClicked(Ad ad) {

        }

        @Override
        public void onLoggingImpression(Ad ad) {

        }
    }

    private void onAdLoadFailed(String priority, String adType) {
        addAdLoadStateMap(adType, priority, false);
        boolean isAllAdFailedForPriority = isAllAdLoadFailedForPriority(priority);
        LogUtil.d(TAG, "loadInterstitialAd onAdFailed adType:" + adType + ",priority:" + priority + ",isAllAdFailedForPriority:" + isAllAdFailedForPriority);
        if (isAllAdFailedForPriority) {
            mCurrentLoadingAdPriority = "";
            refreshAd(false);
        } else {
            if (isAllAdRequestCompleteForPriority(priority)) {
                mIsLoading = false;
            }
        }
    }

    private void addAdLoadStateMap(String adType, String priority, boolean isSuccess) {
        if (mAdLoadStateMap == null) {
            mAdLoadStateMap = new ConcurrentHashMap<>();
        }
        Map<String, Boolean> adLoadStateMap = mAdLoadStateMap.get(priority);
        if (adLoadStateMap == null) {
            adLoadStateMap = new ConcurrentHashMap<>();
        }
        adLoadStateMap.put(adType, isSuccess);
        mAdLoadStateMap.put(priority, adLoadStateMap);
    }

    private boolean isAllAdLoadFailedForPriority(String priority) {
        if (mAdLoadStateMap == null) return false;
        Map<String, Boolean> adLoadStateMap = mAdLoadStateMap.get(priority);
        if (adLoadStateMap != null && adLoadStateMap.size() >= 3) {
            for (Boolean boo : adLoadStateMap.values()) {
                if (boo != null && boo) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean isAllAdRequestCompleteForPriority(String priority) {
        if (mAdLoadStateMap == null) return false;
        Map<String, Boolean> adLoadStateMap = mAdLoadStateMap.get(priority);
        if (adLoadStateMap != null && adLoadStateMap.size() >= 3) {
            return true;
        }
        return false;
    }

    /**
     * 判断所有广告是否请求完毕，包括加载失败和加载成功的
     */
    private boolean isAllAdRequsetComplete() {
        if (mAdLoadStateMap != null) {
            Map<String, Boolean> adLoadStateHighMap = mAdLoadStateMap.get(InterstitialAdPriority.HIGH);
            Map<String, Boolean> adLoadStateMediumMap = mAdLoadStateMap.get(InterstitialAdPriority.MEDIUM);
            Map<String, Boolean> adLoadStateNormalMap = mAdLoadStateMap.get(InterstitialAdPriority.NORMAL);
            if (adLoadStateHighMap != null && adLoadStateHighMap.size() >= 3
                    && adLoadStateMediumMap != null && adLoadStateMediumMap.size() >= 3
                    && adLoadStateNormalMap != null && adLoadStateNormalMap.size() >= 3) {
                return true;
            }
        }
        return false;
    }

    private String getAndUpdateLoadAdPrority() {
        mCurrentAdLoadIndex++;
        return mCurrentAdLoadIndex >= mAdShowPriority.size() ? "" : mAdShowPriority.get(mCurrentAdLoadIndex % mAdShowPriority.size());
    }

    private String getAndUpdateShowAdPriority() {
        mCurrentAdShowIndex++;
        return mCurrentAdShowIndex >= mAdShowPriority.size() ? "" : mAdShowPriority.get(mCurrentAdShowIndex % mAdShowPriority.size());
    }

    private String getAndUpdateValidAdPriority() {
        mCurrentAdValidIndex++;
        return mCurrentAdValidIndex >= mAdShowPriority.size() ? "" : mAdShowPriority.get(mCurrentAdValidIndex % mAdShowPriority.size());
    }

    public static class FbAdId {
        String highId;//显示优先级最高
        String mediumId;//显示优先级次之
        String normalId;//显示优先级最低
    }

    public static class AdmobAdId {
        String highId;//显示优先级最高
        String mediumId;//显示优先级次之
        String normalId;//显示优先级最低
    }

    public static class AdmobAdxId {
        String highId;//显示优先级最高
        String mediumId;//显示优先级次之
        String normalId;//显示优先级最低
    }

    /**
     * 某一类插屏广告显示的优先级
     */
    public class InterstitialAdPriority {
        public static final String HIGH = "HIGH";
        public static final String MEDIUM = "MEDIUM";
        public static final String NORMAL = "NORMAL";
    }

    public class AdType {
        public static final String FACEBOOK = "facebook";
        public static final String ADMOB = "admob";
        public static final String ADMOB_ADX = "admob_adx";
    }

    public interface InterstitialAdShowListener {
        void onAdClosed();

        void onAdShow();

        void onAdError();
    }

    public interface InterstitialAdLoadedListener {
        /**
         * 由于加载和显示可能不在同一个界面上，加载完插屏后保存InterstitialAdvertisement，当显示时取出来直接用
         */
        void onAdLoaded(InterstitialAdvertisement interstitialAdvertisement);
    }

}
