package com.luckmerlin.file.adapter;

import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.adapter.recycleview.ListAdapter;
import com.luckmerlin.file.R;
import com.luckmerlin.file.databinding.ItemTaskBinding;
import com.luckmerlin.task.Task;

import java.util.List;

public class TaskListAdapter extends ListAdapter<Task> {

    @Override
    protected void onResolveFixedViewItem(RecyclerView recyclerView) {
        super.onResolveFixedViewItem(recyclerView);
        setFixHolder(TYPE_EMPTY,generateViewHolder(null!=recyclerView?recyclerView.getContext():null,R.layout.item_task_empty));
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
        }
    }
}
