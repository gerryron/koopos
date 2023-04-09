package com.gerryron.koopos.usermanagementservice.shared.response;

import com.gerryron.koopos.usermanagementservice.shared.dto.ErrorDetail;
import com.gerryron.koopos.usermanagementservice.shared.dto.ResponseStatus;

import java.util.List;

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
        return new Builder<T>();
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public T getData() {
        return data;
    }

    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }

    public static class Builder<T> {
        private ResponseStatus responseStatus;
        private T data;
        private List<ErrorDetail> errorDetails;

        public Builder<T> responseStatus(final ResponseStatus responseStatus) {
            this.responseStatus = responseStatus;
            return this;
        }

        public Builder<T> data(final T data) {
            this.data = data;
            return this;
        }

        public Builder<T> errorDetails(final List<ErrorDetail> errorDetails) {
            this.errorDetails = errorDetails;
            return this;
        }

        public RestResponse<T> build() {
            return new RestResponse<T>(this);
        }
    }
}
