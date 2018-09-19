package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.md.callring.Constant;
import com.md.wallpaper.bean.WallpaperDataType;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.WallpaperListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;

public class AssortmentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intiView();
    }

    private void intiView() {
        int assort = getIntent().getIntExtra(Constant.ISASSORTMENT,0);
        String name = getIntent().getStringExtra(Constant.NAME);
        ActionBar abAssort = findViewById(R.id.ab_assort);
        abAssort.setTitle(name);
        WallpaperListFragment wallpaperListFragment = WallpaperListFragment.newInstance(WallpaperDataType.WALLPAPER_DATA_ASSORTMENT);
        getFragmentManager()
                .beginTransaction()
                .add(R.id.rl_assort,wallpaperListFragment)
                .commit();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.ISASSORTMENT,assort);
        wallpaperListFragment.setArguments(bundle);
    }

    @Override
    protected void translucentStatusBar() {

    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_assortment;
    }


}
