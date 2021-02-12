package com.csdk.ui.model;

import androidx.databinding.ObservableField;

import com.csdk.api.bean.Group;
import com.csdk.api.bean.Message;
import com.csdk.api.bean.Session;
import com.csdk.api.bean.User;
import com.csdk.api.common.Api;
import com.csdk.api.core.MessageType;
import com.csdk.api.core.OnEventChange;
import com.csdk.api.ui.Model;
import com.csdk.ui.adapter.MessageListAdapter;

/**
 * Create LuckMerlin
 * Date 15:08 2021/2/2
 * TODO
 */
public abstract class HomeSessionModel<T extends Session> extends Model implements OnEventChange {
    private final ObservableField<T> mSession=new ObservableField<>();
    private final MessageListAdapter mMessageListAdapter=new MessageListAdapter();

    public HomeSessionModel(Api api) {
        super(api);
    }

    protected final boolean setSession(T session,String debug){
        Session current=mSession.get();
        mSession.set(session);
        //Check session if changed
        if (!((null==current&&null==session)||(null!=current&&null!=session&&current.equals(current)))){
            mMessageListAdapter.clean("While session changed "+(null!=debug?debug:"."));
            //Load
        }
        return true;
    }

    @Override
    public void onEventChanged(int event, Object arg) {
        switch (event) {
            case EVENT_MESSAGE_RECEIVED://Get through
            case EVENT_MESSAGE_SENDING://Get through
            case EVENT_MESSAGE_SENT:
                MessageListAdapter adapter = mMessageListAdapter;
                if (null != adapter && null != arg && arg instanceof Message) {
                    Message message = (Message) arg;
                    if (isSelfMessage(message)) {
                        boolean needScroll = (event == EVENT_MESSAGE_RECEIVED || event == EVENT_MESSAGE_SENDING) && adapter.indexData(message) == null;
                        boolean succeed = adapter.replaceMessageWithResendStatusCheck(message, "After group session message event changed.");
                    }
                }
        }
    }

    protected final boolean isSelfMessage(Message message) {
        T session=mSession.get();
        if (null != session) {
            if (session instanceof Group){
                Group group=(Group)session;
                String gid = group.getId();
                String gt = group.getType();
                String groupId = message.getGroupId();
                String groupType = message.getGroupType();
                return null != groupId && null != groupType && null != gid && null != gt && gid.equals(groupId) && groupType.equals(gt);
            }else if (session instanceof User){
                User user = ((User)session);
                String userId = null != user && message.getMsgType() == MessageType.MESSAGETYPE_SINGLE?user.getId():null;
                if (null != userId) {
                    //If message is self send,Just to match `to uid`,otherwise match `from uid` for received from others
                    String targetUid=message.isSelfMessage()?message.getFirstToUid():message.getFromUid();
                    return null != targetUid && userId.equals(targetUid);
                }
            }
        }
        return false;
    }

    public final T getSessionObject() {
        return mSession.get();
    }

    public final ObservableField<T> getSession() {
        return mSession;
    }

    public final MessageListAdapter getMessageAdapter() {
        return mMessageListAdapter;
    }

}
