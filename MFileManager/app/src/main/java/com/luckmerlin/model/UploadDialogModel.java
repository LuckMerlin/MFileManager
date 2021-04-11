package com.luckmerlin.model;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.R;
import com.luckmerlin.file.ui.UriPath;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UploadDialogModel extends Model implements OnModelResolve, OnViewClick {
    private ArrayList<Path> mFiles;

    public UploadDialogModel(Object files){
        prepare(files);
    }

    private boolean prepare(Object files){
        if (null==files){
            return false;
        }else if (files instanceof Path){
            List<Path> list=new ArrayList<>();
            list.add((Path)files);
            return prepare(list);
        }else if (files instanceof Collection){
            Context context=getContext();
            Collection collection=(Collection)mFiles;
            UriPath uriPath=new UriPath();
            List<Path> paths=mFiles=new ArrayList<>();
            for (Object child:collection){
                child=null!=child&&child instanceof Uri ?uriPath.getUriPath(context,(Uri)child):child;
                child=null!=child&&child instanceof String?new File((String)child):child;
                child=null!=child&&child instanceof File? LocalPath.create((File)child):child;
                if (null!=child&&child instanceof Path){
                    paths.add((Path)child);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onViewClick(View view, int i, int i1, Object o) {
        return false;
    }

    public final ArrayList<Path> getFiles() {
        return mFiles;
    }

    @Override
    public final Object onResolveModel() {
        return R.layout.upload_dialog_model;
    }
}
