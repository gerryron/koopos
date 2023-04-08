package com.gerryron.koopos.grocerystoreservice.shared.response;

import com.gerryron.koopos.grocerystoreservice.shared.dto.ErrorDetail;
import com.gerryron.koopos.grocerystoreservice.shared.dto.ResponseStatus;
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
