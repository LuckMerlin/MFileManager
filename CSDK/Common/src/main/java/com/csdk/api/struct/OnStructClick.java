package com.csdk.api.struct;

import android.view.View;
import com.csdk.api.ui.ClickListener;

/**
 * Create LuckMerlin
 * Date 16:49 2021/2/2
 * TODO
 */
public interface OnStructClick extends ClickListener {
    void onStructClicked(View view, String content, Struct struct, int start, int end);
}
