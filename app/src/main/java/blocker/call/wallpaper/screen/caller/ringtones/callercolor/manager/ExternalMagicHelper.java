package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.example.lakes.externaldemonstrate.api.ExternalMagicManager;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.Advertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.AdvertisementSwitcher;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.BaseAdvertisementAdapter;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.CallerAdManager;

public class ExternalMagicHelper {
    private static ExternalMagicHelper sInstance;
    private final Context mContext;

    private ExternalMagicHelper() {
        mContext = ApplicationEx.getInstance().getBaseContext();
    }

    public static ExternalMagicHelper getInstance() {
        if (sInstance == null) {
            synchronized (ExternalMagicHelper.class) {
                sInstance = new ExternalMagicHelper();
                return sInstance;
            }
        }
        return sInstance;
    }

    public void init() {
        ExternalMagicManager.getInstance().setServerConfigCallback(new ExternalMagicManager.ServerConfigCallback() {
            @Override
            public boolean isCommercialValid() {
                //总服务器配置开关，false 为关闭所有变现的显示
                return true;
            }

            @Override
            public ExternalMagicManager.ServerCommonBean getServerCommon() {
                //外部变现（省电、加速、运势、头部运动）的相互显示间隔
                return new ExternalMagicManager.ServerCommonBean(0,true);
                //5 为间隔时间，单位：分钟
            }

            @Override
            public ExternalMagicManager.ServerConfigBean getServerConfig(ExternalMagicManager.MagicType config) {
                //设置各个变现的参数
                ExternalMagicManager.ServerConfigBean bean = null;
                switch (config) {
                    case M_HL://MAGIC_HOROSCOPE_LUCK://运势变现
                        bean = new ExternalMagicManager.ServerConfigBean(true, 0, 1, 1, 3000, 100, 1);
                        break;
                    case M_BR://MAGIC_BATTERY_REMAIN://移除电源变现
                        bean = new ExternalMagicManager.ServerConfigBean(
                                true, //是否开启变现
                                0, //安装后延迟显示时间（小时）
                                1, //自身显示间隔（小时）
                                1,//每天弹出的最大次数
                                3000, //变现界面关闭按钮的延迟显示时间（豪秒）
                                100, //变现界面按钮延迟显示的几率（0-100）
                                1 //变现被用户禁用后重新显示间隔（天）
                        );
                        break;
                    case M_WS://MAGIC_WIFI_SECURITY://wifi 检测变现
                        bean = new ExternalMagicManager.ServerConfigBean(true, 0, 1, 1, 3000, 100, 1);
                        break;
                    case M_EC://MAGIC_END_CALL://通话结束变现
                        //默认该变现显示关闭的参数
                        bean = ExternalMagicManager.getInstance().createDefaultDisableConfig();
                        break;
                    case M_NM://MAGIC_NECK_MOVEMENT://头部运动变现
                        bean = new ExternalMagicManager.ServerConfigBean(true, 0, 1, 1, 3000, 100, 1);
                        break;
                    case M_BS://MAGIC_BATTERY_SAVE://省电变现
                        bean = new ExternalMagicManager.ServerConfigBean(true, 0, 1, 1, 3000, 100, 1);
                        break;
                    case M_DW://MAGIC_DRINK_WATER://喝水提醒变现
                        bean = new ExternalMagicManager.ServerConfigBean(true, 0, 1, 1, 3000, 100, 1);
                        break;
                    case M_AC://MAGIC_AUTO_CLEAN://安装卸载清理变现
                        bean = new ExternalMagicManager.ServerConfigBean(true, 0, 1, 1, 3000, 100, 1);
                        break;
                    case M_AB://MAGIC_AUTO_BOOST://加速变现
                        bean = new ExternalMagicManager.ServerConfigBean(true, 0, 1, 1, 3000, 100, 1);
                        break;
                    case M_CS://MAGIC_CHARGING_STATUS://充电统计变现
                        bean = new ExternalMagicManager.ServerConfigBean(true, 0, 1, 1, 3000, 100, 1);
                        break;
                    case M_IT://MAGIC_INSTALL://安装插屏
                        bean = new ExternalMagicManager.ServerConfigBean(true, 0, 1, 1, 3000, 100, 1);
                        break;
                }
                return bean;
            }
        });

        ExternalMagicManager.getInstance().setMagicOccurCallback(new ExternalMagicManager.OnMagicOccurCallback() {
            //变现显示的生命周期
            @Override
            public boolean beforeViewPopup(ExternalMagicManager.MagicType type) {
                //显示变现前可以控制本次是否显示：true 显示、false 不显示
                return true;
            }

            @Override
            public void onViewPopup(ExternalMagicManager.MagicType type, LinearLayout adRootView) {
                //显示时加载广告： type 为变现类型，adRootView 为广告布局 layout
                initAds(type, adRootView);

                switch (type) {
                    case M_AB://MAGIC_AUTO_BOOST:
                        //整体布局左、右间隔 16dp
                        break;
                    case M_AC://MAGIC_AUTO_CLEAN:
                        //整体布局左、右间隔 16dp
                        break;
                    case M_DW://MAGIC_DRINK_WATER:
                        //整体布局左、右间隔 16dp
                        break;
                    case M_BS://MAGIC_BATTERY_SAVE:
                        //整体布局左、右间隔 0dp
                        break;
                    case M_NM://MAGIC_NECK_MOVEMENT:
                        //整体布局左、右间隔 16dp
                        break;
                    case M_EC://MAGIC_END_CALL:
                        //整体布局左、右间隔 0dp
                        break;
                    case M_WS://MAGIC_WIFI_SECURITY:
                        //整体布局左、右间隔 0dp
                        break;
                    case M_BR://MAGIC_BATTERY_REMAIN:
                        //整体布局左、右间隔 23dp
                        break;
                    case M_HL://MAGIC_HOROSCOPE_LUCK:
                        //整体布局左、右间隔 16dp
                        break;
                    case M_CS://MAGIC_CHARGING_STATUS:
                        //整体布局左、右间隔 16dp
                        break;
                }
            }

            @Override
            public void onViewHide(ExternalMagicManager.MagicType type) {
                //变现关闭回调
            }

            @Override
            public void onFeatureDisabled(ExternalMagicManager.MagicType magicType, boolean enable) {

            }
        });

        ExternalMagicManager.getInstance().setBroadcastCallback(new ExternalMagicManager.BroadcastCallback() {
            @Override
            public boolean OnHomeKeyPressed() {
                return true;
            }

            @Override
            public boolean OnScreenUnlocked() {
                return true;
            }

            @Override
            public boolean OnAppUnInstalled() {
                return true;
            }

            @Override
            public boolean OnAppInstalled() {
                return true;
            }

            @Override
            public boolean OnWifiConnectionChange() {
                return true;
            }

            @Override
            public boolean OnCallEndEvent() {
                return false;
            }
        });

        ExternalMagicManager.getInstance().init(mContext); //sdk 初始化（注：初始化放在最后）
    }

