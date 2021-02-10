package com.csdk.socket;

import java.io.IOException;

/**
 * Create LuckMerlin
 * Date 10:29 2020/12/22
 * TODO
 */
class DuplexWriteThread  extends AbsLoopThread {
    private IStateSender mStateSender;
    private IWriter mWriter;

    public DuplexWriteThread(IWriter writer, IStateSender stateSender) {
        super("client_duplex_write_thread");
        this.mStateSender = stateSender;
        this.mWriter = writer;
    }

    protected void beforeLoop() {
        this.mStateSender.sendBroadcast("action_write_thread_start");
    }

    protected void runInLoopThread() throws IOException {
        this.mWriter.write();
    }

    public synchronized void shutdown(Exception e) {
        this.mWriter.close();
        super.shutdown(e);
    }

    protected void loopFinish(Exception e) {
        e = e instanceof ManuallyDisconnectException ? null : e;
        if (e != null) {
            SLog.e("duplex write error,thread is dead with exception:" + e.getMessage());
        }

        this.mStateSender.sendBroadcast("action_write_thread_shutdown", e);
    }
}

