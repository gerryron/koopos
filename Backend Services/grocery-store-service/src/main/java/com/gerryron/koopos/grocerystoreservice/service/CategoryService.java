package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.shared.dto.Category;
import com.gerryron.koopos.grocerystoreservice.shared.response.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.shared.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public PaginatedResponse<List<Category>> findPaginatedCategories(int page, int size) {
        Page<CategoryEntity> categoryEntities = categoryRepository.findAll(PageRequest.of(page, size,
                Sort.by(Sort.Direction.ASC, "name")));

        return PaginatedResponse.<List<Category>>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(categoryEntities.stream()
                        .map(Category::new)
                        .collect(Collectors.toList()))
                .detailPages(PaginatedResponse.PagingMetadata.builder()
                        .page(categoryEntities.getNumber() + 1)
                        .rowPerPage(size)
                        .totalData(categoryEntities.getTotalElements())
                        .build())
                .build();
    }

    public RestResponse<Category> findCategory(Integer id, String categoryName) {
        CategoryEntity categoryEntity = new CategoryEntity();
        if (null == id && categoryName.isBlank()) {
            throw new KooposException(ApplicationCode.INVALID_PARAMETER);
        } else if (id != null) {
            categoryEntity = categoryRepository.findById(id)
                    .orElseThrow(() -> new KooposException(ApplicationCode.CATEGORY_NOT_FOUND));
        } else if (!categoryName.isBlank()) {
            categoryEntity = categoryRepository.findFirstByName(categoryName)
                    .orElseThrow(() -> new KooposException(ApplicationCode.CATEGORY_NOT_FOUND));
        }

        return RestResponse.<Category>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(new Category(categoryEntity, true))
                .build();
    }

    public RestResponse<Category> updateCategoryName(Integer id, Category category) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new KooposException(ApplicationCode.CATEGORY_NOT_FOUND));

        categoryEntity.setName(category.getCategoryName());
        CategoryEntity updatedCategory = categoryRepository.save(categoryEntity);
        return RestResponse.<Category>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(new Category(updatedCategory, true))
                .build();
    }

    public RestResponse<Object> deleteCategory(Integer id) {
        CategoryEntity existingCategory = categoryRepository.findById(id).orElseThrow(() ->
                new KooposException(ApplicationCode.CATEGORY_NOT_FOUND));

        int itemTotal = existingCategory.getProductEntities().size();
        if (itemTotal != 0) {
            throw new KooposException(ApplicationCode.CANNOT_DELETE_CATEGORY.getCode(),
                    ApplicationCode.CANNOT_DELETE_CATEGORY.getMessage() + ": " +
                            "because there are " + itemTotal + " items in this category");
        }

        categoryRepository.delete(existingCategory);
        return RestResponse.builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .build();
    }

}
