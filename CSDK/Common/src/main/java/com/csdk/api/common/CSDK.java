package com.csdk.api.common;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.csdk.api.bean.CSDKAction;
import com.csdk.api.bean.ChatBaseInfo;
import com.csdk.api.bean.ChatConfig;
import com.csdk.api.bean.FriendRelation;
import com.csdk.api.core.Code;
import com.csdk.api.core.OnSendFinish;
import com.csdk.debug.Logger;
import com.csdk.server.CommonSDK;
import com.csdk.server.Configure;

/**
 * Create LuckMerlin
 * Date 18:24 2021/1/13
 * TODO
 */
public final class CSDK implements Code {
    private final CommonApi mSocket;

    private CSDK() {
        CommonApi sdkui=mSocket=new CommonSDK();
        Configure configure=Configure.getInstance();
        String buildVersion=null!=configure?configure.getBuildVersion():null;
        Logger.D("CSDK version "+(null!=sdkui?sdkui.getVersion():"")+" "+buildVersion);
    }

    public static CSDK getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final CSDK sInstance = new CSDK();
    }

    public int init(Context context, ChatConfig config) {
        try {
            CommonApi socket=mSocket;
            if (null==socket){
                Logger.W("Can't init csdk while api NULL.");
                return CODE_NONE_INITIAL;
            }
            config.setDispatchFriendRelation(FriendRelation.ALL);
            return socket.initial(context, config);
        }catch (Exception e){
            Logger.E("Exception while init csdk."+e);
            return CODE_EXCEPTION;
        }
    }

    public int setChatBaseInfo(ChatBaseInfo chatBaseInfo) {
        try{
            CommonApi socket=mSocket;
            if (null==socket){
                Logger.W("Can't set chat base info while api NULL.");
                return CODE_NONE_INITIAL;
            }
            return socket.setChatBaseInfo(chatBaseInfo);
        }catch (Exception e){
            Logger.E("Exception while set chat base info."+e);
            return CODE_EXCEPTION;
        }
    }

    public int setOnCSDKListener(OnCSDKListener listener){
        try{
            CommonApi socket=mSocket;
            if (null==socket){
                Logger.W("Can't set csdk listener while api NULL.");
                return CODE_NONE_INITIAL;
            }
            return socket.setOnCSDKListener(listener);
        }catch (Exception e){
            Logger.E("Exception while set on csdk listener."+e);
            return CODE_EXCEPTION;
        }
    }

    public int setContentView(Object contentView, FrameLayout.LayoutParams params){
        try{
            CommonApi socket=mSocket;
            if (null==socket){
                Logger.W("Can't set content view while api NULL.");
                return CODE_NONE_INITIAL;
            }
            return socket.setContentView(contentView,params);
        }catch (Exception e){
            Logger.E("Exception while set content view."+e);
            return CODE_EXCEPTION;
        }
    }

    public int intoGroupWithArgs(String args,OnSendFinish callback){
        try{
            CommonApi socket=mSocket;
            if (null==socket){
                Logger.W("Can't into group with args while api NULL.");
                return CODE_NONE_INITIAL;
            }
            return socket.intoGroupWithArgs(args,callback);
        }catch (Exception e){
            Logger.E("Exception into group with args."+e);
            return CODE_EXCEPTION;
        }
    }

    public int quitGroupWithArgs(String args, OnSendFinish callback){
        try{
            CommonApi socket=mSocket;
            if (null==socket){
                Logger.W("Can't quit group with args while api NULL.");
                return CODE_NONE_INITIAL;
            }
            return socket.quitGroupWithArgs(args,callback);
        }catch (Exception e){
            Logger.E("Exception quit group with args."+e);
            return CODE_EXCEPTION;
        }
    }

    public int notifyActionChange(CSDKAction action,String args,OnSendFinish callback){
        try{
            CommonApi socket=mSocket;
            if (null==socket){
                Logger.W("Can't notify action change while api NULL.");
                return CODE_NONE_INITIAL;
            }
            return socket.notifyActionChange(action,args,callback);
        }catch (Exception e){
            Logger.E("Exception notify action change."+e);
            return CODE_EXCEPTION;
        }
    }
    /////////////////////////////////
    public int openChatUi(boolean outline){
        try{
            CommonApi socket=mSocket;
            return null==socket?CODE_NONE_INITIAL:socket.openChatUi(outline);
        }catch (Exception e){
            return CODE_EXCEPTION;
        }
    }

    public int closeChatUi(){
        try{
            CommonApi socket=mSocket;
            return null==socket?CODE_NONE_INITIAL:socket.closeChatUi();
        }catch (Exception e){
            return CODE_EXCEPTION;
        }
    }

}
