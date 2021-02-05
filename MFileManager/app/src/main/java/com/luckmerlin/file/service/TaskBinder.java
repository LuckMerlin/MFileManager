package com.luckmerlin.file.service;

import android.os.Binder;
 class TaskBinder extends Binder implements Tasker{
     private final TaskService mTaskService;

     TaskBinder(TaskService service){
         mTaskService=service;
     }

     @Override
     public boolean register(OnTaskUpdate callback, Object... task) {
         TaskService taskService=mTaskService;
         return null!=taskService&&taskService.register(callback,task);
     }

     @Override
     public boolean unregister(OnTaskUpdate callback) {
         TaskService taskService=mTaskService;
         return null!=taskService&&taskService.unregister(callback);
     }
}
