package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.MD5;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.util.FileSize;
import com.luckmerlin.task.CodeResult;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public final class NasDownloadTask extends FileTask<NasPath,LocalPath> {
    private long mPerSecondSize;

    public NasDownloadTask(NasPath from, LocalPath to){
        super(from,to);
    }

    @Override
    public long getPerSpeed() {
        return mPerSecondSize;
    }

    @Override
    protected Result onExecute(Task task, OnTaskUpdate callback) {
        final LocalPath toPath=getTo();
        final NasPath fromPath=getFrom();
        if (null==toPath||null==fromPath){
            Debug.D("Can't download while path invalid.");
            return null;
        }
        final String toPathValue=toPath.getPath();
        final String fromUriPath=fromPath.getPath();
        if (null==toPathValue||null==fromUriPath){
            Debug.D("Can't download while path value invalid."+toPath);
            return null;
        }
        final String hostPort=fromPath.getHostPort();
        final File toFile=new File(toPathValue);
        final File parent=toFile.getParentFile();
        if (null==parent){
            Debug.D("Can't download while parent is NULL.");
            return null;
        }
        notifyTaskUpdate(Status.PREPARING,callback);
        OutputStream outputStream=null;
        InputStream inputStream=null;
        HttpURLConnection connection = null;
        String fileMd5=null;
        long fileLength=-1;
        String fileMime=null;
        try {
            final Map<String,String> maps=new HashMap<>();
            maps.put(Label.LABEL_PATH,fromUriPath);
            final HttpURLConnection conn=connection=createHttpConnect(hostPort,GET,maps);
            if (null==conn){
                Debug.W("Fail open connection for download target path.");
                return null;
            }
            final long localLength=toFile.exists()?toFile.length():0;
            conn.setRequestProperty(Label.LABEL_FROM,Long.toString(localLength));
            conn.setRequestProperty(Label.LABEL_SIZE,Long.toString(-1));
            Debug.D("Prepared to download file from "+localLength+" "+fromUriPath);
            conn.connect();
            final int responseCode=conn.getResponseCode();
            if (responseCode!=HttpURLConnection.HTTP_OK&&responseCode!=HttpURLConnection.HTTP_PARTIAL){
                Debug.W("Fail open connection response code invalid."+responseCode);
                return null;
            }
            final String contentType=conn.getContentType();
            if (null==contentType||contentType.length()<=0||!contentType.contains("luckMerlin/file-data")){
                Debug.W("Can't download file while server response content type invalid."+contentType);
                return null;
            }
            fileMd5 = conn.getHeaderField(Label.LABEL_MD5);
            fileMime=conn.getHeaderField(Label.LABEL_MIME);
            fileLength= string2Long(conn.getHeaderField(Label.LABEL_LENGTH),-1);
            if (fileLength<0){
                Debug.W("Can't download file while response file md5 or length invalid."+
                        conn.getHeaderField(Label.LABEL_LENGTH)+" "+fileMd5);
                return null;
            }
            if (!toFile.exists()){
                if (!parent.exists()) {//Make parent directory
                    Debug.D("Create download file parent directory. "+parent);
                    parent.mkdirs();
                }
                if (parent.exists()){
                    Debug.D("Create download file. "+toFile);
                    toFile.createNewFile();
                }
            }
            if (!toFile.exists()){
                Debug.D("Fail download file which create file fail. "+toFile);
                return null;
            }
            if ((null==fileMd5||fileMd5.length()<=0)&&fileLength==0){//Check if empty file
                Debug.D("Downloaded empty file."+toFile);
                return new FileCodeResult(What.WHAT_SUCCEED);
            }else if (localLength>fileLength){

            }
            if (!toFile.canWrite()){
                Debug.D("Fail download file which target path NONE permission. "+toFile);
                return null;
            }
            final InputStream input=inputStream= conn.getInputStream();
            if (null==input){
                Debug.W("Stream is NULL.");
                return null;
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(input);
            Debug.D("Downloading file "+localLength+" "+contentType+" "+toPath);
            notifyTaskUpdate(Status.EXECUTING,callback);
            OutputStream out =outputStream= new FileOutputStream(toFile,localLength>0);
            int size=0;
            final byte[] buf = new byte[1024*1024];
            long lastTime=System.nanoTime();
            double sec;
            while ((size = bufferedInputStream.read(buf)) >=0) {
                if (size>0){
                    out.write(buf, 0, size);
                    long currentTime=System.nanoTime();
                    if ((sec=(((double) (currentTime-lastTime))/(1000000000)))>0){
                        mPerSecondSize=(long)(size/sec);
                    }
                    notifyTaskUpdate(Status.EXECUTING,callback);
                    lastTime=currentTime;
                }
                if (isCanceled()){
                    Debug.D("Canceled download file."+toPath);
                    break;
                }
            }
            out.flush();
            Debug.D("Finish download nas file."+toFile);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            mPerSecondSize=0;
            new Closer().close(outputStream,inputStream);
            if (null!=connection){
                connection.disconnect();
            }
            String localFileMD5=null;
            final long localFileLength=null!=toFile?toFile.length():-1;
            if (null!=fileMd5&&localFileLength==fileLength){
                localFileMD5=new MD5().getFileMD5(toFile);
                if (null!=localFileMD5&&localFileMD5.equalsIgnoreCase(fileMd5)){
                    Debug.D("Succeed download nas file."+ FileSize.formatSizeText(fileLength)+" "+fileMime +" "+toFile);
                    return new FileCodeResult(What.WHAT_SUCCEED);
                }
            }
            Debug.W("Fail download file task."+fileLength+" "+localFileLength+" "+toPath+"\n"+ localFileMD5+"\n"+fileMd5);  //Download fail
            if (!isEnableBreakpoint()&&null!=toFile&&toFile.exists()){//Delete fail file
                Debug.D("Delete download fail file."+toFile.length()+" "+toPath);
                toFile.delete();
            }
        }
        return null;
    }

}
