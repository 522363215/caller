package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.md.flashset.bean.CallFlashDataType;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.CallFlashListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;

public class CollectionActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        setActionBar();

        CallFlashListFragment callFlashListFragment = CallFlashListFragment.newInstance(CallFlashDataType.CALL_FLASH_DATA_COLLECTION);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.layout_container, callFlashListFragment).commit();
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
}
