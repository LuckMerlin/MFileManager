package com.luckmerlin.task;

import android.content.Context;
import android.graphics.Color;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.task.CodeResult;
import com.luckmerlin.file.task.Progress;

public abstract class Task implements Status {
    private  String mId;
    private  Result mResult;
    private  Progress mProgress;
    private  int mStatus=Status.IDLE;
    private  Boolean mCanceled;
    private  Boolean mDeleteFailed;
    private  long mCreateTime;
    private  long mStartTime;
    private  String mName;
    private  long mEndTime;

    public Task(){
        this(null);
    }

    public Task(String name){
        mName=name;
    }

    public final synchronized boolean execute(Context context,boolean start,OnTaskUpdate callback) {
        if (isStarted()||isExecuting()){//Check if already doing
            return false;
        }
        mDeleteFailed=null;
        mCanceled=null;
        mResult=null;
        String name=getName();
        Debug.D("Start execute task "+(null!=name?name:"."));
        mStartTime=System.currentTimeMillis();
        mEndTime=-1;
        notifyTaskUpdate(Status.START,callback);
        Result result=onExecute(this,context,start,(Task task1, int status)-> {
            if (Status.START!=status){
                notifyTaskUpdate(task1,status,null,callback);
            }
        });
        mResult=null!=result?result:new CodeResult<>(What.WHAT_ERROR);
        mEndTime=System.currentTimeMillis();
        notifyTaskUpdate(Status.IDLE,callback);
        Debug.D("Finish execute task "+(null!=name?name:".")+" ");
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

    protected final Task setId(Object id) {
        if (null==mId){
            this.mId = null!=id?id.toString():null;
        }
        return this;
    }

    public final void setName(String name) {
        this.mName = name;
    }

    public final String getId() {
        return mId;
    }

    public final boolean isSucceed(){
        return isResultSucceed(mResult);
    }

    public final boolean isCanceled(){
        Boolean canceled=mCanceled;
        return null!=canceled&&canceled;
    }

    public final boolean cancel(boolean cancel){
        Boolean canceled=mCanceled;
        if (null==canceled||canceled!=cancel){
            mCanceled=cancel;
            return true;
        }
        return false;
    }

    protected final Task setCreateTime(long createTime) {
        this.mCreateTime = createTime;
        return this;
    }

    public final long getTime(){
        long createTime=mCreateTime;
        return createTime>0?createTime:getStartTime();
    }

    public final long getCreateTime() {
        return mCreateTime;
    }

    public int getStatusColor(){
        switch (mStatus) {
            case Status.START:
                return Color.parseColor("#008000");
            case Status.PREPARE:
                return Color.parseColor("#FFD700");
            case Status.IDLE:
                Result result=mResult;
                if (null==result){
                    return Color.WHITE;
                }else if (isResultSucceed(result)){
                    return Color.GREEN;
                }
                return Color.RED;
        }
        return Color.TRANSPARENT;
    }

    public String getName() {
        return mName;
    }

    public final Progress getProgress() {
        return mProgress;
    }

    public final boolean isFinished(){
        return null!=mResult;
    }

    protected abstract Result onExecute(Task task,Context context,boolean start, OnTaskUpdate callback);

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

    public final Task deleteFailed(boolean deleteFail){
        Boolean deleteFailed=mDeleteFailed;
        if (null==deleteFailed||deleteFailed!=deleteFail){
            mDeleteFailed=deleteFailed;
        }
        return this;
    }

    public final boolean isDeleteFailed() {
        return mDeleteFailed;
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
