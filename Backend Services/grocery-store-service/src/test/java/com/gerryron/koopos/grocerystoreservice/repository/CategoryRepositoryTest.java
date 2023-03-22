package com.gerryron.koopos.grocerystoreservice.repository;

import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    @Sql("classpath:data/db/category.sql")
    void testFindAll() {
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();

        assertEquals(2, categoryEntities.size());
        assertEquals("Category A", categoryEntities.get(0).getName());
        assertEquals("Category B", categoryEntities.get(1).getName());
    }
}
