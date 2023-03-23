package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
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

    public PaginatedResponse<List<String>> findPaginatedCategories(int page, int size) {
        Page<CategoryEntity> categoryEntities = categoryRepository.findAll(PageRequest.of(page, size,
                Sort.by(Sort.Direction.ASC, "name")));

        return PaginatedResponse.<List<String>>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(categoryEntities.stream()
                        .map(CategoryEntity::getName)
                        .collect(Collectors.toList()))
                .detailPages(PaginatedResponse.PagingMetadata.builder()
                        .page(categoryEntities.getNumber() + 1)
                        .rowPerPage(size)
                        .totalData(categoryEntities.getTotalElements())
                        .build())
                .build();
    }

    public CategoryEntity getCategoryEntityIfExists(CategoryEntity categoryEntity) {
        CategoryEntity existingCategory = categoryRepository.findFirstByName(categoryEntity.getName());
        return existingCategory != null ? existingCategory : categoryEntity;
    }
}
