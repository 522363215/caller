package com.md.wallpaper.manager;

import android.content.Context;

import com.md.serverflash.ThemeSyncManager;
import com.md.serverflash.beans.Theme;
import com.md.wallpaper.WallpaperPreferenceHelper;
import com.md.wallpaper.bean.DownloadState;
import com.md.wallpaper.bean.WallpaperFormat;
import com.md.wallpaper.bean.WallpaperInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WallpaperManager {

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
