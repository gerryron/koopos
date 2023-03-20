package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.Item;
import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import com.gerryron.koopos.grocerystoreservice.repository.InventoryRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Captor
    private ArgumentCaptor<InventoryEntity> inventoryEntityArgumentCaptor;

    @Test
    @Tag("createItem")
    void testCreateItem() {
        final Item expectedItem = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000));

        RestResponse<Item> actualResult = inventoryService.createItem(expectedItem);

        verify(inventoryRepository).save(inventoryEntityArgumentCaptor.capture());
        InventoryEntity valueCaptor = inventoryEntityArgumentCaptor.getValue();
        assertEquals(expectedItem.getItemName(), valueCaptor.getItemName());
        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(expectedItem, actualResult.getData());
    }

    @Test
    @Tag("findPaginatedInventories")
    void testFindPaginatedInventories() {
        final Item expectedItem1 = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000));
        final Item expectedItem2 = new Item("AA22", "Item B", "Item B Description",
                20, new BigDecimal(2000), new BigDecimal(3000));
        List<InventoryEntity> expectedResult = List.of(new InventoryEntity(expectedItem1),
                new InventoryEntity(expectedItem2));
        when(inventoryRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(expectedResult));

        PaginatedResponse<List<Item>> actualResult = inventoryService.findPaginatedInventories(0, 10);

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(2, actualResult.getData().size());
        assertEquals(expectedItem1, actualResult.getData().get(0));
        assertEquals(expectedItem2, actualResult.getData().get(1));
        assertEquals(1, actualResult.getDetailPages().getPage());
        assertEquals(10, actualResult.getDetailPages().getRowPerPage());
        assertEquals(2, actualResult.getDetailPages().getTotalData());
    }

    @Test
    @Tag("findItemByBarcode")
    void testFindItemByBarcode() {
        final Item expectedItem = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000));
        when(inventoryRepository.findByBarcode(anyString()))
                .thenReturn(Optional.of(expectedItem));

        RestResponse<Item> actualResult = inventoryService.findItemByBarcode(expectedItem.getBarcode());

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(expectedItem.getBarcode(), actualResult.getData().getBarcode());
        assertEquals(expectedItem.getItemName(), actualResult.getData().getItemName());
        assertNull(actualResult.getErrorDetails());
    }

}