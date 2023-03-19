package com.gerryron.koopos.grocerystoreservice.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class Item {
    String barcode;
    String itemName;
    String description;
    Integer quantity;
    BigDecimal buyingPrice;
    BigDecimal sellingPrice;
}
