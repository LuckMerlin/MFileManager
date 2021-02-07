package com.luckmerlin.task;

import com.luckmerlin.core.debug.Debug;

public abstract class Task{
    private Result mResult;
    private int mStatus;
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
        notifyTaskUpdate(Status.START,callback);
        Result result=mResult=onExecute(this,(Task task1, int status)-> {
            if (Status.START!=status&&status!=Status.STOP){
                notifyTaskUpdate(task1,status,callback);
            }
        });
        Debug.D("Finish execute task "+(null!=name?name:".")+" "+(null!=result&&result.isSucceed()));
        notifyTaskUpdate(Status.STOP,callback);
        return true;
    }

    public final boolean isSucceed(){
        Result result=mResult;
        return null!=result&&result.isSucceed();
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

    public String getName() {
        return mName;
    }

    public final boolean isFinished(){
        return null!=mResult;
    }

    protected abstract Result onExecute(Task task,OnTaskUpdate update);

    public final boolean isStarted(){
        return mStatus!=Status.STOP;
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
}
