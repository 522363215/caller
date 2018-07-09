package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;


import java.util.List;

/**
 * Created by ChenR on 2018/7/7.
 */

public class BlockPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> model;

    private String [] mPageTitle = {"Contacts", "History"};

    public BlockPagerAdapter(FragmentManager fm, List<android.app.Fragment> model) {
        super(fm);
        this.model = model;
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
