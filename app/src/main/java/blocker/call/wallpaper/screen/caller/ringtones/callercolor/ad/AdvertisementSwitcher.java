package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.lionmobi.sdk.adpriority.AdPriorityManager;
import com.md.serverflash.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;

/**
 * Created by luowp on 2016/12/13.
 */
public class AdvertisementSwitcher {
    private static final String LOG_TAG = "AdvertisementSwitcher";
    public static final String AD_FACEBOOK = "facebook";
    public static final String AD_ADMOB = "admob";
    public static final String AD_ADMOB_ADX = "admob_adx";
    public static final String AD_BAIDU = "baidu";
    public static final String AD_MOPUB = "mopub";
    public static final String AD_MOPUB_50 = "mopub_50";
    public static final String AD_MOPUB_BANNER = "mopub_banner";
    public static final String AD_ADMOB_BIG_BANNER = "admob_big_banner";


    private static final int AD_TYPE_OUNT = 2;//change this when update ad type
    public static final String AD_DISABLE_FLAG = "none";
    public static final String SERVER_KEY_QUICK_CHARGING = "QUICK_CHARGING";
    public static final String SERVER_KEY_QUICK_DEFAULT = "DEFAULT";
    public static final String SERVER_KEY_END_CALL = "END_CALL";  //电话结束页
    public static final String SERVER_KEY_START_UP = "START_UP";  //启动页
    public static final String SERVER_KEY_CALL_FLASH_SETTING = "CALL_FLASH_SETTING";  //来电秀设置, fakecall首页, calllog adapter, block2
    public static final String SERVER_KEY_COMMON_RESULT = "COMMON_RESULT";           //扫描结果，号码升级结果, 短信详情， 短信编辑
    public static final String SERVER_KEY_CLEAN_RESULT = "CLEAN_RESULT";           //短信清理结果
    public static final String SERVER_KEY_SET_RESULT = "SET_RESULT";           //来电秀设置结果结果
    public static final String SERVER_KEY_IN_RESULT = "IN_RESULT";           //结果页插页广告
    public static final String SERVER_KEY_CHARGING_ASSISTANT = "CHARGING_ASSISTANT";
    public static final String SERVER_KEY_CONTACT_BIG = "CONTACT_BIG";           //联系人大头像
    public static final String SERVER_KEY_CONTACT_BIG_NEW = "CONTACT_NEW_BIG";           //联系人大头像 新大图
    public static final String SERVER_KEY_SMS_FLASH_SET = "SMS_FLASH_SET";           //短信秀设置
    public static final String SERVER_KEY_SMS_FLASH_SET_RESULT = "SMS_FLASH_SET_RESULT";           //短信秀设置结果页
    public static final String SERVER_KEY_SMS_FLASH_SHOW = "SMS_FLASH_SHOW";           //短信秀收到
    public static final String SERVER_KEY_CALL_FLASH_GROUP = "CALL_FLASH_GROUP";
    public static final String SERVER_KEY_FLASH_PREVIEW = "FLASH_PREVIEW";  //来电秀预览

    public static final String SERVER_KEY_MISSED_CALL = "MISSED_CALL";  //电话结束页 - missed call

    public static final String SERVER_KEY_SCREEN_ON_TIPS = "SCREEN_ON_TIPS"; //解锁弹新的来电秀提示

    public static final String SERVER_KEY_CALL_FLASH_DOWN_GROUP = "CALL_FLASH_DOWN_GROUP";
    public static final String SERVER_KEY_FIRST_SHOW_ADMOB = "FIRST_SHOW_ADMOB";
    public static final String SERVER_KEY_QUICK_SWIPE = "QUICK_SWIPE";  //左右下角快划

    private static AdvertisementSwitcher sInstance = null;
    public static boolean forceAdmobMode = false; // default is false, test is true

    private ArrayList<String> mAdPriority = new ArrayList<String>() {
        {
            add(AD_FACEBOOK);
            add(AD_ADMOB);
        }
    };

    private ArrayList<String> mFirstShowAdMobPriority = new ArrayList<String>() {
        {
            add(AD_ADMOB);
            add(AD_FACEBOOK);
        }
    };

    private ArrayList<String> mAdGroupPriority = new ArrayList<String>() {
        {
            add(AD_ADMOB_ADX);
            add(AD_FACEBOOK);
            add(AD_ADMOB);
        }
    };

    private ArrayList<String> DebugPriority = new ArrayList<String>() {
        {
//            add(AD_MOPUB);
//            add(AD_BAIDU);
//            add(AD_MOPUB_BANNER);
            add(AD_ADMOB_BIG_BANNER);
        }
    };

    private ArrayList<String> mAdCharge = new ArrayList<String>() {
        {
//            add(AD_ADMOB);
            add(AD_MOPUB);
        }
    };

