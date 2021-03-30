package com.luckmerlin.file.task;

import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.What;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Response;
import com.luckmerlin.task.Task;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class FilesTask extends Task {
    private final LinkedHashMap<Path, Response> mPaths=new LinkedHashMap<>();
    private int mCover=What.WHAT_INVALID;

    public FilesTask(){
        this(null);
    }

    public FilesTask(String name){
        this(name,null);
    }

    public FilesTask(String name,List<Path> paths){
        super(name);
        LinkedHashMap<Path, Response> maps=mPaths;
        if (null!=paths&&null!=maps){
            synchronized (paths){
                for (Path child:paths) {
                    if (null!=child){
                        maps.put(child,null);
                    }
                }
            }
        }
    }

    public final boolean isEmpty(){
        Map<Path, Response> paths=mPaths;
        return null==paths||paths.size()<=0;
    }

    protected abstract Response onExecute(Path path, Task task, OnTaskUpdate callback);

    public final Collection<Path> getPaths() {
        LinkedHashMap<Path, Response> paths=mPaths;
        if (null!=paths){
            synchronized (paths){
                return paths.keySet();
            }
        }
        return null;
    }

    public final FilesTask setCover(int cover) {
        this.mCover = cover;
        return this;
    }

    public final int getCover() {
        return mCover;
    }

    @Override
    protected final Response onExecute(Task task, OnTaskUpdate callback) {
        LinkedHashMap<Path, Response> paths=mPaths;
        synchronized (paths){
            if (null==paths||paths.size()<=0){
                return response(What.WHAT_SUCCEED);
            }
            Set<Path> set=paths.keySet();
            if (null==set){
                return response(What.WHAT_ERROR);
            }
            Response executeResult=null;
            for (Path child:set) {
                if (null==child) {
                    continue;
                }
                Response result=paths.get(child);
                if (null!=result){
                    executeResult=result.getCode()!=What.WHAT_ERROR?result:executeResult;
                    continue;
                }
                result=null!=child?onExecute(child,task,callback):null;
                result=null!=result?result:response(What.WHAT_ERROR);
                paths.put(child,result);
                executeResult=result.getCode()!=What.WHAT_SUCCEED?result:executeResult;
            }
            return executeResult;
        }
    }
}
