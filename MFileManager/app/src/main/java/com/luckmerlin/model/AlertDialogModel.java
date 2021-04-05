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
    private final ObservableField<Object> mTitle=new ObservableField<>();
    private final ObservableField<Object> mMessage=new ObservableField<>();
    private final ObservableField<Object> mLeftText=new ObservableField<>();
    private final ObservableField<Object> mRightText=new ObservableField<>();
    private final ObservableField<Object> mContentLayout=new ObservableField<>();
    private final ObservableField<Object> mCenterText=new ObservableField<>();

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

    public final AlertDialogModel setLeftText(Object leftText) {
        mLeftText.set(leftText);
        return this;
    }

    public final AlertDialogModel setRightText(Object rightText) {
        mRightText.set(rightText);
        return this;
    }

    public final AlertDialogModel setCenterText(Object centerText) {
        mCenterText.set(centerText);
        return this;
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
