package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.common.sdk.base.manager.AnalyticsManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ServerManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.LocalService;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.RomUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.SpecialPermissionsUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.SystemInfoUtil;

public abstract class BaseActivity extends AppCompatActivity implements PermissionUtils.PermissionGrant {

    protected LanguageSettingUtil languageSetting;
    protected ApplicationEx app;
    //protected AppEventsLogger logger;
    private final static String APP_ADMOB_ID = "ca-app-pub-4922304484386262~6359729062";

    protected ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private BroadcastReceiver mFinishReceiver;

    @Override
    protected void onDestroy() {
        // Log.d("ACT", this.getClass().getSimpleName() + "(onDestroy)");
//        setContentView(R.layout.view_null);
        super.onDestroy();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    unbindService(mServiceConnection);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Log.d("ACT", this.getClass().getSimpleName() + "(onCreate)");
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRootId());
        translucentStatusBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CommonUtils.scheduleJob(this.getApplicationContext());
            LogUtil.d("JobLocalService", "base_activity scheduleJob startService: ");
        }else {
            bindService();
        }
        SwitchLang();

        //init tuiguang fb
        FacebookSdk.sdkInitialize(getApplicationContext());

        //init admob
        MobileAds.initialize(this, APP_ADMOB_ID);

        //
        ServerManager.getInstance().getParamFromServer();


