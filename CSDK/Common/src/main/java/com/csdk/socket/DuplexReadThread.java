package com.csdk.socket;

import java.io.IOException;

/**
 * Create LuckMerlin
 * Date 10:29 2020/12/22
 * TODO
 */
class DuplexReadThread extends AbsLoopThread {
    private IStateSender mStateSender;
    private IReader mReader;

    public DuplexReadThread(IReader reader, IStateSender stateSender) {
        super("client_duplex_read_thread");
        this.mStateSender = stateSender;
        this.mReader = reader;
    }

    protected void beforeLoop() {
        this.mStateSender.sendBroadcast("action_read_thread_start");
    }

    protected void runInLoopThread() throws IOException {
        this.mReader.read();
    }

    public synchronized void shutdown(Exception e) {
        this.mReader.close();
        super.shutdown(e);
    }

    protected void loopFinish(Exception e) {
        e = e instanceof ManuallyDisconnectException ? null : e;
        if (e != null) {
            SLog.e("duplex read error,thread is dead with exception:" + e.getMessage());
        }

        this.mStateSender.sendBroadcast("action_read_thread_shutdown", e);
    }
}
