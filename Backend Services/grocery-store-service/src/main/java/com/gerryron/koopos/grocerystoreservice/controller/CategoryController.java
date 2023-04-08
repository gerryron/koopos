package com.gerryron.koopos.grocerystoreservice.controller;

import com.gerryron.koopos.grocerystoreservice.shared.dto.Category;
import com.gerryron.koopos.grocerystoreservice.shared.response.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import com.gerryron.koopos.grocerystoreservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<List<Category>>> getPaginatedCategories(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(categoryService.findPaginatedCategories(page - 1, size));
    }

    @GetMapping("/category")
    public ResponseEntity<RestResponse<Category>> getCategory(
            @RequestParam(value = "id", defaultValue = "") Integer id,
            @RequestParam(value = "categoryName", defaultValue = "") String categoryName
    ) {
        return ResponseEntity.ok(categoryService.findCategory(id, categoryName));
    }

    @PutMapping(value = "/category/{id}")
    public ResponseEntity<RestResponse<Category>> updateCategory(
            @PathVariable(value = "id") Integer id,
            @RequestBody Category category
    ) {
        return ResponseEntity.ok(categoryService.updateCategoryName(id, category));
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<RestResponse<Object>> deleteCategory(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.ok((categoryService.deleteCategory(id)));
    }
}
