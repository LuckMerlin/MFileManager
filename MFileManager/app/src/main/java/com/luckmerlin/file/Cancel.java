package com.luckmerlin.file;

import com.luckmerlin.core.Canceler;

public class Cancel implements Canceler {
    private boolean mCanceled=false;

    public Cancel(boolean canceled){
        mCanceled=canceled;
    }

    public final boolean isCanceled() {
        return mCanceled;
    }

    protected void onCancelChange(boolean canceled){
        //Do nothing
    }

    @Override
    public final boolean cancel(boolean b, String s) {
        if (b!=mCanceled){
            mCanceled=b;
            onCancelChange(b);
            return true;
        }
        return false;
    }
}
