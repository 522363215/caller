package com.md.wallpaper;

import java.io.Serializable;

public class Picture implements Serializable {

    private String Id;
    private String name;
    private String drawableRes;
    private String path;
    private String thumbnail;
    private int type;

    public Picture(String id, String name, String drawableRes, String thumbnail, int type) {
        Id = id;
        this.name = name;
        this.drawableRes = drawableRes;
        this.thumbnail = thumbnail;
        this.type = type;
    }

    public Picture(String id, String name, String drawableRes, String path, String thumbnail, int type) {
        Id = id;
        this.name = name;
        this.drawableRes = drawableRes;
        this.path = path;
        this.thumbnail = thumbnail;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(String drawableRes) {
        this.drawableRes = drawableRes;
    }
}
