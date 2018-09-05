package com.md.flashset.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.md.flashset.R;
import com.md.flashset.View.FlashLed;
import com.md.flashset.bean.CallFlashFormat;
import com.md.flashset.bean.CallFlashInfo;
import com.md.flashset.download.DownloadState;
import com.md.flashset.helper.CallFlashPreferenceHelper;
import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Theme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Leo on 17/9/4.
 */
public class CallFlashManager {
    public static final String ONLINE_THEME_TOPIC_NAME_FEATURED = "Featured";
    public static final String ONLINE_THEME_TOPIC_NAME_NON_FEATURED = "Non-Featured";
    public static final String ONLINE_THEME_TOPIC_NAME_LOCAL_HOME = "Localhome";
    public static final String ONLINE_THEME_TOPIC_NAME_NEW_FLASH = "Newflash";

    public static final String CALL_FLASH_START_SKY_ID = "7242";

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

    public ArrayList<CallFlashInfo> getAllLocalFlashList() {
        if (mAllLocalFlashList.size() == 0) {
            CallFlashInfo info = getLocalFlash();
            mAllLocalFlashList.add(info);
        }
        return mAllLocalFlashList;
    }

    public CallFlashInfo getLocalFlash() {
        CallFlashInfo info = new CallFlashInfo();
        info.id = CALL_FLASH_START_SKY_ID;
        info.title = "star_sky2";
        info.format = CallFlashFormat.FORMAT_VIDEO;
        info.path = "android.resource://" + mContext.getPackageName() + "/" + R.raw.starsky;
        info.flashType = 65537;
        info.isHaveSound = true;
        info.imgResId = R.drawable.img_star_sky_v;
        info.img_hResId = R.drawable.img_star_sky_h;
        info.isOnlionCallFlash = false;
        info.downloadSuccessTime = -1;
        info.isDownloadSuccess = true;
        info.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        return info;
    }

    public List<CallFlashInfo> themeToCallFlashInfo(List<Theme> res) {
        List<CallFlashInfo> dot = null;
        CallFlashInfo localFlash = getLocalFlash();
        if (res != null && res.size() > 0) {
            dot = new ArrayList<CallFlashInfo>(res.size());
            for (Theme item : res) {
                if (item == null) {
                    continue;
                }

                //如果和本地的callFlahId相同则直接用本地的
                if (localFlash.id.equals(String.valueOf(item.getId()))) {
                    localFlash = setLocalFlash(item, localFlash);
                    saveDownloadedCallFlash(localFlash);
                    dot.add(localFlash);
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

                info.isLock = "1".equals(item.getIs_lock());

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
                } else {
                    info.format = CallFlashFormat.FORMAT_IMAGE;
                }
                dot.add(info);
            }
        }

        return dot;
    }

    private CallFlashInfo setLocalFlash(Theme item, CallFlashInfo localFlash) {
        CallFlashInfo info = new CallFlashInfo();
        info.id = localFlash.id;
        info.title = localFlash.title;
        info.format = localFlash.format;
        info.path = localFlash.path;
        info.flashType = localFlash.flashType;
        info.isHaveSound = localFlash.isHaveSound;
        info.imgResId = localFlash.imgResId;
        info.img_hResId = localFlash.img_hResId;
        info.isOnlionCallFlash = localFlash.isOnlionCallFlash;
        info.downloadSuccessTime = localFlash.downloadSuccessTime;
        info.isDownloadSuccess = localFlash.isDownloadSuccess;
        info.downloadState = localFlash.downloadState;


        info.collection = (int) item.getCollection();
        info.downloadCount = (int) item.getDownload();
        info.commentCount = (int) item.getComment();
        info.img_hUrl = "";
        info.url = "";
        info.img_vUrl = "";
        info.logoUrl = "";
        info.logoPressUrl = "";
        info.intro = item.getIntro();

        CallFlashInfo cacheCallFlashInfo = getCacheJustLikeFlashList(info.id);
        if (cacheCallFlashInfo != null) {
            info.likeCount = item.getNum_of_likes() >= cacheCallFlashInfo.likeCount ? item.getNum_of_likes() : cacheCallFlashInfo.likeCount;
            info.isLike = cacheCallFlashInfo.isLike;
        } else {
            info.likeCount = item.getNum_of_likes();
            info.isLike = false;
        }

        info.imgPath = "";
        return info;
    }

