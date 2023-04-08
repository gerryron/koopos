package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.request.TransactionRequest;
import com.gerryron.koopos.grocerystoreservice.repository.InventoryRepository;
import com.gerryron.koopos.grocerystoreservice.repository.TransactionDetailsRepository;
import com.gerryron.koopos.grocerystoreservice.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionDetailsRepository transactionDetailsRepository;
    
    @InjectMocks
    private TransactionService transactionService;
    
    @Test
    void testCreateTransaction_Success() {
        TransactionRequest transactionRequest = new TransactionRequest()
    }
}
