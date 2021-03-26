package com.luckmerlin.lib;

import java.util.ArrayList;

public class ArraysList<T> extends ArrayList<T> {

    public ArraysList<T> addData(T data){
        if (null!=data){
            super.add(data);
        }
        return this;
    }
}
