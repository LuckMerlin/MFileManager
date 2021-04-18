package com.luckmerlin.file;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.retrofit.Retrofit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;

public final class NasClient extends AbsClient<NasFolder<Query>,Query,NasPath> {
    private final Retrofit mRetrofit=new Retrofit();
    private final String mHostUrl;
    private final int mHostPort;
    private String name;

    private interface Api{

        @POST("/file/browser")
        @FormUrlEncoded
        Observable<Reply<NasFolder<Query>>> queryFiles(@FieldMap Map<String,Object> map);

        @POST("/file/home")
        @FormUrlEncoded
        Observable<Reply<NasPath>> setUserHome(@Field(Label.LABEL_PATH) String path);

        @POST("/file/delete")
        @FormUrlEncoded
        Observable<Reply<NasPath>> deletePath(@Field(Label.LABEL_PATH) String path);

        @POST("/file/rename")
        @FormUrlEncoded
        Observable<Reply<NasPath>> rename(@Field(Label.LABEL_PATH) String path,@Field(Label.LABEL_NAME)String name,@Field(Label.LABEL_NAME) boolean justName);

        @POST("/file/create")
        @FormUrlEncoded
        Observable<Reply<NasPath>> createPath(@Field(Label.LABEL_PATH) String path,@Field(Label.LABEL_FOLDER) boolean createFolder);

        @POST("/file/thumb")
        @Streaming
        @FormUrlEncoded
        Call<ResponseBody> loadThumb(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_WIDTH) int width, @Field(Label.LABEL_HEIGHT) int height);

        @POST("/file/detail")
        @Streaming
        @FormUrlEncoded
        Observable<Reply<NasPath>> loadDetail(@Field(Label.LABEL_PATH) String path);

    }

    public NasClient(String hostUrl,int hostPort,String name){
        mHostUrl=hostUrl;
        mHostPort=hostPort;
        this.name=name;
    }

    @Override
    public boolean setAsHome(Folder folder, OnApiFinish<Reply<? extends Path>> callback) {
        return null!=folder&&null!=mRetrofit.call(mRetrofit.prepare(Api.class,getHostUri()).setUserHome(folder.getPath()),callback);
    }

    @Override
    protected Canceler query(Query query, long from, long to, OnApiFinish<Reply<NasFolder<Query>>> callback) {
        Retrofit retrofit=mRetrofit;
        String path=null!=query?query.getPath():null;
        String name=null!=query?query.getName():null;
        final Map<String,Object> map=new HashMap<>();
        map.put(Label.LABEL_PATH,null!=path?path:"");
        map.put(Label.LABEL_NAME,null!=name?name:"");
        map.put(Label.LABEL_FROM,from);
        map.put(Label.LABEL_TO,to);
        return null!=retrofit&&null!=callback?retrofit.call(retrofit.prepare(Api.class,
                getHostUri()).queryFiles(map), (OnApiFinish<Reply<NasFolder<Query>>>)
                (int what, String note, Reply<NasFolder<Query>> data, Object arg)-> {
                    NasFolder<Query> nasFolder=null!=data?data.getData():null;
                    if (null!=nasFolder){
                        nasFolder.setHost(mHostUrl).setPort(mHostPort);
                    }
                if (null!=callback){
                    callback.onApiFinish(what,note,data,arg);
                }
        }):null;
    }


    @Override
    public Canceler loadPathDetail(Path path, OnApiFinish<Reply<NasPath>> callback) {
        return mRetrofit.call(mRetrofit.prepare(Api.class,getHostUri()).loadDetail(null!=path?path.getPath():null),callback);
    }

    public final String getHostUri() {
        String host=mHostUrl;
        return null!=host&&host.length()>0?host+":"+mHostPort:null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean deletePath(String path, OnApiFinish<Reply<NasPath>> callback) {
        return null!=mRetrofit.call(mRetrofit.prepare(Api.class,getHostUri()).deletePath(path),callback);
    }

    @Override
    public boolean rename(Path path, String newName, boolean justName,OnApiFinish<Reply<NasPath>> callback) {
        return null!=mRetrofit.call(mRetrofit.prepare(Api.class,getHostUri()).rename
                (null!=path?path.getPath():null,newName,justName),callback);
    }

    @Override
    public boolean createPath(String path, boolean createFolder, OnApiFinish<Reply<NasPath>> callback) {
        return null!=mRetrofit.call(mRetrofit.prepare(Api.class,getHostUri()).createPath(path,createFolder),callback);
    }

    @Override
    public long getAvailable() {
        return 0;
    }

    @Override
    public long getTotal() {
        return 0;
    }
}
