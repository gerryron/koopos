package com.gerryron.koopos.grocerystoreservice.shared.response;

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
