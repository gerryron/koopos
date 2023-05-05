package com.gerryron.kooposservice.helper;

import com.gerryron.kooposservice.dto.response.CategoryResponse;
import com.gerryron.kooposservice.dto.response.ProductResponse;
import com.gerryron.kooposservice.entity.CategoryEntity;
import com.gerryron.kooposservice.entity.ProductEntity;

import java.util.stream.Collectors;

public class MapHelper {

    public static ProductResponse productEntityToResponse(ProductEntity productEntity) {
        return ProductResponse.builder()
                .productName(productEntity.getProductName())
                .barcode(productEntity.getBarcode())
                .description(productEntity.getDescription())
                .quantity(productEntity.getQuantity())
                .buyingPrice(productEntity.getBuyingPrice())
                .sellingPrice(productEntity.getSellingPrice())
                .createdDate(productEntity.getCreatedDate())
                .updatedDate(productEntity.getUpdatedDate())
                .categories(productEntity.getProductCategoryEntities()
                        .stream()
                        .map(productCategory -> productCategory.getCategory().getName())
                        .collect(Collectors.toSet()))
                .build();
    }

    public static CategoryResponse categoryEntityToResponse(CategoryEntity categoryEntity) {
        return CategoryResponse.builder()
                .categoryName(categoryEntity.getName())
                .createdDate(categoryEntity.getCreatedDate())
                .updatedDate(categoryEntity.getUpdatedDate())
                .products(categoryEntity.getProductCategoryEntities()
                        .stream()
                        .map(productCategory -> {
                            ProductEntity product = productCategory.getProduct();
                            return CategoryResponse.Product.builder()
                                    .productName(product.getProductName())
                                    .barcode(product.getBarcode())
                                    .build();
                        })
                        .collect(Collectors.toSet()))
                .build();
    }
}
