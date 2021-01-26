package com.luckmerlin.file;

import android.content.Context;

import com.luckmerlin.mvvm.ModelLifeBinder;

public class Application extends android.app.Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ModelLifeBinder.bindActivityLife(true,this);
    }
}
