package com.luckmerlin.file.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.luckmerlin.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TaskService extends Service implements Tasker{
    private final List<Task> mTasks=new ArrayList<>();
    private final Map<OnTaskUpdate,Object[]> mMaps=new HashMap<>();

    @Override
    public IBinder onBind(Intent intent) {
        return new TaskBinder(TaskService.this);
    }

    @Override
    public boolean start(Object... tasks) {

        return false;
    }

    @Override
    public boolean stop(Object... tasks) {
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
