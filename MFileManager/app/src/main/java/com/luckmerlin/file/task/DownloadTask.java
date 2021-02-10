package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.What;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadTask extends ActionFolderTask{

    public DownloadTask(List<Path> paths, Folder folder) {
        super(paths,folder);
    }

    @Override
    protected Result onExecute(Task task, OnTaskUpdate callback) {
        Folder folder=getFolder();
        if (null!=folder){
            final List<Path> paths=getPaths();
            if (null==paths||paths.size()<=0){//Empty tasks
                return null;
            }
            final Map<Path,Result> results=new HashMap<>();
            for (Path child:paths) {
                Task childTask=null;
                if (null==child){
                    continue;
                }else if (child instanceof NasPath){
                    childTask= new NasFileCopyTask((NasPath)child,folder);
                }else if (child instanceof LocalPath){
                    childTask= new LocalFileCopyTask((LocalPath)child,folder);
                }
                Result childResult=null;
                if (null!=childTask){
                    childTask.execute(callback);
                    childResult=childTask.getResult();
                    childResult=null!=childResult?childResult:new FileCodeResult(What.WHAT_ERROR);
                }else{
                    childResult=new FileCodeResult(What.WHAT_NOT_SUPPORT);
                }
                results.put(child,childResult);
            }
        }
        Debug.W("Can't execute action folder task while folder invalid.");
        return null;
    }
}
