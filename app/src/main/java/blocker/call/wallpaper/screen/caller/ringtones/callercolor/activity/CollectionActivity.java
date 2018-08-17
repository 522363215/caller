package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.md.flashset.bean.CallFlashDataType;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCollection;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.CallFlashListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import event.EventBus;

public class CollectionActivity extends BaseActivity {
    private CallFlashListFragment mCallFlashListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setActionBar();

        mCallFlashListFragment = CallFlashListFragment.newInstance(CallFlashDataType.CALL_FLASH_DATA_COLLECTION);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.layout_container, mCallFlashListFragment).commit();
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this, false);

    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_collection;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void setActionBar() {
        ActionBar actionBar = findViewById(R.id.action_bar);
        actionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onEventMainThread(EventRefreshCollection event) {
        if (mCallFlashListFragment != null) {
            mCallFlashListFragment.initData(true);
        }
    }
}
