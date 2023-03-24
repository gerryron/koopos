package com.gerryron.koopos.grocerystoreservice.controller;

import com.gerryron.koopos.grocerystoreservice.dto.Item;
import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import com.gerryron.koopos.grocerystoreservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/categories")
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

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<RestResponse<List<Item>>> getItemByCategoryName(
            @PathVariable(value = "categoryName") String categoryName
    ) {
        return ResponseEntity.ok(categoryService.findItemByCategoryName(categoryName));
    }

    @PutMapping(value = "/category/{categoryNameBefore}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<String>> updateCategoryName(
            @PathVariable(value = "categoryNameBefore") String categoryNameBefore,
            @RequestBody @NotBlank String categoryName
    ) {
        return ResponseEntity.ok(categoryService.updateCategoryName(categoryNameBefore, categoryName));
    }
}
