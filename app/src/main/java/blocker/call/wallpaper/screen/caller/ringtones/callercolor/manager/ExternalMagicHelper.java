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
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad.InterstitialAdvertisement;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.async.Async;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.ExternalParam;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;

public class ExternalMagicHelper {
    private static final String TAG = "ExternalMagicHelper";
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

    public void delayInit() {
        Async.schedule(40 * 1000, new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    public void init() {
        long lastInitTime = PreferenceHelper.getLong(PreferenceHelper.PREF_LAST_INIT_EXTERNAL_MAGIC_TIME, 0);
        if (System.currentTimeMillis() - lastInitTime < 15 * 60 * 1000) {
            return;
        }

        ExternalMagicManager.getInstance().setServerConfigCallback(new ExternalMagicManager.ServerConfigCallback() {
            @Override
            public boolean isCommercialValid() {
                //总服务器配置开关，false 为关闭所有变现的显示
                return CallerAdManager.isExternalOn();
            }

            @Override
            public ExternalMagicManager.ServerCommonBean getServerCommon() {
                //外部变现（省电、加速、运势、头部运动）的相互显示间隔
                return new ExternalMagicManager.ServerCommonBean(CallerAdManager.getExternalInterval(), true);
                //5 为间隔时间，单位：分钟
            }

            @Override
            public ExternalMagicManager.ServerConfigBean getServerConfig(ExternalMagicManager.MagicType config) {
                //设置各个变现的参数
                ExternalMagicManager.ServerConfigBean bean = null;
                ExternalParam externalParam = getExternalParam(config);
                LogUtil.d(TAG, "getServerConfig config:" + config + ",externalParam:" + externalParam);
                if (externalParam != null) {
                    bean = new ExternalMagicManager.ServerConfigBean(
                            externalParam.mEnable, //是否开启变现
                            externalParam.mDelayTime, //安装后延迟显示时间（小时）
                            externalParam.mSelfInterval, //自身显示间隔（分钟）
                            externalParam.mPopupNumber,//每天弹出的最大次数
                            externalParam.mDelayedDisplayTime, //变现界面关闭按钮的延迟显示时间（豪秒）
                            externalParam.mDelayedDisplayRate, //变现界面按钮延迟显示的几率（0-100）
                            externalParam.mRestartDay //变现被用户禁用后重新显示间隔（天）
                    );
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

                //加载插屏ad
                loadInterstitialAd();
            }

            @Override
            public void onViewHide(ExternalMagicManager.MagicType type) {
                //变现关闭回调
                showInterstitialAd();
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

        PreferenceHelper.putLong(PreferenceHelper.PREF_LAST_INIT_EXTERNAL_MAGIC_TIME, System.currentTimeMillis());
    }

    private ExternalParam getExternalParam(ExternalMagicManager.MagicType config) {
        ExternalParam externalParam = null;
        switch (config) {
            case M_BR://MAGIC_BATTERY_REMAIN://移除电源变现
                externalParam = CallerAdManager.getExternalParam("M_BR");
                break;
            case M_WS://MAGIC_WIFI_SECURITY://wifi 检测变现
                externalParam = CallerAdManager.getExternalParam("M_WS");
                break;
            case M_EC://MAGIC_END_CALL://通话结束变现
                externalParam = CallerAdManager.getExternalParam("M_EC");
                break;
            case M_NM://MAGIC_NECK_MOVEMENT://头部运动变现
                externalParam = CallerAdManager.getExternalParam("M_NM");
                break;
            case M_BS://MAGIC_BATTERY_SAVE://省电变现
                externalParam = CallerAdManager.getExternalParam("M_BS");
                break;
            case M_DW://MAGIC_DRINK_WATER://喝水提醒变现
                externalParam = CallerAdManager.getExternalParam("M_DW");
                break;
            case M_AC://MAGIC_AUTO_CLEAN://安装卸载清理变现
                externalParam = CallerAdManager.getExternalParam("M_AC");
                break;
            case M_AB://MAGIC_AUTO_BOOST://加速变现
                externalParam = CallerAdManager.getExternalParam("M_AB");
                break;
            case M_CS://MAGIC_CHARGING_STATUS://充电统计变现
                externalParam = CallerAdManager.getExternalParam("M_CS");
                break;
        }
        return externalParam;
    }

    //******************************************AD******************************************//
    private void initAds(ExternalMagicManager.MagicType magicType, LinearLayout adRootView) {
        MyAdvertisementAdapter adapter = new MyAdvertisementAdapter(adRootView,
                "",//ConstantUtils.FB_AFTER_CALL_ID
                getAdmobId(magicType),//ConstantUtils.ADMOB_AFTER_CALL_NATIVE_ID
                Advertisement.ADMOB_TYPE_NATIVE,//Advertisement.ADMOB_TYPE_NATIVE, Advertisement.ADMOB_TYPE_NONE
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

        @Override
        public int getAdmobWidth() {
            return ExternalMagicHelper.this.getAdmobWidth(magicType);
        }

        @Override
        public void onAdClicked(boolean isAdmob) {
            super.onAdClicked(isAdmob);
            ExternalMagicManager.getInstance().hideCurrentPopup();
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
            case M_CS://MAGIC_CHARGING_STATUS:
                return AdvertisementSwitcher.SERVER_KEY_EXTERNAL;
        }
        return "";
    }

    private String getAdmobId(ExternalMagicManager.MagicType type) {
        String admobId = "";
        ExternalParam externalParam = getExternalParam(type);
        if (externalParam != null) {
            admobId = externalParam.admob_id;
        }
        return admobId;
    }

    private int getAdmobViewRes(ExternalMagicManager.MagicType type, boolean isAppInstall) {
        switch (type) {
            case M_AB://MAGIC_AUTO_BOOST:
            case M_AC://MAGIC_AUTO_CLEAN:
            case M_DW://MAGIC_DRINK_WATER:
            case M_NM://MAGIC_NECK_MOVEMENT:
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

    private int getAdmobWidth(ExternalMagicManager.MagicType type) {
        switch (type) {
            case M_AB://MAGIC_AUTO_BOOST:
            case M_AC://MAGIC_AUTO_CLEAN:
            case M_DW://MAGIC_DRINK_WATER:
            case M_NM://MAGIC_NECK_MOVEMENT:
            case M_CS://MAGIC_CHARGING_STATUS:
                //整体布局左、右间隔 16dp
                return DeviceUtil.getScreenWidth() - Stringutil.dpToPx(32);
            case M_BS://MAGIC_BATTERY_SAVE:
            case M_EC://MAGIC_END_CALL:
            case M_WS://MAGIC_WIFI_SECURITY:
                //整体布局左、右间隔 0dp
                return DeviceUtil.getScreenWidth();
            case M_BR://MAGIC_BATTERY_REMAIN:
                //整体布局左、右间隔 23dp
                return DeviceUtil.getScreenWidth() - Stringutil.dpToPx(46);
        }
        return -1;
    }

    private void loadInterstitialAd() {
        if (!CallerAdManager.isShowInAdsExternal()) return;
        InterstitialAdUtil.loadInterstitialAd(mContext, InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
    }

    private void showInterstitialAd() {
        try {
            if (!CallerAdManager.isShowInAdsExternal()) return;
            InterstitialAdvertisement interstitialAdvertisement = ApplicationEx.getInstance().getInterstitialAdvertisement(InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
            if (interstitialAdvertisement == null) return;
            interstitialAdvertisement.show(new InterstitialAdvertisement.InterstitialAdShowListener() {
                @Override
                public void onAdClosed() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdClosed");
                    ApplicationEx.getInstance().setInterstitialAdvertisement(null, InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
                }

                @Override
                public void onAdShow() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdShow");
                }

                @Override
                public void onAdError() {
                    LogUtil.d(TAG, "InterstitialAdvertisement showInterstitialAd onAdError");
                    ApplicationEx.getInstance().setInterstitialAdvertisement(null, InterstitialAdUtil.POSITION_INTERSTITIAL_AD_IN_CALL_FLASH_DETAIL);
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "showInterstitialAd e:" + e.getMessage());
        }
    }

    //******************************************AD******************************************//

}
