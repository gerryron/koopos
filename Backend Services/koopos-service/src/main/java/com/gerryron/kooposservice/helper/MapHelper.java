package com.gerryron.kooposservice.helper;

import com.gerryron.kooposservice.dto.response.CategoryResponse;
import com.gerryron.kooposservice.dto.response.ProductResponse;
import com.gerryron.kooposservice.entity.CategoryEntity;
import com.gerryron.kooposservice.entity.ProductEntity;

import java.util.stream.Collectors;

public class MapHelper {

    public static ProductResponse productEntityToResponse(ProductEntity productEntity) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductName(productEntity.getProductName());
        productResponse.setBarcode(productEntity.getBarcode());
        productResponse.setDescription(productEntity.getDescription());
        productResponse.setQuantity(productEntity.getQuantity());
        productResponse.setBuyingPrice(productEntity.getBuyingPrice());
        productResponse.setSellingPrice(productEntity.getSellingPrice());
        productResponse.setCreatedDate(productEntity.getCreatedDate());
        productResponse.setUpdatedDate(productEntity.getUpdatedDate());
        productResponse.setCategories(productEntity.getProductCategories()
                .stream()
                .map(productCategory -> productCategory.getCategory().getName())
                .collect(Collectors.toSet()));

        return productResponse;
    }

    public static CategoryResponse categoryEntityToResponse(CategoryEntity categoryEntity) {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategoryName(categoryEntity.getName());
        categoryResponse.setCreatedDate(categoryEntity.getCreatedDate());
        categoryResponse.setUpdatedDate(categoryEntity.getUpdatedDate());
        categoryResponse.setProducts(categoryEntity.getProductCategories()
                .stream()
                .map(productCategory -> {
                    ProductEntity product = productCategory.getProduct();
                    return CategoryResponse.Product.builder()
                            .productName(product.getProductName())
                            .barcode(product.getBarcode())
                            .build();
                })
                .collect(Collectors.toSet()));

        return categoryResponse;
    }
}
