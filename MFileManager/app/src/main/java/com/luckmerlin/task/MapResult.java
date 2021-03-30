package com.luckmerlin.task;

import com.luckmerlin.file.task.Progress;

import java.util.HashMap;

public final class MapResult<T extends Task,V extends Response> extends HashMap<T,V> implements Response {

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public Progress getProgress() {
        return null;
    }
}
