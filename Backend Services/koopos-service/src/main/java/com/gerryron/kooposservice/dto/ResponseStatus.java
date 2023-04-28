package com.gerryron.kooposservice.dto;

import com.gerryron.kooposservice.enums.ApplicationCode;

public class ResponseStatus {

    private final String responseCode;
    private final String responseMessage;

    public ResponseStatus(String responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public ResponseStatus(ApplicationCode applicationCode) {
        this.responseCode = applicationCode.getCode();
        this.responseMessage = applicationCode.getMessage();
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
