package com.csdk.socket;

import java.io.InputStream;

/**
 * Created by xuhao on 2017/5/16.
 */

 interface IReader<T extends IIOCoreOptions> {

    void initialize(InputStream inputStream, IStateSender stateSender);

    void read() throws RuntimeException;

    void setOption(T option);

    void close();
}
