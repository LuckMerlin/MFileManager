package com.csdk.socket;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Create LuckMerlin
 * Date 10:27 2020/12/22
 * TODO
 */
public class PulseManager implements IPulse {
    private volatile IConnectionManager mManager;
    private IPulseSendable mSendable;
    private volatile OkSocketOptions mOkOptions;
    private volatile long mCurrentFrequency;
    private volatile OkSocketOptions.IOThreadMode mCurrentThreadMode;
    private volatile boolean isDead = false;
    private volatile AtomicInteger mLoseTimes = new AtomicInteger(-1);
    private PulseThread mPulseThread = new PulseThread();

    PulseManager(IConnectionManager manager, OkSocketOptions okOptions) {
        this.mManager = manager;
        this.mOkOptions = okOptions;
        this.mCurrentThreadMode = this.mOkOptions.getIOThreadMode();
    }

    public synchronized IPulse setPulseSendable(IPulseSendable sendable) {
        if (sendable != null) {
            this.mSendable = sendable;
        }

        return this;
    }

    public IPulseSendable getPulseSendable() {
        return this.mSendable;
    }

    public synchronized void pulse() {
        this.privateDead();
        this.updateFrequency();
        if (this.mCurrentThreadMode != OkSocketOptions.IOThreadMode.SIMPLEX && this.mPulseThread.isShutdown()) {
            this.mPulseThread.start();
        }

    }

    public synchronized void trigger() {
        if (!this.isDead) {
            if (this.mCurrentThreadMode != OkSocketOptions.IOThreadMode.SIMPLEX && this.mManager != null && this.mSendable != null) {
                this.mManager.send(this.mSendable);
            }

        }
    }

    public synchronized void dead() {
        this.mLoseTimes.set(0);
        this.isDead = true;
        this.privateDead();
    }

    private synchronized void updateFrequency() {
        if (this.mCurrentThreadMode != OkSocketOptions.IOThreadMode.SIMPLEX) {
            this.mCurrentFrequency = this.mOkOptions.getPulseFrequency();
            this.mCurrentFrequency = this.mCurrentFrequency < 1000L ? 1000L : this.mCurrentFrequency;
        } else {
            this.privateDead();
        }

    }

    public synchronized void feed() {
        this.mLoseTimes.set(-1);
    }

    private void privateDead() {
        if (this.mPulseThread != null) {
            this.mPulseThread.shutdown();
        }

    }

    public int getLoseTimes() {
        return this.mLoseTimes.get();
    }

    protected synchronized void setOkOptions(OkSocketOptions okOptions) {
        this.mOkOptions = okOptions;
        this.mCurrentThreadMode = this.mOkOptions.getIOThreadMode();
        this.updateFrequency();
    }

    private class PulseThread extends AbsLoopThread {
        private PulseThread() {
        }

        protected void runInLoopThread() throws Exception {
            if (PulseManager.this.isDead) {
                this.shutdown();
            } else {
                if (PulseManager.this.mManager != null && PulseManager.this.mSendable != null) {
                    if (PulseManager.this.mOkOptions.getPulseFeedLoseTimes() != -1 && PulseManager.this.mLoseTimes.incrementAndGet() >= PulseManager.this.mOkOptions.getPulseFeedLoseTimes()) {
                        PulseManager.this.mManager.disconnect(new DogDeadException("you need feed dog on time,otherwise he will die"));
                    } else {
                        PulseManager.this.mManager.send(PulseManager.this.mSendable);
                    }
                }

                Thread.sleep(PulseManager.this.mCurrentFrequency);
            }
        }

        protected void loopFinish(Exception e) {
        }
    }
}

