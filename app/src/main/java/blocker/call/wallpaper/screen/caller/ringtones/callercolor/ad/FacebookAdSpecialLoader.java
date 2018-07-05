package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import java.util.concurrent.atomic.AtomicBoolean;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

/**
 * Created by zhangjinwei on 2017/7/13.
 */

public class FacebookAdSpecialLoader {

    public static final String TAG = "fb-loader";

    private final long DEFAULT_REFRESH_TIME = 2 * 60 * 1000;//ConstantValue.MINUTE * 2;
    private View mAdRootView;

    private long mRefreshTime = DEFAULT_REFRESH_TIME;

    private String mPlacementId;
    private int mContainerId;
    private int mLayoutResId;

    private NativeAd nativeAd;
    private ViewGroup nativeAdContainer;
    private LinearLayout adView;

    private WeakReference<Context> mContext;
    private View mDecorView;

    private NativeAdListener mListener;
    private long mAdLoadedTime;

    private long mLastRefreshTime;
    private AtomicBoolean mIsLoading = new AtomicBoolean(false);
    private boolean mIsBanner = false;

    private boolean isOnlyBtnClickable = false;

    public FacebookAdSpecialLoader(Context context,
                                   View decorView,
                                   int containerId,
                                   int layoutResId,
                                   String placementId,
                                   boolean isBanner) {
        mContext = new WeakReference<>(context);
        mDecorView = decorView;
        mContainerId = containerId;
        mPlacementId = placementId;
        mLayoutResId = layoutResId;
        mIsBanner = isBanner;
        isOnlyBtnClickable = false;

        if (mContext.get() != null) {
            LayoutInflater inflater = (LayoutInflater) mContext.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layoutFacebook = (LinearLayout) inflater.inflate(layoutResId, null);
            mAdRootView = layoutFacebook.findViewById(R.id.layout_ad_view_root);
        }
    }

    public void enableOnlyBtnClickable() {
        isOnlyBtnClickable = true;
    }

    public void setNativeAdListener(NativeAdListener listener) {
        mListener = listener;
    }

    public void refreshAd(boolean forceRefresh) {
        if (!forceRefresh
                && System.currentTimeMillis() - mLastRefreshTime < mRefreshTime) {
            return;
        }

        mLastRefreshTime = System.currentTimeMillis();
        loadNativeAd();
    }

    private void loadNativeAd() {
        LogUtil.d("ad_check", "fb special loadNativeAd start.");
        if (mIsLoading.get()) {
            return;
        }

        nativeAd = new NativeAd(mContext.get(), mPlacementId);
        nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                mIsLoading.set(false);
                if (mListener != null) {
                    mListener.onError(ad, error);
                }
                if (BuildConfig.DEBUG) {
                    LogUtil.d(TAG, "onError: " + nativeAd.getPlacementId());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                mIsLoading.set(false);
                mAdLoadedTime = System.currentTimeMillis();
                inflateNativeAd(null);
                if (mListener != null) {
                    mListener.onAdLoaded(ad);
                }
                if (BuildConfig.DEBUG) {
                    LogUtil.d(TAG, "onAdLoaded: " + nativeAd.getPlacementId());
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (mListener != null) {
                    mListener.onAdClicked(ad);
                }
                if (BuildConfig.DEBUG) {
                    LogUtil.d(TAG, "onAdClicked: " + nativeAd.getPlacementId());
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                if (mListener != null) {
                    mListener.onLoggingImpression(ad);
                }
                if (BuildConfig.DEBUG) {
                    LogUtil.d(TAG, "onLoggingImpression: " + nativeAd.getPlacementId());
                }
            }
        });

        // Request an ad
        nativeAd.loadAd();
        mIsLoading.set(true);
        LogUtil.d("ad_check", "fb special loadNativeAd end.");
    }

