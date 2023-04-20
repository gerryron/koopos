package com.gerryron.kooposservice.service;

import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.TransactionRequest;
import com.gerryron.kooposservice.entity.ProductEntity;
import com.gerryron.kooposservice.entity.TransactionDetailEntity;
import com.gerryron.kooposservice.entity.TransactionEntity;
import com.gerryron.kooposservice.enums.ApplicationCode;
import com.gerryron.kooposservice.enums.TransactionStatus;
import com.gerryron.kooposservice.exception.BadRequestException;
import com.gerryron.kooposservice.helper.ErrorDetailHelper;
import com.gerryron.kooposservice.repository.ProductRepository;
import com.gerryron.kooposservice.repository.TransactionDetailRepository;
import com.gerryron.kooposservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;

    @Transactional
    public RestResponse<Object> createTransaction(TransactionRequest request) {

        final Set<TransactionDetailEntity> transactionDetailEntities = new HashSet<>();
        final TransactionEntity transactionEntity = transactionRepository.save(TransactionEntity.builder()
                .transactionNumber(request.getTransactionNumber())
                .status(TransactionStatus.PENDING)
                .createdDate(LocalDateTime.now())
                .build());

        List<ErrorDetail> outOfStockProductError = new ArrayList<>();
        for (TransactionRequest.ProductPurchased productPurchased : request.getProductsPurchased()) {
            ProductEntity productEntity = productRepository.findByBarcode(productPurchased.getBarcode())
                    .orElseThrow(() -> new BadRequestException(ErrorDetailHelper.barcodeNotFound()));
            if (productEntity.getQuantity() < productPurchased.getQuantity()) {
                outOfStockProductError.add(ErrorDetailHelper
                        .invalidTransactionProductQuantity(productEntity.getProductName()));
            }

            if (!outOfStockProductError.isEmpty()) {
                continue;
            }
            transactionDetailEntities.add(TransactionDetailEntity.builder()
                    .transaction(transactionEntity)
                    .productId(productEntity.getId())
                    .quantity(productPurchased.getQuantity())
                    .price(productEntity.getSellingPrice().multiply(new BigDecimal(productPurchased.getQuantity())))
                    .discount(productPurchased.getDiscount())
                    .profit(productEntity.getProfit().multiply(new BigDecimal(productPurchased.getQuantity()))
                            .subtract(productPurchased.getDiscount()))
                    .createdDate(LocalDateTime.now())
                    .build());
        }

        if (!outOfStockProductError.isEmpty()) {
            throw new BadRequestException(outOfStockProductError);
        }
        transactionDetailRepository.saveAll(transactionDetailEntities);

        log.info("Transaction with transaction number: {} created successfully", request.getTransactionNumber());
        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }
}
