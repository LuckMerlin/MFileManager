package com.csdk.ui.ue4;

import android.app.Activity;
import android.app.NativeActivity;
import android.view.InputQueue;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import com.csdk.debug.Logger;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Create LuckMerlin
 * Date 19:39 2020/12/2
 * TODO
 */
public final class NativityActivityTouchHocker {
    private Hocked mHocked;

    private Hocked hock(Activity activity){
        Hocked hocked=mHocked;
        if (null!=hocked){
            return hocked;
        }
        if (null==activity||!(activity instanceof NativeActivity)){
            Logger.E("Fail open input queue while activity invalid.");
            return null;
        }
        Window window=activity.getWindow();
        View decorView=null!=window?window.getDecorView():null;
        ViewParent parent=null!=decorView?decorView.getParent():null;
        if (null==parent){
            return null;
        }
        InputQueue inputQueue=getActivityInputQueue((NativeActivity) activity);
        Field  field4InputQueue=JReflectUtil.findField(parent.getClass(), "mInputQueue");
        return null!=inputQueue&&null!=parent&&null!=field4InputQueue?new Hocked(parent,inputQueue,field4InputQueue):null;
    }

    public boolean enableHocker(Activity activity, boolean enable,String debug) {
        Hocked hocked=null!=activity?hock(activity):null;
        Field field4InputQueue=null!=hocked?hocked.mField4InputQueue:null;
        ViewParent parent=null!=hocked?hocked.mRootView:null;
        InputQueue inputQueue=null!=hocked?hocked.mActivityInputQueue:null;
        if (field4InputQueue != null&&null!=parent&&null!=inputQueue) {
            try {
                field4InputQueue.set(parent, enable?null:inputQueue);
                Logger.D("Enable input queue."+enable+" "+(null!=debug?debug:"."));
                return true;
            } catch (Exception e) {
                Logger.E("Exception open input queue."+e);
                e.printStackTrace();
            }
        }
        return false;
    }

    private InputQueue getActivityInputQueue(NativeActivity activity){
        Field activityQueueField=null!=activity?JReflectUtil.findField(NativeActivity.class, "mCurInputQueue"):null;
        if (null!=activityQueueField){
            boolean accessible=activityQueueField.isAccessible();
            activityQueueField.setAccessible(true);
            try {
               Object object= activityQueueField.get(activity);
               return null!=object&&object instanceof InputQueue?((InputQueue)object):null;
            } catch (IllegalAccessException e) {
                Logger.E("Exception get activity input queue.e="+e);
                e.printStackTrace();
            }finally {
                activityQueueField.setAccessible(accessible);
            }
        }
        return null;
    }

    public boolean closeHocker(String debug){
        Hocked hocked=mHocked;
        mHocked=null;
        Field field4InputQueue=null!=hocked?hocked.mField4InputQueue:null;
        InputQueue inputQueue=null!=hocked?hocked.mActivityInputQueue:null;
        if (field4InputQueue != null&&null!=inputQueue) {
            Method m1 = JReflectUtil.findMethod(field4InputQueue.getClass(), "dispose");
            if (m1 != null) {
                JReflectUtil.tryInvoke(m1, inputQueue);
                Logger.D("Close input queue.");
                return true;
            }
        }
        return false;
    }

    private static class Hocked{
        private final ViewParent mRootView;
        private final Field mField4InputQueue;
        private final InputQueue mActivityInputQueue;

        private Hocked(ViewParent rootView,InputQueue activityInputQueue,Field field4InputQueue){
            mRootView=rootView;
            mActivityInputQueue=activityInputQueue;
            mField4InputQueue=field4InputQueue;
        }
    }

}
