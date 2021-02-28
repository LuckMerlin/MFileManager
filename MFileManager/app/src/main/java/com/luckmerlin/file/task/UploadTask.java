package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalFolder;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.NasFolder;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.What;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;

import java.io.File;
import java.util.List;

public class UploadTask extends ActionFolderTask{

    public UploadTask(List<Path> paths, Folder folder) {
        super(paths,folder);
    }

    @Override
    protected Result onExecute(Path child, Task task, OnTaskUpdate callback) {
        Folder folder=getFolder();
        if (null==folder){
            return code(What.WHAT_ERROR);
        }else if (null==child){
            return code(What.WHAT_ERROR);
        }else if (child instanceof LocalPath){
           if (folder instanceof NasFolder){//Upload file into cloud
               return uploadFileToCloud((LocalPath) child,(NasFolder)folder);
           }
           return code(What.WHAT_NOT_SUPPORT);
        }
        return code(What.WHAT_NOT_SUPPORT);
    }

    private Result uploadFileToCloud(LocalPath localPath,NasFolder folder){
        if (null==localPath||null==folder){
            return code(What.WHAT_ERROR);
        }
        String localFile=localPath.getPath();
        File file=null!=localFile&&localFile.length()>0?new File(localFile):null;
        if (null==file||!file.exists()){
            return code(What.WHAT_NOT_EXIST);
        }else if (!file.canRead()){
            return code(What.WHAT_NONE_PERMISSION);
        }
//        final String hostPort=folder.get();
//        if (null==hostPort||hostPort.length()<=0){
//            Debug.D("Can't upload while file host port invalid.");
//            return null;
//        }

        return null;
    }
}
