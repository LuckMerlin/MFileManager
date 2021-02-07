package com.luckmerlin.file.task;

import com.luckmerlin.file.api.What;
import com.luckmerlin.task.CodeResult;

public class FileCodeResult extends CodeResult {

    public FileCodeResult(int code){
        super(code);
    }

    @Override
    public boolean isSucceed() {
        return getCode()== What.WHAT_SUCCEED;
    }
}
