package com.csdk.api.struct;

import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import com.csdk.api.core.Debug;
import com.csdk.api.core.Label;
import com.csdk.server.data.Json;
import com.csdk.server.data.JsonObject;

import java.util.Base64;

/**
 * Create LuckMerlin
 * Date 10:25 2021/1/25
 * TODO
 */
public abstract class Struct implements JsonObject {
    public static final String TYPE_TEXT="text";
    public static final String TYPE_AT="at";
    public static final String TYPE_LINK_TEXT="linkText";
    public static final String TYPE_LINK="link";
    private final static String PROTOCOL="herotalk://";
    private final static String SEP="/";

    public final boolean isAnyType(String ...types){
        if (null!=types&&types.length>0){
            String type=getType();
            for (String child:types) {
                if (null!=type&&null!=child&&type.equals(child)){
                    return true;
                }
            }
        }
        return false;
    }

    public final String getStructUrl(){
        String type=getType();
        Object data=getData();
        return PROTOCOL+(null!=type&&type.length()>0?type+SEP:"")+ (null!=data? Label.LABEL_DATA+"="+data:"");
    }

    public final boolean isText(){
        String type=getType();
        return null!=type&&(type.equals(TYPE_TEXT)||type.equals(TYPE_LINK));
    }

    public abstract String getType();

    public final String getDataUrl(){
        Json json=getDataJson();
        return null!=json?json.optString(Label.LABEL_URL,null):null;
    }

    public final Integer getDataColor(){
        Json json=getDataJson();
        String colorText= null!=json?json.optString(Label.LABEL_COLOR,null):null;
        try {
            colorText=null!=colorText&&!colorText.startsWith("#")?"#"+colorText:colorText;
            colorText=null!=colorText?colorText.trim().toLowerCase():null;
            return null!=colorText&&colorText.length()>0&&colorText.startsWith("#")&&colorText.length()%2==1?Color.parseColor(colorText):null;
        }catch (Exception e){
            //Do nothing
        }
        return null;
    }

    public abstract String getTitle() ;

    public abstract Object getData() ;

    public final String getAction(){
        String protocol=PROTOCOL;
        String type=getType();
        return (null!=protocol?protocol:"")+""+(null!=type?type:"");
    }

    public final Json getDataJson(){
        Object data=getData();
        String dataJson=null!=data?data.toString():null;
        return null!=dataJson&&dataJson.length()>0?Json.create(dataJson):null;
    }

    public final String getAvatarUrl(){
        Json json=getDataJson();
        return null!=json?json.optString(Label.LABEL_AVATAR_URL, null):null;
    }

    @Override
    public boolean apply(Object json) {
        return false;
    }

    public Object json(){
        return new Json().putSafe(Label.LABEL_TYPE,getType()).putSafe(Label.LABEL_TITLE, getTitle()).putSafe(Label.LABEL_DATA, getData());
    }
}
