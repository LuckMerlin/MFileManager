package com.luckmerlin.task;

import com.luckmerlin.core.Canceler;

public interface ITask {
    Canceler execute(OnTaskStatusChange callback);
    Response getResult();
    boolean isDoing();
}
