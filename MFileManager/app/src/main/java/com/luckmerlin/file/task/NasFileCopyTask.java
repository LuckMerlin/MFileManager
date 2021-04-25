package com.luckmerlin.file.task;

import com.luckmerlin.file.Folder;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Response;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;

/**
 * @deprecated
 */
public class NasFileCopyTask extends Task {

    public NasFileCopyTask(NasPath localPath, Folder folder){

    }

    @Override
    protected Result onExecute(Task task, OnTaskUpdate callback) {
        return null;
    }

    //    @Override
//    protected Response onExecute(Task task, OnTaskUpdate callback) {
//        return null;
//    }
}
