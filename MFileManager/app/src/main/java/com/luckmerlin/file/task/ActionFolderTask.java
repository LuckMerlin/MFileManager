package com.luckmerlin.file.task;

import com.luckmerlin.file.Folder;
import com.luckmerlin.file.Path;

import java.util.Collection;
import java.util.List;

/**
 * @deprecated
 */
public abstract class ActionFolderTask extends FileGroupTask {
    private final Folder mFolder;

    public ActionFolderTask(){
        this(null);
    }

    public ActionFolderTask(String name){
        this(name,null,null);
    }

    public ActionFolderTask(String name,List<Path> paths, Folder folder){
       super(name,paths);
        mFolder=folder;
    }

    public final boolean isAllInSameFolder(){
        Folder folder=mFolder;
        String folderPath=null!=folder?folder.getPath():null;
        String folderDivider=null!=folder?folder.getSep():null;
        folderPath=null!=folderDivider&&null!=folderPath&&!folderPath.endsWith(folderDivider)?folderPath+folderDivider:folderPath;
        Collection<Path> paths=getTasks();
        if (null!=paths&&null!=folderPath){
            for (Path child:paths) {
                String childParent=null!=child?child.getParent():null;
                String childDivider=null!=child?child.getSep():null;
                childParent=null!=childParent&&null!=childDivider&&!childParent.endsWith(childDivider)?childParent+childDivider:childParent;
                if (null!=childParent&&!childParent.equals(folderPath)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public final Folder getFolder() {
        return mFolder;
    }
}
