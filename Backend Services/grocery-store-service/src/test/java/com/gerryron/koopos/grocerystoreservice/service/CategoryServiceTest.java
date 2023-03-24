package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.Category;
import com.gerryron.koopos.grocerystoreservice.dto.Item;
import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode.CATEGORY_NOT_FOUND;
import static com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @Tag("findPaginatedCategories")
    void testFindPaginatedCategories() {
        final String expectedCategory1 = "Category A";
        final String expectedCategory2 = "Category B";
        List<CategoryEntity> expectedResult = List.of(new CategoryEntity(expectedCategory1),
                new CategoryEntity(expectedCategory2));

        when(categoryRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))))
                .thenReturn(new PageImpl<>(expectedResult));
        PaginatedResponse<List<String>> actualResult = categoryService.findPaginatedCategories(0, 10);

        assertEquals(SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(2, actualResult.getData().size());
        assertEquals(expectedCategory1, actualResult.getData().get(0));
        assertEquals(expectedCategory2, actualResult.getData().get(1));
        assertEquals(1, actualResult.getDetailPages().getPage());
        assertEquals(10, actualResult.getDetailPages().getRowPerPage());
        assertEquals(2, actualResult.getDetailPages().getTotalData());
    }

    @Test
    @Tag("findItemByCategoryName")
    void testFindItemByCategoryName_SUCCESS() {
        final String categoryName = "Category A";
        Item item = new Item("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000),
                Collections.singleton(categoryName));
        CategoryEntity categoryEntity = new CategoryEntity(categoryName);
        categoryEntity.setInventories(Collections.singleton(new InventoryEntity(item)));

        when(categoryRepository.findFirstByName(anyString()))
                .thenReturn(Optional.of(categoryEntity));
        RestResponse<List<Item>> actualResult = categoryService.findItemByCategoryName(categoryName);

        assertEquals(SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(1, actualResult.getData().size());
        assertEquals("AA21", actualResult.getData().get(0).getBarcode());
        assertNull(actualResult.getErrorDetails());
    }

    @Test
    @Tag("findItemByCategoryName")
    void testFindItemByCategoryName_NotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> categoryService.findItemByCategoryName(any()));
        assertEquals(CATEGORY_NOT_FOUND.getCode(), kooposException.getCode());
        assertEquals(CATEGORY_NOT_FOUND.getMessage(), kooposException.getMessage());
    }

    @Test
    @Tag("updateCategory")
    void testUpdateCategory_Success() {
        final Category category = new Category(1, "Category A Updated");

        when(categoryRepository.findById(anyInt()))
                .thenReturn(Optional.of(new CategoryEntity("Category A")));
        when(categoryRepository.save(any())).thenReturn(new CategoryEntity(category));
        RestResponse<Category> actualResult = categoryService.updateCategoryName(1, category);

        assertEquals(SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(category.getCategoryName(), actualResult.getData().getCategoryName());
    }

    @Test
    @Tag("updateCategory")
    void testUpdateCategory_NotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> categoryService.updateCategoryName(anyInt(), new Category()));
        assertEquals(CATEGORY_NOT_FOUND.getCode(), kooposException.getCode());
        assertEquals(CATEGORY_NOT_FOUND.getMessage(), kooposException.getMessage());
    }

    @Test
    @Tag("getCategoryEntityIfExists")
    void testGetCategoryEntityIfExists_ReturnExpected() {
        final String expectedCategoryName = "Category A";
        final CategoryEntity expectedCategoryEntity = new CategoryEntity(expectedCategoryName);

        when(categoryRepository.findFirstByName(expectedCategoryName)).thenReturn(Optional.of(expectedCategoryEntity));
        CategoryEntity existingCategory = categoryService.getCategoryEntityIfExists(expectedCategoryEntity);

        assertEquals(expectedCategoryEntity, existingCategory);
    }
}

