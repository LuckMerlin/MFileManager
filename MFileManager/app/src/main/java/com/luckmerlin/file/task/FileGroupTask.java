package com.luckmerlin.file.task;

import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.What;

import java.util.List;

public abstract class FileGroupTask extends GroupTask<Path> {
    private int mCover= What.WHAT_INVALID;

    public FileGroupTask(){
        this(null);
    }

    public FileGroupTask(String name){
        this(name,null);
    }

    public FileGroupTask(String name, List<Path> paths){
        super(name,paths);
    }

    public final FileGroupTask setCover(int cover) {
        this.mCover = cover;
        return this;
    }

    public final int getCover() {
        return mCover;
    }

}
