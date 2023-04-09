package com.gerryron.koopos.grocerystoreservice.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicationCode {
    SUCCESS("00", "SUCCESS"),
    VALIDATION_ERROR("01", "Validation error"),
    INVALID_PARAMETER("02", "Invalid parameter"),
    PRODUCT_ALREADY_EXISTS("03", "Product already exists"),
    PRODUCT_NOT_FOUND("04", "Product is not found"),
    CATEGORY_NOT_FOUND("05", "Category is not found"),
    CANNOT_DELETE_CATEGORY("06", "Cannot delete category"),
    TRANSACTION_NOT_FOUND("07", "Transaction is not found"),
    PRODUCT_NOT_ENOUGH("08", "Requested product is not enough");

    private final String code;
    private final String message;
}
