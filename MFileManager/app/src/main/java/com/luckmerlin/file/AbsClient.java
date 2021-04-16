package com.luckmerlin.file;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.luckmerlin.adapter.recycleview.SectionRequest;
import com.luckmerlin.core.Canceler;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;

public abstract class AbsClient<A extends Folder<T,V>,T,V extends Path> implements Client<A,T,V> {

    protected abstract Canceler query(T query, long from, long to, OnApiFinish<Reply<A>> callback);

    @Override
    public Canceler loadPathThumb(Context context,Path path, int width,int height,OnApiFinish<Thumb> callback) {
        if (null == path) {
            return null;
        } else if (path.isDirectory()) {
            return notifyApiFinish(What.WHAT_SUCCEED, null, R.drawable.hidisk_icon_folder, callback)?null:null;
        } else {
            String mime = path.getMime();
            Integer iconDefId = null;
            String pathValue = path.getPath();
            if (null == mime || mime.length() <= 0) {
                return notifyApiFinish(What.WHAT_SUCCEED, null, R.drawable.hidisk_icon_unknown, callback)?null:null;
            } else if (null != (iconDefId = new FileDefaultThumb().thumb(mime))) {
                return notifyApiFinish(What.WHAT_SUCCEED, null, iconDefId, callback)?null:null;
            } else if (null == pathValue || pathValue.length() <= 0) {
                return notifyApiFinish(What.WHAT_SUCCEED, null, null, callback)?null:null;
            }
        }
        return notifyApiFinish(What.WHAT_FAIL,null,null,callback)?null:null;
    }

    @Override
    public Canceler onNextSectionLoad(SectionRequest<T> request, OnApiFinish<Reply<A>> callback, String s) {
        T arg=null!=request?request.getArg():null;
        long from=null!=request?request.getFrom():-1;
        return from>=0?query(arg,from,from+request.getLimit(),callback):null;
    }

    protected final boolean notifyApiFinish(int what, String note, Object data, OnApiFinish callback){
        return notifyApiFinish(what,note,data,null,callback);
    }

    /**
     * @deprecated
     */
    protected final boolean notifyApiFinish(int what, String note, Object data, Object arg,OnApiFinish callback){
        if (null!=callback){
            callback.onApiFinish(what,note,data,arg);
            return true;
        }
        return false;
    }
}
