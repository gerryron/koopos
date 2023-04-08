package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.ProductEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
import com.gerryron.koopos.grocerystoreservice.repository.ProductRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import com.gerryron.koopos.grocerystoreservice.shared.request.ProductDto;
import com.gerryron.koopos.grocerystoreservice.shared.response.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.ResponseStatus;
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
    public RestResponse<ProductDto> createProduct(ProductDto productDto) {
        productRepository.findByBarcode(productDto.getBarcode()).ifPresent(s -> {
            throw new KooposException(ApplicationCode.ITEM_ALREADY_EXISTS);
        });

        ProductEntity productEntity = new ProductEntity(productDto);
        if (null != productDto.getCategories()) {
            Set<CategoryEntity> categoryEntities = new HashSet<>();
            for (String category : productDto.getCategories()) {
                CategoryEntity categoryEntity = new CategoryEntity(category);
                categoryEntities.add(getCategoryEntityIfExists(categoryEntity));
            }
            productEntity.setCategories(categoryEntities);
        }

        productRepository.save(productEntity);
        log.info("Success create: {}", productDto);
        return RestResponse.<ProductDto>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(new ProductDto(productEntity, true))
                .build();
    }

    public PaginatedResponse<List<ProductDto>> findPaginatedProducts(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.ASC);
        Page<ProductEntity> productEntities = productRepository.findAll(
                PageRequest.of(page, size, Sort.by(direction, sortBy)));

        List<ProductDto> products = productEntities.getContent()
                .stream()
                .map(product -> new ProductDto(product, true))
                .collect(Collectors.toList());

        return PaginatedResponse.<List<ProductDto>>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(products)
                .detailPages(PaginatedResponse.PagingMetadata.builder()
                        .page(productEntities.getNumber() + 1)
                        .rowPerPage(size)
                        .totalData(productEntities.getTotalElements())
                        .build())
                .build();
    }

    public RestResponse<ProductDto> findProduct(String barcode, String productName) {
        ProductDto productDto;
        if (barcode.isBlank() && productName.isBlank()) {
            log.warn("Invalid Parameter: barcode & productName is blank");
            throw new KooposException(ApplicationCode.INVALID_PARAMETER);
        } else if (!barcode.isBlank()) {
            productDto = new ProductDto(productRepository.findByBarcode(barcode).orElseThrow(() ->
                    new KooposException(ApplicationCode.BARCODE_NOT_FOUND)), true);
        } else {
            productDto = new ProductDto(productRepository.findByProductName(productName).orElseThrow(() ->
                    new KooposException(ApplicationCode.ITEM_NAME_NOT_FOUND)), true);
        }

        return RestResponse.<ProductDto>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(productDto)
                .build();
    }

    public RestResponse<ProductDto> updateProduct(String barcode, ProductDto productDto) {
        ProductEntity existingInventory = productRepository.findByBarcode(barcode).orElseThrow(() ->
                new KooposException(ApplicationCode.BARCODE_NOT_FOUND));
        existingInventory.setProductName(productDto.getProductName());
        existingInventory.setDescription(productDto.getDescription());
        existingInventory.setQuantity(productDto.getQuantity());
        existingInventory.setBuyingPrice(productDto.getBuyingPrice());
        existingInventory.setSellingPrice(productDto.getSellingPrice());
        existingInventory.setUpdatedDate(LocalDateTime.now());
        existingInventory.getCategories().clear();
        if (null != productDto.getCategories()) {
            Set<CategoryEntity> categoryEntities = new HashSet<>();
            for (String category : productDto.getCategories()) {
                CategoryEntity categoryEntity = new CategoryEntity(category);
                categoryEntities.add(getCategoryEntityIfExists(categoryEntity));
            }
            existingInventory.setCategories(categoryEntities);
        }

        final ProductEntity updatedInventory = productRepository.save(existingInventory);
        return RestResponse.<ProductDto>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(new ProductDto(updatedInventory, true))
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
