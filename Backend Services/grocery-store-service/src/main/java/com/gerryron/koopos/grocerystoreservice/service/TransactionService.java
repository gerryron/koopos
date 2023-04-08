package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.entity.ProductEntity;
import com.gerryron.koopos.grocerystoreservice.entity.TransactionDetailsEntity;
import com.gerryron.koopos.grocerystoreservice.entity.TransactionEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.ProductRepository;
import com.gerryron.koopos.grocerystoreservice.repository.TransactionDetailsRepository;
import com.gerryron.koopos.grocerystoreservice.repository.TransactionRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import com.gerryron.koopos.grocerystoreservice.shared.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.shared.request.TransactionRequest;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TransactionService {

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionDetailsRepository transactionDetailsRepository;

    public TransactionService(ProductRepository productRepository,
                              TransactionRepository transactionRepository,
                              TransactionDetailsRepository transactionDetailsRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.transactionDetailsRepository = transactionDetailsRepository;
    }

    @Transactional
    public RestResponse<Object> createTransaction(TransactionRequest transactionRequest) {
        log.info("Process transaction with transactionNumber: {}", transactionRequest.getTransactionNumber());

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal profit = BigDecimal.ZERO;
        List<TransactionDetailsEntity> transactionDetailsEntities = new ArrayList<>();
        for (TransactionRequest.ProductPurchased productPurchased : transactionRequest.getProductsPurchased()) {
            ProductEntity product = productRepository.findById(productPurchased.getProductId()).orElseThrow(() ->
                    new KooposException(ApplicationCode.ITEM_NAME_NOT_FOUND));

            totalPrice = totalPrice.add(product.getSellingPrice()
                    .multiply(new BigDecimal(productPurchased.getAmount())));
            profit = profit.add(product.getSellingPrice().subtract(product.getBuyingPrice())
                    .multiply(new BigDecimal(productPurchased.getAmount())));
            transactionDetailsEntities.add(TransactionDetailsEntity.builder()
                    .transactionNumber(transactionRequest.getTransactionNumber())
                    .productId(product.getId())
                    .amount(productPurchased.getAmount())
                    .price(product.getSellingPrice())
                    .createdDate(LocalDateTime.now())
                    .build());
        }

        transactionDetailsRepository.saveAll(transactionDetailsEntities);
        transactionRepository.save(TransactionEntity.builder()
                .transactionNumber(transactionRequest.getTransactionNumber())
                .amount(transactionRequest.getProductsPurchased().size())
                .totalPrice(totalPrice)
                .profit(profit)
                .createdDate(LocalDateTime.now())
                .build());

        return RestResponse.builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .build();
    }
}
