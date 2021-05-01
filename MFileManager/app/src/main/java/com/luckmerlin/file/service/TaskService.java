package com.luckmerlin.file.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.core.match.Matcher;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Task;
import com.luckmerlin.task.TaskExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class TaskService extends Service implements Tasker{
    private final List<Task> mTasks=new ArrayList<>();
    private final Map<OnTaskUpdate,Matchable> mUpdateMaps=new WeakHashMap<>();
    private final DefaultTaskExecutor mExecutor=new DefaultTaskExecutor();
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final OnTaskUpdate mInnerUpdate=(Task task, int status)-> {
        Map<OnTaskUpdate,Matchable> updateMaps=mUpdateMaps;
        if (null!=updateMaps&&null!=task){
            final Handler handler=mHandler;
            synchronized (updateMaps){
                Set<OnTaskUpdate> set=updateMaps.keySet();
                if (null!=set){
                    Matchable matchable=null;Integer matched=null;
                    for (OnTaskUpdate child:set) {
                        if (null!=child&&(null==(matchable=updateMaps.get(child))||
                                null!=(matched=matchable.onMatch(task))&&matched==Matchable.MATCHED)){
                            if (child instanceof OnTaskSyncUpdate){
                                child.onTaskUpdated(task,status);
                            }else if (null!=handler){
                                handler.post(()->child.onTaskUpdated(task,status));
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return new TaskBinder(TaskService.this);
    }

    @Override
    public boolean startTask(Object task) {
        Task child=null;
        List<Task> list=mTasks;
        if (null!=task&&null!=list){
            if (task instanceof Collection){
                for (Object object:(Collection)task){
                    startTask(object);
                }
                return true;
            }
            synchronized (list){
                if (task instanceof Task){
                    if (!list.contains(child=(Task)task)){
                        list.add(child);
                    }
                }else{
                    int index=list.indexOf(task);
                    child=index>=0?list.get(index):null;
                }
            }
        }
        final TaskExecutor executor=mExecutor;
        if (null==executor){
            Debug.W("Can't start task while executor NULL.");
            return false;
        }
        final Task finalTask=child;
        if (null==finalTask){
            Debug.W("Can't start task while NONE task matched");
            return false;
        }else if (finalTask.isStarted()){
            Debug.W("Can't start task while task already started.");
            return false;
        }
        return null!=executor.submit(()-> { finalTask.execute(mInnerUpdate); });
    }

    @Override
    public boolean cancelTask(Object task){
        List<Task> list=mTasks;
        if (null==task||null==list){
            return false;
        }
        Task child=null;
        synchronized (list){
            int index=list.indexOf(task);
            child=index>=0?list.get(index):null;
        }
        return null!=child&&child.cancel(true);
    }

    @Override
    public boolean register(OnTaskUpdate callback,Matchable matchable) {
        final Map<OnTaskUpdate,Matchable> maps=null!=callback?mUpdateMaps:null;
        if (null!=maps){
            synchronized (maps){
                maps.put(callback,matchable);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean unregister(OnTaskUpdate callback) {
        final Map<OnTaskUpdate,Matchable> maps=null!=callback?mUpdateMaps:null;
        if (null!=maps){
            synchronized (maps){
                maps.remove(callback);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Task> getTasks(Matchable matchable, int max) {
        List<Task> list=mTasks;
        if (null!=list){
            List<Task> result=null;
            synchronized (list){
                int size=list.size();
                (result=new ArrayList<>(size)).addAll(list);
            }
            if (null!=result){
                return new Matcher().match(result,matchable,max<=0?Integer.MAX_VALUE:max);
            }
        }
        return null;
    }

    private final boolean notifyTaskUpdate(Task task, int status,OnTaskUpdate callback){
        if (null!=callback){
            callback.onTaskUpdated(task,status);
            return true;
        }
        return false;
    }
}
