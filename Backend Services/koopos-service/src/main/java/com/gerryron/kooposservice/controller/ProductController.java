package com.gerryron.kooposservice.controller;

import com.gerryron.kooposservice.dto.PaginatedResponse;
import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.ProductRequest;
import com.gerryron.kooposservice.dto.response.ProductResponse;
import com.gerryron.kooposservice.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<RestResponse<Object>> saveProduct(@RequestBody @Valid ProductRequest product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<List<ProductResponse>>> getPaginatedProducts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.ASC);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(productService.findPaginatedProducts(pageRequest));
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<RestResponse<ProductResponse>> getProduct(@PathVariable(value = "barcode") String barcode) {
        return ResponseEntity.ok(productService.findProduct(barcode));
    }

    @PutMapping("/{barcode}")
    public ResponseEntity<RestResponse<Object>> putProduct(
            @PathVariable(value = "barcode") String barcode,
            @RequestBody @Valid ProductRequest product) {
        return ResponseEntity.ok((productService.updateProduct(barcode, product)));
    }

    @DeleteMapping("/{barcode}")
    public ResponseEntity<RestResponse<Object>> deleteProduct(@PathVariable(value = "barcode") String barcode) {
        return ResponseEntity.ok((productService.deleteProduct(barcode)));
    }
}
