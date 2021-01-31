package com.luckmerlin.task;

public abstract class Task   {
    private Result mResult;
    private boolean mExecuting;

    public final Result execute() {
        if (mExecuting){//Check if already doing
            return null;
        }
        mExecuting=true;
        Result result=mResult=onExecute(this);
        mExecuting=false;
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
