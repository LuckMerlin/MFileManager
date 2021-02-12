package com.csdk.ui.adapter;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.csdk.ui.R;
import com.csdk.ui.data.SystemMessage;
import com.csdk.ui.databinding.CsdkItemSystemMessageBinding;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 15:20 2021/2/3
 * TODO
 */
public class SystemMessageListAdapter extends ListAdapter<SystemMessage> {

    @Override
    protected Integer onResolveDataTypeLayout(ViewGroup parent) {
        return R.layout.csdk_item_system_message;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int position, SystemMessage data, ViewDataBinding binding, List<Object> payloads) {
        super.onBindViewHolder(holder, position, data, binding, payloads);
        if (null!=binding&&binding instanceof CsdkItemSystemMessageBinding){
            CsdkItemSystemMessageBinding notifyBinding=(CsdkItemSystemMessageBinding)binding;
            float[] outerR = new float[] { 8, 8, 8, 8, 8, 8, 8, 8 };
            ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(outerR, null, null));
            shapeDrawable.getPaint().setColor(Color.parseColor("#FFD56E00"));
            notifyBinding.setTitleBackground(shapeDrawable);
            notifyBinding.setNotify(data);
        }
    }
}
