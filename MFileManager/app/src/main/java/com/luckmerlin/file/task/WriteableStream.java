package com.luckmerlin.file.task;

import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.Reply;

public interface WriteableStream {
     void write(byte[] b, int off, int len) throws Exception;
     Reply<? extends Path> close()throws Exception;
}
