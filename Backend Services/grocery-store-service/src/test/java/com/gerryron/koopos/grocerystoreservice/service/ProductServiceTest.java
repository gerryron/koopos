package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.ProductEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
import com.gerryron.koopos.grocerystoreservice.repository.ProductRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import com.gerryron.koopos.grocerystoreservice.shared.request.ProductDto;
import com.gerryron.koopos.grocerystoreservice.shared.response.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Captor
    private ArgumentCaptor<ProductEntity> productEntityArgumentCaptor;

    @Test
    @Tag("createProduct")
    void testCreateProduct_SuccessWithoutCategories() {
        ProductDto expectedProductDto = new ProductDto("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        expectedProductDto.setUpdatedDate(LocalDateTime.MIN);

        RestResponse<ProductDto> actualResult = productService.createProduct(expectedProductDto);

        verify(productRepository).save(productEntityArgumentCaptor.capture());
        ProductEntity valueCaptor = productEntityArgumentCaptor.getValue();
        assertEquals(expectedProductDto.getProductName(), valueCaptor.getProductName());
        assertNotNull(valueCaptor.getUpdatedDate());
        assertNotEquals(expectedProductDto.getUpdatedDate(), valueCaptor.getUpdatedDate());
        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertNotNull(actualResult.getData());
    }

    @Test
    @Tag("createProduct")
    void testCreateProduct_SuccessWithCategories() {
        final String expectedCategory = "Category A";
        ProductDto expectedProductDto = new ProductDto("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000),
                Collections.singleton(expectedCategory));

        when(categoryRepository.findFirstByName(anyString()))
                .thenReturn(Optional.of(new CategoryEntity(expectedCategory)));
        RestResponse<ProductDto> actualResult = productService.createProduct(expectedProductDto);

        verify(productRepository).save(productEntityArgumentCaptor.capture());
        ProductEntity valueCaptor = productEntityArgumentCaptor.getValue();
        assertEquals(expectedProductDto.getProductName(), valueCaptor.getProductName());
        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertNotNull(actualResult.getData());
    }

    @Test
    @Tag("createProduct")
    void testCreateProduct_ProductAlreadyExists() {
        final String barcode = "AA21";
        final ProductDto expectedProductDto = new ProductDto(barcode, "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        final ProductDto existingProductDto = new ProductDto(barcode, "Product Z", "Product Z Description",
                5, new BigDecimal(80000), new BigDecimal(85000), null);

        when(productRepository.findByBarcode(barcode))
                .thenReturn(Optional.of(new ProductEntity(existingProductDto)));

        KooposException kooposException = assertThrows(KooposException.class,
                () -> productService.createProduct(expectedProductDto));
        assertEquals(kooposException.getCode(), ApplicationCode.ITEM_ALREADY_EXISTS.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.ITEM_ALREADY_EXISTS.getMessage());
    }

    @Test
    @Tag("findPaginatedProducts")
    void testFindPaginatedProducts() {
        final ProductDto expectedProductDto1 = new ProductDto("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        final ProductDto expectedProductDto2 = new ProductDto("AA22", "Product B", "Product B Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        List<ProductEntity> expectedResult = List.of(new ProductEntity(expectedProductDto1),
                new ProductEntity(expectedProductDto2));

        when(productRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"))))
                .thenReturn(new PageImpl<>(expectedResult));
        PaginatedResponse<List<ProductDto>> actualResult = productService.findPaginatedProducts(0, 10,
                "id", "asc");

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(2, actualResult.getData().size());
        assertEquals(expectedProductDto1.getBarcode(), actualResult.getData().get(0).getBarcode());
        assertEquals(expectedProductDto2.getBarcode(), actualResult.getData().get(1).getBarcode());
        assertEquals(1, actualResult.getDetailPages().getPage());
        assertEquals(10, actualResult.getDetailPages().getRowPerPage());
        assertEquals(2, actualResult.getDetailPages().getTotalData());
    }

    @Test
    @Tag("findProduct")
    void testFindProductByBarcode_SuccessFindBarcode() {
        final ProductDto expectedProductDto = new ProductDto("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);

        when(productRepository.findByBarcode(anyString()))
                .thenReturn(Optional.of(new ProductEntity(expectedProductDto)));
        RestResponse<ProductDto> actualResult = productService.findProduct(expectedProductDto.getBarcode(), "");

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(expectedProductDto.getBarcode(), actualResult.getData().getBarcode());
        assertEquals(expectedProductDto.getProductName(), actualResult.getData().getProductName());
        assertNull(actualResult.getErrorDetails());
    }

    @Test
    @Tag("findProduct")
    void testFindProductByBarcode_SuccessFindProductName() {
        final ProductDto expectedProductDto = new ProductDto("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);

        when(productRepository.findByProductName(anyString()))
                .thenReturn(Optional.of(new ProductEntity(expectedProductDto)));
        RestResponse<ProductDto> actualResult = productService.findProduct("", expectedProductDto.getProductName());

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(expectedProductDto.getBarcode(), actualResult.getData().getBarcode());
        assertEquals(expectedProductDto.getProductName(), actualResult.getData().getProductName());
        assertNull(actualResult.getErrorDetails());
    }

    @Test
    @Tag("findProduct")
    void testFindProductByBarcode_InvalidParameter() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> productService.findProduct("", ""));
        assertEquals(kooposException.getCode(), ApplicationCode.INVALID_PARAMETER.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.INVALID_PARAMETER.getMessage());
    }

    @Test
    @Tag("findProduct")
    void testFindProductByBarcode_BarcodeNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> productService.findProduct("ASAL", ""));
        assertEquals(kooposException.getCode(), ApplicationCode.BARCODE_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.BARCODE_NOT_FOUND.getMessage());
    }

    @Test
    @Tag("findProduct")
    void testFindProductByBarcode_ProductNameNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> productService.findProduct("", "ASAL"));
        assertEquals(kooposException.getCode(), ApplicationCode.ITEM_NAME_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.ITEM_NAME_NOT_FOUND.getMessage());
    }

    @Test
    @Tag("updateProduct")
    void testUpdateProduct_SUCCESS() {
        String barcode = "AA21";
        Set<String> categories = new HashSet<>();
        categories.add("Category A");
        categories.add("Category B");
        final ProductDto productDto = new ProductDto(barcode, "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), categories);
        final ProductDto updatedProductDto = new ProductDto(barcode, "Product A", "Product A Description Updated",
                16, new BigDecimal(2000), new BigDecimal(3000), Collections.singleton("Category B"));

        when(productRepository.findByBarcode(barcode)).thenReturn(Optional.of(new ProductEntity(productDto)));
        when(productRepository.save(any())).thenReturn(new ProductEntity(updatedProductDto));
        RestResponse<ProductDto> actualResult = productService.updateProduct(barcode, productDto);

        assertEquals(actualResult.getResponseStatus().getResponseCode(), ApplicationCode.SUCCESS.getCode());
        assertEquals(actualResult.getResponseStatus().getResponseMessage(), ApplicationCode.SUCCESS.getMessage());
        assertEquals(actualResult.getData().getBarcode(), barcode);
        assertEquals(actualResult.getData().getDescription(), updatedProductDto.getDescription());
        assertEquals(actualResult.getData().getCategories().size(), updatedProductDto.getCategories().size());
        assertNull(actualResult.getErrorDetails());
    }

    @Test
    @Tag("updateProduct")
    void testUpdateProduct_BARCODE_NOT_FOUND() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> productService.updateProduct("ASAL", new ProductDto()));
        assertEquals(kooposException.getCode(), ApplicationCode.BARCODE_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.BARCODE_NOT_FOUND.getMessage());
    }

    @Test
    @Tag("deleteProduct")
    void testDeleteProduct_Success() {
        final String barcode = "AA21";
        final ProductDto productDto = new ProductDto(barcode, "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), Collections.singleton("Category A"));

        when(productRepository.findByBarcode(barcode)).thenReturn(Optional.of(new ProductEntity(productDto)));
        RestResponse<Object> actualResult = productService.deleteProduct(barcode);

        verify(productRepository).delete(productEntityArgumentCaptor.capture());
        ProductEntity valueCaptor = productEntityArgumentCaptor.getValue();
        assertEquals(actualResult.getResponseStatus().getResponseCode(), ApplicationCode.SUCCESS.getCode());
        assertEquals(actualResult.getResponseStatus().getResponseMessage(), ApplicationCode.SUCCESS.getMessage());
        assertEquals(valueCaptor.getCategories().size(), 0);
    }

    @Test
    @Tag("deleteProduct")
    void testDeleteProduct_BarcodeNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> productService.deleteProduct("ASAL"));
        assertEquals(kooposException.getCode(), ApplicationCode.BARCODE_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.BARCODE_NOT_FOUND.getMessage());
    }

    @Test
    @Tag("getCategoryEntityIfExists")
    void testGetCategoryEntityIfExists_ReturnExpected() {
        final String expectedCategoryName = "Category A";
        final CategoryEntity expectedCategoryEntity = new CategoryEntity(expectedCategoryName);

        when(categoryRepository.findFirstByName(expectedCategoryName)).thenReturn(Optional.of(expectedCategoryEntity));
        CategoryEntity existingCategory = productService.getCategoryEntityIfExists(expectedCategoryEntity);

        assertEquals(expectedCategoryEntity, existingCategory);
    }
}