package com.luckmerlin.file.task;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.renderscript.ScriptGroup;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
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
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class UriStreamTask extends FromToTask<Uri, Uri> {
    private final WeakReference<Context> mContext;
    private boolean mRecheckMd5=false;
    private boolean mDeleteFail=false;

    public UriStreamTask(Context context,Uri from, Uri to) {
        super(from, to);
        mContext=null!=context?new WeakReference<>(context):null;
    }

    public final UriStreamTask enableRecheckMd5(boolean enable) {
        this.mRecheckMd5 = enable;
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
        StreamOpener<CodeResult<WriteableStream>> deleteFailOpener=null;
        ReadableStream readableStream=null;
        WriteableStream writeableStream=null;
        try {
            final StreamOpener<CodeResult<WriteableStream>> outputOpener=deleteFailOpener=createOutputStream(to);
            int outputCode=null!=outputOpener?outputOpener.getCode():What.WHAT_FAIL;
            if (outputCode!=What.WHAT_SUCCEED){
                Debug.W("Fail execute uri stream task while TO stream opener create fail.");
                return new CodeResult(outputCode);
            }
            final StreamOpener<CodeResult<ReadableStream>> inputOpener=createInputStream(from);
            int inputCode=null!=inputOpener?inputOpener.getCode():What.WHAT_FAIL;
            if (inputCode!=What.WHAT_SUCCEED){
                Debug.W("Fail execute uri stream task while FROM stream opener create fail.");
                return new CodeResult(inputCode);
            }
            CodeResult checkCode=checkIfAlreadyDone(inputOpener,outputOpener);
            if (null==checkCode||checkCode.getCode()!=What.WHAT_SUCCEED){
                Debug.W("Check skip do uri stream.checkCode="+checkCode);
                return null!=checkCode?checkCode:new CodeResult<>(What.WHAT_FAIL);
            }
            final String inputMd5=inputOpener.getMd5();
            final long inputLength=inputOpener.getLength();
            final long outputLength=outputOpener.getLength();
            final boolean recheckMd5=mRecheckMd5;
            CodeResult<ReadableStream> inputStreamResult=inputOpener.open(recheckMd5,outputLength);
            inputCode=null!=inputStreamResult?inputStreamResult.getCode():What.WHAT_FAIL;
            readableStream=null!=inputStreamResult?inputStreamResult.getArg():null;
            if (inputCode!=What.WHAT_SUCCEED||null==readableStream){
                Debug.W("Fail execute uri stream task while FROM opener open fail."+inputCode);
                return new CodeResult(outputCode);
            }
            CodeResult<WriteableStream> outputStreamResult=outputOpener.open(recheckMd5,outputLength);
            writeableStream=null!=outputStreamResult?outputStreamResult.getArg():null;
            outputCode=null!=outputStreamResult?outputStreamResult.getCode():What.WHAT_FAIL;
            if (null==writeableStream||outputCode!=What.WHAT_SUCCEED||null==outputStreamResult){
                Debug.W("Fail execute uri stream task while TO opener open fail."+outputCode);
                return new CodeResult(outputCode);
            }
            byte[] buffer=new byte[1024*1024];
            int read=0;long uploaded=0;float speed;
            long startTime=System.nanoTime();
            while ((read=readableStream.read(buffer))>=0){
                uploaded += read;
                if (isCanceled()) {
                    Debug.D("Cancel uri stream task.");
                    return new CodeResult<>(What.WHAT_CANCEL);
                } else if (read>0) {
                    writeableStream.write(buffer,0,read);
                    if ((startTime = startTime > 0 ? System.nanoTime() - startTime : -1) > 0) {
                        startTime = TimeUnit.NANOSECONDS.toMillis(startTime);
                        speed = startTime > 0 ? read / startTime : 0;
                    }
                }
            }
            //Done stream
            Reply<?extends Path> path=writeableStream.close();
            Path donePath=null!=path&&path.getWhat()==What.WHAT_SUCCEED?path.getData():null;
            if (null!=donePath){
                if (donePath.getLength()!=inputLength){
                    Debug.D("Fail done uri stream while length match fail."+to);
                    return new CodeResult<>(What.WHAT_MATCH_FAIL);
                }
                String doneMd5=donePath.getMd5();
                if (recheckMd5&&null!=inputMd5
                        &&!((null==inputMd5&&null==doneMd5)||(null!=inputMd5&&null!=doneMd5&&inputMd5.equals(doneMd5)))){//Check md5
                    Debug.D("Fail done uri stream while md5 match fail."+to);
                    return new CodeResult<>(What.WHAT_MATCH_FAIL);
                }
                deleteFailOpener=null;//Clean delete fail while succeed
                Debug.D("Succeed done uri stream."+to);
                return new CodeResult<>(What.WHAT_SUCCEED);
            }
            Debug.D("Failed do uri stream."+to);
            return new CodeResult(What.WHAT_FAIL);
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
            new Closer().close(readableStream);
            if (null!=writeableStream){
                try {
                    writeableStream.close();
                } catch (Exception e) {
                    //Do nothing
                }
            }
        }
    }

    private CodeResult checkIfAlreadyDone(StreamOpener<CodeResult<ReadableStream>> inputOpener,
                                          StreamOpener<CodeResult<WriteableStream>> outputOpener){
       if (null!=inputOpener&&null!=outputOpener){
           final long inputLength=inputOpener.getLength();
           final long outputLength=outputOpener.getLength();
           if (outputLength<0||inputLength<0){
               Debug.W("Fail execute uri stream task while FROM or TO's length invalid."+outputLength+" "+inputLength);
               return new CodeResult(What.WHAT_FAIL);
           }else if (outputLength>inputLength){//Already length match
               Debug.W("Fail execute uri stream task while TO's length already larger than FROM.");
               return new CodeResult(What.WHAT_ERROR);
           }else if (outputLength==inputLength){//Already
               String inputMd5=inputOpener.getMd5();
               String outputMd5=outputOpener.getMd5();
               if (null!=inputMd5&&null!=outputMd5&&inputMd5.equals(outputMd5)){
                   Debug.W("Uri stream already done.");
                   return new CodeResult(What.WHAT_ALREADY_DONE);
               }
               Debug.W("Fail execute uri stream task while TO's length already match FROM but md5 not match.");
               return new CodeResult(What.WHAT_ERROR);
           }
           return new CodeResult(What.WHAT_SUCCEED);
       }
        return new CodeResult(What.WHAT_ERROR);
    }

    protected StreamOpener<CodeResult<WriteableStream>> createOutputStream(Uri uri) throws Exception {
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
                CodeResult<WriteableStream> open(boolean loadMd5,long seek) throws Exception{
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
                    final FileOutputStream fileOutputStream=new FileOutputStream(file,seek>0);
                    return new CodeResult<>(What.WHAT_SUCCEED,new WriteableStream(){
                        @Override
                       public void write(byte[] b, int off, int len) throws IOException {
                            fileOutputStream.write(b,off,len);
                        }

                        @Override
                        public Reply<Path> close() {
                            new Closer().close(fileOutputStream);
                            return null;
                        }
                    });
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
                CodeResult<WriteableStream> open(boolean loadMd5,long seek) throws Exception{
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
                    urlConnection.setChunkedStreamingMode(1024 * 1024);
                    urlConnection.setRequestProperty(Label.LABEL_PATH, path);
                    urlConnection.setRequestProperty(Label.LABEL_MD5, loadMd5?Label.LABEL_MD5:"");
                    urlConnection.setRequestProperty(Label.LABEL_POSITION, Long.toString(seek));
                    urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(50000);
                    urlConnection.setReadTimeout(500000);
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setRequestProperty("Charset", "UTF-8");
                    urlConnection.connect();
                    final OutputStream outputStream=urlConnection.getOutputStream();
                    if (null==outputStream){
                        Debug.W("Can't open outputStream while connect outputStream NULL.");
                        return new CodeResult<>(What.WHAT_FAIL);
                    }
                    return new CodeResult<>(What.WHAT_SUCCEED,new WriteableStream(){
                        @Override
                        public void write(byte[] b, int off, int len) throws IOException {
                            outputStream.write(b,off,len);
                        }

                        @Override
                        public Reply<? extends Path> close() {
                            InputStream inputStream=null;
                            String responseText=null;
                            try {
                                inputStream=urlConnection.getInputStream();
                                if (null!=inputStream){
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                    StringBuffer stringBuffer = new StringBuffer();
                                    String responseLine;
                                    while ((responseLine = bufferedReader.readLine()) != null) {
                                        stringBuffer.append(responseLine);
                                    }
                                    responseText=stringBuffer.toString();
                                    responseText=null!=responseText&&responseText.length()>0?responseText.trim():null;
                                    JSONObject json=null!=responseText&&responseText.length()>0? new JSONObject(responseText) :null;
                                    if (null!=json){
                                        Object object=json.opt(Label.LABEL_DATA);
                                        return new Reply<NasPath>(json.optBoolean(Label.LABEL_SUCCESS,false),
                                                json.optInt(Label.LABEL_WHAT,What.WHAT_FAIL),json.optString(Label.LABEL_NOTE,"Parse response fail."),
                                                null!=object?new Gson().fromJson(object.toString(),NasPath.class):null);
                                    }
                                }
                                Debug.W("Uri stream output response fail.\n"+responseText);
                            }catch (Exception e){
                                Debug.E("Exception read uri stream response.e="+e+"\n"+responseText);
                                e.printStackTrace();
                            }finally {
                                new Closer().close(inputStream,outputStream);
                                urlConnection.disconnect();
                            }
                            return null;
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

    protected StreamOpener<CodeResult<ReadableStream>> createInputStream(Uri uri) throws Exception {
        String scheme=uri.getScheme();
        scheme=null!=scheme?scheme.toLowerCase():null;
        if (null==scheme||scheme.length()<=0){
            Debug.W("Can't create Uri inputStream while uri scheme invalid.");
            return new StreamOpener(What.WHAT_ARGS_INVALID);
        }else if (scheme.equals(ContentResolver.SCHEME_FILE)){
            String path=uri.getPath();
            File file = null!=path&&path.length()>0?new File(path):null;
            if (null==file||!file.exists()) {
                Debug.W("Can't create Uri inputStream while from invalid.");
                return new StreamOpener(What.WHAT_NOT_EXIST);
            }
            final boolean isDirectory=file.isDirectory();
            if (isDirectory?file.canExecute():file.canRead()){
                Debug.W("Can't create Uri inputStream while from NONE permission.");
                return new StreamOpener(What.WHAT_NONE_PERMISSION);
            }
//            else if (!file.isFile()){
//                Debug.W("Can't create Uri inputStream while from is not file.");
//                return new StreamOpener(What.WHAT_NOT_FILE);
//            }
            final long length=file.length();
            final String md5=isDirectory||length==0?"":new MD5().getFileMD5(file);
            return new StreamOpener<CodeResult<ReadableStream>>(What.WHAT_SUCCEED,length,md5){
                @Override
                CodeResult<ReadableStream> open(boolean loadMd5, long seek) throws Exception {
                    if (seek<0){
                        Debug.W("Can't open Uri inputStream while seek invalid."+seek);
                        return new CodeResult(What.WHAT_FAIL);
                    }
                    return new CodeResult<>(What.WHAT_SUCCEED,new FileReadableStream(file));
                }
            };
//            return new StreamOpener(What.WHAT_SUCCEED,length,md5){
//                @Override
//                CodeResult<ReadableStream> open(boolean loadMd5,long seek) throws Exception {
//                    if (seek<0){
//                        Debug.W("Can't open Uri inputStream while seek invalid."+seek);
//                        return new CodeResult(What.WHAT_FAIL);
//                    }else if (file.isDirectory()){
//                        return new CodeResult<>(What.WHAT_FAIL);
//                    }
//                    InputStream inputStream=new FileInputStream(file);
//                    if (seek>0&&inputStream.skip(seek)!=seek){
//                        Debug.W("Can't open Uri inputStream while seek fail."+seek);
//                        new Closer().close(inputStream);
//                        return new CodeResult<>(What.WHAT_FAIL);
//                    }
//                    return new CodeResult<>(What.WHAT_SUCCEED, new ReadableStream() {
//                        @Override
//                        int read(byte[] b) throws IOException {
//
//                            return inputStream.read(b);
//                        }
//
//                        @Override
//                        public void close() throws IOException {
//                            inputStream.close();
//                        }
//                    });
//                }
//            };
        }else if (scheme.equals(ContentResolver.SCHEME_CONTENT)){
            Context context=getContext();
            final ContentResolver resolver = null!=context?context.getContentResolver():null;
            if (null==resolver){
                Debug.W("Can't create Uri inputStream while resolver is NULL.");
                return new StreamOpener(What.WHAT_FAIL);
            }
            return new StreamOpener(What.WHAT_SUCCEED){
                @Override
                CodeResult<ReadableStream> open(boolean loadMd5,long seek) throws Exception {
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
                    return new CodeResult<>(What.WHAT_SUCCEED, new ReadableStream() {
                        @Override
                        public int read(byte[] b) throws IOException {
                            return inputStream.read(b);
                        }

                        @Override
                        public void close() throws IOException {
                            inputStream.close();
                        }
                    });
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

}
