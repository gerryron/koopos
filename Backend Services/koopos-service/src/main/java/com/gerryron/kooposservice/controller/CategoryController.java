package com.gerryron.kooposservice.controller;

import com.gerryron.kooposservice.dto.PaginatedResponse;
import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.CategoryRequest;
import com.gerryron.kooposservice.dto.response.CategoryResponse;
import com.gerryron.kooposservice.service.CategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<RestResponse<Object>> postCategory(@RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(request));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<List<CategoryResponse>>> getPaginatedCategories(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.ASC);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(categoryService.findPaginatedCategories(pageRequest));
    }

    @GetMapping("/{categoryName}")
    public ResponseEntity<RestResponse<CategoryResponse>> getCategory(
            @PathVariable(value = "categoryName") String categoryName) {
        return ResponseEntity.ok(categoryService.findCategory(categoryName));
    }

    @PutMapping("/{categoryName}")
    public ResponseEntity<RestResponse<Object>> putCategory(
            @PathVariable(value = "categoryName") String categoryName,
            @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok((categoryService.updateCategory(categoryName, request)));
    }

    @DeleteMapping("/{categoryName}")
    public ResponseEntity<RestResponse<Object>> deleteCategory(
            @PathVariable(value = "categoryName") String categoryName) {
        return ResponseEntity.ok((categoryService.deleteCategory(categoryName)));
    }
}
