package com.gerryron.kooposservice.service;

import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.CategoryRequest;
import com.gerryron.kooposservice.entity.CategoryEntity;
import com.gerryron.kooposservice.enums.ApplicationCode;
import com.gerryron.kooposservice.exception.ConflictException;
import com.gerryron.kooposservice.helper.ErrorDetailHelper;
import com.gerryron.kooposservice.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public RestResponse<Object> createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getCategoryName())) {
            log.warn("Category with name: {} already exists", request.getCategoryName());
            throw new ConflictException(ErrorDetailHelper.categoryNameAlreadyExists());
        }

        categoryRepository.save(CategoryEntity.builder()
                .name(request.getCategoryName())
                .createdDate(LocalDateTime.now())
                .build());

        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }
}
