package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bednotdisturb;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mopub.test.util.JSONUtils;

import org.json.JSONObject;

import java.util.Calendar;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.EncodeUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;

public class BedNotDisturbManager {
    private static final String TAG = "BedNotDisturbManager";
    public static final String BS_CONFIG_PREF_FILE = "cid_bs_config_sp";

    // 床头钟: 服务器开关(升级用户). 0:关闭、1：启用
    public static final int KEY_SERVER_ENABLE_BS_DEFAULT = 0;
    // 床头钟: 服务器开关(新用户). 0:关闭、1：启用
//    public static final int KEY_SERVER_ENABLE_BS_ROOKIE_DEFAULT = 0;
    // 床头钟: 新安装8小时内强制关闭
    public static final int KEY_SERVER_ENABLE_BS_LIMIT_DEFAULT = 8;
    // 床头钟: 锁屏后等待15分钟才展示。
    public static final int KEY_SERVER_BS_DELAY_DEFAULT = 15;
    // 床头钟: 每日展示的TimeZone（0点~7点）。
    public static final String KEY_SERVER_BS_TZ_DEFAULT = "0:0,7:0";
    // 床头钟: 广告展示间隔，单位秒（同等于请求最小间隔）
    public static final int KEY_SERVER_BS_AD_DISTIME_DEFAULT = 10;
    // 床头钟: 同一渠道请求间隔，单位秒
    public static final int KEY_SERVER_BS_AD_LOAD_GAP_DEFAULT = 30;


    // (升级用户)床头钟是否开启. 0:关闭、1：启用
    public static final String KEY_SERVER_ENABLE_BS = "server_bs_enable";
    // (新用户)床头钟是否开启. 0:关闭、1：启用
//    public static final String KEY_SERVER_ENABLE_BS_ROOKIE = "server_bs_rk_enable";
    // 床头钟: 新安装xx小时内强制不开启
    public static final String KEY_SERVER_ENABLE_BS_LIMIT = "server_bs_enable_limit";
    // 床头钟: 锁屏后等待xx分钟才展示
    public static final String KEY_SERVER_BS_DELAY = "server_bs_delay";
    // 床头钟: 每日展示的TimeZone（0点~5点）
    public static final String KEY_SERVER_BS_TIMEZONE = "server_bs_tz";
    // 床头钟: 广告渠道 优先级配置
    public static final String KEY_SERVER_BS_AD_PRLIST = "server_bs_an_prlist";
    // 床头钟: 广告id配置
    public static final String KEY_SERVER_BS_AD_CONFIG = "server_bs_an_config";
    // 床头钟: 广告展示间隔，单位秒（同等于请求最小间隔）
    public static final String KEY_SERVER_BS_AD_DISTIME = "server_bs_an_distime";
    // 床头钟: 同一渠道请求间隔，单位秒
    public static final String KEY_SERVER_BS_AD_LOADGAP = "server_bs_an_loadgp";
    // 纯cpm展示id配置（根据广告位置名称配置）
    public static final String KEY_SERVER_CPM_CONFIG = "server_pman_config";


    // ////////////////////
    // 床头钟控制
    // ////////////////////

    /**
     * 【综合服务器开关】是否开启床头钟
     *
     * @param context
     * @return
     */
    public static boolean isCanLaunchBedside(Context context) {
        if (!isBedsideEnable()) return false;
        long install = System.currentTimeMillis();
        if (ApplicationEx.getInstance() != null) {
            install = PreferenceHelper.getLong("colorphone_inStall_time", System.currentTimeMillis());
        }
        long limit = getBedsideEnableLimitHour() * 60 * 60 * 1000;
        if (Math.abs(System.currentTimeMillis() - install) >= limit) {
            return true;
        }
        return false;
    }

    /**
     * 床头钟: 服务器开关(升级用户)
     *
     * @return
     */
    public static boolean isBedsideEnable() {
        if (getBSConfigSharePreference() == null) {
            return KEY_SERVER_ENABLE_BS_DEFAULT == 1;
        }

        return getBSConfigSharePreference().getInt(KEY_SERVER_ENABLE_BS,
                KEY_SERVER_ENABLE_BS_DEFAULT) == 1;

    }