    private Map<String, List<String>> mPriorityInfo = new HashMap<String, List<String>>() {
        {
            put(SERVER_KEY_QUICK_DEFAULT, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_END_CALL, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_START_UP, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_CALL_FLASH_SETTING, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_COMMON_RESULT, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_CLEAN_RESULT, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_SET_RESULT, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_IN_RESULT, (ArrayList<String>) mAdGroupPriority.clone());
            put(SERVER_KEY_CONTACT_BIG, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_SMS_FLASH_SET, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_SMS_FLASH_SET_RESULT, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_SMS_FLASH_SHOW, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_CHARGING_ASSISTANT, (ArrayList<String>) mAdCharge.clone());
            put(SERVER_KEY_CONTACT_BIG_NEW, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_CALL_FLASH_GROUP, (ArrayList<String>) mAdGroupPriority.clone());
            put(SERVER_KEY_SCREEN_ON_TIPS, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_CALL_FLASH_DOWN_GROUP, (ArrayList<String>) mAdGroupPriority.clone());
            put(SERVER_KEY_MISSED_CALL, (ArrayList<String>) mAdPriority.clone());
            put(SERVER_KEY_FIRST_SHOW_ADMOB, (ArrayList<String>) mFirstShowAdMobPriority.clone());
            put(SERVER_KEY_FLASH_PREVIEW, (ArrayList<String>) mFirstShowAdMobPriority.clone());
            put(SERVER_KEY_QUICK_SWIPE, (ArrayList<String>) mAdPriority.clone());
        }
    };

    public static AdvertisementSwitcher getInstance() {
        if (sInstance == null) {
            synchronized (AdvertisementSwitcher.class) {
                if (sInstance == null) {
                    sInstance = new AdvertisementSwitcher();
                }
            }
        }

        return sInstance;
    }

    public void initFromConfigCache(AdPriorityManager mgr) {
        if (mgr.getPriorityList(SERVER_KEY_QUICK_DEFAULT) != null) {
            //has cache, do init
            updateConfig(mgr);
        }
    }

    public void updateConfig(AdPriorityManager mgr) {
        Iterator<String> iterator = mPriorityInfo.keySet().iterator();
        String key;
        List<String> priorityList;
        List<String> value;


        boolean isDebugTest = false;
//        if (BuildConfig.DEBUG) isDebugTest = true;

        synchronized (mPriorityInfo) {
            while (iterator.hasNext()) {
                key = iterator.next();
                priorityList = mgr.getPriorityList(key);

                if (priorityList == null || priorityList.size() == 0) {
                    continue;
                }

                value = mPriorityInfo.get(key);
                value.clear();
                if (priorityList.contains(AD_DISABLE_FLAG)) {
                    value.add(AD_DISABLE_FLAG);
                } else {
                    if (isDebugTest) {
                        value.addAll(DebugPriority);
                    } else {
                        value.addAll(priorityList);
                    }
                }

                if (BuildConfig.DEBUG) {
                    String temp = "";
                    for (String info : value) {
                        temp += "-";
                        temp += info;
                    }
                    LogUtil.d(LOG_TAG, key + " : " + temp);
                }
            }
        }

    }

    public boolean isAdEnabled(String placementId) {
        boolean result = true;
        synchronized (mPriorityInfo) {
            if (mPriorityInfo.containsKey(placementId) && mPriorityInfo.get(placementId).get(0).equals(AD_DISABLE_FLAG)) {
                result = false;
            }
        }

        return result;
    }

    public boolean loadFacebookFirst(String placementId) {
        boolean result = !forceAdmobMode;
        synchronized (mPriorityInfo) {
            if (!forceAdmobMode && mPriorityInfo.containsKey(placementId)) {
                result = mPriorityInfo.get(placementId).equals(AD_FACEBOOK);
            }
        }

        return result;
    }

    public List<String> getAdPriority(String placementId) {
        synchronized (mPriorityInfo) {
            if (!forceAdmobMode && mPriorityInfo.containsKey(placementId)) {
                return mPriorityInfo.get(placementId);
            }
        }

        return (List<String>) mAdPriority.clone();
    }

    public List<String> getInterstitialAdPriority(String placementId) {
        List<String> ads_key = AdvertisementSwitcher.getInstance().getAdPriority(placementId);
//        List<String> ads_key_debug = new ArrayList<String>() {
//            {
//                add(AD_FACEBOOK);
//                add(AD_ADMOB);
//                add(AD_ADMOB_ADX);
//            }
//        };
        return ads_key;
    }

    public static boolean isAppInstalled(String packagename) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = ApplicationEx.getInstance().getPackageManager().getPackageInfo(
                    packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            return false;
        } catch (Exception e) {
            packageInfo = null;
            return false;
        }

        return packageInfo != null;
    }

    public static boolean isFacebookEnable() {
        return !forceAdmobMode && (isAppInstalled("com.facebook.katana") ||
                isAppInstalled("com.facebook.lite") ||
                isAppInstalled("com.facebook.mlite") ||
                isAppInstalled("com.facebook.orca") ||
                isAppInstalled("com.instagram.android"));
    }
}
