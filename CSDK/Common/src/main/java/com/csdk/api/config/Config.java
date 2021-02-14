package com.csdk.api.config;

/**
 * Create LuckMerlin
 * Date 11:59 2021/2/3
 * TODO
 */
public interface Config {
    boolean isGroupVoiceMessageEnable(String type);
    boolean isGroupMembersListVisible(String type);
    boolean isGroupTitleInvisible(String type);
    boolean isGroupLiveAudioEnabled(String type);
    boolean isGroupVoice2TextEnabled(String type);
    long getMessageWithdrawTime(String type);
    CharSequence getMenuIcon(String menuType);
}
