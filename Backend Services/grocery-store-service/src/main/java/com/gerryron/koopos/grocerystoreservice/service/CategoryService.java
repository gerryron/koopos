package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.Category;
import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        Page<CategoryEntity> categoryEntities = categoryRepository.findAll(PageRequest.of(page, size));

        return PaginatedResponse.<List<Category>>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(categoryEntities.stream()
                        .map(categoryEntity -> new Category(categoryEntity.getName()))
                        .collect(Collectors.toList()))
                .detailPages(PaginatedResponse.PagingMetadata.builder()
                        .page(categoryEntities.getNumber() + 1)
                        .rowPerPage(size)
                        .totalData(categoryEntities.getTotalElements())
                        .build())
                .build();
    }
}
