package com.md.flashset.manager;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.md.flashset.R;
import com.md.flashset.Utils.CallFlashConstansUtil;
import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashClassification;
import com.md.flashset.bean.CallFlashFormat;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.download.DownloadState;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Theme;
import com.md.serverflash.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leo on 17/9/4.
 */
public class CallFlashManager {
    public static final String ONLINE_THEME_TOPIC_NAME_FEATURED = "Featured";
    public static final String ONLINE_THEME_TOPIC_NAME_NON_FEATURED = "Non-Featured";
    public static final String ONLINE_THEME_TOPIC_NAME_LOCAL_HOME = "Localhome";
    public static final String ONLINE_THEME_TOPIC_NAME_NEW_FLASH = "Newflash";

    private static CallFlashManager instance;
    private ArrayList<CallFlashInfo> mAllLocalFlashList = new ArrayList<>();
    private Context mContext;

    private CallFlashManager() {
    }

    public void init(Context context) {
        mContext = context;
    }

    public synchronized static CallFlashManager getInstance() {
        if (null == instance) {
            instance = new CallFlashManager();
        }
        return instance;
    }

    public File getOnlineThemeSourcePath(String sourceUrl) {
        return ThemeSyncManager.getInstance().getFileByUrl(mContext, sourceUrl);
    }

    // 仅仅初始化保存固定的来电秀;
    private void initFlashDataAndSave() {
        List<CallFlashInfo> classicList = new ArrayList<>();
        CallFlashInfo monkey = new CallFlashInfo();
        monkey.id = String.valueOf(FlashLed.FLASH_TYPE_MONKEY);
        monkey.title = CallFlashConstansUtil.CALL_FLASH_THEME_GIF_NAME_MONKEY;
        monkey.imgResId = R.drawable.icon_call_flash_monkey_bg;
        monkey.format = CallFlashFormat.FORMAT_VIDEO;
        monkey.path = "android.resource://" + mContext.getPackageName() + "/" + R.raw.monkey;
        monkey.url = "";
        monkey.isDownloadSuccess = true;
        monkey.isHaveSound = true;
        monkey.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        monkey.flashType = FlashLed.FLASH_TYPE_MONKEY;
        monkey.position = 3;
        monkey.flashClassification = CallFlashClassification.CLASSIFICATION_CLASSIC;

        CallFlashInfo festival = new CallFlashInfo();
        festival.id = String.valueOf(FlashLed.FLASH_TYPE_FESTIVAL);
        festival.title = mContext.getString(R.string.call_flash_led_festival);
        festival.imgResId = R.drawable.icon_flash_festival_small;
        festival.format = CallFlashFormat.FORMAT_CUSTOM_ANIM;
        festival.path = "";
        festival.url = "";
        festival.isDownloadSuccess = true;
        festival.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        festival.flashType = FlashLed.FLASH_TYPE_FESTIVAL;
        festival.position = 5;
        festival.flashClassification = CallFlashClassification.CLASSIFICATION_CLASSIC;

        CallFlashInfo love = new CallFlashInfo();
        love.id = String.valueOf(FlashLed.FLASH_TYPE_LOVE);
        love.title = mContext.getString(R.string.call_flash_led_love);
        love.imgResId = R.drawable.icon_flash_love_small;
        love.format = CallFlashFormat.FORMAT_CUSTOM_ANIM;
        love.path = "";
        love.url = "";
        love.isDownloadSuccess = true;
        love.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        love.flashType = FlashLed.FLASH_TYPE_LOVE;
        love.position = 6;
        love.flashClassification = CallFlashClassification.CLASSIFICATION_CLASSIC;

        CallFlashInfo kiss = new CallFlashInfo();
        kiss.id = String.valueOf(FlashLed.FLASH_TYPE_KISS);
        kiss.title = mContext.getString(R.string.call_flash_led_kiss);
        kiss.imgResId = R.drawable.icon_flash_kiss_small;
        kiss.format = CallFlashFormat.FORMAT_CUSTOM_ANIM;
        kiss.path = "";
        kiss.url = "";
        kiss.isDownloadSuccess = true;
        kiss.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        kiss.flashType = FlashLed.FLASH_TYPE_KISS;
        kiss.position = 7;
        kiss.flashClassification = CallFlashClassification.CLASSIFICATION_CLASSIC;

        CallFlashInfo rose = new CallFlashInfo();
        rose.id = String.valueOf(FlashLed.FLASH_TYPE_ROSE);
        rose.title = mContext.getString(R.string.call_flash_led_rose);
        rose.imgResId = R.drawable.icon_flash_flower_small;
        rose.format = CallFlashFormat.FORMAT_CUSTOM_ANIM;
        rose.path = "";
        rose.url = "";
        rose.isDownloadSuccess = true;
        rose.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        rose.flashType = FlashLed.FLASH_TYPE_ROSE;
        rose.position = 7;
        rose.flashClassification = CallFlashClassification.CLASSIFICATION_CLASSIC;

        CallFlashInfo streamer = new CallFlashInfo();
        streamer.id = String.valueOf(FlashLed.FLASH_TYPE_STREAMER);
        streamer.title = mContext.getString(R.string.call_falsh_led_streamer);
        streamer.imgResId = R.drawable.icon_flash_streamer_small;
        streamer.format = CallFlashFormat.FORMAT_CUSTOM_ANIM;
        streamer.path = "";
        streamer.url = "";
        streamer.isDownloadSuccess = true;
        streamer.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        streamer.flashType = FlashLed.FLASH_TYPE_STREAMER;
        classicList.add(love);
        classicList.add(monkey);
        classicList.add(kiss);
        classicList.add(festival);
        classicList.add(rose);
        classicList.add(streamer);
        mAllLocalFlashList.addAll(classicList);
    }

