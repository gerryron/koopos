package com.gerryron.koopos.grocerystoreservice.dto;

import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import lombok.Getter;

@Getter
public class ResponseStatus {
    private final String responseCode;
    private final String responseMessage;

    public ResponseStatus(ApplicationCode applicationCode) {
        this.responseCode = applicationCode.getCode();
        this.responseMessage = applicationCode.getMessage();
    }
}
