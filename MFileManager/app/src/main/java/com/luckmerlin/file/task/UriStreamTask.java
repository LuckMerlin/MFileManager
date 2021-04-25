package com.luckmerlin.file.task;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
            int read=0;long mUploaded=0;float speed;
            long startTime=System.nanoTime();
            while ((read=inputStream.read(buffer))>=0){
                mUploaded += read;
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
            if (mRecheck){//Recheck again
                outputOpener=deleteFailOpener=createOutputStream(to);//Create output stream again to recheck
                if (null==outputOpener||!checkIfAlreadyDone(inputOpener,outputOpener)||outputOpener.mLength!=inputOpener.mLength){
                    Debug.W("Fail match just done uri stream."+to);
                    return new CodeResult<>(What.WHAT_MATCH_FAIL);
                }
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
                        return null;
                    }
                    return new CodeResult<>(What.WHAT_SUCCEED,new FileOutputStream(file,seek>0));
                }

                @Override
                public CodeResult delete() {
                    return new CodeResult((!file.exists()||file.delete())&&!file.exists()?What.WHAT_SUCCEED:What.WHAT_FAIL);
                }
            };
        }else if (scheme.startsWith("http")){
            final String finalScheme=null!=scheme?scheme.toLowerCase():null;
            String specificPart=uri.getEncodedSchemeSpecificPart();
            final String hostUri=null!=specificPart?scheme+":"+specificPart:null;
            if (null==hostUri||hostUri.length()<=0){
                Debug.W("Can't create Uri outputStream while TO's host uri invalid.");
                return new StreamOpener(What.WHAT_ERROR);
            }
            Map args=new HashMap();
            final String path=uri.getQueryParameter(Label.LABEL_PATH);
            args.put(Label.LABEL_PATH,null!=path?path:"");
            final Nas nas=new Nas();
            final Reply<NasPath> reply=nas.getNasFileData(hostUri,args);
            if (null==reply||!reply.isSuccess()){
                Debug.W("Can't create Uri outputStream while fet uri reply fail.");
                return new StreamOpener(What.WHAT_ERROR);
            }
            final NasPath existPath=null!=reply?reply.getData():null;
            return new StreamOpener(What.WHAT_SUCCEED,null!=existPath?existPath.getLength():0,
                    null!=existPath?existPath.getMd5():null){
                @Override
                CodeResult<OutputStream> open(long seek) throws Exception{
                    String uriPath=uri.getPath();
                    final URL url = null!=uriPath?new URL(uriPath):null;
                    URLConnection urlConnection=null!=url?url.openConnection():null;
                    if (null==urlConnection){
                        Debug.W("Can't open outputStream while connect fail.");
                        return new CodeResult<>(What.WHAT_FAIL);
                    }
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    urlConnection.getContentType();

                    OutputStream outputStream=urlConnection.getOutputStream();
                    if ()
                    long length=file.length();
                    if (seek>0&&seek!=length){
                        Debug.W("Can't create outputStream while seek not match.");
                        return null;
                    }
//                    int contentLength = urlConnection.getContentLength();
                    return null;
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
