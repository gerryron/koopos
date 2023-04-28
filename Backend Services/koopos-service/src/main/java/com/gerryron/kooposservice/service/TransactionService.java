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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;

    public TransactionService(ProductRepository productRepository, TransactionRepository transactionRepository,
                              TransactionDetailRepository transactionDetailRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
    }

    @Transactional
    public RestResponse<Object> createTransaction(TransactionRequest request) {

        final Set<TransactionDetailEntity> transactionDetailEntities = new HashSet<>();
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTransactionNumber(request.getTransactionNumber());
        transactionEntity.setStatus(TransactionStatus.PENDING);
        transactionEntity.setCreatedDate(LocalDateTime.now());
        final TransactionEntity savedTransaction = transactionRepository.save(transactionEntity);

        List<ErrorDetail> outOfStockProductError = new ArrayList<>();
        for (TransactionRequest.ProductPurchased productPurchased : request.getProductsPurchased()) {
            ProductEntity productEntity = productRepository.findByBarcode(productPurchased.getBarcode())
                    .orElseThrow(() -> new BadRequestException(ErrorDetailHelper.barcodeNotFound()));
            if (productEntity.getQuantity() < productPurchased.getQuantity()) {
                outOfStockProductError.add(ErrorDetailHelper
                        .invalidTransactionProductQuantity(productEntity.getProductName()));
            }
            if (!outOfStockProductError.isEmpty()) continue;

            TransactionDetailEntity transactionDetailEntity = new TransactionDetailEntity();
            transactionDetailEntity.setTransaction(savedTransaction);
            transactionDetailEntity.setProductId(productEntity.getId());
            transactionDetailEntity.setQuantity(productPurchased.getQuantity());
            transactionDetailEntity.setPrice(productEntity.getSellingPrice()
                    .multiply(new BigDecimal(productPurchased.getQuantity())));
            transactionDetailEntity.setDiscount(productPurchased.getDiscount());
            transactionDetailEntity.setProfit(productEntity.getProfit().multiply(new BigDecimal(productPurchased.getQuantity()))
                    .subtract(productPurchased.getDiscount()));
            transactionDetailEntity.setCreatedDate(LocalDateTime.now());
            transactionDetailEntities.add(transactionDetailEntity);
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
