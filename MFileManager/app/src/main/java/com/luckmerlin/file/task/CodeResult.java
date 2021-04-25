package com.luckmerlin.file.task;

import com.luckmerlin.file.api.What;
import com.luckmerlin.task.Result;

public class CodeResult<T> implements Result {
    private final int mCode;
    private final T mArg;

    public CodeResult(int code){
        this(code,null);
    }

    public CodeResult(int code,T arg){
        mCode=code;
        mArg=arg;
    }

    public final int getCode() {
        return mCode;
    }

    public final T getArg() {
        return mArg;
    }

    @Override
    public boolean isSucceed() {
        int code=mCode;
        return code== What.WHAT_SUCCEED||code==What.WHAT_ALREADY_DONE;
    }
}
