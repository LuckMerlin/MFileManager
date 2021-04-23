package com.luckmerlin.file.task;

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
import com.luckmerlin.file.util.FileSize;
import com.luckmerlin.file.util.Time;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Response;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadTask extends FileTask<Path,Folder>{
    private boolean mDeleteAfterSucceed=false;
    private boolean mCheckMd5Existed=false;

    public UploadTask(){
        this(null,null);
    }

    public UploadTask(Path path, Folder folder){
        this(null,path,folder);
    }

    public UploadTask(String name,Path path, Folder folder) {
        super(name,path,folder);
    }

    public final UploadTask deleteSucceed(boolean enable) {
        this.mDeleteAfterSucceed = enable;
        return this;
    }

    @Override
    protected Response onExecute(Task task, OnTaskUpdate callback) {
        final Folder folder=getTo();
        final Path path=getFrom();
        if (null==folder){
            return response(What.WHAT_ERROR);
        }else if (null==folder){
            return response(What.WHAT_ERROR);
        }else if (path instanceof LocalPath){
            String localFile=path.getPath();
            File file=null!=localFile&&localFile.length()>0?new File(localFile):null;
            Response response=null;
           if (folder instanceof NasFolder){//Upload file into cloud
               response= uploadFileToCloud(file,(NasFolder)folder,null,callback);
           }
           if (null!=file&&file.isFile()&&mDeleteAfterSucceed&&null!=response&&response.getCode()==What.WHAT_SUCCEED){
               boolean succeed=file.delete();
               Debug.D("Delete upload succeed file."+succeed+" "+file);
           }
           return response;
        }
        return response(What.WHAT_NOT_SUPPORT);
    }

    private Response uploadFileToCloud(File file, NasFolder folder, String rootPath, OnTaskUpdate callback) {
        final String folderSep = null != folder ? folder.getSep() : null;
        final String folderHostUrl = null != folder ? folder.getHostUrl() : null;
        if (null == file || null == folder || null == folderSep || null == folderHostUrl || folderHostUrl.length() <= 0) {
            Debug.D("Can't upload file while args invalid.");
            return response(What.WHAT_ERROR);
        }
        if (null == file || !file.exists()) {
            Debug.D("Can't upload file while file not exist." + file);
            return response(What.WHAT_NOT_EXIST);
        } else if (!file.canRead()) {
            Debug.D("Can't upload file while none permission.");
            return response(What.WHAT_NONE_PERMISSION);
        }
        rootPath = null != rootPath && rootPath.length() > 0 ? rootPath : file.getParent();
        notifyTaskUpdate(Status.PREPARING, callback);
        final Nas nas = new Nas();
        final String filePath = file.getAbsolutePath();
        String namePath = null != rootPath ? filePath.replaceFirst(rootPath, "") : filePath;
        String targetPath = null != namePath ? namePath.replaceAll(File.separator, folderSep) : null;
        targetPath = null != targetPath ? folder.getChildPath(targetPath) : null;
        if (file.isDirectory()) {
            Reply<NasPath> folderReply = nas.createFile(folderHostUrl, true, targetPath);
            final int replyWhat = null != folderReply ? folderReply.getWhat() : What.WHAT_EXCEPTION;
            if (replyWhat != What.WHAT_SUCCEED && replyWhat != What.WHAT_ALREADY_DONE) {
                Debug.D("Can't upload file to cloud while create folder fail.");
                return response(What.WHAT_EXCEPTION);
            }
            File[] files = file.listFiles();
            int count = null != files ? files.length : -1;
            Response result = null;
            for (int i = 0; i < count; i++) {
                File childFile = files[i];
                if (null == childFile) {
                    continue;
                }else if (isCanceled()){
                    Debug.D("Canceled upload file.");
                    return response(What.WHAT_CANCEL);
                }
                Response childResult = uploadFileToCloud(childFile, folder, rootPath, callback);
                result = (null == childResult || !isResponseSucceed(childResult)) ? childResult : result;
            }
            return result;
        }
        //
        final Map<String, String> maps = new HashMap<>();
        final long fileLength = file.length();
        final String localMd5 = new MD5().getFileMD5(file);
        maps.put(Label.LABEL_PATH, null != targetPath ? targetPath : "");
        maps.put(Label.LABEL_MD5, null != localMd5 ? localMd5 : "");
        maps.put(Label.LABEL_MODE, mCheckMd5Existed? Label.LABEL_MD5:"");
        maps.put(Label.LABEL_LENGTH, Long.toString(fileLength));
        final FileProgress progress=new FileProgress();
        progress.mUploaded=0;progress.mTotal=fileLength;progress.mSpeed=0;progress.mTitle=file.getName();
        notifyTaskUpdate(Status.PREPARING, progress,callback);
        Reply<NasPath> existReply = nas.getNasFileData(folderHostUrl, maps);
        final int cover=getCover();
        long from = 0;
        if (null == existReply || !existReply.isSuccess()) {
            Debug.W("Can't upload file while fetch exist fail.");
            return response(What.WHAT_EXCEPTION);
        } else if (existReply.getWhat() == What.WHAT_SUCCEED) {//Already exist
            NasPath exist = existReply.getData();
            final long uploadLength=progress.mUploaded=(null!=exist?exist.getLength():0);
            notifyTaskUpdate(Status.PREPARING, progress,callback);
            if ((from = uploadLength) < 0) {
                Debug.W("Can't upload file while fetch exist length invalid.");
                return response(What.WHAT_ERROR);
            } else if (from >= fileLength&&cover!=What.WHAT_REPLACE) {
                String cloudMd5=exist.getMd5();
                if ((null!=cloudMd5&&null!=localMd5)||(null!=cloudMd5&&null!=localMd5&&cloudMd5.equals(localMd5))){
                    Debug.D("File already uploaded. "+ FileSize.formatSizeText(fileLength)+" " + filePath);
                    return response(What.WHAT_ALREADY_DONE);
                }
                Debug.D("File already uploaded but md5 not match." + filePath+"\n "+localMd5+" "+cloudMd5);
                return response(What.WHAT_FAIL);
            }
        }
        return nas.upload(file, folderHostUrl, targetPath, from, cover,localMd5,(long upload, long length,float speed) -> {
            progress.mSpeed=speed;progress.mUploaded=upload;progress.mTotal=length;
            notifyTaskUpdate(Status.EXECUTING, progress,callback);
            return super.isCanceled() ? true : null;
        }, null);
    }

    private static class FileProgress implements Progress{
        private long mUploaded;
        private long mTotal;
        private Object mTitle;
        private float mSpeed;

        @Override
        public Object getProgress(int type) {
            switch (type){
                case Progress.TYPE_DONE:
                    return mUploaded;
                case Progress.TYPE_SPEED:
                    return FileSize.formatSizeText(mSpeed)+"/s";
                case Progress.TYPE_TOTAL:
                    return mTotal;
                case Progress.TYPE_TITLE:
                    return mTitle;
                case Progress.TYPE_PERCENT:
                    long total=mTotal;
                    long upload=mUploaded;
                    return total>0?(upload<=0?0:upload)*100.f/total:0;
                case (Progress.TYPE_DONE|Progress.TYPE_TOTAL):
                    return FileSize.formatSizeText(mUploaded)+"/"+FileSize.formatSizeText(mTotal);
            }
            return null;
        }
    }

}
