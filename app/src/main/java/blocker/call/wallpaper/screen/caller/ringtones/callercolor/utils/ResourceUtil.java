package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.graphics.drawable.Drawable;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;


/**
 * Created by luowp on 2016/7/19.
 */
public class ResourceUtil {
    //private static Typeface FONT_PRODUCTSCANS_REGULAR = Typeface.createFromAsset(ApplicationEx.getInstance().getAssets(), "fonts/ProductSans-Regular.ttf");

    public static String getString(int resId) {
        return ApplicationEx.getInstance().getString(resId);
    }

    public static int getColor(int colorId) {
        return ApplicationEx.getInstance().getResources().getColor(colorId);
    }

    public static Drawable getDrawable(int drawableId) {
        return ApplicationEx.getInstance().getResources().getDrawable(drawableId);
    }

    public static int getDefaultIcon() {
        return android.R.drawable.sym_def_app_icon;
    }

//    public static Typeface getFontProductScansRegular() {
//        return FONT_PRODUCTSCANS_REGULAR;
//    }
}
