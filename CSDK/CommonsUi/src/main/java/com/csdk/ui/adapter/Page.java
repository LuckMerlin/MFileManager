package com.csdk.ui.adapter;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 16:27 2020/9/8
 * TODO
 */
public final class Page<A,D> {
    private int mTotal;
    private int mFrom;
    private List<D> mData;
    private A mArg;

    public Page(){
        this(0,null,null);
    }

    public Page(int from, A arg, List<D> data){
        this(from,arg,data,0);
    }

    public Page(int from, A arg, List<D> data, int total){
        mFrom=from;
        mArg=arg;
        mData=data;
        mTotal=total;
    }

    public int getFrom() {
        return mFrom;
    }

    public List<D> getData() {
        return mData;
    }

    public A getArg() {
        return mArg;
    }

    public int getTo(){
        int from=mFrom;
        List<D> data=mData;
        return (from<=0?0:from)+(null!=data?data.size():0);
    }

    public int getTotal() {
        return mTotal;
    }
}
