package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import java.util.Locale;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.service.LocalService;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;

public class BaseActivity extends Activity implements PermissionUtils.PermissionGrant {

    protected LanguageSettingUtil languageSetting;
    protected ApplicationEx app;
    //protected AppEventsLogger logger;

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
        setContentView(R.layout.view_null);
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Log.d("ACT", this.getClass().getSimpleName() + "(onCreate)");
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
        SwitchLang();
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

    @Override
    public void onPermissionGranted(int requestCode) {

    }

    @Override
    public void onPermissionNotGranted(int requestCode) {
        Toast.makeText(this, getString(R.string.permission_denied_txt), Toast.LENGTH_SHORT).show();
    }
    //**************************************请求权限 end**************************//
}
