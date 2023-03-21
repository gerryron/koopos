package com.gerryron.koopos.grocerystoreservice.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicationCode {
    SUCCESS("00", "SUCCESS"),
    VALIDATION_ERROR("01", "Validation error"),
    ITEM_ALREADY_EXISTS("02", "Item already exists"),
    BARCODE_NOT_FOUND("03", "Barcode not found");

    private final String code;
    private final String message;
}
