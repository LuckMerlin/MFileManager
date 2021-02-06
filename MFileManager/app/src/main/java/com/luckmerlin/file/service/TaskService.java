package com.luckmerlin.file.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class TaskService extends Service implements Tasker{
    private final List<Task> mTasks=new ArrayList<>();
    private final Map<OnTaskUpdate,Object[]> mMaps=new HashMap<>();
    private final ExecutorService mExecutor=Executors.newScheduledThreadPool(5);

    @Override
    public IBinder onBind(Intent intent) {
        return new TaskBinder(TaskService.this);
    }

    @Override
    public boolean startTask(Object task) {
        Task child=null;
        List<Task> list=mTasks;
        if (null!=task&&null!=list){
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
        final Task finalTask=child;
        if (null!=finalTask){
            final ExecutorService executor=null!=child?mExecutor:null;
            Future future=executor.submit(()->{
                Result result= finalTask.execute(future);
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean stopTask(Object task) {

        return false;
    }

    @Override
    public boolean register(OnTaskUpdate callback, Object... task) {
        final Map<OnTaskUpdate,Object[]> maps=null!=callback?mMaps:null;
        if (null!=maps){
            synchronized (maps){
                maps.put(callback,task);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean unregister(OnTaskUpdate callback) {
        final Map<OnTaskUpdate,Object[]> maps=null!=callback?mMaps:null;
        if (null!=maps){
            synchronized (maps){
                maps.remove(callback);
            }
            return true;
        }
        return false;
    }

}
