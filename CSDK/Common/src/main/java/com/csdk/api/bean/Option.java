package com.csdk.api.bean;

/**
 * Create LuckMerlin
 * Date 18:20 2020/9/7
 * TODO
 */
public class Option {
    private final String mTitle;
    private final String mValue;

    public Option(String value, String title){
        mTitle=title;
        mValue=value;
    }

    public final String getValue() {
        return mValue;
    }

    public final String getTitle() {
        return mTitle;
    }

    @Override
    public String toString() {
        return "Option{" +
                "mTitle='" + mTitle + '\'' +
                ", mValue='" + mValue + '\'' +
                '}';
    }
}
