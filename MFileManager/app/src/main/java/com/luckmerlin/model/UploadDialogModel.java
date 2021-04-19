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
    private final UploadPrepareListAdapter mPrepareListAdapter=new UploadPrepareListAdapter();
    private final Folder mFolder;
    private final Object mFiles;

    public UploadDialogModel(Object files,Folder folder){
        mFolder=folder;
        mFiles=files;
    }

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
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
            mStatus.set(R.string.prepare);
            final Context context=getContext();
            Observable.create((ObservableEmitter<Collection> emitter)->{
                Collection collection=(Collection)files;
                UriPath uriPath=new UriPath();
                Path path=null;
                final int[] skipCount=new int[]{0};
                for (Object child:collection) {
                    Object currentStatus=mStatus.get();
                    if (null!=currentStatus&&currentStatus instanceof Integer&&((Integer)currentStatus)==Status.PREPARING){
                        child = null != child && child instanceof Uri ? uriPath.getUriPath(context, (Uri) child) : child;
                        child = null != child && child instanceof String ? new File((String) child) : child;
                        child = null != child && child instanceof File ? LocalPath.create((File) child) : child;
                        if (null==(path=(null!=child&&child instanceof Path?(Path)child:null))||!path.isLocal()){
                            skipCount[0]+=1;
                            Debug.W("Skip upload one file.");
                        }
                        final Path finalPath=path;
                        post(()->{
                            StringBuffer buffer=new StringBuffer();
                            if (null!=finalPath){
                                String name=finalPath.getNameWithExtension();
                                if (null!=name){
                                    buffer.append(name).append("\n");
                                }
                                listAdapter.add(new UploadTask(finalPath,folder));
                            }
                            String message=getString(R.string.sureWhich,null,getString(R.string.upload,null));
                            if (null!=message){
                                buffer.append(message);
                            }
                            String skip=skipCount[0]>0?getString(R.string.skipWhich,null,""+skipCount[0]):null;
                            mMessage.set(skip!=null?(null!=message?message:"")+"\n"+skip:message);
                        });
                    }
                }
                emitter.onComplete();
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
           return true;
        }
        return false;
    }

    @Override
    public boolean onViewClick(View view, int i, int i1, Object o) {
        return false;
    }

    public final ArrayList<Path> getFiles() {
        return null;
    }

    public final ObservableField<Object> getStatus() {
        return mStatus;
    }

    public UploadPrepareListAdapter getPrepareListAdapter() {
        return mPrepareListAdapter;
    }

    @Override
    public final Object onResolveModel() {
        return R.layout.upload_dialog_model;
    }
}
