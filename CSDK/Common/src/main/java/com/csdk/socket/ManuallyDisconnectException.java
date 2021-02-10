package com.csdk.socket;

/**
 * Create LuckMerlin
 * Date 10:34 2020/12/22
 * TODO
 */
 class ManuallyDisconnectException extends RuntimeException {
    public ManuallyDisconnectException() {
    }

    public ManuallyDisconnectException(String message) {
        super(message);
    }

    public ManuallyDisconnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManuallyDisconnectException(Throwable cause) {
        super(cause);
    }
}
