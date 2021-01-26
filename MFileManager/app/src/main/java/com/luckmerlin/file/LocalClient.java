package com.luckmerlin.file;

import com.luckmerlin.adapter.OnSectionLoadFinish;
import com.luckmerlin.core.Canceler;

public final class LocalClient extends AbsClient{
    private String mName;
    private long mTotal;
    private long mAvailable;

    public LocalClient(String rootPath,String name){
        mName=name;
    }

    public String getName() {
        return mName;
    }

    public long getAvailable() {
        return mAvailable;
    }

    public long getTotal() {
        return mTotal;
    }

    @Override
    protected Canceler query(Object path, long from, long to, OnSectionLoadFinish callback) {
        return null;
    }
}
