package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        when(categoryRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(expectedResult));

        PaginatedResponse<List<String>> actualResult = categoryService.findPaginatedCategories(0, 10);

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(2, actualResult.getData().size());
        assertEquals(expectedCategory1, actualResult.getData().get(0));
        assertEquals(expectedCategory2, actualResult.getData().get(1));
        assertEquals(1, actualResult.getDetailPages().getPage());
        assertEquals(10, actualResult.getDetailPages().getRowPerPage());
        assertEquals(2, actualResult.getDetailPages().getTotalData());
    }

    @Test
    @Tag("getCategoryEntityIfExists")
    void testGetCategoryEntityIfExists_ReturnExpected() {
        final String expectedCategoryName = "Category A";
        final CategoryEntity expectedCategoryEntity = new CategoryEntity(expectedCategoryName);
        when(categoryRepository.findFirstByName(expectedCategoryName)).thenReturn(expectedCategoryEntity);

        CategoryEntity existingCategory = categoryService.getCategoryEntityIfExists(expectedCategoryEntity);
        assertEquals(expectedCategoryEntity, existingCategory);
    }
}

