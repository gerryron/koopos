package com.gerryron.koopos.grocerystoreservice.dto;

import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStatus {
    private String responseCode;
    private String responseMessage;

    public ResponseStatus(ApplicationCode applicationCode) {
        this.responseCode = applicationCode.getCode();
        this.responseMessage = applicationCode.getMessage();
    }
}
