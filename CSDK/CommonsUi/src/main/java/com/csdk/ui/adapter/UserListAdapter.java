package com.csdk.ui.adapter;

import com.csdk.api.bean.User;
import com.csdk.server.Matchable;
import com.csdk.server.util.MatchInvoker;
import com.csdk.ui.data.UserListSortCompare;

import java.util.Collections;
import java.util.List;

/**
 * Create LuckMerlin
 * Date 16:18 2021/2/3
 * TODO
 */
public abstract class UserListAdapter extends ListAdapter<User> {
    private final UserListSortCompare mUserListSortCompare=new UserListSortCompare();
    private User mSelected;

    public final boolean setUserListWithSort(List<User> users,String debug){
        if (null!=users){
            UserListSortCompare compare=mUserListSortCompare;
            if (null!=compare){
                Collections.sort(users, compare);
            }
        }
        return super.set(users,true);
    }

    public final boolean replaceUserWithSort(User user,String debug){
        if (null!=user){
            List<User> copyList=getData();
            if (null!=copyList&&copyList.size()>0){
                int currentIndex=copyList.indexOf(user);
                if (currentIndex>=0){
                    copyList.remove(currentIndex);
                    copyList.add(user);
                    int newIndex=currentIndex;
                    UserListSortCompare compare=mUserListSortCompare;
                    if (null!=compare){
                        Collections.sort(copyList, compare);
                        newIndex=copyList.indexOf(user);
                    }
                    if (newIndex==currentIndex){//If index changed
                        return super.replace(user,false);
                    }else{
                        super.remove(user,debug);
                        return super.add(newIndex,user,true);
                    }
                }
            }
            return false;
        }
        return false;
    }

    public final boolean setSelect(User select){
        User current=mSelected;
        mSelected=select;
        if (null!=select){
            int index=indexDataPosition(select);
            if (index>=0){
                notifyItemChanged(index);
            }
        }
        if (null!=current){
            int index=indexDataPosition(current);
            if (index>=0){
                notifyItemChanged(index);
            }
        }
        return false;
    }

    protected final boolean isUserSelected(User  user){
        User selected=mSelected;
        return null!=user&&null!=selected&&selected.equals(user);
    }

    public final List<User> search(String text){
        return null!=text&&text.length()>0?new MatchInvoker().invokeMatch(getData(),(Object arg)-> {
            String roleName=null!=arg&&arg instanceof User?((User)arg).getRoleName():null;
            return null!=roleName&&roleName.toLowerCase().contains(text.toLowerCase())? Matchable.MATCHED:Matchable.CONTINUE;
        }, -1):null;
    }

}
