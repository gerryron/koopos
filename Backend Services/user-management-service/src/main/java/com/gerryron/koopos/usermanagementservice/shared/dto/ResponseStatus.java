package com.gerryron.koopos.usermanagementservice.shared.dto;

import com.gerryron.koopos.usermanagementservice.shared.ApplicationCode;

public class ResponseStatus {
    private String responseCode;
    private String responseMessage;

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
