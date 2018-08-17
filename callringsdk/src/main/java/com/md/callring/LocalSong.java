package com.md.callring;

import java.io.Serializable;

public class LocalSong implements Serializable{

    private String name;
    private int music;
    private String drawableRes;

    public LocalSong(String name, int music, String drawableRes) {
        this.name = name;
        this.music = music;
        this.drawableRes = drawableRes;
    }

    public String getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(String drawableRes) {
        this.drawableRes = drawableRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMusic() {
        return music;
    }

    public void setMusic(int music) {
        this.music = music;
    }
}
