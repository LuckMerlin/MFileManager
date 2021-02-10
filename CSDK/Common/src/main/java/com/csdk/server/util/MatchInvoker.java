package com.csdk.server.util;

import com.csdk.server.Matchable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create LuckMerlin
 * Date 14:13 2020/9/7
 * TODO
 */
public class MatchInvoker {

    public final  <T> ArrayList<T> invokeMatch(Collection<T> collection, Matchable matcher, int max){
        ArrayList<T> list=null;
        if (null != collection && collection.size() > 0) {
            list = new ArrayList<>();
            if (null==matcher){
                list.addAll(collection);
                return list;
            }
            Integer matchResult= Matchable.CONTINUE;
            for (T child : collection) {
                if (max>=0&&list.size()>=max){
                    break;
                }
                matchResult=null!=child?matcher.onMatch(child):null;
                if (null==matchResult||matchResult== Matchable.CONTINUE){
                    continue;
                }else if (matchResult== Matchable.MATCHED) {
                    list.add(child);
                }else if (matchResult== Matchable.BREAK) {
                   break;
                }
            }
        }
        return null!=list&&list.size()>0?list:null;
    }

    public final <T> List<T> invokeMatch(T[] arrays, Matchable matcher, int max){
        List<T> list=null;
        if (null != arrays && arrays.length > 0) {
            list = new ArrayList<>();
            Integer matchResult= Matchable.CONTINUE;
            for (T child : arrays) {
                if (max>=0&&list.size()>=max){
                    break;
                }
                if (null==child){
                    continue;
                }else if (null==matcher){
                    matchResult=Matchable.MATCHED;
                }else {
                    matchResult=matcher.onMatch(child);
                }
                if (null==matchResult||matchResult== Matchable.CONTINUE){
                    continue;
                }else if (matchResult== Matchable.MATCHED) {
                    list.add(child);
                }else if (matchResult== Matchable.BREAK) {
                    break;
                }
            }
        }
        return null!=list&&list.size()>0?list:null;
    }

}
