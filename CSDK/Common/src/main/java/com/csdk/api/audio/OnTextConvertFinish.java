package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 18:28 2020/9/15
 * TODO
 */
public interface OnTextConvertFinish {
    void onTextConvertFinish(boolean succeed, String note, String text, Object src);
}
