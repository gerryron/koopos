package com.gerryron.koopos.grocerystoreservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TransactionRequest {

    @NotNull
    private String transactionNumber;
    @NotNull
    @Valid
    private List<ProductPurchased> productsPurchased;

    @Getter
    @Setter
    public static class ProductPurchased {
        @NotNull
        private Integer productId;
        @NotNull
        @Min(value = 1)
        private Integer amount;
    }
}


