package com.csdk.server.util;


/**
 * Create LuckMerlin
 * Date 10:50 2020/9/7
 * TODO
 */
public class TimeOutline {

    public String outline(long timeStamp,String def){
        timeStamp=System.currentTimeMillis()-timeStamp;
        if (timeStamp<60000){//1000 *60
            return "刚刚";
        }else if (timeStamp<3600000){//1000*60*60
            return (timeStamp/60000)+"分钟前";
        }else if (timeStamp<86400000){//1000*60*60*24
            return (timeStamp/3600000)+"小时前";
        }else if (timeStamp<2592000000L){//1000*60*60*24*30
            return (timeStamp/86400000)+"天前";
        }
        return "一个月前";
    }


}
