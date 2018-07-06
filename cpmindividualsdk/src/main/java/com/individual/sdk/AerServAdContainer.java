package com.individual.sdk;

import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;

import com.aerserv.sdk.AerServBanner;
import com.aerserv.sdk.AerServConfig;
import com.aerserv.sdk.AerServEvent;
import com.aerserv.sdk.AerServEventListener;

import java.util.List;

/**
 * AS
 * 注意事项：
 *  ① click事件非常不准，使用AS时切勿使用该回调。（OM测试点击后会进行跳转，但是回调不准确）
 *  ② 偶尔会发生ad已经展示，但是onAdImpression没有调用的情况
 *  ③ 测试期间display类型会有视频展示，是自动播放的，但是没有声音（声音这点不知道和广告内容有没有关系）
 *  ④ 生命周期可能是: onAdLoaded → show() → onAdLoadFail(Timeout trying toshow ad) → onAdImpression
 */
public class AerServAdContainer extends BaseAdContainer implements AerServEventListener {

    private AerServBanner mAsDisplayBanner = null;
    private AerServBanner mAsLoadingBanner = null;
    private AerServConfig mAsConfig = null;

    public AerServAdContainer(Context context, String adId) {
        super(context, adId);
    }

    @Override
    public AdType getAdType() {
        return AdType.TYPE_AS;
    }

    @Override
    public boolean show(ViewGroup layout) {
        if(layout == null) {
            return false;
        }

        if(mAsLoadingBanner != null && mAsLoadingBanner.isEnabled()) {
            if(mAsDisplayBanner != null) {
                mAsDisplayBanner.kill();
                mAsDisplayBanner = null;
            }
            mAsDisplayBanner = mAsLoadingBanner;
            mAsLoadingBanner = null;
            layout.removeAllViews();
            layout.addView(mAsDisplayBanner);
            mAsDisplayBanner.show();
            return true;
        }

        return false;
    }

    @Override
    protected boolean startLoad() {

        SdkLogger.e(TAG, "load(AS) :" + mId);
        if(mAsLoadingBanner != null) {
            mAsLoadingBanner.kill();
            mAsLoadingBanner = null;
        }

        mAsLoadingBanner = new AerServBanner(mContext);
        if(mAsConfig == null) {
            mAsConfig = new AerServConfig(mContext, mId);
            mAsConfig.setEventListener(this);
            mAsConfig.setRefreshInterval(mRefreshInterval);
            mAsConfig.setPreload(true);
            //mAsConfig.setDebug(true);
        }

        //mAsLoadingBanner.configure(mAsConfig).show();
        mAsLoadingBanner.configure(mAsConfig);
        return true;
    }

    @Override
    protected void onLoadTimeout() {
        // destroy timeout loadingAd
        if(mAsLoadingBanner != null) {
            mAsLoadingBanner.kill();
            mAsLoadingBanner = null;
        }
    }

    @Override
    protected boolean isInited() {
        return CpmGatherManager.isASInited();
    }

    private int mRefreshInterval = Integer.MAX_VALUE;

    /**
     * 设置自动刷新时间(默认为Integer.MAX_VALUE，即不自动刷新)
     * @param interval must be >= 10
     */
    public void setRefreshInterval(int interval) {
        mRefreshInterval = interval;
        if(mAsConfig != null) {
            mAsConfig.setRefreshInterval(mRefreshInterval);
        }
    }

    @Override
    public void resume() {
        if(mAsDisplayBanner != null) {
            mAsDisplayBanner.play();
            SdkLogger.i(TAG, "resume(AS)");
        }
    }

    @Override
    public void pause() {
        if(mAsDisplayBanner != null) {
            mAsDisplayBanner.pause();
            SdkLogger.i(TAG, "pause(AS)");
        }
    }

    @Override
    public void destory() {
        if(mAsDisplayBanner != null) {
            mAsDisplayBanner.kill();
            mAsDisplayBanner = null;
        }

        if(mAsLoadingBanner != null) {
            mAsLoadingBanner.kill();
            mAsLoadingBanner = null;
        }
        this.callback = null;
    }


    // /////////////////////////////
    // listener
    // /////////////////////////////
    @Override
    public void onAerServEvent(final AerServEvent aerServEvent, final List<Object> list) {
        // 回调是在线程里，这里需要抛到主线程
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (aerServEvent) {
                    // case AD_LOADED:
                    case PRELOAD_READY:
                        SdkLogger.e(TAG, "loadSuccess(AS) : " + aerServEvent.toString());
                        isLoading.set(false);
                        if(callback != null) {
                            callback.onAdLoaded(AerServAdContainer.this);
                        }
                        break;
                    case AD_FAILED:
                        String errorMsg = (list!=null && list.size()>0) ? (", " + list.get(0).toString()):"";
                        SdkLogger.i(TAG, "AD_FAILED(AS) : " + errorMsg);
                        isLoading.set(false);
                        if(callback != null) {
                            callback.onAdLoadFailed(AerServAdContainer.this,aerServEvent.toString() + errorMsg);
                        }
                        break;
                    case AD_CLICKED:
                        SdkLogger.i(TAG, "onAdClicked(AS)");
                        if(callback != null) {
                            callback.onAdClick(AerServAdContainer.this);
                        }
                        break;
                    case AD_IMPRESSION:
                        SdkLogger.i(TAG, "onAdImpression(AS)");
                        if(callback != null) {
                            callback.onAdImpression(AerServAdContainer.this);
                        }
                        break;
                    default:
                        SdkLogger.i(TAG, aerServEvent.toString() + "(AS) event fired with args: " + list.toString());
                        break;
                }
            }
        });
    }
}
