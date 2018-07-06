package com.individual.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseAdContainer {
    public static final String TAG = "GATHER_AD_LOG";

    protected final String mId;
    protected final Context mContext;
    protected AdListener callback;
    protected AtomicBoolean isLoading;

    protected final Handler mHandler;
    protected final Runnable timeoutRunnable;
    protected long TIMEOUT = 25 * 1000;// default:20s

    public BaseAdContainer(Context context, String adId){
        this.mId = adId;
        this.mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        isLoading = new AtomicBoolean(false);
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if(isLoading.get()) {
                    onLoadTimeout();
                    isLoading.set(false);
                    if(callback != null) {
                        callback.onAdLoadFailed(BaseAdContainer.this, "TimeOut!");
                    }
                }
            }
        };
    }

    public abstract AdType getAdType();

    public String getId() {
        return mId;
    }

    public void setCallback(AdListener call) {
        this.callback = call;
    }

    public void setLoadTimeout(long timeout) {
        if(timeout > 0) {
            TIMEOUT = timeout;
        }
    }

    /**
     * 该方法只能在当次loaded后进行调用
     * @param layout
     * @return
     */
    public abstract boolean show(ViewGroup layout);

    public void load() {
        if(isLoading.get()) {
            return;
        }
        isLoading.set(true);
        if(mContext == null) {
            isLoading.set(false);
            throw new NullPointerException("AdsContainer load with null context");
        }

        if(!isInited()) {
            isLoading.set(false);
            if(callback != null) callback.onAdLoadFailed(this, getAdType() + " is not initial yet.");
            return;
        }

        if(TextUtils.isEmpty(mId)) {
            isLoading.set(false);
            if(callback != null) callback.onAdLoadFailed(this, "empty id.");
            return;
        }

        if(startLoad()) {
            mHandler.removeCallbacks(timeoutRunnable);
            mHandler.postDelayed(timeoutRunnable, TIMEOUT);
        }
    }

    protected abstract boolean startLoad();
    protected abstract void onLoadTimeout();
    protected abstract boolean isInited();
    public abstract void resume();
    public abstract void pause();
    public abstract void destory();

}
