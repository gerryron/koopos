package com.gerryron.kooposservice.controller;

import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.CategoryRequest;
import com.gerryron.kooposservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<RestResponse<Object>> saveProduct(@RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

}