    //******************************************AD******************************************//
    private void initAds(ExternalMagicManager.MagicType magicType, LinearLayout adRootView) {
        MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(adRootView,
                "",//ConstantUtils.FB_AFTER_CALL_ID
                getAdmobId(magicType),//ConstantUtils.ADMOB_AFTER_CALL_NATIVE_ID
                Advertisement.ADMOB_TYPE_NATIVE_ADVANCED,//Advertisement.ADMOB_TYPE_NATIVE, Advertisement.ADMOB_TYPE_NONE
                "",
                Advertisement.MOPUB_TYPE_NATIVE,
                -1,
                "",
                false,
                magicType);
        Advertisement advertisement = new Advertisement(adapter);
        advertisement.setRefreshWhenClicked(false);
        advertisement.refreshAD(true);
        advertisement.enableFullClickable();
    }

    private class MyAdvertisementAdapter extends BaseAdvertisementAdapter {

        private ExternalMagicManager.MagicType magicType;

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String eventKey, boolean isBanner, ExternalMagicManager.MagicType magicType) {
            super(context, facebookKey, admobKey, admobType, eventKey, isBanner, ExternalMagicHelper.this.getPlacementId(magicType));
        }

        public MyAdvertisementAdapter(View context, String facebookKey, String admobKey, int admobType, String mopubKey, int moPubType, int baiduKey, String eventKey, boolean isBanner, ExternalMagicManager.MagicType magicType) {
            super(context, facebookKey, admobKey, admobType, mopubKey, moPubType, baiduKey, eventKey, ExternalMagicHelper.this.getPlacementId(magicType), isBanner);
            this.magicType = magicType;
        }

