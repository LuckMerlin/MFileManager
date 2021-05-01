package com.luckmerlin.file;

import com.luckmerlin.adapter.recycleview.Section;
import com.luckmerlin.core.debug.Debug;

import java.util.List;

public abstract class Folder<A,T> extends Path implements Section<A,T> {

    @Override
    public final String getMd5() {
        return null;
    }
}
