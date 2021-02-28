package com.luckmerlin.file.task;

import com.google.gson.Gson;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.MD5;
import com.luckmerlin.file.NasFolder;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.nas.Nas;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
               String localFile=((LocalPath)child).getPath();
               File file=null!=localFile&&localFile.length()>0?new File(localFile):null;
               return uploadFileToCloud(file,(NasFolder)folder,null,callback);
           }
           return code(What.WHAT_NOT_SUPPORT);
        }
        return code(What.WHAT_NOT_SUPPORT);
    }

    private Result uploadFileToCloud(File file,NasFolder folder,String rootPath,OnTaskUpdate callback){
        final String folderSep=null!=folder?folder.getSep():null;
        final String folderHostUrl=null!=folder?folder.getHostUrl():null;
        if (null==file||null==folder||null==folderSep||null==folderHostUrl||folderHostUrl.length()<=0){
            Debug.D("Can't upload file while args invalid.");
            return code(What.WHAT_ERROR);
        }
        if (null==file||!file.exists()){
            Debug.D("Can't upload file while file not exist.");
            return code(What.WHAT_NOT_EXIST);
        }else if (!file.canRead()){
            Debug.D("Can't upload file while none permission.");
            return code(What.WHAT_NONE_PERMISSION);
        }
        notifyTaskUpdate(Status.PREPARING,callback);
        if (file.isDirectory()){
            File[] files=file.listFiles();
            int count=null!=files?files.length:-1;
            Result result=null;
            for (int i = 0; i < count; i++) {
                File childFile=files[i];
                if (null==childFile){
                    continue;
                }
                Result childResult=uploadFileToCloud(childFile,folder,null!=rootPath
                        &&rootPath.length()>0?rootPath:file.getParent(),callback);
                result=null!=childResult&&!isResultSucceed(childResult)?childResult:result;
            }
            Debug.D("EEEEEEEEEEEE ddd ");
            return result;
        }
        final String filePath=file.getAbsolutePath();
        String namePath=null!=rootPath?filePath.replaceFirst(rootPath,""):filePath;
        String targetPath=null!=namePath?namePath.replaceAll(File.separator,folderSep):null;
        targetPath=null!=targetPath?folder.getChildPath(targetPath):null;
        //
        final Map<String,String> maps=new HashMap<>();
        final long fileLength=file.length();
//        final String localMd5=new MD5().getFileMD5(file);
        maps.put(Label.LABEL_PATH, targetPath);
        final Reply<NasPath> reply=new Nas().getNasFileData(folderHostUrl,targetPath);
        Debug.D("EEEEEEEEEEEE "+reply);
        if (null==reply){

        }
//        long existLength=null!=existNas?existNas.getLength():-1;
//        if (existLength>=fileLength){
//            Debug.D("File already uploaded."+file);
//            return code(What.WHAT_ALREADY_DONE);
//        }
//        Debug.D("开始上传 "+file+" "+);
//        final HttpURLConnection headConn = createHttpConnect(hostPort, HEAD, maps);
//        if (null == headConn) {
//            Debug.W("Fail open connection for download target path.");
//            return null;
//        }
//        headConn.setRequestProperty(Label.LABEL_LENGTH,Long.toString(localLength));
//        headConn.setRequestProperty(Label.LABEL_MD5,null!=localMd5?localMd5:"");
//        Debug.D("Prepared to upload file from "+localLength+" "+fromUriPath);
//        headConn.connect();
//        final String hostPort=folder.get();
//        if (null==hostPort||hostPort.length()<=0){
//            Debug.D("Can't upload while file host port invalid.");
//            return null;
//        }
        return null;
    }
}
