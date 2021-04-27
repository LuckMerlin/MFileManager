package com.luckmerlin.file.task;

public class StreamOpener <T>{
    private final int mCode;
    private final String mMd5;
    private final long mLength;

    public StreamOpener(int code) {
        this(code,-1,null);
    }

    public StreamOpener(int code, long length,String md5) {
        mCode=code;
        mLength=length;
        mMd5=md5;
    }

    T open(boolean loadMd5,long seek) throws Exception {
        return null;
    }

    public CodeResult delete() {
        return null;
    }


    public final int getCode() {
        return mCode;
    }

    public final long getLength() {
        return mLength;
    }

    public final String getMd5() {
        return mMd5;
    }
}