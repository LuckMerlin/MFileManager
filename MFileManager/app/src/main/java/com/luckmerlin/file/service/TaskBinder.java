package com.luckmerlin.file.service;

import android.os.Binder;

import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Task;

import java.util.List;

public class TaskBinder extends Binder implements Tasker{
     private final TaskService mTaskService;

     TaskBinder(TaskService service){
         mTaskService=service;
     }

     @Override
     public boolean register(OnTaskUpdate callback, Matchable  matchable) {
         TaskService taskService=mTaskService;
         return null!=taskService&&taskService.register(callback,matchable);
     }

     @Override
     public boolean unregister(OnTaskUpdate callback) {
         TaskService taskService=mTaskService;
         return null!=taskService&&taskService.unregister(callback);
     }

    @Override
    public boolean startTask(Object task) {
        TaskService service=mTaskService;
        return null!=service&&service.startTask(task);
    }

    @Override
    public boolean cancelTask(Object task) {
        TaskService service=mTaskService;
        return null!=service&&service.cancelTask(task);
    }

    @Override
    public List<Task> getTasks(Matchable matchable, int max) {
        TaskService service=mTaskService;
        return null!=service?service.getTasks(matchable,max):null;
    }
}
