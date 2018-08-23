package com.md.wallpaper;

import android.content.Context;

import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Theme;
import com.md.wallpaper.R;
import com.md.wallpaper.WallpaperLed;
import com.md.wallpaper.WallpaperPreferenceHelper;
import com.md.wallpaper.bean.DownloadState;
import com.md.wallpaper.bean.WallpaperFormat;
import com.md.wallpaper.bean.WallpaperInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WallpaperUtil {

    public static final String CALL_FLASH_START_SKY_ID = "7242";

    private static WallpaperUtil instance;
    private Context mContext;

    public void init(Context context) {
        mContext = context;
    }

    public WallpaperUtil() {
    }

    public synchronized static WallpaperUtil getInstance() {
        if (null == instance) {
            instance = new WallpaperUtil();
        }
        return instance;
    }

    public List<WallpaperInfo> getDownloadedWallpaper() {
        List<WallpaperInfo> list = WallpaperPreferenceHelper.getDataList(WallpaperPreferenceHelper.DOWNLOADED_WALLPAPERS, WallpaperInfo[].class);
        WallpaperInfo localFlash = WallpaperUtil.getInstance().getLocalFlash();
        if (list == null) {
            list = new ArrayList<>();
        }
        if (!list.contains(localFlash)) {
            list.add(localFlash);
        }
        if (list.size() > 0) {
            //排序(按下载时间排序)
            Collections.sort(list, new Comparator<WallpaperInfo>() {
                @Override
                public int compare(WallpaperInfo o1, WallpaperInfo o2) {
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

    public List<WallpaperInfo> getSetRecordWallpaper() {
        List<WallpaperInfo> list = WallpaperPreferenceHelper.getDataList(WallpaperPreferenceHelper.SETED_WALLPAPERS, WallpaperInfo[].class);
        if (list != null && list.size() > 0) {
            //排序(按设置的时间排序)
            Collections.sort(list, new Comparator<WallpaperInfo>() {
                @Override
                public int compare(WallpaperInfo o1, WallpaperInfo o2) {
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


    public List<WallpaperInfo> getCollectionCallFlash() {
        List<WallpaperInfo> collectionList = new ArrayList<>();
        List<WallpaperInfo> list = WallpaperPreferenceHelper.getDataList(WallpaperPreferenceHelper.PREF_JUST_LIKE_WALLPAPER_LIST, WallpaperInfo[].class);
        if (list == null) return collectionList;
        for (WallpaperInfo info : list) {
            if (info.isLike) {
                collectionList.add(info);
            }
        }

        if (collectionList.size() > 0) {
            //排序(按下载时间排序)
            Collections.sort(collectionList, new Comparator<WallpaperInfo>() {
                @Override
                public int compare(WallpaperInfo o1, WallpaperInfo o2) {
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

    public WallpaperInfo getCacheJustLikeFlashList(String id) {
        WallpaperInfo info = null;
        List<WallpaperInfo> list = WallpaperPreferenceHelper.getDataList(WallpaperPreferenceHelper.PREF_JUST_LIKE_WALLPAPER_LIST, WallpaperInfo[].class);
        if (list != null && list.size() > 0) {
            for (WallpaperInfo temp : list) {
                if (temp.id.equals(id)) {
                    info = temp;
                    break;
                }
            }
        }
        return info;
    }

    private WallpaperInfo setLocalFlash(Theme item, WallpaperInfo localFlash) {
        WallpaperInfo info = new WallpaperInfo();
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

        WallpaperInfo cacheCallFlashInfo = getCacheJustLikeFlashList(info.id);
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

    public WallpaperInfo getLocalFlash() {
        WallpaperInfo info = new WallpaperInfo();
        info.id = CALL_FLASH_START_SKY_ID;
        info.title = "star_sky2";
        info.format = WallpaperFormat.FORMAT_VIDEO;
//        info.path = "android.resource://" + mContext.getPackageName() + "/" + R.raw.starsky;
        info.flashType = 65537;
        info.isHaveSound = true;
//        info.imgResId = R.drawable.img_star_sky_v;
//        info.img_hResId = R.drawable.img_star_sky_h;
        info.isOnlionCallFlash = false;
        info.downloadSuccessTime = -1;
        info.isDownloadSuccess = true;
        info.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
        return info;
    }


    public List<WallpaperInfo> themeToWallpaperInfo(List<Theme> res) {
        List<WallpaperInfo> dot = null;
        WallpaperInfo localFlash = getLocalFlash();
        if (res != null && res.size() > 0) {
            dot = new ArrayList<WallpaperInfo>(res.size());
            for (Theme item : res) {
                if (item == null) {
                    continue;
                }

                //如果和本地的callFlahId相同则直接用本地的
                if (localFlash.id.equals(String.valueOf(item.getId()))) {
                    localFlash = setLocalFlash(item, localFlash);
                    saveDownloadedWallPaper(localFlash);
                    dot.add(localFlash);
                    continue;
                }

                WallpaperInfo info = new WallpaperInfo();
                info.id = String.valueOf(item.getId());
                info.collection = (int) item.getCollection();
                info.title = item.getTitle();
                info.flashType = WallpaperLed.FLASH_TYPE_DYNAMIC;
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

                WallpaperInfo cacheCallFlashInfo = getCacheJustLikeFlashList(info.id);
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
                    info.format = WallpaperFormat.FORMAT_VIDEO;
                } else if (info.url.endsWith("gif") || info.url.endsWith("GIF")) {
                    info.format = WallpaperFormat.FORMAT_GIF;
                } else {
                    info.format = WallpaperFormat.FORMAT_IMAGE;
                }
                dot.add(info);
            }
        }

        return dot;
    }
    public static void saveDownloadedWallPaper(WallpaperInfo wallpaper) {
        if (wallpaper == null) return;
        List<WallpaperInfo> downloadedWallpapers = WallpaperPreferenceHelper.getDataList(WallpaperPreferenceHelper.DOWNLOADED_WALLPAPERS, WallpaperInfo[].class);
        if (downloadedWallpapers == null) {
            downloadedWallpapers = new ArrayList<>();
        }

        //去重，并留下最新的数据
        if (downloadedWallpapers.contains(wallpaper)) {
            downloadedWallpapers.remove(wallpaper);
        }

        downloadedWallpapers.add(wallpaper);

        WallpaperPreferenceHelper.putDataList(WallpaperPreferenceHelper.DOWNLOADED_WALLPAPERS, downloadedWallpapers);
    }

    public static void saveSettedWallPaper(WallpaperInfo wallpaper) {
        if (wallpaper == null) return;
        List<WallpaperInfo> settedWallpapers = WallpaperPreferenceHelper.getDataList(WallpaperPreferenceHelper.COOKIE, WallpaperInfo[].class);
        if (settedWallpapers == null) {
            settedWallpapers = new ArrayList<>();
        }

        //去重，并留下最新的数据
        if (settedWallpapers.contains(wallpaper)) {
            settedWallpapers.remove(wallpaper);
        }

        settedWallpapers.add(wallpaper);

        WallpaperPreferenceHelper.putDataList(WallpaperPreferenceHelper.COOKIE, settedWallpapers);
    }

    public static List<WallpaperInfo> themeToWallpaprInfo(Context context, List<Theme> res) {
        List<WallpaperInfo> dot = null;
        if (res != null && res.size() > 0) {
            dot = new ArrayList<WallpaperInfo>(res.size());
            for (Theme item : res) {
                if (item == null) {
                    continue;
                }

                WallpaperInfo info = new WallpaperInfo();
                info.id = String.valueOf(item.getId());
                info.collection = (int) item.getCollection();
                info.title = item.getTitle();
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

                if (item.getDownload() == Theme.DOWNLOADED) {
                    info.downloadState = DownloadState.STATE_DOWNLOAD_SUCCESS;
                } else if (item.getDownload() == Theme.UNDOWNLOADED) {
                    info.downloadState = DownloadState.STATE_NOT_DOWNLOAD;
                } else {
                    info.downloadState = DownloadState.STATE_DOWNLOAD_FAIL;
                }

                File resFile = ThemeSyncManager.getInstance().getFileByUrl(context, info.url);
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

                File imgFile = ThemeSyncManager.getInstance().getFileByUrl(context, info.img_vUrl);
                info.imgPath = imgFile != null ? imgFile.getAbsolutePath() : "";

                if (info.url.endsWith("mp4") || info.url.endsWith("MP4")) {
                    info.format = WallpaperFormat.FORMAT_VIDEO;
                } else if (info.url.endsWith("gif") || info.url.endsWith("GIF")) {
                    info.format = WallpaperFormat.FORMAT_GIF;
                } else {
                    info.format = WallpaperFormat.FORMAT_IMAGE;
                }
                dot.add(info);
            }
        }

        return dot;
    }
}
