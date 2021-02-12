package com.csdk.ui.data;

import com.csdk.api.bean.User;

import java.util.Comparator;

/**
 * Create LuckMerlin
 * Date 13:53 2020/11/30
 * TODO
 */
public final class UserListSortCompare implements Comparator<User> {

    private long parseLevel(String level){
        if (null!=level&&level.length()>0){
            try {
                return Long.parseLong(level);
            }catch (Exception e){
                //Do nothing
            }
        }
        return -1;
    }

    @Override
    public int compare(User o1, User o2) {
        if (null!=o1&&null!=o2){
            int onlineMatch=(o2.isOnline()?1:0)-(o1.isOnline()?1:0);
            if (onlineMatch!=0){//Check online status for idle status
                return onlineMatch;
            }
//            String statusCode1=o1.getOnlineStatusCode();
//            String statusCode2=o2.getOnlineStatusCode();
//            boolean inArea1=null!=statusCode1&&statusCode1.equals(RoleStatus.IN_AREA);
//            boolean inArea2=null!=statusCode2&&statusCode2.equals(RoleStatus.IN_AREA);
//            int inAreaMatch=(inArea2?1:0)-(inArea1?1:0);
//            if (inAreaMatch!=0){//Check Area status
//                return inAreaMatch;
//            }
            long userLevel1=parseLevel(o1.getUserLevel());
            long userLevel2=parseLevel(o2.getUserLevel());
            if (userLevel1>userLevel2){
                return 1;
            }else if (userLevel1==userLevel2){
                String roleName1=o1.getRoleName();
                String roleName2=o2.getRoleName();
                if (null!=roleName1&&null!=roleName2){
                    return roleName1.compareToIgnoreCase(roleName2);
                }
            }
            //Check last login time compare
            long time1=o1.getLastOnlineTime(-1);
            long time2=o2.getLastOnlineTime(-1);
            if (time2>time1){
                return 1;
            }
        }
        return -1;
    }
}
