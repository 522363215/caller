package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;


import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

/**
 * Created by ChenR on 2018/7/7.
 */

public class BlockPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> model;

    private String [] mPageTitle;

    public BlockPagerAdapter(FragmentManager fm, List<android.app.Fragment> model) {
        super(fm);
        this.model = model;
        mPageTitle = new String[model.size()];
        mPageTitle[0] = ApplicationEx.getInstance().getString(R.string.block_list_tab_contact);
        mPageTitle[1] = ApplicationEx.getInstance().getString(R.string.block_list_tab_logs);
    }

    @Override
    public Fragment getItem(int position) {
        return model.get(position);
    }

    @Override
    public int getCount() {
        return model.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitle[position];
    }
}
