package com.gerryron.koopos.grocerystoreservice.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Category {
    private Integer id;
    @NotNull
    @NotBlank
    private String categoryName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer itemTotal;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Product> products;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(CategoryEntity categoryEntity) {
        this.id = categoryEntity.getId();
        this.categoryName = categoryEntity.getName();
        this.createdDate = categoryEntity.getCreatedDate();
        this.updatedDate = categoryEntity.getUpdatedDate();
        if (null != categoryEntity.getProductEntities()) {
            this.itemTotal = categoryEntity.getProductEntities().size();
        } else {
            this.itemTotal = 0;
        }
    }

    public Category(CategoryEntity categoryEntity, boolean withProducts) {
        this(categoryEntity);
        if (withProducts && null != categoryEntity.getProductEntities()) {
            this.products = categoryEntity.getProductEntities().stream()
                    .map(item -> new Product(item, false))
                    .collect(Collectors.toSet());
        }
    }
}
