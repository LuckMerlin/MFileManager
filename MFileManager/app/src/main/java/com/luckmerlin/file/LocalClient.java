package com.luckmerlin.file;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.nas.Nas;
import com.luckmerlin.file.retrofit.Retrofit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public final class LocalClient extends AbsClient<LocalFolder<Query>,Query,LocalPath>{
    private String mName;
    private final String mRootPath;
    private String mCloudHostUrl;
    private final Retrofit mRetrofit=new Retrofit();

    private interface Api{
        @POST("/file/query")
        @FormUrlEncoded
        Observable<Reply<List<Reply<NasPath>>>> queryFiles(@Field(Label.LABEL_MD5) List<String> md5s);
    }

    public LocalClient(String rootPath,String name){
        mRootPath=rootPath;
        mName=name;
    }

    public String getName() {
        return mName;
    }

    public long getAvailable() {
        String root=mRootPath;
        return null!=root&&root.length()>0?new File(root).getFreeSpace():0;
    }

    public long getTotal() {
        String root=mRootPath;
        return null!=root&&root.length()>0?new File(root).getTotalSpace():0;
    }

    @Override
    protected Canceler query(Query path, long from, long to, OnApiFinish<Reply<LocalFolder<Query>>> callback) {
        if (null==callback){
            Debug.W("Can't query local client while callback or path invalid.");
            return null;
        }else if (from<0||to<from){
            Debug.W("Can't query local client while from or to invalid.");
            return null;
        }
        String pathValue=null!=path?path.getPath():null;
        String browserPath=null!=pathValue&&pathValue.length()>0?pathValue:mRootPath;
        File browserFile=null!=browserPath&&browserPath.length()>0?new File(browserPath):null;
        if (null==browserFile){
            Debug.W("Can't query local client while query file invalid.");
            return null;
        }else if (!browserFile.exists()){
            Debug.W("Can't query local client while query file not exist.");
            return null;
        }else if (!browserFile.isDirectory()){
            Debug.W("Can't query local client while query file not directory.");
            return null;
        }else if (!browserFile.canRead()){
            Debug.W("Can't query local client while query file NONE permission.");
            Reply<LocalFolder<Query>> reply=new Reply<LocalFolder<Query>>(false, What.WHAT_NONE_PERMISSION,null,null);
            notifyApiFinish(What.WHAT_SUCCEED,null,reply,null,callback);
            return null;
        }
        String filterName=null!=path?path.getName():null;
        final List<LocalPath> list=new ArrayList<>();
        final LocalPath currentPath=LocalPath.create(browserFile);
        browserFile.listFiles((File file)-> {
            if (null==file){
                return false;
            }
            String fileName=file.getName();
            if (null!=filterName&&filterName.length()>0){
                if (null==fileName||!fileName.contains(filterName)){
                    return false;
                }
            }
            LocalPath child=LocalPath.create(browserFile,false,null);
            if (null!=child){
                list.add(child);
            }
            return false;
        });
        int code=What.WHAT_SUCCEED;
        Collections.sort(list, (LocalPath file1, LocalPath file2)-> {
            boolean directory1=file1.isDirectory();
            boolean directory2=file2.isDirectory();
            if (directory1&&directory2){
                return file1.compareTo(file2);
            }
            return directory1?-1:directory2?1:0;
        });
        LocalFolder<Query> localFolder=new LocalFolder<>(currentPath,path,from,to,list);
        Reply<LocalFolder<Query>> reply=new Reply<LocalFolder<Query>>(true, code,null,localFolder);
        notifyApiFinish(code,null,reply,localFolder,callback);
        mRetrofit.call(mRetrofit.prepare(Api.class,mCloudHostUrl).queryFiles(null), );
        return null!=list&&list.size()>0?mRetrofit.prepare(Api.class,mCloudHostUrl).queryFiles(null):(a,b  )->true;
    }
}
