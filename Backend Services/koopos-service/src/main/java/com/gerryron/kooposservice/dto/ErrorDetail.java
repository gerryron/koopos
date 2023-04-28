package com.gerryron.kooposservice.dto;

public class ErrorDetail {

    private final String field;
    private final String object;
    private final String message;

    public ErrorDetail(Builder builder) {
        this.field = builder.field;
        this.object = builder.object;
        this.message = builder.message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getField() {
        return field;
    }

    public String getObject() {
        return object;
    }

    public String getMessage() {
        return message;
    }

    public static class Builder {
        private String field;
        private String object;
        private String message;

        public Builder field(String field) {
            this.field = field;
            return this;
        }

        public Builder object(String object) {
            this.object = object;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorDetail build() {
            return new ErrorDetail(this);
        }
    }
}
