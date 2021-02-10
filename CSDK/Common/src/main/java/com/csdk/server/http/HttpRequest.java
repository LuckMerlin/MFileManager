package com.csdk.server.http;

import com.csdk.api.bean.LoginAuth;
import com.csdk.debug.Logger;
import com.csdk.server.Configure;
import com.csdk.server.data.Json;
import com.csdk.api.core.Label;
import com.csdk.server.util.AESUtil;
import com.csdk.server.util.Md5;
import org.json.JSONObject;
import java.io.File;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Create LuckMerlin
 * Date 10:43 2020/8/26
 * TODO
 */
public final class HttpRequest {

    public final boolean callHttpRequest(LoginAuth loginAuth, String httpServerUri, String routeUri, JSONObject args, OnHttpCallback callback, String debug){
        return callHttpRequest(loginAuth,httpServerUri, routeUri, args,null,callback,debug);
    }

    public final boolean callHttpRequest(LoginAuth loginAuth,String httpServerUri,String routeUri, JSONObject args, File file,OnHttpCallback callback, String debug){
        Request request=createHttpRequest(loginAuth,httpServerUri,routeUri,args,file,debug);
        if (null!=request){
            return callHttpRequest(request,callback,debug);
        }
        Logger.E("Can't call http request while create NONE "+(null!=debug?debug:"."));
        return false;
    }

    public final boolean callHttpRequest(String productId,String productKey,String httpServerUri, String routeUri, JSONObject args,OnHttpCallback callback,String debug){
        Request request=createHttpRequest(productId,productKey,httpServerUri,routeUri,args,debug);
        if (null!=request){
            return callHttpRequest(request,callback,debug);
        }
        Logger.E("Can't call http request while create NONE "+(null!=debug?debug:"."));
        return false;
    }

    public final boolean callHttpRequest(Request request,OnHttpCallback callback,String debug){
        if (null!=request){
            callback=null!=callback?callback:new OnHttpFinish();
            new OkHttpClient.Builder().addInterceptor(new RetryIntercepter(5)).build().newCall(request).enqueue(callback);
            return true;
        }
        return false;
    }

    public final Request createHttpRequest(LoginAuth loginAuth,String httpServerUri,String routeUri, JSONObject args, String debug){
            return createHttpRequest(loginAuth, httpServerUri,routeUri,args,null, debug);
    }

    public final Request createHttpRequest(LoginAuth loginAuth,String httpServerUri,String routeUri, JSONObject args,File file, String debug){
        if (null==loginAuth){
            Logger.W("Can't create HTTP request while NONE login " + (null != debug ? debug : "."));
            return null;
        }
        final String productId=loginAuth.getProductId();
        if (null==productId||productId.length()<=0){
            Logger.W("Can't create HTTP request while product id NULL " + (null != debug ? debug : "."));
            return null;
        }
        final String productKey=loginAuth.getProductKey();
        if (null==productKey||productKey.length()<=0){
            Logger.W("Can't create HTTP request while product key NULL " + (null != debug ? debug : "."));
            return null;
        }
        if (null==httpServerUri||httpServerUri.length()<=0){
            Logger.W("Can't create HTTP request while server URI NULL "+(null!=debug?debug:"."));
            return null;
        }
        String serverId=loginAuth.getServerId();
        String loginKey=loginAuth.getKey();
        if (null==serverId||null==loginKey){
            Logger.W("Can't create HTTP request while serverID or login KEY invalid "+(null!=debug?debug:"."));
            return null;
        }
        Json json=new Json().putSafe(Label.LABEL_SERVER_ID,serverId);//.putSafe(Label.LABEL_PRODUCT_ID, productId);
        json=null!=args&&args.length()>0?json.putSafe(args):json;
        Request request=build(httpServerUri+(null!=routeUri?routeUri:""),loginKey, json,productId,productKey,file);
        return request;
    }

