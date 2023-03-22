package com.gerryron.koopos.grocerystoreservice.dto;

import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Data
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

    @Valid
    private Set<Category> categories;

    public Item(InventoryEntity inventoryEntity) {
        this.barcode = inventoryEntity.getBarcode();
        this.itemName = inventoryEntity.getItemName();
        this.description = inventoryEntity.getDescription();
        this.quantity = inventoryEntity.getQuantity();
        this.buyingPrice = inventoryEntity.getBuyingPrice();
        this.sellingPrice = inventoryEntity.getSellingPrice();
        if (null != inventoryEntity.getCategories()) {
            this.categories = inventoryEntity.getCategories().stream()
                    .map(categoryEntity -> new Category(categoryEntity.getName()))
                    .collect(Collectors.toSet());
        }
    }
}
