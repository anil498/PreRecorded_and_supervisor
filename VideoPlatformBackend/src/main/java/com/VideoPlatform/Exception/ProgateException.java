package com.VideoPlatform.Exception;

public abstract class ProgateException extends RuntimeException {

    private static final long serialVersionUID = 2308601250942862834L;

    public ProgateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProgateException(String message) {
        super(message);
    }
}
