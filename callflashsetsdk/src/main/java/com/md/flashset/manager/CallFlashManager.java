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

    public static final int BIG_TOPIC_THEME_REQUEST_COUNT = 100;
    public static final int SMALL_TOPIC_THEME_REQUEST_COUNT = 6;
    public static final int LOCAL_HOME_REQUEST_COUNT = 2;

    private static CallFlashManager instance;
    private ArrayList<CallFlashInfo> mAllLocalFlashList = new ArrayList<>();
    private ArrayList<CallFlashInfo> mClassicFlashList = new ArrayList<>();
    private List<CallFlashInfo> mLocalClassMoreFlashList = new ArrayList<>();
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

    public static int getMaxReqCount() {
        return CallFlashPreferenceHelper.getInt("pref_request_theme_count_max", BIG_TOPIC_THEME_REQUEST_COUNT);
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
//        monkey.logoResId = R.drawable.ic_flash_monkey;
//        monkey.logoPressResId = R.drawable.icon_call_flash_monkey_on;
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

//        festival.logoResId = R.drawable.ic_flash_light;
//        festival.logoPressResId = R.drawable.ic_festival_on;
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
//        love.logoResId = R.drawable.ic_flash_heart;
//        love.logoPressResId = R.drawable.ic_love_on;
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
//        kiss.logoResId = R.drawable.ic_flash_kiss;
//        kiss.logoPressResId = R.drawable.ico_switch_kiss_ok;
        kiss.position = 7;
        kiss.flashClassification = CallFlashClassification.CLASSIFICATION_CLASSIC;

        classicList.add(love);
        classicList.add(monkey);
        classicList.add(kiss);
        classicList.add(festival);
        mClassicFlashList.addAll(classicList);
        mLocalClassMoreFlashList.addAll(classicList);

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
        mLocalClassMoreFlashList.add(rose);

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
        mLocalClassMoreFlashList.add(streamer);
//        classicList.add(streamer);

        /*CallFlashInfo visual_1 = new CallFlashInfo();
        visual_1.id = String.valueOf(FlashLed.FLASH_TYPE_VISUAL_1);
        visual_1.title = ConstantUtils.CALL_FLASH_THEME_GIF_NAME_VISUAL_1;
        visual_1.format = CallFlashFormat.FORMAT_GIF;
        visual_1.url = ConstantUtils.CALL_FLASH_THEME_GIF_URL_VISUAL_1;
        visual_1.path = getOnlineThemeSourcePath(visual_1.url).getAbsolutePath();
        visual_1.isDownloadSuccess = new File(visual_1.path).exists();
        visual_1.downloadState = visual_1.isDownloadSuccess ? DownloadState.STATE_DOWNLOAD_SUCCESS : DownloadState.STATE_NOT_DOWNLOAD;
        visual_1.imgResId = R.drawable.icon_call_flash_visual_bg_1;
        visual_1.logoResId = R.drawable.icon_call_flash_visual_1_off;
        visual_1.logoPressResId = R.drawable.icon_call_flash_visual_1_on;
        visual_1.flashType = FlashLed.FLASH_TYPE_VISUAL_1;

        CallFlashInfo love_1 = new CallFlashInfo();
        love_1.id = String.valueOf(FlashLed.FLASH_TYPE_LOVE1);
        love_1.title = ApplicationEx.getInstance().getApplicationContext().getString(R.string.call_flash_led_love1);
        love_1.imgResId = R.drawable.icon_call_flash_love1_bg;
        love_1.format = CallFlashFormat.FORMAT_GIF;
        love_1.url = ConstantUtils.CALL_FLASH_THEME_GIF_URL_LOVE1;
        love_1.path = getOnlineThemeSourcePath(love_1.url).getAbsolutePath();
        love_1.isDownloadSuccess = true;
        love_1.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        love_1.flashType = FlashLed.FLASH_TYPE_LOVE1;
        love_1.logoResId = R.drawable.icon_call_flash_lover1_off;
        love_1.logoPressResId = R.drawable.icon_call_flash_lover1_on;

        CallFlashInfo visual_2 = new CallFlashInfo();
        visual_2.id = String.valueOf(FlashLed.FLASH_TYPE_VISUAL_2);
        visual_2.title = ConstantUtils.CALL_FLASH_THEME_GIF_NAME_VISUAL_2;
        visual_2.format = CallFlashFormat.FORMAT_GIF;
        visual_2.url = ConstantUtils.CALL_FLASH_THEME_GIF_URL_VISUAL_2;
        visual_2.path = getOnlineThemeSourcePath(visual_2.url).getAbsolutePath();
        visual_2.isDownloadSuccess = new File(visual_2.path).exists();
        visual_2.downloadState = visual_2.isDownloadSuccess ? DownloadState.STATE_DOWNLOAD_SUCCESS : DownloadState.STATE_NOT_DOWNLOAD;
        visual_2.imgResId = R.drawable.icon_call_flash_visual_bg_2;
        visual_2.logoResId = R.drawable.icon_call_flash_visual_2_off;
        visual_2.logoPressResId = R.drawable.icon_call_flash_visual_2_on;
        visual_2.flashType = FlashLed.FLASH_TYPE_VISUAL_2;

        CallFlashInfo rap = new CallFlashInfo();
        rap.id = String.valueOf(FlashLed.FLASH_TYPE_RAP);
        rap.title = ConstantUtils.CALL_FLASH_THEME_VIDEO_NAME_RAP;
        rap.imgResId = R.drawable.icon_call_flash_rap_bg;
        rap.format = CallFlashFormat.FORMAT_VIDEO;
        rap.url = ConstantUtils.CALL_FLASH_THEME_VIDEO_URL_RAP;
        rap.path = getOnlineThemeSourcePath(rap.url).getAbsolutePath();
        rap.isDownloadSuccess = new File(rap.path).exists();
        rap.downloadState = rap.isDownloadSuccess ? DownloadState.STATE_DOWNLOAD_SUCCESS : DownloadState.STATE_NOT_DOWNLOAD;
        rap.flashType = FlashLed.FLASH_TYPE_RAP;
        rap.logoResId = R.drawable.icon_call_flash_rap_off;
        rap.logoPressResId = R.drawable.icon_call_flash_rap_on;

        CallFlashInfo bear = new CallFlashInfo();
        bear.id = String.valueOf(FlashLed.FLASH_TYPE_BEAR);
        bear.title = ConstantUtils.CALL_FLASH_THEME_VIDEO_NAME_BEAR;
        bear.imgResId = R.drawable.icon_call_flash_bear_bg;
        bear.format = CallFlashFormat.FORMAT_VIDEO;
        bear.url = ConstantUtils.CALL_FLASH_THEME_VIDEO_URL_BEAR;
        bear.path = getOnlineThemeSourcePath(bear.url).getAbsolutePath();
        bear.isDownloadSuccess = true;
        bear.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        bear.flashType = FlashLed.FLASH_TYPE_BEAR;
        bear.logoResId = R.drawable.icon_call_flash_bear_off;
        bear.logoPressResId = R.drawable.icon_call_flash_bear_on;

        CallFlashInfo panda = new CallFlashInfo();
        panda.id = String.valueOf(FlashLed.FLASH_TYPE_PANDA);
        panda.title = ConstantUtils.CALL_FLASH_THEME_GIF_NAME_PANDA;
        panda.imgResId = R.drawable.icon_call_flash_panda_bg;
        panda.format = CallFlashFormat.FORMAT_GIF;
        panda.url = ConstantUtils.CALL_FLASH_THEME_GIF_URL_PANDA;
        panda.path = getOnlineThemeSourcePath(panda.url).getAbsolutePath();
        panda.isDownloadSuccess = new File(panda.path).exists();
        panda.downloadState = panda.isDownloadSuccess ? DownloadState.STATE_DOWNLOAD_SUCCESS : DownloadState.STATE_NOT_DOWNLOAD;
        panda.flashType = FlashLed.FLASH_TYPE_PANDA;
        panda.logoResId = R.drawable.ic_panda_off;
        panda.logoPressResId = R.drawable.ic_panda_on;

        CallFlashInfo visual_3 = new CallFlashInfo();
        visual_3.id = String.valueOf(FlashLed.FLASH_TYPE_VISUAL_3);
        visual_3.title = ConstantUtils.CALL_FLASH_THEME_GIF_NAME_VISUAL_3;
        visual_3.format = CallFlashFormat.FORMAT_GIF;
        visual_3.url = ConstantUtils.CALL_FLASH_THEME_GIF_URL_VISUAL_3;
        visual_3.path = getOnlineThemeSourcePath(visual_3.url).getAbsolutePath();
        visual_3.isDownloadSuccess = new File(visual_3.path).exists();
        visual_3.downloadState = visual_3.isDownloadSuccess ? DownloadState.STATE_DOWNLOAD_SUCCESS : DownloadState.STATE_NOT_DOWNLOAD;
        visual_3.imgResId = R.drawable.icon_call_flash_visual_bg_3;
        visual_3.logoResId = R.drawable.icon_call_flash_visual_3_off;
        visual_3.logoPressResId = R.drawable.icon_call_flash_visual_3_on;
        visual_3.flashType = FlashLed.FLASH_TYPE_VISUAL_3;

        CallFlashInfo pig = new CallFlashInfo();
        pig.id = String.valueOf(FlashLed.FLASH_TYPE_DANCE_PIG);
        pig.title = ConstantUtils.CALL_FLASH_THEME_VIDEO_NAME_PIG;
        pig.imgResId = R.drawable.icon_call_flash_pig_bg;
        pig.format = CallFlashFormat.FORMAT_VIDEO;
        pig.url = ConstantUtils.CALL_FLASH_THEME_VIDEO_URL_PIG;
        pig.path = getOnlineThemeSourcePath(pig.url).getAbsolutePath();
        pig.isDownloadSuccess = new File(pig.path).exists();
        pig.downloadState = pig.isDownloadSuccess ? DownloadState.STATE_DOWNLOAD_SUCCESS : DownloadState.STATE_NOT_DOWNLOAD;
        pig.flashType = FlashLed.FLASH_TYPE_DANCE_PIG;
        pig.logoResId = R.drawable.icon_call_flash_pig_off;
        pig.logoPressResId = R.drawable.icon_call_flash_pig_on;*/

        /*mLocalClassMoreFlashList.add(rap);
        mLocalClassMoreFlashList.add(panda);
        mLocalClassMoreFlashList.add(pig);
        mLocalClassMoreFlashList.add(bear);
        mLocalClassMoreFlashList.add(love_1);
        mLocalClassMoreFlashList.add(visual_1);
        mLocalClassMoreFlashList.add(visual_3);
        mLocalClassMoreFlashList.add(visual_2);*/

        mAllLocalFlashList.addAll(mClassicFlashList);
        //mAllLocalFlashList.addAll(mLocalClassMoreFlashList);

//        CallFlashPreferenceHelper.setDataList(CallFlashPreferenceHelper.PREF_ALL_CALL_FLASH, dataList);
    }

    public ArrayList<CallFlashInfo> getLocalClassicFlashList() {
        if (mClassicFlashList.size() == 0) {
            initFlashDataAndSave();
        }
        return mClassicFlashList;
    }

    public ArrayList<CallFlashInfo> getLocalOnlineFlashList() {
        return (ArrayList<CallFlashInfo>) CallFlashPreferenceHelper
                .getDataList(CallFlashPreferenceHelper.PREF_PRESET_ONLINE_FLASH, CallFlashInfo[].class);
    }

    public ArrayList<CallFlashInfo> getAllLocalFlashList() {
        if (mAllLocalFlashList.size() == 0) {
            initFlashDataAndSave();
        }
        return mAllLocalFlashList;
    }

    /*public ArrayList<CallFlashInfo> onlineThemeListToFlashInfo(List<OnlineTheme> resList) {
        ArrayList<CallFlashInfo> list = null;
        if (resList != null && resList.size() > 0) {
            list = new ArrayList<CallFlashInfo>();
            for (OnlineTheme theme : resList) {
                CallFlashInfo info = new CallFlashInfo();
                info.id = theme.id;
                info.title = theme.title;
                info.url = theme.url;
                info.img_hUrl = theme.img_h;
                info.img_vUrl = theme.img_v;
                info.score = theme.score;
                // theme.comment; 没有相对应的属性,
                // theme.collection;
                info.intro = theme.intro;
                info.logoUrl = theme.logo;
                info.logoPressUrl = theme.logo_pressed;

                CallFlashInfo cacheCallFlashInfo = getCacheCallFlashInfo(info.id);
                if (cacheCallFlashInfo != null) {
                    info.likeCount = theme.num_of_likes >= cacheCallFlashInfo.likeCount ? theme.num_of_likes : cacheCallFlashInfo.likeCount;
                    info.isLike = cacheCallFlashInfo.isLike;
                } else {
                    info.likeCount = theme.num_of_likes;
                    info.isLike = false;
                }

                if (theme.download == OnlineTheme.DOWNLOADED) {
                    info.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
                } else if (theme.download == OnlineTheme.UNDOWNLOADED) {
                    info.downloadState = DownloadState.STATE_NOT_DOWNLOAD;
                } else {
                    info.downloadState = DownloadState.STATE_DOWNLOAD_FAIL;
                }

                if (theme.url.endsWith("mp4") || theme.url.endsWith("MP4")) {
                    info.format = CallFlashFormat.FORMAT_VIDEO;
                } else if (theme.url.endsWith("gif") || theme.url.endsWith("GIF")) {
                    info.format = CallFlashFormat.FORMAT_GIF;
                }

                File sourceFile = getOnlineThemeSourcePath(theme.url);
                if (sourceFile.exists()) {
                    info.isDownloaded = true;
                    info.isDownloadSuccess = true;
                } else {
                    info.isDownloaded = false;
                    info.isDownloadSuccess = false;
                }
                info.path = sourceFile.getAbsolutePath();

                File imgFile = getOnlineThemeSourcePath(theme.img_v);
                info.imgPath = imgFile.getAbsolutePath();

                info.isOnlionCallFlash = true;
                info.flashType = FlashLed.FLASH_TYPE_DYNAMIC;

                list.add(info);
            }
        }
        return list;
    }*/

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

    public List<CallFlashInfo> getLocalClassMoreThemeList() {
        if (mLocalClassMoreFlashList.isEmpty()) {
            initFlashDataAndSave();
        }

        return mLocalClassMoreFlashList;
    }

    public final static Map<String, String> oldOnlineThemes = new HashMap<String, String>() {
        {
            put("34", "pig.mp4");
            put("285", "cat.gif");
            put("284", "tom.gif");
            put("283", "chick.gif");
            put("282", "av.mp4");
            put("274", "love1.gif");
            put("273", "gif_visual_3.gif");
            put("272", "gif_visual_2.gif");
            put("271", "gif_visual_1.gif");
            put("39", "gif_panda.gif");
            put("38", "bb.mp4");
            put("37", "bear.mp4");
            put("35", "rap.mp4");
            put("286", "jerry.gif");
        }
    };

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

    public void renameOldFlashToSdkName(List<Theme> onlineThemes) {
        if (CallFlashPreferenceHelper.getBoolean("rename_old_flash_source_to_sdk", false)) {
            return;
        }
        if (CallFlashPreferenceHelper.getBoolean(CallFlashPreferenceHelper.CALL_FLASH_ON, false)) {
            int flashType = CallFlashPreferenceHelper.getInt(CallFlashPreferenceHelper.CALL_FLASH_TYPE, FlashLed.FLASH_TYPE_DEFAULT);
            if (flashType == FlashLed.FLASH_TYPE_PANDA
                    || flashType == FlashLed.FLASH_TYPE_LOVE1
                    || flashType == FlashLed.FLASH_TYPE_VISUAL_1
                    || flashType == FlashLed.FLASH_TYPE_VISUAL_2
                    || flashType == FlashLed.FLASH_TYPE_VISUAL_3
                    || flashType == FlashLed.FLASH_TYPE_DANCE_PIG
                    || flashType == FlashLed.FLASH_TYPE_RAP
                    || flashType == FlashLed.FLASH_TYPE_BEAR) {
                CallFlashPreferenceHelper.putInt(CallFlashPreferenceHelper.CALL_FLASH_TYPE, FlashLed.FLASH_TYPE_DYNAMIC);
            }
        }

        File parentFile = mContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File[] files = parentFile.listFiles();
        List<Theme> downloadedList = ThemeSyncManager.getInstance().getDownloadedThemeList();
        String flashShowPath = "";
        flashShowPath = CallFlashPreferenceHelper.getString(CallFlashPreferenceHelper.CALL_FLASH_TYPE_DYNAMIC_PATH, "");
        LogUtil.d("renameOldFlash", "flashShowPath: " + flashShowPath);

        if (files != null) {
            for (File file : files) {
                if (file == null || !file.exists()) {
                    continue;
                }
                String themeId = getOldOnlineThemesPath(mContext).get(file.getAbsolutePath());
                LogUtil.d("renameOldFlash", "themeId: " + themeId + ", file path: " + file.getAbsolutePath());
                if (!isAlreadyDownload(themeId, downloadedList)) {
                    for (Theme theme : onlineThemes) {
                        if (theme != null && !TextUtils.isEmpty(theme.getUrl()) &&
                                !TextUtils.isEmpty(themeId) && String.valueOf(theme.getId()).equals(themeId)) {
                            File newFile = ThemeSyncManager.getInstance()
                                    .getFileByUrl(mContext, theme.getUrl());
                            if (flashShowPath.equals(file.getAbsolutePath())) {
                                CallFlashPreferenceHelper.putString(CallFlashPreferenceHelper.CALL_FLASH_TYPE_DYNAMIC_PATH, newFile.getAbsolutePath());
                                List<Theme> currentFlashTheme = new ArrayList<Theme>();
                                currentFlashTheme.add(theme);
                                List<CallFlashInfo> currentFlashInfo = themeToCallFlashInfo(currentFlashTheme);
                                CallFlashPreferenceHelper.setObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, currentFlashInfo.get(0));
                            }
                            file.renameTo(newFile);
                            break;

                        }
                    }
                }
            }
        }
        CallFlashPreferenceHelper.putBoolean("rename_old_flash_source_to_sdk", true);
    }

    public Map<String, String> getOldOnlineThemesPath(Context context) {
        HashMap<String, String> map = new HashMap<>();
        try {
            String current_flash_dir = mContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath();
            map.put(current_flash_dir + File.separator + "pig.mp4", "34");
            map.put(current_flash_dir + File.separator + "cat.gif", "285");
            map.put(current_flash_dir + File.separator + "tom.gif", "284");
            map.put(current_flash_dir + File.separator + "chick.gif", "283");
            map.put(current_flash_dir + File.separator + "av.mp4", "282");
            map.put(current_flash_dir + File.separator + "lover1.gif", "274");
            map.put(current_flash_dir + File.separator + "Photo_1013_6a.gif", "273");
            map.put(current_flash_dir + File.separator + "Photo_1013_7a.gif", "272");
            map.put(current_flash_dir + File.separator + "Photo_1013_5a.gif", "271");
            map.put(current_flash_dir + File.separator + "gif_panda.gif", "39");
            map.put(current_flash_dir + File.separator + "bb.mp4", "38");
            map.put(current_flash_dir + File.separator + "video_bear.mp4", "37");
            map.put(current_flash_dir + File.separator + "rap.mp4", "35");
            map.put(current_flash_dir + File.separator + "jerry.gif", "286");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void clearAll() {
        try {
            mClassicFlashList.clear();
            mLocalClassMoreFlashList.clear();
            mAllLocalFlashList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public String getTopicOnHome() {
        String type = ONLINE_THEME_TOPIC_NAME_FEATURED;//default is featured
        return type;
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

}
