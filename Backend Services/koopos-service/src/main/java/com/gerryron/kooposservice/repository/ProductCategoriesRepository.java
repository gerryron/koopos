package com.gerryron.kooposservice.repository;

import com.gerryron.kooposservice.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoriesRepository extends JpaRepository<ProductCategoryEntity, ProductCategoryEntity.CompositeKey> {
}
