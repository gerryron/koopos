package com.gerryron.koopos.grocerystoreservice.repository;

import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @AfterEach
    void tearDown() {
        inventoryRepository.deleteAll();
    }

    @Test
    @Sql("classpath:data/db/inventory.sql")
    void testFindAll() {
        List<InventoryEntity> inventoryEntities = inventoryRepository.findAll();

        assertEquals(2, inventoryEntities.size());
        assertEquals("Item A", inventoryEntities.get(0).getItemName());
        assertEquals("Item B", inventoryEntities.get(1).getItemName());
    }

    @Test
    void testSave() {
        InventoryEntity inventoryEntity = new InventoryEntity();
        inventoryEntity.setInventoryId(3);
        inventoryEntity.setItemName("Item C");
        inventoryRepository.save(inventoryEntity);

        List<InventoryEntity> inventoryEntities = inventoryRepository.findAll();

        assertEquals(1, inventoryEntities.size());
        assertEquals(inventoryEntity.getItemName(), inventoryEntities.get(0).getItemName());
    }

    @Test
    @Sql("classpath:data/db/inventory.sql")
    void testDeleteById() {
        inventoryRepository.deleteById(1);

        List<InventoryEntity> inventoryEntities = inventoryRepository.findAll();

        assertEquals(1, inventoryEntities.size());
        assertEquals("Item B", inventoryEntities.get(0).getItemName());
    }
}
