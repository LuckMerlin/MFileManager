package com.csdk.api.common;

import android.content.Context;
import android.view.View;

import com.csdk.api.bean.CSDKAction;
import com.csdk.api.bean.ChatBaseInfo;
import com.csdk.api.bean.ChatConfig;
import com.csdk.api.core.Listener;
import com.csdk.api.core.OnSendFinish;

/**
 * Create LuckMerlin
 * Date 18:27 2021/1/13
 * TODO
 */
public interface CommonApi  {
    /**
     *
     */
    String getVersion();

    /**
     *
     */
    int initial(Context context, ChatConfig config);
    /**
     *
     */
    int setChatBaseInfo(ChatBaseInfo baseInfo);
    /**
     *
     */
    int setOnCSDKListener(Listener listener) ;
    /**
     *
     */
     int intoGroupWithArgs(String args, OnSendFinish callback);

    /**
     *
     */
     int quitGroupWithArgs(String args,OnSendFinish callback);
    /**
     *
     */
    int notifyActionChange(CSDKAction action, String args,OnSendFinish callback);
    /**
     *
     */
    int openChatUi(boolean outline);
    /**
     *
     */
    int closeChatUi() ;

    /**
     *
     */
    int setContentView(Object contentView);
    /**
     *
     */
    Api getApi();
}
