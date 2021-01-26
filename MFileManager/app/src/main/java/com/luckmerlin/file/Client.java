package com.luckmerlin.file;

import com.luckmerlin.adapter.OnSectionLoadFinish;
import com.luckmerlin.adapter.recycleview.SectionRequest;
import com.luckmerlin.core.Canceler;

public interface Client<T,V extends Path> {
    public String getName();

    public long getAvailable();

    public long getTotal();

    Canceler onNextSectionLoad(SectionRequest<T> request, OnSectionLoadFinish<T, V> callback, String s) ;
}
