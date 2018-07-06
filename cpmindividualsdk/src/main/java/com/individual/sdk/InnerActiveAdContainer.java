package com.individual.sdk;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.fyber.inneractive.sdk.external.InneractiveAdRequest;
import com.fyber.inneractive.sdk.external.InneractiveAdSpot;
import com.fyber.inneractive.sdk.external.InneractiveAdSpotManager;
import com.fyber.inneractive.sdk.external.InneractiveAdViewEventsListener;
import com.fyber.inneractive.sdk.external.InneractiveAdViewUnitController;
import com.fyber.inneractive.sdk.external.InneractiveErrorCode;

public class InnerActiveAdContainer extends BaseAdContainer implements InneractiveAdViewEventsListener, InneractiveAdSpot.RequestListener {

    InneractiveAdSpot mLoadSpot = null;
    InneractiveAdSpot mDisplaySpot = null;

    public InnerActiveAdContainer(Context context, String adId) {
        super(context, adId);
    }

    @Override
    public AdType getAdType() {
        return AdType.TYPE_IA;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
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
    }

    @Override
    protected boolean startLoad() {

        SdkLogger.e(TAG, "load(IA) :" + mId);
        if(mLoadSpot != null) {
            mLoadSpot.destroy();
            mLoadSpot = null;
        }
        // spot integration for display Square
        mLoadSpot = InneractiveAdSpotManager.get().createSpot();
        // adding the adview controller
        InneractiveAdViewUnitController controller = new InneractiveAdViewUnitController();
        mLoadSpot.addUnitController(controller);
        // add listener
        mLoadSpot.setRequestListener(this);


        InneractiveAdRequest adRequest = new InneractiveAdRequest(mId);
        //when ready to perform the ad request
        mLoadSpot.requestAd(adRequest);
        return true;
    }

    @Override
    protected void onLoadTimeout() {
        // destroy timeout loadingAd
        if(mLoadSpot != null) {
            mLoadSpot.destroy();
            mLoadSpot = null;
        }
    }

    @Override
    protected boolean isInited() {
        return CpmGatherManager.isIAInited();
    }

    @Override
    public boolean show(ViewGroup layout) {
        if(layout == null) {
            return false;
        }

        if(mLoadSpot != null && mLoadSpot.isReady()) {
            layout.removeAllViews();
            if(mDisplaySpot != null) {
                // 释放前一个正在展示的广告对象，再展示新的广告对象
                mDisplaySpot.destroy();
                mDisplaySpot = null;
            }
            try {
                mDisplaySpot = mLoadSpot;
                mLoadSpot = null;
                // getting the spot's controller
                InneractiveAdViewUnitController controller = (InneractiveAdViewUnitController) mDisplaySpot.getSelectedUnitController();
                controller.setEventsListener(this);
                // showing the ad
                controller.bindView(layout);
                return true;
            }catch (Exception e) {
            }
        }
        return false;
    }

    // ///////////////////
    // Listeners
    // ///////////////////

    @Override
    public void onInneractiveFailedAdRequest(InneractiveAdSpot adSpot, InneractiveErrorCode errorCode) {
        SdkLogger.i(TAG, "Failed loading Square! with error(IA): " + errorCode);
        isLoading.set(false);
        if (callback != null) {
            callback.onAdLoadFailed(this, errorCode.toString());
        }
    }

    @Override
    public void onInneractiveSuccessfulAdRequest(InneractiveAdSpot adSpot) {
        SdkLogger.e(TAG, "loadSuccess(IA)");
        isLoading.set(false);
        if (callback != null) {
            callback.onAdLoaded(this);
        }
    }


    @Override
    public void onAdImpression(InneractiveAdSpot adSpot) {
        SdkLogger.i(TAG, "onAdImpression(IA)");
        if(callback != null) {
            callback.onAdImpression(InnerActiveAdContainer.this);
        }
    }

    @Override
    public void onAdClicked(InneractiveAdSpot adSpot) {
        SdkLogger.i(TAG, "onAdClicked(IA)");
        if(callback != null) {
            callback.onAdClick(InnerActiveAdContainer.this);
        }
    }
    @Override
    public void onAdWillCloseInternalBrowser(InneractiveAdSpot adSpot) {
        //SdkLogger.i(TAG, "onAdWillCloseInternalBrowser(IA)");
    }
    @Override
    public void onAdWillOpenExternalApp(InneractiveAdSpot adSpot) {
        //SdkLogger.i(TAG, "onAdWillOpenExternalApp(IA)");
    }
    @Override
    public void onAdExpanded(InneractiveAdSpot adSpot) {
        //SdkLogger.i(TAG, "onAdExpanded(IA)");
    }
    @Override
    public void onAdResized(InneractiveAdSpot adSpot) {
        // Relevant only for MRaid units
        //SdkLogger.i(TAG, "onAdResized(IA)");
    }
    @Override
    public void onAdCollapsed(InneractiveAdSpot adSpot) {
        //SdkLogger.i(TAG, "onAdCollapsed(IA)");
    }

}
