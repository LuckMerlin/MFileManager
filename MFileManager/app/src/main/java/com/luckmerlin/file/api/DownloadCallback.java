package com.luckmerlin.file.api;

import java.io.File;

import retrofit2.Call;
import retrofit2.Response;

public final class DownloadCallback<T> implements retrofit2.Callback<T> {

    protected void onResponse(T response, File file){

    }

    @Override
    public final void onResponse(Call<T> call, Response<T> response) {

    }

    @Override
    public final void onFailure(Call<T> call, Throwable t) {
//        onResponse();
    }
}
