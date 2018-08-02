package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.os.Bundle;
import android.view.View;

import com.md.block.core.BlockManager;
import com.md.flashset.helper.CallFlashPreferenceHelper;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.SwitchButton;

/**
 * Created by ChenR on 2018/7/14.
 */

public class SettingActivity extends BaseActivity implements SwitchButton.OnCheckedChangeListener {

    private SwitchButton sbBlock;
    private SwitchButton sbCallerId;
    private SwitchButton sbMessage;
    private SwitchButton sbCallFlash;
    private boolean enableBlock, enableCallerId, enableMessage, enableCallFlash;

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
    }

    private void initView() {
        sbBlock = findViewById(R.id.sb_block);
        sbCallerId = findViewById(R.id.sb_caller_id);
        sbMessage = findViewById(R.id.sb_message);
        sbCallFlash = findViewById(R.id.sb_caller_show);

        enableCallFlash = CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, PreferenceHelper.DEFAULT_VALUE_FOR_CALL_FLASH);
        enableBlock = BlockManager.getInstance().getBlockSwitchState();
        enableCallerId = PreferenceHelper.getBoolean(PreferenceHelper.PREF_KEY_ENABLE_SHOW_CALL_AFTER, PreferenceHelper.DEFAULT_VALUE_FOR_CALLER_ID);
        enableMessage = PreferenceHelper.getBoolean(PreferenceHelper.SHOW_MESSAGE_COME, PreferenceHelper.DEFAULT_VALUE_FOR_MESSAGE);

        sbBlock.setChecked(enableBlock);
        sbCallerId.setChecked(enableCallerId);
        sbMessage.setChecked(enableMessage);
        sbCallFlash.setChecked(enableCallFlash);
    }


    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.sb_block:
                enableBlock = isChecked;
                BlockManager.getInstance().setBlockSwitchState(isChecked);
                break;
            case R.id.sb_caller_id:
                enableCallerId = isChecked;
                PreferenceHelper.putBoolean(PreferenceHelper.PREF_KEY_ENABLE_SHOW_CALL_AFTER, isChecked);
                break;
            case R.id.sb_message:
                enableMessage = isChecked;
                PreferenceHelper.putBoolean(PreferenceHelper.SHOW_MESSAGE_COME, isChecked);
                break;
            case R.id.sb_caller_show:
                enableCallFlash = isChecked;
                CallFlashPreferenceHelper.putBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, isChecked);
                break;
        }
    }
}
