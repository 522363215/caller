package com.md.wallpaper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by admin on 2018/4/12.
 */

public class SharedUtils {

    public static SharedUtils mSharedUtils;
    Context mContext;


    public void saveNews(String key, String set) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.putString(key, set);
        editor.apply();
    }

    public String readNews(String key) {
        return mContext.getSharedPreferences("info", Context.MODE_PRIVATE).getString(key, "");
    }

    public void saveFrist(String key, boolean set) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, set);
        editor.apply();
    }

    public boolean readfrist(String key) {
        return mContext.getSharedPreferences("info", Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public void saveAuthority(String key, Set set) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.putStringSet(key, set);
        editor.apply();
    }

    public Set<String> readAuthority(String key) {
        return mContext.getSharedPreferences("info", Context.MODE_PRIVATE).getStringSet(key, null);
    }

    public void saveUserId(String key, int set) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.putInt(key, set);
        editor.apply();
    }

    public int readUserId(String key) {
        return this.mContext.getSharedPreferences("info", Context.MODE_PRIVATE).getInt(key, 0);
    }

    private SharedUtils(Context context) {
        this.mContext = context;
    }

    //单例,懒汉式
    public static SharedUtils getSharedUtils(Context context) {
        if (mSharedUtils == null) {
            synchronized (SharedUtils.class) {
                if (mSharedUtils == null) {
                    mSharedUtils = new SharedUtils(context);
                }
            }
        }
        return mSharedUtils;
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.apply();
    }

    public void savePlace(String key, float set) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("info", Context.MODE_PRIVATE).edit();
        editor.putFloat(key, set);
        editor.apply();
    }

    public double readPlace(String key) {
        return mContext.getSharedPreferences("info", Context.MODE_PRIVATE).getFloat(key, 0);
    }

}
