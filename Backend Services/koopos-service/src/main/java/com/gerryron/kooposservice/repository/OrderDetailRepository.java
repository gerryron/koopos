package com.gerryron.kooposservice.repository;

import com.gerryron.kooposservice.entity.OrderDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailsEntity, Integer> {
}
