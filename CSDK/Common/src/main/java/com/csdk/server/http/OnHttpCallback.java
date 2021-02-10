package com.csdk.server.http;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create LuckMerlin
 * Date 17:49 2020/8/18
 * TODO
 */
public abstract class OnHttpCallback implements Callback {

    protected abstract  void onHttpFinish(boolean succeed, Call call, String note, Object data);

    @Override
    public void onFailure(Call call, IOException e) {
        onHttpFinish(false,call, null!=e?e.toString():"Failure",null);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        onHttpFinish(true,call,null,response);
    }
}
