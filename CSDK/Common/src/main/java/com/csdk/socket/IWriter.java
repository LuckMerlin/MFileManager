package com.csdk.socket;


import java.io.OutputStream;

/**
 * Created by xuhao on 2017/5/16.
 */

 interface IWriter<T extends IIOCoreOptions> {

    void initialize(OutputStream outputStream, IStateSender stateSender);

    boolean write() throws RuntimeException;

    void setOption(T option);

    void offer(ISendable sendable);

    void close();

}
