package com.gerryron.kooposservice.service;

import com.gerryron.kooposservice.dto.PaginatedResponse;
import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.CategoryRequest;
import com.gerryron.kooposservice.dto.response.CategoryResponse;
import com.gerryron.kooposservice.entity.CategoryEntity;
import com.gerryron.kooposservice.enums.ApplicationCode;
import com.gerryron.kooposservice.exception.ConflictException;
import com.gerryron.kooposservice.exception.NotFoundException;
import com.gerryron.kooposservice.helper.ErrorDetailHelper;
import com.gerryron.kooposservice.helper.MapHelper;
import com.gerryron.kooposservice.repository.CategoryRepository;
import com.gerryron.kooposservice.repository.ProductCategoriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final ProductCategoriesRepository productCategoriesRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductCategoriesRepository productCategoriesRepository) {
        this.categoryRepository = categoryRepository;
        this.productCategoriesRepository = productCategoriesRepository;
    }

    public RestResponse<Object> createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getCategoryName())) {
            log.warn("Category with name: {} already exists", request.getCategoryName());
            throw new ConflictException(ErrorDetailHelper.categoryNameAlreadyExists());
        }

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(request.getCategoryName());
        categoryEntity.setCreatedDate(LocalDateTime.now());
        categoryRepository.save(categoryEntity);

        log.info("Category with name: {} created successfully", request.getCategoryName());
        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }

    @Transactional
    public PaginatedResponse<List<CategoryResponse>> findPaginatedCategories(PageRequest pageRequest) {
        Page<CategoryEntity> categoryEntities = categoryRepository.findAll(pageRequest);

        List<CategoryResponse> categoryResponses = categoryEntities.getContent()
                .stream()
                .map(MapHelper::categoryEntityToResponse)
                .collect(Collectors.toList());

        return PaginatedResponse.<List<CategoryResponse>>builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .data(categoryResponses)
                .detailPages(PaginatedResponse.PagingMetadata.builder()
                        .page(categoryEntities.getNumber() + 1)
                        .rowPerPage(pageRequest.getPageSize())
                        .totalData(categoryEntities.getTotalElements())
                        .build())
                .build();
    }

    @Transactional
    public RestResponse<CategoryResponse> findCategory(String categoryName) {
        CategoryEntity categoryEntity = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new NotFoundException(ErrorDetailHelper.categoryNameNotFound()));

        return RestResponse.<CategoryResponse>builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .data(MapHelper.categoryEntityToResponse(categoryEntity))
                .build();
    }

    public RestResponse<Object> updateCategory(String categoryName, CategoryRequest request) {
        CategoryEntity categoryEntity = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new NotFoundException(ErrorDetailHelper.categoryNameNotFound()));
        // validate if category name wants to be replaced
        if (!categoryName.equalsIgnoreCase(request.getCategoryName()) &&
                categoryRepository.existsByName(request.getCategoryName())) {
            log.warn("Category with name: {} already exists", request.getCategoryName());
            throw new ConflictException(ErrorDetailHelper.categoryNameAlreadyExists());
        }

        categoryEntity.setName(request.getCategoryName());
        categoryEntity.setUpdatedDate(LocalDateTime.now());
        categoryRepository.save(categoryEntity);

        log.info("Category with name: {} updated successfully", categoryName);
        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }

    @Transactional
    public RestResponse<Object> deleteCategory(String categoryName) {
        CategoryEntity categoryEntity = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new NotFoundException(ErrorDetailHelper.categoryNameNotFound()));

        productCategoriesRepository.deleteAll(categoryEntity.getProductCategoryEntities());
        categoryRepository.delete(categoryEntity);

        log.info("Category with name: {} deleted successfully", categoryName);
        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }


}
