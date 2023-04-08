package com.gerryron.koopos.grocerystoreservice.shared.request;

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
public class CategoryDto {
    private Integer id;
    @NotBlank
    private String categoryName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer itemTotal;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<ProductDto> products;

    public CategoryDto(String categoryName) {
        this.categoryName = categoryName;
    }

    public CategoryDto(CategoryEntity categoryEntity) {
        this.id = categoryEntity.getId();
        this.categoryName = categoryEntity.getName();
        this.createdDate = categoryEntity.getCreatedDate();
        if (null != categoryEntity.getProductEntities()) {
            this.itemTotal = categoryEntity.getProductEntities().size();
        } else {
            this.itemTotal = 0;
        }
    }

    public CategoryDto(CategoryEntity categoryEntity, boolean withProducts) {
        this(categoryEntity);
        if (withProducts && null != categoryEntity.getProductEntities()) {
            this.products = categoryEntity.getProductEntities().stream()
                    .map(item -> new ProductDto(item, false))
                    .collect(Collectors.toSet());
        }
    }
}
