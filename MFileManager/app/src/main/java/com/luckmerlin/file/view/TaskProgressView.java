package com.luckmerlin.file.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.luckmerlin.file.TaskColors;
import com.luckmerlin.task.Task;

public class TaskProgressView extends View {
    private float mProgress;

    public TaskProgressView(Context context) {
        this(context,null);
    }

    public TaskProgressView(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TaskProgressView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTask(Task task){
        int color= new TaskColors().createColor(task);
        Drawable drawable=getBackground();
        if (null!=task&&task instanceof ProgressTask){
            mProgress=((ProgressTask)task).getProgress();
        }else if (null!=drawable&&drawable instanceof ColorDrawable&&((ColorDrawable)drawable).getColor()==color){
            return;
        }
        setBackgroundColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int left=getLeft();
        int right=getRight();
        if (left<=0||right<=left){
            left=right=0;
        }
        float progress=mProgress;
        canvas.clipRect(left,getTop(),left+((right-left)*(progress<=0?0:progress>=1?1:progress)),getBottom());
        super.onDraw(canvas);
    }
}
