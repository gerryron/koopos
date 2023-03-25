package com.gerryron.koopos.grocerystoreservice.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicationCode {
    SUCCESS("00", "SUCCESS"),
    VALIDATION_ERROR("01", "Validation error"),
    ITEM_ALREADY_EXISTS("02", "Item already exists"),
    INVALID_PARAMETER("03", "Invalid parameter"),
    BARCODE_NOT_FOUND("04", "Barcode is not found"),
    ITEM_NAME_NOT_FOUND("05", "Item name is not found"),
    CATEGORY_NOT_FOUND("06", "Category is not found"),
    CANNOT_DELETE_CATEGORY("07", "Cannot delete category");

    private final String code;
    private final String message;
}
