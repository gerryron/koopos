package com.gerryron.kooposservice.exception;

import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.enums.ApplicationCode;
import lombok.Getter;

import java.util.List;

@Getter
public class ConflictException extends KooposException {

    private final List<ErrorDetail> errorDetails;

    public ConflictException(List<ErrorDetail> errorDetails) {
        super(ApplicationCode.DATA_ALREADY_EXISTS);
        this.errorDetails = errorDetails;
    }
}
