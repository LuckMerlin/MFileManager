package com.csdk.api.bean;

/**
 * Create LuckMerlin
 * Date 15:25 2020/9/24
 * TODO
 */
public final class Image {
    private final String mPath;
    private String createTime;
    private String id;
    private int mStatus;
    private String uid;
    private String url;
    private String descr;
    private String thumbnail;

    public Image(){
        this(null);
    }

    public Image(String path){
        mPath=path;
    }

    public void setCloudPath(String cloudPath) {
        this.url = cloudPath;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getPath() {
        return mPath;
    }

    public void setCloudId(String cloudId) {
        this.id = cloudId;
    }

    public String getCloudId() {
        return id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUid() {
        return uid;
    }

    public String getDescr() {
        return descr;
    }

    public String getCloudPath() {
        return url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public String toString() {
        return "Image{" +
                "mPath='" + mPath + '\'' +
                ", createTime='" + createTime + '\'' +
                ", id=" + id +
                ", mStatus=" + mStatus +
                ", uid='" + uid + '\'' +
                ", url='" + url + '\'' +
                ", descr='" + descr + '\'' +
                '}';
    }
}
