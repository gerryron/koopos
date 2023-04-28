package com.gerryron.kooposservice.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;

public class TransactionRequest {

    @NotNull
    private String transactionNumber;
    @NotNull
    @Valid
    private Set<ProductPurchased> productsPurchased;
    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public Set<ProductPurchased> getProductsPurchased() {
        return productsPurchased;
    }

    public void setProductsPurchased(Set<ProductPurchased> productsPurchased) {
        this.productsPurchased = productsPurchased;
    }

    public static class ProductPurchased {

        @NotEmpty
        private String barcode;

        @Positive
        private Integer quantity;

        @NotNull
        private BigDecimal discount;

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getDiscount() {
            return discount;
        }

        public void setDiscount(BigDecimal discount) {
            this.discount = discount;
        }
    }
}
