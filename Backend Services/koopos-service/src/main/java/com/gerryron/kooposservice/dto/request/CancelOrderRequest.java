package com.gerryron.kooposservice.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CancelOrderRequest {

    @NotBlank
    private String orderNumber;

    private String description;
}
