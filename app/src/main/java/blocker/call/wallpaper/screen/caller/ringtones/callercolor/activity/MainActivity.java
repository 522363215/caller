package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flurry.android.FlurryAgent;
import com.md.flashset.bean.CallFlashDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.MainPagerAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.NotifyInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventLanguageChange;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventRefreshCallFlashEnable;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.CallFlashListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.CategoryFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.MineFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.SideslipContraller;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.NotifyManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.PermissionUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.StatisticsUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.FontIconView;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.NotScrollViewPager;
import event.EventBus;

public class MainActivity extends BaseActivity implements View.OnClickListener, SideslipContraller.SideslipContrallerCallBack, PermissionUtils.PermissionGrant {
    private static final String TAG = "MainActivity";
    private final long TIME_DIFF = 2 * 1000;
    public static boolean sIsAlive = false;
    public int currentPage;
    private long mLastBackTime = 0;
    private NotScrollViewPager mViewPager;
    private List<android.app.Fragment> mFragmentList;
    private LinearLayout mLeft_drawer;
    private DrawerLayout mMain_drawer_layout;
    private SideslipContraller sideslipContraller;
    private RelativeLayout mTabHome;
    private RelativeLayout mTabCategory;
    private MainPagerAdapter mMainPagerAdapter;
    private CallFlashListFragment mCallFlashListFragment;
    private CategoryFragment mCategoryFragment;
    private MineFragment mMineFragment;
    private RelativeLayout mSideslipMenu;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private boolean mIsOnPageSelected;
    private TextView mTvPageTitle;

