package com.luckmerlin.task;

import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Task;

import java.util.List;

public interface Tasker {
    boolean register(OnTaskUpdate callback, Matchable matchable);
    boolean unregister(OnTaskUpdate callback);
    boolean action(int action,Object...tasks);
    List<Task> getTasks(Matchable matchable,int max);
}
