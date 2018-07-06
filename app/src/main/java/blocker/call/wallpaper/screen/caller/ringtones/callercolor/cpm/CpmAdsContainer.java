package blocker.call.wallpaper.screen.caller.ringtones.callercolor.cpm;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.aerserv.sdk.AerServBanner;
import com.aerserv.sdk.AerServConfig;
import com.aerserv.sdk.AerServEvent;
import com.aerserv.sdk.AerServEventListener;
import com.flurry.android.FlurryAgent;
import com.fyber.inneractive.sdk.external.InneractiveAdRequest;
import com.fyber.inneractive.sdk.external.InneractiveAdSpot;
import com.fyber.inneractive.sdk.external.InneractiveAdSpotManager;
import com.fyber.inneractive.sdk.external.InneractiveAdViewEventsListener;
import com.fyber.inneractive.sdk.external.InneractiveAdViewUnitController;
import com.fyber.inneractive.sdk.external.InneractiveErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;


/**
 * cpmSdk渠道管理类
 * load一次只对应一个渠道进行加载，并提供其生命周期，仅供一个页面使用
 * Created by Sandy on 18/4/14.
 */

public class CpmAdsContainer {

    private static final String TAG = "cid_" + "_TestCpm";
    private boolean isLoading = false;
    private final Activity mAct;
    private final Handler mHandler;
    private final String PrId;

    public CpmAdsContainer(Activity act, String PrId) {
        this.mAct = act;
        this.PrId = PrId;
        mHandler = new Handler();
    }

    public void load(TestCpmId randomIdBean) {
        if(isLoading) {
            return;
        }
        isLoading = true;
        if(mAct == null) {
            isLoading = false;
            throw new NullPointerException("AdsContainer load with null context");
        }
        if(randomIdBean == null) {
            isLoading = false;
            throw new NullPointerException("AdsContainer load with empty id");
        }

        startLoad(mAct, randomIdBean.generateId());
    }

    private void startLoad(Activity act, IdBean idBean) {
        if(idBean == null || TextUtils.isEmpty(idBean.getId())) {
            isLoading = false;
            if(callback != null)
                callback.onAdLoadFailed("empty id");
            return;
        }

        switch (idBean.getAdType()) {
            case IdBean.PR_IA:
                loadIA(idBean.getId());
                break;
            case IdBean.PR_AS:
                loadAS(act, idBean.getId());
                break;
        }

        Map<String, String> params = new HashMap<>();
        params.put("Position", PrId);
        params.put("type", idBean.getAdType());
        FlurryAgent.logEvent("TestLoad", params);

    }

    public void destory() {
        this.callback = null;
        if(mDisplaySpot != null) {
            mDisplaySpot.destroy();
            mDisplaySpot = null;
        }
        if(mLoadSpot != null) {
            mLoadSpot.destroy();
            mLoadSpot = null;
        }

        if(mAsDisplayBanner != null) {
            mAsDisplayBanner.kill();
        }
    }

    /**
     * @param layout view container
     * @return
     */
    public boolean show(ViewGroup layout) {
        layout.removeAllViews();

        // try show IA
        if(mDisplaySpot != null) {
            mDisplaySpot.destroy();
            mDisplaySpot = null;
        }
        if(mLoadSpot != null && mLoadSpot.isReady()) {
            try {
                mDisplaySpot = mLoadSpot;
                mLoadSpot = null;
                // getting the spot's controller
                InneractiveAdViewUnitController controller = (InneractiveAdViewUnitController) mDisplaySpot.getSelectedUnitController();
                controller.setEventsListener(iaViewEventListner);
                // showing the ad
                controller.bindView(layout);
                return true;
            }catch (Exception e) {
            }
        }

        // try show AS
        if(mAsDisplayBanner != null && mAsDisplayBanner.isEnabled()) {
            layout.addView(mAsDisplayBanner);
            mAsDisplayBanner.show();
            return true;
        }

        return false;
    }

    public void pause() {
        if(mAsDisplayBanner != null) {
            mAsDisplayBanner.pause();
            LogUtil.i(TAG, "pause(AS)");
        }
    }

    public void resume() {
        if(mAsDisplayBanner != null) {
            mAsDisplayBanner.play();
            LogUtil.i(TAG, "resume(AS)");
        }
    }


    // ///////////////////////////
    // IA
    // ///////////////////////////

    InneractiveAdSpot mLoadSpot = null;
    InneractiveAdSpot mDisplaySpot = null;

    private void loadIA(String spotId) {
        LogUtil.e(TAG, "load(IA) :" + spotId);
        // spot integration for display Square
        mLoadSpot = InneractiveAdSpotManager.get().createSpot();
        // adding the adview controller
        InneractiveAdViewUnitController controller = new InneractiveAdViewUnitController();
        mLoadSpot.addUnitController(controller);
        // add listener
        mLoadSpot.setRequestListener(spotListener);


        InneractiveAdRequest adRequest = new InneractiveAdRequest(spotId);
        //when ready to perform the ad request
        mLoadSpot.requestAd(adRequest);
    }

