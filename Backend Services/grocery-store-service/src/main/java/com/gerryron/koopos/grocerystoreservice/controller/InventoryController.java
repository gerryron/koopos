package com.gerryron.koopos.grocerystoreservice.controller;

import com.gerryron.koopos.grocerystoreservice.dto.Item;
import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import com.gerryron.koopos.grocerystoreservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<RestResponse<Item>> saveItem(@RequestBody @Valid Item item) {
        return ResponseEntity.ok(inventoryService.createItem(item));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<List<Item>>> getPaginatedInventories(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(inventoryService.findPaginatedInventories(page - 1, size));
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<RestResponse<Item>> getItemByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(inventoryService.findItemByBarcode(barcode));
    }
}