    /**
     * 床头钟: 服务器开关(新用户)
     *
     * @return
     */
//    public static boolean isBedsideRookieEnable() {
//        if (getBSConfigSharePreference() == null) {
//            return KEY_SERVER_ENABLE_BS_ROOKIE_DEFAULT == 1;
//        }
//
//        return getBSConfigSharePreference().getInt(KEY_SERVER_ENABLE_BS_ROOKIE,
//                KEY_SERVER_ENABLE_BS_ROOKIE_DEFAULT) == 1;
//
//    }

    /**
     * 床头钟: 新安装xx小时内强制不开启
     *
     * @return
     */
    public static int getBedsideEnableLimitHour() {
        if (getBSConfigSharePreference() == null) {
            return KEY_SERVER_ENABLE_BS_LIMIT_DEFAULT;
        }

        return getBSConfigSharePreference().getInt(KEY_SERVER_ENABLE_BS_LIMIT,
                KEY_SERVER_ENABLE_BS_LIMIT_DEFAULT);

    }

    /**
     * 床头钟: 锁屏后等待xx分钟才展示
     *
     * @return
     */
    public static int getBedsideDelayMin() {
        if (getBSConfigSharePreference() == null) {
            return KEY_SERVER_BS_DELAY_DEFAULT;
        }

        return getBSConfigSharePreference().getInt(KEY_SERVER_BS_DELAY,
                KEY_SERVER_BS_DELAY_DEFAULT);

    }

    /**
     * 床头钟: 每日展示的TimeZone - "0:0,5:0"【24小时制】
     *
     * @return
     */
    public static String getBedsideTimeZone() {
        if (getBSConfigSharePreference() == null) {
            return KEY_SERVER_BS_TZ_DEFAULT;
        }

        return getBSConfigSharePreference().getString(KEY_SERVER_BS_TIMEZONE,
                KEY_SERVER_BS_TZ_DEFAULT);
    }

