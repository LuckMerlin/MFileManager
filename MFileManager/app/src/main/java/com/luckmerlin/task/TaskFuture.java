package com.luckmerlin.task;

public interface TaskFuture {
    boolean isCancelled();

    boolean cancel(boolean interrupt);

    boolean isDone();
}
