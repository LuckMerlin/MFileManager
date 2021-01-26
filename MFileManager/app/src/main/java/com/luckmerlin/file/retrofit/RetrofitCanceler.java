package com.luckmerlin.file.retrofit;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;
import io.reactivex.disposables.Disposable;

public final class RetrofitCanceler implements Canceler {
    Disposable mDisposable;

    public boolean cancel(boolean cancel,String debug){
        Disposable disposable=mDisposable;
        if (null!=disposable){
            if (cancel&&!disposable.isDisposed()){
                disposable.dispose();
                Debug.D("Cancel api call "+(null!=debug?debug:"."));
                return true;
            }
        }
        return false;
    }
}
