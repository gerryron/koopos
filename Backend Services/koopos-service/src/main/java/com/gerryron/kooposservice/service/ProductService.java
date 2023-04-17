package com.gerryron.kooposservice.service;

import com.gerryron.kooposservice.dto.PaginatedResponse;
import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.ProductRequest;
import com.gerryron.kooposservice.dto.response.ProductResponse;
import com.gerryron.kooposservice.entity.ProductEntity;
import com.gerryron.kooposservice.enums.ApplicationCode;
import com.gerryron.kooposservice.exception.ConflictException;
import com.gerryron.kooposservice.exception.NotFoundException;
import com.gerryron.kooposservice.helper.ErrorDetailHelper;
import com.gerryron.kooposservice.helper.MapHelper;
import com.gerryron.kooposservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public RestResponse<Object> createProduct(ProductRequest request) {
        productRepository.findByBarcode(request.getBarcode()).ifPresent(s -> {
            log.warn("Product with barcode: {} already exists", request.getBarcode());
            throw new ConflictException(ErrorDetailHelper.barcodeAlreadyExists());
        });

        productRepository.save(ProductEntity.builder()
                .barcode(request.getBarcode())
                .productName(request.getProductName())
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .buyingPrice(request.getBuyingPrice())
                .sellingPrice(request.getSellingPrice())
                .createdDate(LocalDateTime.now())
                .build());
        log.info("Product with barcode: {} created successfully", request.getBarcode());

        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }

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

    public RestResponse<ProductResponse> findProduct(String barcode) {
        ProductEntity productEntity = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException(ErrorDetailHelper.barcodeNotFound()));

        return RestResponse.<ProductResponse>builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .data(MapHelper.productEntityToResponse(productEntity))
                .build();
    }

    public RestResponse<Object> updateProduct(String barcode, ProductRequest request) {
        ProductEntity productEntity = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException(ErrorDetailHelper.barcodeNotFound()));

        productEntity.setBarcode(request.getBarcode());
        productEntity.setProductName(request.getProductName());
        productEntity.setDescription(request.getDescription());
        productEntity.setQuantity(request.getQuantity());
        productEntity.setBuyingPrice(request.getBuyingPrice());
        productEntity.setSellingPrice(request.getSellingPrice());
        productEntity.setUpdatedDate(LocalDateTime.now());

        productRepository.save(productEntity);
        log.info("Product with barcode: {} updated successfully", barcode);

        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }

    public RestResponse<Object> deleteProduct(String barcode) {
        ProductEntity productEntity = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new NotFoundException(ErrorDetailHelper.barcodeNotFound()));

        productRepository.delete(productEntity);
        log.info("Product with barcode: {} deleted successfully", barcode);

        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }
}
