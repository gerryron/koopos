package com.gerryron.koopos.grocerystoreservice.exception;

import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import lombok.Value;

@Value
public class KooposException extends RuntimeException {
    String code;
    String message;

    public KooposException(final ApplicationCode applicationCode) {
        code = applicationCode.getCode();
        message = applicationCode.getMessage();
    }
}
