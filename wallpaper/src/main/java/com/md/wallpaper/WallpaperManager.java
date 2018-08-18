package com.md.wallpaper;

import java.util.ArrayList;
import java.util.List;

public class WallpaperManager {

    public static void saveDownloadedWallPaper(Wallpaper wallpaper){
        if (wallpaper == null) return;
        List<Wallpaper> downloadedWallpapers = WallpaperPreferenceHelper.getDataList(WallpaperPreferenceHelper.DOWNLOADED_WALLPAPERS, Wallpaper[].class);
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
}
