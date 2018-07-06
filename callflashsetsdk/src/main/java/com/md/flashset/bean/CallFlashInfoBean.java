package com.md.flashset.bean;

import java.util.List;

/**
 * Created by Zhq on 2018/1/9.
 */

public class CallFlashInfoBean {
    /**
     * status : {"code":0,"msg":""}
     * data : [{"id":"28","logo":"","logo_pressed":null,"img_v":"http://djwtigu7b03af.cloudfront.net/caller_show_manager/201801/09203630172.jpg","img_h":"","url":"http://djwtigu7b03af.cloudfront.net/caller_show_manager/201801/09203630508.mp4","score":"0.00","comment":"0","download":"0","collection":"0","title":"pig2","intro":"pig2"},{"id":"29","logo":"","logo_pressed":null,"img_v":"http://djwtigu7b03af.cloudfront.net/caller_show_manager/201801/09205011109.jpg","img_h":"","url":"http://djwtigu7b03af.cloudfront.net/caller_show_manager/201801/09205011610.mp4","score":"0.00","comment":"0","download":"0","collection":"0","title":"bear","intro":"bear"}]
     */
    private StatusBean status;
    private List<DataBean> data;

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class StatusBean {
        /**
         * code : 0
         * msg :
         */

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public static class DataBean {
        /**
         * id : 28
         * logo :
         * logo_pressed : null
         * img_v : http://djwtigu7b03af.cloudfront.net/caller_show_manager/201801/09203630172.jpg
         * img_h :
         * url : http://djwtigu7b03af.cloudfront.net/caller_show_manager/201801/09203630508.mp4
         * score : 0.00
         * comment : 0
         * download : 0
         * collection : 0
         * title : pig2
         * intro : pig2
         */

        private String id;
        private String logo;
        private Object logo_pressed;
        private String img_v;
        private String img_h;
        private String url;
        private String score;
        private String comment;
        private String download;
        private String collection;
        private String title;
        private String intro;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public Object getLogo_pressed() {
            return logo_pressed;
        }

        public void setLogo_pressed(Object logo_pressed) {
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

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

        public String getCollection() {
            return collection;
        }

        public void setCollection(String collection) {
            this.collection = collection;
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
    }
}
