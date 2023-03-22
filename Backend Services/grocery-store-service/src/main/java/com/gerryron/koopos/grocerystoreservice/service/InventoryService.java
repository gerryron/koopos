package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.Item;
import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.InventoryRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public RestResponse<Item> createItem(Item item) {
        inventoryRepository.findByBarcode(item.getBarcode()).ifPresent(s -> {
            throw new KooposException(ApplicationCode.ITEM_ALREADY_EXISTS);
        });

        inventoryRepository.save(new InventoryEntity(item));
        log.info("Success create: {}", item);
        return RestResponse.<Item>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(item)
                .build();
    }

    public PaginatedResponse<List<Item>> findPaginatedInventories(int page, int size) {
        Page<InventoryEntity> inventoryEntities = inventoryRepository.findAll(PageRequest.of(page, size));

        List<Item> inventories = inventoryEntities.getContent()
                .stream()
                .map(inventoryEntity -> new Item(inventoryEntity.getBarcode(), inventoryEntity.getItemName(),
                        inventoryEntity.getDescription(), inventoryEntity.getQuantity(),
                        inventoryEntity.getBuyingPrice(), inventoryEntity.getSellingPrice()))
                .collect(Collectors.toList());

        return PaginatedResponse.<List<Item>>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(inventories)
                .detailPages(PaginatedResponse.PagingMetadata.builder()
                        .page(inventoryEntities.getNumber() + 1)
                        .rowPerPage(size)
                        .totalData(inventoryEntities.getTotalElements())
                        .build())
                .build();
    }

    public RestResponse<Item> findItem(String barcode, String itemName) {
        Item item;
        if (barcode.isBlank() && itemName.isBlank()) {
            log.warn("Invalid Parameter: barcode & itemName is blank");
            throw new KooposException(ApplicationCode.INVALID_PARAMETER);
        } else if (!barcode.isBlank()) {
            item = inventoryRepository.findByBarcode(barcode).orElseThrow(() ->
                    new KooposException(ApplicationCode.BARCODE_NOT_FOUND));
        } else {
            item = inventoryRepository.findByItemName(itemName).orElseThrow(() ->
                    new KooposException(ApplicationCode.ITEM_NAME_NOT_FOUND));
        }

        return RestResponse.<Item>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(item)
                .build();
    }
}
