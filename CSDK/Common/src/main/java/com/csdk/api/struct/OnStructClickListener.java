package com.csdk.api.struct;


import android.view.View;

public interface OnStructClickListener {
    void onStructClick(View view, Struct struct,CharSequence text,int start,int end);
}
