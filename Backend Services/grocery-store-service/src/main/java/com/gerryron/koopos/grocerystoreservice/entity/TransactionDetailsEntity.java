package com.gerryron.koopos.grocerystoreservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction_details")
public class TransactionDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String transactionNumber;
    private Integer productId;
    private Integer amount;
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
