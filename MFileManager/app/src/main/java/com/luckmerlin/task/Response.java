package com.luckmerlin.task;

public interface Response<T> {
   int getCode();
   T getResult();
}
