package com.gerryron.kooposservice.exception;

import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.enums.ApplicationCode;

import java.util.List;

public class BadRequestException extends KooposException {

    private final List<ErrorDetail> errorDetails;

    public BadRequestException(List<ErrorDetail> errorDetails) {
        super(ApplicationCode.INVALID_PARAMETER);
        this.errorDetails = errorDetails;
    }

    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }

}
