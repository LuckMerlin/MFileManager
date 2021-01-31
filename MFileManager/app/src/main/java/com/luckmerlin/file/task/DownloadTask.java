package com.luckmerlin.file.task;

import com.luckmerlin.file.Path;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;

public final class DownloadTask extends AbstractFileTask {

    public DownloadTask(Path from,Path to){
        super(from,to);
    }

    @Override
    protected Result onExecute(Task task) {
        return null;
    }
}
