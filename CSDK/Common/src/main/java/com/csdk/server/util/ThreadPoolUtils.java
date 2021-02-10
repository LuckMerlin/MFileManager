package com.csdk.server.util;

import android.annotation.SuppressLint;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by JohnnyLiu on 2020/9/4.
 */

public class ThreadPoolUtils {

    //将CORE_POOL_SIZE，MAXIMUM_POOL_SIZE与KEEP_ALIVE_SECONDS设置得与Android SDK V28的AsyncTask类一致
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with csdk_background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4)); // 线程池核心线程数
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1; // 线程池最大的线程数，包括核心/非核心线程
    private static final int KEEP_ALIVE_SECONDS = 30; // 线程池非核心线程允许闲置的最长时间

    private volatile static ThreadPoolUtils instance;
    private ThreadPoolExecutor threadPoolExecutor = null;

    @SuppressLint("NewApi")
    private ThreadPoolUtils(){
        if (threadPoolExecutor == null) {
            threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128));
        }
    }

    private static class SingleTonHolder{
        private static ThreadPoolUtils INSTANCE = new ThreadPoolUtils();
    }

    public static ThreadPoolUtils getInstance(){
        return SingleTonHolder.INSTANCE;
    }

    /**
     * 将Runnable加入到线程池中并执行
     *
     * @param runnable
     */
    public void execute(Runnable runnable) {
        try {
            threadPoolExecutor.execute(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
