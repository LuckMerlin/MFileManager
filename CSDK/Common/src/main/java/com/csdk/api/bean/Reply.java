package com.csdk.api.bean;

import com.csdk.api.core.Code;
import com.csdk.api.core.Label;

/**
 * Create LuckMerlin
 * Date 11:36 2020/8/6
 * TODO
 */
public class Reply<T> implements Label, Code {
    private int code;
    private String msg;
    private T data;

    public Reply(){
        this(0,null,null);
    }

    public Reply(int code,String msg,T data){
        this.code=code;
        this.msg=msg;
        this.data=data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isSucceed(){
        return code== CODE_SUCCEED;
    }

    public T getSucceedData(){
        return code== CODE_SUCCEED ?data:null;
    }

    public T getData() {
        return data;
    }

//    public static <T extends JsonObject> Reply<T> fromJson(String jsonText, Class<T> cls){
//        if (null==jsonText||jsonText.length()<=0||!jsonText.startsWith("{")||!jsonText.endsWith("}")){
//            return null;
//        }
//        try {
//            Json json=new Json(jsonText);
//            return new Reply<>(json.optInt(Label.LABEL_CODE,Code.CODE_FAIL),json.optString(Label.LABEL_MSG,null),
//                    null!=cls?json.optObject(Label.LABEL_DATA,cls):null);
//        } catch (JSONException e) {
//            Debug.E("Exception get body reply."+e);
//            e.printStackTrace();
//        }
//        return null;
//    }

    @Override
    public String toString() {
        return "Reply{" + "code=" + code + ", msg='" + msg + '\'' + ", data=" + data + '}';
    }
}
