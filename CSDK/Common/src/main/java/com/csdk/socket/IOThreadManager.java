package com.csdk.socket;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Create LuckMerlin
 * Date 10:30 2020/12/22
 * TODO
 */
class IOThreadManager implements IIOManager<OkSocketOptions> {
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private volatile OkSocketOptions mOkOptions;
    private IStateSender mSender;
    private IReader mReader;
    private IWriter mWriter;
    private AbsLoopThread mSimplexThread;
    private DuplexReadThread mDuplexReadThread;
    private DuplexWriteThread mDuplexWriteThread;
    private OkSocketOptions.IOThreadMode mCurrentThreadMode;

    public IOThreadManager(InputStream inputStream, OutputStream outputStream, OkSocketOptions okOptions, IStateSender stateSender) {
        this.mInputStream = inputStream;
        this.mOutputStream = outputStream;
        this.mOkOptions = okOptions;
        this.mSender = stateSender;
        this.initIO();
    }

    private void initIO() {
        this.assertHeaderProtocolNotEmpty();
        this.mReader = new ReaderImpl();
        this.mReader.initialize(this.mInputStream, this.mSender);
        this.mWriter = new WriterImpl();
        this.mWriter.initialize(this.mOutputStream, this.mSender);
    }

    public void startEngine() {
        this.mCurrentThreadMode = this.mOkOptions.getIOThreadMode();
        this.mReader.setOption(this.mOkOptions);
        this.mWriter.setOption(this.mOkOptions);
        switch(this.mOkOptions.getIOThreadMode()) {
            case DUPLEX:
                SLog.w("DUPLEX is processing");
                this.duplex();
                break;
            case SIMPLEX:
                SLog.w("SIMPLEX is processing");
                this.simplex();
                break;
            default:
                throw new RuntimeException("未定义的线程模式");
        }

    }

    private void duplex() {
        this.shutdownAllThread((Exception)null);
        this.mDuplexWriteThread = new DuplexWriteThread(this.mWriter, this.mSender);
        this.mDuplexReadThread = new DuplexReadThread(this.mReader, this.mSender);
        this.mDuplexWriteThread.start();
        this.mDuplexReadThread.start();
    }

    private void simplex() {
        this.shutdownAllThread((Exception)null);
        this.mSimplexThread = new SimplexIOThread(this.mReader, this.mWriter, this.mSender);
        this.mSimplexThread.start();
    }

    private void shutdownAllThread(Exception e) {
        if (this.mSimplexThread != null) {
            this.mSimplexThread.shutdown(e);
            this.mSimplexThread = null;
        }

        if (this.mDuplexReadThread != null) {
            this.mDuplexReadThread.shutdown(e);
            this.mDuplexReadThread = null;
        }

        if (this.mDuplexWriteThread != null) {
            this.mDuplexWriteThread.shutdown(e);
            this.mDuplexWriteThread = null;
        }

    }

    public void setOkOptions(OkSocketOptions options) {
        this.mOkOptions = options;
        if (this.mCurrentThreadMode == null) {
            this.mCurrentThreadMode = this.mOkOptions.getIOThreadMode();
        }

        this.assertTheThreadModeNotChanged();
        this.assertHeaderProtocolNotEmpty();
        this.mWriter.setOption(this.mOkOptions);
        this.mReader.setOption(this.mOkOptions);
    }

    public void send(ISendable sendable) {
        this.mWriter.offer(sendable);
    }

    public void close() {
        this.close(new ManuallyDisconnectException());
    }

    public void close(Exception e) {
        this.shutdownAllThread(e);
        this.mCurrentThreadMode = null;
    }

    private void assertHeaderProtocolNotEmpty() {
        IReaderProtocol protocol = this.mOkOptions.getReaderProtocol();
        if (protocol == null) {
            throw new IllegalArgumentException("The reader protocol can not be Null.");
        } else if (protocol.getHeaderLength() == 0) {
            throw new IllegalArgumentException("The header length can not be zero.");
        }
    }

    private void assertTheThreadModeNotChanged() {
        if (this.mOkOptions.getIOThreadMode() != this.mCurrentThreadMode) {
            throw new IllegalArgumentException("can't hot change iothread mode from " + this.mCurrentThreadMode + " to " + this.mOkOptions.getIOThreadMode() + " in blocking io manager");
        }
    }
}
