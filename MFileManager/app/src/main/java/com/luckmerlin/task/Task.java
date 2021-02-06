package com.luckmerlin.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class Task   {
    private Result mResult;
    private Future mFuture;

    public final boolean execute(ExecutorService executor) {
        if (null!=mFuture){//Check if already doing
            return false;
        }
        mFuture=executor.submit(()->{
            Result result=mResult=onExecute(this);
        });
        return result;
    }

    public final boolean isFinished(){
        return null!=mResult;
    }

    protected abstract Result onExecute(Task task);

    public final boolean isExecuting() {
        return mExecuting;
    }

    public final Result getResult() {
        return mResult;
    }
}
