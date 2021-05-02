package com.luckmerlin.task;

import android.os.Binder;

import com.luckmerlin.core.match.Matchable;

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
    public boolean action(int action, Object... tasks) {
        TaskService service=mTaskService;
        return null!=service&&service.action(action,tasks);
    }

    @Override
    public List<Task> getTasks(Matchable matchable, int max) {
        TaskService service=mTaskService;
        return null!=service?service.getTasks(matchable,max):null;
    }
}
