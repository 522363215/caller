package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.widget.FrameLayout;

import com.quick.easyswipe.EasySwipe;
import com.quick.easyswipe.callback.EasySwipeFunctionCallback;
import com.quick.easyswipe.callback.EasySwipeViewCallback;
import com.quick.easyswipe.callback.QuickSwitchCallback;
import com.quick.easyswipe.callback.QuickSwitchResultCallback;
import com.quick.easyswipe.callback.ServerConfigCallback;
import com.quick.easyswipe.type.EasySwipeItem;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity.ActivityBuilder;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

public class SwipeManager {
    private static final String TAG = "SwipeManager";
    private static SwipeManager sInstance;
    private Advertisement mAdvertisement;
    private boolean mIsAdLoading;

    private static final int AD_REFRESH_MINUTE = 15;//单位分钟

    private SwipeManager() {
    }

    public static SwipeManager getInstance() {
        if (sInstance == null) {
            synchronized (SwipeManager.class) {
                sInstance = new SwipeManager();
                return sInstance;
            }
        }
        return sInstance;
    }

    public void initEasySwipe(final Application application) {
        // 1. 全局初始化, 传入Applicatoin
        EasySwipe.init(application);
        // 2. 快划布局的隐藏和显示回调, 用于显示广告
        EasySwipe.setEasySwipeViewCallback(new EasySwipeViewCallback() {
            @Override
            public void swipeViewShown(FrameLayout frameLayout) {
                //frameLayout是侧滑布局中的广告布局
                initAd(frameLayout);
            }

            @Override
            public void swipeViewHidden() {
            }
        });
        // 3. 通过服务器配置，设置快划功能
        EasySwipe.setServerConfigCallback(new ServerConfigCallback() {
            // 快划是否默认开启, 默认为false
            @Override
            public boolean defaultEasySwipeOn() {
                return false;//ServerConfig.esp_def_on
            }

            // 点击快划页面空白区域是否自动关闭, 默认为false(不关闭)
            @Override
            public boolean blankClickCancelable() {
                return true;//ServerConfig.esp_blank_clk_ccb
            }

            // "常用应用"的菜单是否显示自己, 默认为false(不显示)
            @Override
            public boolean needDisplaySelf() {
                return false;
            }

            // "最近应用"菜单中是否隐藏自己, 默认为true(隐藏)
            @Override
            public boolean needHideInRecent() {
                return true;
            }

            // 点击快划菜单角落的关闭按钮(叉), 延迟关闭快划布局的时间（毫秒), 默认为0
            @Override
            public long delayedCloseSwipeViewTimeMillis() {
                return 0;//ServerConfig.esp_close_dly_mm
            }

            // 快划菜单栏中，是否在快捷工具栏中显示手电筒菜单, 需要camera权限
            @Override
            public boolean enableFlashLight() {
                return false;
            }
            // 快划菜单快捷工具栏中是否显示蓝牙功能，需要Bluetooth相关权限，建议不使用(false)

            // 仅在1.0.4.8支持
            @Override
            public boolean enableBluetooth() {
                return false;
            }
        });
        // 4. 设置快划菜单中"快捷工具"按钮行为
        EasySwipe.setQuickSwitchCallback(new QuickSwitchCallback() {
            @Override
            public void openSettingActivity(QuickSwitchResultCallback quickSwitchResultCallback) {
                ActivityBuilder.toSettingActivity(application.getApplicationContext());
                if (quickSwitchResultCallback != null) {
                    //true-使用了外部打开设置页面的方式
                    quickSwitchResultCallback.isUseExternal(true);
                }
            }

            @Override
            public void openFlashlight(QuickSwitchResultCallback quickSwitchResultCallback) {
                //点击手电筒按钮
                if (quickSwitchResultCallback != null) {
                    //如果使用其他方式打开手电筒 isUseExternal返回true
                    //使用easeyswipe默认方式打开手电筒 isUseExternal返回false
                    quickSwitchResultCallback.isUseExternal(false);
                }
            }
        });
        // 注：如果想自定义菜单替换”最近应用”菜单栏，则使用下面方法
//        initEasySwipeMenu();
        //启动快划服务
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && !AdvertisementSwitcher.isAppInstalled(ConstantUtils.PACKAGE_CID)) {
            enableEasySwipe();
        }
    }

    // EasySwipeItem用来装载用户自定义的item信息
    private void initEasySwipeMenu() {
        List<EasySwipeItem> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            EasySwipeItem item;
            item = new EasySwipeItem(i, R.drawable.ic_launcher, R.string.app_name);
            // 参数1：自定义功能item的id，用于点击click事件标记你点击的是哪个自定义功能item
            // 参数2：自定义功能item的图标资源id
            // 参数3：自定义功能item的名称资源id
            // 参数4：自定义功能item的图标ImageView的background资源id 默认蓝色圆形则写-1,自定义参照文档末尾swipe_item_bg.xml
            list.add(item);
        }
        //swipe菜单模块：常用应用，快捷工具，自定义菜单(替换 最近应用)
        //参数2：自定菜单的图标；参数3：true快划布局中只显示单个自定义菜单;false则显示三个菜单
        EasySwipe.customSwipeMenu(list, R.drawable.ic_launcher); //扩展菜单中item的点击事件
        EasySwipe.setEasySwipeFunctionCallback(new EasySwipeFunctionCallback() {
            @Override
            public void swipeFunctionClick(int i) {
                //i是 EasySwipeItem对象中的id
                switch (i) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }
        });
    }

    public void enableEasySwipe() {
        EasySwipe.toggleEasySwipe(true);
        EasySwipe.tryStartService();
    }

    public void restartEasySwipe() {
        EasySwipe.toggleEasySwipe(true);
        EasySwipe.tryRestartService();
    }

    public void disableEasySwipe() {
        EasySwipe.toggleEasySwipe(false);
        EasySwipe.stopService();

        //关闭服务时，清楚广告加载时间,防止关闭后又打开，需要等一部分时间才出现ad
        PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_LOADED_SWIPE_AD_TIME, 0);
    }

    public void checkSwipeService() {
        try {
            SharedPreferences pref = ApplicationEx.getInstance().getGlobalSettingPreference();
            if(pref.getLong("key_cid_first_sync_server_time", 0) <= 0){
                return;
            }
            if(System.currentTimeMillis() - PreferenceHelper.getLong("colorphone_inStall_time", System.currentTimeMillis()) < 40 * 1000){
                return;
            }
            long lastCheck = pref.getLong("swipe_last_check_time", 0);
            if(System.currentTimeMillis() - lastCheck < getAdRefreshMinute() * 60 * 1000){
                return;
            }
            pref.edit().putLong("swipe_last_check_time", System.currentTimeMillis()).apply();
            boolean isEnableByUser = false;
            boolean isDisableByUser = false;
            int isEnableByServer = 0;
            isEnableByUser = pref.getBoolean("swipe_enable_by_user",false);
            isDisableByUser = pref.getBoolean("swipe_disable_by_user",false);
            if (ApplicationEx.getInstance() != null) {
                isEnableByServer = ApplicationEx.getInstance().getGlobalADPreference().getInt("pref_swipe_toogle_by_server", 0); //1 enable, 2 force stop
            }
            if (!isDisableByUser && (isEnableByUser || isEnableByServer == 1)) {
                enableEasySwipe();
                LogUtil.d("checkSwipeService", "easy-swipe checkSwipeService restartEasySwipe: ");
            }

            if (isEnableByServer == 2) {
                disableEasySwipe();
                LogUtil.d("checkSwipeService", "easy-swipe checkSwipeService disableEasySwipe: ");
            }
            LogUtil.d("checkSwipeService", "easy-swipe checkSwipeService end. ");
        } catch (Exception e) {
            LogUtil.e("checkSwipeService", "easy-swipe checkSwipeService exception: " + e.getMessage());
        }
    }

    //******************************************AD******************************************//
    private void initAd(View view) {
        if (isRefresh()) {
            mIsAdLoading = true;
            MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(view,
                    CallerAdManager.getSwipeFbId(),//
                    CallerAdManager.ADMOB_ID_ADV_SWIPE,
                    Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,//Advertisement.ADMOB_TYPE_NATIVE, Advertisement.ADMOB_TYPE_NONE
                    "",
                    Advertisement.MOPUB_TYPE_NATIVE,
                    -1,
                    "",
                    false);
            mAdvertisement = new Advertisement(adapter);
            mAdvertisement.setRefreshWhenClicked(false);
            mAdvertisement.refreshAD(true);
        }
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner, AdvertisementSwitcher.SERVER_KEY_QUICK_SWIPE);
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baiduKey, String eventKey, boolean isBanner) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey, eventKey, AdvertisementSwitcher.SERVER_KEY_QUICK_SWIPE, isBanner);
        }

        @Override
        public void onAdShow() {
            super.onAdShow();
            LogUtil.d(TAG, "initAd show");
        }

        @Override
        public void onAdLoaded() {
            mIsAdLoading = false;
            PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_LOADED_SWIPE_AD_TIME, System.currentTimeMillis());
        }

        @Override
        public void onAdClicked(boolean isAdmob) {
            super.onAdClicked(isAdmob);
            EasySwipe.hideSwipeView();
        }

        @Override
        public void onAdError(boolean isLastRequestIndex) {
            super.onAdError(isLastRequestIndex);
            mIsAdLoading = false;
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.facebook_native_ads_banner_50 : R.layout.facebook_ads_swipe;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_swipe : R.layout.layout_admob_advanced_content_ad_swipe;
        }

        @Override
        public int getMoPubViewRes() {
            return mIsBanner ? R.layout.layout_mopub_ad_banner : R.layout.layout_mopub_native_big_swipe;
        }

        @Override
        public int getBaiDuViewRes() {
            return mIsBanner ? R.layout.layout_du_ad_banner : R.layout.layout_du_ad_big_swipe;
        }
    }

    private int getAdRefreshMinute() {
        int m = AD_REFRESH_MINUTE;
        if (ApplicationEx.getInstance() != null) {
            m = ApplicationEx.getInstance().getGlobalADPreference().getInt("pref_swipe_ad_refresh", AD_REFRESH_MINUTE);
        }
        return m;
    }

    private boolean isRefresh() {
        if (mIsAdLoading) {
            return false;
        }
        long lastLoadTime = PreferenceHelper.getLong(PreferenceHelper.PREF_LAST_LOADED_SWIPE_AD_TIME, System.currentTimeMillis());
        //离上一次加载成功超过15分钟 ，刷新ad
        if (System.currentTimeMillis() - lastLoadTime >= getAdRefreshMinute() * 60 * 1000) {
            return true;
        }
        if (mAdvertisement == null) {
            return true;
        }
        return false;
    }
    //******************************************AD******************************************//

}
