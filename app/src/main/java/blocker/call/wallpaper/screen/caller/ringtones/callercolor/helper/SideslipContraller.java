package blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;
import com.md.flashset.helper.CallFlashPreferenceHelper;

import java.lang.reflect.Method;
import java.util.Locale;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.AboutActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.BlockActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.CollectionActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.MainActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.SettingActivity;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog.RatingDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.AppUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.SwitchButton;

/**
 * Created by Admin on 2016/10/18.
 */
public class SideslipContraller implements View.OnClickListener {
    private MainActivity mAct;
    private View root;
    private View mMenuBlock;
    private View mMenuSettings;
    private View mMenuRate;
    private View mMenuAbout;
    private View mMenuTest;
    private String deviceInfo;
    private int versionCode;
    private View mMenuCollection;
    private View mMenuFeedback;
    private SwitchButton mSwitchButton;
    private View mMenuPermission;

    public SideslipContraller(MainActivity mAct) {
        this.mAct = mAct;
    }

    public void enable() {
        if (root == null) {
            ViewStub stub = (ViewStub) mAct.findViewById(R.id.vstub_mian_sideslip);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppUtils.getStatusBasHeight(mAct));
                View statusBasHeightView = mAct.findViewById(R.id.status_bas_height);
                statusBasHeightView.setLayoutParams(layout);
            }
            root = stub.inflate();

            mSwitchButton = root.findViewById(R.id.sb_enable);
            mMenuBlock = root.findViewById(R.id.menu_block);
            mMenuCollection = root.findViewById(R.id.menu_collection);
            mMenuSettings = root.findViewById(R.id.menu_settings);
            mMenuRate = root.findViewById(R.id.menu_rate);
            mMenuFeedback = root.findViewById(R.id.menu_feedback);
            mMenuAbout = root.findViewById(R.id.menu_about);
            mMenuTest = root.findViewById(R.id.menu_test);
            mMenuPermission = root.findViewById(R.id.menu_permission);

            mMenuBlock.setOnClickListener(this);
            mMenuCollection.setOnClickListener(this);
            mMenuSettings.setOnClickListener(this);
            mMenuRate.setOnClickListener(this);
            mMenuFeedback.setOnClickListener(this);
            mMenuAbout.setOnClickListener(this);
            mMenuTest.setOnClickListener(this);
            mMenuPermission.setOnClickListener(this);

