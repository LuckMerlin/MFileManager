package com.luckmerlin.file.task;


import com.luckmerlin.file.Path;

public interface Output {
      long getLength();
      CodeResult open(long seek) throws Exception;
      Path close();
      boolean write(byte[] buffer,int offset,int length) throws Exception;
      boolean delete();
}
