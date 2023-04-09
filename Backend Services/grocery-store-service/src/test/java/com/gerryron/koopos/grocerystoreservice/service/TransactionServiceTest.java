package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.entity.ProductEntity;
import com.gerryron.koopos.grocerystoreservice.entity.TransactionDetailsEntity;
import com.gerryron.koopos.grocerystoreservice.entity.TransactionEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.ProductRepository;
import com.gerryron.koopos.grocerystoreservice.repository.TransactionDetailsRepository;
import com.gerryron.koopos.grocerystoreservice.repository.TransactionRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import com.gerryron.koopos.grocerystoreservice.shared.dto.Product;
import com.gerryron.koopos.grocerystoreservice.shared.request.TransactionRequest;
import com.gerryron.koopos.grocerystoreservice.shared.response.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final String TRANSACTION_NUMBER = "TransactionNumber01";
    @Mock
    private ProductRepository productRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionDetailsRepository transactionDetailsRepository;
    @InjectMocks
    private TransactionService transactionService;
    @Captor
    private ArgumentCaptor<ProductEntity> productEntityArgumentCaptor;
    @Captor
    private ArgumentCaptor<TransactionEntity> transactionEntityArgumentCaptor;
    @Captor
    private ArgumentCaptor<List<TransactionDetailsEntity>> transactionDetailEntitiesArgumentCaptor;

    private ProductEntity createSimpleProductEntity() {
        Product product = new Product("AA21", "Product A", "Product A Description", 5,
                new BigDecimal(2800), new BigDecimal(3000), Collections.emptySet());
        return new ProductEntity(product);
    }

    private TransactionRequest createSimpleTransactionRequest() {
        TransactionRequest.ProductPurchased productPurchased = new TransactionRequest.ProductPurchased();
        productPurchased.setProductId(1);
        productPurchased.setAmount(4);
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionNumber(TRANSACTION_NUMBER);
        transactionRequest.setProductsPurchased(Collections.singletonList(productPurchased));
        return transactionRequest;
    }

    private TransactionDetailsEntity createSimpleTransactionDetailsEntity() {
        return TransactionDetailsEntity.builder()
                .id(1)
                .transactionNumber(TRANSACTION_NUMBER)
                .productId(1)
                .amount(4)
                .price(new BigDecimal(3000))
                .createdDate(LocalDateTime.now())
                .build();
    }

    private TransactionEntity createSimpleTransactionEntity() {
        return TransactionEntity.builder()
                .id(1)
                .transactionNumber(TRANSACTION_NUMBER)
                .amount(1)
                .totalPrice(new BigDecimal(12000))
                .profit(new BigDecimal(800))
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateTransaction_Success() {
        ProductEntity productEntity = createSimpleProductEntity();
        TransactionRequest transactionRequest = createSimpleTransactionRequest();

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(productEntity));
        RestResponse<Object> transactionResponse = transactionService.createTransaction(transactionRequest);

        verify(productRepository).save(productEntityArgumentCaptor.capture());
        verify(transactionDetailsRepository).saveAll(transactionDetailEntitiesArgumentCaptor.capture());
        verify(transactionRepository).save(transactionEntityArgumentCaptor.capture());
        ProductEntity productEntityArgumentCaptorValue = productEntityArgumentCaptor.getValue();
        List<TransactionDetailsEntity> tdEntitiesArgumentCaptorValue = transactionDetailEntitiesArgumentCaptor.getValue();
        TransactionEntity transactionEntityArgumentCaptorValue = transactionEntityArgumentCaptor.getValue();
        assertEquals(1, productEntityArgumentCaptorValue.getQuantity());
        assertEquals(TRANSACTION_NUMBER, transactionEntityArgumentCaptorValue.getTransactionNumber());
        assertEquals(1, transactionEntityArgumentCaptorValue.getAmount());
        assertEquals(new BigDecimal(12000), transactionEntityArgumentCaptorValue.getTotalPrice());
        assertEquals(new BigDecimal(800), transactionEntityArgumentCaptorValue.getProfit());
        assertNotNull(transactionEntityArgumentCaptorValue.getCreatedDate());
        assertNull(transactionEntityArgumentCaptorValue.getUpdatedDate());
        assertEquals(TRANSACTION_NUMBER, tdEntitiesArgumentCaptorValue.get(0).getTransactionNumber());
//        assertEquals(1, transactionDetailsEntities.get(0).getProductId());
        assertNull(tdEntitiesArgumentCaptorValue.get(0).getProductId());
        assertEquals(4, tdEntitiesArgumentCaptorValue.get(0).getAmount());
        assertEquals(new BigDecimal(3000), tdEntitiesArgumentCaptorValue.get(0).getPrice());
        assertNotNull(tdEntitiesArgumentCaptorValue.get(0).getCreatedDate());
        assertNull(tdEntitiesArgumentCaptorValue.get(0).getUpdatedDate());
        assertEquals(ApplicationCode.SUCCESS.getCode(), transactionResponse.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), transactionResponse.getResponseStatus().getResponseMessage());
        assertNull(transactionResponse.getData());
        assertNull(transactionResponse.getErrorDetails());
    }

    @Test
    void testCreateTransaction_ProductNotFound() {
        TransactionRequest transactionRequest = createSimpleTransactionRequest();

        KooposException kooposException = assertThrows(KooposException.class,
                () -> transactionService.createTransaction(transactionRequest));
        assertEquals(ApplicationCode.PRODUCT_NOT_FOUND.getCode(), kooposException.getCode());
        assertEquals(ApplicationCode.PRODUCT_NOT_FOUND.getMessage(), kooposException.getMessage());
    }

    @Test
    void testCreateTransaction_ProductNotEnough() {
        ProductEntity productEntity = createSimpleProductEntity();
        productEntity.setQuantity(0);
        TransactionRequest transactionRequest = createSimpleTransactionRequest();

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(productEntity));

        KooposException kooposException = assertThrows(KooposException.class,
                () -> transactionService.createTransaction(transactionRequest));
        assertEquals(ApplicationCode.PRODUCT_NOT_ENOUGH.getCode(), kooposException.getCode());
        assertEquals(ApplicationCode.PRODUCT_NOT_ENOUGH.getMessage(), kooposException.getMessage());
    }

    @Test
    void testFindPaginatedTransaction_Success() {
        ProductEntity productEntity = createSimpleProductEntity();
        PageRequest pageRequest = PageRequest.of(1, 10);
        TransactionEntity transactionEntity = createSimpleTransactionEntity();
        TransactionDetailsEntity transactionDetailsEntity = createSimpleTransactionDetailsEntity();
        List<TransactionDetailsEntity> transactionDetailsEntities = Collections.singletonList(transactionDetailsEntity);
        List<TransactionEntity> transactionEntities = Collections.singletonList(transactionEntity);

        when(transactionRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(transactionEntities));
        when(transactionDetailsRepository.findAllByTransactionNumber(anyString())).thenReturn(transactionDetailsEntities);
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(productEntity));
        PaginatedResponse<List<TransactionResponse>> response = transactionService
                .findPaginatedTransaction(pageRequest);

        assertEquals(ApplicationCode.SUCCESS.getCode(), response.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), response.getResponseStatus().getResponseMessage());
        assertNotNull(response.getData());
        assertEquals(transactionEntities.size(), response.getData().size());
        TransactionResponse responseData = response.getData().get(0);
        TransactionResponse.TransactionDetail responseDataDetail = responseData.getTransactionDetails().get(0);
        assertEquals(transactionEntity.getTransactionNumber(), responseData.getTransactionNumber());
        assertEquals(transactionEntity.getAmount(), responseData.getAmount());
        assertEquals(transactionEntity.getTotalPrice(), responseData.getTotalPrice());
        assertEquals(transactionEntity.getProfit(), responseData.getProfit());
        assertEquals(transactionEntity.getCreatedDate(), responseData.getCreatedDate());
        assertEquals(transactionDetailsEntity.getId(), responseDataDetail.getId());
        assertEquals(productEntity.getProductName(), responseDataDetail.getProductName());
        assertEquals(transactionDetailsEntity.getAmount(), responseDataDetail.getAmount());
        assertEquals(transactionDetailsEntity.getPrice(), responseDataDetail.getPrice());
        assertEquals(transactionDetailsEntity.getCreatedDate(), responseDataDetail.getCreatedDate());
        assertEquals(1, response.getDetailPages().getPage());
        assertEquals(10, response.getDetailPages().getRowPerPage());
        assertEquals(1, response.getDetailPages().getTotalData());
    }

    @Test
    void testGetTransactionByTransactionNumber_Success() {
        ProductEntity productEntity = createSimpleProductEntity();
        TransactionEntity transactionEntity = createSimpleTransactionEntity();
        TransactionDetailsEntity transactionDetailsEntity = createSimpleTransactionDetailsEntity();
        List<TransactionDetailsEntity> transactionDetailsEntities = Collections.singletonList(transactionDetailsEntity);

        when(transactionRepository.findByTransactionNumber(anyString())).thenReturn(Optional.of(transactionEntity));
        when(transactionDetailsRepository.findAllByTransactionNumber(anyString())).thenReturn(transactionDetailsEntities);
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(productEntity));
        RestResponse<TransactionResponse> response = transactionService
                .findTransactionByTransactionNumber(TRANSACTION_NUMBER);

        assertEquals(ApplicationCode.SUCCESS.getCode(), response.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), response.getResponseStatus().getResponseMessage());
        TransactionResponse responseData = response.getData();
        TransactionResponse.TransactionDetail responseDataDetail = responseData.getTransactionDetails().get(0);
        assertNotNull(responseData);
        assertEquals(transactionEntity.getTransactionNumber(), responseData.getTransactionNumber());
        assertEquals(transactionEntity.getAmount(), responseData.getAmount());
        assertEquals(transactionEntity.getTotalPrice(), responseData.getTotalPrice());
        assertEquals(transactionEntity.getProfit(), responseData.getProfit());
        assertEquals(transactionEntity.getCreatedDate(), responseData.getCreatedDate());
        assertEquals(transactionDetailsEntity.getId(), responseDataDetail.getId());
        assertEquals(productEntity.getProductName(), responseDataDetail.getProductName());
        assertEquals(transactionDetailsEntity.getAmount(), responseDataDetail.getAmount());
        assertEquals(transactionDetailsEntity.getPrice(), responseDataDetail.getPrice());
        assertEquals(transactionDetailsEntity.getCreatedDate(), responseDataDetail.getCreatedDate());
        assertNull(response.getErrorDetails());
    }

    @Test
    void testGetTransactionByTransactionNumber_TransactionNotFound() {
        when(transactionRepository.findByTransactionNumber(TRANSACTION_NUMBER)).thenReturn(Optional.empty());

        KooposException kooposException = assertThrows(KooposException.class,
                () -> transactionService.findTransactionByTransactionNumber(TRANSACTION_NUMBER));
        assertEquals(ApplicationCode.TRANSACTION_NOT_FOUND.getCode(), kooposException.getCode());
        assertEquals(ApplicationCode.TRANSACTION_NOT_FOUND.getMessage(), kooposException.getMessage());
    }

    @Test
    void testDeleteTransaction_Success() {
        ProductEntity productEntity = createSimpleProductEntity();
        TransactionEntity transactionEntity = createSimpleTransactionEntity();
        TransactionDetailsEntity transactionDetailsEntity = createSimpleTransactionDetailsEntity();
        List<TransactionDetailsEntity> transactionDetailsEntities = Collections.singletonList(transactionDetailsEntity);

        when(transactionRepository.findByTransactionNumber(TRANSACTION_NUMBER)).thenReturn(Optional.of(transactionEntity));
        when(transactionDetailsRepository.findAllByTransactionNumber(TRANSACTION_NUMBER)).thenReturn(transactionDetailsEntities);
        when(productRepository.findById(anyInt())).thenReturn(Optional.of(productEntity));
        RestResponse<Object> response = transactionService.deleteTransaction(TRANSACTION_NUMBER);

        verify(productRepository).save(productEntityArgumentCaptor.capture());
        ProductEntity productEntityArgumentCaptorValue = productEntityArgumentCaptor.getValue();
        assertEquals(9, productEntityArgumentCaptorValue.getQuantity());
        assertEquals(ApplicationCode.SUCCESS.getCode(), response.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), response.getResponseStatus().getResponseMessage());
        assertNull(response.getData());
        assertNull(response.getErrorDetails());
    }

    @Test
    void testDeleteTransaction_TransactionNotFound() {
        when(transactionRepository.findByTransactionNumber(TRANSACTION_NUMBER)).thenReturn(Optional.empty());

        KooposException kooposException = assertThrows(KooposException.class,
                () -> transactionService.deleteTransaction(TRANSACTION_NUMBER));
        assertEquals(ApplicationCode.TRANSACTION_NOT_FOUND.getCode(), kooposException.getCode());
        assertEquals(ApplicationCode.TRANSACTION_NOT_FOUND.getMessage(), kooposException.getMessage());
    }

    @Test
    void testDeleteTransaction_ProductNotFound() {
        TransactionEntity transactionEntity = createSimpleTransactionEntity();
        TransactionDetailsEntity transactionDetailsEntity = createSimpleTransactionDetailsEntity();
        List<TransactionDetailsEntity> transactionDetailsEntities = Collections.singletonList(transactionDetailsEntity);

        when(transactionRepository.findByTransactionNumber(TRANSACTION_NUMBER)).thenReturn(Optional.of(transactionEntity));
        when(transactionDetailsRepository.findAllByTransactionNumber(TRANSACTION_NUMBER)).thenReturn(transactionDetailsEntities);
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        KooposException kooposException = assertThrows(KooposException.class,
                () -> transactionService.deleteTransaction(TRANSACTION_NUMBER));
        assertEquals(ApplicationCode.PRODUCT_NOT_FOUND.getCode(), kooposException.getCode());
        assertEquals(ApplicationCode.PRODUCT_NOT_FOUND.getMessage(), kooposException.getMessage());
    }
}
