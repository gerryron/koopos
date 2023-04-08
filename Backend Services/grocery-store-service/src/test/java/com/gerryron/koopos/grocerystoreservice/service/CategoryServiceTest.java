package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.ProductEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
import com.gerryron.koopos.grocerystoreservice.shared.dto.Category;
import com.gerryron.koopos.grocerystoreservice.shared.dto.Product;
import com.gerryron.koopos.grocerystoreservice.shared.response.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode.*;
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
        final Category expectedCategory1Dto = new Category("Category A");
        final Category expectedCategory2Dto = new Category("Category B");
        List<CategoryEntity> expectedResult = List.of(new CategoryEntity(expectedCategory1Dto),
                new CategoryEntity(expectedCategory2Dto));

        when(categoryRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))))
                .thenReturn(new PageImpl<>(expectedResult));
        PaginatedResponse<List<Category>> actualResult = categoryService.findPaginatedCategories(0, 10);

        assertEquals(SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(2, actualResult.getData().size());
        assertEquals(expectedCategory1Dto.getCategoryName(), actualResult.getData().get(0).getCategoryName());
        assertEquals(0, actualResult.getData().get(0).getItemTotal());
        assertEquals(expectedCategory2Dto.getCategoryName(), actualResult.getData().get(1).getCategoryName());
        assertEquals(0, actualResult.getData().get(1).getItemTotal());
        assertEquals(1, actualResult.getDetailPages().getPage());
        assertEquals(10, actualResult.getDetailPages().getRowPerPage());
        assertEquals(2, actualResult.getDetailPages().getTotalData());
    }

    @Test
    @Tag("findCategory")
    void testFindCategory_SUCCESSFindCategoryName() {
        final String categoryName = "Category A";
        final Product product = new Product("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000),
                Collections.singleton(categoryName));
        CategoryEntity categoryEntity = new CategoryEntity(categoryName);
        categoryEntity.setProductEntities(Collections.singleton(new ProductEntity(product)));

        when(categoryRepository.findFirstByName(anyString()))
                .thenReturn(Optional.of(categoryEntity));
        RestResponse<Category> actualResult = categoryService.findCategory(null, categoryName);

        assertEquals(SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(categoryName, actualResult.getData().getCategoryName());
        assertEquals(1, actualResult.getData().getProducts().size());
        assertNull(actualResult.getErrorDetails());
    }

    @Test
    @Tag("findCategory")
    void testFindCategory_InvalidParameter() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> categoryService.findCategory(null, ""));
        assertEquals(INVALID_PARAMETER.getCode(), kooposException.getCode());
        assertEquals(INVALID_PARAMETER.getMessage(), kooposException.getMessage());
    }

    @Test
    @Tag("findCategory")
    void testFindCategory_CategoryIdNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> categoryService.findCategory(1, ""));
        assertEquals(CATEGORY_NOT_FOUND.getCode(), kooposException.getCode());
        assertEquals(CATEGORY_NOT_FOUND.getMessage(), kooposException.getMessage());
    }

    @Test
    @Tag("findCategory")
    void testFindCategory_CategoryNameNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> categoryService.findCategory(null, "AA21"));
        assertEquals(CATEGORY_NOT_FOUND.getCode(), kooposException.getCode());
        assertEquals(CATEGORY_NOT_FOUND.getMessage(), kooposException.getMessage());
    }

    @Test
    @Tag("updateCategory")
    void testUpdateCategory_Success() {
        final Category category = new Category("Category A Updated");

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
    @Tag("deleteCategory")
    void testDeleteCategory_Success() {
        final Category category = new Category("Category A");
        final CategoryEntity categoryEntity = new CategoryEntity(category);
        categoryEntity.setProductEntities(new HashSet<>());

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(categoryEntity));
        RestResponse<Object> actualResult = categoryService.deleteCategory(anyInt());

        assertEquals(actualResult.getResponseStatus().getResponseCode(), SUCCESS.getCode());
        assertEquals(actualResult.getResponseStatus().getResponseMessage(), SUCCESS.getMessage());
    }

    @Test
    @Tag("deleteCategory")
    void testDeleteCategory_CannotDeleteCategory() {
        final Category category = new Category("Category A");
        final CategoryEntity categoryEntity = new CategoryEntity(category);
        final Product product = new Product("AA21", "Item A", "Item A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), Collections.singleton("Category A"));
        categoryEntity.setProductEntities(Collections.singleton(new ProductEntity(product)));

        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(categoryEntity));

        KooposException kooposException = assertThrows(KooposException.class,
                () -> categoryService.deleteCategory(anyInt()));
        assertEquals(kooposException.getCode(), CANNOT_DELETE_CATEGORY.getCode());
        assertEquals(kooposException.getMessage(), CANNOT_DELETE_CATEGORY.getMessage()
                + ": " + "because there are " + categoryEntity.getProductEntities().size() + " items in this category");
    }

    @Test
    @Tag("deleteCategory")
    void testDeleteCategory_CategoryIdNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> categoryService.deleteCategory(99));
        assertEquals(kooposException.getCode(), CATEGORY_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), CATEGORY_NOT_FOUND.getMessage());
    }
}

