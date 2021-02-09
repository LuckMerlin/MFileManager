package com.luckmerlin.file.task;

import com.luckmerlin.file.Path;
import com.luckmerlin.task.Task;
import java.util.List;

public abstract class ActionTask extends Task {
    private final List<Path> mPaths;

    public ActionTask(List<Path> paths){
        mPaths=paths;
    }

    public final List<Path> getPaths() {
        return mPaths;
    }

    public final boolean isEmpty(){
        List<Path> paths=mPaths;
        return null==paths||paths.size()<=0;
    }
}
