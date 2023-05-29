package com.VideoPlatform.Exception;

public class UserNotAuthorizedException extends ProgateException {
    private static final long serialVersionUID = -3286647743925714170L;

    public UserNotAuthorizedException(String message) {
        super(message);
    }

    public static UserNotAuthorizedException of(String message) {
        return new UserNotAuthorizedException(message);
    }
}
