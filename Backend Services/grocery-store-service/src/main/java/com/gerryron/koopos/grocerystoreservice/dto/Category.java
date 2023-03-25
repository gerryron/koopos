package com.gerryron.koopos.grocerystoreservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Category {
    private Integer id;
    @NotBlank
    private String categoryName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer itemTotal;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Item> inventories;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(CategoryEntity categoryEntity) {
        this.id = categoryEntity.getId();
        this.categoryName = categoryEntity.getName();
        this.createdDate = categoryEntity.getCreatedDate();
        if (null != categoryEntity.getInventories()) {
            this.itemTotal = categoryEntity.getInventories().size();
        } else {
            this.itemTotal = 0;
        }
    }

    public Category(CategoryEntity categoryEntity, boolean withInventories) {
        this(categoryEntity);
        if (withInventories && null != categoryEntity.getInventories()) {
            this.inventories = categoryEntity.getInventories().stream()
                    .map(item -> new Item(item, false))
                    .collect(Collectors.toSet());
        }
    }
}
