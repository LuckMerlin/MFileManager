package com.luckmerlin.file;

public final class Query {
    private String mPath;
    private String mName;

    public Query(String path,String name){
        mPath=path;
        mName=name;
    }

    public String getName() {
        return mName;
    }

    public String getPath() {
        return mPath;
    }
}
