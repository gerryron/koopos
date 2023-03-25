package com.gerryron.koopos.grocerystoreservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> categories;

    public Item(String barcode, String itemName, String description, Integer quantity,
                BigDecimal buyingPrice, BigDecimal sellingPrice, Set<String> categories) {
        this.barcode = barcode;
        this.itemName = itemName;
        this.description = description;
        this.quantity = quantity;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.categories = categories;
    }

    public Item(InventoryEntity inventoryEntity) {
        this.barcode = inventoryEntity.getBarcode();
        this.itemName = inventoryEntity.getItemName();
        this.description = inventoryEntity.getDescription();
        this.quantity = inventoryEntity.getQuantity();
        this.buyingPrice = inventoryEntity.getBuyingPrice();
        this.sellingPrice = inventoryEntity.getSellingPrice();
        this.createdDate = inventoryEntity.getCreatedDate();
        this.updatedDate = inventoryEntity.getUpdatedDate();
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
