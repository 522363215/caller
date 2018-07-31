package com.md.callring;

import java.io.Serializable;

public class LocalSong implements Serializable{

    private String name;
    private int music;
    private int drawableRes;

    public LocalSong(String name, int music, int drawableRes) {
        this.name = name;
        this.music = music;
        this.drawableRes = drawableRes;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
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
