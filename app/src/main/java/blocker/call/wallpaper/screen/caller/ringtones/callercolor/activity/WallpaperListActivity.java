package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.FragmentAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.WallpaperListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.UsedPictureFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;

public class WallpaperListActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private List<Fragment> fragments;
    private int lastShowIndexFragment;
    private ActionBar mAbMessage;
    private ViewPager mVpMessage;
    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (lastShowIndexFragment != 0) {
                        mVpMessage.setCurrentItem(0);
                        lastShowIndexFragment = 0;
                    }
                    mAbMessage.setTitle(R.string.message_action_one);
                    return true;
                case R.id.navigation_dashboard:
                    if (lastShowIndexFragment != 1) {
                        mVpMessage.setCurrentItem(1);
                        lastShowIndexFragment = 1;
                    }
                    mAbMessage.setTitle(R.string.message_action_two);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentArray();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mVpMessage = findViewById(R.id.vp_message);

        mAbMessage = findViewById(R.id.ab_message);

        mAbMessage.setTitle(R.string.message_action_one);

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        mVpMessage.setAdapter(fragmentAdapter);
        mVpMessage.setOnPageChangeListener(this);

        ActionBar actionBar = findViewById(R.id.ab_message);
        actionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void translucentStatusBar() {

    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_message;
    }

    private void getFragmentArray() {
        WallpaperListFragment wallpaperListFragment = new WallpaperListFragment();
        UsedPictureFragment usedPictureFragment = new UsedPictureFragment();
        fragments = new ArrayList<>();
//        fragments.add(wallpaperListFragment);
        fragments.add(usedPictureFragment);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mAbMessage.setTitle(R.string.message_action_one);
                navigation.setSelectedItemId(R.id.navigation_home);
                break;
            case 1:
                mAbMessage.setTitle(R.string.message_action_two);
                navigation.setSelectedItemId(R.id.navigation_dashboard);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
