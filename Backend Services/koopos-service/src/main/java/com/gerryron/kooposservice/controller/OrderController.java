package com.gerryron.kooposservice.controller;

import com.gerryron.kooposservice.dto.RestResponse;
import com.gerryron.kooposservice.dto.request.CancelOrderRequest;
import com.gerryron.kooposservice.dto.request.OrderRequest;
import com.gerryron.kooposservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<RestResponse<Object>> postOrder(@RequestBody @Valid OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    @PostMapping("/canceled")
    public ResponseEntity<RestResponse<Object>> cancelOrder(@RequestBody @Valid CancelOrderRequest request) {
        return ResponseEntity.ok(orderService.cancelOrder(request));
    }

}
