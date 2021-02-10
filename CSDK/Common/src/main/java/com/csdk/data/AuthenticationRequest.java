package com.csdk.data;

import com.csdk.api.bean.ChatBaseInfo;
import com.csdk.api.bean.Platform;
import com.csdk.api.core.Operation;
import com.csdk.server.MessageObject;
import com.csdk.server.data.Json;
import com.csdk.api.core.Label;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create LuckMerlin
 * Date 17:09 2020/8/5
 * TODO
 */
public final class AuthenticationRequest implements MessageObject {
    private Platform plat;//Platform ID
    private String serverId;//Server ID
    private String serverName;//Server Name
    private String roleId;//Role ID
    private String roleName;//Role Name
    private String productId;
    private List<Integer> accepts;
    private int friendshipVersion;
    private String avatarUrl;
    private String gender;
    private String thirdType;
    private String thirdId;
    private String thirdName;
    private String thirdAvatar;
    private String sdkVersion;
    private String deviceNumber;
    private String roleLevel;
    private String vipLevel;
    private Map<String,?> extra;

    public AuthenticationRequest(String productId, String sdkVersion, String deviceNumber, ChatBaseInfo baseInfo, int ...accepts){
        this.productId=productId;
        this.sdkVersion=sdkVersion;
        this.deviceNumber=deviceNumber;
        this.plat=null!=baseInfo?baseInfo.getPlat():null;
        this.serverId=null!=baseInfo?baseInfo.getServerId():null;
        this.serverName=null!=baseInfo?baseInfo.getServerName():null;
        this.friendshipVersion=null!=baseInfo?baseInfo.getFriendshipVersion():this.friendshipVersion;
        this.roleId=null!=baseInfo?baseInfo.getRoleId():null;
        this.roleName=null!=baseInfo?baseInfo.getRoleName():null;
        this.roleLevel=null!=baseInfo?baseInfo.getRoleLevel():null;
        this.vipLevel=null!=baseInfo?baseInfo.getVipLavel():null;
        this.avatarUrl=null!=baseInfo?baseInfo.getAvatarUrl():null;
        this.gender=null!=baseInfo?baseInfo.getGender():null;
        this.thirdType=null!=baseInfo?baseInfo.getThirdType():null;
        this.thirdId=null!=baseInfo?baseInfo.getThirdId():null;
        this.thirdName=null!=baseInfo?baseInfo.getThirdName():null;
        this.thirdAvatar=null!=baseInfo?baseInfo.getThirdAvatar():null;
        this.extra=null!=baseInfo?baseInfo.getExtra():null;
        addAccepts(accepts);
    }

    public AuthenticationRequest setThirdName(String thirdName) {
        this.thirdName = thirdName;
        return this;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public AuthenticationRequest setThirdAvatar(String thirdAvatar) {
        this.thirdAvatar = thirdAvatar;
        return this;
    }

    public AuthenticationRequest setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public AuthenticationRequest setThirdId(String thirdId) {
        this.thirdId = thirdId;
        return this;
    }

    public AuthenticationRequest setThirdType(String thirdType) {
        this.thirdType = thirdType;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public AuthenticationRequest setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public AuthenticationRequest setFriendshipVersion(int friendshipVersion) {
        this.friendshipVersion = friendshipVersion;
        return this;
    }

    public final String getServerId() {
        return serverId;
    }

    public String getRoleId() {
        return roleId;
    }

    public Platform getPlat() {
        return plat;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getServerName() {
        return serverName;
    }

    public String getProductId() {
        return productId;
    }

    public int getFriendshipVersion() {
        return friendshipVersion;
    }

    public final boolean addAccepts(int ...acceptValues){
        int length=null!=acceptValues?acceptValues.length:-1;
        if (length>0){
            List<Integer> current=this.accepts;
            current=null!=current?current:(this.accepts=new ArrayList<Integer>(length));
            for (int child:acceptValues) {
                if (!current.contains(child)){
                    current.add(child);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Integer getOperation() {
        return Operation.AUTH;
    }

    @Override
    public String getMessageText() {
        Json messageExtra=new Json().put(this.extra).putSafe("thirdType",thirdType).putSafe("thirdId",thirdId).putSafe("thirdName",thirdName).
                putSafe("thirdAvatar",thirdAvatar).putSafe(Label.LABEL_ROLE_LEVEL,roleLevel);
        return new Json().putSafe("plat", (null!=plat?plat:Platform.ANDROID).getValue()).
                putSafe(Label.LABEL_SDK_VERSION, sdkVersion).putSafe(Label.LABEL_DEVICE_ID, deviceNumber).
                putSafe(Label.LABEL_FRIENDSHIP_VERSION, friendshipVersion).putSafe("serverId",serverId).putSafe("serverName", serverName).
                putSafe("roleId", roleId).putSafe(Label.LABEL_GENDER,gender).putSafe("avatarUrl",avatarUrl).
                putSafe("roleName",roleName).putSafe(Label.LABEL_EXTRA,messageExtra).
                putCollectionSafe("accepts",accepts).toString();
    }
}
