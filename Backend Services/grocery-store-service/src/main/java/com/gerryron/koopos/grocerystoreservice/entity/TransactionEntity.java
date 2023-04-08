package com.gerryron.koopos.grocerystoreservice.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String transactionNumber;
    private Integer amount;
    private BigDecimal totalPrice;
    private BigDecimal profit;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    void preInsert() {
        this.createdDate = LocalDateTime.now();
    }
}
