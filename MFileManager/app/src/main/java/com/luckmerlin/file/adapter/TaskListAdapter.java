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

import com.luckmerlin.adapter.recycleview.ListAdapter;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.R;
import com.luckmerlin.file.databinding.ItemTaskBinding;
import com.luckmerlin.file.task.Progress;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;

import java.util.List;

public class TaskListAdapter extends ListAdapter<Task> {

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
            task.getProgress();
            //
            int status=null!=task?task.getStatus(): Status.IDLE;
            String buttonBgColor="ffffff";
            Object buttonText=R.string.pause;
            switch (status){
                case Status.EXECUTING:
                    buttonBgColor="008000";
                    break;
                case Status.PREPARING:
                    buttonBgColor="FFD700";
                    break;
                case Status.STARTED:
                    buttonBgColor="7CFC00";
                    break;
                default:
                    buttonText=R.string.started;
                    break;
            }
            taskBinding.setButtonText(buttonText);
            Debug.D("QQQQQQQQQQq "+task.getProgress());
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
}
