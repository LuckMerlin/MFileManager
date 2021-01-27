package com.luckmerlin.file;

public interface Permission {
    int PERMISSION_NONE=0x000;
    int PERMISSION_READ=0x0001;
    int PERMISSION_WRITE=0x0010;
    int PERMISSION_EXECUTE=0x0100;
}
