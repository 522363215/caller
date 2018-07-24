package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;

import com.md.block.core.BlockManager;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.BlockPagerAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog.AddBlockContactDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.BlockListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.popup.BlockOptionWindow;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.NotScrollViewPager;

/**
 * Created by ChenR on 2018/7/5.
 */

public class BlockActivity extends BaseActivity implements View.OnClickListener {

    private NotScrollViewPager viewPager;
    private TabLayout tabBlockCategoryTitle;
    private View fivAddBlockContact;
    private View fivOption;

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
        fivAddBlockContact = findViewById(R.id.fiv_add);
        fivOption = findViewById(R.id.fiv_option);

        BlockListFragment contactFragment = BlockListFragment.newInstance(BlockListFragment.BLOCK_LIST_SHOW_CONTACT);
        BlockListFragment historyFragment = BlockListFragment.newInstance(BlockListFragment.BLOCK_LIST_SHOW_HISTORY);
        fragmentList.add(contactFragment);
        fragmentList.add(historyFragment);


        viewPager.setCurrentItem(mCurrentIndex);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        viewPager.setAdapter(new BlockPagerAdapter(getFragmentManager(), fragmentList));
        setAddBlockContactState(mCurrentIndex);
        tabBlockCategoryTitle.setupWithViewPager(viewPager);

    }

    private void listener() {
        fivOption.setOnClickListener(this);
        fivAddBlockContact.setOnClickListener(this);

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
                mCurrentIndex = position;
                startAddButtonAnim();
            }

            private void startAddButtonAnim() {
                ValueAnimator animator = new ValueAnimator();
                animator.setDuration(400);
                animator.setFloatValues(0, 1);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (isFinishing() || fivAddBlockContact == null) {
                            return;
                        }
                        float value = (float) animation.getAnimatedValue();
                        value = mCurrentIndex == BlockListFragment.BLOCK_LIST_SHOW_CONTACT ? value : (1 - value);
                        fivAddBlockContact.setScaleX(value);
                        fivAddBlockContact.setScaleY(value);
                    }
                });
                animator.start();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setAddBlockContactState(int position) {
        fivAddBlockContact.setVisibility(position == BlockListFragment.BLOCK_LIST_SHOW_CONTACT ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fiv_add: {
                AddBlockContactDialog dialog = new AddBlockContactDialog(BlockActivity.this);
                dialog.show();

                dialog.setOnAddBlockContactListener(new AddBlockContactDialog.OnAddBlockContactListener() {
                    @Override
                    public void onAddBlockContact(boolean isSuccess, String blockNumber) {
                        if (isSuccess) {
                            BlockListFragment fragment = (BlockListFragment) fragmentList.get(0);
                            fragment.updateData();
                        }
                    }
                });
            }
            break;
            case R.id.fiv_option: {
                BlockOptionWindow window = new BlockOptionWindow(BlockActivity.this, mCurrentIndex);
                window.setWidth(DeviceUtil.dp2Px(140));

                window.setClearCallback(new BlockOptionWindow.OnClearDataCallback() {
                    @Override
                    public void onClearData(int clearIndex) {
                        ((BlockListFragment) fragmentList.get(clearIndex)).updateData();
                    }
                });

                window.showAtLocation(fivOption, Gravity.END | Gravity.TOP, 40, 120);
            }
            break;
        }
    }

}
