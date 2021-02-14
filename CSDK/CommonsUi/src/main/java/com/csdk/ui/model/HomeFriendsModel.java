package com.csdk.ui.model;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableField;

import com.csdk.api.bean.Menu;
import com.csdk.api.bean.User;
import com.csdk.api.common.Api;
import com.csdk.api.ui.Dialog;
import com.csdk.api.ui.OnViewClick;
import com.csdk.ui.R;
import com.csdk.ui.adapter.FriendUserListAdapter;
import com.csdk.ui.adapter.UserListAdapter;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 13:18 2021/2/2
 * TODO
 */
public class HomeFriendsModel extends SingleSessionModel implements OnViewClick,HomeContentModel{
    private final ObservableField<Integer> mSelectFriendTab=new ObservableField<>(R.id.csdk_homeChannelFriend_listTV);
    private final ObservableField<Integer> mFriendsSize=new ObservableField<>();
    private final ObservableField<Integer> mFriendOnLineSize=new ObservableField<>();
    private final ObservableField<String> mSearchInput=new ObservableField<>();
    private final ObservableField<User> mUser=new ObservableField<>();
    private final UserListAdapter mFriendsListAdapter=new FriendUserListAdapter();
    private final UserListAdapter mRecentListAdapter=new FriendUserListAdapter();
    private final UserListAdapter mSearchAdapter=new FriendUserListAdapter();
    private final ObservableField<UserListAdapter> mUserAdapter=new ObservableField<>(mFriendsListAdapter);

    public HomeFriendsModel(Api api) {
        super(api);
        mFriendsListAdapter.add(new User("2").setRoleName("是范德萨发"));//Test
        mFriendsListAdapter.add(new User("3"));//Test
        mFriendsListAdapter.add(new User("4"));//Test
        mFriendsListAdapter.add(new User("5"));//Test
    }

    @Override
    public Object onResolveModelView(Context context) {
        return R.layout.csdk_home_friends_model;
    }

    @Override
    public void onMenuSelect(Menu menu, Object arg) {
        if (null!=arg&&arg instanceof User){
            startChat((User)arg,"While menu select.");
        }
    }

    public final boolean startChat(User user,String debug){
        mUser.set(user);
        mFriendsListAdapter.setSelect(user);
        mRecentListAdapter.setSelect(user);
        mSearchAdapter.setSelect(user);
        return true;
    }

    @Override
    public boolean onClicked(int viewId, View view, Object tag) {
        if (viewId== R.id.csdk_homeChannelFriend_listTV||viewId== R.id.csdk_homeChannelFriend_recentTV){
            return selectFriendTab(viewId,"While home tab view click.")||true;
        }else if (viewId== R.id.csdk_homeChannelFriend_searchIV){
            return searchFriends("While search view click.")||true;
//        }else if (viewId== R.id.csdk_itemHomeFriend_rootFL){
//            return setUser(null!=tag&&tag instanceof User?(User)tag:null,"While view click.");
        }else if (viewId== R.id.csdk_friendChatSession_deleteMessageIV){
            return deleteMessage("After friend chat delete message view click.");
        }
        return false;
    }

    private boolean deleteMessage(String debug){
        final Dialog dialog=new Dialog(getContext());
        return dialog.setContentView(new AlertDialogModel(getApi(),getText(R.string.csdk_delete_user_chat_message)){
            @Override
            public boolean onClicked(int viewId, View view, Object tag) {
//                if (viewId== R.id.csdk_alertMessageDialog_sureTV&&deleteFriendCachedMessage(getChatUserId(), debug)){
//                    AudioManager manager= AudioManager.instance();
//                    AudioObject audioObject=null!=manager?manager.getPlaying():null;
//                    if (null!=audioObject){
//                        ChatMessageListAdapter adapter=getAdapter();
//                        if (null!=adapter&&null!=adapter.indexData(audioObject)){
//                            manager.stopPlayVoiceFile("While single session audio message clean.");
//                        }
//                    }
//                }
                return dialog.dismiss()||true;
            }
        }).show();
    }

    private boolean searchFriends(String debug){
        String text=mSearchInput.get();
        boolean empty=null==text||text.length()<=0;
        Integer tabLayoutId=mSelectFriendTab.get();
        if (null!=tabLayoutId){
            UserListAdapter searchAdapter=mSearchAdapter;
            List<User> searchList=null;
            if (tabLayoutId== R.id.csdk_homeChannelFriend_listTV){
                searchList=empty?mFriendsListAdapter.getData():mFriendsListAdapter.search(text);
            }else if (tabLayoutId== R.id.csdk_homeChannelFriend_recentTV){
                searchList=empty?mRecentListAdapter.getData():mRecentListAdapter.search(text);
            }
            if (null!=searchAdapter){
                searchAdapter.set(searchList,true);
                searchAdapter.setSelect(getSession());
            }
            mUserAdapter.set(searchAdapter);
            if (null==searchList||searchList.size()<=0){
                toast(R.string.csdk_searchFriendsEmpty);
            }
            return true;
        }
        return false;
    }

    private boolean selectFriendTab(int tabId,String debug){
        Integer current=mSelectFriendTab.get();
        if (null==current||current!=tabId){
            mSelectFriendTab.set(tabId);
            mSearchInput.set(null);
            if (tabId== R.id.csdk_homeChannelFriend_listTV){
                post(()->mUserAdapter.set(mFriendsListAdapter),debug);
            }else if (tabId== R.id.csdk_homeChannelFriend_recentTV){
                post(()->{ mUserAdapter.set(mRecentListAdapter);updateRecentContactList("While select recent tab."); },debug);
            }
            return true;
        }
        return false;
    }

    private final boolean updateRecentContactList(String debug){
//        ArrayList<String> list=getRecentContactUsers(debug);
//        if (null==list||list.size()<=0){
//            mRecentListAdapter.set(null, true);
//            return true;
//        }
//        return loadUsers((boolean succeed, int code, String note, List<User> data)-> {
//            if (succeed&&code== Code.CODE_SUCCEED&&data.size()>0){
//                List<User> result=new ArrayList<>();
//                for (User child:data) {
//                    if (null!=child&&child.isFriend()&&!child.isBlocked()){
//                        User cacheUsr=getUserProfileByUid(child.getId(),"While load recent list.");
//                        result.add(null!=cacheUsr?cacheUsr:child);
//                    }
//                }
//                post(()->mRecentListAdapter.set(result, true));
//            }
//        }, debug, list);
        return false;
    }

    public ObservableField<Integer> getFriendOnLineSize() {
        return mFriendOnLineSize;
    }

    public ObservableField<Integer> getFriendsSize() {
        return mFriendsSize;
    }

    public ObservableField<Integer> getSelectFriendTab() {
        return mSelectFriendTab;
    }

    public ObservableField<String> getSearchInput() {
        return mSearchInput;
    }

    public ObservableField<User> getUser() {
        return mUser;
    }

    public ObservableField<UserListAdapter> getUserAdapter() {
        return mUserAdapter;
    }
}
