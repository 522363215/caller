package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.md.block.core.BlockManager;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.quick.easyswipe.EasySwipe;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        listener();
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this);
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
                    EasySwipe.toggleEasySwipe(true);
                    EasySwipe.tryStartService();
                    ToastUtils.showToast(this, R.string.setting_enable_swipe_success_tip);
                    FlurryAgent.logEvent("SettingActivity----click----enable_swipe");
                } else {
                    EasySwipe.toggleEasySwipe(false);
                    EasySwipe.stopService();
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
}
