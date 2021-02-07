package com.luckmerlin.file.service;

import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Task;

import java.util.List;

public interface Tasker {
    boolean register(OnTaskUpdate callback, Matchable matchable);
    boolean unregister(OnTaskUpdate callback);
    boolean startTask(Object task);
    boolean cancelTask(Object task,boolean interrupt);
    List<Task> getTasks(Matchable matchable,int max);
}
