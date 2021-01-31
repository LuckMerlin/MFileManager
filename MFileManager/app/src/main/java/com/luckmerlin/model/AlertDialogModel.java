package com.luckmerlin.model;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.file.R;
import com.luckmerlin.file.databinding.AlertDialogBinding;

public class AlertDialogModel extends Model implements OnModelResolve,DialogModel {
    private final ObservableField<Object> mTitle=new ObservableField<>();

    public AlertDialogModel(Object title){
        mTitle.set(title);
    }

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        ViewDataBinding dataBinding=null!=view? DataBindingUtil.getBinding(view):null;
        if (null!=dataBinding&&dataBinding instanceof AlertDialogBinding){
            AlertDialogBinding alertDialogBinding=(AlertDialogBinding)dataBinding;
            alertDialogBinding.setVm(this);
        }
    }

    @Override
    public Object getCenterText() {
        return null;
    }

    @Override
    public Object getContentLayout() {
        return null;
    }

    @Override
    public Object getLeftText() {
        return null;
    }

    @Override
    public Object getMessageText() {
        return null;
    }

    @Override
    public Object getRightText() {
        return null;
    }

    @Override
    public Object getTitleText() {
        return mTitle;
    }

    @Override
    public Object onResolveModel() {
        return R.layout.alert_dialog;
    }
}
