package com.csdk.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.csdk.api.bean.User;
import com.csdk.ui.R;
import com.csdk.ui.databinding.CsdkItemFriendBinding;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 16:20 2021/2/3
 * TODO
 */
public class FriendUserListAdapter extends UserListAdapter {
    private boolean mDisabledLogoClick=false;

    @Override
    protected void onResolveFixViewHolder(RecyclerView recyclerView) {
        super.onResolveFixViewHolder(recyclerView);
        setFixHolder(VIEW_TYPE_EMPTY, R.layout.csdk_friend_list_empty);
    }

    @Override
    protected Integer onResolveDataTypeLayout(ViewGroup parent) {
        return R.layout.csdk_item_friend;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, User data, ViewDataBinding binding, List<Object> payloads) {
        if (null!=binding&&binding instanceof CsdkItemFriendBinding){
            CsdkItemFriendBinding friendBinding=(CsdkItemFriendBinding)binding;
            friendBinding.setUser(data);
            friendBinding.setSelected(isUserSelected(data));
            friendBinding.setDisabled(mDisabledLogoClick);
        }
    }

}
