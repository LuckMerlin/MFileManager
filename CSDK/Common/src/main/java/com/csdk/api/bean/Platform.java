package com.csdk.api.bean;

/**
 * Create LuckMerlin
 * Date 17:02 2020/11/28
 * TODO
 */
public enum  Platform {
    IOS(0),
    ANDROID(1),
    WEB(2);

    private int  mValue=0;

    private Platform(int value) {
        mValue=value;
    }

    public int getValue() {
        return mValue;
    }
}
