package com.app.babybaby.exception;

import com.app.babybaby.type.ErrorCode;

public class LoginFailedException extends RuntimeException{
    public LoginFailedException() {
        super(ErrorCode.LOGIN_FAILED.getMessage());
    }

    public LoginFailedException(String message) {
        super(message);
    }
}
