package com.csdk.api.ui;

import android.view.View;

import com.csdk.api.ui.ClickListener;

/**
 * Create LuckMerlin
 * Date 11:45 2020/9/11
 * TODO
 */
public interface OnViewLongClick extends ClickListener {
    boolean onLongClicked(int viewId, View view,Object tag);
}
