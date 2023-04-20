package com.gerryron.kooposservice.dto.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class TransactionRequest {

    @NotNull
    private String transactionNumber;
    @NotNull
    @Valid
    private Set<ProductPurchased> productsPurchased;

    @Data
    public static class ProductPurchased {

        @NotEmpty
        private String barcode;
        @Positive
        private Integer quantity;
        @NotNull
        private BigDecimal discount;

    }
}
