package com.luckmerlin.file;

import com.luckmerlin.adapter.recycleview.SectionRequest;
import com.luckmerlin.core.Canceler;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;

public interface Client<A extends Folder<T,V>,T,V extends Path> {
    public String getName();

    public long getAvailable();

    public long getTotal();

    Canceler onNextSectionLoad(SectionRequest<T> request, OnApiFinish<Reply<A>> callback, String s) ;
}
