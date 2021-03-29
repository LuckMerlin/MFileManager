package com.luckmerlin.file.task;

public interface Progress {
    int TYPE_SPEED=0x01;//0001
    int TYPE_DONE=0x2;//0010
    int TYPE_TITLE=0x4;
    Object getProgress(int type);
}
