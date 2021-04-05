package com.luckmerlin.file;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.retrofit.Retrofit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public final class LocalClient extends AbsClient<LocalFolder<Query>,Query,LocalPath>{
    private String mName;
    private final String mRootPath;
    private String mSyncHost;
    private Querying mQuerying;

    private interface Api{
        @POST("/file/query")
        @FormUrlEncoded
        Call<Reply<Map<String,Reply<NasPath>>>> queryFiles(@Field(Label.LABEL_MD5) Collection<String> md5s);
    }

    public LocalClient(String rootPath,String name){
        mRootPath=rootPath;
        mName=name;
    }

    public LocalClient setSyncHost(String syncHost){
        mSyncHost=syncHost;
        return this;
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
            if (null!=file){
                String fileName=file.getName();
                if (null!=filterName&&filterName.length()>0){
                    if (null==fileName||!fileName.contains(filterName)){
                        return false;
                    }
                }
                LocalPath child=LocalPath.create(file,false,null);
                if (null!=child){
                    list.add(child);
                }
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
        final Reply<LocalFolder<Query>> reply=new Reply<LocalFolder<Query>>(true, code,null,localFolder);
        notifyApiFinish(code,null,reply,localFolder,callback);
        String serverUrl=mSyncHost;
        if (null==list||list.size()<=0||null==serverUrl||serverUrl.length()<=0||null==callback||!(callback instanceof OnPathUpdate)){
            return (a,b)->true;
        }
        final OnPathUpdate onSyncUpdate=(OnPathUpdate)callback;
        final Disposable[] disposables=new Disposable[1];
        final Querying querying=new Querying(path,from,to,false){
            @Override
            protected void onCancelChange(boolean canceled) {
                Disposable disposable=disposables[0];
                if (null!=disposable&&canceled){
                    Debug.D("Cancel query local folder while cancel.");
                    disposable.dispose();
                }
            }
        };
        Cancel lastSyncCanceler=mQuerying;
        if (null!=lastSyncCanceler&&lastSyncCanceler.equals(querying)){
            lastSyncCanceler.cancel(true,"While start new sync.");
        }
        mQuerying=querying;
        final Disposable disposable=disposables[0]=Observable.create((ObservableEmitter<List<LocalPath>> emitter)-> {
                Map<String,LocalPath> md5Maps=new HashMap<>();
                String md5=null;
                for (LocalPath localPath:list) {
                    if (querying.isCanceled()){
                        break;
                    }else if (null!=localPath){
                        if (null!=(md5=localPath.load(true,querying).getMd5())){
                            md5Maps.put(md5,localPath);
                        }else{
                            localPath.setSync(new Reply<>(true,What.WHAT_NORMAL,null,null));
                        }
                    }
                    onSyncUpdate.onPathUpdate(localPath);
                }
                if (querying.isCanceled()){
                    notifyApiFinish(What.WHAT_CANCEL,null,reply,null,callback);
                    return;
                }
                Set<String> md5Set=md5Maps.keySet();
                if (null!=md5Set&&md5Set.size()>0){
                    Map<String,Reply<NasPath>> replyList=null;
                    try {
                        if (!querying.isCanceled()){
                            Retrofit retrofit=new Retrofit();
                            Response<Reply<Map<String,Reply<NasPath>>>> response=retrofit.prepare(Api.class, serverUrl).queryFiles(md5Set).execute();
                            Reply<Map<String,Reply<NasPath>>> responseReply=null!=response?response.body():null;
                            replyList=null!=responseReply?responseReply.getData():null;
                        }
                    }catch (Exception e){
                        //Do nothing
                    }finally {
                        for (String child:md5Set) {
                            LocalPath childPath=md5Maps.get(child);
                            if (null!=childPath&&!querying.isCanceled()){
                                Reply<NasPath> childReply=null!=replyList&&null!=child?replyList.get(child):null;
                                childPath.setSync(null!=childReply?childReply:new Reply<>(false,What.WHAT_FAIL,null,null));
                                onSyncUpdate.onPathUpdate(childPath);
                            }
                        }
                    }
                }
            Cancel current=mQuerying;
            if (null!=current&&current==querying) {
                mQuerying = null;
            }
        }).subscribeOn(Schedulers.io()).subscribe();
        if (querying.isCanceled()&&null!=disposable){
            Debug.D("Cancel query local folder while canceled.");
            disposable.dispose();
        }
        return querying;
    }

    private static class Querying extends Cancel{
        private final Query mQuery;
        private final long mFrom;
        private final long mTo;

        public Querying(Query query,long from,long to,boolean canceled) {
            super(canceled);
            mQuery=query;
            mFrom=from;
            mTo=to;
        }

        @Override
        public boolean equals(Object object) {
            if (null!=object&&object instanceof Querying){
                Querying querying=(Querying)object;
                Query query=querying.mQuery;
                Query current=mQuery;
                if (mFrom==querying.mFrom&&mTo==querying.mTo&&((null==query&&null==current)||(null!=query&&
                        null!=current&&query.equals(current)))){
                    return true;
                }
            }
            return false;
        }
    }
}
