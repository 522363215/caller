package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duapps.ad.AdError;
import com.duapps.ad.DuAdListener;
import com.duapps.ad.DuNativeAd;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.glide.GlideHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

/**
 * Created by ChenR on 2017/9/29.
 */

public class DuAdsManager {
    private static final String TAG = "du_advertisment";

    private static DuAdsManager instance;

    private DuAdsManager() {
    }

    public static DuAdsManager getInstance() {
        if (instance == null) {
            synchronized (DuAdsManager.class) {
                if (instance == null) {
                    instance = new DuAdsManager();
                }
            }
        }
        return instance;
    }

    private View mRootView;
    private LinearLayout nativeAdContainer;
    private LinearLayout mDuAdsView;
    private DuNativeAd mDuNativeAd;
    private DuAdsAdapter mDuAdsAdapter;

    public boolean loadAds(final DuAdsAdapter duAdsAdapter) {
        if (duAdsAdapter == null) return false;

        this.mDuAdsAdapter = duAdsAdapter;
        mRootView = duAdsAdapter.getContextView();
        mDuNativeAd = new DuNativeAd(ApplicationEx.getInstance(), duAdsAdapter.getPid());
        mDuNativeAd.setMobulaAdListener(new DuAdListener() {

            @Override
            public void onError(DuNativeAd duNativeAd, AdError adError) {
                LogUtil.e(TAG, "DU Ads load error: " + adError.getErrorMessage() + ", error code: " + adError.getErrorCode());
                if (BuildConfig.DEBUG)
                    LogUtil.e(TAG, adError.getErrorMessage());
                duAdsAdapter.onAdError(adError);
            }

            @Override
            public void onAdLoaded(DuNativeAd duNativeAd) {
                if (duAdsAdapter.isBanner()) {
                    inflateBannerAd(duNativeAd);
                } else {
                    inflateBigAd(duNativeAd);
                }
                duAdsAdapter.onAdLoaded();
            }

            @Override
            public void onClick(DuNativeAd duNativeAd) {

            }
        });
        mDuNativeAd.load();
        return true;
    }

    private void inflateBannerAd(DuNativeAd nativeAd) {
        if (mRootView != null) {
            nativeAdContainer = (LinearLayout) mRootView.findViewById(R.id.nativeAdContainer);
            if (nativeAdContainer == null ) return;

            nativeAdContainer.removeAllViews();
            mDuAdsView = (LinearLayout) LayoutInflater.from(ApplicationEx.getInstance())
                    .inflate(mDuAdsAdapter.getDuViewResId(), nativeAdContainer, false);
            nativeAdContainer.addView(mDuAdsView);
            if (mDuAdsAdapter != null) mDuAdsAdapter.setDuLayout(mDuAdsView);

            ImageView nativeAdIcon = (ImageView) mDuAdsView.findViewById(R.id.nativeAdIcon);
            TextView nativeAdTitle = (TextView) mDuAdsView.findViewById(R.id.nativeAdTitle);
            TextView nativeAdBody = (TextView) mDuAdsView.findViewById(R.id.nativeAdBody);
            TextView nativeAdCallToAction = (TextView) mDuAdsView.findViewById(R.id.nativeAdCallToAction);

            nativeAdBody.setSelected(true);

            nativeAdTitle.setText(nativeAd.getTitle());
            nativeAdBody.setText(nativeAd.getShortDesc());
            nativeAdCallToAction.setText(nativeAd.getCallToAction());
            GlideHelper.with(ApplicationEx.getInstance()).load(nativeAd.getIconUrl()).into(nativeAdIcon);

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdBody);
            clickableViews.add(nativeAdCallToAction);
            clickableViews.add(nativeAdIcon);

            try {
                mDuNativeAd.registerViewForInteraction(mDuAdsView, clickableViews);
            } catch (Exception e) {
                LogUtil.error(e);
            }
            nativeAdContainer.setVisibility(View.VISIBLE);
        }
    }

    // 大图广告;
    private void inflateBigAd(DuNativeAd nativeAd) {
        if (mRootView != null) {
            nativeAdContainer = (LinearLayout) mRootView.findViewById(R.id.nativeAdContainer);
            if (nativeAdContainer == null) return;

            nativeAdContainer.removeAllViews();
            mDuAdsView = (LinearLayout) LayoutInflater.from(ApplicationEx.getInstance())
                    .inflate(mDuAdsAdapter.getDuViewResId(), nativeAdContainer, false);
            nativeAdContainer.addView(mDuAdsView);
            if (mDuAdsAdapter != null) mDuAdsAdapter.setDuLayout(mDuAdsView);

            ImageView nativeAdIcon = (ImageView) mDuAdsView.findViewById(R.id.nativeAdIcon);
            ImageView nativeAdImage = (ImageView) mDuAdsView.findViewById(R.id.nativeAdImage);
            TextView nativeAdTitle = (TextView) mDuAdsView.findViewById(R.id.nativeAdTitle);
            TextView nativeAdBody = (TextView) mDuAdsView.findViewById(R.id.nativeAdBody);
            TextView nativeAdCallToAction = (TextView) mDuAdsView.findViewById(R.id.nativeAdCallToAction);

            nativeAdBody.setSelected(true);

            nativeAdTitle.setText(nativeAd.getTitle());
            nativeAdBody.setText(nativeAd.getShortDesc());
            nativeAdCallToAction.setText(nativeAd.getCallToAction());
            GlideHelper.with(ApplicationEx.getInstance()).load(nativeAd.getIconUrl()).into(nativeAdIcon);
            GlideHelper.with(ApplicationEx.getInstance()).load(nativeAd.getImageUrl()).into(nativeAdImage);

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdBody);
            clickableViews.add(nativeAdImage);
            clickableViews.add(nativeAdCallToAction);
            clickableViews.add(nativeAdIcon);

            try {
                mDuNativeAd.registerViewForInteraction(nativeAdContainer, clickableViews);
            } catch (Exception e) {
                LogUtil.error(e);
            }
            nativeAdContainer.setVisibility(View.VISIBLE);
        }
    }
}