package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.Item;
import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
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
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Captor
    private ArgumentCaptor<InventoryEntity> inventoryEntityArgumentCaptor;

    @Test
    @Tag("createItem")
    void testCreateItem_SuccessWithoutCategories() {
        Item expectedItem = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        expectedItem.setUpdatedDate(LocalDateTime.MIN);

        RestResponse<Item> actualResult = inventoryService.createItem(expectedItem);

        verify(inventoryRepository).save(inventoryEntityArgumentCaptor.capture());
        InventoryEntity valueCaptor = inventoryEntityArgumentCaptor.getValue();
        assertEquals(expectedItem.getItemName(), valueCaptor.getItemName());
        assertNotNull(valueCaptor.getUpdatedDate());
        assertNotEquals(expectedItem.getUpdatedDate(), valueCaptor.getUpdatedDate());
        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(expectedItem, actualResult.getData());
    }

    @Test
    @Tag("createItem")
    void testCreateItem_SuccessWithCategories() {
        final String expectedCategory = "Category A";
        Item expectedItem = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000),
                Collections.singleton(expectedCategory));

        when(categoryRepository.findFirstByName(anyString()))
                .thenReturn(Optional.of(new CategoryEntity(expectedCategory)));
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
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        final Item existingItem = new Item(barcode, "Item Z", "Item Z Description",
                5, new BigDecimal(80000), new BigDecimal(85000), null);

        when(inventoryRepository.findByBarcode(barcode))
                .thenReturn(Optional.of(new InventoryEntity(existingItem)));

        KooposException kooposException = assertThrows(KooposException.class,
                () -> inventoryService.createItem(expectedItem));
        assertEquals(kooposException.getCode(), ApplicationCode.ITEM_ALREADY_EXISTS.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.ITEM_ALREADY_EXISTS.getMessage());
    }

    @Test
    @Tag("findPaginatedInventories")
    void testFindPaginatedInventories() {
        final Item expectedItem1 = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        final Item expectedItem2 = new Item("AA22", "Item B", "Item B Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        List<InventoryEntity> expectedResult = List.of(new InventoryEntity(expectedItem1),
                new InventoryEntity(expectedItem2));

        when(inventoryRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"))))
                .thenReturn(new PageImpl<>(expectedResult));
        PaginatedResponse<List<Item>> actualResult = inventoryService.findPaginatedInventories(0, 10,
                "id", "asc");

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(2, actualResult.getData().size());
        assertEquals(expectedItem1.getBarcode(), actualResult.getData().get(0).getBarcode());
        assertEquals(expectedItem2.getBarcode(), actualResult.getData().get(1).getBarcode());
        assertEquals(1, actualResult.getDetailPages().getPage());
        assertEquals(10, actualResult.getDetailPages().getRowPerPage());
        assertEquals(2, actualResult.getDetailPages().getTotalData());
    }

    @Test
    @Tag("findItem")
    void testFindItemByBarcode_SuccessFindBarcode() {
        final Item expectedItem = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);

        when(inventoryRepository.findByBarcode(anyString()))
                .thenReturn(Optional.of(new InventoryEntity(expectedItem)));
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
                20, new BigDecimal(2000), new BigDecimal(3000), null);

        when(inventoryRepository.findByItemName(anyString()))
                .thenReturn(Optional.of(new InventoryEntity(expectedItem)));
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

    @Test
    @Tag("updateItem")
    void testUpdateItem_SUCCESS() {
        String barcode = "AA21";
        Set<String> categories = new HashSet<>();
        categories.add("Category A");
        categories.add("Category B");
        final Item item = new Item(barcode, "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), categories);
        final Item updatedItem = new Item(barcode, "Item A", "Item A Description Updated",
                16, new BigDecimal(2000), new BigDecimal(3000), Collections.singleton("Category B"));

        when(inventoryRepository.findByBarcode(barcode)).thenReturn(Optional.of(new InventoryEntity(item)));
        when(inventoryRepository.save(any())).thenReturn(new InventoryEntity(updatedItem));
        RestResponse<Item> actualResult = inventoryService.updateItem(barcode, item);

        assertEquals(actualResult.getResponseStatus().getResponseCode(), ApplicationCode.SUCCESS.getCode());
        assertEquals(actualResult.getResponseStatus().getResponseMessage(), ApplicationCode.SUCCESS.getMessage());
        assertEquals(actualResult.getData().getBarcode(), barcode);
        assertEquals(actualResult.getData().getDescription(), updatedItem.getDescription());
        assertEquals(actualResult.getData().getCategories().size(), updatedItem.getCategories().size());
        assertNull(actualResult.getErrorDetails());
    }

    @Test
    @Tag("updateItem")
    void testUpdateItem_BARCODE_NOT_FOUND() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> inventoryService.updateItem("ASAL", new Item()));
        assertEquals(kooposException.getCode(), ApplicationCode.BARCODE_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.BARCODE_NOT_FOUND.getMessage());
    }

    @Test
    @Tag("deleteItem")
    void testDeleteItem_Success() {
        final String barcode = "AA21";
        final Item item = new Item(barcode, "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), Collections.singleton("Category A"));

        when(inventoryRepository.findByBarcode(barcode)).thenReturn(Optional.of(new InventoryEntity(item)));
        RestResponse<Object> actualResult = inventoryService.deleteItem(barcode);

        verify(inventoryRepository).delete(inventoryEntityArgumentCaptor.capture());
        InventoryEntity valueCaptor = inventoryEntityArgumentCaptor.getValue();
        assertEquals(actualResult.getResponseStatus().getResponseCode(), ApplicationCode.SUCCESS.getCode());
        assertEquals(actualResult.getResponseStatus().getResponseMessage(), ApplicationCode.SUCCESS.getMessage());
        assertEquals(valueCaptor.getCategories().size(), 0);
    }

    @Test
    @Tag("deleteItem")
    void testDeleteItem_BarcodeNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> inventoryService.deleteItem("ASAL"));
        assertEquals(kooposException.getCode(), ApplicationCode.BARCODE_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.BARCODE_NOT_FOUND.getMessage());
    }

    @Test
    @Tag("getCategoryEntityIfExists")
    void testGetCategoryEntityIfExists_ReturnExpected() {
        final String expectedCategoryName = "Category A";
        final CategoryEntity expectedCategoryEntity = new CategoryEntity(expectedCategoryName);

        when(categoryRepository.findFirstByName(expectedCategoryName)).thenReturn(Optional.of(expectedCategoryEntity));
        CategoryEntity existingCategory = inventoryService.getCategoryEntityIfExists(expectedCategoryEntity);

        assertEquals(expectedCategoryEntity, existingCategory);
    }
}