    public ArrayList<CallFlashInfo> getAllLocalFlashList() {
        if (mAllLocalFlashList.size() == 0) {
            initFlashDataAndSave();
        }
        return mAllLocalFlashList;
    }


    public List<CallFlashInfo> themeToCallFlashInfo(List<Theme> res) {
        List<CallFlashInfo> dot = null;

        if (res != null && res.size() > 0) {
            dot = new ArrayList<CallFlashInfo>(res.size());
            for (Theme item : res) {
                if (item == null) {
                    continue;
                }

                CallFlashInfo info = new CallFlashInfo();
                info.id = String.valueOf(item.getId());
                info.collection = (int) item.getCollection();
                info.title = item.getTitle();
                info.flashType = FlashLed.FLASH_TYPE_DYNAMIC;
                info.downloadCount = (int) item.getDownload();
                info.commentCount = (int) item.getComment();
                info.img_hUrl = item.getImg_h();
                info.url = item.getUrl();
                info.img_vUrl = item.getImg_v();
                info.isHaveSound = item.getType() != 103;
                info.logoUrl = item.getLogo();
                info.logoPressUrl = item.getLogo_pressed();
                info.intro = item.getIntro();
                info.isOnlionCallFlash = true;

                CallFlashInfo cacheCallFlashInfo = getCacheJustLikeFlashList(info.id);
                if (cacheCallFlashInfo != null) {
                    info.likeCount = item.getNum_of_likes() >= cacheCallFlashInfo.likeCount ? item.getNum_of_likes() : cacheCallFlashInfo.likeCount;
                    info.isLike = cacheCallFlashInfo.isLike;
                } else {
                    info.likeCount = item.getNum_of_likes();
                    info.isLike = false;
                }

                if (item.getDownload() == Theme.DOWNLOADED) {
                    info.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
                } else if (item.getDownload() == Theme.UNDOWNLOADED) {
                    info.downloadState = DownloadState.STATE_NOT_DOWNLOAD;
                } else {
                    info.downloadState = DownloadState.STATE_DOWNLOAD_FAIL;
                }

                File resFile = ThemeSyncManager.getInstance().getFileByUrl(mContext, info.url);
                if (resFile != null) {
                    if (resFile.exists()) {
                        info.isDownloaded = true;
                        info.isDownloadSuccess = true;
                    } else {
                        info.isDownloaded = false;
                        info.isDownloadSuccess = false;
                    }
                    info.path = resFile.getAbsolutePath();
                }

                File imgFile = ThemeSyncManager.getInstance().getFileByUrl(mContext, info.img_vUrl);
                info.imgPath = imgFile != null ? imgFile.getAbsolutePath() : "";

                if (info.url.endsWith("mp4") || info.url.endsWith("MP4")) {
                    info.format = CallFlashFormat.FORMAT_VIDEO;
                } else if (info.url.endsWith("gif") || info.url.endsWith("GIF")) {
                    info.format = CallFlashFormat.FORMAT_GIF;
                }
                dot.add(info);
            }
        }

        return dot;
    }

