package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:49 2020/12/22
 * TODO
 */
public class ReadException extends RuntimeException {
    public ReadException() {
        super();
    }

    public ReadException(String message) {
        super(message);
    }

    public ReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadException(Throwable cause) {
        super(cause);
    }

    protected ReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}