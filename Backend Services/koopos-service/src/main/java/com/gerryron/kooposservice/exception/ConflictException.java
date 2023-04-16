package com.gerryron.kooposservice.exception;

import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.enums.ApplicationCode;
import lombok.Getter;

@Getter
public class ConflictException extends KooposException {

    private final ErrorDetail errorDetail;

    public ConflictException(ErrorDetail errorDetail) {
        super(ApplicationCode.DATA_ALREADY_EXISTS);
        this.errorDetail = errorDetail;
    }
}
