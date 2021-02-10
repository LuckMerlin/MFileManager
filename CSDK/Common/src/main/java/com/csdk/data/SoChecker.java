package com.csdk.data;

import android.content.Context;

import com.csdk.debug.Logger;

import java.lang.reflect.Method;


/**
 * Create LuckMerlin
 * Date 15:08 2020/11/30
 * TODO
 */
public final class SoChecker {
    public final static String[] GME_SO=new String[]{"libgmecodec.so","libgmesdk.so","libsilk.so","libtraeimp.so"};

    public String checkNotExist(final Context context, String ...soNames) {
        try {
            if (null!=context){
                Class cls=Class.forName("com.tencent.TMG.ITMGContext");
                if (null!=cls){
                    Method instanceMethod=cls.getDeclaredMethod("GetInstance", Context.class);
                    if (null!=instanceMethod){
                        instanceMethod.setAccessible(true);
                        Object instanceObj=instanceMethod.invoke(null, context);
                        return null;
                    }
                }
            }
        } catch (Throwable e) {
            Logger.D("Exception check so "+e);
            e.printStackTrace();
            return "GM";
        }
        return null;
    }
}
