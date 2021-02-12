package com.csdk.ui.model;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableField;

import com.csdk.api.common.Api;
import com.csdk.api.ui.Model;
import com.csdk.api.ui.OnViewClick;
import com.csdk.ui.R;

/**
 * Create LuckMerlin
 * Date 16:34 2020/9/18
 * TODO
 */
public class AlertDialogModel extends Model implements OnViewClick {
    private final ObservableField<String> mMessage=new ObservableField<>();
    private final ObservableField<String> mTitle=new ObservableField<>();

    public AlertDialogModel(Api socket, String message) {
        super(socket);
        mMessage.set(message);
    }

    @Override
    public boolean onClicked(int viewId, View view, Object tag) {
        return false;
    }

    @Override
    public Object onResolveModelView(Context context) {
        return R.layout.csdk_alert_dialog;
    }

    public ObservableField<String> getMessage() {
        return mMessage;
    }

    public ObservableField<String> getTitle() {
        return mTitle;
    }
}
