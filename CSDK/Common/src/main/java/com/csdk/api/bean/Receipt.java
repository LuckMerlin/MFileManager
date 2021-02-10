package com.csdk.api.bean;

import com.csdk.server.data.JsonObject;
import com.csdk.server.data.ObjectCreator;

import org.json.JSONObject;

/**
 * Create LuckMerlin
 * Date 18:01 2020/8/20
 * TODO
 */
public final class Receipt  implements JsonObject {
    private String msgId;
    private long time;
    private Message msg;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Message getMsg() {
        return msg;
    }

    @Override
    public boolean apply(Object object) {
        if (null!=object&&object instanceof JSONObject){
            JSONObject json=(JSONObject)object;
            msgId=json.optString("msgId", null);
            time=json.optLong("time", 0);
            msg=new ObjectCreator().generate(Message.class, json.opt("msg"));
            return null!=msgId;
        }
        return false;
    }

}
