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
import com.gerryron.koopos.grocerystoreservice.shared.response.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                    new KooposException(ApplicationCode.PRODUCT_NOT_FOUND));
            if (product.getQuantity() < productPurchased.getAmount()) {
                throw new KooposException(ApplicationCode.PRODUCT_NOT_ENOUGH);
            }

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

            product.setQuantity(product.getQuantity() - productPurchased.getAmount());
            productRepository.save(product);
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

    public PaginatedResponse<List<TransactionResponse>> findPaginatedTransaction(PageRequest pageRequest) {
        Page<TransactionEntity> transactionEntities = transactionRepository.findAll(pageRequest);
        List<TransactionResponse> transactionResponses = transactionEntities.getContent()
                .stream()
                .map(transactionEntity -> {
                    List<TransactionDetailsEntity> transactionDetailEntities = transactionDetailsRepository
                            .findAllByTransactionNumber(transactionEntity.getTransactionNumber());

                    List<TransactionResponse.TransactionDetail> transactionDetailResponse = transactionDetailEntities
                            .stream()
                            .map(transactionDetailsEntity -> {
                                ProductEntity productEntity = productRepository
                                        .findById(transactionDetailsEntity.getProductId())
                                        .orElseThrow(() -> new KooposException(ApplicationCode.PRODUCT_NOT_FOUND));

                                return TransactionResponse.TransactionDetail.builder()
                                        .id(transactionDetailsEntity.getId())
                                        .productName(productEntity.getProductName())
                                        .amount(transactionDetailsEntity.getAmount())
                                        .price(transactionDetailsEntity.getPrice())
                                        .createdDate(transactionDetailsEntity.getCreatedDate())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return TransactionResponse.builder()
                            .transactionNumber(transactionEntity.getTransactionNumber())
                            .amount(transactionEntity.getAmount())
                            .totalPrice(transactionEntity.getTotalPrice())
                            .profit(transactionEntity.getProfit())
                            .transactionDetails(transactionDetailResponse)
                            .createdDate(transactionEntity.getCreatedDate())
                            .build();
                })
                .collect(Collectors.toList());

        return PaginatedResponse.<List<TransactionResponse>>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(transactionResponses)
                .detailPages(PaginatedResponse.PagingMetadata.builder()
                        .page(transactionEntities.getNumber() + 1)
                        .rowPerPage(pageRequest.getPageSize())
                        .totalData(transactionEntities.getTotalElements())
                        .build())
                .build();
    }

    public RestResponse<TransactionResponse> findTransactionByTransactionNumber(String transactionNumber) {
        TransactionEntity transactionEntity = transactionRepository.findByTransactionNumber(transactionNumber)
                .orElseThrow(() -> new KooposException(ApplicationCode.TRANSACTION_NOT_FOUND));
        List<TransactionResponse.TransactionDetail> transactionDetailResponse = transactionDetailsRepository
                .findAllByTransactionNumber(transactionNumber)
                .stream()
                .map(transactionDetailsEntity -> {
                    ProductEntity productEntity = productRepository
                            .findById(transactionDetailsEntity.getProductId())
                            .orElseThrow(() -> new KooposException(ApplicationCode.PRODUCT_NOT_FOUND));

                    return TransactionResponse.TransactionDetail.builder()
                            .id(transactionDetailsEntity.getId())
                            .productName(productEntity.getProductName())
                            .amount(transactionDetailsEntity.getAmount())
                            .price(transactionDetailsEntity.getPrice())
                            .createdDate(transactionDetailsEntity.getCreatedDate())
                            .build();
                })
                .collect(Collectors.toList());

        return RestResponse.<TransactionResponse>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(TransactionResponse.builder()
                        .transactionNumber(transactionEntity.getTransactionNumber())
                        .amount(transactionEntity.getAmount())
                        .totalPrice(transactionEntity.getTotalPrice())
                        .profit(transactionEntity.getProfit())
                        .transactionDetails(transactionDetailResponse)
                        .createdDate(transactionEntity.getCreatedDate())
                        .build())
                .build();
    }
}
