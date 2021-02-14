package com.csdk.ui.model;
import com.csdk.api.bean.User;
import com.csdk.api.common.Api;
import com.csdk.api.ui.Model;
import com.csdk.ui.adapter.MessageListAdapter;

/**
 * Create LuckMerlin
 * Date 17:27 2021/2/3
 * TODO
 */
public abstract class SingleSessionModel extends Model {
    private final MessageListAdapter mMessageListAdapter=new MessageListAdapter();

    public SingleSessionModel(Api api) {
        super(api);
    }

    protected final User getSession(){
        return null;
    }

    public final MessageListAdapter getMessageListAdapter() {
        return mMessageListAdapter;
    }
}
