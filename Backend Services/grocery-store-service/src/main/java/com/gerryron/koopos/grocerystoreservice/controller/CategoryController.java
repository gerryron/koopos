package com.gerryron.koopos.grocerystoreservice.controller;

import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<List<String>>> getPaginatedCategories(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(categoryService.findPaginatedCategories(page - 1, size));
    }
}
