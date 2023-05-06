package com.gerryron.kooposservice.service;

import com.gerryron.kooposservice.config.auth.AuthUserDetails;
import com.gerryron.kooposservice.dto.ErrorDetail;
import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.CancelOrderRequest;
import com.gerryron.kooposservice.dto.request.OrderRequest;
import com.gerryron.kooposservice.entity.OrderDetailsEntity;
import com.gerryron.kooposservice.entity.OrderEntity;
import com.gerryron.kooposservice.entity.ProductEntity;
import com.gerryron.kooposservice.entity.UserEntity;
import com.gerryron.kooposservice.enums.ApplicationCode;
import com.gerryron.kooposservice.enums.OrderStatus;
import com.gerryron.kooposservice.exception.BadRequestException;
import com.gerryron.kooposservice.exception.NotFoundException;
import com.gerryron.kooposservice.helper.ErrorDetailHelper;
import com.gerryron.kooposservice.repository.OrderDetailRepository;
import com.gerryron.kooposservice.repository.OrderRepository;
import com.gerryron.kooposservice.repository.ProductRepository;
import com.gerryron.kooposservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public RestResponse<Object> createOrder(OrderRequest request) {
        final Set<OrderDetailsEntity> orderDetailsEntities = new HashSet<>();
        OrderEntity orderEntity = new OrderEntity();
        System.out.println(getUserContext().getUsername());
        orderEntity.setUser(getUserContext());
        orderEntity.setOrderNumber(request.getOrderNumber());
        orderEntity.setStatus(OrderStatus.PENDING);
        orderEntity.setCreatedDate(LocalDateTime.now());
        final OrderEntity savedTransaction = orderRepository.save(orderEntity);

        List<ErrorDetail> outOfStockProductError = new ArrayList<>();
        for (OrderRequest.ProductPurchased productPurchased : request.getProductsPurchased()) {
            ProductEntity productEntity = productRepository.findByBarcode(productPurchased.getBarcode())
                    .orElseThrow(() -> new BadRequestException(
                            ErrorDetailHelper.notFoundSingletonList("barcode")));
            if (productEntity.getQuantity() < productPurchased.getQuantity()) {
                outOfStockProductError.add(ErrorDetailHelper
                        .productOutOfStock(productEntity.getProductName()));
            }
            if (!outOfStockProductError.isEmpty()) continue;

            OrderDetailsEntity orderDetailsEntity = new OrderDetailsEntity();
            orderDetailsEntity.setOrder(savedTransaction);
            orderDetailsEntity.setProductId(productEntity.getId());
            orderDetailsEntity.setQuantity(productPurchased.getQuantity());
            orderDetailsEntity.setPrice(productEntity.getSellingPrice()
                    .multiply(new BigDecimal(productPurchased.getQuantity())));
            orderDetailsEntity.setDiscount(productPurchased.getDiscount());
            orderDetailsEntity.setProfit(productEntity.getProfit().multiply(new BigDecimal(productPurchased.getQuantity()))
                    .subtract(productPurchased.getDiscount()));
            orderDetailsEntity.setCreatedDate(LocalDateTime.now());
            orderDetailsEntities.add(orderDetailsEntity);
        }

        if (!outOfStockProductError.isEmpty()) {
            throw new BadRequestException(outOfStockProductError);
        }
        orderDetailRepository.saveAll(orderDetailsEntities);

        log.info("Transaction with transaction number: {} created successfully", request.getOrderNumber());
        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }

    public RestResponse<Object> cancelOrder(CancelOrderRequest request) {
        OrderEntity orderEntity = orderRepository.findByOrderNumber(request.getOrderNumber())
                .orElseThrow(() -> new NotFoundException(
                        ErrorDetailHelper.notFoundSingletonList("orderNumber")));
        orderEntity.setStatus(OrderStatus.CANCELED);
        orderEntity.setDescription(request.getDescription());
        orderEntity.setUpdatedDate(LocalDateTime.now());
        orderRepository.save(orderEntity);

        return RestResponse.builder()
                .responseStatus(ApplicationCode.SUCCESS)
                .build();
    }

    private UserEntity getUserContext() {
        final AuthUserDetails authUserDetails = (AuthUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByUsername(authUserDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(ErrorDetailHelper.notFoundSingletonList("username")));
    }
}
