package com.gerryron.kooposservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorDetail {
    private String field;
    private String object;
    private String message;
}
