package com.csdk.api.data;

import com.csdk.api.core.Debug;
import com.csdk.server.data.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2020/7/30.
 */

public class Json extends JSONObject {

    public Json(String jsonText) throws JSONException {
        super(jsonText);
    }

    public Json()   {
        super();
    }

    public final Json put(Map<String, ?> map){
        Set<String> set=null!=map?map.keySet():null;
        if (null!=set){
            for (String child:set) {
                if (null!=child){
                    putSafe(child, map.get(child));
                }
            }
        }
        return this;
    }

    public final String optString(String key,String def){
        Object value=null!=key?opt(key):null;
        if (null!=value&&value instanceof String){
            return (String)value;
        }
        return def;
    }

    public static Json create(String jsonText){
        try {
            return null!=jsonText&&jsonText.length()>0&&jsonText.startsWith("{")&&jsonText.endsWith("}")?new Json(jsonText):null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final Json putSafe(JSONObject jsonObject){
        Iterator<String> iterator=null!=jsonObject&&jsonObject.length()>0?jsonObject.keys():null;
        if (null!=iterator&&iterator.hasNext()){
            do {
                String nextKey=iterator.next();
                putSafe(nextKey, jsonObject.opt(nextKey));
            }while (iterator.hasNext());
        }
        return this;
    }

    public final Json putMapSafe(String key, Map<String, ?> value)  {
        if (null!=key&&null!=value){
            Json json=new Json();
            json.put(value);
            putSafe(key, json);
        }
        return this;
    }

    public final Json putSafe(String key, Object value)  {
        try {
            putNotNull(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public final <T extends JsonObject> T optObject(String key, Class<T> cls){
        return optObject(key, cls,null);
    }

    public final <T extends JsonObject> T optObject(String key, Class<T> cls, T def){
        Object object=null!=key&&null!=cls?opt(key):null;
        if (null!=object){
            try {
                if (object instanceof String){
                    String jsonText=(String)object;
                    jsonText=null!=jsonText?jsonText.trim():jsonText;
                    if (null!=jsonText&&jsonText.length()>0&&jsonText.startsWith("{")&&jsonText.endsWith("}")){
                        object=new JSONObject(jsonText);
                    }
                }
                if (object instanceof JSONObject){
                    Constructor constructor=cls.getDeclaredConstructor();
                    T jsonObject=null!=constructor?(T)constructor.newInstance():null;
                    return null!=jsonObject&&jsonObject.apply(object)?jsonObject:null;
                }
            } catch (Exception e) {
                Debug.E("Exception opt json object.e="+e);
                e.printStackTrace();
            }
        }
        return def;
    }

    public final Json putCollectionSafe(String key, Collection<?> values)  {
        if (null!=key&&null!=values){
            JSONArray array=new JSONArray();
            for (Object child:values) {
                if (null!=child){
                    array.put(child);
                }
            }
            try {
                putNotNull(key,array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public final Json putArraySafe(String key, Object[] values)  {
        if (null!=key&&null!=values){
            JSONArray array=new JSONArray();
            for (Object child:values) {
                if (null!=child){
                    array.put(child);
                }
            }
            try {
                putNotNull(key,array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public final String optString(){

        return null;
    }

    public final Json putNotNull(String key, Object value) throws JSONException {
        if (null!=key&&null!=value){
            super.put(key,value);
        }
        return this;
    }

    public final Json putNotEmptySafe(String key, String value)   {
        try {
            putNotEmpty(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public final Json putNotEmpty(String key, String value) throws JSONException {
        if (null!=key&&key.length()>0&&null!=value&&value.length()>0){
            super.put(key,value);
        }
        return this;
    }

}
