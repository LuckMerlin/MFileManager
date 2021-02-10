package com.csdk.api.bean;
import com.csdk.api.core.ApiKeys;
import com.csdk.server.cache.Cacheable;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create LuckMerlin
 * Date 11:17 2020/11/3
 * TODO
 */
public final class AddFriendRequest extends HashMap<String, Object> implements Cacheable , Serializable {
    private  long mTime;
    private  String mFromUid;
    private  String mLoginUid;
    private  String mUserId;
    private  String mUserRoleId;
    private  String mRoleName;
    private  String mUserLevel;
    private boolean mUserFemale;
    private String mUserSlogan;
    private String mActionToken;
    private List<String> mUserTags;

    public AddFriendRequest(User user, Message message){
        if (null!=message){
            mTime=message.getTime();
            mFromUid=message.getFromUid();
            mLoginUid=message.getLoginUid();
            mActionToken=message.getActionToken();
            Map<String, Object> extra=message.getExtra();
            if (null!=extra&&extra.size()>0){
                putAll(extra);
            }
        }
        if (null!=user){
            mUserId=user.getId();
            mUserTags=user.getTagTexts();
            mUserLevel=user.getUserLevel();
            mUserFemale=user.isFemale();
            mRoleName=user.getRoleName();
            mUserSlogan=user.getSlogan();
            mUserRoleId=user.getRoleId();
        }
    }

    public Object getExtra(String key,String def){
        return null!=key?super.get(key):def;
    }

    public String getExtraText(String key,String def){
        Object valueObj=getExtra(key, def);
        return null!=valueObj?valueObj.toString():null;
    }

    public long getTime(){
        return mTime;
    }

    public String getFromName(){
       return getExtraText(ApiKeys.USER_NAME,null);
    }

    public String getFromUid(){
        return mFromUid;
    }

    public String getLoginUid(){
        return mLoginUid;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getUserLevel() {
        return mUserLevel;
    }

    public boolean isUserFemale() {
        return mUserFemale;
    }

    public String getRoleName() {
        return mRoleName;
    }

    public String getUserSlogan() {
        return mUserSlogan;
    }

    public String getUserRoleId() {
        return mUserRoleId;
    }

    public String getActionToken() {
        return mActionToken;
    }

    public List<String> getUserTags() {
        return mUserTags;
    }

    public boolean isFromCommonUser(AddFriendRequest request){
        if (null!=request){
            String userId=request.getUserId();
            String loginUid=request.getLoginUid();
            String currentUserId=getUserId();
            String currentLoginUid=getLoginUid();
            return null!=userId&&null!=loginUid&&null!=currentUserId&&null!=currentLoginUid&&
                    userId.equals(currentUserId)&&loginUid.equals(currentLoginUid);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (null!=o){
            if (super.equals(o)){
                return true;
            }else if (o instanceof AddFriendRequest){
                return ((AddFriendRequest)o).isFromCommonUser(this);
            }
        }
        return false;
    }


}
