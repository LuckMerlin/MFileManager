package com.luckmerlin.file.service;

public interface Tasker {
    boolean register(OnTaskUpdate callback,Object... task);
    boolean unregister(OnTaskUpdate callback);
    boolean startTask(Object task);
    boolean stopTask(Object task);
}
