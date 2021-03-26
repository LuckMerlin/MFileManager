package com.luckmerlin.file.retrofit;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.api.Callback;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.HttpException;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit {
    private final retrofit2.Retrofit.Builder mBuilder;

    public Retrofit(){
        OkHttpClient client = createClient();
        mBuilder = null!=client?new retrofit2.Retrofit.Builder().client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()):null;
    }

    public final <T>T prepare(Class<T>  cls,String url){
            return prepare(cls,url,null);
    }

    protected String onResolveUrl(Class<?> cls,Executor callbackExecutor){

        return null;
    }

    public final <T>T prepare(Class<T>  cls,String url,Executor callbackExecutor){
        retrofit2.Retrofit retrofit=createRetrofit(cls,url,callbackExecutor);
        return null!=retrofit?retrofit.create(cls):null;
    }

    protected final WebSocket newWebSocket(String url, WebSocketListener listener){
        url=null==url||url.length()<=0?onResolveUrl(null,null):url;
        if (null==url||url.length()<=0){
            Debug.E("None url to prepare.");
            throw new RuntimeException("None url to prepare.");
        }
        OkHttpClient client=createClient();
        Request request = null!=client?new Request.Builder().url(url).build():null;
        return null!=request?client.newWebSocket(request,listener):null;
    }

    protected final OkHttpClient createClient(){
        OkHttpClient.Builder okHttp = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new LogInterceptor())
                .connectionPool(new ConnectionPool(5,1,TimeUnit.SECONDS));
        return null!=okHttp?okHttp.build():null;
    }

    protected final <T> retrofit2.Retrofit createRetrofit(Class<T>  cls,String url,Executor callbackExecutor){
        url=null==url||url.length()<=0?onResolveUrl(cls,callbackExecutor):url;
        if (null==url||url.length()<=0){
            Debug.E("None url to prepare.");
            throw new RuntimeException("None url to prepare.");
        }
        retrofit2.Retrofit.Builder builder=mBuilder;
        url=url.endsWith("/")?url:url+"/";
        builder=null!=builder?builder.baseUrl(url):null;
        if (null!=callbackExecutor&&null!=builder){
            builder.callbackExecutor(callbackExecutor);
        }
        return null!=builder?builder.build():null;
    }

    public final<T> RetrofitCanceler call(Observable<T> observable, Callback ...callbacks){
        return call(observable,null,null,callbacks);
    }

    public final<T> RetrofitCanceler call(Observable<T> observable, Scheduler subscribeOn, Scheduler observeOn, Callback ...callbacks){
        if (null!=observable){
            final RetrofitCanceler canceler=new RetrofitCanceler();
            observable.subscribeOn(null!=subscribeOn?subscribeOn:Schedulers.io()).observeOn(null!=observeOn?observeOn:AndroidSchedulers.mainThread())
                    .subscribe(new InnerCallback<T>(callbacks){
                        @Override
                        public void onSubscribe(Disposable d) {
                            canceler.mDisposable=d;
                        }

                        @Override
                        public void onComplete() {
                            canceler.mDisposable=null;
                        }
                    });
            return canceler;
        }
        Debug.W("Can't call retrofit, Observable is NULL.");
        return null;
    }

    private static abstract class InnerCallback <T>implements Observer<T> {
        private final Callback[] mCallbacks;
        private RetrofitCanceler mCanceler;

        private InnerCallback(Callback...callbacks){
            mCallbacks=callbacks;
        }


        @Override
        public void onNext(T t) {
            finishCall(null,t,mCallbacks,null);
        }

        @Override
        public void onError(Throwable e) {
            Debug.E("Error on api "+e,e);
            int what= What.WHAT_ERROR;
            String note=e.toString();
            if (e instanceof HttpException) {
                HttpException httpException = (HttpException) e;
                //httpException.response().errorBody().string()
                int code = httpException.code();
                note = httpException.getMessage();
                if (code == 504) {
                    note=" Network poor.";
                    what=What.WHAT_NETWORK_POOR;
                } else if (code == 502) {
                    note = "Server error.";
                    what=What.WHAT_SERVER_EXCEPTION;
                } else if (code == 408) {
                    note = "Request timeout.";
                    what=What.WHAT_TIMEOUT;
                } else if (code == 403) {
                    note = "None request permission.";
                    what=What.WHAT_NONE_PERMISSION;
                } else if (code == 401) {
                    note="Token invalid.";
                    what=What.WHAT_TOKEN_INVALID;
                }
            }
            finishCall(what,null,mCallbacks,note);
        }

        private void finishCall(Integer what,Object data,Callback[] callbacks,String note){
            if (null!=callbacks&&callbacks.length>0){
                for (Callback callback:callbacks) {
                    if(null==callback||!(callback instanceof OnApiFinish)){
                        continue;
                    }
                    int childWhat=What.WHAT_FAIL;boolean succeed=false;
                    data=data!=null&&checkDataGeneric(data,callback)?data:null;
                    if (null!=data&&data instanceof Reply){
                        Reply reply=(Reply)data;
                        childWhat=null!=what?what:reply.getWhat();
                        note=reply.getNote();
                        succeed=reply.isSuccess();
                    }else{
                        data=null;
                    }
                    ((OnApiFinish) callback).onApiFinish(childWhat,note,data,null);
                }
            }
        }

        private boolean checkDataGeneric(Object data, Callback callback){
            return null!=data&&null!=callback&&!(data instanceof Class);
        }

    }

}
