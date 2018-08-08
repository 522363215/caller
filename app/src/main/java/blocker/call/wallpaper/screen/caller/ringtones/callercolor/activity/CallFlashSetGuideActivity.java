package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashClassification;
import com.md.flashset.bean.CallFlashFormat;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.download.DownloadState;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.GuideUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.callflash.CallFlashView;

public class CallFlashSetGuideActivity extends BaseActivity implements View.OnClickListener {
    private CallFlashView mCallFlashView;
    private View mFivClose;
    private View mTvButton;
    private CallFlashInfo mCallFlashInfo;

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this);
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_call_flash_set_guide;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();

        mCallFlashView.showCallFlashView(mCallFlashInfo);
    }

    private void initData() {
        mCallFlashInfo = new CallFlashInfo();
        mCallFlashInfo.id = String.valueOf(FlashLed.FLASH_TYPE_LOVE);
        mCallFlashInfo.title = getString(com.md.flashset.R.string.call_flash_led_love);
        mCallFlashInfo.imgResId = com.md.flashset.R.drawable.icon_flash_love_small;
        mCallFlashInfo.format = CallFlashFormat.FORMAT_CUSTOM_ANIM;
        mCallFlashInfo.path = "";
        mCallFlashInfo.url = "";
        mCallFlashInfo.isDownloadSuccess = true;
        mCallFlashInfo.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        mCallFlashInfo.flashType = FlashLed.FLASH_TYPE_LOVE;
        mCallFlashInfo.position = 6;
        mCallFlashInfo.flashClassification = CallFlashClassification.CLASSIFICATION_CLASSIC;
        mCallFlashInfo.downloadSuccessTime = -3;
    }

    private void initView() {
        mCallFlashView = findViewById(R.id.call_flash_view);
        mFivClose = findViewById(R.id.fiv_close);
        mTvButton = findViewById(R.id.tv_button);

        CardView cardView = mCallFlashView.findViewById(R.id.layout_card_view);
        cardView.setCardElevation(0);
        cardView.setPreventCornerOverlap(false);
        cardView.setUseCompatPadding(false);

        mFivClose.setOnClickListener(this);
        mTvButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("CallFlashSetGuideActivity-----show_main");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fiv_close:
                GuideUtil.toPermissionGuide(this);
                finish();
                break;
            case R.id.tv_button:
                FlurryAgent.logEvent("CallFlashSetGuideActivity-----click----to_call_flash_detail");
                ActivityBuilder.toCallFlashDetail(this, mCallFlashInfo, false, true);
                break;
        }
    }
}
