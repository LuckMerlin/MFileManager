package com.luckmerlin.task;

public abstract class FromToTask<T,V> extends Task {
    private T mFrom;
    private V mTo;

    public FromToTask(){
        this(null,null,null);
    }

    public FromToTask(String name, T from, V to){
        super(name);
        mFrom=from;
        mTo=to;
    }

    protected final FromToTask setFrom(T from) {
        this.mFrom = from;
        return this;
    }

    protected final FromToTask setTo(V to) {
        this.mTo = to;
        return this;
    }

    public final T getFrom() {
        return mFrom;
    }

    public final V getTo() {
        return mTo;
    }

    @Override
    public boolean equals(Object o) {
        if (null!=o&&o instanceof FromToTask){
            FromToTask fromToTask=(FromToTask)o;
            Object fromT=fromToTask.mFrom;
            Object toT=fromToTask.mTo;
            Object from=mFrom;
            Object to=mTo;
            return ((null==fromT&&null==from)||(null!=from&&null!=fromT&&from.equals(fromT)))&&
                    ((null==toT&&null==to)||(null!=to&&null!=toT&&to.equals(toT)));
        }
        return false;
    }

}
