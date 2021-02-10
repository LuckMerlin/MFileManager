package com.csdk.server.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Create LuckMerlin
 * Date 16:56 2020/12/3
 * TODO
 */
 class RetryIntercepter implements Interceptor {

    public int maxRetry=5;//最大重试次数
    private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

    public RetryIntercepter(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        if (null!=chain){
            Request request = chain.request();
            Response response = chain.proceed(request);
            if (null!=response){
                while (!response.isSuccessful() && retryNum < maxRetry) {
                    retryNum++;
                    response = chain.proceed(request);
                }
                return response;
            }
        }
        return null;
    }
}
