package com.csdk.api.audio;

/**
 * Create LuckMerlin
 * Date 10:11 2021/1/28
 * TODO
 */
public final class TextTranslating {
    private final String mText;
    private final Object mSrcLanguage;
    private final Object mTargetLanguage;
    private final OnTextTranslateFinish mCallback;

    public TextTranslating(String text, Object srcLanguage, Object targetLanguage, OnTextTranslateFinish callback){
        mText=text;
        mSrcLanguage=srcLanguage;
        mTargetLanguage=targetLanguage;
        mCallback=callback;
    }

    public OnTextTranslateFinish getCallback() {
        return mCallback;
    }

    public Object getSrcLanguage() {
        return mSrcLanguage;
    }

    public Object getTargetLanguage() {
        return mTargetLanguage;
    }

    public String getText() {
        return mText;
    }
}
