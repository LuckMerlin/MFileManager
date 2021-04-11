package com.luckmerlin.task;

import android.graphics.Color;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.R;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.task.Progress;

public abstract class Task implements Status{
    private Response mResponse;
    private Progress mProgress;
    private int mStatus=Status.IDLE;
    private boolean mCanceled=false;
    private final String mName;
    private long mStartTime;
    private long mEndTime;
    private String mAction;

    public Task(){
        this(null,null);
    }

    public Task(String name,String action){
        mName=name;
        mAction=action;
    }

    public final synchronized boolean execute(OnTaskUpdate callback) {
        if (isStarted()||isExecuting()){//Check if already doing
            return false;
        }
        mCanceled=false;
        mResponse=null;
        String name=mName;
        Debug.D("Start execute task "+(null!=name?name:"."));
        notifyTaskUpdate(Status.STARTED,null,callback);
        mStartTime=System.currentTimeMillis();
        mEndTime=-1;
        Response response=mResponse=onExecute(this,(Task task1, int status)-> {
            if (Status.STARTED!=status&&status!=Status.STARTED){
                notifyTaskUpdate(task1,status,null,callback);
            }
        });
        mEndTime=System.currentTimeMillis();
        Debug.D("Finish execute task "+(null!=name?name:".")+" "+isResponseSucceed(response));
        notifyTaskUpdate(Status.IDLE,null,callback);
        return true;
    }

    public final long getStartTime() {
        return mStartTime;
    }

    public final long getEndTime() {
        return mEndTime;
    }

    public final String getAction() {
        return mAction;
    }

    public final long getUsedTime(){
        long time=mStartTime;
        time=System.currentTimeMillis()-time;
        return time>0?time:0;
    }

    public final boolean isSucceed(){
        return isResponseSucceed(mResponse);
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
                Response response=mResponse;
                if (null==response){
                    return Color.WHITE;
                }else if (isResponseSucceed(response)){
                    return Color.GREEN;
                }else if (response.getCode()==What.WHAT_ALREADY_DONE){
                    return Color.parseColor("#5500ff00");
                }
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
        return null!=mResponse;
    }

    protected abstract Response onExecute(Task task, OnTaskUpdate callback);

    public final boolean isStarted(){
        return mStatus!=Status.IDLE;
    }

    protected final boolean isResponseSucceed(Response result){
        int code=null!=result?result.getCode():What.WHAT_FAIL;
        return (code==What.WHAT_SUCCEED)||(code==What.WHAT_ALREADY_DONE);
    }

    public final Response response(int code){
        return response(code,null);
    }

    public final Response response(int code,Object result){
        return new AbsResponse(code,result);
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

    public final Response getResponse() {
        return mResponse;
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
            if (null!=object&&object instanceof Progress){
                mProgress=(Progress)object;
            }
        }
        if (null!=callback){
            callback.onTaskUpdated(task,status);
        }
    }

    protected static final class AbsResponse<T> implements Response<T> {
        final int mCode;
        final T mResult;

        protected AbsResponse(int code,T result){
            mCode=code;
            mResult=result;
        }

        @Override
        public int getCode() {
            return mCode;
        }

        @Override
        public T getResult() {
            return mResult;
        }
    }
}
