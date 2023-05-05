package com.gerryron.kooposservice.dto.request;

import javax.validation.constraints.NotEmpty;

public class CancelOrderRequest {

    @NotEmpty
    private String orderNumber;

    private String description;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
