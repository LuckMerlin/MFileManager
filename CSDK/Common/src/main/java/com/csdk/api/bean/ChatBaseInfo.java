package com.csdk.api.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Create LuckMerlin
 * Date 15:24 2020/9/28
 * TODO
 */
public final class ChatBaseInfo {
    private Platform mPlat;
    private String mServerId;
    private String mServerName;
    private String mRoleId;
    private String mRoleName;
    private String mRoleLevel;
    private String mVipLevel;
    private String mGender;
    private String mAvatarUrl;
    private int mFriendshipVersion=1;
    private String thirdType;
    private String thirdId;
    private String thirdName;
    private String thirdAvatar;
    private Map<String, Object> mExtra;

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public String getGender() {
        return mGender;
    }

    public Platform getPlat() {
        return mPlat;
    }

    public String getRoleId() {
        return mRoleId;
    }

    public void setThirdId(String thirdId) {
        this.thirdId = thirdId;
    }

    public void setThirdAvatar(String thirdAvatar) {
        this.thirdAvatar = thirdAvatar;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public void setThirdType(String thirdType) {
        this.thirdType = thirdType;
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

    public String getRoleLevel() {
        return mRoleLevel;
    }

    public String getRoleName() {
        return mRoleName;
    }

    public String getServerId() {
        return mServerId;
    }

    public String getServerName() {
        return mServerName;
    }

    public ChatBaseInfo setFriendshipVersion(int friendshipVersion) {
        this.mFriendshipVersion = friendshipVersion;
        return this;
    }

    public String getVipLavel() {
        return mVipLevel;
    }

    /**
     * @deprecated
     */
    public ChatBaseInfo setServerName(String serverName) {
        this.mServerName = serverName;
        return this;
    }

    public ChatBaseInfo setServerId(String serverId) {
        this.mServerId = serverId;
        return this;
    }

    public ChatBaseInfo setRoleName(String roleName) {
        this.mRoleName = roleName;
        return this;
    }

    public ChatBaseInfo setRoleId(String mRoleId) {
        this.mRoleId = mRoleId;
        return this;
    }

    public ChatBaseInfo setAvatarUrl(String avatarUrl) {
        this.mAvatarUrl = avatarUrl;
        return this;
    }

    public ChatBaseInfo setGender(boolean female) {
        return setGender(Integer.toString(female?Gender.FEMALE: com.csdk.api.bean.Gender.MAN));
    }

    public ChatBaseInfo setGender(String gender) {
        this.mGender = gender;
        return this;
    }

    public ChatBaseInfo setPlat(Platform plat) {
        this.mPlat = plat;
        return this;
    }

    public ChatBaseInfo setRoleLevel(String roleLevel) {
        this.mRoleLevel = roleLevel;
        return this;
    }

    public ChatBaseInfo setVipLevel(String vipLevel) {
        this.mVipLevel = vipLevel;
        return this;
    }

    public int getFriendshipVersion() {
        return mFriendshipVersion;
    }

    public ChatBaseInfo putExtra(String key, Object value){
        if (null!=key&&key.length()>0){
            Map<String, Object> extra=mExtra;
            if (null==value){
                if (null!=extra){
                    extra.remove(key);
                }
                return this;
            }
            extra=null!=extra?extra:(mExtra=new HashMap<>());
            extra.put(key, value);
        }
        return this;
    }

    public ChatBaseInfo setExtra(Map<String, Object> extra) {
        this.mExtra = extra;
        return this;
    }

    public Map<String, ?> getExtra() {
        return mExtra;
    }
}
