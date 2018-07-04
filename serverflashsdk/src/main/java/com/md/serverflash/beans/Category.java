package com.md.serverflash.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ChenR on 2018/5/7.
 */

public class Category implements Serializable{

    private static final long serialVersionUID = 2L;

    /**
     * px_id : 144
     * condition_id : 38
     * id : 47
     * parent_id : 0
     * icon_url :
     * is_test : 0
     * list : []
     * title : Anime
     * intro :
     */

    private String px_id;
    private String condition_id;
    private String id;
    private String parent_id;
    private String icon_url;
    private String is_test;
    private String title;
    private String intro;
    private List<Theme> list;

    public String getPx_id() {
        return px_id;
    }

    public void setPx_id(String px_id) {
        this.px_id = px_id;
    }

    public String getCondition_id() {
        return condition_id;
    }

    public void setCondition_id(String condition_id) {
        this.condition_id = condition_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getIs_test() {
        return is_test;
    }

    public void setIs_test(String is_test) {
        this.is_test = is_test;
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

    public List<Theme> getList() {
        return list;
    }

    public void setList(List<Theme> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", px_id='" + px_id + '\'' +
                ", condition_id='" + condition_id + '\'' +
                ", parent_id='" + parent_id + '\'' +
                ", is_test='" + is_test + '\'' +
                ", list=" + list +
                '}';
    }
}
