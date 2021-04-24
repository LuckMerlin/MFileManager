package com.luckmerlin.file.task;

import com.luckmerlin.file.api.What;
import com.luckmerlin.file.util.FileSize;
import com.luckmerlin.task.Response;
import java.util.ArrayList;
import java.util.List;

public class FileProgress<M> implements Progress{
     long mDone;
     long mTotal;
     Object mTitle;
     float mSpeed;

    @Override
    public Object getProgress(int type) {
        switch (type){
            case Progress.TYPE_DONE:
                return mDone;
            case Progress.TYPE_SPEED:
                return FileSize.formatSizeText(mSpeed)+"/s";
            case Progress.TYPE_TOTAL:
                return mTotal;
            case Progress.TYPE_TITLE:
                return mTitle;
            case Progress.TYPE_PERCENT:
                long total=mTotal;
                long upload=mDone;
                return total>0?(upload<=0?0:upload)*100.f/total:0;
            case (Progress.TYPE_DONE|Progress.TYPE_TOTAL):
                return FileSize.formatSizeText(mDone)+"/"+FileSize.formatSizeText(mTotal);
        }
        return null;
    }
}
