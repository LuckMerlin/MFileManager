package com.csdk.server.socket;

import com.csdk.api.core.Operation;
import com.csdk.debug.Logger;
import com.csdk.server.Configure;
import com.csdk.server.util.Int;
import com.csdk.socket.IPulseSendable;

/**
 * Created by Administrator on 2020/7/29.
 */

public final class ImHeartPlus implements IPulseSendable {
    private final int mUid;
    private final String mGame;
    private final String mProductId;
    private byte[] mHeartPlusBytes;
    private final long mProtocolVersion;

    public ImHeartPlus(int uid, String game, String productId){
        mProductId=productId;
        mUid=uid;
        mGame=game;
        Configure configure=Configure.getInstance();
        mProtocolVersion=null!=configure?configure.getProtocolVersion():0;
        mHeartPlusBytes=buildBytes();
    }

    private byte[] buildBytes(){
        String productId=mProductId;
        if (null==productId||productId.length()<=0){
            Logger.E("Can't build hearBeat while product ID is NULL.");
            return null;
        }
        String msg = Integer.toString(mUid) + "," + getGameCode(mGame);
//        int packLength = msg.length() + 16;
//        byte[] message = new byte[4 + 2 + 2 + 4 + 4];
//        // package length
//        int offset = Int.encodeIntBigEndian(message, packLength, 0, 4 * Int.BSIZE);
//        // header lenght
//        offset = Int.encodeIntBigEndian(message, 16, offset, 2 * Int.BSIZE);
//        // ver
//        offset = Int.encodeIntBigEndian(message, 1, offset, 2 * Int.BSIZE);
//        // operation
//        offset = Int.encodeIntBigEndian(message, Operation.HEARTBEAT, offset, 4 * Int.BSIZE);
//        // jsonp callback
//        offset = Int.encodeIntBigEndian(message, 1, offset, 4 * Int.BSIZE);
        final int packLength = msg.length() + 26;
        final byte[] headBytes = new byte[26];//4 + 2 + 2 + 4 + 4+10
        int offset = Int.encodeIntBigEndian(headBytes, packLength, 0, 4 *Int.BSIZE);  // package length
        offset = Int.encodeIntBigEndian(headBytes, 26, offset, 2 * Int.BSIZE);  // header length
        offset = Int.encodeIntBigEndian(headBytes, mProtocolVersion, offset, 2 * Int.BSIZE); // ver
        offset = Int.encodeIntBigEndian(headBytes,  Operation.HEARTBEAT, offset, 4 * Int.BSIZE);   // operation   4 自定义消息 def 7
        offset = Int.encodeIntBigEndian(headBytes, 1, offset, 4 * Int.BSIZE);        // json callback
        byte[] b = productId.getBytes();
        for (int i = 0; i < b.length; i++) {
            headBytes[16 + i] = b[i];
        }
        return Int.add(headBytes, msg.getBytes());
    }

    @Override
    public byte[] parse() {
        return mHeartPlusBytes;
    }

    private int getGameCode(String game) {
        int sum = 0;
        byte[] array = (null!=game?game:"").getBytes();
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }
}
