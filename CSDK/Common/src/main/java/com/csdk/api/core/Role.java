package com.csdk.api.core;

import com.csdk.api.bean.Session;

/**
 * Create LuckMerlin
 * Date 20:03 2021/1/20
 * TODO
 */
public final class Role implements Session {
    private final String mUserId;
    private final String mRoleId;

    public Role(String userId,String roleId){
        mUserId=userId;
        mRoleId=roleId;
    }

    public String getRoleId() {
        return mRoleId;
    }

    public String getUserId() {
        return mUserId;
    }

    @Override
    public String getLogo() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }
}
