package com.gerryron.kooposservice.service;

import com.gerryron.kooposservice.dto.PaginatedResponse;
import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.ProductRequest;
import com.gerryron.kooposservice.dto.response.ProductResponse;
import com.gerryron.kooposservice.entity.CategoryEntity;
import com.gerryron.kooposservice.entity.ProductCategoryEntity;
import com.gerryron.kooposservice.entity.ProductEntity;
import com.gerryron.kooposservice.enums.ApplicationCode;
import com.gerryron.kooposservice.exception.BadRequestException;
import com.gerryron.kooposservice.exception.ConflictException;
import com.gerryron.kooposservice.exception.NotFoundException;
import com.gerryron.kooposservice.helper.ErrorDetailHelper;
import com.gerryron.kooposservice.helper.MapHelper;
import com.gerryron.kooposservice.repository.CategoryRepository;
import com.gerryron.kooposservice.repository.ProductCategoriesRepository;
import com.gerryron.kooposservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductCategoriesRepository productCategoriesRepository;

    @Transactional
    public RestResponse<Object> createProduct(ProductRequest request) {
        if (productRepository.existsByBarcode(request.getBarcode())) {
            log.warn("Product with barcode: {} already exists", request.getBarcode());
            throw new ConflictException(ErrorDetailHelper.alreadyExistsSingletonList("barcode"));
        }

        ProductEntity productEntity = new ProductEntity();
        productEntity.setBarcode(request.getBarcode());
        productEntity.setProductName(request.getProductName());
        productEntity.setDescription(request.getDescription());
        productEntity.setQuantity(request.getQuantity());
        productEntity.setBuyingPrice(request.getBuyingPrice());
        productEntity.setSellingPrice(request.getSellingPrice());
        productEntity.setCreatedDate(LocalDateTime.now());
        ProductEntity savedProduct = productRepository.save(productEntity);

        for (String category : request.getCategories()) {
            CategoryEntity categoryEntity = categoryRepository.findByName(category)
                    .orElseThrow(() -> new BadRequestException(
                            ErrorDetailHelper.invalidInputSingletonList("categories")));

            ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
            ProductCategoryEntity.CompositeKey compositeKey = new ProductCategoryEntity.CompositeKey();
            compositeKey.setProductId(savedProduct.getId());
            compositeKey.setCategoryId(categoryEntity.getId());
            productCategoryEntity.setId(compositeKey);
            productCategoryEntity.setProduct(savedProduct);
            productCategoryEntity.setCategory(categoryEntity);
            productCategoriesRepository.save(productCategoryEntity);
        }

        log.info("Product with barcode: {} created successfully", request.getBarcode());
        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }

    @Transactional
    public PaginatedResponse<List<ProductResponse>> findPaginatedProducts(PageRequest page) {
        Page<ProductEntity> productEntities = productRepository.findAll(page);

        List<ProductResponse> products = productEntities.getContent()
                .stream()
                .map(MapHelper::productEntityToResponse)
                .collect(Collectors.toList());

        return PaginatedResponse.<List<ProductResponse>>builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .data(products)
                .detailPages(PaginatedResponse.PagingMetadata.builder()
                        .page(productEntities.getNumber() + 1)
                        .rowPerPage(page.getPageSize())
                        .totalData(productEntities.getTotalElements())
                        .build())
                .build();
    }

    @Transactional
    public RestResponse<ProductResponse> findProduct(String barcode) {
        ProductEntity productEntity = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException(ErrorDetailHelper.notFoundSingletonList("barcode")));

        return RestResponse.<ProductResponse>builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .data(MapHelper.productEntityToResponse(productEntity))
                .build();
    }

    @Transactional
    public RestResponse<Object> updateProduct(String barcode, ProductRequest request) {
        ProductEntity productEntity = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException(ErrorDetailHelper.notFoundSingletonList("barcode")));
        // validate if barcode wants to be replaced
        if (!barcode.equalsIgnoreCase(request.getBarcode()) &&
                productRepository.existsByBarcode(request.getBarcode())) {
            log.warn("Product with barcode: {} already exists", request.getBarcode());
            throw new ConflictException(ErrorDetailHelper.alreadyExistsSingletonList("barcode"));
        }

        productEntity.setBarcode(request.getBarcode());
        productEntity.setProductName(request.getProductName());
        productEntity.setDescription(request.getDescription());
        productEntity.setQuantity(request.getQuantity());
        productEntity.setBuyingPrice(request.getBuyingPrice());
        productEntity.setSellingPrice(request.getSellingPrice());
        productEntity.setUpdatedDate(LocalDateTime.now());
        productCategoriesRepository.deleteAll(productEntity.getProductCategoryEntities());

        for (String category : request.getCategories()) {
            CategoryEntity categoryEntity = categoryRepository.findByName(category)
                    .orElseThrow(() -> new BadRequestException(
                            ErrorDetailHelper.invalidInputSingletonList("categories")));

            ProductCategoryEntity productCategoryEntity = new ProductCategoryEntity();
            ProductCategoryEntity.CompositeKey compositeKey = new ProductCategoryEntity.CompositeKey();
            compositeKey.setProductId(productEntity.getId());
            compositeKey.setCategoryId(categoryEntity.getId());
            productCategoryEntity.setId(compositeKey);
            productCategoryEntity.setProduct(productEntity);
            productCategoryEntity.setCategory(categoryEntity);
            productCategoriesRepository.save(productCategoryEntity);
        }
        productRepository.save(productEntity);

        log.info("Product with barcode: {} updated successfully", barcode);
        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }

    @Transactional
    public RestResponse<Object> deleteProduct(String barcode) {
        ProductEntity productEntity = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException(ErrorDetailHelper.notFoundSingletonList("barcode")));

        productCategoriesRepository.deleteAll(productEntity.getProductCategoryEntities());
        productRepository.delete(productEntity);

        log.info("Product with barcode: {} deleted successfully", barcode);
        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }
}
