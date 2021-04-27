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
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Response;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated
 */
public class UploadTask extends FileTask<Path,Folder>{
    private final Nas mNas=new Nas();
    private boolean mDeleteAfterSucceed=false;
    private FileUploadProgress mUploadProgress;

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
    protected Result onExecute(Task task, OnTaskUpdate callback) {
        return null;
    }


    protected Response response(int d,Object de){
        return null;
    }

    protected Response response(int d){
        return null;
    }

    protected Response onExecuted(Task task, OnTaskUpdate callback) {
        final Folder folder=getTo();
        final Path path=getFrom();
        if (null==folder){
            return response(What.WHAT_ERROR);
        }else if (null==folder){
            return response(What.WHAT_ERROR);
        }else if (path instanceof LocalPath&&folder instanceof NasFolder){
            String localFile=path.getPath();
            File file=null!=localFile&&localFile.length()>0?new File(localFile):null;
            FileUploadProgress uploadProgress=mUploadProgress;
            if (null==uploadProgress){
                uploadProgress=new FileUploadProgress();
                Reply<NasPath> prepareResponse=prepare(file,(NasFolder)folder,null,uploadProgress,callback);
                final int code=null!=prepareResponse?prepareResponse.getWhat():What.WHAT_FAIL;
                if (code==What.WHAT_ALREADY_DONE){
                    return response(code,prepareResponse.getData());
                }else if (code!=What.WHAT_SUCCEED){
                    Debug.W("Fail prepare upload files.");
                    return response(What.WHAT_FAIL,"Prepare fail.");
                }
                mUploadProgress=uploadProgress;
            }
            final List<FileUpload> uploadList=uploadProgress.mFiles;
            if (null==uploadList||uploadList.size()<=0){
                Debug.W("Fail prepare upload files while NONE file need upload.");
                return response(What.WHAT_ALREADY_DONE,null);
            }
            Reply<NasPath> lastFailReply=null;
            for (FileUpload upload:uploadList) {
                Reply<NasPath> reply=null!=upload?upload.upload():null;
                reply=null!=reply?reply:new Reply<>(false,What.WHAT_FAIL,null,null);
                if (reply.getWhat()==What.WHAT_SUCCEED){
                    uploadProgress.put(upload,What.WHAT_SUCCEED);
                    continue;
                }
                lastFailReply=reply;
            }
            return null==lastFailReply||lastFailReply.getWhat()==What.WHAT_SUCCEED?
                    response(What.WHAT_ALREADY_DONE):response(What.WHAT_FAIL);
        }
        Debug.W("Fail prepare upload files while arg not support.");
        return response(What.WHAT_NOT_SUPPORT);
    }

