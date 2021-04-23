package com.luckmerlin.model;

import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;
import com.luckmerlin.adapter.recycleview.OnItemSlideRemove;
import com.luckmerlin.adapter.recycleview.Remover;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.file.R;
import com.luckmerlin.file.adapter.TaskListAdapter;
import com.luckmerlin.file.service.TaskBinder;
import com.luckmerlin.file.service.TaskService;
import com.luckmerlin.file.task.UploadTask;
import com.luckmerlin.mvvm.service.OnModelServiceResolve;
import com.luckmerlin.mvvm.service.OnServiceBindChange;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskListModel extends Model implements OnModelServiceResolve, OnServiceBindChange,
        OnTaskUpdate, OnViewClick, OnItemSlideRemove {
    private final TaskListAdapter mTaskListAdapter=new TaskListAdapter();
    private final ObservableField<Integer> mTotal=new ObservableField<>();
    private final ObservableField<Integer> mDone=new ObservableField<>();
    private TaskBinder mTaskBinder;

    @Override
    public List<Intent> onServiceResolved(List<Intent> list) {
        list=null!=list?list:new ArrayList<>(1);
        list.add(new Intent(getContext(), TaskService.class));
        return list;
    }

    @Override
    public void onServiceBindChanged(IBinder iBinder, ComponentName componentName) {
        TaskBinder taskBinder=mTaskBinder=null!=iBinder&&iBinder instanceof TaskBinder?((TaskBinder)iBinder):null;
        if (null!=taskBinder){
            taskBinder.register(this,null);
            updateTasks(null,"After binder changed.");
            mTaskListAdapter.set(taskBinder.getTasks(null,-1),null);
         }
    }

    @Override
    public boolean onViewClick(View view, int resId, int i1, Object o) {
        Debug.D("QQQQQQQQQQQQQQ  "+resId+" "+view);
       switch (resId){
           case R.drawable.selector_back:
               return onBackPressed(null);
           case R.string.start:
               return startTask(o)||true;
           case R.string.cancel:
               return cancelTask(o)||true;
       }
        return false;
    }

    @Override
    public void onItemSlideRemove(int i, Object object, int i1, RecyclerView.ViewHolder viewHolder, Remover remover) {
       if (null!=object){
           if (object instanceof UploadTask){
               remover.remove(false);
           }
       }
    }

    private boolean updateTasks(Task task,String debug){
        TaskBinder binder=mTaskBinder;
        if (null!=binder){
            final int[] sum=new int[2];
            binder.getTasks((Object o)-> {
                if (null!=o&&o instanceof Task){
                    sum[0]+=(((Task)o).isFinished()?1:0);
                    sum[1]+=1;
                }
                return Matchable.CONTINUE;
            },1);
            mDone.set(sum[0]);
            mTotal.set(sum[1]);
            return true;
        }
        return false;
    }

    private boolean onBackPressed(String debug){
        return finishActivity(debug);
    }

    private boolean startTask(Object task){
        TaskBinder binder=mTaskBinder;
        return null!=task&&null!=binder&&binder.startTask(task);
    }

    private boolean cancelTask(Object task){
        TaskBinder binder=mTaskBinder;
        return null!=task&&null!=binder&&binder.cancelTask(task);
    }

    @Override
    public void onTaskUpdated(Task task, int status) {
        switch (status){
            case Task.IDLE:
                updateTasks(task,"While task idle status.");
                break;
            case Task.STARTED:
                updateTasks(task,"While task started status.");
                break;
        }
        mTaskListAdapter.replace(task,null);
    }

    public final RecyclerView.Adapter getTaskListAdapter(){
        return mTaskListAdapter;
    }

    public ObservableField<Integer> getTotal() {
        return mTotal;
    }

    public ObservableField<Integer> getDone() {
        return mDone;
    }
}
