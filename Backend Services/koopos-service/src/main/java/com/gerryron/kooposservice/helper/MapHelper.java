package com.gerryron.kooposservice.helper;

import com.gerryron.kooposservice.dto.response.ProductResponse;
import com.gerryron.kooposservice.entity.ProductEntity;

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

        return productResponse;
    }
}
