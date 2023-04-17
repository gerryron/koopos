package com.gerryron.kooposservice.repository;

import com.gerryron.kooposservice.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    boolean existsByName(String categoryName);

    Optional<CategoryEntity> findByName(String categoryName);
}
