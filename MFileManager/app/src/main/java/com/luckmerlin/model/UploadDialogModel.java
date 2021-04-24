package com.luckmerlin.model;

import android.net.Uri;
import android.view.View;

import androidx.databinding.ObservableField;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.R;
import com.luckmerlin.file.adapter.UploadPrepareListAdapter;
import com.luckmerlin.file.task.UploadTask;
import com.luckmerlin.file.ui.UriPath;
import com.luckmerlin.lib.StringBuffer;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;
import java.io.File;
import java.util.Collection;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;

public class UploadDialogModel extends Model implements OnModelResolve, OnViewClick {
    private final ObservableField<Object> mStatus=new ObservableField<>(R.string.upload);
    private final ObservableField<String> mMessage=new ObservableField<>();
    private final boolean mDeleteSucceed;
    private final UploadPrepareListAdapter mPrepareListAdapter=new UploadPrepareListAdapter();
    private final Folder mFolder;
    private final Object mFiles;
    private boolean mCanceled=false;

    public UploadDialogModel(Object files,Folder folder,boolean deleteSucceed){
        mDeleteSucceed=deleteSucceed;
        mFolder=folder;
        mFiles=files;
    }

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        mCanceled=false;
        mStatus.set(R.string.preparing);
        Observable.create((ObservableEmitter<Collection> emitter)->{
            prepare(mFiles);
            post(()->mStatus.set(getString(R.string.sureWhich,null,getString(R.string.upload,null))));
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean setStatus(Object status){
        mStatus.set(status);
        return false;
    }

    private boolean prepare(Object files){
        Object currentStatus=mStatus.get();
        if (null==currentStatus||!(currentStatus instanceof Integer)||((Integer)currentStatus)!=R.string.preparing){
            return false;
        }else if (mCanceled){
            return setStatus(getString(R.string.whichFailed,null,getString(R.string.cancel,null)))&&false;
        }
        final UploadPrepareListAdapter listAdapter=mPrepareListAdapter;
        final Object status=mStatus.get();
        final Folder folder=mFolder;
        if (null==files||null==listAdapter||null==folder||folder.isLocal()){
            return setStatus(getString(R.string.whichFailed,null,getString(R.string.prepare,null)))&&false;
        }else if (null!=status&&status instanceof Integer&&(Integer)status==Status.PREPARING){
            return setStatus(getString(R.string.alreadyWhich,null,getString(R.string.prepare,null)))&&false;
        }else if (files instanceof Uri){
            return prepare(new UriPath().getUriPath(getContext(), (Uri) files));
        }else if (files instanceof String){
            return prepare(new File((String)files));
        }else if (files instanceof File){
            return prepare(LocalPath.create((File)files));
        }else if (files instanceof Collection){
            Collection collection=(Collection)files;
            for (Object child:collection) {
                if (mCanceled){
                    break;
                }
                prepare(child);
            }
           return true;
        }else if (files instanceof Path){
            final Path finalPath=(Path)files;
            final String folderName=folder.getNameWithExtension();
            post(()->{
                String name=null;
                StringBuffer buffer=new StringBuffer();
                buffer.append(null!=folderName&&folderName.length()>15?folderName.substring(0,14):folderName);
                buffer.append("\n🔼\n");
                if (null!=finalPath){
                    buffer.append(null!=(name=finalPath.getNameWithExtension())?(name.length()>15?name.substring(0,15):name)+"\n":null);
                    listAdapter.add(new UploadTask(finalPath,folder).deleteSucceed(mDeleteSucceed));
                }
                buffer.append(""+getString(R.string.summeryItemWhich,null,listAdapter.getDataCount()));
                mMessage.set(buffer.toString());
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean onViewClick(View view, int i, int i1, Object o) {
        if (i==R.string.cancel){
            mCanceled=true;
            return true;
        }
        return false;
    }

    public final boolean isCanceled(){
        return mCanceled;
    }

    public final List<Task> getTasks(){
        return mPrepareListAdapter.getData();
    }

    public final ObservableField<Object> getStatus() {
        return mStatus;
    }

    public final UploadPrepareListAdapter getPrepareListAdapter() {
        return mPrepareListAdapter;
    }

    public final ObservableField<String> getMessage() {
        return mMessage;
    }

    @Override
    public final Object onResolveModel() {
        return R.layout.upload_dialog_model;
    }
}
