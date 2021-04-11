package com.luckmerlin.task;

public abstract class FromToTask<T,V> extends Task {
    private final T mFrom;
    private final V mTo;

    public FromToTask(String name, T from, V to){
        super(name,null);
        mFrom=from;
        mTo=to;
    }

    public final T getFrom() {
        return mFrom;
    }

    public final V getTo() {
        return mTo;
    }
}
