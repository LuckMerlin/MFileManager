package com.luckmerlin.model;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.file.R;
import com.luckmerlin.file.databinding.AlertDialogBinding;

public class AlertDialogModel extends Model implements OnModelResolve,DialogModel {
    private final Object mTitle;
    private final Object mMessage;
    private final Object mLeftText;
    private final Object mRightText;
    private final ObservableField<Object> mContentLayout=new ObservableField<>();
    private final Object mCenterText;

    public AlertDialogModel(Object title,Object message,Object left,Object right){
        this(title,message,left,right,null);
    }

    public AlertDialogModel(Object title,Object message,Object left,Object right,Object center){
        mTitle=title;
        mMessage=message;
        mLeftText=left;
        mCenterText=center;
        mRightText=right;
    }

    public final AlertDialogModel setContentLayout(Object contentLayout) {
        mContentLayout.set(contentLayout);
        return this;
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
        return mCenterText;
    }

    @Override
    public Object getContentLayout() {
        return mContentLayout;
    }

    @Override
    public Object getLeftText() {
        return mLeftText;
    }

    @Override
    public Object getMessageText() {
        return mMessage;
    }

    @Override
    public Object getRightText() {
        return mRightText;
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
