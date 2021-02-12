package com.csdk.ui.data;

/**
 * Create LuckMerlin
 * Date 19:03 2021/1/21
 * TODO
 */
public final class SystemMessage {
    private final String mTitle;
    private final CharSequence mContent;

    public SystemMessage(String title, CharSequence content){
        mTitle=title;
        mContent=content;
    }

    public CharSequence getContent() {
        return mContent;
    }

    public String getTitle() {
        return mTitle;
    }
}
