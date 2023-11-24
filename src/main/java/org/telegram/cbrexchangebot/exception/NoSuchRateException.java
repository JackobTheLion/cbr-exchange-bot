package org.telegram.cbrexchangebot.exception;

public class NoSuchRateException extends RuntimeException {
    public NoSuchRateException() {
    }

    public NoSuchRateException(String message) {
        super(message);
    }

    public NoSuchRateException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchRateException(Throwable cause) {
        super(cause);
    }
}
