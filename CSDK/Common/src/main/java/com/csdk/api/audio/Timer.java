package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 14:50 2020/10/14
 * TODO
 */
public class Timer {

    private final int mTimerCount;

    public Timer(int timerCount){
        mTimerCount=timerCount;
    }

    public boolean onTimerCount(int count,int max){
        return count==max;
    }

    public final int getTimerCount() {
        return mTimerCount;
    }
}
