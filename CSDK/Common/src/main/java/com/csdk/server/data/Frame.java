package com.csdk.server.data;

import com.csdk.api.bean.Authentication;
import com.csdk.api.bean.Message;
import com.csdk.api.bean.Receipt;
import com.csdk.api.bean.Reply;
import com.csdk.api.core.Code;
import com.csdk.api.core.Label;
import com.csdk.api.core.Operation;
import com.csdk.debug.Logger;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2020/7/30.
 */

public final class Frame implements Operation {
    private final String mCharset;
    private final int mOperation;
    private final long mPacketLen;
    private final long mHeadLength;
    private final byte[] mBodyBytes;
    private final String mUserId;

    public Frame(String charset, String userId, int operation, long packetLen, long headLength, byte[] bodyBytes){
        mUserId=userId;
        mCharset=charset;
        mOperation=operation;
        mPacketLen=packetLen;
        mHeadLength=headLength;
        mBodyBytes=bodyBytes;
    }

    public final String getBodyText(){
        return getBodyText(getCharset(),null);
    }

    public final String getBodyText(String charset,String def){
        byte[] bytes=mBodyBytes;
        if (null!=bytes&&bytes.length>0){
            try {
                return null!=charset&&charset.length()>0?new String(bytes,charset):new String(bytes);
            } catch (UnsupportedEncodingException e) {
                Logger.E("Exception get frame body text "+e+" "+charset);
                e.printStackTrace();
            }
        }
        return def;
    }

    public final Message getBodyMessage(){
        return getBodyMessage(null);
    }

    public final  Message getBodyMessage(String charset){
        if (mOperation==MESSAGE_RECEIVE||mOperation==SYSTEM_MESSAGE){
            String bodyText=getBodyText(charset,null);
            if (null!=bodyText&&bodyText.length()>0&&isJson(bodyText)){
                Message message= new Gson().fromJson(bodyText, Message.class);
                if (null!=message){
                    message.setOperation(mOperation);
                    String receiveUid=message.getLoginUid();
                    if (null==receiveUid||receiveUid.length()<=0) {
                        message.setLoginUid(mUserId);
                    }
                }
                return message;
            }
        }
        return null;
    }

    public final boolean isJson(String text){
        return null!=text&&text.length()>0&&text.startsWith("{")&&text.endsWith("}");
    }

    public final Object getBody(){
        Object object=getBodyReply();
        return null!=object?object:this.getBodyMessage();
    }

    public final Reply getBodyReply(){
        return getBodyReply(null);
    }

    public String getUserId() {
        return mUserId;
    }

    public boolean isLoginChanged(){
        return getOperation()==Operation.AUTH_REPLY;
    }

    public final Reply getBodyReply(String charset){
        return getBodyReply(charset, null);
    }

    public final <T> Reply<T> getBodyReply(String charset, Class<T> targetCls){
        final String bodyText=getBodyText(charset, null);
        if (null==bodyText||bodyText.length()<=0||!bodyText.startsWith("{")||!bodyText.endsWith("}")){
            return null;
        }
        Class cls=null;
        if (null==cls){
            final long operation=mOperation;
            if (operation==AUTH_REPLY){
                cls= Authentication.class;
            }else if (operation==SEND_RECEIPT){
                cls= Receipt.class;
            }else if (operation==ADD_FRIEND_REPLY||operation==INVITE_CREATE_GROUP_REPLY|| operation==ADD_FRIEND_AGREE_REPLY||
            operation==AGREE_INVITE_CREATE_GROUP_REPLY||operation==QUIT_GROUP_REPLY||operation==BLOCK_FRIEND_REPLY||operation==AGREE_JOIN_GROUP_APPLY_REPLY
            ||operation==UNBLOCK_FRIEND_REPLY||operation==JOIN_ROOM_REPLY||operation==CREATE_GROUP_REPLY||operation==INVITE_JOIN_TEAM_REPLY||operation==INVITE_JOIN_TEAM_AGREE_REPLY
                    ||operation==DISMISS_GROUP_REPLY||operation==Operation.JOIN_GROUP_REPLY){
                cls=null;
            }
            cls=null!=cls&&(null==targetCls||cls.getName().equals(targetCls.getName()))?cls:null;
        }
        try {
            Json json=Json.create(bodyText);
            if (null!=json){
                T instance=null!=cls?(T)new ObjectCreator().generate(cls,json.opt(Label.LABEL_DATA)):null;
                return new Reply<T>(json.optInt(Label.LABEL_CODE, Code.CODE_FAIL),json.optString(Label.LABEL_MSG,null), instance);
            }
        } catch (Exception e) {
            Logger.E("Exception get body reply."+e);
            e.printStackTrace();
        }
        return null;
    }

    public int getBodyLength(){
        byte[] bodyBytes=mBodyBytes;
        return null!=bodyBytes?bodyBytes.length:-1;
    }

    public String getCharset() {
        return mCharset;
    }

    public final int getOperation() {
        return mOperation;
    }

    @Override
    public String toString() {
        return ""+super.toString()+" "+mOperation+" "+mPacketLen+" "+mHeadLength+" "+getBodyText();
    }
}
