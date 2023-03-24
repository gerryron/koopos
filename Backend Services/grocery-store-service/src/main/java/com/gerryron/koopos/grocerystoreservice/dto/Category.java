package com.gerryron.koopos.grocerystoreservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer id;
    @NotBlank
    private String categoryName;

    public Category(CategoryEntity categoryEntity) {
        this.id = categoryEntity.getId();
        this.categoryName = categoryEntity.getName();
    }
}
