package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.view.View;

import com.duapps.ad.AdError;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

/**
 * Created by ChenR on 2017/9/30.
 */

public class DuAdsAdapter {
    private View mRootView;
    private View mDuLayout;
    private int pid;
    private boolean isBanner;

    public DuAdsAdapter (View context, int pid, boolean isBanner) {
        this.mRootView = context;
        this.pid = pid;
        this.isBanner = isBanner;
    }

    public View getContextView() {
        return mRootView;
    }

    public int getPid() {
        return pid;
    }

    public boolean isBanner() {
        return isBanner;
    }

    public View getDuLayout() {
        return mDuLayout;
    }

    public void setDuLayout(View mDuLayout) {
        this.mDuLayout = mDuLayout;
    }

    protected int getDuViewResId() {
        return isBanner() ? R.layout.layout_du_ad_banner : R.layout.layout_du_ad_big;
    }

    protected void onAdLoaded() {};

    // load admob or facebook ads
    public void onAdError(AdError error) {}

    public void onAdClick() {}
}
