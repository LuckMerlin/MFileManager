package com.luckmerlin.file;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class LocalClient extends AbsClient<LocalFolder<Query>,Query,LocalPath>{
    private String mName;
    private long mTotal;
    private long mAvailable;
    private final String mRootPath;

    public LocalClient(String rootPath,String name){
        mRootPath=rootPath;
        mName=name;
    }

    public String getName() {
        return mName;
    }

    public long getAvailable() {
        return mAvailable;
    }

    public long getTotal() {
        return mTotal;
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
        }else if (!browserFile.canExecute()){
            Debug.W("Can't query local client while query file NONE permission.");
            return null;
        }
        String filterName=null!=path?path.getName():null;
        final File[] files=browserFile.listFiles((File file)-> { if (null!=filterName&&filterName.length()>0){
                    String fileName=file.getName();
                    return null!=fileName&&fileName.contains(fileName); }return true; });
        Arrays.sort(files,(File file1, File file2)-> {
                boolean directory1=file1.isDirectory();
                boolean directory2=file2.isDirectory();
                if (directory1&&directory2){
                    return file1.compareTo(file2);
                }
                return directory1?-1:directory2?1:0; });
        int size=null!=files?files.length:-1;
        to=Math.min(to,size);
        LocalPath currentPath=LocalPath.create(browserFile);
        List<LocalPath> list=new ArrayList<>();
        LocalPath localPath=null;
        for (int i = (int)from; i < to; i++) {
            File childFile=files[i];
            if (null!=(localPath=(null!=childFile?LocalPath.create(childFile):null))){
                list.add(localPath);
            }
        }
        LocalFolder<Query> localFolder=new LocalFolder<>(currentPath,path,from,to,list);
        Reply<LocalFolder<Query>> reply=new Reply<LocalFolder<Query>>(true, What.WHAT_SUCCEED,null,localFolder);
        notifyApiFinish(What.WHAT_SUCCEED,null,reply,null,callback);
        return (cd,dd)->true;
    }
}