    // display Square callbacks
    InneractiveAdSpot.RequestListener spotListener = new InneractiveAdSpot.RequestListener() {

        @Override
        public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot, InneractiveErrorCode errorCode) {
            LogUtil.i(TAG, "Failed loading Square! with error(IA): " + errorCode);
            isLoading = false;
            if (callback != null) {
                callback.onAdLoadFailed(errorCode.toString());
            }
        }

        @Override
        public void onInneractiveSuccessfulAdRequest(InneractiveAdSpot adSpot) {
            LogUtil.e(TAG, "loadSuccess(IA)");
            isLoading = false;
            if (callback != null) {
                callback.onAdLoaded();
            }

            Map<String, String> params = new HashMap<>();
            params.put("Position", PrId);
            params.put("type", IdBean.PR_IA);
            FlurryAgent.logEvent("TestLoaded", params);
        }
    };

    InneractiveAdViewEventsListener iaViewEventListner = new InneractiveAdViewEventsListener() {
        @Override
        public void onAdImpression(InneractiveAdSpot adSpot) {
            //LogUtil.i(TAG, "onAdImpression(IA)");
        }

        @Override
        public void onAdClicked(InneractiveAdSpot adSpot) {
            LogUtil.i(TAG, "onAdClicked(IA)");
            if(callback != null) {
                callback.onAdClick();
            }
        }
        @Override
        public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
            //LogUtil.i(TAG, "onAdWillCloseInternalBrowser(IA)");
        }
        @Override
        public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
            //LogUtil.i(TAG, "onAdWillOpenExternalApp(IA)");
        }
        @Override
        public void onAdExpanded(InneractiveAdSpot adSpot) {
            //LogUtil.i(TAG, "onAdExpanded(IA)");
        }
        @Override
        public void onAdResized(InneractiveAdSpot adSpot) {
            // Relevant only for MRaid units
            //LogUtil.i(TAG, "onAdResized(IA)");
        }
        @Override
        public void onAdCollapsed(InneractiveAdSpot adSpot) {
            //LogUtil.i(TAG, "onAdCollapsed(IA)");
        }
    };


    // ///////////////////////////
    // AS
    // ///////////////////////////

    private AerServBanner mAsDisplayBanner = null;
    private AerServConfig mAsConfig = null;

    private void loadAS(Activity activity, String id) {
        LogUtil.e(TAG, "load(AS) :" + id);
        if(mAsDisplayBanner == null) {
            mAsDisplayBanner = new AerServBanner(mAct);
        }
        if(mAsConfig == null) {
            mAsConfig = new AerServConfig(activity, id);
            mAsConfig.setEventListener(asListener);
            mAsConfig.setRefreshInterval(Integer.MAX_VALUE);
            mAsConfig.setPreload(true);
            //mAsConfig.setDebug(true);
        }
        //mAsDisplayBanner.configure(mAsConfig).show();
        mAsDisplayBanner.configure(mAsConfig);
    }

    private AerServEventListener asListener = new AerServEventListener() {
        @Override
        public void onAerServEvent(final AerServEvent aerServEvent, final List<Object> list) {
            // 回调是在线程里，这里需要抛到主线程
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch (aerServEvent) {
//                        case AD_LOADED:
                        case PRELOAD_READY:
                            LogUtil.e(TAG, "loadSuccess(AS) : " + aerServEvent.toString());
                            isLoading = false;
                            if(callback != null) {
                                callback.onAdLoaded();
                            }

                            Map<String, String> params = new HashMap<>();
                            params.put("Position", PrId);
                            params.put("type", IdBean.PR_AS);
                            FlurryAgent.logEvent("TestLoaded", params);
                            break;
                        case AD_FAILED:
                            LogUtil.i(TAG, "AD_FAILED(AS)");
                            isLoading = false;
                            if(callback != null) {
                                String errorMsg = (list!=null && list.size()>0) ? (", " + list.get(0).toString()):"";
                                callback.onAdLoadFailed(aerServEvent.toString() + errorMsg);
                            }
                            break;
                        case AD_CLICKED:
                            LogUtil.i(TAG, "onAdClicked(AS)");
                            if(callback != null) {
                                callback.onAdClick();
                            }
                            break;
                        default:
                            LogUtil.i(TAG, aerServEvent.toString() + "(AS) event fired with args: " + list.toString());
                            break;
                    }
                }
            });

        }
    };



    // ///////////////////////////
    // 回调
    // ///////////////////////////

    private Callback callback;

    public void setCallback(Callback call) {
        this.callback = call;
    }

    public interface Callback {
        void onAdLoaded();
        void onAdLoadFailed(String errorMsg);
        void onAdClick();
    }

}
