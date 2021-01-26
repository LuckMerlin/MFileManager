package com.luckmerlin.file;

import com.luckmerlin.adapter.OnSectionLoadFinish;
import com.luckmerlin.adapter.recycleview.SectionRequest;
import com.luckmerlin.core.Canceler;

public abstract class AbsClient<T,V extends Path> implements Client<T,V> {

    protected abstract Canceler query(T path, long from, long to,OnSectionLoadFinish<T, V> callback);

    @Override
    public Canceler onNextSectionLoad(SectionRequest<T> request, OnSectionLoadFinish<T, V> callback, String s) {
        T arg=null!=request?request.getArg():null;
        long from=null!=request?request.getFrom():-1;
        return from>=0?query(arg,from,from+request.getLimit(),callback):null;
    }
}
