package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.Path;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;

import java.util.List;

public class DownloadTask extends ActionFolderTask{

    public DownloadTask(List<Path> paths, Folder folder) {
        super(paths,folder);
    }

    @Override
    protected Result onExecute(Task task, OnTaskUpdate callback) {
        Debug.D("SSSSSSSSSSSSSS "+task+" "+callback);
        return null;
    }
}
