package com.luckmerlin.file.task;

import com.luckmerlin.file.Path;
import com.luckmerlin.task.Task;

public abstract class AbstractFileTask extends Task {
    private final Path mFrom;
    private final Path mTo;

    public AbstractFileTask(Path from,Path to){
        mFrom=from;
        mTo=to;
    }

    public final Path getFrom() {
        return mFrom;
    }

    public final Path getTo() {
        return mTo;
    }
}
