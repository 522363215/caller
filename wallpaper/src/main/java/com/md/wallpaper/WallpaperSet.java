package com.md.wallpaper;

import android.content.Context;

public class WallpaperSet {
    public static void init(Context context) {
       WallpaperUtil.getInstance().init(context);
        WallpaperPreferenceHelper.init(context);
    }
}