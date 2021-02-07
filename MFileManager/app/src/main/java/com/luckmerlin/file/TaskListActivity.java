package com.luckmerlin.file;

import android.app.Activity;

import com.luckmerlin.databinding.OnModelResolve;

public class TaskListActivity extends Activity implements OnModelResolve {

    @Override
    public Object onResolveModel() {
        return R.layout.task_list;
    }
}
