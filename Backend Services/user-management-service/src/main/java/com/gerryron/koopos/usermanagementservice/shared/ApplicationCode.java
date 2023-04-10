package com.gerryron.koopos.usermanagementservice.shared;

public enum ApplicationCode {
    SUCCESS("00", "SUCCESS"),
    VALIDATION_ERROR("01", "Validation error"),

    USERNAME_ALREADY_USED("A1", "Username or email already used"),
    INVALID_ROLE("A2", "Invalid role"),
    LOGIN_FAILED("A3", "Login failed"),
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
