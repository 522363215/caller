package blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;

	public MainPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList, Context context) {
		super(fragmentManager);
			this.mFragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int arg0) {
		return mFragmentList.get(arg0);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	@Override
	public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
	}
}
