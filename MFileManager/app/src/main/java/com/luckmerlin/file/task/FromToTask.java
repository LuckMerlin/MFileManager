package com.luckmerlin.file.task;

import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Task;

public abstract class FromToTask<F,T> extends Task {
    private final F mFrom;
    private final T mTo;

    public FromToTask(F from,T to){
        mFrom=from;
        mTo=to;
    }

    abstract Result onExecute(F from,T to,Task task, OnTaskUpdate callback);

    @Override
    protected final Result onExecute(Task task, OnTaskUpdate callback) {
        return onExecute(mFrom,mTo,task,callback);
    }

    public final F getFrom() {
        return mFrom;
    }

    public final T getTo() {
        return mTo;
    }
}
