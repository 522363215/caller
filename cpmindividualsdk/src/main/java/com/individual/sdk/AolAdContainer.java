package com.individual.sdk;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.millennialmedia.InlineAd;

public class AolAdContainer extends BaseAdContainer implements InlineAd.InlineListener{

    private InlineAd inlineAd;
    private int refreshInterval = 0;
    private InlineAd.InlineAdMetadata inlineAdMetadata;
    private FrameLayout adContainer;

    /**
     *
     * @param context
     * @param adId
     * @param adSize

     *
     */
    public AolAdContainer(Context context, String adId, InlineAd.AdSize adSize) {
        super(context, adId);
        if (adSize == null) {
            inlineAdMetadata = new InlineAd.InlineAdMetadata().
                    setAdSize(InlineAd.AdSize.BANNER);
        } else {
            inlineAdMetadata = new InlineAd.InlineAdMetadata().
                    setAdSize(adSize);
        }

        initAdContainer();
    }

    @Override
    public AdType getAdType() {
        return AdType.TYPE_AOL;
    }
    @Override
    public boolean show(ViewGroup layout) {
        if(layout == null) {
            return false;
        }

        layout.removeAllViews();
        layout.addView(adContainer);

        return true;
    }

    /**
     * 设置自动刷新时间(默认为0，即不自动刷新)
     * @param interval
     * Sets the refresh interval in milliseconds.
    If the value is less than the allowed minimum and greater than 0 then the minimum value will be used.
    Set to 0 to disable refreshing.
     */
    public void setRefreshInterval(int interval) {
        refreshInterval = interval;
    }


    private void initAdContainer() {
        adContainer = new FrameLayout(mContext.getApplicationContext());
        FrameLayout.LayoutParams viewPream =new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );//设置布局控件的属性
        viewPream.gravity = Gravity.CENTER;
        adContainer.setLayoutParams(viewPream);
    }

    @Override
    protected boolean startLoad() {

        SdkLogger.e(TAG, "load(Aol) , adid: = " + mId);
        if(inlineAd != null) {
            inlineAd.destroy();
            inlineAd = null;
        }

        try {
            inlineAd = InlineAd.createInstance(mId, adContainer);
            inlineAd.setListener(this);
            inlineAd.setRefreshInterval(refreshInterval);
            inlineAd.request(inlineAdMetadata);
            return true;
        } catch (Exception e) {
            isLoading.set(false);
            if(callback != null) callback.onAdLoadFailed(this, e.getMessage());
            return false;
        }
    }

    @Override
    protected void onLoadTimeout() {
        // destroy timeout ad
        if(inlineAd != null) {
            inlineAd.destroy();
            inlineAd = null;
        }
    }

    @Override
    protected boolean isInited() {
        return CpmGatherManager.isAolInited();
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destory() {
        if(inlineAd != null) {
            inlineAd.destroy();
            inlineAd = null;
        }
        this.callback = null;
    }

    @Override
    public void onRequestSucceeded(InlineAd inlineAd) {
        // 点击广告后回调
        SdkLogger.e(TAG, "onRequestSucceeded(AOL)");
        isLoading.set(false);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(callback != null) callback.onAdLoaded(AolAdContainer.this);
            }
        });
    }

    @Override
    public void onRequestFailed(InlineAd inlineAd,final InlineAd.InlineErrorStatus inlineErrorStatus) {
        // 点击广告后回调
        SdkLogger.e(TAG, "onRequestFailed(AOL), msg = " + inlineErrorStatus.getDescription());
        isLoading.set(false);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(callback != null) callback.onAdLoadFailed(AolAdContainer.this, inlineErrorStatus.getDescription());
            }
        });

    }

    @Override
    public void onClicked(InlineAd inlineAd) {
        // 点击广告后回调
        SdkLogger.e(TAG, "onClicked(AOL)");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(callback != null) callback.onAdClick(AolAdContainer.this);
            }
        });

    }

    @Override
    public void onResize(InlineAd inlineAd, int i, int i1) {

    }

    @Override
    public void onResized(InlineAd inlineAd, int i, int i1, boolean b) {

    }

    @Override
    public void onExpanded(InlineAd inlineAd) {

    }

    @Override
    public void onCollapsed(InlineAd inlineAd) {

    }

    @Override
    public void onAdLeftApplication(InlineAd inlineAd) {
        // 点击广告后离开应用
        SdkLogger.e(TAG, "onAdLeftApplication(AOL)");
    }
}
