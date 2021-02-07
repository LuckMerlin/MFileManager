package com.luckmerlin.task;

public abstract class CodeResult implements Result{
    private final int mCode;

    public CodeResult(int code){
        mCode=code;
    }

    public final int getCode() {
        return mCode;
    }
}
