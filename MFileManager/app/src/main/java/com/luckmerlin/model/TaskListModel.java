package com.luckmerlin.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.R;
import com.luckmerlin.file.adapter.TaskListAdapter;
import com.luckmerlin.file.service.TaskBinder;
import com.luckmerlin.file.service.TaskService;
import com.luckmerlin.file.task.NasDownloadTask;
import com.luckmerlin.file.task.NasUploadTask;
import com.luckmerlin.file.task.UploadTask;
import com.luckmerlin.mvvm.service.OnModelServiceResolve;
import com.luckmerlin.mvvm.service.OnServiceBindChange;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TaskListModel extends Model implements OnModelServiceResolve, OnServiceBindChange, OnTaskUpdate, OnViewClick {
    private final TaskListAdapter mTaskListAdapter=new TaskListAdapter();
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
            mTaskListAdapter.set(taskBinder.getTasks(null,-1),null);
         }
    }

    @Override
    public boolean onViewClick(View view, int resId, int i1, Object o) {
       switch (resId){
           case R.drawable.selector_back:
               return onBackPressed(null);
       }
        return false;
    }

    private boolean onBackPressed(String debug){
        return finishActivity(debug);
    }

    @Override
    public void onTaskUpdated(Task task, int status) {
        mTaskListAdapter.replace(task,null);
    }

    public final RecyclerView.Adapter getTaskListAdapter(){
        return mTaskListAdapter;
    }
}
