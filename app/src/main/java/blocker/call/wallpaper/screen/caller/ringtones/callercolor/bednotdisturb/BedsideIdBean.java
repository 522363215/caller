package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bednotdisturb;

import android.content.SharedPreferences;


import java.util.Calendar;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DateUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.EncodeUtils;

/**
 * Created by Sandy on 18/5/4.
 */

public class BedsideIdBean {

    private String id = null;    // 对应广告渠道id
    private int sizeType = 0;    // 0:小图banner，1：大图banner
    private int maxLoadCount = 0;// 每天最多请求次数（也作为随机比率）

    public BedsideIdBean(String id, int sizeType, int maxLoadCount) {
        this.id = id;
        this.sizeType = sizeType;
        this.maxLoadCount = maxLoadCount;

        String encodeName = EncodeUtils.encrypt(id);
        KEY_TIME = "last_time_" + encodeName;
        KEY_COUNT = "last_ct_" + encodeName;
        // init record count&time
        lastRefreshTime = PreferenceHelper.getLong(KEY_TIME, 0);
        lastRefreshCount = PreferenceHelper.getInt(KEY_COUNT, 0);
    }

    public String getId() {
        return id;
    }

    public int getSizeType() {
        return sizeType;
    }

    public int getMaxLoadCount() {
        return maxLoadCount;
    }

    // ////////////////////
    // 刷新后计次
    // ////////////////////

    // 后刷新计次的key
    private final String KEY_TIME;
    private final String KEY_COUNT;

    private long lastRefreshTime = 0;
    private int lastRefreshCount = 0;

    public boolean isMaxToday() {
        if(!DateUtils.isSameToady(lastRefreshTime)) {
            lastRefreshCount = 0;
            saveCount(0);
        }
        return lastRefreshCount >= maxLoadCount;
    }

    // 记录一次刷新
    public void recordCount() {
        // record
        if(DateUtils.isSameToady(lastRefreshTime)) {
            lastRefreshCount++;
        }else {
            lastRefreshCount = 1;
        }
        saveCount(lastRefreshCount);
    }

    private void saveCount(int count) {
        lastRefreshTime = System.currentTimeMillis();
        PreferenceHelper.putLong(KEY_TIME, lastRefreshTime);
        PreferenceHelper.putInt(KEY_COUNT, count);
    }

    @Override
    public String toString() {
        if(BuildConfig.DEBUG) {
            String log = "[" + id;
            log += ", size=" + sizeType;
            log += ", count=" + maxLoadCount;
            log += ", lastRefCount=" + lastRefreshCount;
            log += ", lastRefTime=" + getPrintTimeStamp(lastRefreshTime);
            log += "]";
            return log;
        }
        return super.toString();
    }

    private static String getPrintTimeStamp(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return year + ", " + month + ", " + day + ", " + hour + ", " + min + ", " + second;
    }

}
