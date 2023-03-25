package com.gerryron.koopos.grocerystoreservice.service;

import com.gerryron.koopos.grocerystoreservice.dto.Item;
import com.gerryron.koopos.grocerystoreservice.dto.PaginatedResponse;
import com.gerryron.koopos.grocerystoreservice.dto.ResponseStatus;
import com.gerryron.koopos.grocerystoreservice.dto.RestResponse;
import com.gerryron.koopos.grocerystoreservice.entity.CategoryEntity;
import com.gerryron.koopos.grocerystoreservice.entity.InventoryEntity;
import com.gerryron.koopos.grocerystoreservice.exception.KooposException;
import com.gerryron.koopos.grocerystoreservice.repository.CategoryRepository;
import com.gerryron.koopos.grocerystoreservice.repository.InventoryRepository;
import com.gerryron.koopos.grocerystoreservice.shared.ApplicationCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class InventoryService {

    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public InventoryService(CategoryRepository categoryRepository, InventoryRepository inventoryRepository) {
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public RestResponse<Item> createItem(Item item) {
        inventoryRepository.findByBarcode(item.getBarcode()).ifPresent(s -> {
            throw new KooposException(ApplicationCode.ITEM_ALREADY_EXISTS);
        });

        InventoryEntity inventoryEntity = new InventoryEntity(item);
        if (null != item.getCategories()) {
            Set<CategoryEntity> categoryEntities = new HashSet<>();
            for (String category : item.getCategories()) {
                CategoryEntity categoryEntity = new CategoryEntity(category);
                categoryEntities.add(getCategoryEntityIfExists(categoryEntity));
            }
            inventoryEntity.setCategories(categoryEntities);
        }

        InventoryEntity createdInventory = inventoryRepository.save(inventoryEntity);
        log.info("Success create: {}", item);
        return RestResponse.<Item>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(new Item(createdInventory, true))
                .build();
    }

    public PaginatedResponse<List<Item>> findPaginatedInventories(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.ASC);
        Page<InventoryEntity> inventoryEntities = inventoryRepository.findAll(
                PageRequest.of(page, size, Sort.by(direction, sortBy)));

        List<Item> inventories = inventoryEntities.getContent()
                .stream()
                .map(item -> new Item(item, true))
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
            item = new Item(inventoryRepository.findByBarcode(barcode).orElseThrow(() ->
                    new KooposException(ApplicationCode.BARCODE_NOT_FOUND)), true);
        } else {
            item = new Item(inventoryRepository.findByItemName(itemName).orElseThrow(() ->
                    new KooposException(ApplicationCode.ITEM_NAME_NOT_FOUND)), true);
        }

        return RestResponse.<Item>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(item)
                .build();
    }

    public RestResponse<Item> updateItem(String barcode, Item item) {
        InventoryEntity existingInventory = inventoryRepository.findByBarcode(barcode).orElseThrow(() ->
                new KooposException(ApplicationCode.BARCODE_NOT_FOUND));
        existingInventory.setItemName(item.getItemName());
        existingInventory.setDescription(item.getDescription());
        existingInventory.setQuantity(item.getQuantity());
        existingInventory.setBuyingPrice(item.getBuyingPrice());
        existingInventory.setSellingPrice(item.getSellingPrice());
        existingInventory.setUpdatedDate(LocalDateTime.now());
        existingInventory.getCategories().clear();
        if (null != item.getCategories()) {
            Set<CategoryEntity> categoryEntities = new HashSet<>();
            for (String category : item.getCategories()) {
                CategoryEntity categoryEntity = new CategoryEntity(category);
                categoryEntities.add(getCategoryEntityIfExists(categoryEntity));
            }
            existingInventory.setCategories(categoryEntities);
        }

        final InventoryEntity updatedInventory = inventoryRepository.save(existingInventory);
        return RestResponse.<Item>builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .data(new Item(updatedInventory, true))
                .build();
    }

    public RestResponse<Object> deleteItem(String barcode) {
        InventoryEntity existingInventory = inventoryRepository.findByBarcode(barcode).orElseThrow(() ->
                new KooposException(ApplicationCode.BARCODE_NOT_FOUND));
        existingInventory.getCategories().clear();

        inventoryRepository.delete(existingInventory);
        return RestResponse.builder()
                .responseStatus(new ResponseStatus(ApplicationCode.SUCCESS))
                .build();
    }

    public CategoryEntity getCategoryEntityIfExists(CategoryEntity categoryEntity) {
        return categoryRepository.findFirstByName(categoryEntity.getName())
                .orElse(categoryEntity);
    }
}
