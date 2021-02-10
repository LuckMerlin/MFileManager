package com.csdk.socket;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Create LuckMerlin
 * Date 10:48 2020/12/22
 * TODO
 */
 class SPIUtils {
    public static <E> E load(Class<E> clz) {
        if (clz == null) {
            SLog.e("load null clz error!");
            return null;
        }
        ServiceLoader<E> serviceLoader = ServiceLoader.load(clz, clz.getClassLoader());
        Iterator<E> it = serviceLoader.iterator();
        try {
            if (it.hasNext()) {
                E service = it.next();
                return service;
            }
        } catch (Throwable throwable) {
            SLog.e("load " + clz.getSimpleName() + " error! " + throwable.getMessage());
        }
        return null;
    }
}
