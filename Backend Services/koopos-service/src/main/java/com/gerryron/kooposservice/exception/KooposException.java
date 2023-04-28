package com.gerryron.kooposservice.exception;

import com.gerryron.kooposservice.enums.ApplicationCode;

public class KooposException extends RuntimeException {

    private final String code;
    private final String message;

    public KooposException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public KooposException(final ApplicationCode applicationCode) {
        code = applicationCode.getCode();
        message = applicationCode.getMessage();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
