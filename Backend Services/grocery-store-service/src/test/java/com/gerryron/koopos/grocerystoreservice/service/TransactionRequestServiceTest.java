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
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionRequestServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionDetailsRepository transactionDetailsRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Captor
    private ArgumentCaptor<TransactionEntity> transactionEntityArgumentCaptor;
    @Captor
    private ArgumentCaptor<List<TransactionDetailsEntity>> transactionDetailEntitiesArgumentCaptor;

    @Test
    void testCreateTransaction_Success() {
        Product product = new Product("AA21", "Product A", "Product A Description", 5,
                new BigDecimal(2800), new BigDecimal(3000), Collections.emptySet());
        TransactionRequest.ProductPurchased productPurchased = new TransactionRequest.ProductPurchased();
        productPurchased.setProductId(1);
        productPurchased.setAmount(4);
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionNumber(UUID.randomUUID().toString());
        transactionRequest.setProductsPurchased(Collections.singletonList(productPurchased));

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(new ProductEntity(product)));
        RestResponse<Object> transactionResponse = transactionService.createTransaction(transactionRequest);

        verify(transactionDetailsRepository).saveAll(transactionDetailEntitiesArgumentCaptor.capture());
        verify(transactionRepository).save(transactionEntityArgumentCaptor.capture());
        List<TransactionDetailsEntity> transactionDetailsEntities = transactionDetailEntitiesArgumentCaptor.getValue();
        TransactionEntity transactionEntity = transactionEntityArgumentCaptor.getValue();
        assertEquals(transactionRequest.getTransactionNumber(), transactionEntity.getTransactionNumber());
        assertEquals(transactionRequest.getProductsPurchased().size(), transactionEntity.getAmount());
        assertEquals(transactionRequest.getProductsPurchased().stream()
                        .map(x -> product.getSellingPrice()
                                .multiply(new BigDecimal(x.getAmount())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                transactionEntity.getTotalPrice());
        assertEquals(transactionRequest.getProductsPurchased().stream()
                        .map(x -> product.getSellingPrice().subtract(product.getBuyingPrice())
                                .multiply(new BigDecimal(x.getAmount())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                transactionEntity.getProfit());
        assertNotNull(transactionEntity.getCreatedDate());
        assertNull(transactionEntity.getUpdatedDate());
        assertEquals(transactionRequest.getTransactionNumber(), transactionDetailsEntities.get(0).getTransactionNumber());
//        assertEquals(1, transactionDetailsEntities.get(0).getProductId());
        assertNull(transactionDetailsEntities.get(0).getProductId());
        assertEquals(transactionRequest.getProductsPurchased().get(0).getAmount(), transactionDetailsEntities.get(0).getAmount());
        assertEquals(product.getSellingPrice(), transactionDetailsEntities.get(0).getPrice());
        assertNotNull(transactionDetailsEntities.get(0).getCreatedDate());
        assertNull(transactionDetailsEntities.get(0).getUpdatedDate());
        assertEquals(ApplicationCode.SUCCESS.getCode(), transactionResponse.getResponseStatus().getResponseCode());
        assertEquals(ApplicationCode.SUCCESS.getMessage(), transactionResponse.getResponseStatus().getResponseMessage());
        assertNull(transactionResponse.getData());
        assertNull(transactionResponse.getErrorDetails());
    }

    @Test
    void testCreateTransaction_ProductNotFound() {
        TransactionRequest.ProductPurchased productPurchased = new TransactionRequest.ProductPurchased();
        productPurchased.setProductId(1);
        productPurchased.setAmount(4);
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransactionNumber(UUID.randomUUID().toString());
        transactionRequest.setProductsPurchased(Collections.singletonList(productPurchased));

        KooposException kooposException = assertThrows(KooposException.class,
                () -> transactionService.createTransaction(transactionRequest));
        assertEquals(ApplicationCode.PRODUCT_NOT_FOUND.getCode(), kooposException.getCode());
        assertEquals(ApplicationCode.PRODUCT_NOT_FOUND.getMessage(), kooposException.getMessage());
    }
}
