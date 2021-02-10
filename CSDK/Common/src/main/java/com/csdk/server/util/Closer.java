package com.csdk.server.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Create LuckMerlin
 * Date 17:00 2020/10/19
 * TODO
 */
public final class Closer {

    public boolean closeSafe(Closeable ...closeables){
        if (null!=closeables&&closeables.length>0){
            for (Closeable child:closeables) {
                if (null!=child){
                    try {
                        child.close();
                    } catch (IOException e) {
                        //Do nothing
                    }
                }
            }
        }
        return false;
    }
}
