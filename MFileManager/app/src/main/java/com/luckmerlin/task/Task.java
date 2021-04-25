package com.luckmerlin.task;

import android.graphics.Color;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.R;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.task.Progress;

public abstract class Task implements Status{
    private Result mResult;
    private Progress mProgress;
    private int mStatus=Status.IDLE;
    private boolean mCanceled=false;
    private final String mName;
    private long mStartTime;
    private long mEndTime;

    public Task(){
        this(null,null);
    }

    public Task(String name,String action){
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
        notifyTaskUpdate(Status.STARTED,null,callback);
        mStartTime=System.currentTimeMillis();
        mEndTime=-1;
        Result result=mResult=onExecute(this,(Task task1, int status)-> {
            if (Status.STARTED!=status&&status!=Status.STARTED){
                notifyTaskUpdate(task1,status,null,callback);
            }
        });
        mEndTime=System.currentTimeMillis();
        Debug.D("Finish execute task "+(null!=name?name:".")+" ");
        notifyTaskUpdate(Status.IDLE,null,callback);
        return true;
    }

    public final long getStartTime() {
        return mStartTime;
    }

    public final long getEndTime() {
        return mEndTime;
    };

    public final long getUsedTime(){
        long time=mStartTime;
        time=System.currentTimeMillis()-time;
        return time>0?time:0;
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

    public int getStatusColor(){
        switch (mStatus) {
            case Status.EXECUTING:
                return Color.parseColor("#008000");
            case Status.PREPARING:
                return Color.parseColor("#FFD700");
            case Status.STARTED:
                return Color.parseColor("#7CFC00");
            case Status.IDLE:
                Result result=mResult;
                if (null==result){
                    return Color.WHITE;
                }else if (isResultSucceed(result)){
                    return Color.GREEN;
                }
//                else if (result.getCode()==What.WHAT_ALREADY_DONE){
//                    return Color.parseColor("#5500ff00");
//                }
                return Color.RED;
        }
        return Color.TRANSPARENT;
    }

    public final String getName() {
        return mName;
    }

    public final Progress getProgress() {
        return mProgress;
    }

    public final boolean isFinished(){
        return null!=mResult;
    }

    protected abstract Result onExecute(Task task, OnTaskUpdate callback);

    public final boolean isStarted(){
        return mStatus!=Status.IDLE;
    }

    protected final boolean isResultSucceed(Result result){
        return null!=result&&result.isSucceed();
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

    public final int getStatus() {
        return mStatus;
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
      notifyTaskUpdate(status,null,callback);
    }

    protected final void notifyTaskUpdate(int status,Object object,OnTaskUpdate callback){
        notifyTaskUpdate(this,status,object,callback);
    }

    private final void notifyTaskUpdate(Task task,int status,Object object,OnTaskUpdate callback){
        if (null!=task&&task==this){
            mStatus=status;
            if (null!=object){
                if (object instanceof Progress){
                    mProgress=(Progress)object;
                }
                if (object instanceof Result){
                    mResult=(Result)object;
                }
            }
        }
        if (null!=callback){
            callback.onTaskUpdated(task,status);
        }
    }

}
