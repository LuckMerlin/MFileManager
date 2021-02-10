package com.csdk.socket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Create LuckMerlin
 * Date 10:47 2020/12/22
 * TODO
 */
class DefaultNormalReaderProtocol implements IReaderProtocol {

    @Override
    public int getHeaderLength() {
        return 4;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        if (header == null || header.length < getHeaderLength()) {
            return 0;
        }
        ByteBuffer bb = ByteBuffer.wrap(header);
        bb.order(byteOrder);
        return bb.getInt();
    }
}
