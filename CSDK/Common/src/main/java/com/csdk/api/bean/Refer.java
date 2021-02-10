package com.csdk.api.bean;

import com.csdk.server.data.Json;
import com.csdk.api.core.Label;

/**
 * Create LuckMerlin
 * Date 15:26 2021/1/11
 * TODO
 */
public final class Refer {
    private final String mContent;
    private final String mNickName;

    public Refer(Json json){
        mContent=null!=json?json.optString(Label.LABEL_CONTENT,null):null;
        mNickName=null!=json?json.optString(Label.LABEL_NICK_NAME,null):null;
    }

    public String getContent() {
        return mContent;
    }

    public String getNickName() {
        return mNickName;
    }

    public String getText() {
        String nickName=mNickName;
        String content=mContent;
        return (null!=nickName?nickName:"")+":"+(null!=content?content:"");
    }
}
