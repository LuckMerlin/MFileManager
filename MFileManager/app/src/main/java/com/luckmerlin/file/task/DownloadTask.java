package com.luckmerlin.file.task;

import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.What;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Response;
import com.luckmerlin.task.Task;
import java.util.List;

public class DownloadTask extends ActionFolderTask{

    public DownloadTask(String name,List<Path> paths, Folder folder) {
        super(name,paths,folder);
    }

    @Override
    protected Response onExecute(Path child, Task task, OnTaskUpdate callback) {
        Folder folder=getFolder();
        Task childTask=null;
        if (null==folder){
            return response(What.WHAT_ERROR);
        }else if (null==child){
            return response(What.WHAT_ERROR);
        }else if (child instanceof NasPath){
            childTask= new NasFileCopyTask((NasPath)child,folder);
        }else if (child instanceof LocalPath){
            childTask= new LocalFileCopyTask((LocalPath)child,folder);
        }
        if (null==childTask){
            return response(What.WHAT_NOT_SUPPORT);
        }
        childTask.execute(callback);
        return childTask.getResponse();
    }
}
