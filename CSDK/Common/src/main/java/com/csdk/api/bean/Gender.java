package com.csdk.api.bean;

/**
 * Create LuckMerlin
 * Date 18:22 2020/9/7
 * TODO
 */
public final class Gender extends Option {
    public final static int NONE=0;
    public final static int MAN=1;
    public final static int FEMALE=2;

    public Gender(String sex, String title){
        super(sex,title);
    }

}
