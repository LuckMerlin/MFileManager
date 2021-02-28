package com.luckmerlin.task;

import java.util.Collection;
import java.util.HashMap;

public final class MapResult<T extends Task,V extends Result> extends HashMap<T,V> implements Result {

    @Override
    public int getCode() {
        return 0;
    }
}
