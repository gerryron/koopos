package com.gerryron.koopos.grocerystoreservice.shared.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class PaginatedResponse<T> {
    private final ResponseStatus responseStatus;
    private final T data;
    private final PagingMetadata detailPages;

    @Builder
    @Getter
    public static final class PagingMetadata implements Serializable {
        private final int page;
        private final int rowPerPage;
        private final Long totalData;
    }
}
