package com.gerryron.kooposservice.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class ProductRequest {
    @NotNull
    private String barcode;
    @NotBlank
    private String productName;
    @NotNull
    private String description;
    @PositiveOrZero
    private Integer quantity;
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal buyingPrice;
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal sellingPrice;
    @NotNull
    private Set<String> categories;
}
