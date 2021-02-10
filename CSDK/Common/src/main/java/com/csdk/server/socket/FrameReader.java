package com.csdk.server.socket;

import com.csdk.server.util.Int;
import com.csdk.socket.IReaderProtocol;

import java.nio.ByteOrder;

/**
 * Created by Administrator on 2020/7/29.
 */

public final class FrameReader implements IReaderProtocol {

    @Override
    public int getHeaderLength() {
        return 26;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        long packetLen = Int.decodeIntBigEndian(header, 0, 4);
        long headLength = Int.decodeIntBigEndian(header, 4, 2);
        return (int)(packetLen-headLength);
    }
}
