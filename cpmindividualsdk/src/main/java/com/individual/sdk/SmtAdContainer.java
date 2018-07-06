package com.individual.sdk;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.smaato.soma.AdDimension;
import com.smaato.soma.AdDownloaderInterface;
import com.smaato.soma.AdListenerInterface;
import com.smaato.soma.BannerStateListener;
import com.smaato.soma.BannerView;
import com.smaato.soma.BaseView;
import com.smaato.soma.ReceivedBannerInterface;
import com.smaato.soma.bannerutilities.constant.BannerStatus;

/**
 * SMT
 * 注意事项：
 *  ① 没有impression事件监听，show到impression之间会有一定时间
 *  ② AdDimension一定要与id后台配置的size相同，否则无法正常展示
 */
public class SmtAdContainer extends BaseAdContainer implements AdListenerInterface, BannerStateListener {

    private long publisherId;
    private long adSpaceId;

    private BannerView mLoadBannerView;
    private BannerView mDisplayBannerView;

    /**
     * Available values are:
     *     DEFAULT (320 x 50)
     *     LEADERBOARD (728 x 90)
     *     MEDIUMRECTANGLE (300 x 250)
     *     SKYSCRAPER (120 x 600)
     */
    private AdDimension adSize = null;//AdDimension.DEFAULT;
    private int height = -2;
    private int width = -2;

    public SmtAdContainer(Context context, String adId, AdDimension size) {
        super(context, adId);
        String id = CpmGatherManager.getMetaDataFromInteger(context, "com.individual.sdk.SMT_APP_ID");
        try {
            publisherId = Long.valueOf(id);
        }catch (Exception e) {
            publisherId = -1;
        }

        try {
            adSpaceId = Long.valueOf(adId);
        }catch (Exception e) {
            adSpaceId = -1;
        }

        this.adSize = size;
    }

    @Override
    public AdType getAdType() {
        return AdType.TYPE_SMT;
    }

    @Override
    public boolean show(ViewGroup layout) {
        if(layout == null) {
            return false;
        }

        if(mLoadBannerView != null) {
            if(mDisplayBannerView != null) {
                mDisplayBannerView.destroy();
                mDisplayBannerView = null;
            }
            mDisplayBannerView = mLoadBannerView;
            mLoadBannerView = null;
            layout.removeAllViews();

            if(layout instanceof FrameLayout) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
                lp.gravity = Gravity.CENTER;
                layout.addView(mDisplayBannerView, lp);
            }else if(layout instanceof RelativeLayout) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
                layout.addView(mDisplayBannerView, lp);
            }else if(layout instanceof LinearLayout) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
                lp.gravity = Gravity.CENTER;
                layout.addView(mDisplayBannerView, lp);
            }else {
                layout.addView(mDisplayBannerView);
            }

            return true;
        }
        return false;
    }

    @Override
    protected boolean startLoad() {

        if(publisherId == -1) {
            isLoading.set(false);
            if(callback != null) callback.onAdLoadFailed(this, "SMT publisherId is invalid.");
            return false;
        }

        if(adSpaceId == -1) {
            isLoading.set(false);
            if(callback != null) callback.onAdLoadFailed(this, "empty id.");
            return false;
        }

        SdkLogger.e(TAG, "load(SMT) , spaceId:" + adSpaceId + ", publishId:" + publisherId);
        if(mLoadBannerView != null) {
            mLoadBannerView.destroy();
            mLoadBannerView = null;
        }

        mLoadBannerView = new BannerView(mContext);
        // 双id都为0，则为测试
        mLoadBannerView.getAdSettings().setPublisherId(publisherId);
        mLoadBannerView.getAdSettings().setAdspaceId(adSpaceId);
        // 设置广告尺寸
        if(adSize != null) {
            mLoadBannerView.getAdSettings().setAdDimension(adSize);
        }
        // 是否自动刷新
        mLoadBannerView.setAutoReloadEnabled(enableAutoReload);
        //刷新周期，单位秒（最大600，超出会被强制设为10）
        mLoadBannerView.setAutoReloadFrequency(mRefreshInterval);
        mLoadBannerView.setBannerStateListener(this);
        mLoadBannerView.addAdListener(this);

        // do load
        mLoadBannerView.asyncLoadNewBanner();
        return true;
    }

    @Override
    protected void onLoadTimeout() {
        if(mLoadBannerView != null) {
            mLoadBannerView.destroy();
            mLoadBannerView = null;
        }
    }

    @Override
    protected boolean isInited() {
        return CpmGatherManager.isSmtInited();
    }

    private int mRefreshInterval = 600;
    private boolean enableAutoReload = false;

    /**
     * 是否开启自动间隔刷新（不设置则默认关闭）
     * @param isEnable
     */
    public void setAutoReloadEnabled(boolean isEnable) {
        enableAutoReload = isEnable;
        if(mLoadBannerView != null) {
            mLoadBannerView.setAutoReloadEnabled(enableAutoReload);
        }
    }

    /**
     * 设置自动间隔刷新时间
     * @param interval 10~600，单位秒
     */
    public void setRefreshInterval(int interval) {
        this.mRefreshInterval = interval;
        if(mLoadBannerView != null) {
            mLoadBannerView.setAutoReloadFrequency(mRefreshInterval);
        }
    }


    @Override
    public void resume() {
        // do nothing
    }

    @Override
    public void pause() {
        // do nothing
    }

    @Override
    public void destory() {

        if(mLoadBannerView != null) {
            mLoadBannerView.destroy();
            mLoadBannerView = null;
        }

        if(mDisplayBannerView != null) {
            mDisplayBannerView.destroy();
            mDisplayBannerView = null;
        }
        this.callback = null;
    }

    // /////////////////////
    // listenr
    // /////////////////////
    @Override
    public void onReceiveAd(AdDownloaderInterface adDownloaderInterface, ReceivedBannerInterface receivedBannerInterface) {
        isLoading.set(false);
        if(receivedBannerInterface.getStatus() == BannerStatus.ERROR){
            // Banner download failed
            SdkLogger.e(TAG, "AD_FAILED(SMT) : " + receivedBannerInterface.getErrorMessage());
            if (callback != null) {
                callback.onAdLoadFailed(this, receivedBannerInterface.getErrorMessage());
            }
        } else {
            // Banner download succeeded
            SdkLogger.e(TAG, "loadSuccess(SMT)");
            if(mLoadBannerView != null) {
                // 只有load后的这一次success算作loaded（onWillOpenLandingPage后也会回调downloadSuccess）
                if (callback != null) {
                    callback.onAdLoaded(this);
                }
            }
        }
    }

    @Override
    public void onWillOpenLandingPage(BaseView baseView) {
        // 点击后展开页面
        SdkLogger.e(TAG, "onWillOpenLandingPage(SMT)");
        if(callback != null) {
            callback.onAdClick(this);
        }
    }

    @Override
    public void onWillCloseLandingPage(BaseView baseView) {
        // 页面关闭
        SdkLogger.e(TAG, "onWillOpenLandingPage(SMT)");
    }
}
