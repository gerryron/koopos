package com.gerryron.koopos.grocerystoreservice.repository;

import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Integer> {
}
