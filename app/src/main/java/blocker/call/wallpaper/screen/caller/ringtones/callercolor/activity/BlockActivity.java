package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.BlockPagerAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.BlockListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.NotScrollViewPager;

/**
 * Created by ChenR on 2018/7/5.
 */

public class BlockActivity extends BaseActivity implements View.OnClickListener{

    private NotScrollViewPager viewPager;
    private TabLayout tabBlockCategoryTitle;

    private int mCurrentIndex = 0;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        initView();
        listener();
    }

    private void initView() {
        viewPager = findViewById(R.id.viewpager);
        tabBlockCategoryTitle = findViewById(R.id.tab_block_category_title);

        BlockListFragment contactFragment = BlockListFragment.newInstance(BlockListFragment.BLOCK_LIST_SHOW_CONTACT);
        BlockListFragment historyFragment = BlockListFragment.newInstance(BlockListFragment.BLOCK_LIST_SHOW_HISTORY);
        fragmentList.add(contactFragment);
        fragmentList.add(historyFragment);

        viewPager.setCurrentItem(mCurrentIndex);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        viewPager.setAdapter(new BlockPagerAdapter(getFragmentManager(), fragmentList));
        tabBlockCategoryTitle.setupWithViewPager(viewPager);
    }

    private void listener() {
        ((ActionBar) findViewById(R.id.action_bar)).setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

}
