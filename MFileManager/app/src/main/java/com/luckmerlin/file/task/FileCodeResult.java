package com.luckmerlin.file.task;

import com.luckmerlin.file.api.What;
import com.luckmerlin.task.CodeResult;

public final class FileCodeResult extends CodeResult {
    private Boolean mError;

    public FileCodeResult(int code){
        this(code,null);
    }

    public FileCodeResult(int code,Boolean error){
        super(code);
        mError=error;
    }

    public FileCodeResult error(Boolean error){
        mError=error;
        return this;
    }

    @Override
    public boolean isSucceed() {
        return getCode()== What.WHAT_SUCCEED;
    }
}
