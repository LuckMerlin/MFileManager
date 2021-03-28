package com.luckmerlin.file.task;

public interface Progress {
    int TYPE_SPEED=0x01;
    int TYPE_DONE=0x3;
    Object getProgress(int type);
}
