package com.gerryron.kooposservice.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class OrderRequest {

    @NotBlank
    private String orderNumber;

    @NotNull
    @Valid
    private Set<ProductPurchased> productsPurchased;

    @Getter
    @Setter
    public static class ProductPurchased {

        @NotEmpty
        private String barcode;

        @Positive
        private Integer quantity;

        @NotNull
        private BigDecimal discount;
    }
}