//        if (BuildConfig.DEBUG) {
//            if (!AdSettings.isTestMode(this)) {
//                SharedPreferences localSharedPreferences = this.getSharedPreferences("FBAdPrefs", 0);
//                AdSettings.addTestDevice(localSharedPreferences.getString("deviceIdHash", null));
//            }
//        }
    }

    // 设置沉浸式状态栏, 并且设置状态栏占位留白;
    protected abstract void translucentStatusBar();

    //
    protected abstract int getLayoutRootId();

    private void bindService() {
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        if (!BuildConfig.DEBUG)
            FlurryAgent.onStartSession(this);
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        AnalyticsManager.onUserActive();
        FlurryAgent.onPageView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        FlurryAgent.onEndSession(this);
        super.onStop();
    }

    private void updateDisplayLanguage(Locale locale) {
        Configuration config = getResources().getConfiguration();
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = locale;
        resources.updateConfiguration(config, dm);
    }

    private void SwitchLang() {
        //加载语言
        languageSetting = LanguageSettingUtil.getInstance(getApplicationContext());
        if (languageSetting != null)
            languageSetting.refreshLanguage(this);
    }

    //**************************************请求权限 start************************//
    public void requestPermission(String[] permissions, int requestCode) {
        PermissionUtils.requestMultiPermissions(this, permissions, this, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, this);
    }

    public void requestSpecialPermission(String permission) {
        if (PermissionUtils.PERMISSION_OVERLAY.equals(permission)) {
            requestSpecialPermission(PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION, false);
        } else if (PermissionUtils.PERMISSION_NOTIFICATION_POLICY_ACCESS.equals(permission)) {
            requestSpecialPermission(PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS, false);
        } else if (PermissionUtils.PERMISSION_SHOW_ON_LOCK.equals(permission)) {
            requestSpecialPermission(PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK, false);
        } else if (PermissionUtils.PERMISSION_AUTO_START.equals(permission)) {
            requestSpecialPermission(PermissionUtils.REQUEST_CODE_AUTO_START, false);
        }
    }

    /**
     * 请求特殊权限，如悬浮窗权限，通知服务权限，修改系统设置权限等非常规权限
     */
    private void requestSpecialPermission(int requestCode, boolean isOnActivityResult) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION://draw overlay
                if (isOnActivityResult) {
                    //overlay premission返回时针对不同的手机判断
                    if (RomUtils.checkIsHuaweiRom()) {
                        if (!SpecialPermissionsUtil.checkFloatWindowPermission(this)) {
                            SpecialPermissionsUtil.applyFloatWindowPermission(this);
                            return;
                        }
                    } else {
                        if (!SpecialPermissionsUtil.checkFloatWindowPermission(this)) {
                            toAppDetailSetting();
                            return;
                        }
                    }
                    if (!SpecialPermissionsUtil.canDrawOverlays(this)) {
                        onPermissionNotGranted(PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION);
                    } else {
                        onPermissionGranted(PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION);
                    }
                } else {
                    if (!SpecialPermissionsUtil.canDrawOverlays(this) && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        // 检查overlay权限
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION);
                        showPermissionTipDialog(getString(R.string.permission_for_show_call_flash));
                    } else {
                        onPermissionGranted(PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION);
                    }
                }
                break;
            case PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS://通知管理,7.0以上用于接听电话
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (isOnActivityResult) {
                        if (!SpecialPermissionsUtil.isHaveNotificationPolicyAccessForAnswerCall(this)) {
                            onPermissionNotGranted(PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS);
                        } else {
                            onPermissionGranted(PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS);
                            PermissionUtils.toggleNotificationListenerService(ApplicationEx.getInstance());
                        }
                    } else {
                        if (!SpecialPermissionsUtil.isHaveNotificationPolicyAccessForAnswerCall(this)) {
                            // 检查通知使用权
                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                            startActivityForResult(intent, PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS);
                            showPermissionTipDialog(getString(R.string.permission_for_answer_call));
                        } else {
                            onPermissionGranted(PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS);
                        }
                    }
                } else {
                    onPermissionGranted(PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS);
                }
                break;
            case PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK://小米专用
                boolean isRequestedShowOnLock = PreferenceHelper.getLong(PreferenceHelper.PREF_KEY_LAST_TO_XIAO_MI_SHOW_ON_LOCK_PERMISSION_ACTIVITY, 0) > 0;
                if (isOnActivityResult) {
                    if (!isRequestedShowOnLock && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && SystemInfoUtil.isMiui()) {
                        onPermissionNotGranted(PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK);
                    } else {
                        onPermissionGranted(PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK);
                    }
                } else {
                    if (!isRequestedShowOnLock && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && SystemInfoUtil.isMiui()) {
                        SpecialPermissionsUtil.toXiaomiShowOnLockPermssion(this, PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK);
                    } else {
                        onPermissionGranted(PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK);
                    }
                }
                break;
            case PermissionUtils.REQUEST_CODE_AUTO_START://小米专用
                boolean isRequestedAutoStart = PreferenceHelper.getLong(PreferenceHelper.PREF_KEY_LAST_TO_XIAO_MI_AUTO_START_BOOT_PERMISSION_ACTIVITY, 0) > 0;
                if (isOnActivityResult) {
                    if (!isRequestedAutoStart && SystemInfoUtil.isMiui()) {
                        onPermissionNotGranted(PermissionUtils.REQUEST_CODE_AUTO_START);
                    } else {
                        onPermissionGranted(PermissionUtils.REQUEST_CODE_AUTO_START);
                    }
                } else {
                    if (!isRequestedAutoStart && SystemInfoUtil.isMiui()) {
                        SpecialPermissionsUtil.toXiaomiAutoStartPermission(this, PermissionUtils.REQUEST_CODE_AUTO_START);
                    } else {
                        onPermissionGranted(PermissionUtils.REQUEST_CODE_AUTO_START);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //特殊权限结果返回
        if (requestCode == PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION || requestCode == PermissionUtils.REQUEST_CODE_NOTIFICATION_LISTENER_SETTINGS
                || requestCode == PermissionUtils.REQUEST_CODE_WRITE_SETTINGS || requestCode == PermissionUtils.REQUEST_CODE_SHOW_ON_LOCK
                || requestCode == PermissionUtils.REQUEST_CODE_AUTO_START) {
            requestSpecialPermission(requestCode, true);
        }

        //华为悬浮窗权限
        if (requestCode == SpecialPermissionsUtil.REQUEST_CODE_FLOAT_WINDAOW_PERMISSION) {
            if (RomUtils.checkIsHuaweiRom()) {
                if (!SpecialPermissionsUtil.checkFloatWindowPermission(this)) {
                    onPermissionNotGranted(PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION);
                    return;
                }
            }
            requestSpecialPermission(PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION, true);
        }

        //跳转app details settings 返回
        if (requestCode == PermissionUtils.REQUEST_CODE_APP_DETAILS_SETTINGS) {
            if (!SpecialPermissionsUtil.checkFloatWindowPermission(this)) {
                onPermissionNotGranted(PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION);
                return;
            }
            requestSpecialPermission(PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION, true);
        }

    }

    @Override
    public void onPermissionGranted(int requestCode) {

    }

    @Override
    public void onPermissionNotGranted(int requestCode) {
        Toast.makeText(this, getString(R.string.permission_denied_txt), Toast.LENGTH_SHORT).show();
    }

    //跳转到系统app详情界面
    private void toAppDetailSetting() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, PermissionUtils.REQUEST_CODE_APP_DETAILS_SETTINGS);
    }

    private void showPermissionTipDialog(final String permissionFor) {
        Async.scheduleTaskOnUiThread(200, new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(BaseActivity.this, PermissionTipActivity.class);
                intent.putExtra("permission_for", permissionFor);
                startActivity(intent);
            }
        });
    }
    //**************************************请求权限 end**************************//
}
