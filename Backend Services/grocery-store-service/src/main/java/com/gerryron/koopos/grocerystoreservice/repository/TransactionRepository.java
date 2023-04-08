package com.gerryron.koopos.grocerystoreservice.repository;

import com.gerryron.koopos.grocerystoreservice.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

    Optional<TransactionEntity> findByTransactionNumber(String transactionNumber);

}
