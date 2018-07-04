package com.md.serverflash.beans;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by ChenR on 2018/5/7.
 */

public class Theme implements Serializable{

    public static final int DOWNLOADED = 101;
    public static final int UNDOWNLOADED = 102;
    public static final int DOWNLOADED_FAILED = 103;

    private static final long serialVersionUID = 1L;

    /**
     * id : 4050
     * cid : 33
     * logo : https://djwtigu7b03af.cloudfront.net/caller_show_manager/201802/01103709380.png
     * logo_pressed : https://djwtigu7b03af.cloudfront.net/caller_show_manager/201802/01103709102.png
     * img_v : https://djwtigu7b03af.cloudfront.net/caller_show_manager/201802/01103709365.jpg
     * img_h :
     * url : https://djwtigu7b03af.cloudfront.net/caller_show_manager/201802/01103709442.mp4
     * score : 0.00
     * comment : 0
     * download : 0
     * collection : 0
     * type : 103
     * bg_color :
     * num_of_likes : 2321
     * is_sound : 0
     * is_lock : 0
     * difficult : 0
     * title : frozen
     * intro : frozen
     * is_test : 0
     * show_type : 2
     * width : 0
     * height : 0
     * author :
     */

    private long id;
    private int cid;
    private String logo;
    private String logo_pressed;
    private String img_v;
    private String img_h;
    private String url;
    private String score;
    private long comment;
    private long download;
    private long collection;
    private int type; // 资源文件类型: 普通, 视频, 3d, ...
    private String bg_color;
    private long num_of_likes;
    private String is_sound;
    private String is_lock;
    private long difficult;
    private String title;
    private String intro;
    private String is_test;
    private int show_type;
    private long width;
    private long height;
    private String author;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo_pressed() {
        return logo_pressed;
    }

    public void setLogo_pressed(String logo_pressed) {
        this.logo_pressed = logo_pressed;
    }

    public String getImg_v() {
        return img_v;
    }

    public void setImg_v(String img_v) {
        this.img_v = img_v;
    }

    public String getImg_h() {
        return img_h;
    }

    public void setImg_h(String img_h) {
        this.img_h = img_h;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public long getComment() {
        return comment;
    }

    public void setComment(long comment) {
        this.comment = comment;
    }

    public long getDownload() {
        return download;
    }

    public void setDownload(long download) {
        this.download = download;
    }

    public long getCollection() {
        return collection;
    }

    public void setCollection(long collection) {
        this.collection = collection;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBg_color() {
        return bg_color;
    }

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }

    public long getNum_of_likes() {
        return num_of_likes;
    }

    public void setNum_of_likes(long num_of_likes) {
        this.num_of_likes = num_of_likes;
    }

    public String getIs_sound() {
        return is_sound;
    }

    public void setIs_sound(String is_sound) {
        this.is_sound = is_sound;
    }

    public String getIs_lock() {
        return is_lock;
    }

    public void setIs_lock(String is_lock) {
        this.is_lock = is_lock;
    }

    public long getDifficult() {
        return difficult;
    }

    public void setDifficult(long difficult) {
        this.difficult = difficult;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getIs_test() {
        return is_test;
    }

    public void setIs_test(String is_test) {
        this.is_test = is_test;
    }

    public int getShow_type() {
        return show_type;
    }

    public void setShow_type(int show_type) {
        this.show_type = show_type;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Theme) {
            Theme theme = (Theme) obj;
            return this.getId() == theme.getId();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + cid;
        result = 31 * result + logo.hashCode();
        result = 31 * result + logo_pressed.hashCode();
        result = 31 * result + img_v.hashCode();
        result = 31 * result + img_h.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + score.hashCode();
        result = 31 * result + (int) (comment ^ (comment >>> 32));
        result = 31 * result + (int) (download ^ (download >>> 32));
        result = 31 * result + (int) (collection ^ (collection >>> 32));
        result = 31 * result + type;
        result = 31 * result + bg_color.hashCode();
        result = 31 * result + (int) (num_of_likes ^ (num_of_likes >>> 32));
        result = 31 * result + is_sound.hashCode();
        result = 31 * result + is_lock.hashCode();
        result = 31 * result + (int) (difficult ^ (difficult >>> 32));
        result = 31 * result + title.hashCode();
        result = 31 * result + intro.hashCode();
        result = 31 * result + is_test.hashCode();
        result = 31 * result + show_type;
        result = 31 * result + (int) (width ^ (width >>> 32));
        result = 31 * result + (int) (height ^ (height >>> 32));
        result = 31 * result + author.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Theme{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", cid='" + cid + '\'' +
                ", url='" + url + '\'' +
                ", logo='" + logo + '\'' +
                ", logo_pressed='" + logo_pressed + '\'' +
                ", is_sound='" + is_sound + '\'' +
                ", is_lock='" + is_lock + '\'' +
                ", is_test='" + is_test + '\'' +
                ", show_type='" + show_type + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
