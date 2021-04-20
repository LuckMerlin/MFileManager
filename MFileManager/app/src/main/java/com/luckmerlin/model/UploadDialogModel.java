package com.luckmerlin.model;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import androidx.databinding.ObservableField;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.databinding.dialog.Dialog;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.R;
import com.luckmerlin.file.adapter.UploadPrepareListAdapter;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.task.UploadTask;
import com.luckmerlin.file.ui.UriPath;
import com.luckmerlin.lib.ArraysList;
import com.luckmerlin.lib.StringBuffer;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
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
        prepare(mFiles);
    }

    private boolean setStatus(Object status){
        mStatus.set(status);
        return false;
    }

    private boolean prepare(Object files){
        final UploadPrepareListAdapter listAdapter=mPrepareListAdapter;
        final Object status=mStatus.get();
        final Folder folder=mFolder;
        if (null==listAdapter||null==folder||folder.isLocal()){
            return setStatus(getString(R.string.whichFailed,null,getString(R.string.prepare,null)))&&false;
        }else if (null!=status&&status instanceof Integer&&(Integer)status==Status.PREPARING){
            return setStatus(getString(R.string.alreadyWhich,null,getString(R.string.prepare,null)))&&false;
        }else if (null==files){
            return false;
        }else if ((files instanceof String)||(files instanceof Uri)||(files instanceof File)){
            return prepare(new ArraysList<>().addData(files));
        }else if (files instanceof Path){
            List<Path> list=new ArrayList<>();
            list.add((Path)files);
            return prepare(list);
        }else if (files instanceof Collection){
            mStatus.set(R.string.preparing);
            final Context context=getContext();
            Observable.create((ObservableEmitter<Collection> emitter)->{
                Collection collection=(Collection)files;
                UriPath uriPath=new UriPath();
                Path path=null;
                final int[] skipCount=new int[]{0};
                final String folderName=folder.getNameWithExtension();
                for (Object child:collection) {
                    if (mCanceled){
                        break;
                    }
                    Object currentStatus=mStatus.get();
                    if (null!=currentStatus&&currentStatus instanceof Integer&&((Integer)currentStatus)==R.string.preparing){
                        child = null != child && child instanceof Uri ? uriPath.getUriPath(context, (Uri) child) : child;
                        child = null != child && child instanceof String ? new File((String) child) : child;
                        child = null != child && child instanceof File ? LocalPath.create((File) child) : child;
                        if (null==(path=(null!=child&&child instanceof Path?(Path)child:null))||!path.isLocal()){
                            skipCount[0]+=1;
                            Debug.W("Skip upload one file.");
                        }
                        final Path finalPath=path;
                        post(()->{
                            String name=null;
                            StringBuffer buffer=new StringBuffer();
                            buffer.append(null!=folderName&&folderName.length()>15?folderName.substring(0,14):folderName);
                            buffer.append("\n🔼\n");
                            if (null!=finalPath){
                                buffer.append(null!=(name=finalPath.getNameWithExtension())?(name.length()>15?name.substring(0,15):name)+"\n":null);
                                listAdapter.add(new UploadTask(finalPath,folder).deleteSucceed(mDeleteSucceed));
                            }
                            int length=listAdapter.getDataCount();
                            buffer.append(length+"/"+(length+skipCount[0]));
                            mMessage.set(buffer.toString());
                        });
                    }
                }
                post(()->mStatus.set(getString(R.string.sureWhich,null,getString(R.string.upload,null))));
                emitter.onComplete();
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
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