    public CallFlashInfo getCustomCallFlash(String sourcePath) {
        CallFlashInfo info = null;
        if (!TextUtils.isEmpty(sourcePath)) {
            info = new CallFlashInfo();
            info.id = String.valueOf(FlashLed.FLASH_TYPE_CUSTOM);
            info.path = sourcePath;
            info.isDownloadSuccess = true;
            info.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
            if (sourcePath.endsWith(".gif") || sourcePath.endsWith(".GIF")) {
                info.format = CallFlashFormat.FORMAT_GIF;
            } else {
                info.format = CallFlashFormat.FORMAT_IMAGE;
            }
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
            if (item.isLike) {
                item.collectTime = System.currentTimeMillis();
            }
            item.isLike = info.isLike;
            item.likeCount = info.likeCount;
        } else {
            if (info.isLike) {
                info.collectTime = System.currentTimeMillis();
            }
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

        if (collectionList.size() > 0) {
            //排序(按下载时间排序)
            Collections.sort(collectionList, new Comparator<CallFlashInfo>() {
                @Override
                public int compare(CallFlashInfo o1, CallFlashInfo o2) {
                    if (o1 == null || o2 == null) return 1;
                    if (o1.collectTime > o2.collectTime) {
                        return -1;
                    } else if (o1.collectTime < o2.collectTime) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }

        return collectionList;
    }

    public void saveDownloadedCallFlash(CallFlashInfo info) {
        if (info == null) return;
        List<CallFlashInfo> downloadedCallFlashs = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.PREF_DOWNLOADED_CALL_FLASH_LIST, CallFlashInfo[].class);
        if (downloadedCallFlashs == null) {
            downloadedCallFlashs = new ArrayList<>();
        }

        //去重，并留下最新的数据
        if (downloadedCallFlashs.contains(info)) {
            downloadedCallFlashs.remove(info);
        }

        info.downloadSuccessTime = System.currentTimeMillis();
        downloadedCallFlashs.add(info);

        CallFlashPreferenceHelper.setDataList(CallFlashPreferenceHelper.PREF_DOWNLOADED_CALL_FLASH_LIST, downloadedCallFlashs);
    }

    public List<CallFlashInfo> getDownloadedCallFlash() {
        List<CallFlashInfo> list = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.PREF_DOWNLOADED_CALL_FLASH_LIST, CallFlashInfo[].class);
        CallFlashInfo localFlash = CallFlashManager.getInstance().getLocalFlash();
        if (list == null) {
            list = new ArrayList<>();
        }
        if (!list.contains(localFlash)) {
            list.add(localFlash);
        }
        if (list.size() > 0) {
            //排序(按下载时间排序)
            Collections.sort(list, new Comparator<CallFlashInfo>() {
                @Override
                public int compare(CallFlashInfo o1, CallFlashInfo o2) {
                    if (o1 == null || o2 == null) return 1;
                    if (o1.downloadSuccessTime > o2.downloadSuccessTime) {
                        return -1;
                    } else if (o1.downloadSuccessTime < o2.downloadSuccessTime) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        return list;
    }

    /**
     * 保存设置过的来电秀
     */
    public void saveSetRecordCallFlash(CallFlashInfo info) {
        if (info == null) return;
        List<CallFlashInfo> setCallFlashs = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.PREF_CALL_FLASH_SET_RECORD_LIST, CallFlashInfo[].class);
        if (setCallFlashs == null) {
            setCallFlashs = new ArrayList<>();
        }
        //去重，并留下最新的数据
        if (setCallFlashs.contains(info)) {
            setCallFlashs.remove(info);
        }
        info.setToCallFlashTime = System.currentTimeMillis();
        setCallFlashs.add(info);
        CallFlashPreferenceHelper.setDataList(CallFlashPreferenceHelper.PREF_CALL_FLASH_SET_RECORD_LIST, setCallFlashs);
    }

    public List<CallFlashInfo> getSetRecordCallFlash() {
        List<CallFlashInfo> list = CallFlashPreferenceHelper.getDataList(CallFlashPreferenceHelper.PREF_CALL_FLASH_SET_RECORD_LIST, CallFlashInfo[].class);
        if (list != null && list.size() > 0) {
            //排序(按设置的时间排序)
            Collections.sort(list, new Comparator<CallFlashInfo>() {
                @Override
                public int compare(CallFlashInfo o1, CallFlashInfo o2) {
                    if (o1 == null || o2 == null) return 1;
                    if (o1.setToCallFlashTime > o2.setToCallFlashTime) {
                        return -1;
                    } else if (o1.setToCallFlashTime < o2.setToCallFlashTime) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        return list;
    }

    public boolean isCallFlashDownloaded(CallFlashInfo info) {
        if (info == null) {
            return false;
        }

        //本地的
        if (CALL_FLASH_START_SKY_ID.equals(info.id)) {
            return true;
        }

        //根据URl获取文件
        File fileByUrl = ThemeSyncManager.getInstance().getFileByUrl(mContext, info.url);
        if (fileByUrl != null && fileByUrl.exists()) {
            return true;
        }

        String path = info.path;
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        //根据路径获取文件
        File file = new File(path);
        return file.exists();
    }

    /**
     * 是否是当前设置的call flash
     */
    public static boolean isCurrentSetCallFlash(CallFlashInfo info) {
        CallFlashInfo currentSetCallFlash = CallFlashPreferenceHelper.getObject(CallFlashPreferenceHelper.CALL_FLASH_SHOW_TYPE_INSTANCE, CallFlashInfo.class);
        if (info != null && info.equals(currentSetCallFlash)) {
            return true;
        }
        return false;
    }

    public void saveVideoFirstFrame(String url) {
        try {
            if (TextUtils.isEmpty(url)) return;
            File fileByUrl = ThemeSyncManager.getInstance().getFileByUrl(mContext, url);
            if (fileByUrl == null || !fileByUrl.exists()) return;
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            String path = fileByUrl.getAbsolutePath();
            media.setDataSource(path);
            Bitmap firstFrameBitmap = media.getFrameAtTime();
            File file = ThemeSyncManager.getInstance().getVideoFirstFrameFileByUrl(mContext, url);
            if (firstFrameBitmap != null && !file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                firstFrameBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
