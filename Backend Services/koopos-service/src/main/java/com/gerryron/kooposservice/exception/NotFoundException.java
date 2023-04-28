package com.gerryron.kooposservice.exception;

import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.enums.ApplicationCode;

import java.util.List;

public class NotFoundException extends KooposException {

    private final List<ErrorDetail> errorDetails;

    public NotFoundException(List<ErrorDetail> errorDetails) {
        super(ApplicationCode.DATA_NOT_FOUND);
        this.errorDetails = errorDetails;
    }

    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }
}
