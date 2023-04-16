package com.gerryron.kooposservice.exception;

import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.enums.ApplicationCode;
import lombok.Getter;

@Getter
public class NotFoundException extends KooposException {

    private final ErrorDetail errorDetail;

    public NotFoundException(ErrorDetail errorDetail) {
        super(ApplicationCode.DATA_NOT_FOUND);
        this.errorDetail = errorDetail;
    }
}
