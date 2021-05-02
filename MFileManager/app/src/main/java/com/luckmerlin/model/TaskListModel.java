package com.luckmerlin.model;

import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;
import com.luckmerlin.adapter.recycleview.OnItemSlideRemove;
import com.luckmerlin.adapter.recycleview.Remover;
import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.file.R;
import com.luckmerlin.file.adapter.TaskListAdapter;
import com.luckmerlin.task.TaskBinder;
import com.luckmerlin.task.TaskService;
import com.luckmerlin.mvvm.service.OnModelServiceResolve;
import com.luckmerlin.mvvm.service.OnServiceBindChange;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;
import java.util.ArrayList;
import java.util.List;

import luckmerlin.core.dialog.MDialog;

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
       switch (resId){
           case R.drawable.selector_back:
               return onBackPressed(null);
           case R.string.start:
               return actionTask(Status.START,o)||true;
           case R.string.cancel:
               return actionTask(Status.CANCEL,o)||true;
           case R.drawable.selector_remove:
                Task task=null!=o&&o instanceof Task?(Task)o:null;
               if (null!=task&&!task.isSucceed()){
                   final MDialog dialog=new MDialog(getContext());
                   String message=getString(R.string.deleteFailedFile,null);
                   message=null!=message?message+"\n"+task.getName():null;
                   return null!=dialog.setContentView(new AlertDialogModel(R.string.notify,message,
                           R.string.remove,R.string.cancel,R.string.delete,null){
                           @Override
                           public boolean onViewClick(View view, int i, int i1, Object o) {
                               if (i==R.string.delete){
                                   actionTask(Status.DELETE,o);
                               }else if (i==R.string.remove){
                                   actionTask(Status.REMOVE,o);
                               }
                               return null!=dialog.dismiss()||true;
                           }
                   },null).fullscreen(false).show()||true;
               }
               return actionTask(Status.REMOVE,o)||true;
       }
        return false;
    }

    @Override
    public void onItemSlideRemove(int i, Object object, int i1, RecyclerView.ViewHolder viewHolder, Remover remover) {
       if (null!=object){
           if (object instanceof Task){
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

    private boolean actionTask(int action,Object ...tasks){
        TaskBinder binder=mTaskBinder;
        return null!=tasks&&tasks.length>0&&null!=binder&&binder.action(action,tasks);
    }

    @Override
    public void onTaskUpdated(Task task, int status) {
        switch (status){
            case Task.IDLE: updateTasks(task,"While task idle status.");break;
            case Task.START: updateTasks(task,"While task started status.");break;
            case Task.REMOVE: mTaskListAdapter.remove(task,"While task remove status.");return;
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
