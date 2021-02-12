package com.csdk.ui;

import android.content.Context;
import android.view.View;
import androidx.databinding.BindingAdapter;

import com.csdk.ui.util.ScaleUtils;
import com.csdk.ui.util.ScreenUtil;

public final class ViewScalePadding {
    public final static int NONE=0x00;//0000 0000
    public final static int LEFT=0x01;//0000 0001
    public final static int RIGHT=0x02;//0000 0010
    public final static int TOP=0x04;//0000 0100
    public final static int BOTTOM=0x08;//0000 1000
    private int mEnablePadding=NONE;

    public ViewScalePadding left(boolean enable){
        int current=mEnablePadding;
        mEnablePadding=enable?current|LEFT:(current&~LEFT);
        return this;
    }
    public ViewScalePadding right(boolean enable){
        int current=mEnablePadding;
        mEnablePadding=enable?current|RIGHT:(current&~RIGHT);
        return this;
    }

    public ViewScalePadding top(boolean enable){
        int current=mEnablePadding;
        mEnablePadding=enable?current|TOP:(current&~TOP);
        return this;
    }

    public ViewScalePadding bottom(boolean enable){
        int current=mEnablePadding;
        mEnablePadding=enable?current|BOTTOM:(current&~BOTTOM);
        return this;
    }

    public ViewScalePadding all(boolean enable){
        int current=mEnablePadding;
        int flag=(LEFT|TOP|BOTTOM|RIGHT);
        mEnablePadding=enable?current|flag:(current&~flag);
        return this;
    }

    public static ViewScalePadding l(boolean enable){
        return new ViewScalePadding().left(enable);
    }

    public static ViewScalePadding r(boolean enable){
        return new ViewScalePadding().right(enable);
    }

    public static ViewScalePadding b(boolean enable){
        return new ViewScalePadding().bottom(enable);
    }

    public static ViewScalePadding t(boolean enable){
        return new ViewScalePadding().top(enable);
    }

    public static ViewScalePadding a(boolean enable){
        return new ViewScalePadding().all(enable);
    }

    @BindingAdapter("viewScalePadding")
    public static void dotEventMatch(View view, ViewScalePadding padding){
        if (null!=view&&null!=padding) {
            Context context=view.getContext();
            ScaleUtils.initScaleParams(context);
            int contentWidth = ScaleUtils.getScaleWidth();
            int contentHeight = ScaleUtils.getScaleHeight();
            int[] size= ScreenUtil.getScreenSize(context);
            int screenWidth=size[0];
            int screenHeight=size[1];
            if (contentWidth>0&&contentHeight>0&&screenWidth>0&&screenHeight>0){
                int paddingWidth=(screenWidth-contentWidth)>>1;
                int paddingHeight=(screenHeight-contentHeight)>>1;
                paddingWidth=paddingWidth>=0?paddingWidth:0;
                paddingHeight=paddingHeight>=0?paddingHeight:0;
                int enablePadding=padding.mEnablePadding;
                int left=(enablePadding&LEFT)>0?paddingWidth:view.getLeft();
                int top=(enablePadding&TOP)>0?paddingHeight:view.getTop();
                int right=(enablePadding&RIGHT)>0?paddingWidth:view.getRight();
                int bottom=(enablePadding&BOTTOM)>0?paddingHeight:view.getBottom();
                view.setPadding(left,top,right,bottom);
            }
        }
    }

}
