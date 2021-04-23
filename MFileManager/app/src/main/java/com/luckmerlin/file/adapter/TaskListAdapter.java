package com.luckmerlin.file.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.adapter.recycleview.ItemSlideRemover;
import com.luckmerlin.adapter.recycleview.ItemTouchInterrupt;
import com.luckmerlin.adapter.recycleview.ListAdapter;
import com.luckmerlin.adapter.recycleview.OnItemSlideRemove;
import com.luckmerlin.adapter.recycleview.OnItemTouchResolver;
import com.luckmerlin.adapter.recycleview.Remover;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.R;
import com.luckmerlin.file.Thumbs;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.databinding.ItemTaskBinding;
import com.luckmerlin.file.task.Progress;
import com.luckmerlin.file.util.Time;
import com.luckmerlin.task.Response;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;

import java.util.List;

public class TaskListAdapter extends ListAdapter<Task> implements OnItemTouchResolver {

    @Override
    protected void onResolveFixedViewItem(RecyclerView recyclerView) {
        super.onResolveFixedViewItem(recyclerView);
        setFixHolder(TYPE_EMPTY,generateViewHolder(null!=recyclerView?recyclerView.getContext():null,R.layout.item_task_empty));
    }

    @Override
    public final RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext());
    }

    @Override
    protected Object onResolveDataViewHolder(ViewGroup viewGroup) {
        return R.layout.item_task;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ViewDataBinding binding, int i1, Task task, List<Object> list) {
        super.onBindViewHolder(viewHolder, i, binding, i1, task, list);
        if (null!=binding&&binding instanceof ItemTaskBinding){
            ItemTaskBinding taskBinding=(ItemTaskBinding)binding;
            taskBinding.setTask(task);
            Object buttonText=null!=task?(task.isAnyStatus(Status.IDLE)?task.isSucceed()?
                    R.string.succeed:R.string.start:R.string.cancel):null;
            taskBinding.setButtonText(buttonText);
            Object statusText=R.string.idle;
            String taskExecuteTime=null;
            String taskAction=null;
            if (null!=task){
                taskAction=task.getAction();
                long startTime=task.getStartTime();
                long endTime=task.getEndTime();
                taskExecuteTime=startTime>0?Time.formatMediaDuration((endTime>=startTime?
                        endTime:System.currentTimeMillis())-startTime):taskExecuteTime;
                switch (task.getStatus()){
                    case Status.EXECUTING: statusText=R.string.executing;break;
                    case Status.PREPARING: statusText=R.string.prepare;break;
                    case Status.STARTED: statusText=R.string.start;break;
                    case Status.IDLE: Response response=task.getResponse();
                        int code=null!=response?response.getCode(): What.WHAT_INVALID;
                        if (code==What.WHAT_ALREADY_DONE){
                            statusText=getText(R.string.alreadyWhich,null!=taskAction?taskAction:"");
                        }
                        break;
                }
            }
            Progress progress=task.getProgress();
            Object progressObj=null!=progress?progress.getProgress(Progress.TYPE_PERCENT):null;
            taskBinding.setProgress(null!=progressObj&&progressObj instanceof Number?((Number)progressObj).intValue():0);
            taskBinding.setTaskExecuteTime(taskExecuteTime);
            taskBinding.setStatusText(statusText);
            //Button color
            float[] outerR = new float[] { 20, 20, 20, 20, 20, 20, 20, 20 };
            RoundRectShape rr = new RoundRectShape(outerR, null, null);
            ShapeDrawable drawableNormal = new ShapeDrawable(rr);
            Paint paint=drawableNormal.getPaint();
            paint.setColor(Color.parseColor("#44ffffff"));
            paint.setStyle(Paint.Style.FILL);
            ShapeDrawable drawablePressed=new ShapeDrawable(rr);
            paint=drawablePressed.getPaint();
            paint.setColor(Color.parseColor("#11ffffff"));
            paint.setStyle(Paint.Style.FILL);
            final int[] STATE_NORMAL = {-android.R.attr.state_selected};
            final int[] STATE_SELECTED = {android.R.attr.state_selected};
            final int[] STATE_PRESSED = {android.R.attr.state_pressed};
            StateListDrawable drawable=new StateListDrawable();
            drawable.addState(STATE_SELECTED,drawablePressed);
            drawable.addState(STATE_PRESSED,drawablePressed);
            drawable.addState(STATE_NORMAL,drawableNormal);
            taskBinding.setButtonBackground(drawable);
        }
    }

    @Override
    public final Object onResolveItemTouch(RecyclerView recyclerView) {
        return new ItemTouchInterrupt(){
            @Override
            protected Integer onResolveSlide(RecyclerView.ViewHolder holder, RecyclerView.LayoutManager manager) {
                return new ItemSlideRemover().onResolveSlide(holder,manager);
            }
        };
    }
}
