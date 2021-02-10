package com.csdk.api.struct;

import com.csdk.api.core.Label;

import org.json.JSONObject;

/**
 * Create LuckMerlin
 * Date 10:25 2021/1/25
 * TODO
 */
public final class SimpleStruct extends Struct {
    private final String mTitle;
    private final String mType;
    private final Object mData;

    public SimpleStruct(JSONObject json) {
        this(null != json ? json.optString(Label.LABEL_TYPE, null) : null, null != json ? json.optString(Label.LABEL_TITLE, null) : null,
                null != json ? json.opt(Label.LABEL_DATA) : null);
    }

    public SimpleStruct(String type, String title, Object data) {
        mType = type;
        mTitle = title;
        mData = data;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public Object getData() {
        return mData;
    }

    @Override
    public String getType() {
        return mType;
    }
}
