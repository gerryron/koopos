package com.gerryron.koopos.grocerystoreservice.exception;

import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KooposException extends RuntimeException {
    private String code;
    private String message;

    public KooposException(final ApplicationCode applicationCode) {
        code = applicationCode.getCode();
        message = applicationCode.getMessage();
    }
}
