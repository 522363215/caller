package blocker.call.wallpaper.screen.caller.ringtones.callercolor;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.flurry.android.FlurryAgent;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.LocalService;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;


public class ApplicationEx extends Application {

    private static ApplicationEx instance;
    private static ApplicationEx MainInstance;

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
        processName = CommonUtils.getProcessName(this, android.os.Process.myPid());//pkgname, com.hiblock.caller
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
        // 加载语言
        LanguageSettingUtil languageSetting = LanguageSettingUtil.getInstance(getApplicationContext());
        if (languageSetting != null)
            languageSetting.refreshLanguage(this);

        //upgrade setting
        new FlurryAgent.Builder()
                .withLogEnabled(BuildConfig.DEBUG)
                .build(getInstance(), ConstantUtils.KEY_FLURRY);
        startService();

    }
}
