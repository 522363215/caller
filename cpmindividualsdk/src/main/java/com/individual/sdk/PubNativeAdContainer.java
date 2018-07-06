package com.individual.sdk;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import net.pubnative.lite.sdk.api.BannerRequestManager;
import net.pubnative.lite.sdk.api.MRectRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenter;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenter;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenterFactory;

/**
 *
 */
public class PubNativeAdContainer extends BaseAdContainer implements RequestManager.RequestListener, MRectPresenter.Listener, BannerPresenter.Listener {

    private RequestManager mRequestManager;
    private MRectPresenter pnMRectPresenter;
    private BannerPresenter pnBannerPresenter;
    private View mLoadedAdView;

    public static final int SIZE_MRECT = 1;
    public static final int SIZE_BANNER = 2;
    private final int SIZE_TYPE;

    private final int RECT_WIDTH;
    private final int RECT_HEIGHT;
    private final int BANNER_WIDTH;
    private final int BANNER_HEIGHT;

    /**
     * @param context
     * @param adId
     * @param sizeType PubNativeAdContainer.SIZE_MRECT(300x250) or PubNativeAdContainer.SIZE_BANNER(320x50)
     */
    public PubNativeAdContainer(Context context, String adId, int sizeType) {
        super(context, adId);
        SIZE_TYPE = sizeType;

        float density = Resources.getSystem().getDisplayMetrics().density;
        RECT_WIDTH = Math.round(300 * density);
        RECT_HEIGHT = Math.round(250 * density);
        BANNER_WIDTH = Math.round(320 * density);
        BANNER_HEIGHT = Math.round(50 * density);
    }

    @Override
    public AdType getAdType() {
        return AdType.TYPE_PN;
    }

    @Override
    public boolean show(ViewGroup layout) {
        if(layout == null) {
            return false;
        }

        if(mLoadedAdView != null) {
            layout.removeAllViews();

            int height, width;
            if(SIZE_TYPE == SIZE_MRECT) {
                width = RECT_WIDTH;
                height = RECT_HEIGHT;
            }else if(SIZE_TYPE == SIZE_BANNER) {
                width = BANNER_WIDTH;
                height = BANNER_HEIGHT;
            }else {
                width = RECT_WIDTH;
                height = RECT_HEIGHT;
            }

            if(layout instanceof FrameLayout) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
                lp.gravity = Gravity.CENTER;
                layout.addView(mLoadedAdView, lp);
            }else if(layout instanceof RelativeLayout) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
                layout.addView(mLoadedAdView, lp);
            }else if(layout instanceof LinearLayout) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
                lp.gravity = Gravity.CENTER;
                layout.addView(mLoadedAdView, lp);
            }else {
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(width, height);
                layout.addView(mLoadedAdView, lp);
            }

            return true;
        }

        return false;
    }

    @Override
    protected boolean startLoad() {

        SdkLogger.e(TAG, "load(PN) :" + mId);
        if(mRequestManager != null) {
            mRequestManager.destroy();
            mRequestManager = null;
        }

        if(SIZE_TYPE == SIZE_MRECT) {
            mRequestManager = new MRectRequestManager();
            mRequestManager.setZoneId(mId);
            mRequestManager.setRequestListener(this);
            mRequestManager.requestAd();
            return true;
        }else if(SIZE_TYPE == SIZE_BANNER) {
            mRequestManager = new BannerRequestManager();
            mRequestManager.setZoneId(mId);
            mRequestManager.setRequestListener(this);
            mRequestManager.requestAd();
            return true;
        }else {
            isLoading.set(false);
            if(callback != null) callback.onAdLoadFailed(this, "invalid size.");
            return false;
        }
    }

    @Override
    protected void onLoadTimeout() {

        // destroy timeout loadingAd
        if(mRequestManager != null) {
            mRequestManager.destroy();
            mRequestManager = null;
        }
        if (pnMRectPresenter != null) {
            pnMRectPresenter.destroy();
            pnMRectPresenter = null;
        }
        if(pnBannerPresenter != null) {
            pnBannerPresenter.destroy();
            pnBannerPresenter = null;
        }
    }

    @Override
    protected boolean isInited() {
        return CpmGatherManager.isPnInited();
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destory() {
        if(mRequestManager != null) {
            mRequestManager.destroy();
            mRequestManager = null;
        }
        if(pnMRectPresenter != null) {
            pnMRectPresenter.destroy();
            pnMRectPresenter = null;
        }
        if(pnBannerPresenter != null) {
            pnBannerPresenter.destroy();
            pnBannerPresenter = null;
        }
        mLoadedAdView = null;
        this.callback = null;
    }


    // //////////////////////
    // listenr
    // //////////////////////

    private void onFailed(String msg) {
        SdkLogger.e(TAG, "AD_FAILED(PN) : " + msg);
        isLoading.set(false);
        if(callback != null) {
            callback.onAdLoadFailed(this, msg);
        }
    }

    private void onAdLoaded(View view) {
        // REAL LOADED(同等于Impression？)
        SdkLogger.e(TAG, "loadSuccess(PN)");
        isLoading.set(false);
        mLoadedAdView = view;
        if(callback != null) {
            callback.onAdLoaded(this);
        }
    }

    @Override
    public void onRequestSuccess(Ad ad) {
        if(SIZE_TYPE == SIZE_MRECT) {
            if (pnMRectPresenter != null) {
                pnMRectPresenter.destroy();
                pnMRectPresenter = null;
            }
            pnMRectPresenter = new MRectPresenterFactory(mContext).createMRectPresenter(ad, this);
            if (pnMRectPresenter != null) {
                pnMRectPresenter.load();
            } else {
                // load fail
                onFailed("The received ad format is not supported by the MRect presenter");
            }
        }else if(SIZE_TYPE == SIZE_BANNER) {
            if (pnBannerPresenter != null) {
                pnBannerPresenter.destroy();
                pnBannerPresenter = null;
            }
            pnBannerPresenter = new BannerPresenterFactory(mContext).createBannerPresenter(ad, this);
            if (pnBannerPresenter != null) {
                pnBannerPresenter.load();
            } else {
                // load fail
                onFailed("The received ad format is not supported by the Banner presenter");
            }
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        // load fail
        onFailed(throwable.getMessage());
    }

    // ///////////////
    // MRect回调
    // //////////////

    @Override
    public void onMRectLoaded(MRectPresenter mRectPresenter, View view) {
        onAdLoaded(view);
    }

    @Override
    public void onMRectClicked(MRectPresenter mRectPresenter) {
        SdkLogger.i(TAG, "onAdClicked(PN)");
        if(callback != null) {
            callback.onAdClick(this);
        }
    }

    @Override
    public void onMRectError(MRectPresenter mRectPresenter) {
        // load fail
        onFailed("onMRectError");
    }

    // ///////////////
    // Banner回调
    // //////////////

    @Override
    public void onBannerLoaded(BannerPresenter bannerPresenter, View view) {
        onAdLoaded(view);
    }

    @Override
    public void onBannerClicked(BannerPresenter bannerPresenter) {
        SdkLogger.i(TAG, "onAdClicked(PN)");
        if(callback != null) {
            callback.onAdClick(this);
        }
    }

    @Override
    public void onBannerError(BannerPresenter bannerPresenter) {
        // load fail
        onFailed("onBannerError");
    }
}
