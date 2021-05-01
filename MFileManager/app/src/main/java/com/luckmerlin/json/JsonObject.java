package com.luckmerlin.json;

import android.net.Uri;

import org.json.JSONObject;

public final class JsonObject {

    public String optString(String key,String def){
        return null;
    }

    public boolean optBoolean(String key,boolean def){
        JSONObject jsonObject;
        return def;
    }

    public Uri optUri(String key, Uri def){
        JSONObject jsonObject;
        return def;
    }

    public JsonObject putNotNull(String key,Object object){
        return this;
    }
}
