package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.block.core.BlockManager;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.mopub.test.manager.TestManager;
import com.quick.easyswipe.EasySwipe;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.SwipeManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.OKCancelDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.SwitchButton;

/**
 * Created by ChenR on 2018/7/14.
 */

public class SettingActivity extends BaseActivity implements SwitchButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = "SettingActivity";
    private SwitchButton sbBlock;
    private SwitchButton sbCallerId;
    private SwitchButton sbMessage;
    private SwitchButton sbCallFlash;
    private SwitchButton sbSwipe;
    private boolean enableBlock, enableCallerId, enableMessage, enableCallFlash, enableSwipe;
    private TextView tvSwitchBlock;
    private TextView tvSwitchCallerId;
    private TextView tvSwitchMessage;
    private TextView tvSwitchCallerShow;
    private TextView tvSwitchSwipe;
    private View layoutSwipe;

    private SharedPreferences pref;
    private Advertisement mAdvertisement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = ApplicationEx.getInstance().getGlobalSettingPreference();
        initAd();
        initView();
        listener();
    }

    @Override
    protected void translucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            View statusBarView = new View(this);
            int statusBarHeight = DeviceUtil.getStatusBarHeight();
            statusBarView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
            statusBarView.setBackgroundColor(getResources().getColor(R.color.transparent));

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup group = findViewById(R.id.layout_root);
            group.addView(statusBarView, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CommonUtils.translucentStatusBar(this, false);
        }
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("SettingActivity-----show_main");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void listener() {
        ((ActionBar) findViewById(R.id.action_bar)).setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sbBlock.setOnCheckedChangeListener(this);
        sbCallerId.setOnCheckedChangeListener(this);
        sbMessage.setOnCheckedChangeListener(this);
        sbCallFlash.setOnCheckedChangeListener(this);
        sbSwipe.setOnCheckedChangeListener(this);

        tvSwitchBlock.setOnClickListener(this);
        tvSwitchCallerId.setOnClickListener(this);
        tvSwitchMessage.setOnClickListener(this);
        tvSwitchCallerShow.setOnClickListener(this);
        tvSwitchSwipe.setOnClickListener(this);
    }

    private void initView() {
        sbBlock = findViewById(R.id.sb_block);
        sbCallerId = findViewById(R.id.sb_caller_id);
        sbMessage = findViewById(R.id.sb_message);
        sbCallFlash = findViewById(R.id.sb_caller_show);
        sbSwipe = findViewById(R.id.sb_swipe);

        tvSwitchBlock = findViewById(R.id.tv_switch_block);
        tvSwitchCallerId = findViewById(R.id.tv_switch_caller_id);
        tvSwitchMessage = findViewById(R.id.tv_switch_message);
        tvSwitchCallerShow = findViewById(R.id.tv_switch_caller_show);
        tvSwitchSwipe = findViewById(R.id.tv_switch_swipe);

        //安装了call id 则不显示swipe
        layoutSwipe = findViewById(R.id.layout_swipe);
        if (AdvertisementSwitcher.isAppInstalled(ConstantUtils.PACKAGE_CID) && !EasySwipe.isEasySwipeOn()) {
            layoutSwipe.setVisibility(View.GONE);
        } else {
            layoutSwipe.setVisibility(View.VISIBLE);
        }

        enableCallFlash = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, PreferenceHelper.DEFAULT_VALUE_FOR_CALL_FLASH);
        enableBlock = BlockManager.getInstance().getBlockSwitchState();
        enableCallerId = PreferenceHelper.getBoolean(PreferenceHelper.PREF_KEY_ENABLE_SHOW_CALL_AFTER, PreferenceHelper.DEFAULT_VALUE_FOR_CALLER_ID);
        enableMessage = PreferenceHelper.getBoolean(PreferenceHelper.SHOW_MESSAGE_COME, PreferenceHelper.DEFAULT_VALUE_FOR_MESSAGE);
        enableSwipe = EasySwipe.isEasySwipeOn();

        sbBlock.setChecked(enableBlock);
        sbCallerId.setChecked(enableCallerId);
        sbMessage.setChecked(enableMessage);
        sbCallFlash.setChecked(enableCallFlash);
        sbSwipe.setChecked(enableSwipe);
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.sb_block:
                enableBlock = isChecked;
                BlockManager.getInstance().setBlockSwitchState(isChecked);
                if (enableBlock) {
                    FlurryAgent.logEvent("SettingActivity----click----enable_block");
                } else {
                    FlurryAgent.logEvent("SettingActivity----click----disable_block");
                }
                break;
            case R.id.sb_caller_id:
                enableCallerId = isChecked;
                PreferenceHelper.putBoolean(PreferenceHelper.PREF_KEY_ENABLE_SHOW_CALL_AFTER, isChecked);
                if (isChecked) {
                    FlurryAgent.logEvent("SettingActivity----click----enable_call_assistant");
                } else {
                    FlurryAgent.logEvent("SettingActivity----click----disable_call_assistant");
                }
                break;
            case R.id.sb_message:
                enableMessage = isChecked;
                PreferenceHelper.putBoolean(PreferenceHelper.SHOW_MESSAGE_COME, isChecked);
                if (isChecked) {
                    FlurryAgent.logEvent("SettingActivity----click----enable_message");
                } else {
                    FlurryAgent.logEvent("SettingActivity----click----disable_message");
                }
                break;
            case R.id.sb_caller_show:
                enableCallFlash = isChecked;
                CallFlashPreferenceHelper.putBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, isChecked);
                if (isChecked) {
                    FlurryAgent.logEvent("SettingActivity----click----enable_call_flash");
                } else {
                    FlurryAgent.logEvent("SettingActivity----click----disable_call_flash");
                }
                break;
            case R.id.sb_swipe:
                enableSwipe = isChecked;
                if (isChecked) {
                    SwipeManager.getInstance().enableEasySwipe();
                    pref.edit().putBoolean("swipe_enable_by_user", true).apply();
                    pref.edit().putBoolean("swipe_disable_by_user", false).apply();
                    ToastUtils.showToast(this, R.string.setting_enable_swipe_success_tip);
                    FlurryAgent.logEvent("SettingActivity----click----enable_swipe");
                } else {
                    SwipeManager.getInstance().disableEasySwipe();
                    pref.edit().putBoolean("swipe_enable_by_user", false).apply();
                    pref.edit().putBoolean("swipe_disable_by_user", true).apply();
                    FlurryAgent.logEvent("SettingActivity----click----disable_swipe");
                }
                break;
        }
    }

    private void onSwitch(boolean enable, final SwitchButton sb, int desResId) {
        if (enable) {
            //关闭
            OKCancelDialog dialog = new OKCancelDialog(this);
            dialog.show();
            dialog.setTvTitle(desResId);
            dialog.setOKCancel(R.string.ok_string, R.string.no_string);
            dialog.setOkClickListener(new OKCancelDialog.OKClickListener() {
                @Override
                public void Ok() {
                    sb.setChecked(false);
                }
            });
        } else {
            sb.setChecked(true);
        }
    }

    private void onSwitchHavePermission(int viewId, boolean enable, final SwitchButton sb, int desResId) {
        if (enable) {
            //关闭
            OKCancelDialog dialog = new OKCancelDialog(this);
            dialog.show();
            dialog.setTvTitle(desResId);
            dialog.setOKCancel(R.string.ok_string, R.string.no_string);
            dialog.setOkClickListener(new OKCancelDialog.OKClickListener() {
                @Override
                public void Ok() {
                    sb.setChecked(false);
                }
            });
        } else {
            switch (viewId) {
                case R.id.tv_switch_swipe:
                    requestSpecialPermission(PermissionUtils.PERMISSION_OVERLAY);
                    break;
            }
        }
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        super.onPermissionGranted(requestCode);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION:
                sbSwipe.setChecked(true);
                break;
        }
    }

    @Override
    public void onPermissionNotGranted(int requestCode) {
        super.onPermissionNotGranted(requestCode);
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_OVERLAY_PERMISSION:
                ToastUtils.showToast(this, R.string.permission_denied_txt);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_switch_block:
                onSwitch(enableBlock, sbBlock, R.string.setting_close_block_tip);
                break;
            case R.id.tv_switch_caller_id:
                onSwitch(enableCallerId, sbCallerId, R.string.setting_close_call_assistant_tip);
                break;
            case R.id.tv_switch_caller_show:
                onSwitch(enableCallFlash, sbCallFlash, R.string.setting_close_caller_show_tip);
                break;
            case R.id.tv_switch_message:
                onSwitch(enableMessage, sbMessage, R.string.setting_close_block_tip);
                break;
            case R.id.tv_switch_swipe:
                onSwitchHavePermission(id, enableSwipe, sbSwipe, R.string.setting_close_swipe_tip);
                break;
        }
    }

    //***********************************************AD*******************************************//
    private void initAd() {
        MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(getWindow().getDecorView(),
                "",//ConstantUtils.FB_AFTER_CALL_ID
                "",//ConstantUtils.ADMOB_AFTER_CALL_NATIVE_ID
                Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,//Advertisement.ADMOB_TYPE_NATIVE, Advertisement.ADMOB_TYPE_NONE
                "",
                Advertisement.MOPUB_TYPE_NATIVE,
                -1,
                "",
                false);

        String mopub_banner_id = TestManager.getInstance(this.getApplicationContext()).getMopubId(AdvertisementSwitcher.SERVER_KEY_SET_MAIN);
        LogUtil.d("mopub_self", "mopub_banner_id flash preview: " + mopub_banner_id);
        adapter.setMopubBannerKey(mopub_banner_id); //mopub banner id
        mAdvertisement = new Advertisement(adapter);

        mAdvertisement.setRefreshWhenClicked(false);
        mAdvertisement.refreshAD(true);
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner, AdvertisementSwitcher.SERVER_KEY_SET_MAIN);
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baiduKey, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey, eventKey, AdvertisementSwitcher.SERVER_KEY_SET_MAIN, isBanner);
        }

        @Override
        public void onAdLoaded() {
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.facebook_native_ads_banner_50 : R.layout.facebook_no_icon_native_ads_call_after_big;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_callafter : R.layout.layout_admob_advanced_content_ad_callafter;
        }

        @Override
        public int getMoPubViewRes() {
            return mIsBanner ? R.layout.layout_mopub_ad_banner : R.layout.layout_mopub_native_big_call_after;
        }

        @Override
        public int getBaiDuViewRes() {
            return mIsBanner ? R.layout.layout_du_ad_banner : R.layout.layout_du_ad_call_after_big;
        }
    }
    //***********************************************AD*******************************************//
}
