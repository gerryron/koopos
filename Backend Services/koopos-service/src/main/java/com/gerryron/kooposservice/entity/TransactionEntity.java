package com.gerryron.kooposservice.entity;

import com.gerryron.kooposservice.enums.TransactionStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String transactionNumber;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY)
    private Set<TransactionDetailEntity> transactionDetails;

}
