package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.ProductEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
import com.gerryron.koopos.grocerystoreservice.repository.ProductRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import com.gerryron.koopos.grocerystoreservice.shared.dto.Product;
import com.gerryron.koopos.grocerystoreservice.shared.response.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.shared.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public RestResponse<Product> createProduct(Product product) {
        productRepository.findByBarcode(product.getBarcode()).ifPresent(s -> {
            throw new KooposException(ApplicationCode.ITEM_ALREADY_EXISTS);
        });

        ProductEntity productEntity = new ProductEntity(product);
        if (null != product.getCategories()) {
            Set<CategoryEntity> categoryEntities = new HashSet<>();
            for (String category : product.getCategories()) {
                CategoryEntity categoryEntity = new CategoryEntity(category);
                categoryEntities.add(getCategoryEntityIfExists(categoryEntity));
            }
            productEntity.setCategories(categoryEntities);
        }

        productRepository.save(productEntity);
        log.info("Success create: {}", product);
        return RestResponse.<Product>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(new Product(productEntity, true))
                .build();
    }

    public PaginatedResponse<List<Product>> findPaginatedProducts(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.ASC);
        Page<ProductEntity> productEntities = productRepository.findAll(
                PageRequest.of(page, size, Sort.by(direction, sortBy)));

        List<Product> products = productEntities.getContent()
                .stream()
                .map(product -> new Product(product, true))
                .collect(Collectors.toList());

        return PaginatedResponse.<List<Product>>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(products)
                .detailPages(PaginatedResponse.PagingMetadata.builder()
                        .page(productEntities.getNumber() + 1)
                        .rowPerPage(size)
                        .totalData(productEntities.getTotalElements())
                        .build())
                .build();
    }

    public RestResponse<Product> findProduct(String barcode, String productName) {
        Product product;
        if (barcode.isBlank() && productName.isBlank()) {
            log.warn("Invalid Parameter: barcode & productName is blank");
            throw new KooposException(ApplicationCode.INVALID_PARAMETER);
        } else if (!barcode.isBlank()) {
            product = new Product(productRepository.findByBarcode(barcode).orElseThrow(() ->
                    new KooposException(ApplicationCode.BARCODE_NOT_FOUND)), true);
        } else {
            product = new Product(productRepository.findByProductName(productName).orElseThrow(() ->
                    new KooposException(ApplicationCode.ITEM_NAME_NOT_FOUND)), true);
        }

        return RestResponse.<Product>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(product)
                .build();
    }

    public RestResponse<Product> updateProduct(String barcode, Product product) {
        ProductEntity existingInventory = productRepository.findByBarcode(barcode).orElseThrow(() ->
                new KooposException(ApplicationCode.BARCODE_NOT_FOUND));
        existingInventory.setProductName(product.getProductName());
        existingInventory.setDescription(product.getDescription());
        existingInventory.setQuantity(product.getQuantity());
        existingInventory.setBuyingPrice(product.getBuyingPrice());
        existingInventory.setSellingPrice(product.getSellingPrice());
        existingInventory.setUpdatedDate(LocalDateTime.now());
        existingInventory.getCategories().clear();
        if (null != product.getCategories()) {
            Set<CategoryEntity> categoryEntities = new HashSet<>();
            for (String category : product.getCategories()) {
                CategoryEntity categoryEntity = new CategoryEntity(category);
                categoryEntities.add(getCategoryEntityIfExists(categoryEntity));
            }
            existingInventory.setCategories(categoryEntities);
        }

        final ProductEntity updatedInventory = productRepository.save(existingInventory);
        return RestResponse.<Product>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(new Product(updatedInventory, true))
                .build();
    }

    public RestResponse<Object> deleteProduct(String barcode) {
        ProductEntity existingInventory = productRepository.findByBarcode(barcode).orElseThrow(() ->
                new KooposException(ApplicationCode.BARCODE_NOT_FOUND));
        existingInventory.getCategories().clear();

        productRepository.delete(existingInventory);
        return RestResponse.builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .build();
    }

    public CategoryEntity getCategoryEntityIfExists(CategoryEntity categoryEntity) {
        return categoryRepository.findFirstByName(categoryEntity.getName())
                .orElse(categoryEntity);
    }
}
