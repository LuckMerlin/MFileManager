package com.luckmerlin.file.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.adapter.recycleview.ListAdapter;

public class LinearLayoutManager extends androidx.recyclerview.widget.LinearLayoutManager {

    public LinearLayoutManager(Context context) {
        super(context);
    }

    public LinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        //Make empty type view layout center
        if (getChildCount()==1){
            View view=getChildAt(0);
            if (null!=view&& ListAdapter.TYPE_EMPTY==getItemViewType(view)) {
                ViewParent parent=view.getParent();
                if (null!=parent&&parent instanceof View){
                    int width=view.getWidth();
                    int height=view.getHeight();
                    View parentView=(View)parent;
                    int left=(parentView.getWidth()-width)>>1;int top=(parentView.getHeight()-height)>>1;
                    view.layout(left, top ,left+width, top+height);
                }
            }
        }
    }

}
