package blocker.call.wallpaper.screen.caller.ringtones.callercolor;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.common.sdk.adpriority.AdPriorityListener;
import com.common.sdk.adpriority.AdPriorityManager;
import com.flurry.android.FlurryAgent;
import com.md.serverflash.ThemeSyncManager;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.JobLocalService;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.LocalService;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.AppUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
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
    private BroadcastReceiver mNetworkChangeListener;
    private BroadcastReceiver mCallerCommonReceiver;
    private BroadcastReceiver mMessageReceiver;

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
        MultiDex.install(base);
    }

    private void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Intent intent = new Intent(this, JobSchedulerService.class);
//            startService(intent);

            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(101, new ComponentName(getPackageName(), JobLocalService.class.getName()))
                    .setPeriodic(120000)//2mins
//                    .setOverrideDeadline(3*1000)
//                    .setMinimumLatency(1*1000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
            jobScheduler.schedule(jobInfo);
        } else {
            Intent intent = new Intent(this, LocalService.class);
            startService(intent);
        }
    }

    private void initAppData() {
        country = NumberUtil.getDefaultCountry();
        mInterstitialAdvertisementMap = new ConcurrentHashMap<>();
        // 加载语言
        loadLanguage();
        //upgrade setting
        new FlurryAgent.Builder().withLogEnabled(BuildConfig.DEBUG).build(getInstance(), BuildConfig.FLURRY_KEY);
        saveVersioncode();
        startService();
        initCallFlashBase();
        // TODO: 2018/7/5 广告暂时屏蔽
//        intiAdManager();
        initPriorityAds();

    }

    private void initCallFlashBase() {
        int clientId = ConstantUtils.CALLER_STATISTICS_CHANNEL;
        String channel = StatisticsUtil.getChannel();
        String subChannel = StatisticsUtil.getSubChannel();
        long expireTime = 2 * android.text.format.DateUtils.HOUR_IN_MILLIS;
        // TODO: 2018/2/9 should modify params(testMode) to false;
        boolean testMode = false;
        testMode = BuildConfig.DEBUG;
        ThemeSyncManager.getInstance().init(
                getApplicationContext(),
                Locale.getDefault().getLanguage(),
                channel,
                subChannel,
                expireTime,
                testMode);
    }

    public SharedPreferences getGlobalSettingPreference() {
        SharedPreferences globalSettingPreference = getSharedPreferences(ConstantUtils.PREF_FILE, Context.MODE_PRIVATE);
        return globalSettingPreference;
    }

    public SharedPreferences getGlobalADPreference() {
        SharedPreferences adPreference = getSharedPreferences(ConstantUtils.AD_PREF_FILE, Context.MODE_PRIVATE);
        return adPreference;
    }

    private void saveVersioncode() {
        long currentTimeMillis = System.currentTimeMillis();
        int versionCode = HttpUtil.pkgVersion(this);
        if (PreferenceHelper.getBoolean("is_first_start", true)) {
            PreferenceHelper.putBoolean("is_first_start", false);
            PreferenceHelper.putLong("colorphone_inStall_time", currentTimeMillis);
            // 记录第一次安装的版本号
            PreferenceHelper.putInt(AppUtils.KEY_INIT_VERCODE, versionCode);
        }
        //记录当前版本号
        PreferenceHelper.putInt(AppUtils.KEY_CUR_VERCODE, versionCode);
    }

    private void loadLanguage() {
        LanguageSettingUtil languageSetting = LanguageSettingUtil.getInstance(getApplicationContext());
        if (languageSetting != null) {
            languageSetting.refreshLanguage(this);
        }
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

    public static boolean isReleaseHUAWEI() {
        boolean is = false; // 华为渠道包， 手动改为true
        return is;
    }

    private void initPriorityAds() {
        try {
            mAdPriorityMgr = AdPriorityManager.getInstance(MainInstance.getApplicationContext());
            mAdPriorityMgr.setChannel(StatisticsUtil.getChannel());
            mAdPriorityMgr.setSubChannel(StatisticsUtil.getSubChannel());
//            mAdPriorityMgr.setClientId(ConstantUtils.CALLER_STATISTICS_CHANNEL);
//            mAdPriorityMgr.setDomain(ConstantUtils.AD_PRIORITY_SERVER);
            mAdPriorityMgr.setFirstSynServerConfigTime(getInstance().getGlobalSettingPreference().getLong("key_cid_first_sync_server_time", 0));

            long firstLaunchTime = getInstance().getGlobalSettingPreference().getLong("colorphone_inStall_time", 0);
            if (firstLaunchTime == 0) {
                firstLaunchTime = System.currentTimeMillis();
            }
            mAdPriorityMgr.setFirstLaunch(firstLaunchTime);
            AdvertisementSwitcher.getInstance().initFromConfigCache(mAdPriorityMgr);//do this init job sync in main thread

            LogUtil.d("advertise", "initPriorityAds get channel: " + StatisticsUtil.getChannel());

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
            LogUtil.e("advertise", "initPriorityAds exception: " + e.getMessage());
        }
    }

}
