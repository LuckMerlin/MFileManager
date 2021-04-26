package com.luckmerlin.file.task;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.renderscript.ScriptGroup;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.MD5;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.nas.Nas;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class UriStreamTask extends FromToTask<Uri, Uri> {
    private final WeakReference<Context> mContext;
    private boolean mRecheck=false;
    private boolean mDeleteFail=false;

    public UriStreamTask(Context context,Uri from, Uri to) {
        super(from, to);
        mContext=null!=context?new WeakReference<>(context):null;
    }

    public final UriStreamTask enableRecheck(boolean enable) {
        this.mRecheck = enable;
        return this;
    }

    public final UriStreamTask enableDeleteFail(boolean deleteFail) {
        mDeleteFail = deleteFail;
        return this;
    }

    @Override
    final Result onExecute(Uri from, Uri to, Task task, OnTaskUpdate callback) {
        if (null==from||null==to){
            Debug.W("Can't execute Uri stream task while args invalid.");
            return new CodeResult(What.WHAT_ARGS_INVALID);
        }
        InputStream inputStream=null;OutputStream outputStream=null;
        StreamOpener<CodeResult<OutputStream>> deleteFailOpener=null;
        try {
            StreamOpener<CodeResult<OutputStream>> outputOpener=deleteFailOpener=createOutputStream(to);
            int outputCode=null!=outputOpener?outputOpener.mCode:What.WHAT_FAIL;
            if (outputCode!=What.WHAT_SUCCEED){
                Debug.W("Fail execute uri stream task while TO stream opener create fail.");
                return new CodeResult(outputCode);
            }
            StreamOpener<CodeResult<InputStream>> inputOpener=createInputStream(from);
            int inputCode=null!=inputOpener?inputOpener.mCode:What.WHAT_FAIL;
            if (inputCode!=What.WHAT_SUCCEED){
                Debug.W("Fail execute uri stream task while FROM stream opener create fail.");
                return new CodeResult(inputCode);
            }
            final long inputLength=inputOpener.mLength;
            final long outputLength=outputOpener.mLength;
            if (outputLength<0||inputLength<0){
                Debug.W("Fail execute uri stream task while FROM or TO's length invalid."+outputLength+" "+inputLength);
                return new CodeResult(What.WHAT_FAIL);
            }else if (outputLength>inputLength){//Already length match
                Debug.W("Fail execute uri stream task while TO's length already larger than FROM.");
                return new CodeResult(What.WHAT_ERROR);
            }else if (outputLength==inputLength){//Already
                if (checkIfAlreadyDone(inputOpener,outputOpener)){
                    Debug.W("Uri stream already done.");
                    return new CodeResult(What.WHAT_ALREADY_DONE);
                }
                Debug.W("Fail execute uri stream task while TO's length already match FROM but md5 not match.");
                return new CodeResult(What.WHAT_ERROR);
            }
            CodeResult<InputStream> inputStreamResult=inputOpener.open(outputLength);
            inputCode=null!=inputStreamResult?inputStreamResult.getCode():What.WHAT_FAIL;
            inputStream=null!=inputStreamResult?inputStreamResult.getArg():null;
            if (inputCode!=What.WHAT_SUCCEED||null==inputStream){
                Debug.W("Fail execute uri stream task while FROM opener open fail."+inputCode);
                return new CodeResult(outputCode);
            }
            CodeResult<OutputStream> outputStreamResult=outputOpener.open(outputLength);
            outputStream=null!=outputStreamResult?outputStreamResult.getArg():null;
            outputCode=null!=outputStreamResult?outputStreamResult.getCode():What.WHAT_FAIL;
            if (outputCode!=What.WHAT_SUCCEED||null==outputStreamResult){
                Debug.W("Fail execute uri stream task while TO opener open fail."+outputCode);
                return new CodeResult(outputCode);
            }
            byte[] buffer=new byte[1024*1024];
            int read=0;long uploaded=0;float speed;
            long startTime=System.nanoTime();
            while ((read=inputStream.read(buffer))>=0){
                uploaded += read;
                if (isCanceled()) {
                    return new CodeResult<>(What.WHAT_CANCEL);
                } else if (read>0) {
                    outputStream.write(buffer,0,read);
                    if ((startTime = startTime > 0 ? System.nanoTime() - startTime : -1) > 0) {
                        startTime = TimeUnit.NANOSECONDS.toMillis(startTime);
                        speed = startTime > 0 ? read / startTime : 0;
                    }
                }
            }
            OutputStream temp=outputStream;
            outputStream=null;
            new Closer().close(temp);
            if (mRecheck){//Recheck again
//

            }
            deleteFailOpener=null;//Clean delete fail while succeed
            Debug.D("Succeed done uri stream."+to);
            return new CodeResult<>(What.WHAT_SUCCEED);
        } catch (Exception e) {
            Debug.W("Can't execute Uri stream task while exception."+e);
            e.printStackTrace();
            return new CodeResult(What.WHAT_EXCEPTION);
        }finally {
            if (null!=deleteFailOpener){
                CodeResult result=deleteFailOpener.delete();
                if (null==result||result.getCode()!=What.WHAT_SUCCEED){
                    Debug.W("Fail to delete not success uri stream.");
                }
            }
            new Closer().close(inputStream,outputStream);
        }
    }

    private boolean checkIfAlreadyDone(StreamOpener<CodeResult<InputStream>> inputOpener, StreamOpener<CodeResult<OutputStream>> outputOpener){
       if (null!=inputOpener&&null!=outputOpener){
           String inputMd5=inputOpener.mMd5;
           String outputMd5=outputOpener.mMd5;
           if (null!=inputMd5&&null!=outputMd5&&inputMd5.equals(outputMd5)){
               return true;
           }
       }
       return false;
    }

    protected StreamOpener<CodeResult<OutputStream>> createOutputStream(Uri uri) throws Exception {
        String scheme=uri.getScheme();
        scheme=null!=scheme?scheme.toLowerCase():null;
        if (null==scheme||scheme.length()<=0){
            Debug.W("Can't create Uri outputStream while uri scheme invalid.");
            return new StreamOpener(What.WHAT_ARGS_INVALID);
        }else if (scheme.equals(ContentResolver.SCHEME_FILE)){
            String path=uri.getPath();
            File file = null!=path&&path.length()>0?new File(path):null;
            if (null==file){
                Debug.W("Can't create Uri outputStream while from invalid.");
                return new StreamOpener(What.WHAT_ARGS_INVALID);
            }
            return new StreamOpener(What.WHAT_SUCCEED,file.length(),new MD5().getFileMD5(file)){
                @Override
                CodeResult<OutputStream> open(long seek) throws Exception{
                    if (!file.exists()){
                        File parent=file.getParentFile();
                        if (null!=parent&&!parent.exists()){
                            parent.mkdirs();
                        }
                        file.createNewFile();
                        if (!file.exists()){
                            Debug.W("Can't create outputStream while create file fail.");
                            return new CodeResult<>(What.WHAT_CREATE_FAILED);
                        }
                        Debug.D("Create file while task open uri stream."+file);
                    }
                    long length=file.length();
                    if (seek>0&&seek!=length){
                        Debug.W("Can't create outputStream while seek not match.");
                        return new CodeResult<>(What.WHAT_FAIL);
                    }
                    return new CodeResult<>(What.WHAT_SUCCEED,new FileOutputStream(file,seek>0));
                }

                @Override
                public CodeResult delete() {
                    return new CodeResult((!file.exists()||file.delete())&&!file.exists()?What.WHAT_SUCCEED:What.WHAT_FAIL);
                }
            };
        }else if (scheme.startsWith("http")){
//            final String finalScheme=null!=scheme?scheme.toLowerCase():null;
            final String host=uri.getHost();
            final int port=uri.getPort();
            final String hostUri=null!=host?scheme+"://"+host+":"+port:null;
            if (null==hostUri||hostUri.length()<=0){
                Debug.W("Can't create Uri outputStream while TO's host uri invalid.");
                return new StreamOpener(What.WHAT_ERROR);
            }
            final Map args=new HashMap();
            final String path=uri.getQueryParameter(Label.LABEL_PATH);
            args.put(Label.LABEL_PATH,null!=path?path:"");
            final Nas nas=new Nas();
            final Reply<NasPath> reply=nas.getNasFileData(hostUri,args);
            if (null==reply||!reply.isSuccess()){
                Debug.W("Can't create Uri outputStream while fet uri reply fail.");
                return new StreamOpener(What.WHAT_ERROR);
            }
            final NasPath existPath=null!=reply?reply.getData():null;
            final long existLength=null!=existPath?existPath.getLength():0;
            return new StreamOpener(What.WHAT_SUCCEED,existLength, null!=existPath?existPath.getMd5():null){
                @Override
                CodeResult<OutputStream> open(long seek) throws Exception{
                    if (seek>0&&seek!=existLength){
                        Debug.W("Can't create outputStream while seek not match.");
                        return new CodeResult<>(What.WHAT_FAIL);
                    }
                    final URL url = new URL(hostUri+"/file/save");
                    HttpURLConnection urlConnection=null!=url? (HttpURLConnection) url.openConnection() :null;
                    if (null==urlConnection){
                        Debug.W("Can't open outputStream while connect fail.");
                        return new CodeResult<>(What.WHAT_FAIL);
                    }
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(500000);
                    urlConnection.setDefaultUseCaches(false);
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setRequestProperty("Charset", "UTF-8");
                    final OutputStream outputStream=urlConnection.getOutputStream();
                    if (null==outputStream){
                        Debug.W("Can't open outputStream while connect outputStream NULL.");
                        return new CodeResult<>(What.WHAT_FAIL);
                    }
                    return new CodeResult<>(What.WHAT_SUCCEED,new OutputStream(){

                        @Override
                        public void write(byte[] b, int off, int len) throws IOException {
                            outputStream.write(b,off,len);
                        }

                        @Override
                        public void write(byte[] b) throws IOException {
                            outputStream.write(b);
                        }

                        @Override
                        public void write(int i) throws IOException {
                            outputStream.write(i);
                        }

                        @Override
                        public void close() throws IOException {
                            InputStream inputStream=null;
                            try {
                                int code=urlConnection.getResponseCode();
                                Debug.D("DDdd  DDDDDd "+code);
                                inputStream=urlConnection.getInputStream();
                                if (null!=inputStream){
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                    StringBuffer stringBuffer = new StringBuffer();
                                    String tempStr;
                                    while ((tempStr = bufferedReader.readLine()) != null) {
                                        tempStr = new String(tempStr.getBytes("UTF-8"));
                                        stringBuffer.append(tempStr);
                                    }
                                    Debug.D("DDDDDDDd "+stringBuffer);
                                }
                            }catch (Exception e){
                                Debug.D("DDDDDDDd "+e);
                                e.printStackTrace();
                            }
                            new Closer().close(outputStream,inputStream);
                            urlConnection.disconnect();
                        }
                    });
                }

                @Override
                public CodeResult delete() {
                    Reply<NasPath> deleteReply=nas.deleteFile(hostUri,path);
                    return new CodeResult(null!=deleteReply?deleteReply.getWhat():What.WHAT_FAIL);
                }
            };
        }
        Debug.W("Can't create Uri outputStream while scheme not support."+scheme);
        return new StreamOpener(What.WHAT_NOT_SUPPORT);
    }

    protected StreamOpener<CodeResult<InputStream>> createInputStream(Uri uri) throws Exception {
        String scheme=uri.getScheme();
        scheme=null!=scheme?scheme.toLowerCase():null;
        if (null==scheme||scheme.length()<=0){
            Debug.W("Can't create Uri inputStream while uri scheme invalid.");
            return new StreamOpener(What.WHAT_ARGS_INVALID);
        }else if (scheme.equals(ContentResolver.SCHEME_FILE)){
            String path=uri.getPath();
            File file = null!=path&&path.length()>0?new File(path):null;
            if (null==file||!file.exists()){
                Debug.W("Can't create Uri inputStream while from invalid.");
                return new StreamOpener(What.WHAT_NOT_EXIST);
            }else if (!file.canRead()){
                Debug.W("Can't create Uri inputStream while from NONE permission.");
                return new StreamOpener(What.WHAT_NONE_PERMISSION);
            }else if (!file.isFile()){
                Debug.W("Can't create Uri inputStream while from is not file.");
                return new StreamOpener(What.WHAT_NOT_FILE);
            }
            return new StreamOpener(What.WHAT_SUCCEED,file.length(),new MD5().getFileMD5(file)){
                @Override
                CodeResult<InputStream> open(long seek) throws Exception {
                    if (seek<0){
                        Debug.W("Can't open Uri inputStream while seek invalid."+seek);
                        return new CodeResult(What.WHAT_FAIL);
                    }
                    InputStream inputStream=new FileInputStream(file);
                    if (seek>0&&inputStream.skip(seek)!=seek){
                        Debug.W("Can't open Uri inputStream while seek fail."+seek);
                        new Closer().close(inputStream);
                        return new CodeResult<>(What.WHAT_FAIL);
                    }
                    return new CodeResult<>(What.WHAT_SUCCEED,inputStream);
                }
            };
        }else if (scheme.equals(ContentResolver.SCHEME_CONTENT)){
            Context context=getContext();
            final ContentResolver resolver = null!=context?context.getContentResolver():null;
            if (null==resolver){
                Debug.W("Can't create Uri inputStream while resolver is NULL.");
                return new StreamOpener(What.WHAT_FAIL);
            }
            return new StreamOpener(What.WHAT_SUCCEED){
                @Override
                CodeResult<InputStream> open(long seek) throws Exception {
                    if (seek<0){
                        Debug.W("Can't open Uri inputStream while seek invalid."+seek);
                        return new CodeResult<>(What.WHAT_FAIL);
                    }
                    InputStream inputStream=resolver.openInputStream(uri);
                    if (null!=inputStream&&seek>0&&inputStream.skip(seek)!=seek){
                        Debug.W("Can't open Uri inputStream while seek fail."+seek);
                        new Closer().close(inputStream);
                        return new CodeResult<>(What.WHAT_FAIL);
                    }
                    return new CodeResult<>(What.WHAT_SUCCEED,inputStream);
                }
            };
        }else if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)){
//            Context context=getContext();
//            Resources resources=null!=context?context.getResources():null;
//            resources.op
        }else if (scheme.startsWith("http")){
//            return
        }
        Debug.W("Can't create Uri inputStream while scheme not support."+scheme);
        return new StreamOpener(What.WHAT_NOT_SUPPORT);
    }

    public final Context getContext(){
        WeakReference<Context> reference=mContext;
        return null!=reference?reference.get():null;
    }

    private static class StreamOpener<T>{
        private final int mCode;
        private final String mMd5;
        private final long mLength;

        public StreamOpener(int code) {
            this(code,-1,null);
        }

        public StreamOpener(int code, long length,String md5) {
            mCode=code;
            mLength=length;
            mMd5=md5;
        }

        T open(long seek) throws Exception{
            return null;
        }

        public CodeResult delete() {
            return null;
        }
    }

}
