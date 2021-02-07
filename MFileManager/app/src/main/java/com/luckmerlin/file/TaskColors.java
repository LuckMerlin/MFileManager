package com.luckmerlin.file;

import android.graphics.Color;

import com.luckmerlin.task.Task;

public class TaskColors {

    public int createColor(Task task){
        int color= Color.TRANSPARENT;
        if (task.isExecuting()){
            color= Color.parseColor("#550000FF");
        }else if (task.isCanceled()){
            color=Color.parseColor("#55FFFF00");
        }else if (task.isSucceed()){
            color=Color.parseColor("#55308014");
        }
        return color;
    }
}
