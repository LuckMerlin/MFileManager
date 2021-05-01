package com.luckmerlin.task;

public abstract class FromToTask<T,V> extends Task {
    private final T mFrom;
    private final V mTo;

    public FromToTask(String name, T from, V to){
        super(name);
        mFrom=from;
        mTo=to;
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