        @Override
        public void onAdLoaded() {
        }

        @Override
        public int getFbViewRes() {
            return mIsBanner ? R.layout.facebook_native_ads_banner_50 : R.layout.facebook_no_icon_native_ads_call_after_big;
        }

        @Override
        public int getAdmobViewRes(int type, boolean isAppInstall) {
            return ExternalMagicHelper.this.getAdmobViewRes(magicType, isAppInstall);
        }
    }

    private String getPlacementId(ExternalMagicManager.MagicType magicType) {
        switch (magicType) {
            case M_AB://MAGIC_AUTO_BOOST:
            case M_AC://MAGIC_AUTO_CLEAN:
            case M_DW://MAGIC_DRINK_WATER:
            case M_BS://MAGIC_BATTERY_SAVE:
            case M_NM://MAGIC_NECK_MOVEMENT:
            case M_EC://MAGIC_END_CALL:
            case M_WS://MAGIC_WIFI_SECURITY:
            case M_BR://MAGIC_BATTERY_REMAIN:
            case M_HL://MAGIC_HOROSCOPE_LUCK:
            case M_CS://MAGIC_CHARGING_STATUS:
                return AdvertisementSwitcher.SERVER_KEY_END_CALL;
        }
        return "";
    }

    private String getAdmobId(ExternalMagicManager.MagicType type) {
        switch (type) {
            case M_AB://MAGIC_AUTO_BOOST:
                return CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL;
            case M_AC://MAGIC_AUTO_CLEAN:
                return CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL;
            case M_DW://MAGIC_DRINK_WATER:
                return CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL;
            case M_BS://MAGIC_BATTERY_SAVE:
                return CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL;
            case M_NM://MAGIC_NECK_MOVEMENT:
                return CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL;
            case M_EC://MAGIC_END_CALL:
                return CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL;
            case M_WS://MAGIC_WIFI_SECURITY:
                return CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL;
            case M_BR://MAGIC_BATTERY_REMAIN:
                return CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL;
            case M_HL://MAGIC_HOROSCOPE_LUCK:
                return CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL;
            case M_CS://MAGIC_CHARGING_STATUS:
                return CallerAdManager.ADMOB_ID_ADV_END_CALL_NORMAL;
        }
        return "";
    }

    private int getAdmobViewRes(ExternalMagicManager.MagicType type, boolean isAppInstall) {
        switch (type) {
            case M_AB://MAGIC_AUTO_BOOST:
            case M_AC://MAGIC_AUTO_CLEAN:
            case M_DW://MAGIC_DRINK_WATER:
            case M_NM://MAGIC_NECK_MOVEMENT:
            case M_HL://MAGIC_HOROSCOPE_LUCK:
            case M_CS://MAGIC_CHARGING_STATUS:
                //整体布局左、右间隔 16dp
                return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_external_magic_magin_left_right_16dp : R.layout.layout_admob_advanced_content_ad_external_magic_magin_left_right_16dp;
            case M_BS://MAGIC_BATTERY_SAVE:
            case M_EC://MAGIC_END_CALL:
            case M_WS://MAGIC_WIFI_SECURITY:
                //整体布局左、右间隔 0dp
                return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_external_magic_magin_left_right_0dp : R.layout.layout_admob_advanced_content_ad_external_magic_magin_left_right_0dp;
            case M_BR://MAGIC_BATTERY_REMAIN:
                //整体布局左、右间隔 23dp
                return isAppInstall ? R.layout.layout_admob_advanced_app_install_ad_external_magic_magin_left_right_23dp : R.layout.layout_admob_advanced_content_ad_external_magic_magin_left_right_23dp;
        }
        return -1;
    }

    //******************************************AD******************************************//

}
