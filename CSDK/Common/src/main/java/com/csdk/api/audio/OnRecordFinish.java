package com.csdk.api.audio;


public interface OnRecordFinish {
    void onRecordFinish(boolean succeed, long duration, String filePath);
}