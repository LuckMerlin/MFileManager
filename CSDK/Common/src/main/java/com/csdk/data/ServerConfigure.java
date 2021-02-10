package com.csdk.data;
import com.csdk.api.config.Config;
import com.csdk.api.core.Debug;
import com.csdk.server.data.Bool;

import java.io.Serializable;
import java.util.Map;

/**
 * Create LuckMerlin
 * Date 15:48 2021/1/5
 * TODO
 */
public final class ServerConfigure implements Serializable, Config {
    private long time;
    private Map<String, GroupBean> group;
    private MessageBean message;

    @Override
    public long getMessageWithdrawTime(String type) {
        return 0;
    }

    @Override
    public boolean isGroupLiveAudioEnabled(String type) {
        GroupBean groupBean=null!=group?group.get(type):null;
        GroupBean.GroupMessageBean bean=null!=groupBean?groupBean.message:null;
        return null!=bean&& Bool.isYes(bean.talk);
    }

    @Override
    public boolean isGroupVoice2TextEnabled(String type) {
        GroupBean groupBean=null!=group?group.get(type):null;
        GroupBean.GroupMessageBean bean=null!=groupBean?groupBean.message:null;
        return null!=bean&& Bool.isYes(bean.voice2Text);
    }

    @Override
    public boolean isGroupVoiceMessageEnable(String type) {
        GroupBean groupBean=null!=group?group.get(type):null;
        GroupBean.GroupMessageBean bean=null!=groupBean?groupBean.message:null;
        return null!=bean&& Bool.isYes(bean.voice);
    }

    @Override
    public boolean isGroupMembersListVisible(String type) {
        GroupBean groupBean=null!=group?group.get(type):null;
        GroupBean.GroupMessageBean bean=null!=groupBean?groupBean.message:null;
        return null!=bean&& Bool.isYes(bean.showMembers);
    }

    @Override
    public boolean isGroupTitleInvisible(String type) {
        GroupBean groupBean=null!=group?group.get(type):null;
        GroupBean.GroupMessageBean bean=null!=groupBean?groupBean.message:null;
        return null!=bean&& Bool.isYes(bean.showInfo);
    }


    private static class MessageBean{
        private long withdrawTime;
        private long historyStoreTime;
    }

    private static class GroupBean{
        private String type;
        private String title;
        private int maxNum;
        private GroupMessageBean message;

        static class GroupMessageBean{
                int uic;//
                int voice;//语音消息发送开关
                int talk;//实时语音开关
                int voice2Text;//语音消息转换开关
                int translate;//翻译功能开关
                int messageOperate;//消息长按开关
                int showInfo;//群组title标题显示开关
                int showMembers;//群成员列表显示开关
        }
    }



}
