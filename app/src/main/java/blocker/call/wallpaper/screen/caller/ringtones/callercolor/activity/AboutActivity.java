package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import java.lang.reflect.Method;
import java.util.Locale;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog.RatingDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.rate.ShareHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LanguageSettingUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private ActionBar actionbar;
    private TextView tv_rote_gp;
    private TextView tv_like_us_facebook;
    private TextView tv_send_apk_to_friends;
    private TextView share_on_facebook;
    private TextView tv_privacy_policy;
    private TextView tv_term_of_use;
    private TextView tv_version;
    private TextView tv_emailus;

    private String deviceInfo;
    private int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        CommonUtils.translucentStatusBar(this);
        initView();
        initData();
        listener();
        FlurryAgent.logEvent("AboutActivity--showMain");
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void initView() {
        actionbar = (ActionBar) findViewById(R.id.actionbar);
        tv_rote_gp = (TextView) findViewById(R.id.tv_rote_gp);
        tv_like_us_facebook = (TextView) findViewById(R.id.tv_like_us_facebook);
        tv_send_apk_to_friends = (TextView) findViewById(R.id.tv_send_apk_to_friends);
        share_on_facebook = (TextView) findViewById(R.id.share_on_facebook);
        tv_privacy_policy = (TextView) findViewById(R.id.tv_privacy_policy);
        tv_term_of_use = (TextView) findViewById(R.id.tv_term_of_use);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_emailus = (TextView) findViewById(R.id.tv_emailus);

        if (ApplicationEx.isReleaseHUAWEI()) {
            tv_rote_gp.setVisibility(View.GONE);
        }
    }

    private void initData() {
        //获取版本号
        PackageManager manager = getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(this.getPackageName(), 0);
            String versionName = packageInfo.versionName;
            tv_version.setText(versionName);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView version_text = (TextView) findViewById(R.id.version_text);
        version_text.setText(String.format(getString(R.string.about_product_version), getString(R.string.app_name).toUpperCase()));

        deviceInfo = getDeviceInfo();
    }

    private void listener() {
        tv_rote_gp.setOnClickListener(this);
        tv_like_us_facebook.setOnClickListener(this);
        tv_send_apk_to_friends.setOnClickListener(this);
        tv_privacy_policy.setOnClickListener(this);
        tv_term_of_use.setOnClickListener(this);
        share_on_facebook.setOnClickListener(this);
        tv_emailus.setOnClickListener(this);
        actionbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_rote_gp:
//                try {
//                    String packageName = getApplicationContext().getPackageName();
//                    Intent intent = new Intent(Intent.ACTION_VIEW, UriHelper.getGooglePlay(packageName));
//                    intent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
//                    startActivity(intent);
//                    System.gc();
//                } catch (Exception e) {
//                    String packageName = getApplicationContext().getPackageName();
//                    Intent intent = new Intent(Intent.ACTION_VIEW,
//                            UriHelper.getGooglePlay(packageName));
//                    AboutActivity.this.startActivity(intent);
//                }
                new RatingDialog(this).show();

                PreferenceHelper.putBoolean(PreferenceHelper.PREF_KEY_IS_AGREE_SHOW_DIALOG, false);
                FlurryAgent.logEvent("AboutActivity--click_rate_us_on_gp");
                break;
            case R.id.tv_like_us_facebook:
//                goFaceBook();
                break;
            case R.id.tv_send_apk_to_friends:
                ShareHelper.shareAPKByFile(this);
                break;
            case R.id.share_on_facebook:
//                showShare();
                break;
            case R.id.tv_privacy_policy:
                openPrivacy();
                FlurryAgent.logEvent("AboutActivity--click_privacy_policy");
                break;
            case R.id.tv_term_of_use:
//                goOpenUrl(ConstantUtils.URL_TERM_OF_USE, this.getString(R.string.about_term));
                break;
            case R.id.tv_emailus:
                sendEmail();
                break;
        }
    }

    private void goFaceBook() {
        String facebookUrl = ConstantUtils.URL_FACEBOOK_PAGE;
        boolean bRetry = false;
        try {
//            int versionCode = getPackageManager().getPackageInfo(
//                    "com.facebook.katana", 0).versionCode;
//            if (versionCode >= 3002850) {
//                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
//                startActivity(new Intent(Intent.ACTION_VIEW, uri));
//            } else {
//                // open the Facebook app using the old method (fb://profile/id
//                // or fb://pro
//                AboutActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
//                        .parse("fb://profile/<310869285773536>")));
//            }

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/")));
        } catch (Exception e) {
            bRetry = true;
        }

        if (bRetry) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
            } catch (Exception e) {
            }
        }
    }

    private void openPrivacy() {
        try {
            Intent intent = new Intent(this, BrowserActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("ccooler", "openPrivacy exception: " + e.getMessage());
        }
    }

    private void goOpenUrl(String url, String title) {
        try {
            Intent intent = new Intent(this, BrowserActivity.class);
            intent.putExtra("fromAboutUsURL", url);
            intent.putExtra("fromAboutUsTitle", title);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("ccooler", "goOpenUrl exception: " + e.getMessage());
        }
    }

    public void showShare() {
        FlurryAgent.logEvent("shapeApp");
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String share_url = String.format(this.getString(R.string.share_text), getString(R.string.app_name), ConstantUtils.URL_SHARE_APP);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                share_url);
        shareIntent.setType("text/plain");
        // 设置分享列表的标题，并且每次都显示分享列表
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    private String getDeviceInfo() {
        String deviceInfo = "";
//        —————————————————
//        For fixing problem, please keep info below:
//        Manufacturer:  LGE
//        RAM:  2GB
//        Model:  LGE Nexus 5
//        Resolution:  1920*1080
//        System Language:  en
//        App Language:  en
//        Android Version:  6.0.1
//        PB Version:  95
        StringBuilder sb = new StringBuilder();
        try {
            String app_lan = LanguageSettingUtil.getInstance(getApplicationContext()).getLanguage();
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

    private void sendEmail() {
        FlurryAgent.logEvent("about_Email_us");
        try {

            String mailto = "contact@lionmobi.com";
            String[] tos = {mailto};

            Intent emailintent = new Intent(
                    Intent.ACTION_SEND);
            emailintent.putExtra(Intent.EXTRA_EMAIL, tos);
            emailintent.setType("text/html");
//            data.setType("message/rfc882");
//            data.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
            emailintent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            emailintent.putExtra(Intent.EXTRA_TEXT, deviceInfo);
            startActivity(Intent.createChooser(emailintent,
                    getString(R.string.email_choose_one)));
        } catch (Exception e) {
            //ignore
            LogUtil.e("nmlogs", "sendEmail exception: " + e.getMessage());
        }
    }

    private String getResolution() {

        String resolution = "";

        int width = 0;
        int height = 0;

        if (Build.VERSION.SDK_INT >= 17) {
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(size);
            width = size.x;
            height = size.y;
        } else if (Build.VERSION.SDK_INT >= 14) {
            Display display = getWindowManager().getDefaultDisplay();
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
}
