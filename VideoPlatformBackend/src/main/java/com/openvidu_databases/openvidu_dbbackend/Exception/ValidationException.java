package com.openvidu_databases.openvidu_dbbackend.Exception;

public class ValidationException extends ProgateException {

    private static final long serialVersionUID = 1457879637978162701L;

    public ValidationException(String message) {
        super(message);
    }

    public static ValidationException of(String message) {
        return new ValidationException(message);
    }
}