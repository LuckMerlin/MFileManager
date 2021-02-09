package com.luckmerlin.model;


import android.view.View;

import androidx.databinding.ObservableField;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.R;

public class FileContextMenuModel extends Model implements OnModelResolve, OnViewClick {
    private final ObservableField<Path> mPath=new ObservableField<>();

    public FileContextMenuModel(Path path){
        mPath.set(path);
    }

    @Override
    public boolean onViewClick(View view, int i, int i1, Object o) {

        return false;
    }

    @Override
    public Object onResolveModel() {
        return R.layout.file_context_menu;
    }

    public ObservableField<Path> getPath() {
        return mPath;
    }
}