    private Reply<NasPath> prepare(File file,NasFolder folder,String rootPath,FileUploadProgress progress,OnTaskUpdate callback){
        if (null==file||null==folder){
            Debug.W("Can't upload file while file or folder invalid.");
            return new Reply<>(true,What.WHAT_ARGS_INVALID,"File or folder invalid.",null);
        }
        final String folderSep = null != folder ? folder.getSep() : null;
        final String folderHostUrl = null != folder ? folder.getHostUrl() : null;
        rootPath = null != rootPath && rootPath.length() > 0 ? rootPath : file.getParent();
        final String filePath = file.getAbsolutePath();
        String namePath = null != rootPath ? filePath.replaceFirst(rootPath, "") : filePath;
        final String targetPath = null != namePath ? folder.getChildPath(namePath.replaceAll(File.separator, folderSep)): null;
        final String fileName=file.getName();
        final boolean directory=file.isDirectory();
        final FileUploadProgress finalProgress=progress=null!=progress?progress:new FileUploadProgress();
        progress.mTitle=fileName;progress.mThumb=file;
        notifyTaskUpdate(Status.PREPARING, progress,callback);
        String localMd5=null;
        Reply<NasPath> prepareReply=null;
        if (null==targetPath||targetPath.length()<=0){
            Debug.W("Can't upload file while target path invalid.");
            prepareReply=new Reply<>(true,What.WHAT_ERROR,"Target path invalid.",null);
        }else if (null == folderSep || null == folderHostUrl || folderHostUrl.length() <= 0) {
            Debug.D("Can't upload file while args invalid.");
            prepareReply=new Reply<>(true,What.WHAT_ERROR,"Args invalid.",null);
        }else if (!file.exists()) {
            Debug.D("Can't upload file while file not exist." + file);
            prepareReply= new Reply<>(true,What.WHAT_NOT_EXIST,"File not exist.",null);
        } else if (!file.canRead()) {
            Debug.D("Can't upload file while none permission.");
            prepareReply= new Reply<>(true,What.WHAT_NONE_PERMISSION,"None permission",null);
        }else{
            if (null==(localMd5=directory||file.length()<=0?"":new MD5().getFileMD5(file,null!=finalProgress? (long done, long total, float speed)-> {
                finalProgress.mSpeed=speed;finalProgress.mDone=done;finalProgress.mTotal=total;finalProgress.mTitle=fileName;
                notifyTaskUpdate(Status.PREPARING, finalProgress,callback);
                return !isCanceled();
            }:null))){
                Debug.W("Can't upload file while md5 invalid.");
                prepareReply = new Reply<>(true,What.WHAT_FAIL,"Md5 invalid.",null);
            }
        }
        final Map<String, String> args = new HashMap<>();
        final long fileLength = file.length();
        args.put(Label.LABEL_PATH, null != targetPath ? targetPath : "");
        args.put(Label.LABEL_MD5, null!=localMd5?localMd5:"");
        args.put(Label.LABEL_LENGTH, Long.toString(fileLength));
        NasPath nasPath=null;
        if (null==prepareReply){//Check upload history
            Reply<NasPath> nasReply=mNas.getNasFileData(folderHostUrl, args);
            nasPath=null!=nasReply&&nasReply.getWhat()==What.WHAT_SUCCEED?nasReply.getData():null;
            final String nasMd5=null!=nasPath?nasPath.isDirectory()||namePath.length()<=0?"":nasPath.getMd5():null;
            if (file.isFile()&&null!=localMd5&&null!=nasMd5&&nasMd5.equals(localMd5)){//Already upload
                Debug.D("Not need upload file while already uploaded.");
                prepareReply = new Reply<>(true,What.WHAT_ALREADY_DONE,"Already uploaded",nasPath);
            }else if (directory){
                File[] files=file.listFiles();
                int count = null != files ? files.length : -1;
                if (null==(prepareReply=(count<=0&&null!=namePath?new Reply<>(true,
                        What.WHAT_ALREADY_DONE,"Already uploaded",nasPath):null))){
                    for (int i = 0; i < count; i++) {
                        File childFile = files[i];
                        if (null == childFile) {
                            continue;
                        }else if (isCanceled()){
                            Debug.D("Canceled upload file.");
                            prepareReply = new Reply<>(true,What.WHAT_CANCEL,"Canceled",null);
                            break;
                        }
                        prepare(childFile, folder, rootPath,progress, callback);
                    }
                }
            }
        }
        prepareReply=null!=prepareReply?prepareReply:new Reply(true,What.WHAT_SUCCEED,null,null);
        final NasPath finalNasPath=nasPath;
        final String finalLocalMd5=localMd5;
        finalProgress.put(()-> {
            finalProgress.mDone=0;finalProgress.mTotal=fileLength;finalProgress.mSpeed=0;finalProgress.mTitle=fileName;
            long from = 0;
            int cover=What.WHAT_REPLACE;
            if (null!=finalNasPath){//Already exist
                final long uploadLength=finalProgress.mDone=finalNasPath.getLength();
                notifyTaskUpdate(Status.PREPARING, finalProgress,callback);
                if ((from = uploadLength) < 0) {
                    Debug.W("Can't upload file while fetch exist length invalid.");
                    return new Reply<>(true,What.WHAT_ERROR,"Fetch exist length invalid.",finalNasPath);
                } else if (from >= fileLength&&cover!=What.WHAT_REPLACE) {
                    String cloudMd5=finalNasPath.getMd5();
                    if ((null!=cloudMd5&&null!=finalLocalMd5)||(null!=cloudMd5&&null!=finalLocalMd5&&cloudMd5.equals(finalLocalMd5))){
                        Debug.D("File already uploaded. "+ FileSize.formatSizeText(fileLength)+" " + filePath);
                        return new Reply<>(true,What.WHAT_ALREADY_DONE,"File already uploaded.",finalNasPath);
                    }
                    Debug.D("File already uploaded but md5 not match." + filePath+"\n "+finalLocalMd5+" "+cloudMd5);
                    return new Reply<>(true,What.WHAT_ERROR,"File already uploaded but md5 not match",finalNasPath);
                }
            }
            return mNas.upload(file, folderHostUrl, targetPath, from, cover,finalLocalMd5,(long upload, long length,float speed) -> {
                finalProgress.mSpeed=speed;finalProgress.mDone=upload;finalProgress.mTotal=length;
                notifyTaskUpdate(Status.EXECUTING, finalProgress,callback);
                return super.isCanceled() ? true : null;
            }, "While upload file.");
        },prepareReply.getWhat()==What.WHAT_ALREADY_DONE?What.WHAT_ALREADY_DONE:What.WHAT_NORMAL);
        notifyTaskUpdate(Status.PREPARING, progress,callback);
        return prepareReply;
    }

    private interface FileUpload{
        Reply<NasPath> upload();
    }

    private class FileUploadProgress extends FileProgress{
        private final List<FileUpload> mDoneFiles=new ArrayList<>();
        private final List<FileUpload> mFiles=new ArrayList<>();
        private Object mThumb;

        public final FileProgress put(FileUpload value, int  what){
            if (null!=value){
                switch (what){
                    case What.WHAT_SUCCEED:
                    case What.WHAT_ALREADY_DONE:
                        if (!mDoneFiles.contains(value)){
                            mDoneFiles.add(value);
                        }
                        mFiles.remove(value);
                        break;
                    default:
                        mDoneFiles.remove(value);
                        if (!mFiles.contains(value)){
                            mFiles.add(value);
                        }
                        break;
                }
            }
            return this;
        }

        @Override
        public Object getProgress(int type) {
            switch (type){
                case Progress.TYPE_SUMMERY:
                    int doneSize=mDoneFiles.size();
                    int size=mFiles.size();
                    return (size<=0&&doneSize<=0)?null:doneSize+"/"+(size+doneSize);
                case Progress.TYPE_THUMB:
                    return mThumb;
            }
            return super.getProgress(type);
        }
    }

}
