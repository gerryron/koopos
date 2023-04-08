package com.gerryron.koopos.grocerystoreservice.controller;

import com.gerryron.koopos.grocerystoreservice.service.TransactionService;
import com.gerryron.koopos.grocerystoreservice.shared.request.TransactionRequest;
import com.gerryron.koopos.grocerystoreservice.shared.response.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.RestResponse;
import com.gerryron.koopos.grocerystoreservice.shared.response.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<RestResponse<Object>> createTransaction(
            @RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionRequest));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<List<TransactionResponse>>> getPaginatedTransactions(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.ASC);
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(transactionService.findPaginatedTransaction(pageRequest));
    }

    @GetMapping("/{transactionNumber}")
    public ResponseEntity<RestResponse<TransactionResponse>> getTransaction(
            @PathVariable(value = "transactionNumber") String transactionNumber
    ) {
        return ResponseEntity.ok(transactionService.findTransactionByTransactionNumber(transactionNumber));
    }
}
