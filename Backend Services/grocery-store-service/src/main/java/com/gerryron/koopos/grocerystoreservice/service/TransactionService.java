package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import com.gerryron.koopos.grocerystoreservice.dto.request.TransactionRequest;
import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.TransactionDetailsEntity;
import com.gerryron.koopos.grocerystoreservice.entity.TransactionEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.InventoryRepository;
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

    private final InventoryRepository inventoryRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionDetailsRepository transactionDetailsRepository;

    public TransactionService(InventoryRepository inventoryRepository,
                              TransactionRepository transactionRepository,
                              TransactionDetailsRepository transactionDetailsRepository) {
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
        this.transactionDetailsRepository = transactionDetailsRepository;
    }

    @Transactional
    public RestResponse<Object> createTransaction(TransactionRequest transactionRequest) {
        log.info("Process transaction with transactionNumber: {}", transactionRequest.getTransactionNumber());

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal profit = BigDecimal.ZERO;
        for (TransactionRequest.ProductPurchased productPurchased : transactionRequest.getProductsPurchased()) {
            InventoryEntity product = inventoryRepository.findById(productPurchased.getProductId()).orElseThrow(() ->
                    new KooposException(ApplicationCode.ITEM_NAME_NOT_FOUND));

            totalPrice = totalPrice.add(product.getSellingPrice());
            profit = profit.add(product.getSellingPrice().subtract(product.getBuyingPrice()));
            transactionDetailsRepository.save(TransactionDetailsEntity.builder()
                    .transactionNumber(transactionRequest.getTransactionNumber())
                    .inventoryId(product.getId())
                    .amount(productPurchased.getAmount())
                    .price(product.getSellingPrice())
                    .build());
        }
        transactionRepository.save(TransactionEntity.builder()
                .transactionNumber(transactionRequest.getTransactionNumber())
                .amount(transactionRequest.getProductsPurchased().size())
                .totalPrice(totalPrice)
                .profit(profit)
                .build());

        return RestResponse.builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .build();
    }
}
