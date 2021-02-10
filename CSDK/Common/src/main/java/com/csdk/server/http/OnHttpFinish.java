package com.csdk.server.http;

import android.os.Handler;
import android.os.Looper;

import com.csdk.debug.Logger;
import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Create LuckMerlin
 * Date 13:56 2020/8/20
 * TODO
 */
public class OnHttpFinish<T> extends OnHttpCallback {
    private final Handler mHandler=new Handler(Looper.getMainLooper());

    protected void onSyncFinish(boolean succeed, Call call, String note, T data){
        //Do nothing
    }

    /**
     * @deprecated
     */
    protected void onFinish(boolean succeed, Call call, String note, T data){
        //Do nothing
    }

    @Override
    protected final void onHttpFinish(final boolean succeed,final Call call,final String note, Object data) {
        Response response=null!=data&&data instanceof Response?((Response)data):null;
        Type genericType=getClass().getGenericSuperclass();
        Type[] types=null!=genericType&&genericType instanceof ParameterizedType?((ParameterizedType)genericType).getActualTypeArguments():null;
        genericType=null!=types&&types.length>0?types[0]:null;
        T result=null;
        ResponseBody responseBody=null!=response?response.body():null;
        try {
            String text= null!=responseBody?responseBody.string():null;
            Request request=null!=call?call.request():null;
            HttpUrl url=null!=request?request.url():null;
//            Debug.D("Http response text :"+(null!=url?url.toString():"")+"\n  "+text);
            Logger.M(null,"Http response text :"+(null!=url?url.toString():"")+"\n  "+text);
//            //Test
//            Request request1=null!=call?call.request():null;
//            HttpUrl httpServerUri=null!=request1?request1.url():null;
//            String test=null!=httpServerUri?httpServerUri.toString():null;
//            if (null!=test&&test.contains("/api/user/friends")){
//                Debug.D("Http response text:"+text);
//            }
            if (null!=genericType&&null!=text&&text.length()>0) {
                Object object = new Gson().fromJson(text, genericType);
                result = null != object ? (T) object : null;
            }
        } catch (Exception e) {
            Logger.E("Exception "+e,e);
            e.printStackTrace();
        }
        final T finalResult=result;
        onSyncFinish(succeed, call, note, finalResult);
        mHandler.post(()-> onFinish(succeed, call, note, finalResult));
    }
}
