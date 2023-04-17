package com.gerryron.kooposservice.repository;

import com.gerryron.kooposservice.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    boolean existsByBarcode(String barcode);

    Optional<ProductEntity> findByBarcode(String barcode);
}
