package com.gerryron.kooposservice.dto;

import com.gerryron.kooposservice.enums.ApplicationCode;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PaginatedResponse<T> {
    private final ResponseStatus responseStatus;
    private final T data;
    private final PagingMetadata detailPages;

    PaginatedResponse(PaginatedResponseBuilder<T> builder) {
        this.responseStatus = builder.responseStatus;
        this.data = builder.data;
        this.detailPages = builder.detailPages;
    }

    public static <T> PaginatedResponseBuilder<T> builder() {
        return new PaginatedResponseBuilder<>();
    }

    public static class PaginatedResponseBuilder<T> {
        private ResponseStatus responseStatus;
        private T data;
        private PagingMetadata detailPages;

        public PaginatedResponseBuilder<T> responseStatus(ApplicationCode applicationCode) {
            this.responseStatus = new ResponseStatus(applicationCode);
            return this;
        }

        public PaginatedResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public PaginatedResponseBuilder<T> detailPages(PagingMetadata detailPages) {
            this.detailPages = detailPages;
            return this;
        }

        public PaginatedResponse<T> build() {
            return new PaginatedResponse<>(this);
        }
    }

    @Builder
    @Getter
    public static final class PagingMetadata implements Serializable {
        private final int page;
        private final int rowPerPage;
        private final Long totalData;
    }
}
