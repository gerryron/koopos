package com.gerryron.koopos.grocerystoreservice.shared.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TransactionResponse {
    private String transactionNumber;
    private Integer amount;
    private BigDecimal totalPrice;
    private BigDecimal profit;
    private List<TransactionDetail> transactionDetails;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @Getter
    @Builder
    public static class TransactionDetail {
        private Integer id;
        private String productName;
        private Integer amount;
        private BigDecimal price;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdDate;
    }
}
