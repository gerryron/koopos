package com.gerryron.kooposservice.dto;

import com.gerryron.kooposservice.enums.ApplicationCode;

import java.io.Serializable;

public class PaginatedResponse<T> {

    private final ResponseStatus responseStatus;
    private final T data;
    private final PagingMetadata detailPages;

    PaginatedResponse(Builder<T> builder) {
        this.responseStatus = builder.responseStatus;
        this.data = builder.data;
        this.detailPages = builder.detailPages;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public T getData() {
        return data;
    }

    public PagingMetadata getDetailPages() {
        return detailPages;
    }

    public static class Builder<T> {
        private ResponseStatus responseStatus;
        private T data;
        private PagingMetadata detailPages;

        public Builder<T> responseStatus(ApplicationCode applicationCode) {
            this.responseStatus = new ResponseStatus(applicationCode);
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> detailPages(PagingMetadata detailPages) {
            this.detailPages = detailPages;
            return this;
        }

        public PaginatedResponse<T> build() {
            return new PaginatedResponse<>(this);
        }
    }

    public static final class PagingMetadata implements Serializable {
        private final int page;
        private final int rowPerPage;
        private final long totalData;

        public PagingMetadata(Builder builder) {
            this.page = builder.page;
            this.rowPerPage = builder.rowPerPage;
            this.totalData = builder.totalData;
        }

        public static Builder builder() {
            return new Builder();
        }

        public int getPage() {
            return page;
        }

        public int getRowPerPage() {
            return rowPerPage;
        }

        public Long getTotalData() {
            return totalData;
        }

        public static class Builder {
            private int page;
            private int rowPerPage;
            private Long totalData;

            public Builder page(int page) {
                this.page = page;
                return this;
            }

            public Builder rowPerPage(int rowPerPage) {
                this.rowPerPage = rowPerPage;
                return this;
            }

            public Builder totalData(long totalData) {
                this.totalData = totalData;
                return this;
            }

            public PagingMetadata build() {
                return new PagingMetadata(this);
            }
        }
    }
}
