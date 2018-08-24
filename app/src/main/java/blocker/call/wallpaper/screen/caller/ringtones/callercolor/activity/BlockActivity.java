package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;

import com.flurry.android.FlurryAgent;
import com.md.block.core.BlockManager;
import com.mopub.test.manager.TestManager;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.FacebookConstant;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.adapter.BlockPagerAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog.AddBlockContactDialog;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.fragment.BlockListFragment;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.popup.BlockOptionWindow;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.NotScrollViewPager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.OKCancelDialog;

/**
 * Created by ChenR on 2018/7/5.
 */

public class BlockActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_PERMISSION_CODE = 1086;

    private NotScrollViewPager viewPager;
    private TabLayout tabBlockCategoryTitle;
    private View fivAddBlockContact;
    private View fivOption;

    private int mCurrentIndex = 0;

    private List<Fragment> fragmentList = new ArrayList<>();
    private boolean mIsComeBlockNotify;
    private Advertisement mAdvertisement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                        != PackageManager.PERMISSION_GRANTED) {
            showRequestPermission();
        } else {
            initAds();
            initView();
            listener();
            onNewIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mIsComeBlockNotify = intent.getBooleanExtra("is_come_block_notify", false);
        if (mIsComeBlockNotify) {
            viewPager.setCurrentItem(BlockListFragment.BLOCK_LIST_SHOW_HISTORY);
        }
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this);
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_block;
    }

    public void showRequestPermission() {
        FlurryAgent.logEvent("BlockActivity-----show_request_permission_dialog");
        OKCancelDialog dialog = new OKCancelDialog(this, true);
        dialog.show();
        dialog.setOKCancel(R.string.ok_string, R.string.no_string);
        dialog.setContent(getString(R.string.block_list_request_permission_tip, getString(R.string.app_name)), true, false);

        dialog.setOkClickListener(new OKCancelDialog.OKClickListener() {
            @Override
            public void Ok() {
                requestPermission(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG}, REQUEST_PERMISSION_CODE);
            }
        });
        dialog.setOnCancelClickListener(new OKCancelDialog.OnCancelClickListener() {
            @Override
            public void cancel() {
                finish();
            }
        });
    }

    @Override
    public void onPermissionGranted(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            initAds();
            initView();
            listener();
            onNewIntent(getIntent());
        }
    }

    @Override
    public void onPermissionNotGranted(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FlurryAgent.logEvent("BlockActivity-show");
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
                FlurryAgent.logEvent("BlockActivity-----click-----show_add_block_contacts_dialog");
                AddBlockContactDialog dialog = new AddBlockContactDialog(BlockActivity.this);
                dialog.show();

                dialog.setOnAddBlockContactListener(new AddBlockContactDialog.OnAddBlockContactListener() {
                    @Override
                    public void onAddBlockContact(boolean isSuccess, String blockNumber) {
                        if (isSuccess) {
                            BlockManager.getInstance().setBlockSwitchState(true);
                            BlockListFragment fragment = (BlockListFragment) fragmentList.get(0);
                            fragment.updateData();
                        }
                    }
                });
            }
            break;
            case R.id.fiv_option: {
                FlurryAgent.logEvent("BlockActivity----click-----show_block_option_dialog");
                BlockOptionWindow window = new BlockOptionWindow(BlockActivity.this, mCurrentIndex);
                window.setWidth(DeviceUtil.dp2Px(180));

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


    //************************************************AD******************************************//
    private void initAds() {
        MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(getWindow().getDecorView(),
                "",
                "",
                Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,
                "",
                Advertisement.MOPUB_TYPE_NATIVE,
                -1,
                "",
                AdvertisementSwitcher.SERVER_KEY_BLOCK_MAIN,
                true);

        mAdvertisement = new Advertisement(adapter);
        mAdvertisement.setRefreshWhenClicked(true);
        mAdvertisement.refreshAD(true);

        String mopub_banner_id = TestManager.getInstance(this.getApplicationContext()).getMopubId(AdvertisementSwitcher.SERVER_KEY_BLOCK_MAIN);
        LogUtil.d("mopub_self", "mopub_banner_id sms show set: " + mopub_banner_id);
        adapter.setMopubBannerKey(mopub_banner_id); //mopub banner id

        if (CallerAdManager.isOnlyBtnClickable(CallerAdManager.POSITION_FB_ADS_SMS_FLASH_SHOW_SET)) {
            mAdvertisement.enableOnlyBtnClickable();
        }
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, String placementId, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner, placementId);//SERVER_KEY_CALL_FLASH_SETTING
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baiduKey, String eventKey, String placementId, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey, eventKey, placementId, isBanner);
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
        }

        @Override
        public void onAdClicked(boolean isAdmob) {
            super.onAdClicked(isAdmob);
        }

        @Override
        public void onAdShow() {
            super.onAdShow();
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.facebook_native_ads_banner_50 : FacebookConstant.FB_AD_BIG_VIEW_RES_ID;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_flash : R.layout.layout_admob_advanced_content_ad_flash;
        }

        @Override
        public int getMoPubViewRes() {
            return mIsBanner ? R.layout.layout_mopub_ad_banner_flash_show : R.layout.layout_mopub_no_icon_native_ads;
        }

        @Override
        public int getBaiDuViewRes() {
            return mIsBanner ? R.layout.layout_du_ad_banner_falsh_show : R.layout.layout_du_ad_big;
        }
    }
    //************************************************AD******************************************//
}
