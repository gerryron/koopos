package com.gerryron.koopos.grocerystoreservice.controller;

import com.gerryron.koopos.grocerystoreservice.service.ProductService;
import com.gerryron.koopos.grocerystoreservice.shared.request.ProductDto;
import com.gerryron.koopos.grocerystoreservice.shared.response.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<RestResponse<ProductDto>> saveProduct(@RequestBody @Valid ProductDto productDto) {
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<List<ProductDto>>> getPaginatedProducts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(productService.findPaginatedProducts(page - 1, size,
                sortBy, sortDirection));
    }

    @GetMapping("product")
    public ResponseEntity<RestResponse<ProductDto>> getProduct(
            @RequestParam(value = "barcode", defaultValue = "") String barcode,
            @RequestParam(value = "productName", defaultValue = "") String productName
    ) {
        return ResponseEntity.ok(productService.findProduct(barcode, productName));
    }

    @PutMapping("/product/{barcode}")
    public ResponseEntity<RestResponse<ProductDto>> putProduct(
            @PathVariable(value = "barcode") String barcode,
            @RequestBody @Valid ProductDto productDto) {
        return ResponseEntity.ok((productService.updateProduct(barcode, productDto)));
    }

    @DeleteMapping("/product/{barcode}")
    public ResponseEntity<RestResponse<Object>> deleteProduct(@PathVariable(value = "barcode") String barcode) {
        return ResponseEntity.ok((productService.deleteProduct(barcode)));
    }
}
