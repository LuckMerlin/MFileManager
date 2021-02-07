package com.luckmerlin.task;

import com.luckmerlin.task.Task;

public interface OnTaskUpdate {
    void onTaskUpdated(Task task, int status);
}
