package com.csdk.ui.model;
import androidx.databinding.ObservableField;

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
    private final ObservableField<User> mUser=new ObservableField<>();

    public SingleSessionModel(Api api) {
        super(api);
    }


    public final boolean setSingleSession(User user,String debug){
        mUser.set(user);
        return true;
    }

    protected final User getCurrentSession(){
        return mUser.get();
    }

    public final ObservableField<User> getSession() {
        return mUser;
    }

    public final MessageListAdapter getMessageListAdapter() {
        return mMessageListAdapter;
    }
}
