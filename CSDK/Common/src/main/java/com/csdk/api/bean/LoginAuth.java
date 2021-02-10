package com.csdk.api.bean;

import com.csdk.data.AuthenticationRequest;
import java.util.List;

/**
 * Create LuckMerlin
 * Date 10:51 2020/8/20
 * TODO
 */
public final class LoginAuth {
    private String uid;
    private String key;
    private Platform plat;//Platform ID
    private String serverId;//Server ID
    private String serverName;//Server Name
    private String roleId;//Role ID
    private String roleName;//Role Name
    private String token;//Token
    private String nickName;
    private List<Integer> accepts;
    private String productId;
    private String productKey;

    private LoginAuth(){

    }

    public String getUid() {
        return uid;
    }

    public Integer getUidInteger(Integer def) {
        String userId=uid;
        if (null!=userId&&userId.length()>0){
            try {
                return Integer.parseInt(userId);
            }catch (Exception e){
                //Do nothing
            }
        }
        return def;
    }

    public String getKey() {
        return key;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getServerId() {
        return serverId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductKey() {
        return productKey;
    }

    public String getRoleName() {
        return roleName;
    }

    public static LoginAuth fromRequestAndAuth(Authentication authentication, ChatConfig chatConfig, AuthenticationRequest request){
        if (null!=authentication&&null!=request){
            LoginAuth auth=new LoginAuth();
            auth.uid=authentication.getUid();
            auth.key=authentication.getKey();
            auth.plat=request.getPlat();
            auth.roleId=request.getRoleId();
            auth.roleName=request.getRoleName();
            auth.serverName=request.getServerName();
            auth.serverId=request.getServerId();
            auth.productId=request.getProductId();
            auth.productKey=null!=chatConfig?chatConfig.getProductKey():null;
            return auth;
        }
        return null;
    }

}
