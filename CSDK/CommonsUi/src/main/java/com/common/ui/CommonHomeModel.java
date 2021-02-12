package com.common.ui;

import android.content.Context;
import com.csdk.api.common.Api;
import com.csdk.api.ui.Model;

public class CommonHomeModel extends Model{

    public CommonHomeModel(Api api) {
        super(api);
    }

    @Override
    public Object onResolveModelView(Context context) {
        return R.layout.home_model;
    }
}
