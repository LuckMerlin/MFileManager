package com.luckmerlin.file;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;

public final class LocalClient extends AbsClient<LocalFolder<Query>,Query,LocalPath>{
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
    protected Canceler query(Query path, long from, long to, OnApiFinish<Reply<LocalFolder<Query>>> callback) {
        return null;
    }
}
