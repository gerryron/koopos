package com.gerryron.kooposservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorDetail {

    private final String property;

    private final String message;
}
