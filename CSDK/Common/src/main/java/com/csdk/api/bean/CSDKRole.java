package com.csdk.api.bean;
/**
 * Create LuckMerlin
 * Date 14:07 2020/12/2
 * TODO
 */
public final class CSDKRole implements CSDKSession{
    private String mRoleId;
    private String mRoleName;

    public CSDKRole(String roleId){
        this(roleId,null);
    }


    public CSDKRole(String roleId, String roleName){
        mRoleId=roleId;
        mRoleName=roleName;
    }

    public CSDKRole setRoleId(String  roleId) {
        this.mRoleId = roleId;
        return this;
    }

    public CSDKRole setRoleName(String roleName) {
        this.mRoleName = roleName;
        return this;
    }

    @Override
    public String getLogo() {
        return null;
    }

    @Override
    public final String getTitle() {
        return mRoleName;
    }

    public String getRoleId() {
        return mRoleId;
    }

    public String getRoleName() {
        return mRoleName;
    }

}