    private boolean isPostingMainData = false;
    private static ExecutorService mainFixedThreadPool = Executors.newFixedThreadPool(1);
    private RelativeLayout mTabMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().registerSticky(this);
        }
        toPermissionActivity();
        initView();
        //初始化page
        initViewPager();
        //初始化侧滑
        initSideSlip();
        listener();
        initIndex(getIntent());

        //发送统计
        SharedPreferences setting = ApplicationEx.getInstance().getGlobalSettingPreference();
        int used_day = setting.getInt("used_day", 0);
        if (used_day != Stringutil.getTodayDayInYearGMT8()) {
            if (!isPostingMainData) {
                isPostingMainData = true;
                CommonUtils.wrappedSubmit(mainFixedThreadPool, new Runnable() {
                    @Override
                    public void run() {
                        StatisticsUtil.sendMainData(MainActivity.this);
                    }
                });
            }
        }
    }

    private void toPermissionActivity() {
        boolean isShowPermissionActivity = PreferenceHelper.getLong(PreferenceHelper.PREF_LAST_REQUEST_PERMISSION_TIME, 0) <= 0;
        if (isShowPermissionActivity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_REQUEST_PERMISSION_TIME, System.currentTimeMillis());
            ActivityBuilder.toPermissionActivity(this, true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!BuildConfig.DEBUG) {
            FlurryAgent.setLogEnabled(false);
            FlurryAgent.setLogLevel(Log.VERBOSE);
            FlurryAgent.setLogEvents(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sIsAlive = true;
        FlurryAgent.logEvent("MainActivity-Main-showMain");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initIndex(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeSlideslip();
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (closeSlideslip()) {
            return;
        }
        if (now - mLastBackTime < TIME_DIFF) {
            if (!isFinishing()) {
                exitAPP();
            }
        } else {
            mLastBackTime = now;
            if (!isFinishing()) {
                Toast.makeText(this, getResources().getString(R.string.quithint), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layout_home:
                onHome();
                break;
            case R.id.layout_category:
                onCategory();
                break;
            case R.id.layout_mine:
                onMine();
                break;
            case R.id.sideslip_menu:
                openSideslip(v);
                break;
            case R.id.iv_upload:
                toImageUpload();

//                NotifyInfo info = new NotifyInfo();
//                info.setNotifyId(NotifyInfo.NotifyId.NOTIFY_NEW_FLASH);
//                info.setTitle("There are new flash.");
//                info.setContent("new flash don't show with you.");
//                info.arg1 = "https://djwtigu7b03af.cloudfront.net/material_manager/201807/05182947599.png";
//                info.arg2 = "https://djwtigu7b03af.cloudfront.net/material_manager/201807/05182947599.png";
//
//                NotifyManager.getInstance(this).showNewFlashWithBigStyle(info);
                break;
        }
    }

    private void toImageUpload() {
        Intent intent = new Intent();
        intent.setClass(this, MediaUploadActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //测试加一下，用于修复后台报的SkSweepGradient::shadeSpan崩溃
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sIsAlive = false;
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        killMe();
    }

    @Override
    public void menuClick() {
        FlurryAgent.logEvent("MainActivity--Main--CloseSlidingMenu");
        closeSlideslip();
    }

    private void initSideSlip() {
        //侧滑Controller
        sideslipContraller = new SideslipContraller(this);
        //侧滑
        sideslipContraller.setCallBack(this);
        mMain_drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (sideslipContraller != null)
                    sideslipContraller.enable();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //hideNewFeaturePoint();
            }
        });
    }

    private void initIndex(Intent intent) {
        currentPage = intent.getIntExtra("fragment_index", 0xff);
        //此值表示从callFlash 结果页返回时回到MainActivity 不需要改变page
        if (currentPage == ActivityBuilder.BACK_FROM_CALL_FLASH_RESULT) {
            return;
        }
        if (currentPage >= ActivityBuilder.MAX_FRAGEMNTS) {
            currentPage = ActivityBuilder.FRAGMENT_HOME;
        }
        mViewPager.setCurrentItem(currentPage, false);
        //防止首次进入时不回调onPageSelected
        Async.scheduleTaskOnUiThread(200, new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "addOnPageChangeListener onPageSelected mIsOnPageSelected:" + mIsOnPageSelected);
                if (!mIsOnPageSelected) {
                    mOnPageChangeListener.onPageSelected(currentPage);
                }
                mIsOnPageSelected = false;
            }
        });
    }

    private void initView() {
        mMain_drawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mLeft_drawer = (LinearLayout) findViewById(R.id.left_drawer);
        mViewPager = (NotScrollViewPager) findViewById(R.id.viewpager);
        mTvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        mSideslipMenu = (RelativeLayout) findViewById(R.id.sideslip_menu);

        //底部tab按钮
        mTabHome = (RelativeLayout) findViewById(R.id.layout_home);
        mTabCategory = (RelativeLayout) findViewById(R.id.layout_category);
        mTabMine = (RelativeLayout) findViewById(R.id.layout_mine);
    }

    public void listener() {
        mTabHome.setOnClickListener(this);
        mTabCategory.setOnClickListener(this);
        mTabMine.setOnClickListener(this);
        mSideslipMenu.setOnClickListener(this);
        findViewById(R.id.iv_upload).setOnClickListener(this);
    }

    private void killMe() {
        if (PreferenceHelper.getInt("pref_is_kill_to_release", 0) == 1) {
            try {
                android.os.Process.killProcess(android.os.Process.myPid());
                LogUtil.d("killMe", "killMe end: ");
            } catch (Exception e) {
                LogUtil.e("killMe", "killMe exception: " + e.getMessage());
            }
        }
    }

    private void onCategory() {
        mViewPager.setCurrentItem(ActivityBuilder.FRAGMENT_CATEGORY);
    }

    private void onMine() {
        mViewPager.setCurrentItem(ActivityBuilder.FRAGMENT_MINE);
    }

    private void onHome() {
        mViewPager.setCurrentItem(ActivityBuilder.FRAGMENT_HOME);
    }

    //打开侧滑
    public void openSideslip(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (v != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        FlurryAgent.logEvent("MainActivity--Main--OpenSlidingMenu");
        boolean drawerOpen = mMain_drawer_layout.isDrawerOpen(mLeft_drawer);
        if (!drawerOpen) {
            mMain_drawer_layout.openDrawer(mLeft_drawer);
        }
    }

    //关闭侧滑
    public boolean closeSlideslip() {
        boolean drawerOpen = mMain_drawer_layout.isDrawerOpen(mLeft_drawer);
        if (drawerOpen)
            mMain_drawer_layout.closeDrawer(mLeft_drawer);
        return drawerOpen;
    }

    private void initViewPager() {
        final int selectColor = getResources().getColor(R.color.fb_ad_green_button);
        final int unSelectColor = getResources().getColor(R.color.fb_ad_white_title);
        final int selectSize = 32;
        final int unSelectSize = 24;

        mCallFlashListFragment = CallFlashListFragment.newInstance(CallFlashDataType.CALL_FLASH_DATA_HOME);
//        mCategoryFragment = new CategoryFragment();
        mMineFragment = new MineFragment();

        mFragmentList = new ArrayList<>();
        mFragmentList.add(mCallFlashListFragment);
//        mFragmentList.add(mCategoryFragment);
        mFragmentList.add(mMineFragment);

        mMainPagerAdapter = new MainPagerAdapter(getFragmentManager(), mFragmentList, this);
        mViewPager.setAdapter(mMainPagerAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);
            }

            @Override
            public void onPageSelected(int arg0) {
                mIsOnPageSelected = true;
                currentPage = arg0;
                ((FontIconView) findViewById(R.id.fiv_home)).setTextColor(currentPage == ActivityBuilder.FRAGMENT_HOME ? selectColor : unSelectColor);
                ((FontIconView) findViewById(R.id.fiv_home)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, currentPage == ActivityBuilder.FRAGMENT_HOME ? selectSize : unSelectSize);
                ((FontIconView) findViewById(R.id.fiv_home)).setAlpha(currentPage == ActivityBuilder.FRAGMENT_HOME ? 1f : 0.4f);

                ((FontIconView) findViewById(R.id.fiv_category)).setTextColor(currentPage == ActivityBuilder.FRAGMENT_CATEGORY ? selectColor : unSelectColor);
                ((FontIconView) findViewById(R.id.fiv_category)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, currentPage == ActivityBuilder.FRAGMENT_CATEGORY ? selectSize : unSelectSize);
                ((FontIconView) findViewById(R.id.fiv_category)).setAlpha(currentPage == ActivityBuilder.FRAGMENT_CATEGORY ? 1f : 0.4f);

                ((FontIconView) findViewById(R.id.fiv_mine)).setTextColor(currentPage == ActivityBuilder.FRAGMENT_MINE ? selectColor : unSelectColor);
                ((FontIconView) findViewById(R.id.fiv_mine)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, currentPage == ActivityBuilder.FRAGMENT_MINE ? selectSize : unSelectSize);
                ((FontIconView) findViewById(R.id.fiv_mine)).setAlpha(currentPage == ActivityBuilder.FRAGMENT_MINE ? 1f : 0.4f);

                if (mCallFlashListFragment != null) {
                    if (arg0 == CallFlashDataType.CALL_FLASH_DATA_HOME)
                        mCallFlashListFragment.continuePlayVideo();
                    else
                        mCallFlashListFragment.pauseVideoPlay();
                }

                setTvTitle(arg0);
            }
        });
        mViewPager.setOffscreenPageLimit(ActivityBuilder.MAX_FRAGEMNTS);
    }

    private void setTvTitle(int position) {
        switch (position) {
            case ActivityBuilder.FRAGMENT_HOME:
                mTvPageTitle.setText(R.string.page_title_caller_theme);
                break;
            case ActivityBuilder.FRAGMENT_CATEGORY:
                mTvPageTitle.setText(R.string.page_title_category);
                break;
            case ActivityBuilder.FRAGMENT_MINE:
                mTvPageTitle.setText(R.string.page_title_mine);
                break;
        }
    }

    public void onEventMainThread(EventLanguageChange event) {
        finish();
    }

    private void exitAPP() {
        FlurryAgent.logEvent("ExitApplication");
        finish();
        Glide.get(this).clearMemory();
    }

    public void onEventMainThread(EventRefreshCallFlashEnable event) {
        boolean isEnable = event.isEnable();
        if (sideslipContraller != null) {
            sideslipContraller.setSwitchButton(isEnable);
        }
    }
}

