package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.md.flashset.bean.CallFlashDataType;
import com.md.serverflash.beans.Category;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.CallFlashListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.CallFlashLocalListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;

public class CategoryDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this);
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_detail_category;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Category category = (Category) getIntent().getSerializableExtra("category_info");
        if (category != null) {
            int categoryId = Integer.parseInt(category.getPx_id());
            //本地call flash
            if (categoryId == -1024) {
                CallFlashLocalListFragment callFlashLocalListFragment = new CallFlashLocalListFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.layout_container, callFlashLocalListFragment).commit();
            } else {
                CallFlashListFragment callFlashListFragment = CallFlashListFragment.newInstance(CallFlashDataType.CALL_FLASH_DATA_CATEGORY, Integer.parseInt(category.getPx_id()));
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.layout_container, callFlashListFragment).commit();
            }

            setActionBarTitle(category.getTitle());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("CategoryDetailActivity-----show_main");
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = findViewById(R.id.action_bar);
        actionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        actionBar.setTitle(title);
    }
}
