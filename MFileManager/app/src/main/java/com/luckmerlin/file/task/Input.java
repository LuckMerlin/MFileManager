package com.luckmerlin.file.task;

import com.luckmerlin.file.Path;

public interface Input  {

    CodeResult open(long seek) throws Exception;

    int read(byte[] buffer) throws Exception;

    Path close();

    public String getName();

    public long getLength();
}
