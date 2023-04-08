package com.gerryron.koopos.grocerystoreservice.repository;

import com.gerryron.koopos.grocerystoreservice.entity.TransactionDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDetailsRepository extends JpaRepository<TransactionDetailsEntity, Integer> {

    List<TransactionDetailsEntity> findAllByTransactionNumber(String transactionNumber);

}
