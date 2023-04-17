package com.gerryron.kooposservice.exception;

import com.gerryron.kooposservice.enums.ApplicationCode;

public class AuthenticationException extends KooposException {
    public AuthenticationException(String message) {
        super(ApplicationCode.AUTHENTICATION_ERROR.getCode(),
                ApplicationCode.AUTHENTICATION_ERROR.getMessage() + ": " + message);
    }

    ;
}
