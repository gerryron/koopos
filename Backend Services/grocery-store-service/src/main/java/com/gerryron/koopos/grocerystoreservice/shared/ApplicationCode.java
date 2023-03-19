package com.gerryron.koopos.grocerystoreservice.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplicationCode {
    SUCCESS("00", "SUCCESS");

    private final String code;
    private final String message;
}
