package com.md.wallpaper;

import android.content.Context;

import com.md.wallpaper.manager.WallpaperUtil;

public class WallpaperSet {
    public static void init(Context context) {
       WallpaperUtil.getInstance().init(context);
        WallpaperPreferenceHelper.init(context);
    }
}