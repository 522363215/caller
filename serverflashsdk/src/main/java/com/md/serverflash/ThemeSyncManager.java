package com.md.serverflash;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.md.serverflash.beans.Category;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.callback.CategoryCallback;
import com.md.serverflash.callback.SingleTopicThemeCallback;
import com.md.serverflash.callback.ThemeNormalCallback;
import com.md.serverflash.callback.ThemeSyncCallback;
import com.md.serverflash.callback.TopicThemeCallback;
import com.md.serverflash.local.ThemeSyncLocal;
import com.md.serverflash.util.HttpUtil;
import com.md.serverflash.util.LogUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ChenR on 2018/5/7.
 */

public class ThemeSyncManager {

    public static long EXPIRE_TIME = 4 * DateUtils.HOUR_IN_MILLIS;
    private static final String THEME_RESOURCES_DIR = Environment.DIRECTORY_MOVIES;

    private ThemeSyncManager() {
    }

    private static ThemeSyncManager instance = null;

    public static ThemeSyncManager getInstance() {
        if (instance == null) {
            synchronized (ThemeSyncManager.class) {
                if (instance == null) {
                    instance = new ThemeSyncManager();
                }
            }
        }
        return instance;
    }

    private WeakReference<Context> mContext;
    private String language;
    private String channel;
    private String subChannel;
    private int testMode;

    /**
     * @param context
     * @param language
     * @param channel
     * @param subChannel
     * @param isTest     0 => formal, 1 => test;
     */
    public void init(Context context, String language, String channel, String subChannel, long minSyncInterval, boolean isTest) {
        this.mContext = new WeakReference<Context>(context);
        this.language = language;
        this.channel = channel;
        this.subChannel = subChannel;
        this.testMode = isTest ? 1 : 0;
        EXPIRE_TIME = minSyncInterval == -1 ? EXPIRE_TIME : minSyncInterval;

        HttpUtil.initHttpConfig(language, channel, subChannel, testMode);
    }

    public Context getContext() {
        return mContext.get();
    }

    public File getFileByUrl(Context appContext, String url) {
        String themeDir = getThemeStorageDir(appContext);
        String fileName = getFileNameFromUrl(url);
        File file = null;
        try {
            if (!TextUtils.isEmpty(themeDir) && !TextUtils.isEmpty(fileName)) {
                file = new File(themeDir, fileName);
            }

        } catch (Exception e) {
        }
        return file;
    }

    public File getOldThemeFileByUrl(Context app, String url) {
        if (app == null || TextUtils.isEmpty(url)) {
            return null;
        }

        File file = null;
        try {
            File dir = app.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            if (dir != null && !dir.exists()) {
                dir.mkdir();
            }
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            if (dir != null && dir.exists()) {
                file = new File(dir, fileName);
            }
        } catch (Exception e) {
        }
        return file;
    }

    public File getVideoFirstFrameFileByUrl(Context appContext, String url) {
        String themeDir = getThemeFirstFrameStorageDir(appContext);
        String fileName = getFileNameFromUrl(url);
        File file = null;
        try {
            if (!TextUtils.isEmpty(themeDir)) {
                file = new File(themeDir, fileName);
            }
        } catch (Exception e) {
        }
        return file;
    }

    private String getFileNameFromUrl(String url) {
        if (TextUtils.isEmpty(url)) return "";
        return toHexStr(url.substring(url.lastIndexOf("/") + 1));
    }

    private String getThemeStorageDir() {
        String path = null;
        if (mContext != null && mContext.get() != null) {
            Context context = mContext.get();
            try {
                File file = context.getExternalFilesDir(THEME_RESOURCES_DIR);
                if (file != null) {
                    path = file.getAbsolutePath();
                }
            } catch (Exception e) {
            }
        }
        return path;
    }

    public boolean existThemeResourcesByUrl(String url) {
        File file = getFileByUrl(mContext.get(), url);
        return file != null && file.exists();
    }

    public void syncCategoryList(final CategoryCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.e(LogUtil.TAG, "syncCategoryList: context is null");
            return;
        }

        long time = ThemeSyncLocal.getInstance().getUpdateTime(ThemeSyncLocal.PREF_SUFFIX_CATEGORY_LIST);
        if (time != 0 && DateUtils.isToday(time) && callback != null) {
            List<Category> list = ThemeSyncLocal.getInstance().getCategoryList();
            if (list != null && list.size() > 0) {
                callback.onSuccess(200, list);
                return;
            }
        }

