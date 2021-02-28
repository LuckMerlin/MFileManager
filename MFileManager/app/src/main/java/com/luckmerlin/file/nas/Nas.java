package com.luckmerlin.file.nas;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.api.ApiList;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.retrofit.Retrofit;
import com.luckmerlin.file.util.FileSize;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Response;
import retrofit2.http.HEAD;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public final class Nas {
    private final Retrofit mRetrofit=new Retrofit();

    public interface ApiSaveFile {
        @POST("/")
        @Multipart
        Call<Reply<NasPath>> save(@Part MultipartBody.Part file);

        @HEAD("/media")
        Call<Reply<NasPath>> getFileData(@Query(Label.LABEL_PATH) String path);
    }

    public final Reply<NasPath> getNasFileData(String serverUrl, String filePath){
        if (null!=serverUrl&&serverUrl.length()>0&&null!=filePath&&filePath.length()>0){
            Retrofit retrofit=mRetrofit;
            Debug.D("EEEEEEEd d EEE "+serverUrl+" "+filePath+"\n "+Thread.currentThread());
            Call<Reply<NasPath>> call=retrofit.prepare(ApiSaveFile.class,serverUrl).getFileData(filePath);
            try {
                Debug.D("EEEEEEEd d EEE "+serverUrl+" "+filePath);
                Response<Reply<NasPath>> response=null!=call?call.execute():null;
                return null!=response?response.body():null;
            }catch (Exception e) {
                Debug.E("Exception get nas file data.e="+e,e);
                e.printStackTrace();
            }
        }
        return null;
    }

    public final Reply<NasPath> upload(File file,String serverUrl,String toPath,long seek,String debug){
//        final UploadRequestBody uploadBody=new UploadRequestBody(file){
//            @Override
//            protected Boolean onProgress(long upload, float speed) {
////                if (!isFinished()){
////                    progress.setConveyed(upload);
////                    updateStatus(PROGRESS,change,UploadConvey.this,progressReply);
////                    return false;
////                }
//                return true;
//            }
//        };
//        MultipartBody.Part part=createFilePart(createFileHeadersBuilder(file.getName(),toPath,file.isDirectory()),uploadBody);
//        Debug.D("Upload file "+file.getName()+" to "+toPath+" "+(null!=debug?debug:"."));
//        Call<Reply<NasPath>> call= new Retrofit().prepare(ApiSaveFile.class, serverUrl).save(part);
//        try {
//            Response<Reply<NasPath>> response=null!=call?call.execute():null;
//            return null!=response&&response.isSuccessful()?response.body():null;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    private static abstract class UploadRequestBody extends RequestBody {
        private final File mFile;
        private boolean mCancel=false;

        protected abstract Boolean onProgress(long upload,float speed);

        private UploadRequestBody(File file){
            mFile=file;
        }

        @Override
        public final MediaType contentType() {
            return MediaType.parse("application/otcet-stream");
        }

        @Override
        public final void writeTo(BufferedSink sink) throws IOException {
            final File file = mFile;
            boolean succeed = false;
            if (null != file && file.exists()) {
                if (file.isFile()) {
                    Debug.D("Uploading file "+ FileSize.formatSizeText(file.length()) +" "+file.getAbsolutePath());
                    FileInputStream in = null;
                    try {
                        long fileLength = file.length();
                        JSONObject json = new JSONObject();
                        json.put(Label.LABEL_NAME, file.getName());
                        json.put(Label.LABEL_LENGTH, fileLength);
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        in = new FileInputStream(file);
                        long uploaded = 0;
                        if (!mCancel) {
                            int read;
                            succeed = true;
                            while ((read = in.read(buffer)) != -1) {
                                if (mCancel) {
                                    break;
                                }
                                uploaded += read;
                                sink.write(buffer, 0, read);
                                Boolean interruptUpload=onProgress(uploaded, -1);
                                if (null!=interruptUpload&&interruptUpload){
                                    Debug.D("File upload interrupted.");
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        succeed = false;
                    } finally {
                        if (null != in) {
                            in.close();
                        }
                    }
                }
            }
        }
    }

    public MultipartBody.Part createFilePart(Headers.Builder builder, RequestBody body){
        return null!=builder&&null!=body?MultipartBody.Part.create(builder.build(),body):null;
    }

    private Headers.Builder createFileHeadersBuilder(String name,String filePath, boolean isDirectory) {
        if (null==filePath||filePath.length()<=0){
            return null;
        }
        StringBuilder disposition = new StringBuilder("form-data; name=" + name+ ";filename=luckmerlin" +name);
        Headers.Builder headersBuilder = new Headers.Builder().addUnsafeNonAscii(
                "Content-Disposition", disposition.toString());
        String encoding = "utf-8";
        headersBuilder.add(Label.LABEL_PATH, encode(filePath, "", encoding));
        if (isDirectory) {
            headersBuilder.add(Label.LABEL_FOLDER, Label.LABEL_FOLDER);
        }
        return headersBuilder;
    }

    public final String encode(String name, String def,String encoding){
        try {
            return null!=name&&name.length()>0? URLEncoder.encode(name,null!=encoding&&encoding.
                    length()>0?encoding: "UTF-8"):def;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return def;
    }
}
