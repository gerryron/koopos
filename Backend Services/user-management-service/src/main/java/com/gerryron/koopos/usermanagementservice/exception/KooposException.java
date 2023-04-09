package com.gerryron.koopos.usermanagementservice.exception;

import com.gerryron.koopos.usermanagementservice.shared.ApplicationCode;

public class KooposException extends RuntimeException {
    private String code;
    private String message;

    public KooposException(final ApplicationCode applicationCode) {
        code = applicationCode.getCode();
        message = applicationCode.getMessage();
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
