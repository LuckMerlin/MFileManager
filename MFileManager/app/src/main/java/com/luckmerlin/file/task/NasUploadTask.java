package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.MD5;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Response;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class NasUploadTask extends FileTask<LocalPath,NasPath> {
    private long mPerSecondSize;

    public NasUploadTask(LocalPath from, NasPath to) {
        super(from, to);
    }

    @Override
    public long getPerSpeed() {
        return mPerSecondSize;
    }

    @Override
    protected Response onExecute(Task task, OnTaskUpdate callback) {
        final NasPath toPath=getTo();
        final LocalPath fromPath=getFrom();
        if (null==toPath||null==fromPath){
            Debug.D("Can't upload while path invalid.");
            return null;
        }
        final String toPathValue=toPath.getPath();
        final String fromUriPath=fromPath.getPath();
        if (null==toPathValue||null==fromUriPath){
            Debug.D("Can't upload while path value invalid."+toPath);
            return null;
        }
        final String hostPort=toPath.getHostPort();
        if (null==hostPort||hostPort.length()<=0){
            Debug.D("Can't upload while file host port invalid.");
            return null;
        }
        final File fromFile=new File(fromUriPath);
        if (!fromFile.exists()){
            Debug.D("Can't upload while file NOT EXIST."+fromUriPath);
            return null;
        }else if (!fromFile.canRead()){
            Debug.D("Can't upload while file NONE read permission.");
            return null;
        }
        notifyTaskUpdate(Status.PREPARING,callback);
        final long localLength=fromFile.length();
        HttpURLConnection connection = null;
        OutputStream outputStream=null;
        InputStream inputStream=null;
        try {
            final Map<String, String> maps = new HashMap<>();
            maps.put(Label.LABEL_PATH, toPathValue);
            final HttpURLConnection headConn = connection = createHttpConnect(hostPort, HEAD, maps);
            if (null == headConn) {
                Debug.W("Fail open connection for download target path.");
                return null;
            }
            final String localMd5=new MD5().getFileMD5(fromFile);
            headConn.setRequestProperty(Label.LABEL_LENGTH,Long.toString(localLength));
            headConn.setRequestProperty(Label.LABEL_MD5,null!=localMd5?localMd5:"");
            Debug.D("Prepared to upload file from "+localLength+" "+fromUriPath);
            headConn.connect();
            final int responseCode=headConn.getResponseCode();
            if (responseCode!=HttpURLConnection.HTTP_OK&&responseCode!=HttpURLConnection.HTTP_PARTIAL){
                Debug.W("Fail open connection response code invalid."+responseCode);
                return null;
            }
            final String contentType=headConn.getContentType();
            if (null==contentType||contentType.length()<=0||!contentType.contains("luckMerlin/file-data")){
                Debug.W("Can't upload file while server response content type invalid."+contentType);
                return null;
            }
            final long fileLength=string2Long(headConn.getHeaderField(Label.LABEL_LENGTH),-1);
            final String fileMd5 = headConn.getHeaderField(Label.LABEL_MD5);
            final String fileMime=headConn.getHeaderField(Label.LABEL_MIME);
            if (fileLength<0){
                Debug.W("Can't upload file while file length invalid."+contentType);
                return null;
            }
            Debug.D("Check upload file "+fileLength+" "+localLength);
            if (fileLength<localLength){
                //Check if need upload again
                new Closer().close(inputStream,outputStream);
                headConn.disconnect();
                final HttpURLConnection upConnect = connection = createHttpConnect(hostPort, POST, maps);
                if (null==upConnect){
                    Debug.W("Can't upload file while create upload connection NULL."+fromUriPath);
                    return null;
                }
                String BOUNDARY = "---------------------------41184676334";
                upConnect.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                upConnect.setRequestProperty("Content-disposition", "attachment; filename=\"dddd" +
                        "\"");
                upConnect.setDoOutput(true);
                upConnect.setDoInput(true);
                upConnect.setUseCaches(false);
                upConnect.setChunkedStreamingMode(1024);
                upConnect.setRequestProperty(Label.LABEL_LENGTH,Long.toString(localLength));
                upConnect.setRequestProperty(Label.LABEL_MD5,null!=localMd5?localMd5:"");
                upConnect.connect();
                final int uploadResponseCode=upConnect.getResponseCode();
                if (uploadResponseCode!=HttpURLConnection.HTTP_OK&&uploadResponseCode!=HttpURLConnection.HTTP_PARTIAL){
                    Debug.W("Fail open connection response code invalid."+uploadResponseCode);
                    return null;
                }
                final String uploadContentType=upConnect.getContentType();
                if (null==uploadContentType||uploadContentType.length()<=0||!uploadContentType.contains("luckMerlin/file-data")){
                    Debug.W("Can't upload file while server response content type invalid."+uploadContentType);
                    return null;
                }
                Debug.D("Uploading file."+localLength +" "+fromUriPath);
                mPerSecondSize=0;
                notifyTaskUpdate(Status.EXECUTING,callback);
                InputStream in=inputStream = new DataInputStream(new FileInputStream(fromFile));
                DataOutputStream out = new DataOutputStream(upConnect.getOutputStream());
                outputStream=out;
                final String NEWLINE = "\r\n";
                final String PREFIX = "--";
                out.writeBytes(PREFIX + BOUNDARY + NEWLINE);
                out.writeBytes("Content-Disposition: form-data; " + "name=\""
                        + "uploadFile" + "\"" + "; filename=\"" +"teset");
                out.writeBytes(NEWLINE);
                int readSize = 0;
                byte[] bufferOut = new byte[1024*1024];
                long lastTime=System.nanoTime();
                long uploaded=fileLength;
                double sec;
                in.skip(fileLength);
                while ((readSize = in.read(bufferOut)) >=0) {
                    if (readSize==0){
                        continue;
                    }
                    out.write(bufferOut, 0, readSize);
                    long currentTime=System.nanoTime();
                    uploaded += readSize;
                    if ((sec=(((double) (currentTime-lastTime))/(1000000000)))>0){
                        mPerSecondSize=(long)(readSize/sec);
                    }
                    notifyTaskUpdate(Status.EXECUTING,callback);
                    lastTime=currentTime;
                }
                out.writeBytes(NEWLINE);
                out.writeBytes(PREFIX + BOUNDARY + PREFIX + NEWLINE);
                out.flush();
                Debug.D("Finish upload file."+fromUriPath);

            }
            if (fileLength<localLength){
                Debug.W("Fail upload file."+fromUriPath);
                return null;//Upload fail
            }else if (fileLength==localLength&&((null==fileMd5&&null==localMd5)||(null!=localMd5&&
                    null!=fileMd5&&fileMd5.equalsIgnoreCase(localMd5)))){
                Debug.D("Succeed upload file."+fromUriPath);
                return null;
            }
            Debug.D("Error upload file  "+fileLength+" "+localLength+"\n"+fileMd5+" "+localMd5);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            mPerSecondSize = 0;
            new Closer().close(outputStream,inputStream);
            if (null!=connection){
                connection.disconnect();
            }
        }
        return null;
    }

}
