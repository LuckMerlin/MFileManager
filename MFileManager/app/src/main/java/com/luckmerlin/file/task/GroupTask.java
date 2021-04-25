package com.luckmerlin.file.task;

import com.luckmerlin.file.api.What;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Response;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @deprecated
 */
public abstract class GroupTask<T> extends Task {
    private final LinkedHashMap<T, Response> mValues=new LinkedHashMap<>();
    private int mDoneSize=0;

    public GroupTask(){
        this(null);
    }

    public GroupTask(String name){
        this(name,null);
    }

    public GroupTask(String name, List<T> paths){
        super(name,null);
        LinkedHashMap<T, Response> maps=mValues;
        if (null!=paths&&null!=maps){
            synchronized (paths){
                for (T child:paths) {
                    if (null!=child){
                        maps.put(child,null);
                    }
                }
            }
        }
    }

    public final int getSize(){
        LinkedHashMap<T, Response> paths=mValues;
        return null!=paths?paths.size():-1;
    }

    public final int getDoneSize(){
        return mDoneSize;
    }

    public final List<T> getRemnant(){
        LinkedHashMap<T, Response> paths=mValues;
        if (null!=paths){
            List<T> result=new ArrayList<>();
            synchronized (paths){
                Set<T> set=paths.keySet();
                if (null!=set){
                    for (T child:set){
                        if (null!=child&&null==paths.get(child)){
                            result.add(child);
                        }
                    }
                }
            }
            return null!=result&&result.size()>0?result:null;
        }
        return null;
    }

    public final boolean isEmpty(){
        Map<T, Response> paths=mValues;
        return null==paths||paths.size()<=0;
    }

    protected abstract Response onExecute(T path, Task task, OnTaskUpdate callback);

    public final Collection<T> getTasks() {
        LinkedHashMap<T, Response> paths=mValues;
        if (null!=paths){
            synchronized (paths){
                return paths.keySet();
            }
        }
        return null;
    }

    @Override
    protected Result onExecute(Task task, OnTaskUpdate callback) {
        return null;
    }

    //    @Override
//    protected final Response onExecute(Task task, OnTaskUpdate callback) {
//        LinkedHashMap<T, Response> paths=mValues;
//        Response executeResult=null;
//        mDoneSize=0;//Reset
//        while (true){
//            Response response=null;
//            T nextTask=null;
//            synchronized (paths){
//                if (null==paths||paths.size()<=0){
//                    return response(What.WHAT_SUCCEED);
//                }
//                Set<T> set=paths.keySet();
//                if (null==set){
//                    return response(What.WHAT_ERROR);
//                }
//                mDoneSize=0;//Reset
//                for (T child:set) {
//                    if (isCanceled()){
//                        executeResult=response(What.WHAT_CANCEL);
//                        break;
//                    }
//                    if (null==child) {
//                        continue;
//                    }
//                    mDoneSize+=1;
//                    response=paths.get(child);
//                    if (null!=response&&response.getCode()==What.WHAT_SUCCEED){
//                        continue;
//                    }
//                    nextTask=child;
//                }
//            }
//            if (null!=nextTask){
//                response=onExecute(nextTask,task,callback);
//                response=null!=response?response:response(What.WHAT_ERROR);
//                paths.put(nextTask,response);
//                executeResult=(null==executeResult||(response.getCode()!=What.WHAT_SUCCEED))?response:executeResult;
//                continue;
//            }
//            return executeResult;
//        }
//    }
}
