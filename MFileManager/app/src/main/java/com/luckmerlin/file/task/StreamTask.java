package com.luckmerlin.file.task;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.MD5;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.nas.Nas;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class StreamTask extends FromToTask<Uri, Uri> {
    private final WeakReference<Context> mContext;
    private boolean mRecheckMd5=false;
    private boolean mDeleteFail=false;

    public StreamTask(Context context, Uri from, Uri to) {
        super(from, to);
        mContext=null!=context?new WeakReference<>(context):null;
    }

    public final StreamTask enableRecheckMd5(boolean enable) {
        this.mRecheckMd5 = enable;
        return this;
    }

    public final StreamTask enableDeleteFail(boolean deleteFail) {
        mDeleteFail = deleteFail;
        return this;
    }

    @Override
    final Result onExecute(Uri from, Uri to, Task task, OnTaskUpdate callback) {
        if (null==from||null==to){
            Debug.W("Can't execute Uri stream task while args invalid.");
            return new CodeResult(What.WHAT_ARGS_INVALID);
        }
        Input streamInput=null;Output streamOutput=null;boolean hasWriteFLag=false;
        try{
            CodeResult<Output> outputResult=createOutputOpener(to);
            final Output output=streamOutput=null!=outputResult?outputResult.getArg():null;
            if (null==output||outputResult.getCode()!=What.WHAT_SUCCEED){
                Debug.W("Can't execute Uri stream task while output create fail.");
                return null!=outputResult?outputResult:new CodeResult<>(What.WHAT_FAIL);
            }
            CodeResult<Input> inputOpenerResult=createInputOpener(from);
            Input input=streamInput=null!=inputOpenerResult?inputOpenerResult.getArg():null;
            if (null==input||null==inputOpenerResult||inputOpenerResult.getCode()!=What.WHAT_SUCCEED){
                Debug.W("Can't execute Uri stream task while input create fail.");
                return null!=inputOpenerResult?inputOpenerResult:new CodeResult<>(What.WHAT_FAIL);
            }
            final long total=input.getLength();
            final long currentLength=output.getLength();
            Debug.D("Now,Doing stream stark."+to);
            if (total<0||currentLength<0){
                Debug.W("Can't execute Uri stream task while input length not match output length.");
                return new CodeResult<>(What.WHAT_MATCH_FAIL);
            }else if (currentLength>total){
                Debug.W("Can't execute Uri stream task while file already exist.");
                return new CodeResult<>(What.WHAT_EXIST);
            }else if (currentLength<total){//If need load again
                CodeResult openResult=input.open(currentLength);
                openResult=null!=openResult?openResult:new CodeResult(What.WHAT_FAIL);
                if (openResult.getCode()!=What.WHAT_SUCCEED){
                    Debug.W("Can't execute Uri stream task while input open fail.");
                    return openResult;
                }
                openResult=output.open(currentLength);
                openResult=null!=openResult?openResult:new CodeResult(What.WHAT_FAIL);
                if (openResult.getCode()!=What.WHAT_SUCCEED){
                    Debug.W("Can't execute Uri stream task while output open fail.");
                    return openResult;
                }
                hasWriteFLag=true;
                byte[] buffer=new byte[1024*1024];
                int read=0;long uploaded=0;float speed;
                long startTime=System.nanoTime();
                while ((read=input.read(buffer))>=0){
                    uploaded += read;
                    if (isCanceled()) {
                        Debug.D("Cancel uri stream task.");
                        return new CodeResult<>(What.WHAT_CANCEL);
                    } else if (read>0) {
                        output.write(buffer,0,read);
                        if ((startTime = startTime > 0 ? System.nanoTime() - startTime : -1) > 0) {
                            startTime = TimeUnit.NANOSECONDS.toMillis(startTime);
                            speed = startTime > 0 ? read / startTime : 0;
                        }
                    }
                }
                Debug.D("Finish stream task."+uploaded+"/"+total);
            }
        } catch (Exception e) {
            Debug.W("Can't execute Uri stream task while exception."+e);
            e.printStackTrace();
            return new CodeResult(What.WHAT_EXCEPTION);
        }finally {
            Path inputPath=null!=streamInput?streamInput.close():null;
            Path outputPath=null!=streamOutput?streamOutput.close():null;
            String inputMd5=null!=inputPath?inputPath.isDirectory()||inputPath.getLength()<=0?"":inputPath.getMd5():null;
            String outputMd5=null!=outputPath?outputPath.isDirectory()||outputPath.getLength()<=0?"":outputPath.getMd5():null;
            if ((null==inputMd5&&outputMd5==null)||(null!=inputMd5&&null!=outputMd5&&inputMd5.toLowerCase().equals(outputMd5.toLowerCase()))){
                return new CodeResult(hasWriteFLag?What.WHAT_SUCCEED:What.WHAT_ALREADY_DONE);
            }
        }
        Debug.D("Fail do stream task."+to);
        if (mDeleteFail&&hasWriteFLag&&null!=streamOutput&&streamOutput.delete()){
            Debug.D("Deleted fail stream task output file."+to);
        }
        return new CodeResult<>(What.WHAT_FAIL);
    }

    private CodeResult<Output> createOutputOpener(Uri uri){
        String scheme=null!=uri?uri.getScheme():null;
        scheme=null!=scheme?scheme.toLowerCase():null;
        if (null==scheme||scheme.length()<=0){
            Debug.W("Can't create Uri outputStream while uri scheme invalid.");
            return new CodeResult(What.WHAT_ARGS_INVALID);
        }else if (scheme.equals(ContentResolver.SCHEME_FILE)){
            String path=uri.getPath();
            final File file = null!=path&&path.length()>0?new File(path):null;
            if (null==file){
                Debug.W("Can't create Uri outputStream while from invalid.");
                return new CodeResult(What.WHAT_ARGS_INVALID);
            }
            return new CodeResult<>(What.WHAT_SUCCEED, new FileOutput(file));
        }else if (scheme.startsWith("http")){
            return new CodeResult<>(What.WHAT_SUCCEED,new NasOutput(uri));
        }
        Debug.W("Can't create Uri outputStream while scheme not support."+scheme);
        return new CodeResult<>(What.WHAT_NOT_SUPPORT);
    }

    private CodeResult<Input> createInputOpener(Uri uri){
        String scheme=null!=uri?uri.getScheme():null;
        scheme=null!=scheme?scheme.toLowerCase():null;
         if (null==scheme||scheme.length()<=0){
            Debug.W("Can't create Uri input while uri scheme invalid.");
            return new CodeResult(What.WHAT_ARGS_INVALID);
        }else if (scheme.equals(ContentResolver.SCHEME_FILE)){
            String path=uri.getPath();
            File file = null!=path&&path.length()>0?new File(path):null;
            if (null==file||!file.exists()) {
                Debug.W("Can't create Uri input while from invalid.");
                return new CodeResult(What.WHAT_NOT_EXIST);
            }
            return new CodeResult(What.WHAT_SUCCEED, new FileInput(file));
        }else if (scheme.startsWith("http")){
             return new CodeResult<>(What.WHAT_SUCCEED,new NasInput(uri));
         }else if (scheme.equals(ContentResolver.SCHEME_CONTENT)){
//            Context context=getContext();
//            final ContentResolver resolver = null!=context?context.getContentResolver():null;
//            if (null==resolver){
//                Debug.W("Can't create Uri input while resolver is NULL.");
//                return new CodeResult(What.WHAT_FAIL);
//            }
//            return new CodeResult<>(What.WHAT_SUCCEED, new Input("名字",0) {
//                @Override
//                CodeResult<InputStream> open(long seek) throws Exception {
//                   if (seek<0){
//                        Debug.W("Can't open Uri inputStream while seek invalid."+seek);
//                        return new CodeResult<>(What.WHAT_FAIL);
//                    }
//                   InputStream inputStream=resolver.openInputStream(uri);
//                    if (null!=inputStream&&seek>0&&inputStream.skip(seek)!=seek){
//                        Debug.W("Can't open Uri inputStream while seek fail."+seek);
//                        new Closer().close(inputStream);
//                        return new CodeResult<>(What.WHAT_FAIL);
//                    }
//                    return new CodeResult<>(What.WHAT_SUCCEED,inputStream);
//                }
//            });
         }else if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)){

         }

         Debug.W("Can't create Uri inputStream while scheme not support."+scheme);
        return new CodeResult(What.WHAT_NOT_SUPPORT);
    }

    public final Context getContext(){
        WeakReference<Context> reference=mContext;
        return null!=reference?reference.get():null;
    }

}
