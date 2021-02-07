package com.luckmerlin.task;

public interface TaskExecutor {
    TaskFuture submit(Runnable runnable);
}
