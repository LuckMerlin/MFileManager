package com.luckmerlin.file;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.retrofit.Retrofit;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public final class NasClient extends AbsClient<NasFolder<Query>,Query,NasPath> {
    private final String mHostUrl;
    private final int mHostPort;
    private String name;

    private interface Api{
        @POST("/file/browser")
        @FormUrlEncoded
        Observable<Reply<NasFolder<Query>>> queryFiles(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_NAME) String name,@Field(Label.LABEL_FROM) long from,
                                                   @Field(Label.LABEL_TO) long to);
    }

    private final Retrofit mRetrofit=new Retrofit();

    public NasClient(String hostUrl,int hostPort,String name){
        mHostUrl=hostUrl;
        mHostPort=hostPort;
        this.name=name;
    }

    @Override
    protected Canceler query(Query query, long from, long to, OnApiFinish<Reply<NasFolder<Query>>> callback) {
        Retrofit retrofit=mRetrofit;
        String path=null!=query?query.getPath():null;
        String name=null!=query?query.getName():null;
        return null!=retrofit&&null!=callback?retrofit.call(retrofit.prepare(Api.class, getHostUrl())
                .queryFiles(path,name, from, to), (OnApiFinish<Reply<NasFolder<Query>>>)
                (int what, String note, Reply<NasFolder<Query>> data, Object arg)-> {
                    NasFolder<Query> nasFolder=null!=data?data.getData():null;
                    if (null!=nasFolder){
                        nasFolder.setHost(mHostUrl);
                        nasFolder.setPort(mHostPort);
                    }
                if (null!=callback){
                    callback.onApiFinish(what,note,data,arg);
                }
        }):null;
    }

    public final String getHostUrl() {
        String host=mHostUrl;
        return null!=host&&host.length()>0?host+":"+mHostPort:null;
    }

    @Override
    public String getName() {
        return name;
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