    /**
     * 判断是否在时间区内
     */
    public static boolean isInTime(long time) {
        try {
            String bedsideTimeZone = getBedsideTimeZone();
            if (TextUtils.isEmpty(bedsideTimeZone)) {
                return false;
            }
            String[] timeZone = bedsideTimeZone.split(",");
            if (timeZone != null && timeZone.length >= 2) {
                String beginTimeZone = timeZone[0];
                String endTimeZone = timeZone[1];
                if (!TextUtils.isEmpty(beginTimeZone) && !TextUtils.isEmpty(endTimeZone)) {
                    String[] beginTime = beginTimeZone.split(":");
                    String[] endTime = endTimeZone.split(":");
                    if (beginTime != null && beginTime.length >= 2 && endTime != null && endTime.length >= 2) {
                        int beginHour = Integer.parseInt(beginTime[0]);
                        int beginMin = Integer.parseInt(beginTime[1]);
                        int endHour = Integer.parseInt(endTime[0]);
                        int endMin = Integer.parseInt(endTime[1]);

                        boolean result = false;
                        final long aDayInMillis = 1000 * 60 * 60 * 24;
                        Calendar nowCalendar = Calendar.getInstance();
                        nowCalendar.setTimeInMillis(time + 1);

                        Calendar beginCalendar = Calendar.getInstance();
                        beginCalendar.set(
                                beginCalendar.get(Calendar.YEAR),
                                beginCalendar.get(Calendar.MONTH),
                                beginCalendar.get(Calendar.DAY_OF_MONTH),
                                beginHour,
                                beginMin,
                                0);

                        Calendar endCalendar = Calendar.getInstance();
                        endCalendar.set(
                                endCalendar.get(Calendar.YEAR),
                                endCalendar.get(Calendar.MONTH),
                                endCalendar.get(Calendar.DAY_OF_MONTH),
                                endHour,
                                endMin,
                                0);
                        //判断时间是否满足
                        if (!beginCalendar.before(endCalendar)) {
                            // 跨天的特殊情况（比如22:00-8:00）
                            beginCalendar.setTimeInMillis(beginCalendar.getTimeInMillis() - aDayInMillis);
                            result = !nowCalendar.before(beginCalendar) && !nowCalendar.after(endCalendar); // startTime <= now <= endTime
                            Calendar startCalendarInThisDay = Calendar.getInstance();
                            startCalendarInThisDay.setTimeInMillis(beginCalendar.getTimeInMillis() + aDayInMillis);
                            if (!nowCalendar.before(startCalendarInThisDay)) {
                                result = true;
                            }
                        } else {
                            // 普通情况(比如 8:00 - 14:00)
                            result = !nowCalendar.before(beginCalendar) && !nowCalendar.after(endCalendar); // startTime <= now <= endTime
                        }
                        LogUtil.d(TAG, "isInTime START:" + beginCalendar.getTimeInMillis() + ",now:" + nowCalendar.getTimeInMillis() + ",end:" + endCalendar.getTimeInMillis());
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.d(TAG, "isInTime e:" + e.getMessage());
        }
        return false;
    }

    /**
     * 获取时间区间
     */
    public static long[] getLongBedTimeZone() {
        int beginHour = 0;
        int beginMin = 0;
        int endHour = 7;
        int endMin = 0;
        try {
            String bedsideTimeZone = getBedsideTimeZone();
            if (!TextUtils.isEmpty(bedsideTimeZone)) {
                String[] timeZone = bedsideTimeZone.split(",");
                if (timeZone != null && timeZone.length >= 2) {
                    String beginTimeZone = timeZone[0];
                    String endTimeZone = timeZone[1];
                    if (!TextUtils.isEmpty(beginTimeZone) && !TextUtils.isEmpty(endTimeZone)) {
                        String[] beginTime = beginTimeZone.split(":");
                        String[] endTime = endTimeZone.split(":");
                        if (beginTime != null && beginTime.length >= 2 && endTime != null && endTime.length >= 2) {
                            beginHour = Integer.parseInt(beginTime[0]);
                            beginMin = Integer.parseInt(beginTime[1]);
                            endHour = Integer.parseInt(endTime[0]);
                            endMin = Integer.parseInt(endTime[1]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.d(TAG, "getLongBedTimeZone e:" + e.getMessage());
        }
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.set(
                beginCalendar.get(Calendar.YEAR),
                beginCalendar.get(Calendar.MONTH),
                beginCalendar.get(Calendar.DAY_OF_MONTH),
                beginHour,
                beginMin,
                0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(
                endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH),
                endHour,
                endMin,
                0);
        return new long[]{beginCalendar.getTimeInMillis(), endCalendar.getTimeInMillis()};
    }

    /**
     * 床头钟: cpm广告优先级列表获取（PrType）
     *
     * @return
     */
    public static String[] getBedsideAdPrList() {
        if (ApplicationEx.getInstance() != null) {
            try {
                String prStr = getBSConfigSharePreference().getString(KEY_SERVER_BS_AD_PRLIST, null);
                if (!TextUtils.isEmpty(prStr)) {
                    String decryptPrStr = EncodeUtils.decrypt(prStr);
                    return decryptPrStr.trim().split(",");
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 床头钟: 获取广告id配置集合
     *
     * @return
     */
    public static JSONObject getBedsideAdConfig() {
        if (ApplicationEx.getInstance() != null) {
            try {
                String configStr = getBSConfigSharePreference().getString(KEY_SERVER_BS_AD_CONFIG, null);
                if (!TextUtils.isEmpty(configStr)) {
                    String decryptConfig = EncodeUtils.decrypt(configStr);
                    if (!TextUtils.isEmpty(decryptConfig)) {
                        return new JSONObject(decryptConfig);
                    }
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 床头钟: 广告展示间隔，单位秒（同等于请求最小间隔）
     *
     * @return
     */
    public static int getBedsideAdDisplayTimeSecond() {
        if (getBSConfigSharePreference() == null) {
            return KEY_SERVER_BS_AD_DISTIME_DEFAULT;
        }
        return getBSConfigSharePreference().getInt(KEY_SERVER_BS_AD_DISTIME,
                KEY_SERVER_BS_AD_DISTIME_DEFAULT);
    }

    /**
     * 床头钟: 同一渠道请求间隔，单位秒
     *
     * @return
     */
    public static int getBedsideAdLoadGapSecond() {
        if (getBSConfigSharePreference() == null) {
            return KEY_SERVER_BS_AD_LOAD_GAP_DEFAULT;
        }
        return getBSConfigSharePreference().getInt(KEY_SERVER_BS_AD_LOADGAP,
                KEY_SERVER_BS_AD_LOAD_GAP_DEFAULT);
    }

    public static SharedPreferences getBSConfigSharePreference() {
        //context = applicationEx.getInstance
        if (ApplicationEx.getInstance() != null) {
            return ApplicationEx.getInstance().getSharedPreferences(BS_CONFIG_PREF_FILE, Context.MODE_PRIVATE);
        } else {
            return null;
        }
    }

    public static void saveBSConfig(JSONObject dataObj) {
        SharedPreferences bs = getBSConfigSharePreference();

        if (bs != null) {
            SharedPreferences.Editor edit = bs.edit();

// 床头钟开关: (升级用户)是否开启. 0:关闭、1：启用
            int bs_enable = JSONUtils.getInt(dataObj, "bs_enable", KEY_SERVER_ENABLE_BS_DEFAULT);
            edit.putInt(KEY_SERVER_ENABLE_BS, bs_enable);
            LogUtil.d("zzzz_night", "bs_enable: " + bs_enable);

//            // 床头钟开关: (新用户)是否开启. 0:关闭、1：启用
//            int bs_rookie_enable = JSONUtils.getInt(dataObj, "bs_rookie_enable", KEY_SERVER_ENABLE_BS_ROOKIE_DEFAULT);
//            edit.putInt(KEY_SERVER_ENABLE_BS_ROOKIE, bs_rookie_enable);
            // 床头钟开关: 新安装xx小时内强制不开启
            int bs_enable_limit = JSONUtils.getInt(dataObj, "bs_enable_limit", KEY_SERVER_ENABLE_BS_LIMIT_DEFAULT);
            edit.putInt(KEY_SERVER_ENABLE_BS_LIMIT, bs_enable_limit);
            LogUtil.d("zzzz_night", "bs_enable_limit: " + bs_enable_limit);
            // 床头钟展示规则: 锁屏后等待xx分钟才展示
            int bs_delay = JSONUtils.getInt(dataObj, "bs_delay", KEY_SERVER_BS_DELAY_DEFAULT);
            edit.putInt(KEY_SERVER_BS_DELAY, bs_delay);
            LogUtil.d("zzzz_night", "bs_delay: " + bs_delay);
            // 床头钟: 每日展示的TimeZone（0点~7点）。
            String bs_tz = JSONUtils.getString(dataObj, "bs_tz", KEY_SERVER_BS_TZ_DEFAULT);
            edit.putString(KEY_SERVER_BS_TIMEZONE, bs_tz);
            LogUtil.d("zzzz_night", "bs_tz: " + bs_tz);

            // 床头钟: 广告渠道 优先级配置(加密存储)
            String bs_an_prlist = JSONUtils.getString(dataObj, "bs_an_prlist", null);
            LogUtil.d("zzzz_night", "bs_an_prlist: " + bs_an_prlist.toString());
            edit.putString(KEY_SERVER_BS_AD_PRLIST, EncodeUtils.encrypt(bs_an_prlist));
            // 床头钟: 广告id配置
            JSONObject bs_an_config = JSONUtils.getJSONObject(dataObj, "bs_an_config");
            LogUtil.d("zzzz_night", "bs_an_config: " + bs_an_config.toString());
            try {
                edit.putString(KEY_SERVER_BS_AD_CONFIG,
                        bs_an_config == null ? "" : EncodeUtils.encrypt(bs_an_config.toString()));
            } catch (Exception e) {
            }
            // 床头钟: 广告展示间隔，单位秒（同等于请求最小间隔）
            int bs_an_distime = JSONUtils.getInt(dataObj, "bs_an_distime", KEY_SERVER_BS_AD_DISTIME_DEFAULT);
            edit.putInt(KEY_SERVER_BS_AD_DISTIME, bs_an_distime);
            LogUtil.d("zzzz_night", "bs_an_distime: " + bs_an_distime);
            // 床头钟: 广告展示间隔，单位秒（同等于请求最小间隔）
            int bs_an_loadgp = JSONUtils.getInt(dataObj, "bs_an_loadgp", KEY_SERVER_BS_AD_LOAD_GAP_DEFAULT);
            edit.putInt(KEY_SERVER_BS_AD_LOADGAP, bs_an_loadgp);
            LogUtil.d("zzzz_night", "bs_an_loadgp: " + bs_an_loadgp);

            edit.apply();
        }
    }

}
