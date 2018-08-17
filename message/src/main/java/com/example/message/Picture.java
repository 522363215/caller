package com.example.message;

import java.io.Serializable;

public class Picture implements Serializable{

    private String Id;
    private String name;
    private String drawableRes;
    private String path;
    private String thumbnail;

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


    public Picture(String id, String name, String drawableRes, String thumbnail) {
        Id = id;
        this.name = name;
        this.drawableRes = drawableRes;
        this.thumbnail = thumbnail;
    }

    public Picture(String id, String name, String drawableRes, String path, String thumbnail) {
        Id = id;
        this.name = name;
        this.drawableRes = drawableRes;
        this.path = path;
        this.thumbnail = thumbnail;
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
