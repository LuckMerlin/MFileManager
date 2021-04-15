package com.luckmerlin.file;

import android.content.Context;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.retrofit.Retrofit;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

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
        @FormUrlEncoded
        Call<Reply<NasPath>> loadThumb(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_WIDTH) int width, @Field(Label.LABEL_WIDTH) int height);

    }

    public NasClient(String hostUrl,int hostPort,String name){
        mHostUrl=hostUrl;
        mHostPort=hostPort;
        this.name=name;
    }

    @Override
    public Canceler loadPathThumb(Context context, Path path,int width,int height,OnApiFinish<Object> callback) {
        if (null!=path&&path instanceof NasPath){
            if (path.isAnyType(Path.TYPE_VIDEO,Path.TYPE_IMAGE)){
                Retrofit retrofit=mRetrofit;
                Call<Reply<NasPath>> call=retrofit.prepare(Api.class,getHostUri()).loadThumb(path.getPath(),width,height);
               if (null!=call){
                   final Canceler canceler=(boolean b, String s)-> {
                       call.cancel();

                       return true;
                   };
                   call.enqueue(new Callback<Reply<NasPath>>() {
                       @Override
                       public void onResponse(Call<Reply<NasPath>> call, Response<Reply<NasPath>> response) {
//                           ResponseBody responseBody = response.body();
//
//                           bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
                       }

                       @Override
                       public void onFailure(Call<Reply<NasPath>> call, Throwable t) {
                            notifyApiFinish(What.WHAT_ERROR,"Error",null,callback);
                       }
                   });
                   return canceler;
               }
            }
        }
        return super.loadPathThumb(context, path,width,height, callback);
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
//        int thumbWidth=null!=query?query.getThumbWidth():0;
//        int thumbHeight=null!=query?query.getThumbHeight():0;
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