    public final Request createHttpRequest(String productId,String productKey,String httpServerUri,String routeUri, JSONObject args,String debug){
        if (null==httpServerUri||httpServerUri.length()<=0){
            Logger.W("Can't create HTTP request while server URI NULL "+(null!=debug?debug:"."));
            return null;
        }
        if (null==productId||productId.length()<=0){
            Logger.W("Can't create HTTP request while product id NULL " + (null != debug ? debug : "."));
            return null;
        }
        if (null==productKey||productKey.length()<=0){
            Logger.W("Can't create HTTP request while product key NULL " + (null != debug ? debug : "."));
            return null;
        }
        Json json=new Json();
        json=null!=args&&args.length()>0?json.putSafe(args):json;
        return build(httpServerUri+(null!=routeUri?routeUri:""),null, json,productId,productKey,null);
    }

    public Request build(String httpServerUri,String apiToken,String productId,String productKey,JSONObject args){
        return build(httpServerUri,apiToken, args,productId,productKey, null);
    }

    public Request build(String httpServerUri,String apiToken,JSONObject args,String productId,String productKey,File file){
        if (null!=httpServerUri&&httpServerUri.length()>0){
            if (null==productId||productId.length()<=0){
                Logger.W("Can't build http while product id invalid.");
                return null;
            }
            if (null==productKey||productKey.length()<=0){
                Logger.W("Can't build http while product key invalid.");
                return null;
            }
            Json bodyJson=new Json();
            bodyJson=null!=args&&args.length()>0?bodyJson.putSafe(args):bodyJson;
            bodyJson.putSafe(Label.LABEL_PRODUCT_ID, productId);
            Configure configure=Configure.getInstance();
            String language=null!=configure?configure.getSystemLanguage():null;
            if (null!=language&&language.length()>0){
                bodyJson.putSafe(Label.LABEL_LANGUAGE,language.toLowerCase());
            }
            String bodyJsonText=null!=bodyJson?bodyJson.toString():"";
            Logger.M(null, "Http request:"+httpServerUri+" "+language+"\n  args="+bodyJsonText);
            //
            String encryptText = AESUtil.encrypt(null != bodyJsonText ? bodyJsonText : "", productKey);
            //
            final long timeStamp=System.currentTimeMillis();
            String toSignStr = "data="+encryptText + "&productId="+productId + "&timestamp="+timeStamp + "&"+productKey;
            String signValue=null!=toSignStr&&toSignStr.length()>0? Md5.stringToMD5(toSignStr):null;
            FormBody.Builder builder= new FormBody.Builder() .add(Label.LABEL_PRODUCT_ID,productId).
                    add(Label.LABEL_TIME_STAMP,""+timeStamp).
                    add(Label.LABEL_DATA,encryptText).add(Label.LABEL_SIGN,signValue);
            if (null!=apiToken&&null!=builder){
                builder.add(Label.LABEL_API_TOKEN,apiToken);
            }
            FormBody formBody=null!=builder?builder.build():null;
            if (null!=file){
                String TYPE = "application/octet-stream";
                RequestBody fileBody = RequestBody.create(MediaType.parse(TYPE),file);
                MultipartBody.Builder bodyBuilder=new MultipartBody.Builder();
                bodyBuilder.setType(MultipartBody.FORM).addFormDataPart(Label.LABEL_DATA,encryptText).
                        addFormDataPart(Label.LABEL_PRODUCT_ID,productId).addFormDataPart(Label.LABEL_TIME_STAMP,
                        ""+timeStamp).addFormDataPart(Label.LABEL_SIGN,signValue).addFormDataPart(Label.LABEL_UPLOAD,file.getName(),fileBody);
                if (null!=apiToken){
                    bodyBuilder.addFormDataPart(Label.LABEL_API_TOKEN,apiToken);
                }
                return new Request.Builder().url(httpServerUri).post(bodyBuilder.build()).build();
            }
            return new Request.Builder().url(httpServerUri).post(formBody).build();
        }
        return null;
    }

    public Request buildGet(String httpServerUri){
        if (null!=httpServerUri&&httpServerUri.length()>0){
            Logger.M(null, "Http request:"+httpServerUri);
            return new Request.Builder().url(httpServerUri).get().build();
        }
        return null;
    }

}
