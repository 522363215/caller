package blocker.call.wallpaper.screen.caller.ringtones.callercolor;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.duapps.ad.base.DuAdNetwork;
import com.flurry.android.FlurryAgent;
import com.lionmobi.sdk.adpriority.AdPriorityListener;
import com.lionmobi.sdk.adpriority.AdPriorityManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.DuAdsConstant;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.LocalService;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.AppUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.FileUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.HttpUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.NumberUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.StatisticsUtil;


public class ApplicationEx extends Application {

    private static ApplicationEx instance;
    private static ApplicationEx MainInstance;
    private Map<Integer, InterstitialAdvertisement> mInterstitialAdvertisementMap;
    public String country;
    private AdPriorityManager mAdPriorityMgr;
    private long mLastUpdateAdConfigTime;

    //main process
    public static ApplicationEx getInstance() {
        return MainInstance;
    }

    public static ApplicationEx getCommonInstance() {
        return instance;
    }

    private String processName;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        processName = CommonUtils.getProcessName(this, android.os.Process.myPid());//pkgname
        LogUtil.d("app_ex", "processName: " + processName);
        if (!TextUtils.isEmpty(processName) && processName.equals(getPackageName())) {
            LogUtil.d("app_ex", "init app data: ");
            MainInstance = this;
            initAppData();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public SharedPreferences getGlobalSettingPreference() {
        SharedPreferences globalSettingPreference = getSharedPreferences(ConstantUtils.PREF_FILE, Context.MODE_PRIVATE);
        return globalSettingPreference;
    }

    public SharedPreferences getGlobalADPreference() {
        SharedPreferences adPreference = getSharedPreferences(ConstantUtils.AD_PREF_FILE, Context.MODE_PRIVATE);
        return adPreference;
    }

    private void startService() {
        startService(new Intent(this, LocalService.class));
    }

    private void initAppData() {
        country = NumberUtil.getDefaultCountry();
        mInterstitialAdvertisementMap = new ConcurrentHashMap<>();
        // 加载语言
        loadLanguage();
        //upgrade setting
        new FlurryAgent.Builder().withLogEnabled(BuildConfig.DEBUG).build(getInstance(), ConstantUtils.KEY_FLURRY);
        saveVersioncode();
        startService();

        // TODO: 2018/7/5 广告暂时屏蔽
//        intiAdManager();
//        initPriorityAds();
    }

    private void saveVersioncode() {
        long currentTimeMillis = System.currentTimeMillis();
        SharedPreferences sp = getInstance().getGlobalSettingPreference();
        int versionCode = HttpUtil.pkgVersion(this);
        if (sp.getBoolean("is_first_start", true)) {
            sp.edit().putBoolean("is_first_start", false).apply();
            sp.edit().putLong("cid_inStall_time", currentTimeMillis).apply();
            // 记录第一次安装的版本号
            sp.edit().putInt(AppUtils.KEY_INIT_VERCODE, versionCode).apply();
        }
        //记录当前版本号
        sp.edit().putInt(AppUtils.KEY_CUR_VERCODE, versionCode).apply();
    }

    private void loadLanguage() {
        LanguageSettingUtil languageSetting = LanguageSettingUtil.getInstance(getApplicationContext());
        if (languageSetting != null){
            languageSetting.refreshLanguage(this);
        }
    }

    private void intiAdManager() {
        // init du ads sdk 
        DuAdNetwork.init(getApplicationContext(), FileUtil.getConfigJson(this, DuAdsConstant.DU_ADS_CONFIG_FILE));
//        AdManager.getInstance(getApplicationContext()).initAdData(false);
    }

    public void setInterstitialAdvertisement(InterstitialAdvertisement interstitialAdvertisement, int position) {
        if (mInterstitialAdvertisementMap == null) {
            mInterstitialAdvertisementMap = new ConcurrentHashMap<>();
        }

        if (interstitialAdvertisement == null) {
            mInterstitialAdvertisementMap.remove(position);
        } else {
            mInterstitialAdvertisementMap.put(position, interstitialAdvertisement);
        }
    }

    public InterstitialAdvertisement getInterstitialAdvertisement(int position) {
        if (mInterstitialAdvertisementMap != null) {
            return mInterstitialAdvertisementMap.get(position);
        }
        return null;
    }

    private void initPriorityAds() {
        try {
            mAdPriorityMgr = AdPriorityManager.getInstance(MainInstance.getApplicationContext());
            mAdPriorityMgr.setChannel(StatisticsUtil.getChannel());
            mAdPriorityMgr.setSubChannel(StatisticsUtil.getSubChannel());
            mAdPriorityMgr.setIsSeparate(false);
            mAdPriorityMgr.setFirstSynServerConfigTime(getInstance().getGlobalSettingPreference().getLong("key_cid_first_sync_server_time", 0));

            long firstLaunchTime = getInstance().getGlobalSettingPreference().getLong(ConstantUtils.PREF_KEY_INSTALL_TIME, 0);
            if (firstLaunchTime == 0) {
                firstLaunchTime = System.currentTimeMillis();
            }
            mAdPriorityMgr.setFirstLaunch(firstLaunchTime);
            AdvertisementSwitcher.getInstance().initFromConfigCache(mAdPriorityMgr);//do this init job sync in main thread

            LogUtil.d("get channel", "initPriorityAds get channel: " + StatisticsUtil.getChannel());

            mAdPriorityMgr.setAdPriorityListener(new AdPriorityListener() {
                @Override
                public void onPriorityLoaded() {
                    if (System.currentTimeMillis() - mLastUpdateAdConfigTime < 5 * 60 * 1000) {
                        return;
                    }
//
                    LogUtil.d("advertise", "init ad priority onPriorityLoaded.");
                    mLastUpdateAdConfigTime = System.currentTimeMillis();
                    AdvertisementSwitcher.getInstance().updateConfig(mAdPriorityMgr);
//                    LionLocalStorageManager.setLong(SharePrefConstant.LAST_REFRESH_AD_PRIORITY_CONFIG_TIME, System.currentTimeMillis());
                }

                @Override
                public void onPriorityError(int i) {
                    LogUtil.d("advertise", "init error.");
                }
            });

            mAdPriorityMgr.getAdPriorityData();

        } catch (Exception e) {
            LogUtil.e("Advertise", "initPriorityAds exception: " + e.getMessage());
        }
    }
}
