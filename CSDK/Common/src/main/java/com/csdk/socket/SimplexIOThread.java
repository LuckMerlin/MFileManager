package com.csdk.socket;

import java.io.IOException;

/**
 * Create LuckMerlin
 * Date 10:30 2020/12/22
 * TODO
 */
class SimplexIOThread extends AbsLoopThread {
    private IStateSender mStateSender;
    private IReader mReader;
    private IWriter mWriter;
    private boolean isWrite = false;

    public SimplexIOThread(IReader reader, IWriter writer, IStateSender stateSender) {
        super("client_simplex_io_thread");
        this.mStateSender = stateSender;
        this.mReader = reader;
        this.mWriter = writer;
    }

    protected void beforeLoop() throws IOException {
        this.mStateSender.sendBroadcast("action_write_thread_start");
        this.mStateSender.sendBroadcast("action_read_thread_start");
    }

    protected void runInLoopThread() throws IOException {
        this.isWrite = this.mWriter.write();
        if (this.isWrite) {
            this.isWrite = false;
            this.mReader.read();
        }

    }

    public synchronized void shutdown(Exception e) {
        this.mReader.close();
        this.mWriter.close();
        super.shutdown(e);
    }

    protected void loopFinish(Exception e) {
        e = e instanceof ManuallyDisconnectException ? null : e;
        if (e != null) {
            SLog.e("simplex error,thread is dead with exception:" + e.getMessage());
        }

        this.mStateSender.sendBroadcast("action_write_thread_shutdown", e);
        this.mStateSender.sendBroadcast("action_read_thread_shutdown", e);
    }
}
