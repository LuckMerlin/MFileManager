package com.luckmerlin.file.service;

public interface Tasker {
    boolean register(OnTaskUpdate callback,Object... task);
    boolean unregister(OnTaskUpdate callback);
    boolean start(Object ...tasks);
    boolean stop(Object ...tasks);
}
