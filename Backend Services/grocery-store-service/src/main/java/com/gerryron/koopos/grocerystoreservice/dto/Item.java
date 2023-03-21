package com.gerryron.koopos.grocerystoreservice.dto;

import lombok.Value;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Value
public class Item {
    @NotNull
    String barcode;
    @NotBlank
    String itemName;
    @NotNull
    String description;
    @PositiveOrZero
    Integer quantity;
    @DecimalMin(value = "0.0", inclusive = false)
    BigDecimal buyingPrice;
    @DecimalMin(value = "0.0", inclusive = false)
    BigDecimal sellingPrice;
}
