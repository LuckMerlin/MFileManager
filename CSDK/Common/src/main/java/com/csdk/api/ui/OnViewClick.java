package com.csdk.api.ui;

import android.view.View;

import com.csdk.api.ui.ClickListener;

/**
 * Create LuckMerlin
 * Date 11:45 2020/9/11
 * TODO
 */
public interface OnViewClick extends ClickListener {
    boolean onClicked(int viewId, View view,Object tag);
}
