package com.luckmerlin.file;

import android.content.Context;
import com.luckmerlin.adapter.recycleview.SectionRequest;
import com.luckmerlin.core.Canceler;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;

public interface Client<A extends Folder<T,V>,T,V extends Path> {
    public String getName();

    public long getAvailable();

    public long getTotal();

    Canceler onNextSectionLoad(SectionRequest<T> request, OnApiFinish<Reply<A>> callback, String s) ;

    Canceler loadPathThumb(Context context,Path path, int width,int height,OnApiFinish<Thumb> callback);

    boolean setAsHome(Folder folder,OnApiFinish<Reply<? extends Path>> callback);

    boolean rename(Path path,String newName,boolean justName,OnApiFinish<Reply<V>> callback);

    boolean createPath(String name,boolean createFolder,OnApiFinish<Reply<V>> callback);

    boolean deletePath(String path,OnApiFinish<Reply<V>> callback);
}
