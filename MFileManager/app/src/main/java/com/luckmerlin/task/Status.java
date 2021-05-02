package com.luckmerlin.task;

public interface Status {
    int IDLE=0;
    int CANCEL = 0x01;//0000 0001
    int ADD = CANCEL<<1;//0000 0010
    int START = ADD<<1;//0000 0100
    int REMOVE = START<<1;//0000 1000
    int PREPARE = REMOVE<<1;
    int DELETE = PREPARE<<1;
    int DOING = DELETE<<1;//0000 01000
    /**
     * @deprecated
     */
    int STARTED=START;

    /**
     * @deprecated
     */
    int EXECUTING=DOING;
    /**
     * @deprecated
     */
    int PREPARING=PREPARE;
}
