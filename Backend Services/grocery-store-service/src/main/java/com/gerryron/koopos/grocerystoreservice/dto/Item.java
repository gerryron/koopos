package com.gerryron.koopos.grocerystoreservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @NotNull
    private String barcode;
    @NotBlank
    private String itemName;
    @NotNull
    private String description;
    @PositiveOrZero
    private Integer quantity;
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal buyingPrice;
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal sellingPrice;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> categories;

    public Item(InventoryEntity inventoryEntity) {
        this.barcode = inventoryEntity.getBarcode();
        this.itemName = inventoryEntity.getItemName();
        this.description = inventoryEntity.getDescription();
        this.quantity = inventoryEntity.getQuantity();
        this.buyingPrice = inventoryEntity.getBuyingPrice();
        this.sellingPrice = inventoryEntity.getSellingPrice();
    }

    public Item(InventoryEntity inventoryEntity, boolean withCategories) {
        this(inventoryEntity);
        if (withCategories && null != inventoryEntity.getCategories()) {
            this.categories = inventoryEntity.getCategories().stream()
                    .map(CategoryEntity::getName)
                    .collect(Collectors.toSet());
        }
    }
}
