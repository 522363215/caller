package com.individual.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import com.aerserv.sdk.AerServSdk;
import com.fyber.inneractive.sdk.external.InneractiveAdManager;
import com.millennialmedia.AppInfo;
import com.millennialmedia.MMSDK;
import com.smaato.soma.SOMA;

import net.pubnative.lite.sdk.PNLite;

/**
 * 接入应用启动时调用，用于初始化各个渠道sdk
 */
public class CpmGatherManager {

    public static void init(Application app) {
        // IA
        initIA(app);
        // SMT
        initSMT(app);
        // PN
        initPN(app);
        // AOL
        initAOL(app);
    }

    private static void destory() {
        // IA
        destoryIA();
    }


    // IA
    private static boolean isIAInited = false;
    public static boolean isIAInited() {
        return isIAInited;
    }
    private static void initIA(Context context) {
        if(isIAInited) {
            return;
        }
        try {
            String id = getMetaDataFromInteger(context, "com.individual.sdk.IA_APP_ID");
            if(!TextUtils.isEmpty(id)) {
                SdkLogger.e(BaseAdContainer.TAG, "initIA:" + id);
                InneractiveAdManager.initialize(context.getApplicationContext(), id);
                isIAInited = true;
            }
        } catch (Exception e) {
            isIAInited = false;
        }

    }
    private static void destoryIA() {
        InneractiveAdManager.destroy();
        isIAInited = false;
    }


    // AS
    private static boolean isAsInited = false;
    public static boolean isASInited() {
        return isAsInited;
    }
    public static void checkAsInit(Activity act) {
        try {
            if(!isAsInited) {
                String id = getMetaDataFromInteger(act, "com.individual.sdk.AS_APP_ID");
                if(!TextUtils.isEmpty(id)) {
                    SdkLogger.e(BaseAdContainer.TAG, "initAS:" + id);
                    AerServSdk.init(act, id);
                    isAsInited = true;
                }
            }
        } catch (Exception e) {
            isAsInited = false;
        }

    }

    // SMT
    private static boolean isSmtInited = false;
    public static boolean isSmtInited() {
        return isSmtInited;
    }
    private static void initSMT(Application app) {
        try {
            if(!isSmtInited && app != null) {
                SdkLogger.e(BaseAdContainer.TAG, "initSMT");
                SOMA.init(app);
                isSmtInited = true;
            }
        } catch (Exception e) {
            isSmtInited = false;
        }
    }

    // PN
    private static boolean isPnInited = false;
    public static boolean isPnInited() {
        return isPnInited;
    }
    private static void initPN(Application app) {
        if(!isPnInited && app != null) {
            try {
                String appToken = getMetaDataFromString(app, "com.individual.sdk.PN_APP_ID");
                if(!TextUtils.isEmpty(appToken)) {
                    SdkLogger.e(BaseAdContainer.TAG, "initPN: " + appToken);
                    PNLite.initialize(appToken, app);
                    // 是否开启测试模式，测试模式只提供测试广告，不计入展示和点击
                    PNLite.setTestMode(false);
                    isPnInited = true;
                }
            } catch (Exception e) {
                isPnInited = false;
            }

        }
    }

    // AOL
    private static boolean isAolInited = false;
    public static boolean isAolInited() {
        return isAolInited;
    }
    public static void initAOL(Application app) {
        if(!isAolInited && app != null) {
            try {
                String id = getMetaDataFromString(app, "com.individual.sdk.AOL_APP_ID");
                if (!TextUtils.isEmpty(id)) {
                    SdkLogger.e(BaseAdContainer.TAG, "initAol: " + id);
                    MMSDK.initialize(app);
                    AppInfo appInfo = new AppInfo();
                    appInfo.setSiteId(id);
                    MMSDK.setAppInfo(appInfo);
                    isAolInited = true;
                }
            } catch (Exception e) {
                isAolInited = false;
            }
        }
    }

    public static String getMetaDataFromInteger(Context context, String key) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            int id = info.metaData.getInt(key, -1);
            if(id != -1) {
                return String.valueOf(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMetaDataFromString(Context context, String key) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            return info.metaData.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
