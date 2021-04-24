package com.luckmerlin.file.task;

public interface Progress {
    int TYPE_SPEED=0x01;//0001
    int TYPE_DONE=0x2;//0010
    int TYPE_TITLE=0x4;//0100
    int TYPE_TOTAL=0x8;//1000
    int TYPE_PERCENT=0xf;//10000
    int TYPE_THUMB=0x20;//100000
    int TYPE_SUMMERY=0x40;//1000000
    Object getProgress(int type);
}
