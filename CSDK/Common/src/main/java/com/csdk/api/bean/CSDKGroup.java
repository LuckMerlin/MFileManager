package com.csdk.api.bean;

/**
 * Create LuckMerlin
 * Date 14:08 2020/12/2
 * TODO
 * @deprecated
 */
public final class CSDKGroup implements CSDKSession {
    private String mId;
    private String mTitle;
    private String mType;

    public CSDKGroup(String type,String id){
        this.mType=type;
        this.mId=id;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getId() {
        return mId;
    }

    public String getType() {
        return mType;
    }

    @Override
    public String getLogo() {
        return null;
    }
}
