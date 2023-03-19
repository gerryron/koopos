package com.gerryron.koopos.grocerystoreservice.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorDetail {
    private final String field;
    private final String object;
    private final String message;
}