    private void inflateNativeAd(View view) {
        if (nativeAd != null) {
            nativeAd.unregisterView();
        }

        View rootView = view == null ? mDecorView : view;
        if (rootView == null) {
            return;
        }

        // Add the Ad viewSelected into the ad container.
        nativeAdContainer = (ViewGroup) rootView.findViewById(mContainerId);
        if (nativeAdContainer == null) {
            return;
        }
//        nativeAdContainer.setVisibility(View.VISIBLE);
        nativeAdContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(mContext.get());
        // Inflate the Ad viewSelected.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(mLayoutResId, nativeAdContainer, false);
        nativeAdContainer.addView(adView);

        // Create native UI using the ad metadata.
        ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.nativeAdIcon);
        TextView nativeAdTitle = (TextView) adView.findViewById(R.id.nativeAdTitle);
        MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.nativeAdMedia);
        //TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = (TextView) adView.findViewById(R.id.nativeAdBody);
        View nativeAdCallToAction = null;

        if (mIsBanner) {
            nativeAdCallToAction = (Button) adView.findViewById(R.id.nativeAdCallToAction);
            ((Button) nativeAdCallToAction).setText(nativeAd.getAdCallToAction());
        } else {
            nativeAdCallToAction = (TextView) adView.findViewById(R.id.nativeAdCallToAction);
            ((TextView) nativeAdCallToAction).setText(nativeAd.getAdCallToAction());
            if (mContext.get() != null && mAdRootView != null) {
                NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
                int bannerWidth = adCoverImage.getWidth();
                int bannerHeight = adCoverImage.getHeight();
                WindowManager wm = (WindowManager) mContext.get().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);

                //Ad Root View layout params
                //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mAdRootView.getLayoutParams();
                int paddingLeft = mAdRootView.getPaddingLeft();
                int paddingRight = mAdRootView.getPaddingRight();

                int mediaViewWidth = metrics.widthPixels - paddingLeft - paddingRight;
                int screenHeight = metrics.heightPixels;
                int height = Math.min((int) ((mediaViewWidth * 1.0 / bannerWidth) * bannerHeight), screenHeight / 3);
                nativeAdMedia.setLayoutParams(new FrameLayout.LayoutParams(mediaViewWidth, height));
            }
        }

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdTitle());
//        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdBody.setText(nativeAd.getAdBody());

        // Download and display the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        // Download and display the cover image.
        if (nativeAdMedia != null) {
            nativeAdMedia.setNativeAd(nativeAd);
        }
        if (mIsBanner && nativeAdMedia != null) {
            nativeAdMedia.setVisibility(View.GONE);
        }

//        // Add the AdChoices icon
//        LinearLayout adChoicesContainer = (LinearLayout) findViewById(R.id.ad_choices_container);
//        AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeAd, true);
//        adChoicesContainer.addView(adChoicesView);
        FrameLayout nativeAdImage = (FrameLayout) adView.findViewById(R.id.layout_fb_image);

//        if (adChoicesView == null) {
        AdChoicesView mAdChoicesView = new AdChoicesView(mContext.get(), nativeAd, true);
        FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(DeviceUtil.dp2Px(16), DeviceUtil.dp2Px(16));

        layoutParam.gravity = Gravity.LEFT | Gravity.BOTTOM | Gravity.START;
        nativeAdImage.addView(mAdChoicesView, layoutParam);
//        }

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        if (isOnlyBtnClickable) {
            if (nativeAdCallToAction != null) {
                clickableViews.add(nativeAdCallToAction);
            }
        } else {
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdBody);
            clickableViews.add(nativeAdCallToAction);
            clickableViews.add(nativeAdImage);
            if (nativeAdMedia != null) {
                clickableViews.add(nativeAdMedia);
            }
        }

        try {
            nativeAd.registerViewForInteraction(nativeAdContainer, clickableViews);
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    public interface NativeAdListener {
        void onError(Ad ad, AdError error);

        void onAdLoaded(Ad ad);

        void onAdClicked(Ad ad);

        void onLoggingImpression(Ad ad);
    }
}
