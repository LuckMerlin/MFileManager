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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadTask extends FileTask<Path,Folder>{
    private final Nas mNas=new Nas();
    private boolean mDeleteAfterSucceed=false;
    private boolean mCheckMd5Existed=false;
    private List<Prepared> mUploads;

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
        }else if (path instanceof LocalPath&&folder instanceof NasFolder){
            String localFile=path.getPath();
            File file=null!=localFile&&localFile.length()>0?new File(localFile):null;
            List<Prepared> uploads=mUploads;
            if (null==uploads){
                uploads=new ArrayList<>();
                notifyTaskUpdate(Status.PREPARING, callback);
                Response prepareResponse=prepare(file,(NasFolder)folder,null,uploads,callback);
                if (null==prepareResponse||prepareResponse.getCode()!=What.WHAT_SUCCEED){
                    Debug.W("Fail prepare upload files.");
                    return prepareResponse;
                }
                mUploads=uploads;
            }
            if (null==uploads||uploads.size()<=0){
                Debug.W("Empty files to upload.");
                return response(What.WHAT_EMPTY);
            }
            synchronized (uploads){
                for (Prepared upload:uploads) {
//                    upload.execute();
                }
            }
//            response= uploadFileToCloud(file,(NasFolder)folder,null,callback);
//           if (null!=file&&file.isFile()&&mDeleteAfterSucceed&&null!=response&&response.getCode()==What.WHAT_SUCCEED){
//               boolean succeed=file.delete();
//               Debug.D("Delete upload succeed file."+succeed+" "+file);
//           }
           return response(What.WHAT_SUCCEED);
        }
        return response(What.WHAT_NOT_SUPPORT);
    }

    private Response prepare(File file,NasFolder folder,String rootPath,List<Prepared> uploads,OnTaskUpdate callback){
        if (null!=file&&null!=folder){
            final String folderSep = null != folder ? folder.getSep() : null;
            final String folderHostUrl = null != folder ? folder.getHostUrl() : null;
            if (null == file || null == folder || null == folderSep || null == folderHostUrl || folderHostUrl.length() <= 0) {
                Debug.D("Can't upload file while args invalid.");
                return response(What.WHAT_ERROR);
            }else if (!file.exists()) {
                Debug.D("Can't upload file while file not exist." + file);
                return response(What.WHAT_NOT_EXIST);
            } else if (!file.canRead()) {
                Debug.D("Can't upload file while none permission.");
                return response(What.WHAT_NONE_PERMISSION);
            }
            rootPath = null != rootPath && rootPath.length() > 0 ? rootPath : file.getParent();
            final String filePath = file.getAbsolutePath();
            String namePath = null != rootPath ? filePath.replaceFirst(rootPath, "") : filePath;
            String targetPath = null != namePath ? namePath.replaceAll(File.separator, folderSep) : null;
            targetPath = null != targetPath ? folder.getChildPath(targetPath) : null;
            notifyTaskUpdate(Status.PREPARING, callback);
            uploads=null!=uploads?uploads:new ArrayList<>();
            final boolean directory=file.isDirectory();
            final String md5=directory||file.length()<=0?"":new MD5().getFileMD5(file);
            if (null==md5){
                Debug.W("Can't upload file while md5 invalid.");
                return response(What.WHAT_FAIL);
            }
            final Map<String,String> args=new HashMap<>();
            args.put(Label.LABEL_MD5,md5);
            args.put(Label.LABEL_PATH, targetPath);
            Reply<NasPath> nasReply=mNas.getNasFileData(folderHostUrl, args);
            final NasPath nasPath=null!=nasReply&&nasReply.getWhat()==What.WHAT_SUCCEED?nasReply.getData():null;
            final String nasMd5=null!=nasPath?nasPath.isDirectory()||namePath.length()<=0?"":nasPath.getMd5():null;
            if (null!=nasMd5&&nasMd5.equals(md5)){//Already upload
                return response(What.WHAT_ALREADY_DONE);
            }
            if (directory) {
                File[] files=file.listFiles();
                if (null==files||files.length<=0){
                    uploads.add(new Prepared(folderHostUrl,nasPath,md5,targetPath));
                    return response(What.WHAT_SUCCEED);
                }
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
                    Response childResult = prepare(childFile, folder, rootPath,uploads, callback);
                    result = (null == childResult || !isResponseSucceed(childResult)) ? childResult : result;
                    if ((result=null==result?response(What.WHAT_FAIL):result).getCode()!=What.WHAT_SUCCEED){
                        return result;
                    }
                }
                return result;
            }
            uploads.add(new Prepared(folderHostUrl,nasPath,md5,targetPath));
            return response(What.WHAT_SUCCEED);
        }
        return response(What.WHAT_FAIL);
    }

    private static class Prepared extends Task{

        Prepared(String folderHostUrl,NasPath done,String md5,String targetPath){

        }

        @Override
        protected Response onExecute(Task task, OnTaskUpdate callback) {
            return null;
        }
//        private final File mFromFile;
//        private final NasFolder mNasPath;
//
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
