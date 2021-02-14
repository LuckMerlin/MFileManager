package com.csdk.server.socket;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.csdk.api.bean.AddFriendRequest;
import com.csdk.api.bean.Authentication;
import com.csdk.api.bean.CSDKAction;
import com.csdk.api.bean.ChatConfig;
import com.csdk.api.bean.Group;
import com.csdk.api.bean.LoginAuth;
import com.csdk.api.bean.Menu;
import com.csdk.api.bean.Message;
import com.csdk.api.bean.Receipt;
import com.csdk.api.bean.Reply;
import com.csdk.api.core.OnEventChange;
import com.csdk.api.core.OnSendFinish;
import com.csdk.api.core.Role;
import com.csdk.debug.Logger;
import com.csdk.server.cache.Cacheable;
import com.csdk.data.ServerConfigure;
import com.csdk.api.bean.Session;
import com.csdk.api.bean.User;
import com.csdk.api.core.Code;
import com.csdk.api.core.Event;
import com.csdk.api.core.GroupType;
import com.csdk.api.core.Listener;
import com.csdk.api.core.MessageType;
import com.csdk.api.core.OnMessageReply;
import com.csdk.api.core.Operation;
import com.csdk.api.core.Status;
import com.csdk.data.AuthenticationRequest;
import com.csdk.server.Configure;
import com.csdk.server.Matchable;
import com.csdk.server.MessageObject;
import com.csdk.server.Notify;
import com.csdk.server.OnChannelLoadFinish;
import com.csdk.server.OnServerConfigureLoadFinish;
import com.csdk.server.OnUserProfileLoadFinish;
import com.csdk.server.cache.HttpCaller;
import com.csdk.server.data.Frame;
import com.csdk.server.data.Json;
import com.csdk.api.core.Label;
import com.csdk.server.data.OnFrameReceive;
import com.csdk.server.http.OnUserIdLoadSyncFinish;
import com.csdk.server.http.OnUserLoadSyncFinish;
import com.csdk.server.util.AESUtil;
import com.csdk.server.util.Int;
import com.csdk.socket.ConnectionInfo;
import com.csdk.socket.OriginalData;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Create LuckMerlin
 * Date 14:06 2020/8/13
 * TODO
 *
 */
public class IMSocket extends Socket {
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Map<Integer, Waiting> mWaiterMap = new HashMap<>();
    private final ChatConfig mConfig;
    private WeakReference<Context> mContext;
    private final Map<String,Long> mSessionLastSendTime=new HashMap<>();
    /**
     * @deprecated
     */
    private final Map<OnFrameReceive, Long> mListeners = new WeakHashMap<>();
    private final Map<Listener, Matchable> mEventChanges = new WeakHashMap<>();//Must keep weak map to avoid some call with lifecycle component
    private LoginAuth mLoginedAuth;
    private final HttpCaller mCacher;
    private ReConnector mReconnector;
    private AuthenticationRequest mLastAuthRequest;
    private AuthenticationRequest mLogining;
    private boolean mServerConfigureLoaded=false;//Must init as false
    private boolean mIsForceExited=false;//Must init as false
    private final String mMessageProtocolVersion="3";
    private final long mProtocolVersion;

    public IMSocket(Context context, String cacheDir, ChatConfig config){
        mConfig=config;
        Configure configure=Configure.getInstance();
        mProtocolVersion=null!=configure?configure.getProtocolVersion():0;
        setContext(context);
        mCacher=new HttpCaller(cacheDir){
            @Override
            protected void notifyEvent(int event, Object arg) {
                IMSocket.this.notifyEvent(event, arg);
            }
        };
    }

    private final boolean setContext(Context context){
        context=null!=context?context instanceof Application?context:context.getApplicationContext():null;
        if (null!=context){
            WeakReference<Context> reference=mContext;
            if (null!=reference){
                mContext=null;
                reference.clear();
            }
            mContext=new WeakReference<>(context);
            return true;
        }
        return false;
    }

    /**
     * @deprecated
     */
    public final LoginAuth getLoginAuth() {
        return mLoginedAuth;
    }

    /**
     * @deprecated
     */
    public final String getLoginUserId() {
        LoginAuth loginAuth = mLoginedAuth;
        return null != loginAuth ? loginAuth.getUid() : null;
    }

    /**
     * @deprecated
     */
    public final String getLoginRoleId() {
        LoginAuth loginAuth = mLoginedAuth;
        return null != loginAuth ? loginAuth.getRoleId() : null;
    }

    public final Role getLoginRole(){
        LoginAuth loginAuth = mLoginedAuth;
        return null!=loginAuth?new Role(loginAuth.getUid(), loginAuth.getRoleId()):null;
    }

    public final boolean isLogin(String uid) {
        LoginAuth loginAuth = mLoginedAuth;
        String loginUid = null != loginAuth ? loginAuth.getUid() : null;
        return null != loginUid && (null == uid || uid.equals(loginUid));
    }

    /**
     * Check message if block to send
     */
    public final int checkMessageSendBlockedDuration(Session session, String debug){
        Map<String,Long> lastMap=mSessionLastSendTime;
        if (null!=session&&null!=lastMap){
            if (session instanceof Group){
                String groupType=((Group)session).getType();
                if (null!=groupType&&groupType.length()>0){
                    String uniqueId=generateGroupTargetUnique((Group)session);
                    if (null!=uniqueId&&uniqueId.length()>0){
                        Long lastTime=lastMap.get(uniqueId);
                        long current= System.currentTimeMillis();
                        int duration=0;
                        if (groupType.equals(GroupType.GROUP_TYPE_AREA)){
                            duration=3000;
                        }else if (groupType.equals(GroupType.GROUP_TYPE_WORLD)){
                            duration=20000;
                        }
                        return null!=lastTime?(int)((duration-(current-lastTime))):-1;
                    }
                }
            }
        }
        return -1;
    }

    public final boolean isChannelDisabled(String groupType){
        if (isProductIdAny(ProductId.PRODUCT_ID_DEMO)){//Demo test product id
            return false;
        }
        if (null!=groupType&&(groupType.equals(GroupType.GROUP_TYPE_AREA)||
                groupType.equals(GroupType.GROUP_TYPE_FRIEND_LIST)||groupType.equals(GroupType.GROUP_TYPE_WORLD))){
            return false;
        }
        return true;
    }

