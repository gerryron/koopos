package com.gerryron.kooposservice.dto;

import com.gerryron.kooposservice.enums.ApplicationCode;
import lombok.Getter;

import java.util.List;

@Getter
public class RestResponse<T> {
    private final ResponseStatus responseStatus;
    private final T data;
    private final List<ErrorDetail> errorDetails;

    RestResponse(Builder<T> builder) {
        this.responseStatus = builder.responseStatus;
        this.data = builder.data;
        this.errorDetails = builder.errorDetails;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private ResponseStatus responseStatus;
        private T data;
        private List<ErrorDetail> errorDetails;

        public Builder<T> responseStatus(String code, String message) {
            this.responseStatus = new ResponseStatus(code, message);
            return this;
        }

        public Builder<T> responseStatus(ApplicationCode applicationCode) {
            this.responseStatus = new ResponseStatus(applicationCode);
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> errorDetails(List<ErrorDetail> errorDetails) {
            this.errorDetails = errorDetails;
            return this;
        }

        public RestResponse<T> build() {
            return new RestResponse<>(this);
        }
    }
}
