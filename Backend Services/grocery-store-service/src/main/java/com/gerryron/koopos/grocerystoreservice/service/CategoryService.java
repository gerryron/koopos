package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.Item;
import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
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

    public RestResponse<List<Item>> findItemByCategoryName(String categoryName) {
        CategoryEntity categoryEntity = categoryRepository.findFirstByName(categoryName)
                .orElseThrow(() -> new KooposException(ApplicationCode.CATEGORY_NOT_FOUND));

        return RestResponse.<List<Item>>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(categoryEntity.getInventories()
                        .stream().map(Item::new)
                        .collect(Collectors.toList()))
                .build();
    }

    public CategoryEntity getCategoryEntityIfExists(CategoryEntity categoryEntity) {
        return categoryRepository.findFirstByName(categoryEntity.getName())
                .orElse(categoryEntity);
    }
}
