package com.luckmerlin.task;

import java.util.Collection;
import java.util.HashMap;

public final class MapResult<T extends Task,V extends Result> extends HashMap<T,V> implements Result {

    @Override
    public boolean isSucceed() {
        synchronized (this){
            Collection<T> values=keySet();
            if (null!=values){
                for (T child:values) {
                    if (null==child||!child.isSucceed()){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
