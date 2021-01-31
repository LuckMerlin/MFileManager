package com.luckmerlin.task;

import java.util.ArrayList;

public class ArrayListTask<T extends Task> extends TaskCollection<T, ArrayList<T>>{

    public ArrayListTask() {
        this(0);
    }

    public ArrayListTask(int size) {
        this(size<=0?null:new ArrayList<>(size));
    }

    public ArrayListTask(ArrayList<T> tasks) {
        super(null!=tasks?tasks:new ArrayList<>());
    }

    public final boolean add(int index,T task,boolean skipEqualed){
        ArrayList<T> tasks=getTasks();
        if (null!=tasks){
            synchronized (tasks){
                int size=tasks.size();
                if (index<0||index>size||(skipEqualed&&tasks.contains(task))){
                    return false;
                }
                tasks.add(index,task);
                return true;
            }
        }
        return false;
    }

}
