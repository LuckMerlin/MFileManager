package com.csdk.socket;

import java.nio.ByteOrder;

/**
 * Create LuckMerlin
 * Date 10:36 2020/12/22
 * TODO
 */
public class OkSocketOptions  implements IIOCoreOptions {
    private static boolean isDebug;
    private IOThreadMode mIOThreadMode;
    private boolean isConnectionHolden;
    private ByteOrder mWriteOrder;
    private ByteOrder mReadByteOrder;
    private IReaderProtocol mReaderProtocol;
    private int mWritePackageBytes;
    private int mReadPackageBytes;
    private long mPulseFrequency;
    private int mPulseFeedLoseTimes;
    private int mConnectTimeoutSecond;
    private int mMaxReadDataMB;
    private AbsReconnectionManager mReconnectionManager;
    private OkSocketSSLConfig mSSLConfig;
    private OkSocketFactory mOkSocketFactory;
    private boolean isCallbackInIndependentThread;
    private ThreadModeToken mCallbackThreadModeToken;

    private OkSocketOptions() {
    }

    public static void setIsDebug(boolean isDebug) {
        OkSocketOptions.isDebug = isDebug;
    }

    public IOThreadMode getIOThreadMode() {
        return this.mIOThreadMode;
    }

    public long getPulseFrequency() {
        return this.mPulseFrequency;
    }

    public OkSocketSSLConfig getSSLConfig() {
        return this.mSSLConfig;
    }

    public OkSocketFactory getOkSocketFactory() {
        return this.mOkSocketFactory;
    }

    public int getConnectTimeoutSecond() {
        return this.mConnectTimeoutSecond;
    }

    public boolean isConnectionHolden() {
        return this.isConnectionHolden;
    }

    public int getPulseFeedLoseTimes() {
        return this.mPulseFeedLoseTimes;
    }

    public AbsReconnectionManager getReconnectionManager() {
        return this.mReconnectionManager;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public int getWritePackageBytes() {
        return this.mWritePackageBytes;
    }

    public int getReadPackageBytes() {
        return this.mReadPackageBytes;
    }

    public ByteOrder getWriteByteOrder() {
        return this.mWriteOrder;
    }

    public IReaderProtocol getReaderProtocol() {
        return this.mReaderProtocol;
    }

    public int getMaxReadDataMB() {
        return this.mMaxReadDataMB;
    }

    public ByteOrder getReadByteOrder() {
        return this.mReadByteOrder;
    }

    public ThreadModeToken getCallbackThreadModeToken() {
        return this.mCallbackThreadModeToken;
    }

    public boolean isCallbackInIndependentThread() {
        return this.isCallbackInIndependentThread;
    }

    public static OkSocketOptions getDefault() {
        OkSocketOptions okOptions = new OkSocketOptions();
        okOptions.mPulseFrequency = 5000L;
        okOptions.mIOThreadMode = IOThreadMode.DUPLEX;
        okOptions.mReaderProtocol = new DefaultNormalReaderProtocol();
        okOptions.mMaxReadDataMB = 5;
        okOptions.mConnectTimeoutSecond = 3;
        okOptions.mWritePackageBytes = 100;
        okOptions.mReadPackageBytes = 50;
        okOptions.mReadByteOrder = ByteOrder.BIG_ENDIAN;
        okOptions.mWriteOrder = ByteOrder.BIG_ENDIAN;
        okOptions.isConnectionHolden = true;
        okOptions.mPulseFeedLoseTimes = 5;
        okOptions.mReconnectionManager = new DefaultReconnectManager();
        okOptions.mSSLConfig = null;
        okOptions.mOkSocketFactory = null;
        okOptions.isCallbackInIndependentThread = true;
        okOptions.mCallbackThreadModeToken = null;
        return okOptions;
    }

    public static enum IOThreadMode {
        SIMPLEX,
        DUPLEX;

        private IOThreadMode() {
        }
    }

    public static class Builder {
        private OkSocketOptions mOptions;

        public Builder() {
            this(OkSocketOptions.getDefault());
        }

        public Builder(IConfiguration configuration) {
            this(configuration.getOption());
        }

        public Builder(OkSocketOptions okOptions) {
            this.mOptions = okOptions;
        }

        public Builder setIOThreadMode(IOThreadMode IOThreadMode) {
            this.mOptions.mIOThreadMode = IOThreadMode;
            return this;
        }

        public Builder setMaxReadDataMB(int maxReadDataMB) {
            this.mOptions.mMaxReadDataMB = maxReadDataMB;
            return this;
        }

        public Builder setSSLConfig(OkSocketSSLConfig SSLConfig) {
            this.mOptions.mSSLConfig = SSLConfig;
            return this;
        }

        public Builder setReaderProtocol(IReaderProtocol readerProtocol) {
            this.mOptions.mReaderProtocol = readerProtocol;
            return this;
        }

        public Builder setPulseFrequency(long pulseFrequency) {
            this.mOptions.mPulseFrequency = pulseFrequency;
            return this;
        }

        public Builder setConnectionHolden(boolean connectionHolden) {
            this.mOptions.isConnectionHolden = connectionHolden;
            return this;
        }

        public Builder setPulseFeedLoseTimes(int pulseFeedLoseTimes) {
            this.mOptions.mPulseFeedLoseTimes = pulseFeedLoseTimes;
            return this;
        }

        /** @deprecated */
        public Builder setWriteOrder(ByteOrder writeOrder) {
            this.setWriteByteOrder(writeOrder);
            return this;
        }

        public Builder setWriteByteOrder(ByteOrder writeOrder) {
            this.mOptions.mWriteOrder = writeOrder;
            return this;
        }

        public Builder setReadByteOrder(ByteOrder readByteOrder) {
            this.mOptions.mReadByteOrder = readByteOrder;
            return this;
        }

        public Builder setWritePackageBytes(int writePackageBytes) {
            this.mOptions.mWritePackageBytes = writePackageBytes;
            return this;
        }

        public Builder setReadPackageBytes(int readPackageBytes) {
            this.mOptions.mReadPackageBytes = readPackageBytes;
            return this;
        }

        public Builder setConnectTimeoutSecond(int connectTimeoutSecond) {
            this.mOptions.mConnectTimeoutSecond = connectTimeoutSecond;
            return this;
        }

        public Builder setReconnectionManager(AbsReconnectionManager reconnectionManager) {
            this.mOptions.mReconnectionManager = reconnectionManager;
            return this;
        }

        public Builder setSocketFactory(OkSocketFactory factory) {
            this.mOptions.mOkSocketFactory = factory;
            return this;
        }

        public Builder setCallbackThreadModeToken(ThreadModeToken threadModeToken) {
            this.mOptions.mCallbackThreadModeToken = threadModeToken;
            return this;
        }

        public OkSocketOptions build() {
            return this.mOptions;
        }
    }

    public abstract static class ThreadModeToken {
        public ThreadModeToken() {
        }

        public abstract void handleCallbackEvent(ActionDispatcher.ActionRunnable var1);
    }
}
