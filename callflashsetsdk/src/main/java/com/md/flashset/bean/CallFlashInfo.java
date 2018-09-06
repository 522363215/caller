package com.md.flashset.bean;

import android.text.TextUtils;

import com.md.flashset.download.DownloadState;

import java.io.Serializable;

/**
 * Created by Zhq on 2018/1/8.
 */
public class CallFlashInfo implements Serializable {

    public String id;
    /**
     * call falsh 的格式 ：CallFlashFormat.FORMAT_GIF,CallFlashFormat.FORMAT_VIDEO,CallFlashFormat.FORMAT_CUSTOM_ANIM
     */
    public int format;
    /**
     * 是否为在线callflash
     */
    public boolean isOnlionCallFlash;
    public String title;
    public String logoUrl;
    public String logoPressUrl;
    public String img_vUrl;//竖向缩略图
    public String img_hUrl; //横向缩略图
    public String url;//素材原始图
    public String score;//评分
    public String intro;//素材介绍
    public int commentCount;//评论数
    public int downloadCount;//下载数量
    public int collection;
    public int flashClassification; // 类型分类: normal, classic, recommend; 相关实体类: CallFlashClassification;

    public long likeCount;//点赞数
    public boolean isLike;//是否点赞

    private String savePath;
    //针对不需要下载的callflash
    public int logoResId;
    public int logoPressResId;
    //随着下载更新的属性
    public String path;//原始素材路径
    public String imgPath;//背景图路径
    public String logoPath;//logo路径
    public String logoPressPath;//logoPress路径
    public int imgResId;
    public int img_hResId;
    public int progress;
    public int downloadState;
    public boolean isAutoDownload = true;//是否为自动下载,默认为自动下载
    public int downloadFailedCount;//下载失败的次数
    public boolean isDownloadSuccess;

    /**
     * 已经下载过，下载失败或成功都算下载过
     */
    public boolean isDownloaded;

    public int viewId;
    public int position;

    public int flashType = -100;
    public boolean isHaveSound; // flash素材是否有声音;
    public String thumbnail_imgUrl;//列表中背景图片的缩略图url

    public long downloadSuccessTime;
    public long setToCallFlashTime;//设置为来电秀的时间
    public long collectTime;//收藏的时间
    public boolean isLock;

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CallFlashInfo) {
            CallFlashInfo info = (CallFlashInfo) obj;
            if (this.id != null && info.id != null) {
                return !TextUtils.isEmpty(this.id) && this.id.equals(info.id);
            }
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "CallFlashInfo{" +
                "id='" + id + '\'' +
                ", format=" + format +
                ", title='" + title + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", logoPressUrl='" + logoPressUrl + '\'' +
                ", img_vUrl='" + img_vUrl + '\'' +
                ", img_hUrl='" + img_hUrl + '\'' +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", logoPath='" + logoPath + '\'' +
                ", logoPressPath='" + logoPressPath + '\'' +
                ", flashType=" + flashType +
                '}';
    }
}
