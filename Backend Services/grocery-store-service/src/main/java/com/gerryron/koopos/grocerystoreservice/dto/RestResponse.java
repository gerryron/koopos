package com.gerryron.koopos.grocerystoreservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RestResponse<T> {
    private final ResponseStatus responseStatus;
    private final T data;
    private final List<ErrorDetail> errorDetails;
}