        HttpUtil.requestCategoryList(callback);
    }

    /**
     * 素材列表; 默认第一页, 50条数据, 所有文件类型;
     *
     * @param categoryListId 对应分类列表id (px_id)
     */
    public void syncPageData(int categoryListId, final ThemeSyncCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.e(LogUtil.TAG, "syncPageData: context is null.");
            return;
        }

        long now = System.currentTimeMillis();
        long time = ThemeSyncLocal.getInstance().getPageDataUpdateTime(categoryListId);
        if ((now - time < EXPIRE_TIME) && callback != null) {
            List<Theme> list = ThemeSyncLocal.getInstance().getPageData(categoryListId);
            if (list != null && list.size() > 0) {
                callback.onSuccess(list);
                return;
            }
        }

        HttpUtil.requestPageData(50, 1, categoryListId, 0, callback);
    }

    public void syncTopicData(String topic, int pageLength, final SingleTopicThemeCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.e(LogUtil.TAG, "syncTopicData: context is null.");
            return;
        }

        if (TextUtils.isEmpty(topic)) {
            LogUtil.e(LogUtil.TAG, "syncTopicData: topic name is null.");
            return;
        }
        HttpUtil.requestTopicData(topic, 1, pageLength, callback);

//        long now = System.currentTimeMillis();
//
//        long topicUpdate = ThemeSyncLocal.getInstance().getTopicDataListUpdateTime(topic);
//        if ((now - topicUpdate) > EXPIRE_TIME) {
//            HttpUtil.requestTopicData(topic, 1, pageLength, callback);
//        }else{
//            if (callback != null) {
//
//                List<Theme> list = ThemeSyncLocal.getInstance().getTopicDataList(topic);
//                if (list != null && list.size() > 0) {
//                    callback.onSuccess(200, list);
//                }
//            }
//        }

    }

    /**
     * 获取专题列表; 默认请求专题的第一页,
     *
     * @param topicName 专题名称;
     */
    public void syncTopicData(String[] topicName, int pageNum, final TopicThemeCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.e(LogUtil.TAG, "syncTopicData: context is null.");
            return;
        }

        if (topicName == null || topicName.length == 0) {
            LogUtil.e(LogUtil.TAG, "syncTopicData: topic name is null.");
            return;
        }

        long now = System.currentTimeMillis();
        boolean bool = true;
        for (String topic : topicName) {
            long topicUpdate = ThemeSyncLocal.getInstance().getTopicDataListUpdateTime(topic);
            if ((now - topicUpdate) > EXPIRE_TIME) {
                bool = false;
                break;
            }
        }
        if (bool && callback != null) {
            Map<String, List<Theme>> map = new HashMap<>();
            for (String topic : topicName) {
                List<Theme> list = ThemeSyncLocal.getInstance().getTopicDataList(topic);
                if (list != null && list.size() > 0) {
//                    if (list.size() > pageNum) {
//                        map.put(topic, list.subList(0, pageNum));
//                    } else/* if (pageNum < 10 && list.size() < 10)*/ {
                    map.put(topic, list);
//                    }
                }
            }
            if (map.size() > 0) {
                callback.onSuccess(200, map);
                return;
            }
        }

        int[] topicWhichNum = new int[topicName.length];
        int[] pageNumber = new int[topicName.length];
        for (int i = 0; i < topicName.length; i++) {
            topicWhichNum[i] = 1;
            pageNumber[i] = pageNum;
        }

        HttpUtil.requestTopicData(topicName, topicWhichNum, pageNumber, callback);
    }

    public void syncJustLike(long themeId, boolean like, final ThemeNormalCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.d(LogUtil.TAG, "syncJustLike: context is null");
            return;
        }

        HttpUtil.requestJustLike(themeId, like, callback);
    }

    /**
     * 获取分类版本
     *
     * @param typeName 专题版本的名字, 如: Recommend (区分大小写)
     * @param type     请求的flash版本类型; 1 => 分类 3 => 专题
     */
    public void syncTypeVersion(String typeName, int type, final ThemeNormalCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.e(LogUtil.TAG, "syncTypeVersion: context is null");
            return;
        }

        String time = ThemeSyncLocal.getInstance().getGeneralParameter(ThemeSyncLocal.PREF_KEY_UPDATE_TYPE_VERSION_TIME + "_" + typeName + "_" + type);
        time = TextUtils.isEmpty(time) ? "0" : time;
        if (DateUtils.isToday(Long.parseLong(time)) && callback != null) {
            callback.onSuccess(200, ThemeSyncLocal.getInstance().getGeneralParameter(ThemeSyncLocal.PREF_KEY_CURRENT_TYPE_VERSION + "_" + typeName + "_" + type));
            return;
        }

        HttpUtil.requestTypeVersion(typeName, type, callback);
    }

    /**
     * 获取指定专题下的子专题列表
     *
     * @param topicName 父专题名称, 如: diy_3d;
     */
    public void syncTopicList(String topicName, final ThemeSyncCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.e(LogUtil.TAG, "syncTopicList: context is null.");
            return;
        }

        long now = System.currentTimeMillis();
        if ((now - ThemeSyncLocal.getInstance().getTopicListUpdateTime(topicName) < EXPIRE_TIME) && callback != null) {
            List<Theme> list = ThemeSyncLocal.getInstance().getTopicList(topicName);
            if (list != null && list.size() > 0) {
                callback.onSuccess(list);
                return;
            }
        }

        HttpUtil.requestTopicList(topicName, callback);
    }

    public void syncHomePageData(final TopicThemeCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.e(LogUtil.TAG, "syncHomePageData: context is null.");
            return;
        }

        long now = System.currentTimeMillis();
        if ((now - ThemeSyncLocal.getInstance().getUpdateTime(ThemeSyncLocal.PREF_SUFFIX_HOME_PAGE_DATA) < EXPIRE_TIME) && callback != null) {
            Map<String, List<Theme>> map = ThemeSyncLocal.getInstance().getHomePageData();
            if (map != null && map.size() > 0) {
                callback.onSuccess(200, map);
                return;
            }
        }

        HttpUtil.requestHomePageData(callback);
    }

    /**
     * 举报素材
     *
     * @param themeId 举报的素材id
     * @param reason  举报原因;
     */
    public void syncReportTheme(long themeId, String reason, ThemeNormalCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.e(LogUtil.TAG, "syncReportTheme: context is null.");
            return;
        }

        HttpUtil.reportTheme(themeId, reason, callback);
    }

    /**
     * todo 2018/5/10 需要添加新的回调接口(Search 相关)
     * 搜索
     *
     * @param keyword    关键字
     * @param fileType   文件类型
     * @param pageNumber 每页数量
     * @param whichPage  第几页
     */
    public void search(String keyword, int fileType, int pageNumber, int whichPage, final ThemeSyncCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.e(LogUtil.TAG, "search: context is null.");
            return;
        }

        HttpUtil.search(keyword, fileType, pageNumber, whichPage, callback);
    }

    /**
     * 回调返回值示例:  aaaa,bbbb,cccc,ddddd
     */
    public void syncHotKeys(final ThemeNormalCallback callback) {
        if (mContext == null || mContext.get() == null) {
            LogUtil.e(LogUtil.TAG, "syncHotKeys: context is null.");
            return;
        }

        long now = System.currentTimeMillis();
        if ((now - ThemeSyncLocal.getInstance().getUpdateTime(ThemeSyncLocal.PREF_SUFFIX_HOT_KEYS) < EXPIRE_TIME) && callback != null) {
            String hotKeys = ThemeSyncLocal.getInstance().getHotKeys();
            callback.onSuccess(200, hotKeys);
            return;
        }

        HttpUtil.requestHotKeys(callback);
    }

    public List<Theme> getDownloadedThemeList() {
        return ThemeSyncLocal.getInstance().getDownloadedList();
    }

    public List<Theme> getRecommendNewList() {
        return ThemeSyncLocal.getInstance().getRecommendNewList();
    }

    public long getRecommendNewLastUpdateTime() {
        return ThemeSyncLocal.getInstance().getRecommendNewLastUpdateTime();
    }

    public void clearRecommendNew() {
        ThemeSyncLocal.getInstance().clearRecommendNew();
    }

    public static String getThemeStorageDir(Context app) {
        String dir = "";
        try {
            File f = app.getExternalFilesDir(toHexStr(Environment.DIRECTORY_MOVIES));
            if (f != null) {
                if (!f.exists())
                    f.mkdir();
                dir = f.getAbsolutePath();
            }
        } catch (Exception e) {
        }
        return dir;
    }

    public static String getThemeFirstFrameStorageDir(Context app) {
        String dir = "";
        try {
            File f = app.getExternalFilesDir(toHexStr(Environment.DIRECTORY_PICTURES));
            if (f != null && !f.exists()) {
                f.mkdir();
            }
            dir = f != null ? f.getAbsolutePath() : dir;
        } catch (Exception e) {
        }
        return dir;
    }

    private static String toHexStr(String msg) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = msg.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    public static String toHexStr(String msg) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = msg.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    public List<Theme> getCacheTopicData(String topic, int pageNumber, int pageLength) {
        List<Theme> data = null;
        if (!TextUtils.isEmpty(topic) && pageNumber > 0 && pageLength > 0) {
            List<Theme> topicList = ThemeSyncLocal.getInstance().getTopicDataList(topic);
            if (topicList != null && !topicList.isEmpty()) {
                int index = (pageNumber - 1) * pageLength;
                int size = topicList.size();
                if (size > index) {
                    int toIndex = index + pageLength;
                    data = topicList.subList(index, size > toIndex ? toIndex : size);
                }
            }
        }
        return data;
    }

    public void resetTopicDataCache() {
        ThemeSyncLocal.getInstance().resetTopicDataCache();
    }

    /**
     * 获取首页缓存的数据
     */
    public List<Theme> getCacheTopicDataList(String topicName) {
        return ThemeSyncLocal.getInstance().getTopicDataList(topicName);
    }

    /**
     * 获取每一类缓存的数据
     */
    public List<Theme> getCacheCategoryDataList(int categoryListId) {
        return ThemeSyncLocal.getInstance().getPageData(categoryListId);
    }

}
