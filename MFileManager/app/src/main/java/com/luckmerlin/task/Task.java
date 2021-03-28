package com.luckmerlin.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.task.Progress;

public abstract class Task{
    private Result mResult;
    private int mStatus=Status.IDLE;
    private boolean mCanceled=false;
    private final String mName;

    public Task(){
        this(null);
    }

    public Task(String name){
        mName=name;
    }

    public final synchronized boolean execute(OnTaskUpdate callback) {
        if (isStarted()||isExecuting()){//Check if already doing
            return false;
        }
        mCanceled=false;
        mResult=null;
        String name=mName;
        Debug.D("Start execute task "+(null!=name?name:"."));
        notifyTaskUpdate(Status.STARTED,callback);
        Result result=mResult=onExecute(this,(Task task1, int status)-> {
            if (Status.STARTED!=status&&status!=Status.STARTED){
                notifyTaskUpdate(task1,status,callback);
            }
        });
        Debug.D("Finish execute task "+(null!=name?name:".")+" "+isResultSucceed(result));
        notifyTaskUpdate(Status.IDLE,callback);
        return true;
    }

    public final boolean isSucceed(){
        return isResultSucceed(mResult);
    }

    public final boolean isCanceled(){
        return mCanceled;
    }

    public final boolean cancel(boolean cancel){
        if (mCanceled!=cancel){
            mCanceled=cancel;
            return true;
        }
        return false;
    }

    public final String getName() {
        return mName;
    }

    public final Progress getProgress() {
        Result result=mResult;
        return null!=result?result.getProgress():null;
    }

    public final boolean isFinished(){
        return null!=mResult;
    }

    protected abstract Result onExecute(Task task,OnTaskUpdate callback);

    public final boolean isStarted(){
        return mStatus!=Status.IDLE;
    }

    protected final boolean isResultSucceed(Result result){
        return null!=result&&result.getCode()==What.WHAT_SUCCEED;
    }

    public final Result code(int code){
        return code(code,null);
    }

    public final Result code(int code,Progress progress){
        return new AbsResult(code,progress);
    }

    public final boolean isAnyStatus(int ...status) {
        int current=mStatus;
        if (null!=status){
            for (int child:status) {
                if (child==current){
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean isExecuting() {
        return mStatus==Status.EXECUTING;
    }

    public final boolean isPreparing() {
        return mStatus==Status.PREPARING;
    }

    public final Result getResult() {
        return mResult;
    }

    protected final void notifyTaskUpdate(int status,OnTaskUpdate callback){
        notifyTaskUpdate(this,status,callback);
    }

    private final void notifyTaskUpdate(Task task,int status,OnTaskUpdate callback){
        if (null!=task&&task==this){
            mStatus=status;
        }
        if (null!=callback){
            callback.onTaskUpdated(task,status);
        }
    }

    protected static class AbsResult implements Result {
        final int mCode;
        final Progress mProgress;

        protected AbsResult(int code,Progress progress){
            mCode=code;
            mProgress=progress;
        }

        @Override
        public int getCode() {
            return mCode;
        }

        @Override
        public Progress getProgress() {
            return mProgress;
        }
    }
}
