package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.ProductEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
import com.gerryron.koopos.grocerystoreservice.repository.ProductRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import com.gerryron.koopos.grocerystoreservice.shared.dto.Product;
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
        Product expectedProduct = new Product("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);

        RestResponse<Product> actualResult = productService.createProduct(expectedProduct);

        verify(productRepository).save(productEntityArgumentCaptor.capture());
        ProductEntity valueCaptor = productEntityArgumentCaptor.getValue();
        assertEquals(expectedProduct.getProductName(), valueCaptor.getProductName());
        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertNotNull(actualResult.getData());
    }

    @Test
    @Tag("createProduct")
    void testCreateProduct_SuccessWithCategories() {
        final String expectedCategory = "Category A";
        Product expectedProduct = new Product("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000),
                Collections.singleton(expectedCategory));

        when(categoryRepository.findFirstByName(anyString()))
                .thenReturn(Optional.of(new CategoryEntity(expectedCategory)));
        RestResponse<Product> actualResult = productService.createProduct(expectedProduct);

        verify(productRepository).save(productEntityArgumentCaptor.capture());
        ProductEntity valueCaptor = productEntityArgumentCaptor.getValue();
        assertEquals(expectedProduct.getProductName(), valueCaptor.getProductName());
        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertNotNull(actualResult.getData());
    }

    @Test
    @Tag("createProduct")
    void testCreateProduct_ProductAlreadyExists() {
        final String barcode = "AA21";
        final Product expectedProduct = new Product(barcode, "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        final Product existingProduct = new Product(barcode, "Product Z", "Product Z Description",
                5, new BigDecimal(80000), new BigDecimal(85000), null);

        when(productRepository.findByBarcode(barcode))
                .thenReturn(Optional.of(new ProductEntity(existingProduct)));

        KooposException kooposException = assertThrows(KooposException.class,
                () -> productService.createProduct(expectedProduct));
        assertEquals(kooposException.getCode(), ApplicationCode.PRODUCT_ALREADY_EXISTS.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.PRODUCT_ALREADY_EXISTS.getMessage());
    }

    @Test
    @Tag("findPaginatedProducts")
    void testFindPaginatedProducts() {
        final Product expectedProduct1 = new Product("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        final Product expectedProduct2 = new Product("AA22", "Product B", "Product B Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);
        List<ProductEntity> expectedResult = List.of(new ProductEntity(expectedProduct1),
                new ProductEntity(expectedProduct2));

        when(productRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"))))
                .thenReturn(new PageImpl<>(expectedResult));
        PaginatedResponse<List<Product>> actualResult = productService.findPaginatedProducts(0, 10,
                "id", "asc");

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(2, actualResult.getData().size());
        assertEquals(expectedProduct1.getBarcode(), actualResult.getData().get(0).getBarcode());
        assertEquals(expectedProduct2.getBarcode(), actualResult.getData().get(1).getBarcode());
        assertEquals(1, actualResult.getDetailPages().getPage());
        assertEquals(10, actualResult.getDetailPages().getRowPerPage());
        assertEquals(2, actualResult.getDetailPages().getTotalData());
    }

    @Test
    @Tag("findProduct")
    void testFindProductByBarcode_SuccessFindBarcode() {
        final Product expectedProduct = new Product("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);

        when(productRepository.findByBarcode(anyString()))
                .thenReturn(Optional.of(new ProductEntity(expectedProduct)));
        RestResponse<Product> actualResult = productService.findProduct(expectedProduct.getBarcode(), "");

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(expectedProduct.getBarcode(), actualResult.getData().getBarcode());
        assertEquals(expectedProduct.getProductName(), actualResult.getData().getProductName());
        assertNull(actualResult.getErrorDetails());
    }

    @Test
    @Tag("findProduct")
    void testFindProductByBarcode_SuccessFindProductName() {
        final Product expectedProduct = new Product("AA21", "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), null);

        when(productRepository.findByProductName(anyString()))
                .thenReturn(Optional.of(new ProductEntity(expectedProduct)));
        RestResponse<Product> actualResult = productService.findProduct("", expectedProduct.getProductName());

        assertEquals(ApplicationCode.SUCCESS.getCode(), actualResult.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), actualResult.getResponseStatus().getResponseMessage());
        assertEquals(expectedProduct.getBarcode(), actualResult.getData().getBarcode());
        assertEquals(expectedProduct.getProductName(), actualResult.getData().getProductName());
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
        assertEquals(kooposException.getCode(), ApplicationCode.PRODUCT_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @Tag("findProduct")
    void testFindProductByBarcode_ProductNameNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> productService.findProduct("", "ASAL"));
        assertEquals(kooposException.getCode(), ApplicationCode.PRODUCT_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @Tag("updateProduct")
    void testUpdateProduct_Success() {
        String barcode = "AA21";
        Set<String> categories = new HashSet<>();
        categories.add("Category A");
        categories.add("Category B");
        final Product product = new Product(barcode, "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), categories);
        final Product updatedProduct = new Product(barcode, "Product A", "Product A Description Updated",
                16, new BigDecimal(2000), new BigDecimal(3000), Collections.singleton("Category B"));

        when(productRepository.findByBarcode(barcode)).thenReturn(Optional.of(new ProductEntity(product)));
        when(productRepository.save(any())).thenReturn(new ProductEntity(updatedProduct));
        RestResponse<Product> actualResult = productService.updateProduct(barcode, product);

        assertEquals(actualResult.getResponseStatus().getResponseCode(), ApplicationCode.SUCCESS.getCode());
        assertEquals(actualResult.getResponseStatus().getResponseMessage(), ApplicationCode.SUCCESS.getMessage());
        assertEquals(actualResult.getData().getBarcode(), barcode);
        assertEquals(actualResult.getData().getDescription(), updatedProduct.getDescription());
        assertEquals(actualResult.getData().getCategories().size(), updatedProduct.getCategories().size());
        assertNull(actualResult.getErrorDetails());
    }

    @Test
    @Tag("updateProduct")
    void testUpdateProduct_BarcodeNotFound() {
        KooposException kooposException = assertThrows(KooposException.class,
                () -> productService.updateProduct("ASAL", new Product()));
        assertEquals(kooposException.getCode(), ApplicationCode.PRODUCT_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @Test
    @Tag("deleteProduct")
    void testDeleteProduct_Success() {
        final String barcode = "AA21";
        final Product product = new Product(barcode, "Product A", "Product A Description",
                20, new BigDecimal(2000), new BigDecimal(3000), Collections.singleton("Category A"));

        when(productRepository.findByBarcode(barcode)).thenReturn(Optional.of(new ProductEntity(product)));
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
        assertEquals(kooposException.getCode(), ApplicationCode.PRODUCT_NOT_FOUND.getCode());
        assertEquals(kooposException.getMessage(), ApplicationCode.PRODUCT_NOT_FOUND.getMessage());
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