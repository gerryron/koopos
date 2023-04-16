package com.gerryron.kooposservice.exception;

import com.gerryron.kooposservice.enums.ApplicationCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KooposException extends RuntimeException {
    private String code;
    private String message;

    public KooposException(final ApplicationCode applicationCode) {
        code = applicationCode.getCode();
        message = applicationCode.getMessage();
    }
}
