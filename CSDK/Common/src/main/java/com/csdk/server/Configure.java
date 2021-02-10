package com.csdk.server;

import com.csdk.api.config.Config;
import com.csdk.data.ServerConfigure;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Create LuckMerlin
 * Date 16:33 2021/1/4
 * TODO
 */
public final class Configure {
    private final Map<String, String> mConfigs=new HashMap<>();
    private ServerConfigure mServerConfigure;
    private String mBuildVersion="2021012001";
    private String mVersion="1.1.2";
    private String mLanguage;
    private String mDeviceId;
    private long mProtocolVersion=3;

    private Configure() {

    }

    private static class SingletonHolder {
        private static final Configure sInstance = new Configure();
    }

    public static Configure getInstance() {
        return SingletonHolder.sInstance;
    }

    public String getVersion() {
        return mVersion;
    }

    public String getBuildVersion() {
        return mBuildVersion;
    }

    public final boolean settSystemLanguage(String value){
        mLanguage=value;
        return true;
    }

    public final boolean setDeviceId(String value){
        mDeviceId=value;
        return true;
    }

    public final String getDeviceId(){
        return mDeviceId;
    }

    public final String getSystemLanguage(){
        return mLanguage;
    }

    public final boolean put(String key,Boolean value){
        return null!=key&&null!=value&&put(key, Boolean.toString(value));
    }

    public final int getMessageMaxCharSize(){
        return 300;
    }

    public final boolean put(String key,String value){
        Map<String, String> configs=mConfigs;
        if (null!=key&&null!=configs){
            synchronized (configs){
                if (null==value){
                    configs.remove(key);
                }else{
                    configs.put(key,value);
                }
                return true;
            }
        }
        return false;
    }

    public final boolean isEncrptyLogEnable(){
        return new File("/sdcard/csdk.log").exists();
    }

    public final String get(String key,String def){
        Map<String, String> configs=mConfigs;
        if (null!=key&&null!=configs){
            synchronized (configs){
                return configs.get(key);
            }
        }
        return def;
    }

    public final boolean getBoolean(String key,boolean def){
        String value=get(key, null);
        if (null!=value&&value.length()>0){
            if (value.equalsIgnoreCase(Boolean.toString(true))){
                return true;
            }else if (value.equalsIgnoreCase(Boolean.toString(false))){
                return false;
            }
        }
        return def;
    }


    public boolean putGroupConfig(ServerConfigure configure,String debug){
        mServerConfigure=configure;
        return false;
    }

    public boolean isLiveAudioEnable(){

        return false;
    }

    public long getProtocolVersion() {
        return mProtocolVersion;
    }

    public Config getConfig() {
        return mConfig;
    }

    private final Config mConfig=new Config() {
        @Override
        public boolean isGroupVoiceMessageEnable(String type) {
            Config config=mServerConfigure;
            return null!=config&&config.isGroupVoiceMessageEnable(type);
        }

        @Override
        public boolean isGroupVoice2TextEnabled(String type) {
            Config config=mServerConfigure;
            return null!=config&&config.isGroupVoice2TextEnabled(type);
        }

        @Override
        public boolean isGroupMembersListVisible(String type) {
            Config config=mServerConfigure;
            return null!=config&&config.isGroupMembersListVisible(type);
        }

        @Override
        public boolean isGroupTitleInvisible(String type) {
            Config config=mServerConfigure;
            return null!=config&&config.isGroupTitleInvisible(type);
        }

        @Override
        public boolean isGroupLiveAudioEnabled(String type) {
            Config config=mServerConfigure;
            return null!=config&&config.isGroupLiveAudioEnabled(type);
        }

        @Override
        public long getMessageWithdrawTime(String type) {
            return 0;
        }


    };
}
