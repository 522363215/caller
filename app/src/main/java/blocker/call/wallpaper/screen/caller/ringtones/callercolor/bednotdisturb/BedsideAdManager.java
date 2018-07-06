package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bednotdisturb;

import android.content.Context;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.cpm.CpmAdUtils;

/**
 * 床头钟 cpm轮询广告管理（这里不支持fb + admob，只支持各cpm平台）
 * <p>
 * #1. 每个渠道请求间隔为30s(可配置)
 * #2. 每个ID每天请求50次(可配置)
 * #3. 每个广告展示5s(可配置)( = 每5s尝试展示，并请求)
 * #4. 同时支持大图和小图展示(id可配置)
 * #5. 考虑到各个平台的填充率，这里进行所有平台的并行请求（以平台为层）
 * #6. 展示进行简单处理，依照优先级配置，进行广告展示。
 * <p>
 * NM:  配置每天 IA、AOL、PN、SMT 50次(每个id50次)
 * <p>
 * Created by Sandy on 18/5/3.
 */

public class BedsideAdManager {

    private final List<BedsideAdContainer> adsList = new ArrayList<>();

    public BedsideAdManager(Context context, boolean isRecordCount, BedsideAdContainer.Callback callback) {
        init(context, isRecordCount, callback);
    }

    private void init(Context context, boolean isRecordCount, BedsideAdContainer.Callback callback) {
        String[] priorityList = BedNotDisturbManager.getBedsideAdPrList();//new String[]{"pr_ia", "pr_as", "pr_smt", "pr_pn", "pr_mf", "pr_aol"};
        int length = priorityList == null ? 0 : priorityList.length;
        if (length > 0) {
            // id 配置
            JSONObject adConfigJson = BedNotDisturbManager.getBedsideAdConfig();
            if (adConfigJson != null) {
                long loadGap = BedNotDisturbManager.getBedsideAdLoadGapSecond() * 1000L;
                for (int i = 0; i < length; i++) {
                    String prType = priorityList[i];
                    try {
                       BedsideIdManager idManager = new BedsideIdManager(adConfigJson.getJSONArray(prType));
                        BedsideAdContainer adContainer = new BedsideAdContainer(idManager, CpmAdUtils.getAdType(prType), loadGap, isRecordCount);
                        adContainer.setCallback(callback);
                        adsList.add(adContainer);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 展示 并 补充请求下一个广告
     *
     * @param context
     * @param layout
     */
    public boolean show(Context context, ViewGroup layout) {
        boolean isShowAd = false;
        if (layout == null || context == null) {
            return isShowAd;
        }

        // 获取最早请求到的广告对象，进行显示
        long loadedTime = Long.MAX_VALUE;
        BedsideAdContainer showAd = null;
        for (BedsideAdContainer adContainer : adsList) {
            if (adContainer != null) {
                if (adContainer.isReady() && adContainer.getLoadedTime() < loadedTime) {
                    showAd = adContainer;
                    loadedTime = adContainer.getLoadedTime();
                }
            }
        }
        if (showAd != null) {
            isShowAd = showAd.show(layout);
            if (showingAd != null) {
                showingAd.pause();
            }
            showingAd = showAd;
        }

        // 请求补充之后的展示
        for (BedsideAdContainer adContainer : adsList) {
            adContainer.load(context);
        }
        return isShowAd;
    }

    /**
     * 今日是否所有渠道id请求都请求完毕
     *
     * @return
     */
    public boolean isAllLoadMax() {
        synchronized (adsList) {
            for (BedsideAdContainer adContainer : adsList) {
                if (!adContainer.isMaxLoadToday()) {
                    return false;
                }
            }
        }
        return true;
    }

    private BedsideAdContainer showingAd = null;

    public void resume() {
        if (showingAd != null) {
            showingAd.resume();
        }
    }

    public void pause() {
        if (showingAd != null) {
            showingAd.pause();
        }
    }

    public void destroy() {
        try {
            for (BedsideAdContainer adContainer : adsList) {
                if (adContainer != null) {
                    adContainer.destroy();
                }
            }
            adsList.clear();
        } catch (Exception e) {
        }
    }

}
