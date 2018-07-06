package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bednotdisturb;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.individual.sdk.AdListener;
import com.individual.sdk.AdType;
import com.individual.sdk.AerServAdContainer;
import com.individual.sdk.AolAdContainer;
import com.individual.sdk.BaseAdContainer;
import com.individual.sdk.InnerActiveAdContainer;
import com.individual.sdk.MobFoxAdContainer;
import com.individual.sdk.PubNativeAdContainer;
import com.individual.sdk.SmtAdContainer;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.cpm.CpmAdUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

/**
 * 封装简单的请求，只针对一个平台，随机获取id + 随机大小图
 * <p>
 * # 这里控制渠道的请求间隔，和请求id
 * <p>
 * Created by Sandy on 18/5/4.
 */

public class BedsideAdContainer implements AdListener {

    private boolean mIsRecordCount;
    private BaseAdContainer mLoadedCpmAd = null;
    private BaseAdContainer mDisplayCpmAd = null;

    private static final long CACHE_TIME = 15 * 60 * 1000;// 缓存有效时间:15min
    private long lastLoadedTime = 0L;
    private long lastLoadTime = 0L;

    private final BedsideIdManager mIdManager;
    private final AdType mAdType;
    private long TIME_OUT = 30000;// default:30s，渠道请求间隔

    public BedsideAdContainer(BedsideIdManager idManager, AdType adType, long loadTimeout, boolean isRecordCount) {
        this.mIdManager = idManager;
        this.mAdType = adType;
        this.TIME_OUT = loadTimeout;
        this.mIsRecordCount = isRecordCount;
        LogUtil.d("zzzz_night", mAdType + " init " + (mIdManager != null ? mIdManager.toString() : "null"));
    }

    public boolean isReady() {
        return mLoadedCpmAd != null && System.currentTimeMillis() - lastLoadedTime < CACHE_TIME;
    }

    public long getLoadedTime() {
        return lastLoadedTime;
    }

    public void destroy() {
        if (mDisplayCpmAd != null) {
            mDisplayCpmAd.destory();
            mDisplayCpmAd = null;
        }
        if (mLoadedCpmAd != null) {
            mLoadedCpmAd.destory();
            mLoadedCpmAd = null;
        }
    }

    public void resume() {
        if (mDisplayCpmAd != null) {
            mDisplayCpmAd.resume();
        }
    }

    public void pause() {
        if (mDisplayCpmAd != null) {
            mDisplayCpmAd.pause();
        }
    }

    /**
     * 当前渠道今日是否已经请求满
     *
     * @return
     */
    public boolean isMaxLoadToday() {
        if (mIdManager != null) {
            return mIdManager.isAllMaxLoadToday();
        }
        return true;
    }

    public boolean show(ViewGroup layout) {
        LogUtil.d("zzzz_night", mAdType + " show.");
        if (layout == null) {
            return false;
        }

        if (isReady()) {
            if (mDisplayCpmAd != null) {
                mDisplayCpmAd.destory();
                mDisplayCpmAd = null;
            }
            mDisplayCpmAd = mLoadedCpmAd;
            mLoadedCpmAd = null;

            LogUtil.d("zzzz_night", mAdType + " showSuccess!");
            CpmAdUtils.flurryCpmLog("BS_AN_SHOW", mAdType);
            return mDisplayCpmAd.show(layout);
        }
        return false;
    }

    private boolean isLoading = false;

    public void load(Context context) {
        //LogUtil.d(BedsideClockActivity.TAG, mAdType + " load=======");
        if (isLoading) {
            return;
        }
        isLoading = true;
        if (System.currentTimeMillis() - lastLoadTime <= TIME_OUT) {
            // skip: 没有满足渠道请求间隔
            isLoading = false;
            LogUtil.d("zzzz_night", mAdType + " load skip, cause not reach.");
            return;
        }
        if (isReady()) {
            isLoading = false;
            LogUtil.d("zzzz_night", mAdType + " load skip, cause is cached.");
            return;
        }

        if (mLoadedCpmAd != null) {
            mLoadedCpmAd.destory();
            mLoadedCpmAd = null;
        }

        BedsideIdBean id = getNextId();
        if (id != null) {
            LogUtil.d("zzzz_night", mAdType + " loadCpm: " + id.toString());
            BaseAdContainer adContainer = null;
            switch (mAdType) {
                case TYPE_IA:
                    adContainer = new InnerActiveAdContainer(context, id.getId());
                    break;
                case TYPE_AS:
                    if (context instanceof Activity) {
                        adContainer = new AerServAdContainer(context, id.getId());
                    }
                    break;
                case TYPE_SMT:
                    adContainer = new SmtAdContainer(context, id.getId(), CpmAdUtils.getSmtSize(id.getSizeType()));
                    break;
                case TYPE_PN:
                    adContainer = new PubNativeAdContainer(context, id.getId(), CpmAdUtils.getPnSize(id.getSizeType()));
                    break;
                case TYPE_MF:
                    int[] size = CpmAdUtils.getMfSize(id.getSizeType());
                    adContainer = new MobFoxAdContainer(context, id.getId(), size[0], size[1]);
                    break;
                case TYPE_AOL:
                    adContainer = new AolAdContainer(context, id.getId(), CpmAdUtils.getAolSize(id.getSizeType()));
                    break;
            }

            if (adContainer != null) {
                adContainer.setCallback(this);
                try {
                    adContainer.load();
                    if (mIsRecordCount) {
                        id.recordCount();// 对应id请求计次
                    }
                    CpmAdUtils.flurryCpmLog("BS_AN_LOAD", mAdType);
                    lastLoadTime = System.currentTimeMillis();
                    // load success
                    return;
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }

        // failed(LoadEnd)
        isLoading = false;
        LogUtil.d("zzzz_night", mAdType + " loadSkipFinal!");
    }

    private BedsideIdBean getNextId() {
        if (mIdManager != null) {
            return mIdManager.genarateNextId();
        }
        return null;
    }

    // //////////////////////
    // Ad Listener
    // //////////////////////

    @Override
    public void onAdLoaded(BaseAdContainer ad) {
        if (mLoadedCpmAd == null) {
            mLoadedCpmAd = ad;
            lastLoadedTime = System.currentTimeMillis();
            // success (LoadEnd)
            isLoading = false;
            CpmAdUtils.flurryCpmLog("BS_AN_LOADED", mAdType);
            LogUtil.d("zzzz_night", mAdType + " loadSuccess!");
            // 再回调到上层
            if (callback != null) {
                callback.onAdLoaded(this);
            }
        }
    }

    @Override
    public void onAdLoadFailed(BaseAdContainer ad, String errorMsg) {
        if (mDisplayCpmAd != ad) {// 鉴于展示时也可能回调onAdLoadFailed，这里仅对mLoadCpmAd做load回调
            // failed (LoadEnd)
            isLoading = false;
            LogUtil.d("zzzz_night", mAdType + " loadFailed: " + errorMsg);
        }
    }

    @Override
    public void onAdClick(BaseAdContainer ad) {
        LogUtil.d("zzzz_night", mAdType + " onAdClick");
        // 再回调到上层
        if (callback != null) {
            callback.onAdClick(ad);
        }
    }

    @Override
    public void onAdImpression(BaseAdContainer ad) {

    }


    // //////////////////////
    // Callback
    // //////////////////////
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onAdClick(BaseAdContainer ad);

        void onAdLoaded(BedsideAdContainer ad);
    }

}
