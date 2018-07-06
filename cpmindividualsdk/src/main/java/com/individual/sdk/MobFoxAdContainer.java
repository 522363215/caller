package com.individual.sdk;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mobfox.sdk.tags.BannerTag;


public class MobFoxAdContainer extends BaseAdContainer implements BannerTag.Listener {
    private BannerTag loadBanner;
    private BannerTag displayBanner;
    private int width;
    private int height;

    public MobFoxAdContainer(Context context, String adId, int width, int height) {
        super(context, adId);
        if (width<=0) {
            this.width = 300;
        } else {
            this.width = width;
        }
        if (height<=0) {
            this.height = 250;
        } else {
            this.height = height;
        }

    }
    @Override
    public AdType getAdType() {
        return AdType.TYPE_MF;
    }

    @Override
    public boolean show(ViewGroup layout) {
        if(layout == null) {
            return false;
        }

        if(loadBanner != null) {
            if(displayBanner != null) {
                displayBanner.destroy();
                displayBanner = null;
            }
            displayBanner = loadBanner;
            loadBanner = null;

            layout.removeAllViews();
            layout.addView(displayBanner);
            return true;
        }

        return false;
    }

    @Override
    protected boolean startLoad() {

        SdkLogger.e(TAG, "load(MF) :" + mId);
        if(loadBanner != null) {
            loadBanner.destroy();
            loadBanner = null;
        }
        loadBanner = new BannerTag(mContext, width, height, mId);
        loadBanner.setListener(this);
        loadBanner.load();
        return true;
    }

    @Override
    protected void onLoadTimeout() {
        // destroy timeout loadingAd
        if(loadBanner != null) {
            loadBanner.destroy();
            loadBanner = null;
        }
    }

    @Override
    protected boolean isInited() {
        // MF不需要项目初始化
        return true;
    }

    @Override
    public void resume() {
        if (displayBanner != null)
            displayBanner.onResume();
    }

    @Override
    public void pause() {
        if (displayBanner != null)
            displayBanner.onPause();
    }
    @Override
    public void destory() {
        if(loadBanner != null) {
            loadBanner.destroy();
            loadBanner = null;
        }
        if (displayBanner != null) {
            displayBanner.destroy();
            displayBanner = null;
        }

        this.callback = null;
    }

    @Override
    public void onBannerError(View view, String s) {
        SdkLogger.e(TAG, "onBannerError(MF)" + s);
        isLoading.set(false);
        if(callback != null) {
            callback.onAdLoadFailed(MobFoxAdContainer.this, s);
        }
    }

    @Override
    public void onBannerLoaded(View view) {
        SdkLogger.e(TAG, "onBannerLoaded(MF)");
        isLoading.set(false);
        if(callback != null) {
            callback.onAdLoaded(MobFoxAdContainer.this);
        }
    }

    @Override
    public void onBannerClosed(View view) {
        SdkLogger.e(TAG, "onBannerClosed(MF)");
    }

    @Override
    public void onBannerFinished() {
        SdkLogger.e(TAG, "onBannerFinished(MF)");
    }

    @Override
    public void onBannerClicked(View view) {
        SdkLogger.e(TAG, "onBannerClicked(MF)");
        if(callback != null) {
            callback.onAdClick(MobFoxAdContainer.this);
        }
    }

    @Override
    public void onNoFill(View view) {
        SdkLogger.e(TAG, "onNoFill(MF)");
        isLoading.set(false);
        if(callback != null) {
            callback.onAdLoadFailed(MobFoxAdContainer.this, "no fill");
        }
    }
//
//    @Override
//    public void onBannerError(View view, Exception e) {
//        SdkLogger.e(TAG, "onBannerError(MF)" + e.getMessage());
//        isLoading = false;
//        if(callback != null) {
//            callback.onAdLoadFailed(MobFoxAdContainer.this, e.getMessage());
//        }
//    }
//
//    @Override
//    public void onBannerLoaded(View view) {
//        SdkLogger.e(TAG, "onBannerLoaded(MF)");
//        isLoading = false;
//        if(callback != null) {
//            callback.onAdLoaded(MobFoxAdContainer.this);
//        }
//    }
//
//    @Override
//    public void onBannerClosed(View view) {
//        SdkLogger.e(TAG, "onBannerClosed(MF)");
//    }
//
//    @Override
//    public void onBannerFinished() {
//        SdkLogger.e(TAG, "onBannerFinished(MF)");
//    }
//
//    @Override
//    public void onBannerClicked(View view) {
//        SdkLogger.e(TAG, "onNoFill(MF)");
//        if(callback != null) {
//            callback.onAdClick(MobFoxAdContainer.this);
//        }
//    }
//
//    @Override
//    public void onNoFill(View view) {
//        SdkLogger.e(TAG, "onNoFill(MF)");
//        isLoading = false;
//        if(callback != null) {
//            callback.onAdLoadFailed(MobFoxAdContainer.this, "no fill");
//        }
//    }
}
