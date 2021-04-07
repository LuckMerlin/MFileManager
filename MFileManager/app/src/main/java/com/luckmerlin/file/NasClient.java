package com.luckmerlin.file;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.retrofit.Retrofit;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public final class NasClient extends AbsClient<NasFolder<Query>,Query,NasPath> {
    private final Retrofit mRetrofit=new Retrofit();
    private final String mHostUrl;
    private final int mHostPort;
    private String name;

    private interface Api{
        @POST("/file/browser")
        @FormUrlEncoded
        Observable<Reply<NasFolder<Query>>> queryFiles(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_NAME) String name,@Field(Label.LABEL_FROM) long from,
                                                   @Field(Label.LABEL_TO) long to);

        @POST("/file/home")
        @FormUrlEncoded
        Observable<Reply<NasPath>> setUserHome(@Field(Label.LABEL_PATH) String path);

        @POST("/file/delete")
        @FormUrlEncoded
        Observable<Reply<NasPath>> deletePath(@Field(Label.LABEL_PATH) String path);

        @POST("/file/rename")
        @FormUrlEncoded
        Observable<Reply<NasPath>> rename(@Field(Label.LABEL_PATH) String path,@Field(Label.LABEL_NAME)String name);


        @POST("/file/create")
        @FormUrlEncoded
        Observable<Reply<NasPath>> createPath(@Field(Label.LABEL_PATH) String path,@Field(Label.LABEL_FOLDER) boolean createFolder);
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
        return null!=retrofit&&null!=callback?retrofit.call(retrofit.prepare(Api.class, getHostUri())
                .queryFiles(path,name, from, to), (OnApiFinish<Reply<NasFolder<Query>>>)
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
    public boolean rename(String path, String newName, OnApiFinish<Reply<NasPath>> callback) {
        return null!=mRetrofit.call(mRetrofit.prepare(Api.class,getHostUri()).rename(path,newName),callback);
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
