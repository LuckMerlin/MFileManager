package com.luckmerlin.file;

import com.luckmerlin.adapter.OnSectionLoadFinish;
import com.luckmerlin.adapter.recycleview.Section;
import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.retrofit.Retrofit;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public final class NasClient extends AbsClient<Query,NasPath> {
    private final String mHostUrl;
    private final int mHostPort;
    private String name;

    private interface Api{
        @POST("/file/browser")
        @FormUrlEncoded
        Observable<Reply<NasFolder<Query,NasPath>>> queryFiles(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_NAME) String name,@Field(Label.LABEL_FROM) long from,
                                                   @Field(Label.LABEL_TO) long to);
    }

    private final Retrofit mRetrofit=new Retrofit();

    public NasClient(String hostUrl,int hostPort){
        mHostUrl=hostUrl;
        mHostPort=hostPort;
    }

    @Override
    protected Canceler query(Query query, long from, long to, OnSectionLoadFinish<Query, NasPath> callback) {
        Retrofit retrofit=mRetrofit;
        String path=null!=query?query.getPath():null;
        String name=null!=query?query.getName():null;
        return null!=retrofit&&null!=callback?retrofit.call(retrofit.prepare(Api.class, getHostUrl()).queryFiles(path,name, from, to),
                (OnApiFinish<Reply<NasFolder<Query,NasPath>>>)(int what, String note, Reply<NasFolder<Query,NasPath>> data, Object arg)-> {
                    boolean succeed=what== What.WHAT_SUCCEED&&null!=data&&data.isSuccess();
                    callback.onSectionLoadFinish(succeed,note,null!=data?data.getData():null);
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
