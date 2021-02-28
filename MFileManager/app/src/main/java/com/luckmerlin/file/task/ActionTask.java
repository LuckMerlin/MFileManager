package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.What;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ActionTask extends Task {
    private final LinkedHashMap<Path,Result> mPaths=new LinkedHashMap<>();

    public ActionTask(List<Path> paths){
        LinkedHashMap<Path,Result> maps=mPaths;
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
        Map<Path,Result> paths=mPaths;
        return null==paths||paths.size()<=0;
    }

    protected abstract Result onExecute(Path path,Task task, OnTaskUpdate callback);

    public final Collection<Path> getPaths() {
        LinkedHashMap<Path,Result> paths=mPaths;
        if (null!=paths){
            synchronized (paths){
                return paths.keySet();
            }
        }
        return null;
    }

    @Override
    protected final Result onExecute(Task task, OnTaskUpdate callback) {
        LinkedHashMap<Path,Result> paths=mPaths;
        synchronized (paths){
            if (null==paths||paths.size()<=0){
                return code(What.WHAT_SUCCEED);
            }
            Set<Path> set=paths.keySet();
            if (null==set){
                return code(What.WHAT_ERROR);
            }
            Result executeResult=null;
            for (Path child:set) {
                if (null==child) {
                    continue;
                }
                Result result=paths.get(child);
                if (null!=result){
                    executeResult=result.getCode()!=What.WHAT_ERROR?result:executeResult;
                    continue;
                }
                result=null!=child?onExecute(child,task,callback):null;
                result=null!=result?result:code(What.WHAT_ERROR);
                paths.put(child,result);
                executeResult=result.getCode()!=What.WHAT_SUCCEED?result:executeResult;
            }
            return executeResult;
        }
    }
}
