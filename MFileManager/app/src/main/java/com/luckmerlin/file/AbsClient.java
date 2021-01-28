package com.luckmerlin.file;

import com.luckmerlin.adapter.recycleview.SectionRequest;
import com.luckmerlin.core.Canceler;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;

public abstract class AbsClient<A extends Folder<T,V>,T,V extends Path> implements Client<A,T,V> {

    protected abstract Canceler query(T query, long from, long to, OnApiFinish<Reply<A>> callback);

    @Override
    public Canceler onNextSectionLoad(SectionRequest<T> request, OnApiFinish<Reply<A>> callback, String s) {
        T arg=null!=request?request.getArg():null;
        long from=null!=request?request.getFrom():-1;
        return from>=0?query(arg,from,from+request.getLimit(),callback):null;
    }
}