    public CallFlashInfo getCustomCallFlash(String sourcePath) {
        CallFlashInfo info = null;
        if (!TextUtils.isEmpty(sourcePath)) {
            info = new CallFlashInfo();
            info.id = String.valueOf(FlashLed.FLASH_TYPE_CUSTOM);
            info.path = sourcePath;
            info.isDownloadSuccess = true;
            info.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
            info.format = CallFlashFormat.FORMAT_GIF;
            info.flashType = FlashLed.FLASH_TYPE_CUSTOM;
            info.img_vUrl = info.url = info.logoUrl = info.logoPressUrl = info.img_hUrl = "";
            info.isDownloaded = true;
        }
        return info;
    }

    public boolean isAlreadyDownload(String themeId, List<Theme> downloadedList) {
        boolean is = false;
        if (downloadedList != null && downloadedList.size() > 0 && !TextUtils.isEmpty(themeId)) {
            for (Theme theme : downloadedList) {
                if (theme != null) {
                    if (String.valueOf(theme.getId()).equals(themeId)) {
                        is = true;
                        break;
                    }
                }
            }
        }
        return is;
    }

    public static void saveFlashJustLike(CallFlashInfo info) {
        if (info == null || TextUtils.isEmpty(info.id)) {
            return;
        }

        List<CallFlashInfo> list = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.PREF_JUST_LIKE_FLASH_LIST, CallFlashInfo[].class);
        if (list == null) {
            list = new ArrayList<>();
        }
        if (list.contains(info)) {
            CallFlashInfo item = list.get(list.indexOf(info));
            item.isLike = info.isLike;
            item.likeCount = info.likeCount;
        } else {
            list.add(info);
        }
        CallFlashPreferenceHelper.setDataList(CallFlashPreferenceHelper.PREF_JUST_LIKE_FLASH_LIST, list);
    }

    public CallFlashInfo getCacheJustLikeFlashList(String id) {
        CallFlashInfo info = null;
        List<CallFlashInfo> list = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.PREF_JUST_LIKE_FLASH_LIST, CallFlashInfo[].class);
        if (list != null && list.size() > 0) {
            for (CallFlashInfo temp : list) {
                if (temp.id.equals(id)) {
                    info = temp;
                    break;
                }
            }
        }
        return info;
    }

    public void saveCallFlashDownloadCount(CallFlashInfo info) {
        if (info == null || TextUtils.isEmpty(info.id)) {
            return;
        }

        List<String> saveDownloadCountCallFlashUrl = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.CALL_FLASH_SAVE_DOWNLOAD_COUNT_URLS, String[].class);
        if (saveDownloadCountCallFlashUrl != null && saveDownloadCountCallFlashUrl.contains(info.url)) {
            //已经保存过
            return;
        }
        if (saveDownloadCountCallFlashUrl == null) {
            saveDownloadCountCallFlashUrl = new ArrayList<>();
        }
        saveDownloadCountCallFlashUrl.add(info.url);
        CallFlashPreferenceHelper.setDataList(CallFlashPreferenceHelper.CALL_FLASH_SAVE_DOWNLOAD_COUNT_URLS, saveDownloadCountCallFlashUrl);

        List<CallFlashInfo> list = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.PREF_JUST_LIKE_FLASH_LIST, CallFlashInfo[].class);
        if (list == null) {
            list = new ArrayList<>();
        }
        if (list.contains(info)) {
            CallFlashInfo item = list.get(list.indexOf(info));
            item.downloadCount = info.downloadCount + 1;
        } else {
            info.downloadCount = info.downloadCount + 1;
            list.add(info);
        }
        CallFlashPreferenceHelper.setDataList(CallFlashPreferenceHelper.PREF_JUST_LIKE_FLASH_LIST, list);
    }

    public List<CallFlashInfo> getCollectionCallFlash() {
        List<CallFlashInfo> collectionList = new ArrayList<>();
        List<CallFlashInfo> list = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.PREF_JUST_LIKE_FLASH_LIST, CallFlashInfo[].class);
        if (list == null) return collectionList;
        for (CallFlashInfo info : list) {
            if (info.isLike) {
                collectionList.add(info);
            }
        }
        return collectionList;
    }

}
