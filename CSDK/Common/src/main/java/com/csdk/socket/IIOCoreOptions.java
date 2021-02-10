package com.csdk.socket;
import java.nio.ByteOrder;

 interface IIOCoreOptions {

    ByteOrder getReadByteOrder();

    int getMaxReadDataMB();

    IReaderProtocol getReaderProtocol();

    ByteOrder getWriteByteOrder();

    int getReadPackageBytes();

    int getWritePackageBytes();

    boolean isDebug();

}
