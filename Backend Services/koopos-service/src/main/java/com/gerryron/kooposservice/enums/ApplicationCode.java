package com.gerryron.kooposservice.enums;


public enum ApplicationCode {
    SUCCESS("00", "SUCCESS"),
    INVALID_PARAMETER("01", "Invalid parameter"),
    DATA_ALREADY_EXISTS("02", "Data already exists"),
    DATA_NOT_FOUND("03", "Data not found"),
    AUTHENTICATION_ERROR("04", "Authentication error"),
    ;

    private final String code;
    private final String message;

    ApplicationCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
