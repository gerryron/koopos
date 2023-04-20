package com.gerryron.kooposservice.repository;

import com.gerryron.kooposservice.entity.TransactionDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDetailRepository extends JpaRepository<TransactionDetailEntity, Integer> {
}
