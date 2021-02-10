package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 10:08 2021/1/28
 * TODO
 */
public interface OnTextTranslateFinish {
    void onTextTranslateFinish(boolean succeed,Object srcLang, String srcText, Object targetLang, String targetText);
}
