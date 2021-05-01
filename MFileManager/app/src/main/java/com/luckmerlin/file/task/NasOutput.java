package com.luckmerlin.file.task;

import android.net.Uri;
import com.google.gson.Gson;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.nas.Nas;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

 class NasOutput implements Output {
    private final String mHostUri;
    private final String mPath;
    private OutputStream mOutputStream;
    private HttpURLConnection mURLConnection;
    private Reply<NasPath> mNasReply;

    public NasOutput(Uri uri) {
        String host=null!=uri?uri.getHost():null;
        String scheme=null!=uri?uri.getScheme():null;
        host=null!=host&&null!=scheme?scheme+"://"+host:null;
        mHostUri=null!=host?host+":"+uri.getPort():null;
        mPath=null!=uri?uri.getQueryParameter(Label.LABEL_PATH):null;
    }

    @Override
    public long getLength() {
        Reply<NasPath> nasReply=mNasReply;
        if (null!=nasReply&&nasReply.isSuccess()){
            NasPath nasPath=nasReply.getData();
            return null!=nasPath?nasPath.getLength():0;
        }
        final Map args=new HashMap();
        final String path=mPath;
        final String hostUri=mHostUri;
        if (null==hostUri||null==path){
            return -1;
        }
        args.put(Label.LABEL_PATH,null!=path?path:"");
        final Nas nas=new Nas();
        final Reply<NasPath> reply=nas.getNasFileData(hostUri,args);
        if (null==reply||!reply.isSuccess()){
            Debug.W("Fetch uri reply fail.");
            return -1;
        }
        mNasReply=reply;
        return getLength();
    }

    @Override
    public CodeResult open(long seek) throws Exception {
        final String hostUri=mHostUri;
        final String path=mPath;
        if (null==hostUri||hostUri.length()<0){
            Debug.W("Can't create outputStream while host uri invalid.");
            return new CodeResult<>(What.WHAT_ARGS_INVALID);
        }else if (seek>0&&seek!=getLength()){
            Debug.W("Can't create outputStream while seek not match.");
            return new CodeResult<>(What.WHAT_MATCH_FAIL);
        }else if (null==path||path.length()<=0){
            Debug.W("Can't create outputStream while path invalid.");
            return new CodeResult<>(What.WHAT_ARGS_INVALID);
        }
        final URL url = new URL(hostUri+"/file/save");
        HttpURLConnection urlConnection=null!=url? (HttpURLConnection) url.openConnection() :null;
        if (null==urlConnection){
            Debug.W("Can't create output stream while http connect fail.");
            return new CodeResult<>(What.WHAT_FAIL);
        }
        mURLConnection=urlConnection;
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setChunkedStreamingMode(1024 * 1024);
        urlConnection.setRequestProperty(Label.LABEL_PATH, mPath);
        urlConnection.setRequestProperty(Label.LABEL_POSITION, Long.toString(seek));
        urlConnection.setRequestProperty("Content-Type", "application/octet-stream");
        urlConnection.setRequestMethod("POST");
        urlConnection.setConnectTimeout(50000);
        urlConnection.setReadTimeout(500000);
        urlConnection.setRequestProperty("Connection", "Keep-Alive");
        urlConnection.setRequestProperty("Charset", "UTF-8");
        urlConnection.connect();
        final OutputStream outputStream=mOutputStream=urlConnection.getOutputStream();
        if (null==outputStream){
            Debug.W("Can't open outputStream while connect outputStream NULL.");
            return new CodeResult<>(What.WHAT_FAIL);
        }
        return new CodeResult(What.WHAT_SUCCEED);
    }

    @Override
    public Path close() {
        final HttpURLConnection connection= mURLConnection;
        OutputStream outputStream=mOutputStream;
        if (null!=outputStream){
            mOutputStream=null;
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStream inputStream=null;
        String responseText=null;
        try {
            if (null!=connection){
                inputStream=connection.getInputStream();
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
                                null!=object?new Gson().fromJson(object.toString(),NasPath.class):null).getData();
                    }
                }
                Debug.W("Uri stream output response fail.\n"+responseText);
            }
        }catch (Exception e){
            Debug.E("Exception read uri stream response.e="+e+"\n"+responseText);
            e.printStackTrace();
        }finally {
            new Closer().close(inputStream,outputStream);
            if (null!=connection){
                connection.disconnect();
            }
        }
        Reply<NasPath> nasReply=mNasReply;
        return null!=nasReply?nasReply.getData():null;
    }

    @Override
    public boolean write(byte[] buffer, int offset, int length) throws Exception {
        OutputStream outputStream=mOutputStream;
        if (null==outputStream){
            return false;
        }
        outputStream.write(buffer,offset,length);
        return true;
    }

    @Override
    public boolean delete() {
        Reply<NasPath> deleteReply=new Nas().deleteFile(mHostUri,mPath);
        return null!=deleteReply&&deleteReply.getWhat()==What.WHAT_SUCCEED;
    }
}
