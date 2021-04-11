package com.luckmerlin.file.task;

import com.luckmerlin.file.Cover;
import com.luckmerlin.file.Path;
import com.luckmerlin.task.Task;

public abstract class FileTask<F extends Path,T extends Path> extends Task {
    private F mFrom;
    private T mTo;
    private int mCover= Cover.NONE;

    public FileTask(){
        this(null,null);
    }

    public FileTask(F from,T to){
        this(null,from,to);
    }

    public FileTask(String name,F from,T to){
        super(name,null);
        mFrom=from;
        mTo=to;
    }

    public final FileTask setCover(int cover) {
        this.mCover = cover;
        return this;
    }

    public final FileTask setFrom(F from) {
        this.mFrom = from;
        return this;
    }

    public final FileTask setTo(T to) {
        this.mTo = to;
        return this;
    }

    public final F getFrom() {
        return mFrom;
    }

    public final T getTo() {
        return mTo;
    }

    public final int getCover(){
        return mCover;
    }
}
