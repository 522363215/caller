package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

public class FacebookAdLoader {
    private final long DEFAULT_REFRESH_TIME = 2 * 60 * 1000;

    private long mRefreshTime = DEFAULT_REFRESH_TIME;
    private String mAdId;
    private int mResId;

    private NativeAd nativeAd;
    private LinearLayout nativeAdContainer;
    private LinearLayout adView;

    private WeakReference<Context> mContext;
    private View mView;
    private NativeAdListener mListener;

    private long mLastRefreshTime;

    private boolean mIsBanner = false;
    private View mDecorView;
    private String mPlacementId;
    private int mContainerId;
    private int mLayoutResId;

    private boolean isContactBig=false;

    public FacebookAdLoader(Context context, View view, String adId, int resId, boolean isContact) {
        mContext = new WeakReference<>(context);
        mView = view;
        mAdId = adId;
        mResId = resId;
        isContactBig = isContact;
    }

    public void setNativeAdListener(NativeAdListener listener) {
        mListener = listener;
    }

    public void refreshAd(boolean forceRefresh) {
        if (!forceRefresh
                || System.currentTimeMillis() - mLastRefreshTime < mRefreshTime) {
            return;
        }

        mLastRefreshTime = System.currentTimeMillis();
        loadNativeAd();
        LogUtil.d("ad_check", "fb big contact refreshAd.");
    }

    private void loadNativeAd() {
        LogUtil.d("ad_check", "fb big contact loadNativeAd start.");
        nativeAd = new NativeAd(mContext.get(), mAdId);
        nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                if (mListener != null) {
                    mListener.onError(ad, error);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                inflateNativeAd();
                if (mListener != null) {
                    mListener.onAdLoaded(ad);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (mListener != null) {
                    mListener.onAdClicked(ad);
                }
//                refreshAd(true); //click not refresh
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                if (mListener != null) {
                    mListener.onLoggingImpression(ad);
                }
            }
        });

        // Request an ad
        nativeAd.loadAd();
        LogUtil.d("ad_check", "fb big contact loadNativeAd end.");
    }

    private void inflateNativeAd() {
        if (nativeAd != null) {
            nativeAd.unregisterView();
        }

        // Add the Ad viewSelected into the ad container.
        nativeAdContainer = (LinearLayout) mView.findViewById(R.id.fb_native_ad_container);
        if (nativeAdContainer == null) {
            return;
        }
        nativeAdContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(mContext.get());
        // Inflate the Ad viewSelected.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(mResId, nativeAdContainer, false);
        nativeAdContainer.addView(adView);

        // Create native UI using the ad metadata.
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
        //TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
        Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdTitle());
        if(isContactBig) {
            nativeAdTitle.setShadowLayer(10f, 0, 0, 0xff000000);
        }
//        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdBody.setText(nativeAd.getAdBody());
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());

        // Download and display the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        // Download and display the cover image.
        nativeAdMedia.setNativeAd(nativeAd);
        if (mIsBanner) {
            nativeAdMedia.setVisibility(View.GONE);
        }

//        // Add the AdChoices icon
//        LinearLayout adChoicesContainer = (LinearLayout) findViewById(R.id.ad_choices_container);
//        AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeAd, true);
//        adChoicesContainer.addView(adChoicesView);
        FrameLayout nativeAdImage = (FrameLayout) adView.findViewById(R.id.layout_fb_image);

//        if (adChoicesView == null) {
        AdChoicesView adChoicesView = new AdChoicesView(mContext.get(), nativeAd, true);
        FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(Stringutil.dpToPx(mContext.get(), 24), Stringutil.dpToPx(mContext.get(), 24));
        layoutParam.gravity = Gravity.RIGHT | Gravity.TOP;
        nativeAdImage.addView(adChoicesView, layoutParam);
//        }

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdBody);
        clickableViews.add(nativeAdCallToAction);
        if(CallerAdManager.isAdImgBgClickable()) {
           clickableViews.add(nativeAdImage);
        }

        try {
            nativeAd.registerViewForInteraction(nativeAdContainer, clickableViews);
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    public LinearLayout showListViewFBAD() {
        // Add the Ad viewSelected into the ad container.
        LogUtil.d("facebookADLoader", "refreshAds nativeAdContainer:" + nativeAdContainer);
//        if (nativeAdContainer == null) {
//            return null;
//        }
//        nativeAdContainer.removeAllViews();
//        LayoutInflater inflater = LayoutInflater.from(mContext.get());
//        // Inflate the Ad viewSelected.  The layout referenced should be the one you created in the last step.
//        adView = (LinearLayout) inflater.inflate(mResId, nativeAdContainer, false);
//        nativeAdContainer.addView(adView);
//
//        // Create native UI using the ad metadata.
//        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
//        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
//        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
//        //TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
//        TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
//        Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);
//
//        // Set the Text.
//        nativeAdTitle.setText(nativeAd.getAdTitle());
//        nativeAdBody.setText(nativeAd.getAdBody());
//        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
//
//        LogUtil.d("facebookADLoader", "refreshAds titel:" + nativeAd.getAdTitle() + ",body:" + nativeAd.getAdBody());
//        // Download and display the ad icon.
//        NativeAd.Image adIcon = nativeAd.getAdIcon();
//        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
//
//        // Download and display the cover image.
//        nativeAdMedia.setNativeAd(nativeAd);
//
//
//        FrameLayout nativeAdImage = (FrameLayout) adView.findViewById(R.id.layout_fb_image);
//
//        AdChoicesView adChoicesView = new AdChoicesView(mContext.get(), nativeAd, true);
//        FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(Stringutil.dpToPx(mContext.get(), 24), Stringutil.dpToPx(mContext.get(), 24));
//        layoutParam.gravity = Gravity.RIGHT | Gravity.TOP;
//        nativeAdImage.addView(adChoicesView, layoutParam);
//
//        // Register the Title and CTA button to listen for clicks.
//        List<View> clickableViews = new ArrayList<>();
//        clickableViews.add(nativeAdTitle);
//        clickableViews.add(nativeAdBody);
//        clickableViews.add(nativeAdCallToAction);
//        clickableViews.add(nativeAdImage);
        return adView;
    }

    public interface NativeAdListener {
        void onError(Ad ad, AdError error);

        void onAdLoaded(Ad ad);

        void onAdClicked(Ad ad);

        void onLoggingImpression(Ad ad);
    }
}