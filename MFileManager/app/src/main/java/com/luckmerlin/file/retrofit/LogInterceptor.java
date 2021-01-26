package com.luckmerlin.file.retrofit;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Response;

public class LogInterceptor implements Interceptor {

    private String TAG = "LM";

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
//                .newBuilder()
//                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                .addHeader("Accept-Encoding", "gzip, deflate")
//                .addHeader("Connection", "keep-alive")
//                .addHeader("Accept", "*/*")
//                .addHeader("Cookie", "add cookies here")
//                .build()
                ;

        Log.e(TAG,"request:" + request.toString());
        okhttp3.Response response = chain.proceed(chain.request());
        ResponseBody responseBody = response.body();
        MediaType mediaType=responseBody.contentType();
        String content=null;
        final String charset="UTF-8";
        try (BufferedSource source =(null!=responseBody?responseBody.source():null)) {
            content=source.readString(Charset.forName(charset));
        }
        String contentType=null!=mediaType?mediaType.toString():null;
        if (null!=contentType&&contentType.equals("application/octet-stream")){
            return response;
        }
        long t1 = System.nanoTime();
        long t2 = System.nanoTime();
        Log.e(TAG,String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));
//        String content = response.body().string();
        Log.e(TAG, "response body:"+contentType+" " + content);
        byte[] bytes=null!=content?content.getBytes(charset):null;
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, bytes))
//                .header("Authorization", Your.sToken)
                .build();
    }

}
