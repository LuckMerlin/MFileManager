package com.luckmerlin.file.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.adapter.recycleview.ItemSlideRemover;
import com.luckmerlin.adapter.recycleview.ItemTouchInterrupt;
import com.luckmerlin.adapter.recycleview.ListAdapter;
import com.luckmerlin.adapter.recycleview.OnItemSlideRemove;
import com.luckmerlin.adapter.recycleview.OnItemTouchResolver;
import com.luckmerlin.adapter.recycleview.Remover;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.file.R;
import com.luckmerlin.file.Thumbs;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.databinding.ItemTaskBinding;
import com.luckmerlin.file.task.Progress;
import com.luckmerlin.file.task.ThumbTask;
import com.luckmerlin.file.ui.TaskItemDeleteRunnable;
import com.luckmerlin.file.util.Time;
import com.luckmerlin.task.Response;
import com.luckmerlin.task.Result;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.WeakHashMap;

import luckmerlin.core.binding.DataBinding;

public class TaskListAdapter extends ListAdapter<Task> implements OnItemTouchResolver,OnItemSlideRemove{

    public boolean insert(List<Task> tasks,Task task, Comparator comparator){
        if (null!=tasks&&tasks.size()>0){
            task=null!=task?task:tasks.get(0);
            ArrayList<Task> data=null!=comparator?getData():null;
            int count=null!=data?data.size():-1;
            int index=0;
            Integer match=null;
            for (int i = 0; i < count; i++) {
                if (comparator.compare(task,data.get(i))>0){
                    index=i;
                    break;
                }
            }
            return insert(index,tasks,true,null);
        }
        return false;
    }

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
    public void onItemSlideRemove(int i, Object object, int i1, RecyclerView.ViewHolder viewHolder, Remover remover) {
        if (null!=object&&object instanceof Task){
            remover.remove(false);
            View itemView=null!=viewHolder?viewHolder.itemView:null;
            final Task task=(Task)object;
            if (i1==8){
//                ViewParent parent=itemView.getParent();
//                if (null!=parent&&parent instanceof ViewGroup){
//                    ViewGroup viewGroup=((ViewGroup)parent);
//                    WeakHashMap<Task,Runnable> hashMap=mDeleteRunnable;
//                    Runnable runnable=null!=hashMap?hashMap.get(task):null;
//                    if (null!=runnable){
//                        viewGroup.removeCallbacks(runnable);
//                        hashMap.remove(task);
//                    }else{
//                        hashMap.put(task,runnable=new TaskItemDeleteRunnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        });
//                        viewGroup.post(runnable);
//                    }
//                }
//                ViewDataBinding binding=null!=itemView?DataBindingUtil.getBinding(itemView):null;
//                if (null!=binding&&binding instanceof ItemTaskBinding){
//                    ItemTaskBinding taskBinding=(ItemTaskBinding)binding;
//                    if (task.isSucceed()){
//                        Debug.D("QQQQQQQQQQQ 1 "+task);
//                        TaskItemDeleteRunnable runnable=taskBinding.getDeleteCountdown();
//                        if (null!=runnable){
//                            itemView.removeCallbacks(runnable);
//                            Debug.D("QQQQQQQQQQQ 2 "+runnable);
//                            taskBinding.setDeleteCountdown(null);
//                        }else{
//                            Debug.D("QQQQQQQQQQQ 3 "+runnable);
//                            final ObservableField<Integer> counter=new ObservableField<>(11);
//                            taskBinding.setDeleteCountdown(runnable=new TaskItemDeleteRunnable(){
//                                @Override
//                                public void run() {
//                                    Task current=taskBinding.getTask();
//                                    Integer count=counter.get();
//                                    count=null!=count?count-1:-1;
//                                    Debug.D("QQQQQQQQQQQ 4 "+count+" "+current+" "+task+" "+viewHolder);
//                                    if (count>=0&&null!=current&&current==task){
//                                        counter.set(count);
//                                        itemView.removeCallbacks(this);
//                                        Debug.D("QQQQQQQQQQQ 5 "+count+" "+current+" "+task);
//                                        itemView.postDelayed(this,1000);
//                                        Debug.D("QQQQQQQQQQQ 6 "+count+" "+current+" "+task);
//                                        if (count==0){
//                                            Debug.D("删除 "+task);
//                                        }
//                                    }else{
//                                        Debug.D("QQQQQQQQQQQ 7 "+count+" "+current+" "+task+" "+viewHolder);
//                                        taskBinding.setDeleteCountdown(null);
//                                    }
//                                }
//
//                                @Override
//                                public ObservableField<Integer> getCounter() {
//                                    return counter;
//                                }
//                            });
//                            runnable.run();
//                        }
//                    }else{
//                        toast(getText(R.string.whichFailed,getText(R.string.delete)));
//                    }
//                }
            }
        }
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
            if (null!=task){
                long startTime=task.getStartTime();
                long endTime=task.getEndTime();
                taskExecuteTime=startTime>0?Time.formatMediaDuration((endTime>=startTime?
                        endTime:System.currentTimeMillis())-startTime):taskExecuteTime;
                switch (task.getStatus()){
                    case Status.START: statusText=R.string.executing;break;
                    case Status.PREPARE: statusText=R.string.prepare;break;
                    case Status.IDLE: statusText=getText(task.isSucceed()?R.string.succeed:R.string.fail);break;
                }
            }
            Progress progress=task.getProgress();
            Object progressObj=null!=progress?progress.getProgress(Progress.TYPE_PERCENT):null;
            taskBinding.setProgress(null!=progressObj&&progressObj instanceof Number?((Number)progressObj).intValue():0);
            taskBinding.setTaskExecuteTime(taskExecuteTime);
            taskBinding.setStatusText(statusText);
            taskBinding.setTaskThumb(null!=task&&task instanceof ThumbTask?((ThumbTask)task).getThumb():null);
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
