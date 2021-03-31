package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalFolder;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.NasFolder;
import com.luckmerlin.file.api.What;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Response;
import com.luckmerlin.task.Task;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public final class LocalFileCopyTask extends Task {
    private final LocalPath mFrom;
    private final Folder mToFolder;
    private long mPerSecondSize=0;
    private int mCover= What.WHAT_INVALID;

    public LocalFileCopyTask(LocalPath from, Folder foFolder){
        mFrom=from;
        mToFolder=foFolder;
    }

    @Override
    protected Response onExecute(Task task, OnTaskUpdate callback) {
        Folder folder=mToFolder;
        if (null==folder){
            Debug.W("Can't copy local file while folder invalid.");
            return response(What.WHAT_ARGS_INVALID);
        }else if(folder instanceof LocalFolder||folder instanceof NasFolder){//Copy local file into local folder
            final LocalPath from=mFrom;
            if (null==folder||null==from){
                Debug.W("Can't copy local file to nas while local path or folder invalid.");
                return response(What.WHAT_ARGS_INVALID);
            }
            final String fromFilePath=from.getPath();
            if (null==fromFilePath||fromFilePath.length()<=0){
                Debug.W("Can't copy local file to nas while local path value invalid.");
                return response(What.WHAT_ARGS_INVALID);
            }
            final File fromFile=new File(fromFilePath);
            if (!fromFile.exists()){
                Debug.W("Can't copy local file while local file not exist.");
                return response(What.WHAT_NOT_EXIST);
            }else if (!fromFile.canRead()){
                Debug.W("Can't copy local file while local file NONE read permission.");
                return response(What.WHAT_NONE_PERMISSION);
            }
            FileInputStream inputStream=null;
            OutputStream outputStream=null;
            try {
                outputStream=createOutputStream(fromFile.getName(),folder);
                if (null==outputStream){
                    return null;
                }
                inputStream=new FileInputStream(fromFile);
            }catch (Exception e){
                //Do nothing
            }finally {
                mPerSecondSize = 0;
                new Closer().close(outputStream,inputStream);
            }
        }
        Debug.W("Can't copy local file while folder not support.");
        return response(What.WHAT_NOT_SUPPORT);
    }

    private OutputStream createOutputStream(String fileName,Folder folder) throws Exception {
        if (null!=fileName&&fileName.length()>0&&null!=folder){
            final String target=folder.getChildPath(fileName);
            if (null==target||target.length()<=0){
                return null;
            }else if (folder instanceof NasFolder){
//                final String localMd5=new MD5().getFileMD5(fromFile);

            }else if (folder instanceof LocalFolder){
                int cover=mCover;
                File file=new File(target);
                if (!file.exists()||cover==What.WHAT_REPLACE){
                    String parent=file.getParent();
                    File parentFile=null!=parent&&parent.length()>0?new File(parent):null;
                    if (null!=parentFile&&!parentFile.exists()){
                        parentFile.mkdirs();
                    }
                    if (!file.exists()){
                        file.createNewFile();
                    }
                    return new FileOutputStream(file);
                }
            }
        }
        return null;
    }
}
