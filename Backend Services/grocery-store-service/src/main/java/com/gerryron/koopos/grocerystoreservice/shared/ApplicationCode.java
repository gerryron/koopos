package com.gerryron.koopos.grocerystoreservice.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicationCode {
    SUCCESS("00", "SUCCESS"),
    ITEM_ALREADY_EXISTS("01", "Item already exists"),
    BARCODE_NOT_FOUND("02", "Barcode not found");

    private final String code;
    private final String message;
}
