package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.shared.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import com.gerryron.koopos.grocerystoreservice.shared.dto.Transaction;
import com.gerryron.koopos.grocerystoreservice.entity.ProductEntity;
import com.gerryron.koopos.grocerystoreservice.entity.TransactionDetailsEntity;
import com.gerryron.koopos.grocerystoreservice.entity.TransactionEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.ProductRepository;
import com.gerryron.koopos.grocerystoreservice.repository.TransactionDetailsRepository;
import com.gerryron.koopos.grocerystoreservice.repository.TransactionRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

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
    public RestResponse<Object> createTransaction(Transaction transaction) {
        log.info("Process transaction with transactionNumber: {}", transaction.getTransactionNumber());

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal profit = BigDecimal.ZERO;
        for (Transaction.ProductPurchased productPurchased : transaction.getProductsPurchased()) {
            ProductEntity product = productRepository.findById(productPurchased.getProductId()).orElseThrow(() ->
                    new KooposException(ApplicationCode.ITEM_NAME_NOT_FOUND));

            totalPrice = totalPrice.add(product.getSellingPrice());
            profit = profit.add(product.getSellingPrice().subtract(product.getBuyingPrice()));
            transactionDetailsRepository.save(TransactionDetailsEntity.builder()
                    .transactionNumber(transaction.getTransactionNumber())
                    .inventoryId(product.getId())
                    .amount(productPurchased.getAmount())
                    .price(product.getSellingPrice())
                    .build());
        }
        transactionRepository.save(TransactionEntity.builder()
                .transactionNumber(transaction.getTransactionNumber())
                .amount(transaction.getProductsPurchased().size())
                .totalPrice(totalPrice)
                .profit(profit)
                .build());

        return RestResponse.builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .build();
    }
}
