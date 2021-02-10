package com.csdk.api.bean;

public final class Third {
    public final static String TYPE_WE_CHAT="WeChat";
    public final static String TYPE_QQ="QQ";
    private String uid;
    private String createTime;
    private String thirdType;
    private String thirdId;
    private String thirdName;
    private String thirdAvatar;

    public String getCreateTime() {
        return createTime;
    }

    public String getThirdAvatar() {
        return thirdAvatar;
    }

    public String getThirdId() {
        return thirdId;
    }

    public String getThirdName() {
        return thirdName;
    }

    public String getThirdType() {
        return thirdType;
    }

    public String getUid() {
        return uid;
    }

    public void setThirdType(String thirdType) {
        this.thirdType = thirdType;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }
}
