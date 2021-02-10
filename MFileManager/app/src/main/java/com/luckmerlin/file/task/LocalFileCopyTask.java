package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalFolder;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.NasFolder;
import com.luckmerlin.file.api.What;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;
import java.io.File;
import java.io.FileInputStream;

public final class LocalFileCopyTask extends Task {
    private final LocalPath mFrom;
    private final Folder mToFolder;
    private long mPerSecondSize=0;

    public LocalFileCopyTask(LocalPath from, Folder foFolder){
        mFrom=from;
        mToFolder=foFolder;
    }

    @Override
    protected Result onExecute(Task task, OnTaskUpdate callback) {
        Folder folder=mToFolder;
        if (null==folder){
            Debug.W("Can't copy local file while folder invalid.");
            return new FileCodeResult(What.WHAT_ARGS_INVALID,true);
        }else if(folder instanceof LocalFolder||folder instanceof NasFolder){//Copy local file into local folder
            final LocalPath from=mFrom;
            if (null==folder||null==from){
                Debug.W("Can't copy local file to nas while local path or folder invalid.");
                return new FileCodeResult(What.WHAT_ARGS_INVALID,true);
            }
            final String fromFilePath=from.getPath();
            if (null==fromFilePath||fromFilePath.length()<=0){
                Debug.W("Can't copy local file to nas while local path value invalid.");
                return new FileCodeResult(What.WHAT_ARGS_INVALID,true);
            }
            final File fromFile=new File(fromFilePath);
            if (!fromFile.exists()){
                Debug.W("Can't copy local file while local file not exist.");
                return new FileCodeResult(What.WHAT_NOT_EXIST,true);
            }else if (!fromFile.canRead()){
                Debug.W("Can't copy local file while local file NONE read permission.");
                return new FileCodeResult(What.WHAT_NONE_PERMISSION,true);
            }
            FileInputStream inputStream=null;
            try {
                inputStream=new FileInputStream(fromFile);
            }catch (Exception e){
                //Do nothing
            }finally {
                mPerSecondSize = 0;
                new Closer().close(outputStream,inputStream);
            }
            //            return copyLocalFileIntoLocalFolder(mFrom,(LocalFolder)folder,callback);
//        }else if (folder instanceof NasFolder){//Copy local file into nas folder
//            return copyLocalFileIntoNasFolder(mFrom,(NasFolder)folder,callback);
        }
        Debug.W("Can't copy local file while folder not support.");
        return new FileCodeResult(What.WHAT_NOT_SUPPORT,true);
    }

    private Result copyLocalFileIntoNasFolder(LocalPath from,NasFolder folder,OnTaskUpdate callback){
        if (null==folder||null==from){
            Debug.W("Can't copy local file to nas while local path or folder invalid.");
            return new FileCodeResult(What.WHAT_ARGS_INVALID,true);
        }
        final String fromFilePath=from.getPath();
        if (null==fromFilePath||fromFilePath.length()<=0){
            Debug.W("Can't copy local file to nas while local path value invalid.");
            return new FileCodeResult(What.WHAT_ARGS_INVALID,true);
        }
        final File fromFile=new File(fromFilePath);
        if (!fromFile.exists()){
            Debug.W("Can't copy local file while local file not exist.");
            return new FileCodeResult(What.WHAT_NOT_EXIST,true);
        }else if (!fromFile.canRead()){
            Debug.W("Can't copy local file while local file NONE read permission.");
            return new FileCodeResult(What.WHAT_NONE_PERMISSION,true);
        }

        return null;
    }
}
