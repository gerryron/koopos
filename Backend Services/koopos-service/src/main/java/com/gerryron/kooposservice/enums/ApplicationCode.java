package com.gerryron.kooposservice.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationCode {
    SUCCESS("00", "SUCCESS"),
    INVALID_PARAMETER("01", "Invalid parameter"),
    DATA_ALREADY_EXISTS("02", "Data already exists"),
    DATA_NOT_FOUND("03", "Data not found"),
    AUTHENTICATION_ERROR("04", "Authentication error"),
    ;

    private final String code;
    private final String message;
}
