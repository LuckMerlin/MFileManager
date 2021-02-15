package com.csdk.ui.model;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableField;

import com.csdk.api.bean.Group;
import com.csdk.api.bean.Menu;
import com.csdk.api.bean.Session;
import com.csdk.api.common.Api;
import com.csdk.api.config.Config;
import com.csdk.api.core.OnEventChange;
import com.csdk.api.ui.OnViewClick;
import com.csdk.ui.R;
import com.csdk.ui.adapter.GroupMemberListAdapter;

public class HomeGroupModel extends HomeSessionModel<Group> implements HomeContentModel, OnEventChange, OnViewClick {
    private final ObservableField<Boolean> mVisibleMemberList=new ObservableField<>();
    private final ObservableField<Boolean> mVisibleTitle=new ObservableField<>(true);
    private final ObservableField<Boolean> mLiveAudioEarphoneEnable=new ObservableField<>(true);
    private final ObservableField<Boolean> mLiveAudioMicrophoneEnable=new ObservableField<>(true);
    private final ObservableField<Boolean> mGroupLiveAudioEnable=new ObservableField<>();
    private final GroupMemberListAdapter mMemberListAdapter=new GroupMemberListAdapter();

    public HomeGroupModel(Api api) {
        super(api);
    }

    @Override
    public void onMenuSelect(Menu menu, Object arg) {
        Group group=null!=menu?menu.getGroup():null;
        super.setSession(group, "While menu select.");
        String groupType=null!=group?group.getType():null;
        Config config=getConfig();
        mVisibleMemberList.set(null==config||config.isGroupMembersListVisible(groupType));
        mVisibleTitle.set(null==config||config.isGroupTitleInvisible(groupType));
        mGroupLiveAudioEnable.set(null==config||config.isGroupLiveAudioEnabled(groupType));
    }

    @Override
    public boolean onClicked(int viewId, View view, Object tag) {
        if (viewId== R.id.csdk_homeGroupModel_earphoneEnableFL){
            Boolean enabled=mLiveAudioEarphoneEnable.get();
            boolean value=!(null!=enabled&&enabled);
            mLiveAudioEarphoneEnable.set(value);
            return true;
        }else if (viewId== R.id.csdk_homeGroupModel_microphoneEnableFL){
            Boolean enabled=mLiveAudioMicrophoneEnable.get();
            boolean value=!(null!=enabled&&enabled);
            mLiveAudioMicrophoneEnable.set(value);
            return true;
        }
        return false;
    }

    @Override
    public Object onResolveModelView(Context context) {
        return R.layout.csdk_home_group_model;
    }

    public ObservableField<Boolean> getLiveAudioMicrophoneEnable() {
        return mLiveAudioMicrophoneEnable;
    }

    public ObservableField<Boolean> getLiveAudioEarphoneEnable() {
        return mLiveAudioEarphoneEnable;
    }

    public GroupMemberListAdapter getMemberListAdapter() {
        return mMemberListAdapter;
    }

    public ObservableField<Boolean> getVisibleMemberList() {
        return mVisibleMemberList;
    }

    public ObservableField<Boolean> getLiveAudioEnable() {
        return mGroupLiveAudioEnable;
    }

    public ObservableField<Boolean> getVisibleTitle() {
        return mVisibleTitle;
    }
}
