package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.Item;
import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
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

import static org.junit.jupiter.api.Assertions.*;
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
    void testCreateItem_Success() {
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
    @Tag("createItem")
    void testCreateItem_ItemAlreadyExists() {
        final String barcode = "AA21";
        final Item expectedItem = new Item(barcode, "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000));
        final Item existingItem = new Item(barcode, "Item Z", "Item Z Description",
                5, new BigDecimal(80000), new BigDecimal(85000));

        when(inventoryRepository.findByBarcode(barcode)).thenReturn(Optional.of(existingItem));

        KooposException kooposException = assertThrows(KooposException.class,
                () -> inventoryService.createItem(expectedItem));
        assertEquals(kooposException.getCode(), ApplicationCode.ITEM_ALREADY_EXISTS.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.ITEM_ALREADY_EXISTS.getMessage());
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
    @Tag("findItem")
    void testFindItemByBarcode_SuccessFindBarcode() {
        final Item expectedItem = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000));
        when(inventoryRepository.findByBarcode(anyString()))
                .thenReturn(Optional.of(expectedItem));

        RestResponse<Item> actualResult = inventoryService.findItem(expectedItem.getBarcode(), "");

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(expectedItem.getBarcode(), actualResult.getData().getBarcode());
        assertEquals(expectedItem.getItemName(), actualResult.getData().getItemName());
        assertNull(actualResult.getErrorDetails());
    }

    @Test
    @Tag("findItem")
    void testFindItemByBarcode_SuccessFindItemName() {
        final Item expectedItem = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000));
        when(inventoryRepository.findByItemName(anyString()))
                .thenReturn(Optional.of(expectedItem));

        RestResponse<Item> actualResult = inventoryService.findItem("", expectedItem.getItemName());

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(expectedItem.getBarcode(), actualResult.getData().getBarcode());
        assertEquals(expectedItem.getItemName(), actualResult.getData().getItemName());
        assertNull(actualResult.getErrorDetails());
    }

    @Test
    @Tag("findItem")
    void testFindItemByBarcode_InvalidParameter() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> inventoryService.findItem("", ""));
        assertEquals(kooposException.getCode(), ApplicationCode.INVALID_PARAMETER.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.INVALID_PARAMETER.getMessage());
    }

    @Test
    @Tag("findItem")
    void testFindItemByBarcode_BarcodeNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> inventoryService.findItem("ASAL", ""));
        assertEquals(kooposException.getCode(), ApplicationCode.BARCODE_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.BARCODE_NOT_FOUND.getMessage());
    }

    @Test
    @Tag("findItem")
    void testFindItemByBarcode_ItemNameNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> inventoryService.findItem("", "ASAL"));
        assertEquals(kooposException.getCode(), ApplicationCode.ITEM_NAME_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.ITEM_NAME_NOT_FOUND.getMessage());
    }

}