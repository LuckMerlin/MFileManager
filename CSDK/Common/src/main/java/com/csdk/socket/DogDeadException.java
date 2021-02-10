package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:34 2020/12/22
 * TODO
 */
class DogDeadException extends RuntimeException {
    public DogDeadException() {
    }

    public DogDeadException(String message) {
        super(message);
    }

    public DogDeadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DogDeadException(Throwable cause) {
        super(cause);
    }

    protected DogDeadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
