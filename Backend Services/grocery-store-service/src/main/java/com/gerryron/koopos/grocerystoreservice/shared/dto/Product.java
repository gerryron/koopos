package com.gerryron.koopos.grocerystoreservice.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.ProductEntity;
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
public class Product {
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> categories;

    public Product(String barcode, String productName, String description, Integer quantity,
                   BigDecimal buyingPrice, BigDecimal sellingPrice, Set<String> categories) {
        this.barcode = barcode;
        this.productName = productName;
        this.description = description;
        this.quantity = quantity;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.categories = categories;
    }

    public Product(ProductEntity productEntity) {
        this.barcode = productEntity.getBarcode();
        this.productName = productEntity.getProductName();
        this.description = productEntity.getDescription();
        this.quantity = productEntity.getQuantity();
        this.buyingPrice = productEntity.getBuyingPrice();
        this.sellingPrice = productEntity.getSellingPrice();
        this.createdDate = productEntity.getCreatedDate();
        this.updatedDate = productEntity.getUpdatedDate();
    }

    public Product(ProductEntity productEntity, boolean withCategories) {
        this(productEntity);
        if (withCategories && null != productEntity.getCategories()) {
            this.categories = productEntity.getCategories().stream()
                    .map(CategoryEntity::getName)
                    .collect(Collectors.toSet());
        }
    }
}