    public final boolean isProductIdAny(String... productIds){
        if (null!=productIds&&productIds.length>0){
            ChatConfig chatConfig=mConfig;
            String current=null!=chatConfig?chatConfig.getProductId():null;
            if (null!=current&&current.length()>0){
                for (String child:productIds) {
                    if (null!=child&&child.equals(current)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void onLoginAuthChanged(LoginAuth last, LoginAuth current) {
        //Do thing
    }

    public final boolean connect(final AuthenticationRequest authRequest, String debug) {
        if (null==authRequest){
            return false;
        }
        return connect(authRequest, ()-> {
             if(!isLogin(null)){//If not login
                launchReLogin("After not login while connect finish.");
             }
        }, debug);
    }

    private final boolean connect(final AuthenticationRequest authRequest, Runnable finishCallback,String debug) {
        if (null == authRequest) {
            Logger.W("Can't connect while auth request NONE." + debug);
            notifyConnectFinish(finishCallback);
            return false;
        }
        final String serverId=authRequest.getServerId();
        final String gender=authRequest.getGender();
        final String roleId=authRequest.getRoleId();
        mLastAuthRequest=authRequest;
        String socketIp = getSocketServerHost();
        Integer socketPort =getSocketServerPort();
        if (null == socketIp || socketIp.length() <= 0 || null==socketPort||socketPort<=0) {
            Logger.W("Can't connect while server IP or PORT invalid " + debug);
            notifyConnectFinish(finishCallback);
            return false;
        }
        Logger.M("Connect im server."," Connect im  " + socketIp+" "+socketPort+" gender="+gender);
        mLogining=authRequest;
        if (!mServerConfigureLoaded){//Check if loaded server configure
            return loadServerConfigure((boolean succeed, String note, ServerConfigure data)-> {
                AuthenticationRequest currentLogining=mLogining;
                if (null!=currentLogining&&null!=authRequest&&currentLogining==authRequest){
                    mLogining=null;
                    if (succeed){
                        Logger.D("Succeed load configure.");
                        mServerConfigureLoaded=true;
                        //Apply server configure into global configure
                        Configure configure=null!=data?Configure.getInstance():null;
                        if (null!=configure){
                            configure.putGroupConfig(data,"After server configure load finish.");
                        }
                        connect(authRequest, finishCallback, debug);
                        return;//Return to interrupt notify connect finish
                    }
                    Logger.D("Fail connect im server while load configure fail.");
                    notifyEvent(Event.EVENT_INITIAL_FAIL, null);
                }
                if (!succeed){
                    notifyConnectFinish(finishCallback);
                }
            },debug);
        }
        return connect(socketIp, socketPort,serverId,roleId,(boolean connected, int status, ConnectionInfo connectionInfo, final String s) ->{
            Logger.M("Finish connect im server."+connected+" "+ socketIp+" "+socketPort, "Finish connect im.connected="+connected);
            if (!connected||null==authRequest){
                Logger.W("FAIL login csdk server.connected="+connected);
                AuthenticationRequest currentLogining=mLogining;
                if (null!=currentLogining&&null!=authRequest&&currentLogining==authRequest){
                    mLogining=null;
                }
                notifyEvent(Event.EVENT_LOGIN_FAIL, roleId);
                notifyConnectFinish(finishCallback);
                return;
            }
            //Check if already login
            if (status==Status.STATUS_ALREADY_DONE){
                Logger.D("Upload user data "+(null!=debug?debug:"."));//Send auth request after connected
                updateUserInfo(authRequest,"While user already login.");
                return;
            }
            Logger.D("Now,Auth im sever.");//Send auth request after connected
            sendMessage(authRequest, null, (boolean succeed, String note, Frame replyFrame)-> {
                Reply reply = null != replyFrame ? replyFrame.getBodyReply() : null;
                Object replyObj = null != reply ? reply.getSucceedData() : null;
                Logger.M("Finish auth im sever."+replyObj,"Finish auth im sever."+(null!=reply?reply.getCode():-1));
                AuthenticationRequest currentLogining=mLogining;
                boolean isLastSession=null!=currentLogining&&null!=authRequest&&currentLogining==authRequest;
                if (isLastSession){
                    mLogining=null;
                }
                if (null == replyObj || !(replyObj instanceof Authentication)) {
                    Logger.M("FAIL auth with csdk server.data="+(null!=reply?reply.getCode(): Code.CODE_FAIL),
                            "FAIL auth with csdk server."+(null!=authRequest?authRequest.getRoleId():null) +" data="+reply);
                    if (isLastSession){
                        notifyEvent(Event.EVENT_AUTH_FAIL,null);
                    }
                    notifyConnectFinish(finishCallback);
                    return;
                }
                mIsForceExited=false;//Reset
                Authentication authentication=(Authentication)replyObj;
                setPulse(new ImHeartPlus(10005, "game",getProductId()), true, "After server auth success.");
                final LoginAuth last = mLoginedAuth;
                final LoginAuth current = mLoginedAuth = LoginAuth.fromRequestAndAuth(authentication, getConfig(),authRequest);
                Logger.M("Login changed.","Login changed."+current);
                ReConnector reconnector=mReconnector;
                if (null!=reconnector){//Remove reconnect runnable while login succeed.
                    mReconnector=null;
                    mHandler.removeCallbacks(reconnector);
                }
                HttpCaller cacher=mCacher;
                if (null!=current){
                    final String currentUid=current.getUid();
                    mHandler.post(()->{
                        mSessionLastSendTime.clear();
                        String lastLoginUid=null!=last?last.getUid():null;
                        if (null!=lastLoginUid&&lastLoginUid.length()>0){
                            if (null==currentUid||!currentUid.equals(lastLoginUid)){
                                cacher.removeGroupMessages(lastLoginUid,"After socket login changed.");
                            }
                        }
                    });
                    notifyEvent(Event.EVENT_LOGIN_SUCCEED,current);
                    final String userId=current.getUid();
                    if (null!=cacher){
                        loadFriends(null,"After socket login changed.");
                        loadBlockUsers(null,"After socket login changed.");
                        loadChannels(null,"After socket login changed.");
                        cacher.loadUserProfileByUid(current, getHttpServerUri(null), userId,
                                (boolean succeed1, String note1, User user)-> {
                                    if (null!=user){
                                        current.setNickName(user.getNickName());
                                    }
                                    onLoginAuthChanged(last, current);
                                    notifyEvent(Event.EVENT_LOGIN_CHANGED, current);
                                }, "After socket login changed.");
                    }
                }
                notifyConnectFinish(finishCallback);
            }, "After socket connect succeed.");
        }, 0, new FrameReader(), debug);
    }

    private boolean launchReLogin(String debug){
        ReConnector current=mReconnector;
        if (null!=current){
            mHandler.removeCallbacks(current);
        }
        if (mIsForceExited){//Force exit not need retry
            Logger.W("Not need launch retry login while force exited flag checked.");
            return false;
        }
        final ReConnector reconnector=mReconnector=new ReConnector(){
            @Override
            protected void onRetry(int retryCount) {
                if (mIsForceExited){//Force exit not need retry
                    ReConnector current=mReconnector;
                    if (null!=current&&current==this){
                        mReconnector=null;
                        mHandler.removeCallbacks(current);
                        Logger.W("Cancel retry while force exited flag checked.");
                    }
                    return;
                }
                AuthenticationRequest request=mLastAuthRequest;
                ReConnector reconnector=mReconnector;
                if (null!=request&&null!=reconnector&&reconnector==this){
                    if (!isLogin(null)||null==getLoginRequest()){
                        final int schedule=getRetrySchedule();
                        Logger.D("Retry reconnect socket."+retryCount+" "+schedule);
                        connect(request,() ->{
                            ReConnector currentReconnector=mReconnector;
                            if (null!=currentReconnector&&currentReconnector==this){
                                if (!isLogin(null)){//If not login
                                    if (schedule>=0){
                                        mHandler.postDelayed(reconnector,schedule);
                                    }
                                }
                            }
                        },"While reconnect retry." + retryCount);
                    }
                }
            }
        };
        int schedule=reconnector.getRetrySchedule();
        if (schedule>=0){//Just large than 0 need post reconnect
            Logger.D("Now,post to reconnect after socket shutdown "+(null!=debug?debug:".")+" schedule="+schedule);
            return mHandler.postDelayed(reconnector,schedule);
        }
        return false;
    }

    @Override
    public void onSocketConnectionSuccess(ConnectionInfo connectionInfo, String s) {
//        setPulse(new ImHeartPlus(10005, "game",getProductId()), true, "After socket connection success");
    }

    private boolean updateUserInfo(AuthenticationRequest authRequest,String debug){
        HttpCaller caller=mCacher;
        return null!=caller&&caller.updateUserInfo(mLoginedAuth,getHttpServerUri(null),authRequest,debug);
    }

    @Override
    protected void onSocketIOThreadShutdown(boolean self, String s, Exception e) {
        super.onSocketIOThreadShutdown(self, s, e);
        if(self){
            launchReLogin("After socket IO shutDown.");
//            String exceptionMessage=null!=e?e.getMessage():null;
//            if (null!=exceptionMessage&&exceptionMessage.contains("caused connection abort")){//Check if network disconnect cause
//                launchReLogin("After socket IO shutDown.");
//            }
        }
    }

    public final boolean deleteCachedMessage(Message message,String debug){
        HttpCaller caller=null!=message?mCacher:null;
        return null!=caller&&caller.deleteCachedMessage(message,debug);
    }

    private AuthenticationRequest getLoginRequest(){
        return mLogining;
    }

    private final boolean sendText(Integer operation, String text, String charSet, final OnMessageReply callback, String debug) {
        final Integer stamp = null != callback ? generateStamp() : null;
        byte[] sendBytes = generateSendBytes(stamp, operation, text, charSet);
        Logger.M("Send text.","Send text " + operation + " stamp=" + stamp + " " + text);
        Handler handler = mHandler;
        final Map<Integer, Waiting> waiterMap = null != stamp ? mWaiterMap : null;
        if (null != waiterMap && null != handler) {
            final Waiting runnable = new Waiting(callback) {
                @Override
                public void run() {
                    removeWaiter(stamp, "While request timeout");
                    notifyReply(false, "Timeout ", null, callback);
                }
            };
            waiterMap.put(stamp, runnable);
            handler.postDelayed(runnable, 10000);
        }
        if (null != sendBytes && sendBytes.length > 0 && sendBytes(sendBytes)) {
            return true;
        }
        Logger.W("Fail send text.");
        removeWaiter(stamp, "While send failed");
        notifyReply(false, "While send failed", null, callback);
        return false;
    }

    private OnMessageReply removeWaiter(Integer stamp, String debug) {
        Map<Integer, Waiting> waiterMap = null != stamp ? mWaiterMap : null;
        Waiting waiting = null != waiterMap ? waiterMap.remove(stamp) : null;
        OnMessageReply callback = null != waiting ? waiting.mCallback : null;
        Handler handler = null != waiting ? mHandler : null;
        if (null != handler) {
            handler.removeCallbacks(waiting);
        }
        return callback;
    }

    public final boolean setMessageSessionLastChatTime(Message msg,String debug){
        if (null!=msg){
            if (msg.getMsgType()== MessageType.MESSAGETYPE_GROUP) {//Save group last send time for send limit
                String key = msg.getSessionTargetUniqueId();
                if (null!=key&&key.length()>0){//Cache message target
                    Map<String,Long> sessionMap=mSessionLastSendTime;
                    if (null!=sessionMap){
                        Logger.W("Set message session last chat time "+(null!=debug?debug:"."));
                        sessionMap.put(key, System.currentTimeMillis());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public final boolean sendMessage(MessageObject message, String charSet, OnMessageReply callback, String debug) {
        if (null != message) {
            if (message instanceof Message){
                Message msg=(Message)message;
                msg.setVersion(mMessageProtocolVersion);
                String loginUid=getLoginUserId();
                msg.setLoginUid(loginUid);
                msg.setFromUid(loginUid);
                msg.setStatus(Status.STATUS_DOING);
                int retry=msg.getSendTry();
                retry=(retry<=0?1:retry+1);
                msg.setSendTry(retry);
                String fromUid=msg.getFromUid();
                if (null!=fromUid&&fromUid.length()>0){
                    applyMessageFromProfile(msg,getUserProfileByUid(fromUid,"While message send."),"While message send.");
                }
                cache(msg, debug);
                setMessageSessionLastChatTime(msg,debug);//Save group last send time for send limit
            }
        }
        notifyEvent(Event.EVENT_MESSAGE_SENDING, message);
        return sendText(null != message ? message.getOperation() : null, null != message ?
                message.getMessageText() : null, charSet, (boolean succeed, String note, Frame frame)-> {
            if (message instanceof Message){
                Message msg=(Message)message;
                Reply reply=succeed&&null!=frame?frame.getBodyReply():null;
//                msg.setStatus(null!=reply&&reply.isSucceed()?(!testFlag||testSucceed?Status.STATUS_SUCCEED:Status.STATUS_FAIL):Status.STATUS_FAIL);
                msg.setStatus(null!=reply&&reply.isSucceed()?Status.STATUS_SUCCEED:Status.STATUS_FAIL);
                HttpCaller caller=mCacher;
                if (null!=caller){
                    caller.cache(msg,"After message send reply." );
                }
            }
            notifyEvent(Event.EVENT_MESSAGE_SENT,message);
            if (null!=callback){
                callback.onMessageReplied(succeed, note, frame);
            }
        }, debug);
    }

    public final boolean sendText(Integer operation, JSONObject textJson, OnMessageReply callback, String debug) {
        String text = null != textJson ? textJson.toString() : null;
        return sendText(operation, null == text || text.length() <= 0 ? null : text, callback, debug);
    }

    private final boolean sendText(Integer operation, String text, OnMessageReply callback, String debug) {
        return sendText(operation, text, null, callback, debug);
    }

    private final byte[] generateSendBytes(Integer stamp, Integer operation, String text, String charSet) {
        try {
            String productKey=getProductKey();
            if (null==productKey||productKey.length()<=0){
                Logger.E("Product key is NULL.");
                return null;
            }
            String encryptText = AESUtil.encrypt(null != text ? text : "", productKey);
            final byte[] textBytes = null != charSet && charSet.length() > 0 ? encryptText.getBytes(charSet) : encryptText.getBytes();
            return generateSendBytes(stamp, operation, textBytes);
        } catch (UnsupportedEncodingException e) {
            Logger.E(""+e,e);
            e.printStackTrace();
        }
        return null;
    }

    private final byte[] generateSendBytes(Integer stamp, Integer operation, byte[] bytes) {
        final String productId=getProductId();
        if (null==productId||productId.length()<=0){
            Logger.E("Product id is NULL.");
            return null;
        }
        final int bytesLength = null != bytes && null != operation ? bytes.length : -1;
        if (bytesLength > 0) {
            final int packLength = bytesLength + 26;
            final byte[] headBytes = new byte[26];//4 + 2 + 2 + 4 + 4+10
            int offset = Int.encodeIntBigEndian(headBytes, packLength, 0, 4 * Int.BSIZE);  // package length
            offset = Int.encodeIntBigEndian(headBytes, 26, offset, 2 * Int.BSIZE);  // header length
            offset = Int.encodeIntBigEndian(headBytes, mProtocolVersion , offset, 2 * Int.BSIZE); // ver
            offset = Int.encodeIntBigEndian(headBytes, operation, offset, 4 * Int.BSIZE);   // operation   4 自定义消息 def 7
            offset = Int.encodeIntBigEndian(headBytes, null != stamp ? stamp : 1, offset, 4 * Int.BSIZE);        // json callback
            byte[] b = productId.getBytes();
            for (int i = 0; i < b.length; i++) {
                headBytes[16 + i] = b[i];
            }
            return Int.add(headBytes, bytes);
        }
        return null;
    }

    public final boolean isDispatchFriendRelation(int relation){
        ChatConfig config=mConfig;
        return null!=config?(config.getDispatchFriendRelation()&relation)>0:false;
    }

    public final String getProductId(){
        ChatConfig config=mConfig;
        return null!=config?config.getProductId():null;
    }

    public final String getProductKey(){
        ChatConfig config=mConfig;
        return null!=config?config.getProductKey():null;
    }

    protected void onMessageReceived(Message message){
        //Do nothing
    }

    protected void onFrameReceived(Frame frame) {
        //Do nothing
    }

    /**
     * @deprecated
     */
    public final boolean put(OnFrameReceive receive) {
        Map<OnFrameReceive, Long> listeners = null != receive ? mListeners : null;
        if (null != listeners) {
            synchronized (listeners) {
                listeners.put(receive, System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    public final ChatConfig getConfig() {
        return mConfig;
    }

    /**
     * @deprecated
     */
    public final boolean remove(OnFrameReceive receive) {
        Map<OnFrameReceive, Long> listeners = null != receive ? mListeners : null;
        if (null != listeners) {
            synchronized (listeners) {
                return null != listeners.remove(receive);
            }
        }
        return false;
    }

    protected final boolean notifyReply(boolean succeed, String note, Frame reply, OnMessageReply callback) {
        if (null != callback) {
            callback.onMessageReplied(succeed, note, reply);
            return true;
        }
        return false;
    }

    private Integer generateStamp() {
        int random = (int) (new Random().nextFloat() * 10);
        return (int) ((System.currentTimeMillis() - random / 1000));
    }

    public boolean notifyActionChange(CSDKAction action, String args, String debug){
        //Do nothing
        return false;
    }

    @Override
    protected final void onSocketReadResponse(ConnectionInfo connectionInfo, String s, OriginalData originalData) {
        if (null == originalData) {
            return;
        }
        final byte[] headBytes = originalData.getHeadBytes();
        final int opOffset = 8;
        int packetLen = (int) Int.decodeIntBigEndian(headBytes, 0, 4);// package length
        int headLength = (int) Int.decodeIntBigEndian(headBytes, 4, 2); // header length
        long version = Int.decodeIntBigEndian(headBytes, 6, 2); // ver
        int operation = (int) Int.decodeIntBigEndian(headBytes, 8, 4); // operation
        int stamp = (int) Int.decodeIntBigEndian(headBytes, 12, 4); // stamp
        if (operation == Operation.HEARTBEAT_REPLY) {
            Logger.D("Heartbeat " + operation + " " + packetLen + " " + headLength);
            feedPulse();
            return;
        }
        byte[] frameBody = originalData.getBodyBytes();
        String loginUserId = getLoginUserId();
        Logger.M("Received frame head."+loginUserId,"Received frame head uid:" + loginUserId + " op:" +
                operation + " stamp=" + stamp + " " + packetLen + " " + headLength+"\n"+new String(frameBody));
        if (operation == Operation.MESSAGE_RECEIVE||operation == Operation.SEND_RECEIPT||operation ==
                Operation.SYSTEM_MESSAGE||operation == Operation.SYSTEM_FORCE_EXIT) {//Fix protocol
            int headSrcLength=null!=headBytes?headBytes.length:0;
            int bodySrcLength=null!=frameBody?frameBody.length:0;
            int srcLength=headSrcLength+bodySrcLength;
            if (srcLength>0){
                byte[] buffer=new byte[srcLength];
                if (headSrcLength>0){
                    System.arraycopy(headBytes, 0, buffer,0,headSrcLength );
                }
                if (bodySrcLength>0){
                    System.arraycopy(frameBody, 0, buffer,headSrcLength,bodySrcLength);
                }
                for (int offset = 0; offset < srcLength; offset += packetLen) {
                    packetLen = (int) Int.decodeIntBigEndian(buffer, offset, 4);
                    headLength = (int) Int.decodeIntBigEndian(buffer, offset+4, 2); // header length
                    version = Int.decodeIntBigEndian(buffer, offset+6, 2); // ver
                    operation = (int)Int.decodeIntBigEndian(buffer, offset + 8, 4);
                    stamp = (int) Int.decodeIntBigEndian(buffer, offset+12, 4); // stamp
                    byte[] result = Int.tail(buffer, offset + headLength, packetLen - 26);
                    onFrameReceived(stamp, operation, new Frame(null, loginUserId, operation, packetLen, headLength, result));
                }
            }
            return;//Interrupt later codes
        }
        onFrameReceived(stamp, operation, new Frame(null, loginUserId, operation, packetLen, headLength, frameBody));
    }

    private void onFrameReceived(int stamp, int operation, Frame frame) {
        switch (operation) {
            case Operation.ADD_FRIEND_AGREE_REPLY:
                mHandler.postDelayed(() -> loadFriends(null,"After friend agree reply."), 1000);
                break;
            case Operation.BLOCK_FRIEND_REPLY:
            case Operation.UNBLOCK_FRIEND_REPLY:
                loadFriends(null,"After block modify reply.");
                loadBlockUsers(null,"After block modify reply.");
                break;
            case Operation.MESSAGE_RECEIVE:
                final Message message = frame.getBodyMessage();
                if (null != message) {
                    String loginUid=getLoginUserId();
                    String fromUid=message.getFromUid();
                    message.setLoginUid(loginUid);
                    onMessageReceived(message);
                    Logger.D("Received message  "+message.getContent());
                    if (isUserBlocked(message.getFromUid())){//Check if blocked user
                        Logger.D("Skip receive block user message.");
                        return;
                    }
                    if (null!=fromUid){//Dispatch others send message
                        boolean selfMessage=null!=loginUid&&fromUid.equals(loginUid);
                        if (!selfMessage){
                            message.setStatus(Status.STATUS_UNREAD);
                        }
                        fetchUserProfile(fromUid,(boolean succeed, String note, User user)-> {
                            if (null!=user){
                                applyMessageFromProfile(message,user,"While message received.");
                            }
                            if (cache(message,"While message received.")&&!selfMessage){
                                notifyEvent(Event.EVENT_FRIEND_CHAT_SESSION_UNREAD_MESSAGE_LIST_CHANGE, message.getFromUid());
                            }
                            notifyEvent(Event.EVENT_MESSAGE_RECEIVED, message);
                        },"While message received.");
                    }
                }
                break;
            case Operation.JOIN_GROUP_REPLY:
                loadChannels(null,"After join group reply.");
                break;
            case Operation.QUIT_TEAM_REPLY:
                loadChannels(null,"After quit team reply.");
                break;
            case Operation.QUIT_GROUP_REPLY:
                loadChannels(null,"After quit group");
                break;
            case Operation.CREATE_GROUP_REPLY:
                loadChannels(null,"After group create succeed reply.");
                break;
            case Operation.SEND_RECEIPT:
                final Reply receiptReply = frame.getBodyReply();
                if (null!=receiptReply){
                    notifyEvent(Event.EVENT_MESSAGE_SEND_RECEIPT, receiptReply);
                    //Temp
                    Object receiptObj=null!=receiptReply?receiptReply.getData():null;
                    Message receiptMessage=null!=receiptObj&&receiptObj instanceof Receipt?((Receipt)receiptObj).getMsg():null;
                    HttpCaller cacher=mCacher;
                    if (null!=receiptMessage&&null!=cacher){
                        Message message1=cacher.testSaveMessageReceoptMessage(getLoginUserId(), receiptMessage);
                        if (null!=message1){
                            notifyEvent(Event.EVENT_MESSAGE_RECEIVED, message1);
                        }
                    }
                    //Temp
                }
                break;
            case Operation.SYSTEM_MESSAGE://System message
                Message sysMessage= frame.getBodyMessage();
                if (null != sysMessage) {
                    int notifyType = sysMessage.getNotifyType();
                    if (notifyType == Notify.NOTIFY_ADD_FRIEND) {
                        String uid=sysMessage.getFromUid();
                        loadUserProfileByUid(uid, (boolean succeed, String note, User user) ->{
                            AddFriendRequest request=new AddFriendRequest(user, sysMessage);
                            cache(request,"While friend add system message received.");
                            notifyEvent(Event.EVENT_FRIEND_ADD_REQUEST_RECEIVE, request);
                            //Test
                            if (null!=request){
                                Json json=new Json().putSafe(Label.LABEL_USER_NAME,request.getRoleName()).
                                        putSafe(Label.LABEL_TO_UID, request.getLoginUid()).
                                        putSafe(Label.LABEL_ACTION_TOKEN, request.getActionToken()).
                                        putSafe(Label.LABEL_FROM_UID,request.getFromUid());
                                notifyActionChange(CSDKAction.ACTION_FRIEND_INVITATION, json.toString(), "");
                            }
                            //
                        },"While friend add system message received.");
                    } else if (notifyType == Notify.NOTIFY_GROUP_JOINED) {
                        loadChannels(null,"After join team");
                    } else if (notifyType == Notify.NOTIFY_GROUP_HAS_QUIT) {
                        loadChannels(null,"After group quit");
                    } else if (notifyType == Notify.NOTIFY_JOIN_GROUP_REPLY_ACCEPTED) {
                        loadChannels(null,"After accept join group request");
                    }else  if (notifyType == Notify.NOTIFY_ACCEPT_ADD_FRIEND) {
                        loadFriends(null,"After accept user add reply.");
                    }else if (notifyType == Notify.NOTIFY_FRIEND_ONLINE){//Friend online or offline
                        String uid=sysMessage.getFromUid();
                        String onlineFlag=sysMessage.getExtraString(Label.LABEL_ONLINE);
                        Logger.D("User online or offline "+onlineFlag+" "+uid);
                        if (null!=uid&&null!=onlineFlag){
                            loadUserProfileByUid(uid,(boolean succeed, String note, User user)-> { },
                                    "After user online or offline event.");
                        }
                    }else if (notifyType==Notify.NOTIFY_SHIENDSHIP_CHANGE){//Friendship changed
                          loadFriends(null, "After friendShip change.");
                          loadBlockUsers(null, "After friendShip change.");
//                        extra["friend"] = friendCount
//                        extra["blocked"] = blockedCount
//                        extra["third"] = thirdCount
                    }else if (notifyType==Notify.NOTIFY_GROUP_QUITED){//User quit group
                        loadChannels(null,"After group quited.");
                    }
                    onSystemMessageReceived(sysMessage);
                }
                break;
            case Operation.SYSTEM_FORCE_EXIT:
                mIsForceExited=true;
                Logger.D("Force exit system message received.");
                notifyEvent(Event.EVENT_FORCE_LOGIN_OUT, null);
                break;
        }
        notifyFrameReceived(stamp, frame);
    }

    protected void onSystemMessageReceived(Message message){
        //Do nothing
    }


    private void notifyFrameReceived(Integer stamp, final Frame frame) {
        if (null != frame) {
            onFrameReceived(frame);
            final Handler handler=mHandler;
            final Map<OnFrameReceive, Long> maps = mListeners;
            if (null!=handler&&null != maps) {
                handler.post(() -> {
                    synchronized (maps) {
                        Set<OnFrameReceive> set = maps.keySet();
                        if (null != set) {
                            for (OnFrameReceive child : set) {
                                child.onFrameReceived(frame);
                            }
                        }
                    }
                });
            }
            final OnMessageReply callback = null != stamp ? removeWaiter(stamp, "While reply received.") : null;
            if(null!=handler&&null!=callback){
                handler.post(()->callback.onMessageReplied(true, null, frame));
            }
        }
    }

    public final boolean cache(Cacheable cacheable, String debug){
        HttpCaller cacher=mCacher;
        return null!=cacheable&&null!=cacher&&cacher.cache(cacheable, debug);
    }

    public final boolean deleteCachedAddRequest(List<AddFriendRequest> list,String debug){
        HttpCaller cacher=mCacher;
        return null!=list&&list.size()>0&&null!=cacher&&cacher.deleteCachedAddRequest(getLoginUserId(),list, debug);
    }

    protected void onMenuListLoadSucceed(List<Menu<Group>> groups,String debug){
        //Do nothing
    }

    public final boolean loadChannels(OnChannelLoadFinish callback, String debug) {
        LoginAuth loginAuth = mLoginedAuth;
        final String uid = null != loginAuth ? loginAuth.getUid() : null;
        HttpCaller cacher = mCacher;
        return null != cacher && cacher.loadChannels(loginAuth, getHttpServerUri(null), (boolean succeed, int code, String note, List<Menu<Group>> channels) -> {
            final Handler handler=mHandler;
            if (succeed && code == Code.CODE_SUCCEED) { //Refresh GM audio room enable
                onMenuListLoadSucceed(channels, debug);//Dispatch sub class
                if (null!=handler){
                    handler.post(()->toggleGmAudioRoom(uid, GroupType.GROUP_TYPE_AREA,channels,"After channel load succeed."));
                }
            }
            if (null!=callback&&null!=handler){
                handler.post(()->callback.onChannelLoadFinish(succeed,code,note,channels));
            }
        }, debug);
    }


    private boolean toggleGmAudioRoom(String uid, String type,List<Menu<Group>> channels, String debug){
//        LoginAuth current=mLoginedAuth;
//        String currentUid=null!=current?current.getUid():null;
//        if (null==uid||(null!=currentUid&&!currentUid.equals(uid))){
//            return false;//Not current login user id
//        }
//        Integer audioUserId=Int.parseInteger(uid, null);
//        if (audioUserId==null){
//            Logger.W("Fail toggle gm audio room while Audio ID NONE."+uid);
//            return false;
//        }
//        if (null==type||type.length()<=0){
//            Logger.W("Fail toggle gm audio room while type ID NONE.");
//            return false;
//        }
//        Gm gm= Gm.instance();
//        if (null==gm){
//            Logger.W("Fail toggle gm audio room while GM NONE.");
//            return false;
//        }
//        if (null!=channels&&channels.size()>0){
//            for (Menu channel:channels) {
//                Group group = null !=channel ? channel.getGroup() : null;
//                String groupType = null!=group?group.getType():null;
//                if (null!=groupType&&groupType.equals(type)&&channel.isVisible()){
//                    Integer roomId=group.getIdInteger(null);
//                    if (null==roomId){
//                        Logger.W("Can't entry GM room which rood id invalid.");
//                        break;
//                    }
//                    return gm.entryRoom(getContext(),audioUserId , roomId, debug);
//                }
//            }
//        }
//        return gm.exitRoom(debug);
        return false;
    }

    public Menu<Group> getChannelFirst(Matchable matcher) {
        List<Menu<Group>> channels = getChannels(matcher, 1);
        return null != channels && channels.size() > 0 ? channels.get(0) : null;
    }

    public List<Menu<Group>> getChannels(Matchable matcher, int max) {
        HttpCaller cache=mCacher;
        return null!=cache?cache.getChannels(matcher,max):null;
    }

    public final List<Message> getFriendsMessage(String uid, int from, int size, boolean setRead,String debug) {
        String loginUid=getLoginUserId();
        HttpCaller cacher=mCacher;
        ArrayList<Message> messages=null!=cacher?cacher.getFriendCachedMessage(loginUid, uid, from,size,debug):null;
        if (setRead&&null!=messages&&messages.size()>0){
            setMessageRead(uid, "While get friends message with set read.");
        }
        return messages;
    }

    public final boolean setMessageRead(String uid,String debug){
        HttpCaller cacher=mCacher;
        if (null!=cacher){
            String loginUid=getLoginUserId();
            if (cacher.setMessagesAllRead(loginUid, new User(uid), debug)){
                notifyEvent(Event.EVENT_FRIEND_CHAT_SESSION_UNREAD_MESSAGE_LIST_CHANGE, uid);
                return true;
            }
            return false;
        }
        return false;
    }

    public final List<Message> getGroupMessage(Group group, int from,int size) {
        HttpCaller cacher=mCacher;
        return null!=cacher?cacher.getGroupCachedMessage(getLoginUserId(),group,from,size,null):null;
    }

    public Collection<?> getEventListData(int event,Object arg,int from,int size,String debug){
        switch (event){
            case Event.EVENT_FRIEND_ADD_REQUEST_LIST_CHANGE:
                return getAddFriendCachedRequest(from,size,debug);
            case Event.EVENT_FRIEND_CHAT_SESSION_UNREAD_MESSAGE_LIST_CHANGE:
                return getFriendUnreadMessages(getLoginUserId(),null!=arg&&arg instanceof String?(String) arg:null,from,size);
        }
        return null;
    }

    private boolean applyMessageFromProfile(Message message,User user,String debug){
        if (null!=message){
            message.setUserLevel(null!=user?user.getUserLevel():null);
            message.setUserFemale(null!=user&&user.isFemale());
            message.setUserAvatarUrl(null!=user?user.getAvatarUrl():null);
            message.setFromRoleId(null!=user?user.getRoleId():null);
            message.setFromUserRoleName(null!=user?user.getRoleName():null);
            return true;
        }
        return false;
    }

    public final boolean isUserBlocked(String ...uids){
        List<User> list=getBlockUsers((Object arg)->null!=arg&&arg instanceof User&&((User)arg).
                isUidMatch(uids)?Matchable.MATCHED:Matchable.CONTINUE, 1);
        return null!=list&&list.size()>0;
    }

    public final ArrayList<String> getRecentContactUsers(String debug){
        HttpCaller cacher=mCacher;
        return null!=cacher?cacher.getFriendRecentContact(getLoginUserId()):null;
    }

    public final List<User> getBlockUsers(Matchable matchable,int max){
        HttpCaller cacher=mCacher;
        return null!=cacher?cacher.getBlockUsers(matchable,max):null;
    }

    public boolean deleteFriendCachedMessage(String uid,String debug) {
        HttpCaller cacher=mCacher;
        return null!=cacher&&null!=uid&&cacher.deleteFriendMessage(getLoginUserId(),uid, debug);
    }

    public final Collection<?> getFriendUnreadMessages(String loginUid,String uid, int from, int size) {
        HttpCaller cacher=mCacher;
        return null!=cacher&&isLogin(null)?cacher.getUnreadMessages(loginUid,null!=uid&&uid.length()>0?new User(uid):null,from,size):null;
    }

    public final ArrayList<AddFriendRequest> getAddFriendCachedRequest(int from, int size,String debug){
        HttpCaller cacher=mCacher;
        return null!=cacher&&isLogin(null)?cacher.getAddFriendCachedRequest(getLoginUserId(), from,size,debug):null;
    }

    public boolean add(Listener change, String debug) {
        return add(change, null, debug);
    }

    public boolean add(Listener change,Matchable matchable, String debug) {
        Map<Listener, Matchable> changeMap = null != change ? mEventChanges : null;
        if (null != changeMap) {
            synchronized (changeMap) {
                changeMap.put(change, matchable);
            }
            return true;
        }
        return false;
    }

    public boolean remove(Listener change, String debug) {
        Map<Listener, Matchable> changeMap = null != change ? mEventChanges : null;
        if (null!=changeMap){
            synchronized (changeMap){
                return null!=changeMap.remove(change);
            }
        }
        return false;
    }

    public final boolean loadUserById(OnUserLoadSyncFinish callback, String debug, String ...uids) {
        HttpCaller cacher = mCacher;
        return null!=cacher&&cacher.loadUserById(mLoginedAuth, getHttpServerUri(null),callback, debug, uids);
    }

    public final boolean loadServerConfigure(OnServerConfigureLoadFinish callback, String debug){
        HttpCaller cacher = mCacher;
        return null!=cacher&&cacher.loadServerConfigure(mConfig, getHttpServerUri(null),callback, debug);
    }

    public final boolean loadBlockUsers(OnUserLoadSyncFinish callback,String debug){
        HttpCaller cacher = mCacher;
        return null!=cacher&&cacher.loadBlockUsers(mLoginedAuth, getHttpServerUri(null),callback, debug);
    }

    public User getUserProfileByUid(String uid,String debug){
        HttpCaller cacher = mCacher;
       return null!=cacher?cacher.getUserProfileByUid(uid,debug):null;
    }

    public User getUserProfile(Matchable matchable,String debug){
        HttpCaller cacher = mCacher;
        return null!=cacher?cacher.getUserProfile(matchable,debug):null;
    }

    public boolean fetchUserProfile(String uid,OnUserProfileLoadFinish callback,String debug){
        if (null!=callback){
            HttpCaller cacher = mCacher;
            if (null==cacher){
                callback.onUserProfileLoadFinish(false, "Cache is NONE.", null);
                return false;
            }
            return cacher.fetchUserProfile(mLoginedAuth, getHttpServerUri(null),uid,callback, debug);
        }
        return false;
    }

    public final boolean loadUserProfileByUid(String uid, OnUserProfileLoadFinish callback, String debug) {
        HttpCaller cacher = mCacher;
        return null!=cacher&&cacher.loadUserProfileByUid(mLoginedAuth, getHttpServerUri(null),uid,callback, debug);
    }

    public final boolean loadUserById(OnUserLoadSyncFinish callback, String debug, Collection<String> uids) {
        HttpCaller cacher = mCacher;
        return null!=cacher&&cacher.loadUserById(mLoginedAuth,getHttpServerUri(null), callback, debug, uids);
    }

    public boolean fetchUserProfileByUids(List<String> uids, OnUserLoadSyncFinish callback,String debug){
        HttpCaller cacher = mCacher;
        return null!=cacher&&cacher.fetchUserProfileByUids(mLoginedAuth,getHttpServerUri(null), uids,callback, debug);
    }

    public final boolean loadUidByRoleIds(String roleId,OnUserIdLoadSyncFinish callback, String debug){
        HttpCaller cacher = mCacher;
        return null!=cacher&&cacher.loadUidByRoleId(mLoginedAuth, getHttpServerUri(null),roleId,callback, debug);
    }

    public final boolean fetchUserIdByRoleId(OnUserIdLoadSyncFinish callback,String roleId, String debug) {
        HttpCaller cacher = mCacher;
        return null!=cacher&&null!=roleId&&roleId.length()>0&&cacher.fetchUserIdByRoleId(mLoginedAuth,getHttpServerUri(null),callback,roleId,debug);
    }

    public final boolean fetchUserById(OnUserLoadSyncFinish callback, String debug, Collection<String> uids) {
        HttpCaller cacher = mCacher;
        return null!=cacher&&cacher.loadUserById(mLoginedAuth,getHttpServerUri(null), callback, debug, uids);
    }

    public final boolean loadFriends(OnUserLoadSyncFinish callback, String debug) {
        HttpCaller cache = mCacher;
        return null != cache && cache.loadFriendList(mLoginedAuth,getHttpServerUri(null),callback, debug);
    }

    public final List<User> getCachedFriends(Matchable matcher, int max, String debug) {
        HttpCaller cacher = mCacher;
        LoginAuth auth=mLoginedAuth;
        return null != cacher&&null!=auth ? cacher.getCachedFriends(auth.getUid(), (Object arg)->
                null!=arg&&arg instanceof User&&((User)arg).isFriend()?(null==matcher? Matchable.
                        MATCHED:matcher.onMatch(arg)): Matchable.CONTINUE,max,debug) : null;
    }

    private final void notifyEvent(final int event, final Object arg) {
        final Handler handler=mHandler;
        final Map<Listener, Matchable> changeMap = mEventChanges;
        if (null!=changeMap){
            synchronized (changeMap) {
                Set<Listener> set = changeMap.keySet();
                if (null != set && set.size() > 0) {
                    Integer match=null;
                    for (Listener child : set) {
                        if (null != child&&child instanceof OnEventChange) {
                            OnEventChange change=(OnEventChange)child;
                            match=child instanceof Matchable?((Matchable)child).onMatch(event):null;
                            if (null!=match&&match==Matchable.MATCHED){
                                handler.post(()-> change.onEventChanged(event, arg));
                                continue;
                            }
                            Matchable matchable=changeMap.get(child);
                            match=null!=matchable?matchable.onMatch(event):Matchable.MATCHED;
                            if (null!=match&&match==Matchable.MATCHED){
                                handler.post(()-> change.onEventChanged(event, arg));
                            }
                        }
                    }
                }
            }
        }
        handler.post(()->onEventSend(event,arg));
    }

    protected void onEventSend(int event,Object arg){
        //Do nothing
    }

    public final String getSocketServerHost(){
        ChatConfig config=mConfig;
        String serverHost=null!=config?config.getServerHost():null;
        return null!=serverHost&&serverHost.length()>0?serverHost:"imdev.yingxiong.com";
    }

    public final Integer getSocketServerPort(){
        ChatConfig config=mConfig;
        Integer port= null!=config?config.getServerPort():null;
        return null!=port&&port>0?port:81;
    }

    private final String generateGroupTargetUnique(Group group){
        String groupType=null!=group?group.getType():null;
        String groupId=null!=group?group.getId():null;
        return null!=groupType&&null!=groupId&&groupType.length()>0&&
                groupId.length()>0?"|||Group||Unique|||"+groupType+groupId:null;
    }

    private String getText(int textId,Object ...args){
        Context context=getContext();
        return null!=context?context.getString(textId,args):null;
    }

    public final String getHttpServerHost(){
        ChatConfig config=mConfig;
        String httpHost= null!=config?config.getHttpHost():null;
        return null!=httpHost&&httpHost.length()>0?httpHost:null;
    }

    public final Integer getHttpServerPort(){
        ChatConfig config=mConfig;
        int httpPort= null!=config?config.getHttpPort():null;
        return httpPort>0?httpPort:3111;
    }

    public final String getHttpServerUri(String def){
        String serverIp=getHttpServerHost();
        Integer httpPort=getHttpServerPort();
        if (null!=serverIp&&serverIp.length()>0){
            return serverIp+(null!=httpPort&&httpPort>0?":"+httpPort:"");
        }
        return def;
    }

    @Override
    public final boolean disconnect(String debug) {
        if (super.disconnect(debug)){
            mLastAuthRequest=null;
            mReconnector=null;
            return true;
        }
        return false;
    }

    private void notifyConnectFinish(Runnable finishCallback){
        if (null!=finishCallback){
            finishCallback.run();
        }
    }

    public final Context getContext(){
        WeakReference<Context> reference=mContext;
        return null!=reference?reference.get():null;
    }

    protected final boolean post(Runnable runnable){
        Handler handler=mHandler;
        if (null!=handler&&null!=runnable){
            handler.post(runnable);
            return true;
        }
        return false;
    }

    public final boolean notifySendFinish(boolean succeed, String note, Object reply, OnSendFinish callback){
        if (null!=callback){
            callback.onSendFinish(succeed,note,reply);
            return true;
        }
        return false;
    }

    private static abstract class Waiting implements Runnable {
        private final OnMessageReply mCallback;

        public Waiting(OnMessageReply callback) {
            mCallback = callback;
        }
    }
}
