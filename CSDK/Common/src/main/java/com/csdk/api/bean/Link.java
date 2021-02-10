package com.csdk.api.bean;

import android.text.Html;

import com.csdk.api.core.ApiKeys;
import com.csdk.server.data.Json;

/**
 * Create LuckMerlin
 * Date 17:48 2020/8/28
 * TODO
 */
public final class Link {
    public final static int INVITE_LINK=1001;
    public final static int WEB_OPEN_LINK=1002;
    private final Json mLinkJson;

    public Link(Json linkJson){
        mLinkJson=linkJson;
    }

    public String optString(String key,String def){
        Json linkJson=mLinkJson;
        return null!=linkJson&&null!=key?linkJson.optString(key, def):def;
    }

    public CharSequence getContentHtml(){
        String linkContent=getContent();
        CharSequence linkUserName=getUserName();
        //Check if need append link from name
        if (null!=linkContent&&linkContent.length()>0&&null!=linkUserName&&linkUserName.length()>0){
            linkUserName="<font color='#5a56ef'>"+linkUserName+" </font>";
            linkContent=""+linkUserName+linkContent;
        }
        return null!=linkContent&&linkContent.length()>0? Html.fromHtml(linkContent):null;
    }

    public String getContent() {
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.optString(ApiKeys.DATA, null):null;
    }

    public String getGroupType() {
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.optString(ApiKeys.GROUP_TYPE, null):null;
    }

    public String getData() {
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.optString(ApiKeys.DATA, null):null;
    }

    public String getGroupId() {
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.optString(ApiKeys.GROUP_ID, null):null;
    }

    public String getCustomId() {
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.optString(ApiKeys.CUSTOM_ID, null):null;
    }

    public String getFromRoleId() {
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.optString(ApiKeys.FROM_ROLE_ID, null):null;
    }

    public CharSequence getUserName() {
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.optString(ApiKeys.USER_NAME, null):null;
    }

    public CharSequence getType() {
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.optString(ApiKeys.TYPE, null):null;
    }

    public CharSequence getAvatarUrl() {
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.optString(ApiKeys.AVATAR_URL, null):null;
    }

    public Link setFromRoleId(String roleId){
        Json linkJson=mLinkJson;
        if (null!=linkJson){
            if (null==roleId){
                linkJson.remove(ApiKeys.FROM_ROLE_ID);
            }else{
                linkJson.putSafe(ApiKeys.FROM_ROLE_ID,roleId);
            }
        }
        return this;
    }

    public CharSequence getTitle() {
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.optString(ApiKeys.TITLE, null):null;
    }

    public String getLinkJsonString(){
        Json linkJson=mLinkJson;
        return null!=linkJson?linkJson.toString():null;
    }

}
