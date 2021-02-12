package com.csdk.ui.adapter;

import android.content.Context;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.csdk.api.bean.Message;
import com.csdk.ui.view.SwipeRefreshLayout;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Create LuckMerlin
 * Date 15:20 2020/8/27
 * TODO
 */
public class AutoScrollMessageAdapter extends ListAdapter<Message> {
    private WeakReference<RecyclerView> mReference;
    private Integer mTotal;

    public void onRefresh() {
        //Do nothing
    }

    public final boolean addMessagePage(Page<Object, Message> page){
        if (null!=page){
           int total= page.getTotal();
           mTotal=total>=0?total:mTotal;
           List<Message> messages=page.getData();
            final int size=null!=messages?messages.size():-1;
            if (size>0){
                return add(0,messages);
            }
        }
        return false;
    }

    private SwipeRefreshLayout getSwipeRefreshLayout(){
        WeakReference<RecyclerView> reference=mReference;
        RecyclerView recyclerView=null!=reference?reference.get():null;
        ViewParent parent=null!=recyclerView?recyclerView.getParent():null;
        return null!=parent&&parent instanceof SwipeRefreshLayout?((SwipeRefreshLayout)parent):null;
    }

    private boolean refreshSwipeAlpha(String debug){
        SwipeRefreshLayout layout=getSwipeRefreshLayout();
        if (null!=layout){
            Integer total=mTotal;
            layout.setCircleAlpha(null==total||getDataCount()<total?1:0);
            return true;
        }
        return false;
    }

    @Override
    protected void onAttachedRecyclerView(RecyclerView recyclerView) {
        if (null!=recyclerView){
            mReference=new WeakReference<>(recyclerView);
        }
        super.onAttachedRecyclerView(recyclerView);
        if (null!=recyclerView){//Check parent
            SwipeRefreshLayout layout=getSwipeRefreshLayout();
            if (null!=layout){
                refreshSwipeAlpha("While adapter attached to recycle view.");
                layout.setOnRefreshListener(()->{
                    onRefresh();
                    layout.setRefreshing(false);
                });
            }
        }
    }

    public final boolean scrollToLastPosition(boolean anim,String debug){
        WeakReference<RecyclerView> reference=mReference;
        final RecyclerView recyclerView=null!=reference?reference.get():null;
        if (null!=recyclerView) {
            int lastIndex=getItemCount()-1;
            if (anim){
                final AlphaAnimation mAnimation=new AlphaAnimation(0,1 );
                mAnimation.setDuration(600);
                recyclerView.startAnimation(mAnimation);
            }
            if (lastIndex>=0){
                recyclerView.scrollToPosition(lastIndex);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDataSizeChanged(int lastSize, int currentSize, int position, Message data) {
        super.onDataSizeChanged(lastSize, currentSize, position, data);
        refreshSwipeAlpha("After data size changed.");
    }

    @Override
    protected RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView recyclerView) {
        Context context=null!=recyclerView?recyclerView.getContext():null;
        return null!=context?new LinearLayoutManager(context, RecyclerView.VERTICAL,false){
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }:null;
    }

    @Override
    protected void onDetachedRecyclerView(RecyclerView recyclerView) {
        super.onDetachedRecyclerView(recyclerView);
        //Check parent
        ViewParent parent=null!=recyclerView?recyclerView.getParent():null;
        if (null!=parent&&parent instanceof SwipeRefreshLayout){
            SwipeRefreshLayout layout=(SwipeRefreshLayout)parent;
            layout.setOnRefreshListener(null);
        }
        //
        WeakReference<RecyclerView> reference=mReference;
        mReference=null;
        if (null!=reference){
            reference.clear();
        }
    }

}
