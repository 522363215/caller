package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.md.flashset.bean.CallFlashDataType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCollection;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.CallFlashListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;

public class CallFlashListActivity extends BaseActivity {
    private CallFlashListFragment mCallFlashListFragment;
    private int mDataType;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        onNewIntent(getIntent());
        setActionBar();
        mCallFlashListFragment = CallFlashListFragment.newInstance(mDataType);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.layout_container, mCallFlashListFragment).commit();
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this);
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_call_flash_list;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mDataType = intent.getIntExtra(ActivityBuilder.CALL_FLASH_DATA_TYPE, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("CallFlashListActivity-----show_main");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void setActionBar() {
        mActionBar = findViewById(R.id.action_bar);
        mActionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitle();
    }

    private void setTitle() {
        switch (mDataType) {
            case CallFlashDataType.CALL_FLASH_DATA_COLLECTION:
                mActionBar.setTitle(R.string.side_slip_collection);
                break;
            case CallFlashDataType.CALL_FLASH_DATA_DOWNLOADED:
                mActionBar.setTitle(R.string.mine_downloaded);
                break;
            case CallFlashDataType.CALL_FLASH_DATA_SET_RECORD:
                mActionBar.setTitle(R.string.mine_set_before);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(EventRefreshCollection event) {
        if (mCallFlashListFragment != null) {
            mCallFlashListFragment.initData(true);
        }
    }
}
