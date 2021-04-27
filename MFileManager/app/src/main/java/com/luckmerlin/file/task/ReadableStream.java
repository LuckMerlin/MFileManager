package com.luckmerlin.file.task;

import java.io.Closeable;
import java.io.IOException;

public interface ReadableStream extends Closeable {

    abstract int read(byte[] b) throws IOException;
}
