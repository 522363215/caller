package com.individual.sdk;

public interface AdListener {
    void onAdLoaded(BaseAdContainer ad);
    void onAdLoadFailed(BaseAdContainer ad, String errorMsg);
    void onAdClick(BaseAdContainer ad);
    void onAdImpression(BaseAdContainer ad);
}
