package com.csdk.server.socket;

/**
 * Create LuckMerlin
 * Date 12:01 2020/12/16
 * TODO
 */
public abstract class ReConnector implements Runnable{
    private int mRetryCount;

    public ReConnector(){
        mRetryCount=0;
    }

    protected abstract void onRetry(int retryCount);

    public final int getRetryCount() {
        return mRetryCount;
    }

    public final int getRetrySchedule() {
        int retryCount=mRetryCount;
        if (retryCount>=0&&retryCount<10){
            if (retryCount==0){
                return 5000;//5s
            }
           return (retryCount)*10000;//count*10s
        }
        return -1;
    }

    @Override
    public final void run() {
        onRetry(mRetryCount++);
    }
}
