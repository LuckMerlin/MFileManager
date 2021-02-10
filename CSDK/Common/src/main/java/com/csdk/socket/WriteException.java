package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:50 2020/12/22
 * TODO
 */
public class WriteException extends RuntimeException {
    public WriteException() {
        super();
    }

    public WriteException(String message) {
        super(message);
    }

    public WriteException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteException(Throwable cause) {
        super(cause);
    }

    protected WriteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
