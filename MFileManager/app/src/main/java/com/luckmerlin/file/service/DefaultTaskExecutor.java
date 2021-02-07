package com.luckmerlin.file.service;

import com.luckmerlin.task.Task;
import com.luckmerlin.task.TaskExecutor;
import com.luckmerlin.task.TaskFuture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DefaultTaskExecutor implements TaskExecutor {
    private final ExecutorService mScheduledExecutorService= Executors.newScheduledThreadPool(6);

    @Override
    public TaskFuture submit(Runnable runnable) {
        ExecutorService service=mScheduledExecutorService;
        final Future future=null!=runnable&&null!=service?service.submit(runnable):null;
        return null!=future?new TaskFuture() {
            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override
            public boolean cancel(boolean interrupt) {
                return future.cancel(interrupt);
            }

            @Override
            public boolean isDone() {
                return future.isDone();
            }
        }:null;
    }
}
