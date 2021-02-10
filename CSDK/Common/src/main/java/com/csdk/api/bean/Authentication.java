package com.csdk.api.bean;

import com.csdk.server.data.JsonObject;
import org.json.JSONObject;

/**
 * Create LuckMerlin
 * Date 12:09 2020/8/6
 * TODO
 */
public final class Authentication  implements JsonObject {
    private String uid;
    private String key;
    private Object accepts;

    public String getUid() {
        return uid;
    }

    public String getKey() {
        return key;
    }

    public Object getAccepts() {
        return accepts;
    }

    @Override
    public boolean apply(Object object) {
        if (null!=object&&object instanceof JSONObject){
            JSONObject json=(JSONObject)object;
            uid=json.optString("uid", null);
            key=json.optString("key", null);
            return (null!=uid||null!=key);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Authentication{" + "uid=" + uid ;
    }
}
