package com.csdk.api.core;

public final class RoleStatus {
    private final String mTitle;
    private final int mColor;

    public RoleStatus(String title,int color){
        mTitle=title;
        mColor=color;
    }

    public int getColor() {
        return mColor;
    }

    public String getTitle() {
        return mTitle;
    }
}