            boolean isCallFlashOn = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false);
            setSwitchButton(isCallFlashOn);

            PackageManager manager = mAct.getPackageManager();
            try {
                PackageInfo packageInfo = manager.getPackageInfo(mAct.getPackageName(), 0);
                String versionName = packageInfo.versionName;
                versionCode = packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            deviceInfo = getDeviceInfo();
        }
    }

    public void setSwitchButton(boolean isCallFlashOn) {
        mSwitchButton.setChecked(isCallFlashOn);
        mSwitchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                CallFlashPreferenceHelper.putBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, isChecked);
            }
        });
    }

    private String getDeviceInfo() {
        String deviceInfo = "";
        StringBuilder sb = new StringBuilder();
        try {
            String app_lan = LanguageSettingUtil.getInstance(mAct.getApplicationContext()).getLanguage();
            Locale locale = Resources.getSystem().getConfiguration().locale;
            String sys_lan = locale.getLanguage();

            if (app_lan.equals(LanguageSettingUtil.AUTO)) {
                app_lan = sys_lan;
            }
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            sb.append("—————————————————");
            sb.append("\n");
            sb.append("For fixing problem, please keep info below: ");
            sb.append("\n");
            sb.append("Manufacturer: " + Build.MANUFACTURER);
            sb.append("\n");
            sb.append("RAM: " + DeviceUtil.getTotalMem());
            sb.append("\n");
            sb.append("Model: " + DeviceUtil.getDeviceModel());
            sb.append("\n");
            sb.append("Resolution: " + getResolution());
            sb.append("\n");
            sb.append("System Language: " + sys_lan);
            sb.append("\n");
            sb.append("App Language: " + app_lan);
            sb.append("\n");
            sb.append("Android Version: " + Build.VERSION.RELEASE);
            sb.append("\n");
            sb.append("CID Version: " + versionCode);

        } catch (Exception e) {
            LogUtil.e("nmlogs", "send Email getDeviceInfo exception: " + e.getMessage());
        }
        deviceInfo = sb.toString();
        return deviceInfo;
    }

    private String getResolution() {
        String resolution = "";

        int width = 0;
        int height = 0;

        if (Build.VERSION.SDK_INT >= 17) {
            Point size = new Point();
            mAct.getWindowManager().getDefaultDisplay().getRealSize(size);
            width = size.x;
            height = size.y;
        } else if (Build.VERSION.SDK_INT >= 14) {
            Display display = mAct.getWindowManager().getDefaultDisplay();
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                width = (Integer) mGetRawW.invoke(display);
                height = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                width = display.getWidth();
                height = display.getHeight();
                LogUtil.e("nmlogs", "sendEmail getResolution exception: " + e.getMessage());
            }

        }

        resolution = height + "*" + width;
        return resolution;

    }

    @Override
    public void onClick(View v) {
        if (mAct == null || mAct.isFinishing()) {
            return;
        }
        switch (v.getId()) {
            case R.id.menu_test:
                onTest();
                break;
            case R.id.menu_block:
                onBlock();
                break;
            case R.id.menu_settings:
                onSettings();
                break;
            case R.id.menu_rate:
                onRate();
                break;
            case R.id.menu_about:
                onAbout();
                break;
            case R.id.menu_feedback:
                sendEmail();
                break;
            case R.id.menu_collection:
                onCollection();
                break;
            case R.id.menu_permission:
                onPermission();
                break;
        }
        if (callBack != null) {
            callBack.menuClick();
        }
    }

    private void onPermission() {
        ActivityBuilder.toPermissionActivity(mAct, false);
    }

    private void onCollection() {
        FlurryAgent.logEvent("left_collection_click");
        if (mAct == null) return;
        Intent intent = new Intent();
        intent.setClass(mAct, CollectionActivity.class);
        mAct.startActivity(intent);
    }

    private void sendEmail() {
        FlurryAgent.logEvent("about_Email_us");
        try {

            String mailto = "contact@lionmobi.com";
            String[] tos = {mailto};

            Intent emailintent = new Intent(
                    Intent.ACTION_SEND);
            emailintent.putExtra(Intent.EXTRA_EMAIL, tos);
            emailintent.setType("text/html");
            emailintent.putExtra(Intent.EXTRA_SUBJECT, mAct.getString(R.string.app_name));
            emailintent.putExtra(Intent.EXTRA_TEXT, deviceInfo);
            mAct.startActivity(Intent.createChooser(emailintent,
                    mAct.getString(R.string.email_choose_one)));
        } catch (Exception e) {
            //ignore
            LogUtil.e("nmlogs", "sendEmail exception: " + e.getMessage());
        }
    }

    /**
     * 用于测试
     */
    private void onTest() {
//        mAct.startActivity(new Intent(mAct, DoNotDisturbActivity.class));
    }

    private void onBlock() {
        FlurryAgent.logEvent("left_block_click");
        Intent intent = new Intent();
        intent.setClass(mAct, BlockActivity.class);
        mAct.startActivity(intent);
    }

    private void onSettings() {
        FlurryAgent.logEvent("left_setting_click");
        Intent intent = new Intent();
        intent.setClass(mAct, SettingActivity.class);
        mAct.startActivity(intent);
    }

    private void onAbout() {
        FlurryAgent.logEvent("left_about_click");
        if (mAct == null) return;
        Intent intentAbout = new Intent(mAct, AboutActivity.class);
        mAct.startActivity(intentAbout);
    }

    private void onRate() {
        FlurryAgent.logEvent("left_rate_us_click");
        new RatingDialog(mAct).show();
    }

    private SideslipContrallerCallBack callBack;

    public interface SideslipContrallerCallBack {
        void menuClick();
    }

    public void setCallBack(SideslipContrallerCallBack callBack) {
        this.callBack = callBack;
    }
}
