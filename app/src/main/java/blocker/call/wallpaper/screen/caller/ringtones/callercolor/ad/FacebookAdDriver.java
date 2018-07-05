package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

/**
 * Created by Zac on 2017/11/3.
 */
public class FacebookAdDriver {

    private static final String TAG = "ad-driver";

    FacebookAdSpecialLoader mLoader;
    long mLoadInterval;
    int mMaxCount;
    AtomicInteger mLoadCount = new AtomicInteger(0);

    long mLoadTime;
    long mImpressionTime;

    AtomicBoolean mStarted = new AtomicBoolean(false);
    AtomicBoolean mPaused = new AtomicBoolean(false);
    private FacebookAdLoadListener mFacebookAdLoadListener;

    public FacebookAdDriver(FacebookAdSpecialLoader loader, long loadInterval, int maxCount) {
        mLoader = loader;
        mLoadInterval = loadInterval;
        mMaxCount = maxCount;
    }

    public void start() {
        mLoadTime = 0;
        mLoadCount.set(0);
        mPaused.set(false);
        mStarted.set(true);

        if (mLoader != null) {
            mLoader.setNativeAdListener(mAdListener);
            load();
        }
    }

    public void pause() {
        mPaused.set(true);
        if (BuildConfig.DEBUG) {
            LogUtil.d(TAG, "pause!");
        }
    }

    public boolean paused() {
        return mPaused.get();
    }

    public void resume() {
        if (mStarted.get()) {
            mPaused.set(false);
            loadNext();
        }
    }

    public void stop() {
        mStarted.set(false);
        mLoader = null;
    }

    private void load() {
        if (mLoader != null) {
            mLoader.refreshAd(true);
            mLoadTime = System.currentTimeMillis();
        }
    }

    private void loadNext() {
        if (mPaused.get()) {
            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, "won't load: paused!");
            }
            return;
        }

        if (mLoadCount.get() >= mMaxCount) {
            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, "won't load: max count!");
            }
            return;
        }

        long curTime = System.currentTimeMillis();
        long timeAfterLoad = curTime - mLoadTime;
        long leftToInterval = mLoadInterval - timeAfterLoad;
        long delay = leftToInterval > 0 ? leftToInterval : 0;

        Async.scheduleTaskOnUiThread(delay, new Runnable() {
            @Override
            public void run() {
                load();
            }
        });
    }

    FacebookAdSpecialLoader.NativeAdListener mAdListener = new FacebookAdSpecialLoader.NativeAdListener() {
        @Override
        public void onError(Ad ad, AdError error) {
            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, "onError!");
            }
        }

        @Override
        public void onAdLoaded(Ad ad) {
            int loadCount = mLoadCount.incrementAndGet();
            if (mFacebookAdLoadListener != null) mFacebookAdLoadListener.onAdLoaded(ad);
            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, "onAdLoaded! " + loadCount);
            }
        }

        @Override
        public void onAdClicked(Ad ad) {
            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, "onAdClicked!");
            }
            loadNext();
        }

        @Override
        public void onLoggingImpression(Ad ad) {
            if (BuildConfig.DEBUG) {
                LogUtil.d(TAG, "onLoggingImpression!");
            }
            mImpressionTime = System.currentTimeMillis();
            loadNext();
        }
    };

    public interface FacebookAdLoadListener {
        void onAdLoaded(Ad ad);
    }

    public void setFacebookAdLoadListener(FacebookAdLoadListener facebookAdLoadListener) {
        mFacebookAdLoadListener = facebookAdLoadListener;
    }
}
