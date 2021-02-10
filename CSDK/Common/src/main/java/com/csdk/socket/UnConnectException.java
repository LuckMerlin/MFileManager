package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:34 2020/12/22
 * TODO
 */
class UnConnectException extends RuntimeException {
    public UnConnectException() {
    }

    public UnConnectException(String message) {
        super(message);
    }

    public UnConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnConnectException(Throwable cause) {
        super(cause);
    }

    protected UnConnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
